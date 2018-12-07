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

package net.daporkchop.lib.db.container.map.index.tree;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.db.container.map.DBMap;
import net.daporkchop.lib.db.container.map.index.IndexLookup;
import net.daporkchop.lib.db.container.map.key.KeyHasher;
import net.daporkchop.lib.logging.Logging;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * An implementation of {@link IndexLookup} that is based on a tree map (at least I call
 * it a tree map, I've never taken any computer science class before so I couldn't tell you
 * if that's the correct name for this data structure)
 * <p>
 * Tree maps don't have hash collisions and have a constant lookup time, however that lookup
 * time will generally be higher than that of a {@link net.daporkchop.lib.db.container.map.index.hashtable.BucketingHashTableIndexLookup}
 * unless you're using a significant number of entries.
 *
 * @author DaPorkchop_
 */
public class FasterTreeIndexLookup<K> implements IndexLookup<K>, Logging {
    public static final int VALUES_PER_SECTOR = 256;

    @Getter
    protected final int pointerBytes;
    protected final byte[] emptySector;
    protected final ReadWriteLock lock = new ReentrantReadWriteLock();
    protected ThreadLocal<ByteBuffer> buffer;
    protected KeyHasher<K> keyHasher;
    @Getter
    protected File file;
    protected RandomAccessFile tableRaf;
    protected FileChannel tableChannel;
    protected MappedByteBuffer rootNode;
    protected final AtomicLong offset = new AtomicLong(0L);
    @Getter
    protected DBMap<K, ?> map;
    @Getter
    @Setter
    protected volatile boolean dirty = false;

    public FasterTreeIndexLookup(int pointerBytes) {
        if (pointerBytes <= 0 || pointerBytes > 8) {
            throw this.exception("Pointer byte count must be in range 1-8 (given: ${0})", pointerBytes);
        }
        this.pointerBytes = pointerBytes;
        this.emptySector = new byte[this.pointerBytes * VALUES_PER_SECTOR];
    }

    @Override
    public void init(@NonNull DBMap<K, ?> map, @NonNull File file) throws IOException {
        this.lock.writeLock().lock();
        try {
            if (this.tableRaf != null) {
                throw new IllegalStateException("already initialized!");
            }
            this.keyHasher = map.getKeyHasher();
            if (this.keyHasher.getHashLength() < 2) {
                throw this.exception("Hash size must be at least 2 bytes!");
            }
            this.buffer = ThreadLocal.withInitial(() -> ByteBuffer.allocateDirect(this.keyHasher.getHashLength() * VALUES_PER_SECTOR));
            this.file = file;
            this.tableRaf = new RandomAccessFile(map.getFile("index/tree"), "rw");
            this.tableChannel = this.tableRaf.getChannel();
            this.map = map;
            IndexLookup.super.init(map, file);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public void load() throws IOException {
        try (DataIn in = this.map.getIn("index/offset", out -> out.writeLong(0L)))  {
            this.offset.set(in.readLong());
        }
        this.rootNode = this.tableChannel.map(FileChannel.MapMode.READ_WRITE, 0, VALUES_PER_SECTOR * this.pointerBytes);
    }

    @Override
    public void save() throws IOException {
        if(this.dirty) {
            try (DataOut out = this.map.getOut("index/offset")) {
                out.writeLong(this.offset.get());
            }
            this.rootNode.force();
            this.dirty = false;
        }
    }

    @Override
    public void close() throws IOException {
        if (this.buffer == null)   {
            throw this.exception("already closed!");
        }
        this.save();

        PorkUtil.release(this.rootNode);
        this.tableRaf.close();
        this.tableChannel.close();

        this.file = null;
        this.rootNode = null;
        this.tableRaf = null;
        this.tableChannel = null;
        this.keyHasher = null;
        this.map = null;
        this.buffer = null;
    }

    @Override
    public void clear() throws IOException {
        this.lock.writeLock().lock();
        try {
            this.tableChannel.truncate(0L);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public long get(@NonNull K key) throws IOException {
        byte[] hash = this.keyHasher.hash(key);
        ByteBuffer buffer = this.buffer.get();
        this.lock.readLock().lock();
        try {
            long next = this.readFromBuffer(this.rootNode, hash[0] & 0xFF);
            for (int i = 1; i < hash.length; i++)   {
                buffer.clear();
                this.tableChannel.read(buffer, next * VALUES_PER_SECTOR * this.pointerBytes);
                next = this.readFromBuffer(buffer, hash[i] & 0xFF);
                if (next == 0L) {
                    return -1L;
                }
            }
            if (next != 0L) {
                next = next;
            }
            return next - 1L;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public void set(@NonNull K key, long val) throws IOException {
        byte[] hash = this.keyHasher.hash(key);
        ByteBuffer buffer = this.buffer.get();
        //this.lock.readLock().lock();
        try {
            long next = this.readFromBuffer(this.rootNode, hash[0] & 0xFF);
            for (int i = 1; i < hash.length - 1; i++)   {
                buffer.clear();
                this.tableChannel.read(buffer, next * VALUES_PER_SECTOR * this.pointerBytes);
                next = this.readFromBuffer(buffer, hash[i] & 0xFF);
                if (next == 0L) {
                    if (val == 0L)  {
                        return; //don't bother setting empty values
                    }
                    //allocate new sector
                    long newPos = this.offset.getAndIncrement();
                    //this.lock.writeLock().lock();
                    try {
                        if (i == 1) {
                            this.writeToBuffer(this.rootNode, newPos, hash[i] & 0xFF);
                        } else {
                            this.writeToBuffer(buffer, newPos, hash[i] & 0xFF);
                            buffer.flip();
                            this.tableChannel.write(buffer, next * VALUES_PER_SECTOR * this.pointerBytes);
                        }
                        this.tableChannel.write(ByteBuffer.wrap(this.emptySector), newPos * VALUES_PER_SECTOR * this.pointerBytes);
                        next = newPos;
                    } finally {
                        //this.lock.writeLock().unlock();
                    }
                }
            }
            buffer.clear();
            //this.lock.writeLock().lock();
            try {
                this.tableChannel.read(buffer, next * VALUES_PER_SECTOR * this.pointerBytes);
                this.writeToBuffer(buffer, val + 1L, hash[hash.length - 1] & 0xFF);
                buffer.flip();
                this.tableChannel.write(buffer, next * VALUES_PER_SECTOR * this.pointerBytes);
                this.markDirty();
            } finally {
                //this.lock.writeLock().unlock();
            }
        } finally {
            //this.lock.readLock().unlock();
        }
    }

    @Override
    public boolean contains(@NonNull K key) throws IOException {
        byte[] hash = this.keyHasher.hash(key);
        ByteBuffer buffer = this.buffer.get();
        this.lock.readLock().lock();
        try {
            long next = this.readFromBuffer(this.rootNode, hash[0] & 0xFF);
            for (int i = 1; next != 0L && i < hash.length; i++)   {
                buffer.clear();
                this.tableChannel.read(buffer, next * VALUES_PER_SECTOR * this.pointerBytes);
                next = this.readFromBuffer(buffer, hash[i] & 0xFF);
            }
            return next != 0L;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public long remove(@NonNull K key) throws IOException {
        byte[] hash = this.keyHasher.hash(key);
        ByteBuffer buffer = this.buffer.get();
        this.lock.readLock().lock();
        try {
            long next = this.readFromBuffer(this.rootNode, hash[0] & 0xFF);
            for (int i = 1; next != 0L && i < hash.length - 1; i++)   {
                buffer.clear();
                this.tableChannel.read(buffer, next * VALUES_PER_SECTOR * this.pointerBytes);
                next = this.readFromBuffer(buffer, hash[i] & 0xFF);
            }
            if (next == 0L) {
                return -1L;
            } else {
                buffer.clear();
                this.lock.writeLock().lock();
                try {
                    this.tableChannel.read(buffer, next * VALUES_PER_SECTOR * this.pointerBytes);
                    long old = this.readFromBuffer(buffer, hash[hash.length - 1] & 0xFF);
                    this.writeToBuffer(buffer, 0L, hash[hash.length - 1] & 0xFF);
                    buffer.flip();
                    this.tableChannel.write(buffer, next * VALUES_PER_SECTOR * this.pointerBytes);
                    return old;
                } finally {
                    this.lock.writeLock().unlock();
                    this.markDirty();
                }
            }
        } finally {
            this.lock.readLock().unlock();
        }
    }

    protected long readFromBuffer(@NonNull ByteBuffer buffer, int pos)   {
        switch (this.pointerBytes)  {
            case 1:
                return buffer.get(pos) & 0xFFL;
            case 2:
                return buffer.getShort(pos) & 0xFFFFL;
            case 3:
                return (buffer.get(pos) & 0xFFL) |
                        ((buffer.get(pos + 1) & 0xFFL) << 8L) |
                        ((buffer.get(pos + 2) & 0xFFL) << 16L);
            case 4:
                return buffer.getInt(pos) & 0xFFFFFFFFL;
            case 5:
                return (buffer.get(pos) & 0xFFL) |
                        ((buffer.get(pos + 1) & 0xFFL) << 8L) |
                        ((buffer.get(pos + 2) & 0xFFL) << 16L) |
                        ((buffer.get(pos + 3) & 0xFFL) << 24L) |
                        ((buffer.get(pos + 4) & 0xFFL) << 32L);
            case 6:
                return (buffer.get() & 0xFFL) |
                        ((buffer.get() & 0xFFL) << 8L) |
                        ((buffer.get() & 0xFFL) << 16L) |
                        ((buffer.get() & 0xFFL) << 24L) |
                        ((buffer.get() & 0xFFL) << 32L) |
                        ((buffer.get() & 0xFFL) << 40L);
            case 7:
                return (buffer.get(pos) & 0xFFL) |
                        ((buffer.get(pos + 1) & 0xFFL) << 8L) |
                        ((buffer.get(pos + 2) & 0xFFL) << 16L) |
                        ((buffer.get(pos + 3) & 0xFFL) << 24L) |
                        ((buffer.get(pos + 4) & 0xFFL) << 32L) |
                        ((buffer.get(pos + 5) & 0xFFL) << 40L) |
                        ((buffer.get(pos + 6) & 0xFFL) << 48L);
            case 8:
                return buffer.getLong(pos);
        }
        throw new IllegalStateException();
    }

    protected void writeToBuffer(@NonNull ByteBuffer buffer, long val, int pos)  {
        switch (this.pointerBytes)  {
            case 1: {
                buffer.put(pos, (byte) (val & 0xFFL));
            }
            break;
            case 2: {
                buffer.putShort(pos, (short) (val & 0xFFFFL));
            }
            break;
            case 3: {
                buffer.put(pos, (byte) (val & 0xFFL));
                buffer.put(pos + 1, (byte) ((val >>> 8L) & 0xFFL));
                buffer.put(pos + 2, (byte) ((val >>> 16L) & 0xFFL));
            }
            break;
            case 4: {
                buffer.putInt(pos, (int) (val & 0xFFFFFFFFL));
            }
            break;
            case 5: {
                buffer.put(pos, (byte) (val & 0xFFL));
                buffer.put(pos + 1, (byte) ((val >>> 8L) & 0xFFL));
                buffer.put(pos + 2, (byte) ((val >>> 16L) & 0xFFL));
                buffer.put(pos + 3, (byte) ((val >>> 24L) & 0xFFL));
                buffer.put(pos + 4, (byte) ((val >>> 32L) & 0xFFL));
            }
            break;
            case 6: {
                buffer.put(pos, (byte) (val & 0xFFL));
                buffer.put(pos + 1, (byte) ((val >>> 8L) & 0xFFL));
                buffer.put(pos + 2, (byte) ((val >>> 16L) & 0xFFL));
                buffer.put(pos + 3, (byte) ((val >>> 24L) & 0xFFL));
                buffer.put(pos + 4, (byte) ((val >>> 32L) & 0xFFL));
                buffer.put(pos + 5, (byte) ((val >>> 40L) & 0xFFL));
            }
            break;
            case 7: {
                buffer.put(pos, (byte) (val & 0xFFL));
                buffer.put(pos + 1, (byte) ((val >>> 8L) & 0xFFL));
                buffer.put(pos + 2, (byte) ((val >>> 16L) & 0xFFL));
                buffer.put(pos + 3, (byte) ((val >>> 24L) & 0xFFL));
                buffer.put(pos + 4, (byte) ((val >>> 32L) & 0xFFL));
                buffer.put(pos + 5, (byte) ((val >>> 40L) & 0xFFL));
                buffer.put(pos + 6, (byte) ((val >>> 48L) & 0xFFL));
            }
            break;
            case 8: {
                buffer.putLong(pos, val);
            }
            break;
            default:
                throw new IllegalStateException();
        }
    }
}
