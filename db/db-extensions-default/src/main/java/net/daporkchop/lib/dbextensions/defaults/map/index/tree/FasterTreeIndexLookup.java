/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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

package net.daporkchop.lib.dbextensions.defaults.map.index.tree;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.function.io.IOConsumer;
import net.daporkchop.lib.common.function.io.IOFunction;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.dbextensions.defaults.map.DBHashMap;
import net.daporkchop.lib.dbextensions.defaults.map.IndexLookup;
import net.daporkchop.lib.dbextensions.defaults.map.KeyHasher;
import net.daporkchop.lib.logging.Logging;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
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
    protected final int bytesPerNode;
    protected final int nodeSize;
    protected final byte[] emptySector;
    protected final ReadWriteLock lock = new ReentrantReadWriteLock();
    protected final ThreadLocal<ByteBuffer> buffer;
    protected final AtomicLong offset = new AtomicLong(1L);
    protected KeyHasher<K> keyHasher;
    @Getter
    protected File file;
    protected FileChannel channel;
    protected MappedByteBuffer rootNode;
    @Getter
    protected DBHashMap<K, ?> map;
    protected int totalDepth;
    @Getter
    @Setter
    protected volatile boolean dirty = false;

    public FasterTreeIndexLookup(int pointerBytes, int bytesPerNode) {
        if (pointerBytes <= 0 || pointerBytes > 8) {
            throw new IllegalArgumentException(String.format("Pointer byte count must be in range 1-8 (given: %d)", pointerBytes));
        } else if (bytesPerNode <= 0 || bytesPerNode > 4) {
            throw new IllegalArgumentException(String.format("Bytes per node must be in range 1-4 (given: %d)", bytesPerNode));
        }
        this.pointerBytes = pointerBytes;
        this.bytesPerNode = bytesPerNode;
        int j = VALUES_PER_SECTOR;
        for (int i = this.bytesPerNode - 2; i >= 0; i--) {
            j *= j;
        }
        this.nodeSize = j * this.pointerBytes;
        this.emptySector = new byte[this.nodeSize];
        this.buffer = ThreadLocal.withInitial(() -> ByteBuffer.allocateDirect(this.nodeSize));
    }

    @Override
    public void init(@NonNull DBHashMap<K, ?> map, @NonNull File file) throws IOException {
        this.lock.writeLock().lock();
        try {
            if (this.channel != null) {
                throw new IllegalStateException("already initialized!");
            }
            this.keyHasher = map.getKeyHasher();
            if (this.keyHasher.getHashLength() < this.bytesPerNode) {
                throw new IllegalArgumentException(String.format("Hash size must be at least %d bytes!", this.bytesPerNode));
            } else if (this.keyHasher.getHashLength() / this.bytesPerNode * this.bytesPerNode != this.keyHasher.getHashLength()) {
                throw new IllegalArgumentException(String.format("Hash size must be a multiple of %d (found: %d)", this.bytesPerNode, this.keyHasher.getHashLength()));
            }
            this.totalDepth = this.keyHasher.getHashLength() / this.bytesPerNode;
            this.file = file;
            this.channel = FileChannel.open(map.getFile("index/tree").toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            this.map = map;
            IndexLookup.super.init(map, file);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public void load() throws IOException {
        try (DataIn in = this.map.getIn("index/offset", out -> out.writeLong(1L))) {
            this.offset.set(in.readLong());
        }
        this.rootNode = this.channel.map(FileChannel.MapMode.READ_WRITE, 0L, this.nodeSize);
    }

    @Override
    public void save() throws IOException {
        if (this.dirty) {
            try (DataOut out = this.map.getOut("index/offset")) {
                out.writeLong(this.offset.get());
            }
            this.rootNode.force();
            this.dirty = false;
        }
    }

    @Override
    public void close() throws IOException {
        if (this.buffer == null) {
            throw new IllegalStateException("already closed!");
        }
        this.save();

        PorkUtil.release(this.rootNode);
        this.channel.close();

        this.file = null;
        this.rootNode = null;
        this.channel = null;
        this.keyHasher = null;
        this.map = null;
    }

    @Override
    public void clear() throws IOException {
        this.lock.writeLock().lock();
        try {
            PorkUtil.release(this.rootNode);
            this.rootNode = null;
            this.channel.truncate(0L);
            this.channel.write(ByteBuffer.wrap(this.emptySector), 0L);
            this.rootNode = this.channel.map(FileChannel.MapMode.READ_WRITE, 0L, this.nodeSize);
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
            long next = this.readFromBuffer(this.rootNode, this.getNextStep(hash, 0));
            for (int i = 1; next != 0L && i < this.totalDepth; i++) {
                buffer.clear();
                if (this.channel.read(buffer, next * this.nodeSize) != this.nodeSize) {
                    throw new IllegalStateException("Couldn't read enough data!");
                } else {
                    next = this.readFromBuffer(buffer, this.getNextStep(hash, i));
                }
            }
            return next - 1L;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public void set(@NonNull K key, long val) throws IOException {
        val += 1L;
        byte[] hash = this.keyHasher.hash(key);
        ByteBuffer buffer = this.buffer.get();
        this.lock.writeLock().lock();
        try {
            long prev = 0L;
            long next = this.readFromBuffer(this.rootNode, this.getNextStep(hash, 0));
            for (int i = 1; i < this.totalDepth; i++) {
                if (next == 0L) {
                    //allocate new sector
                    long newSector = this.offset.getAndIncrement();
                    if (i == 1) {
                        this.writeToBuffer(this.rootNode, newSector, this.getNextStep(hash, 0));
                    } else {
                        this.writeToBuffer(buffer, newSector, this.getNextStep(hash, i - 1));
                        buffer.rewind();
                        this.channel.write(buffer, prev * this.nodeSize);
                    }
                    this.channel.write(ByteBuffer.wrap(this.emptySector), newSector * this.nodeSize);
                    next = newSector;
                }
                buffer.clear();
                if (this.channel.read(buffer, next * this.nodeSize) != this.nodeSize) {
                    throw new IllegalStateException("Couldn't read enough data!");
                } else {
                    prev = next;
                    next = this.readFromBuffer(buffer, this.getNextStep(hash, i));
                }
            }
            if (this.totalDepth <= 1) {
                this.writeToBuffer(this.rootNode, val, this.getNextStep(hash, 0));
            } else {
                this.writeToBuffer(buffer, val, this.getNextStep(hash, this.totalDepth - 1));
                buffer.rewind();
                this.channel.write(buffer, prev * this.nodeSize);
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public boolean contains(@NonNull K key) throws IOException {
        return this.get(key) != -1L;
    }

    @Override
    public long remove(@NonNull K key) throws IOException {
        byte[] hash = this.keyHasher.hash(key);
        ByteBuffer buffer = this.buffer.get();
        this.lock.writeLock().lock();
        try {
            long prev = 0L;
            long next = this.readFromBuffer(this.rootNode, this.getNextStep(hash, 0));
            for (int i = 1; i < this.totalDepth; i++) {
                if (next == 0L) {
                    return -1L;
                } else {
                    buffer.clear();
                    if (this.channel.read(buffer, next * this.nodeSize) != this.nodeSize) {
                        throw new IllegalStateException("Couldn't read enough data!");
                    } else {
                        prev = next;
                        next = this.readFromBuffer(buffer, this.getNextStep(hash, i));
                    }
                }
            }
            if (next == 0L) {
                return -1L;
            } else {
                if (this.totalDepth <= 1) {
                    this.writeToBuffer(this.rootNode, 0L, this.getNextStep(hash, 0));
                } else {
                    this.writeToBuffer(buffer, 0, this.getNextStep(hash, this.totalDepth - 1));
                    buffer.rewind();
                    this.channel.write(buffer, prev * this.nodeSize);
                }
                return next - 1L;
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public boolean runIfContains(@NonNull K key, @NonNull IOConsumer<Long> func) throws IOException {
        long l = this.get(key);
        if (l == -1L) {
            return false;
        } else {
            func.acceptThrowing(l);
            return true;
        }
    }

    @Override
    public boolean changeIfContains(@NonNull K key, @NonNull IOFunction<Long, Long> func) throws IOException {
        byte[] hash = this.keyHasher.hash(key);
        ByteBuffer buffer = this.buffer.get();
        this.lock.writeLock().lock();
        try {
            long prev = 0L;
            long next = this.readFromBuffer(this.rootNode, this.getNextStep(hash, 0));
            for (int i = 1; i < this.totalDepth; i++) {
                if (next == 0L) {
                    //allocate new sector
                    long newSector = this.offset.getAndIncrement();
                    if (i == 1) {
                        this.writeToBuffer(this.rootNode, newSector, this.getNextStep(hash, 0));
                    } else {
                        this.writeToBuffer(buffer, newSector, this.getNextStep(hash, i - 1));
                        buffer.rewind();
                        this.channel.write(buffer, prev * this.nodeSize);
                    }
                    this.channel.write(ByteBuffer.wrap(this.emptySector), newSector * this.nodeSize);
                    next = newSector;
                }
                buffer.clear();
                if (this.channel.read(buffer, next * this.nodeSize) != this.nodeSize) {
                    throw new IllegalStateException("Couldn't read enough data!");
                } else {
                    prev = next;
                    next = this.readFromBuffer(buffer, this.getNextStep(hash, i));
                }
            }
            if (next == 0L) {
                return false;
            } else {
                next = func.apply(next - 1L) + 1L;
            }
            if (this.totalDepth <= 1) {
                this.writeToBuffer(this.rootNode, next, this.getNextStep(hash, 0));
            } else {
                this.writeToBuffer(buffer, next, this.getNextStep(hash, this.totalDepth - 1));
                buffer.rewind();
                this.channel.write(buffer, prev * this.nodeSize);
            }
            return true;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public void change(@NonNull K key, @NonNull IOFunction<Long, Long> func) throws IOException {
        byte[] hash = this.keyHasher.hash(key);
        ByteBuffer buffer = this.buffer.get();
        this.lock.writeLock().lock();
        try {
            long prev = 0L;
            long next = this.readFromBuffer(this.rootNode, this.getNextStep(hash, 0));
            for (int i = 1; i < this.totalDepth; i++) {
                if (next == 0L) {
                    //allocate new sector
                    long newSector = this.offset.getAndIncrement();
                    if (i == 1) {
                        this.writeToBuffer(this.rootNode, newSector, this.getNextStep(hash, 0));
                    } else {
                        this.writeToBuffer(buffer, newSector, this.getNextStep(hash, i - 1));
                        buffer.rewind();
                        this.channel.write(buffer, prev * this.nodeSize);
                    }
                    this.channel.write(ByteBuffer.wrap(this.emptySector), newSector * this.nodeSize);
                    next = newSector;
                }
                buffer.clear();
                if (this.channel.read(buffer, next * this.nodeSize) != this.nodeSize) {
                    throw new IllegalStateException("Couldn't read enough data!");
                } else {
                    prev = next;
                    next = this.readFromBuffer(buffer, this.getNextStep(hash, i));
                }
            }
            next = func.apply(next - 1L) + 1L;
            if (this.totalDepth <= 1) {
                this.writeToBuffer(this.rootNode, next, this.getNextStep(hash, 0));
            } else {
                this.writeToBuffer(buffer, next, this.getNextStep(hash, this.totalDepth - 1));
                buffer.rewind();
                this.channel.write(buffer, prev * this.nodeSize);
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    protected int getNextStep(@NonNull byte[] hash, int depth) {
        int j = 0;
        for (int i = this.bytesPerNode - 1; i >= 0; i--) {
            j |= (hash[depth * this.bytesPerNode + i] & 0xFF) << (i << 3);
        }
        return j;
    }

    protected long readFromBuffer(@NonNull ByteBuffer buffer, int pos) {
        pos *= this.pointerBytes;
        switch (this.pointerBytes) {
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

    protected void writeToBuffer(@NonNull ByteBuffer buffer, long val, int pos) {
        pos *= this.pointerBytes;
        switch (this.pointerBytes) {
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
