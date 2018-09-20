/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.db.io;

import lombok.NonNull;
import net.daporkchop.lib.db.PorkDB;
import net.daporkchop.lib.db.io.file.ConstantLengthFile;
import net.daporkchop.lib.db.io.file.OpenFile;
import net.daporkchop.lib.db.io.file.SectoredFile;
import net.daporkchop.lib.db.util.FastHex;
import net.daporkchop.lib.db.util.exception.WrappedException;
import net.daporkchop.lib.encoding.Hexadecimal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * @author DaPorkchop_
 */
public class TreeFileManager extends FileManager {
    /**
     * A map of subhashes to {@link OpenFile} instances.
     */
    private final transient Map<Long, OpenFile> openFiles = new Hashtable<>();
    /**
     * Used to prevent many redundant byte array allocations for subhashes. The
     * byte array containing the subhash is perpetually recycled from this {@link ThreadLocal}
     */
    private volatile transient ThreadLocal<byte[]> subHashCache;

    public TreeFileManager(int maxOpenFiles) {
        super(maxOpenFiles);
    }

    private static String getFileFromSubHash(@NonNull byte[] subHash) {
        return FastHex.toHex(subHash[0]) + '/' + Hexadecimal.encode(subHash, 1, subHash.length - 1) + PorkDB.ENTRY_ENDING;
    }

    /**
     * Get an instance of {@link OpenFile} from a full key hash
     *
     * @param fullHash            the full key hash
     * @param createIfDoesntExist whether or not a new file should be created if there
     *                            is no existing file with that key
     * @return an instance of {@link OpenFile}
     */
    private OpenFile getFileFull(@NonNull byte[] fullHash, boolean createIfDoesntExist, long hashHash) {
        byte[] subHash = this.getSubHash(fullHash);
        return this.getFile(subHash, createIfDoesntExist, hashHash);
    }

    /**
     * Get an instance of {@link OpenFile} from an key subhash
     *
     * @param subHash             the subhash
     * @param createIfDoesntExist whether or not a new file should be created if there
     *                            is no existing file with that key
     * @return an instance of {@link OpenFile}
     */
    private OpenFile getFile(@NonNull byte[] subHash, boolean createIfDoesntExist, long hashHash) {
        this.lock.lock();
        try {
            OpenFile file = this.openFiles.get(hashHash);
            if (file == null) {
                File f = new File(this.db.getRootFolder(), TreeFileManager.getFileFromSubHash(subHash));
                if (f.exists() || createIfDoesntExist) {
                    file = this.db.getValueSerializer().isConstantLength() ?
                            new ConstantLengthFile(f, this) :
                            new SectoredFile(f, this);
                    //clone subhash here to prevent modifying of keys in map
                    this.openFiles.put(hashHash, file);
                } else {
                    hashHash++;
                }
            }
            if (file != null) {
                file.loadedEntries.incrementAndGet();
            } else {
                hashHash++;
            }
            return file;
        } finally {
            this.lock.unlock();
        }
    }

    /**
     * Gets a subhash from a full hash
     *
     * @param fullHash the full key hash
     * @return a subhash
     */
    private synchronized byte[] getSubHash(@NonNull byte[] fullHash) {
        this.lock.lock();
        try {
            if (this.subHashCache == null) {
                int len = fullHash.length - 1;
                this.subHashCache = ThreadLocal.withInitial(() -> new byte[len]);
            }
        } finally {
            this.lock.unlock();
        }
        byte[] subHash = this.subHashCache.get();
        System.arraycopy(fullHash, 0, subHash, 0, subHash.length);
        return subHash;
    }

    /**
     * Closes all currently open files that can be closed.
     */
    private void fileGC() {
        this.fileGC(false);
    }

    /**
     * Closes all currently open files that can be closed.
     *
     * @param force if true, file garbage collection will happen no matter what. if
     *              false, file garbage collection will only happen if the number of open files
     *              is more than the maximum number.
     */
    private void fileGC(boolean force) {
        this.lock.lock();
        try {
            if (force || this.openFiles.size() > this.maxOpenFiles) {
                Set<Map.Entry<Long, OpenFile>> entries = new HashSet<>(this.openFiles.entrySet());
                entries.forEach(entry -> {
                    if (entry.getValue().canClose()) {
                        entry.getValue().close();
                        this.openFiles.remove(entry.getKey());
                    }
                });
            }
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void init() {
    }

    @Override
    public byte[] get(@NonNull byte[] fullHash, long hashHash) {
        OpenFile file = this.getFileFull(fullHash, false, hashHash);
        if (file == null) {
            return null;
        } else {
            byte[] b = file.get(fullHash[fullHash.length - 1] & 0xFF);
            file.loadedEntries.decrementAndGet();
            this.fileGC();
            return b;
        }
    }

    @Override
    public void put(@NonNull byte[] fullHash, @NonNull byte[] value, long hashHash) {
        OpenFile file = this.getFileFull(fullHash, true, hashHash);
        file.put(fullHash[fullHash.length - 1] & 0xFF, value);
        file.loadedEntries.decrementAndGet();
        this.fileGC();
    }

    @Override
    public void remove(@NonNull byte[] fullHash, long hashHash) {
        OpenFile file = this.getFileFull(fullHash, false, hashHash);
        if (file != null) {
            file.remove(fullHash[fullHash.length - 1] & 0xFF);
            file.loadedEntries.decrementAndGet();
            this.fileGC();
        }
    }

    @Override
    public boolean contains(@NonNull byte[] fullHash, long hashHash) {
        OpenFile file = this.getFileFull(fullHash, false, hashHash);
        if (file == null) {
            return false;
        } else {
            boolean val = file.contains(fullHash[fullHash.length - 1] & 0xFF);
            file.loadedEntries.decrementAndGet();
            this.fileGC();
            return val;
        }
    }

    @Override
    public void shutdown() {
        //TODO: close all loaded entries

        this.fileGC();
        while (!this.openFiles.isEmpty()) {
            try {
                Thread.sleep(25L);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new WrappedException("Unable to shutdown database", e);
            }
            this.fileGC(true);
        }
    }

    @Override
    public void forEach(BiConsumer<byte[], InputStream> consumer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getStream(byte[] fullHash, long hashHash) {
        byte[] data = this.get(fullHash, hashHash);
        return data == null ? null : new ByteArrayInputStream(data);
    }

    @Override
    public OutputStream putStream(byte[] fullHash, long hashHash) {
        return new ByteArrayOutputStream() {
            @Override
            public void close() {
                TreeFileManager.this.put(fullHash, this.toByteArray(), hashHash);
            }
        };
    }
}
