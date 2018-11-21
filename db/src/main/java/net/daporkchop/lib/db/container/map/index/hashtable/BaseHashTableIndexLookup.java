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

package net.daporkchop.lib.db.container.map.index.hashtable;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.binary.util.IsPow2;
import net.daporkchop.lib.common.function.IOFunction;
import net.daporkchop.lib.db.container.map.DBMap;
import net.daporkchop.lib.db.container.map.data.key.KeyHasher;
import net.daporkchop.lib.db.container.map.index.IndexLookup;
import net.daporkchop.lib.db.util.BufferWriterFunc;
import net.daporkchop.lib.primitive.function.biconsumer.ObjectIntegerBiConsumer;
import net.daporkchop.lib.primitive.function.bifunction.ObjectIntegerLongBiFunction;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A base implementation of {@link IndexLookup}.
 * <p>
 * This is also thread-safe (not concurrent!), utilizing a {@link ReadWriteLock}
 *
 * @author DaPorkchop_
 */
public abstract class BaseHashTableIndexLookup<K> implements IndexLookup<K> {
    protected static final ThreadLocal<ByteBuffer> bufferCache = ThreadLocal.withInitial(() -> ByteBuffer.allocateDirect(8));

    protected final int usedBits;
    protected final long tableSize;
    protected final int pointerBytes;
    protected final ObjectIntegerLongBiFunction<ByteBuffer> bufferReader;
    protected final BufferWriterFunc bufferWriter;
    protected final ReadWriteLock lock = new ReentrantReadWriteLock();
    protected ThreadLocal<byte[]> hashCache;
    protected KeyHasher<K> keyHasher;
    @Getter
    protected File file;
    protected RandomAccessFile tableRaf;
    protected FileChannel tableChannel;

    public BaseHashTableIndexLookup(int usedBits, int pointerBytes) {
        if (usedBits <= 0 || usedBits > 32) {
            throw new IllegalArgumentException(String.format("Used bits must be in range 1-31 (given: %d)", usedBits));
        } else if (!(pointerBytes == 1 || pointerBytes == 2 || pointerBytes == 4 || pointerBytes == 8)) {
            throw new IllegalArgumentException(String.format("Pointer byte count must be 1,2,4 or 8 (given: %d)", pointerBytes));
        }
        this.tableSize = (1L << usedBits) * (long) pointerBytes;
        this.usedBits = usedBits;
        this.pointerBytes = pointerBytes;

        switch (pointerBytes) {
            case 1:
                bufferReader = (buf, i) -> buf.get(i) & 0xFFL;
                bufferWriter = (buf, i, val) -> buf.put(i, (byte) (val & 0xFFL));
                break;
            case 2:
                bufferReader = (buf, i) -> buf.getShort(i << 1) & 0xFFFFL;
                bufferWriter = (buf, i, val) -> buf.putShort(i << 1, (short) (val & 0xFFFFL));
                break;
            case 4:
                bufferReader = (buf, i) -> buf.getInt(i << 2) & 0xFFFFFFFFL;
                bufferWriter = (buf, i, val) -> buf.putInt(i << 2, (int) (val & 0xFFFFFFFFL));
                break;
            case 8:
                bufferReader = (buf, i) -> buf.getLong(i << 3);
                bufferWriter = (buf, i, val) -> buf.putLong(i << 3, val);
                break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void init(@NonNull DBMap<K, ?> map, @NonNull File file) throws IOException {
        this.lock.writeLock().lock();
        try {
            if (this.hashCache != null) {
                throw new IllegalStateException("already initialized!");
            }
            {
                int len = map.getKeyHasher().getHashLength();
                this.hashCache = ThreadLocal.withInitial(() -> new byte[len]);
            }
            this.keyHasher = map.getKeyHasher();
            this.file = file;
            this.tableRaf = new RandomAccessFile(map.getFile("index/table"), "rw");
            long fullSize = (long) this.pointerBytes * (long) this.tableSize;
            if (this.tableRaf.length() < fullSize) {
                //grow file
                byte[] buf = new byte[1024];
                this.tableRaf.seek(0L);
                for (long l = fullSize / 1024L - 1L; l >= 0L; l--) {
                    this.tableRaf.write(buf);
                }
            }
            this.tableChannel = this.tableRaf.getChannel();
            this.doInit(map, file);
            IndexLookup.super.init(map, file);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    protected abstract void doInit(@NonNull DBMap<K, ?> map, @NonNull File file) throws IOException;

    @Override
    public void close() throws IOException {
        this.lock.writeLock().lock();
        try {
            if (this.hashCache == null) {
                throw new IllegalStateException("already closed!");
            }

            this.doSave();
            this.doClose();

            this.tableChannel.close();
            this.tableRaf.close();
            this.tableChannel = null;
            this.tableRaf = null;
            this.file = null;
            this.hashCache = null;
            this.keyHasher = null;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    protected abstract void doClose() throws IOException;

    @Override
    public void clear() throws IOException {
        this.lock.writeLock().lock();
        try {
            this.doClear();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    protected abstract void doClear() throws IOException;

    @Override
    public long get(@NonNull K key) throws IOException {
        return this.doGet(key) - 1L;
    }

    protected abstract long doGet(@NonNull K key) throws IOException;

    @Override
    public void set(@NonNull K key, long val) throws IOException {
        this.lock.writeLock().lock();
        try {
            //translate by 1 to avoid issues with 0 keys
            this.doSet(key, val + 1L);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    protected abstract void doSet(@NonNull K key, long val) throws IOException;

    @Override
    public boolean contains(@NonNull K key) throws IOException {
        this.lock.readLock().lock();
        try {
            return this.doContains(key);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    protected abstract boolean doContains(@NonNull K key) throws IOException;

    @Override
    public long remove(@NonNull K key) throws IOException {
        this.lock.writeLock().lock();
        try {
            return this.doRemove(key) - 1L;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    protected abstract long doRemove(@NonNull K key) throws IOException;

    @Override
    public boolean changeIfContains(@NonNull K key, @NonNull IOFunction<Long, Long> func) throws IOException {
        this.lock.readLock().lock();
        try {
            return this.doChangeIfContains(key, func);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    protected boolean doChangeIfContains(@NonNull K key, @NonNull IOFunction<Long, Long> func) throws IOException {
        return IndexLookup.super.changeIfContains(key, func);
    }

    @Override
    public void change(@NonNull K key, @NonNull IOFunction<Long, Long> func) throws IOException {
        this.lock.writeLock().lock();
        try {
            this.doChange(key, func);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    protected void doChange(@NonNull K key, @NonNull IOFunction<Long, Long> func) throws IOException {
        IndexLookup.super.change(key, func);
    }

    @Override
    public void load() throws IOException {
    }

    @Override
    public void save() throws IOException {
        this.lock.writeLock().lock();
        try {
            this.doSave();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    protected abstract void doSave() throws IOException;

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public void setDirty(boolean dirty) {
    }

    protected byte[] getHash(@NonNull K key)    {
        //byte[] hash = this.hashCache.get();
        //this.keyHasher.hash(key);
        return this.keyHasher.hash(key);
    }

    protected int getRelevantHashBits(@NonNull K key)   {
        int i = this.getRelevantHashBits(this.getHash(key));
        return i;
    }

    protected int getRelevantHashBits(@NonNull byte[] hash)   {
        int x = (hash[0] & 0xFF) |
                ((hash[1] & 0xFF) << 8) |
                ((hash[2] & 0xFF) << 16) |
                ((hash[3] & 0xFF) << 24);
        return x & ((1 << this.usedBits) - 1);
    }
}
