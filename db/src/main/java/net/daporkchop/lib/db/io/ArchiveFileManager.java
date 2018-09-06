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

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileInputStream;
import de.schlichtherle.truezip.file.TFileOutputStream;
import lombok.NonNull;
import net.daporkchop.lib.db.PorkDB;
import net.daporkchop.lib.db.util.CountingLock;
import net.daporkchop.lib.db.util.exception.WrappedException;
import net.daporkchop.lib.encoding.basen.Base34;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * @author DaPorkchop_
 */
public class ArchiveFileManager extends FileManager {
    private final Map<Long, CountingLock> usedFiles = new ConcurrentHashMap<>();
    private File file;
    @NonNull
    private final String suffix;

    public ArchiveFileManager(int maxOpenFiles, @NonNull String suffix) {
        super(maxOpenFiles);
        this.suffix =suffix;
    }

    private TFile getFile(@NonNull byte[] fullHash) {
        return new TFile(this.file, "/" + Base34.encodeBase34(fullHash));
    }

    @Override
    public void init() {
        this.file = new File(this.db.getRootFolder(), "data." + this.suffix);
        try {
            TFile file = new TFile(this.file, "data." + this.suffix + "/placeholder");
            if (!file.exists() && !file.mkdirs()) {
                throw new IOException("Unable to create placeholder directory");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new WrappedException("Unable to create data file!", e);
        }
    }

    @Override
    public InputStream getStream(byte[] fullHash, long hashHash) {
        CountingLock lock = this.usedFiles.computeIfAbsent(hashHash, k -> new CountingLock());
        TFile file = this.getFile(fullHash);
        lock.lock();
        try {
            if (file.exists()) {
                return new TFileInputStream(file);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new WrappedException(e);
        } finally {
            lock.unlock();
            this.gc();
        }
    }

    @Override
    public OutputStream putStream(byte[] fullHash, long hashHash) {
        CountingLock lock = this.usedFiles.computeIfAbsent(hashHash, k -> new CountingLock());
        TFile file = this.getFile(fullHash);
        lock.lock();
        try {
            if (!file.exists() && !file.createNewFile()) {
                throw new IOException("Unable to create new entry file: " + file.getAbsolutePath());
            }
            return new TFileOutputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
            throw new WrappedException(e);
        } finally {
            lock.unlock();
            this.gc();
        }
    }

    @Override
    public void remove(byte[] fullHash, long hashHash) {
        CountingLock lock = this.usedFiles.computeIfAbsent(hashHash, k -> new CountingLock());
        TFile file = this.getFile(fullHash);
        lock.lock();
        try {
            if (file.exists()) {
                file.rm();
                if (file.exists()) {
                    throw new IOException("Unable to delete file!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new WrappedException(e);
        } finally {
            lock.unlock();
            this.gc();
        }
    }

    @Override
    public boolean contains(byte[] fullHash, long hashHash) {
        CountingLock lock = this.usedFiles.computeIfAbsent(hashHash, k -> new CountingLock());
        TFile file = this.getFile(fullHash);
        lock.lock();
        try {
            return file.exists();
        } finally {
            lock.unlock();
            this.gc();
        }
    }

    @Override
    public void shutdown() {
        this.gc(true);
        while (!this.usedFiles.isEmpty()) {
            try {
                Thread.sleep(25L);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new WrappedException(e);
            }
            this.gc(true);
        }
    }

    @Override
    public void forEach(BiConsumer<byte[], InputStream> consumer) {
        TFile file = new TFile(this.file);
        for (String member : file.list())   {
            if (!member.endsWith(this.suffix))  {
                byte[] key = Base34.decodeBase34(member);
                InputStream in = this.getStream(key, PorkDB.hash(key));
                consumer.accept(key, in);
            }
        }
    }

    private void gc() {
        this.gc(false);
    }

    private void gc(boolean force) {
        if (force || this.usedFiles.size() > this.maxOpenFiles) {
            this.usedFiles.entrySet().removeIf(e -> e.getValue().getCurrentlyUsing() == 0);
        }
    }
}
