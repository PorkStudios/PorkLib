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

package net.daporkchop.lib.db.io.file;

import lombok.NonNull;
import net.daporkchop.lib.db.io.FileManager;
import net.daporkchop.lib.db.util.Closeable;
import net.daporkchop.lib.db.util.exception.WrappedException;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author DaPorkchop_
 */
public abstract class OpenFile implements Closeable {
    public final AtomicInteger loadedEntries = new AtomicInteger(0);
    protected final FileManager fileManager;
    protected final ReentrantLock lock = new ReentrantLock();
    protected volatile RandomAccessFile file;
    protected volatile boolean writeHeaders;

    public OpenFile(@NonNull File file, @NonNull FileManager fileManager) {
        this.fileManager = fileManager;
        try {
            boolean exists = file.exists();
            this.writeHeaders = !exists;
            if (!exists && file.createNewFile()) {
                throw new IllegalStateException("Unable to create new file!");
            }
            this.file = new RandomAccessFile(file, "rw");
            if (!exists) {
                this.init();
            }
            this.readHeaders();
        } catch (IOException e) {
            e.printStackTrace();
            throw new WrappedException("Initializing OpenFile", e);
        }
    }

    @Deprecated
    protected OpenFile(@NonNull FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public abstract byte[] get(int sector);

    public abstract void put(int sector, @NonNull byte[] data);

    public abstract void remove(int sector);

    public abstract boolean contains(int sector);

    @Override
    public void close() {
        this.lock.lock();
        try {
            this.writeHeaders();
            this.file.close();
            this.file = null;
        } catch (IOException e) {
            e.printStackTrace();
            throw new WrappedException("Closing OpenFile", e);
        } finally {
            this.lock.unlock();
        }
    }

    public boolean canClose() {
        //TODO: add prevention for new threads accessing this file while closing
        return !this.lock.isLocked() && this.loadedEntries.get() == 0;
    }

    protected abstract void init() throws IOException;

    protected abstract void writeHeaders() throws IOException;

    protected abstract void readHeaders() throws IOException;
}
