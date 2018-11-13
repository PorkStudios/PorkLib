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

package net.daporkchop.lib.db.container.map.index;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.db.container.bitset.PersistentSparseBitSet;
import net.daporkchop.lib.db.container.map.DBMap;
import net.daporkchop.lib.db.data.key.KeyHasher;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author DaPorkchop_
 */
@Getter
public class TreeIndexLookup<K> implements IndexLookup<K> {
    protected static final long NODE_SIZE_SHIFT = 11L;
    protected static final byte[] EMPTY_NODE = new byte[1 << NODE_SIZE_SHIFT];

    static {
        LongBuffer buffer = ByteBuffer.wrap(EMPTY_NODE).asLongBuffer();
        for (int i = 255; i >= 0; i--)  {
            buffer.put(-1L);
        }
    }

    protected final ReadWriteLock lock = new ReentrantReadWriteLock();
    protected DBMap<K, ?> map;
    protected KeyHasher<K> keyHasher;
    protected int hashLength;
    protected RandomAccessFile file;
    protected FileChannel channel;
    protected PersistentSparseBitSet nodeSectorMap;
    protected TreeNode rootNode;

    @Override
    public void init(@NonNull DBMap<K, ?> map, @NonNull RandomAccessFile file) throws IOException {
        if (this.map != null) {
            throw new IllegalStateException("already initialized");
        }
        this.map = map;
        this.keyHasher = this.map.getKeyHasher();
        this.hashLength = this.keyHasher.getHashLength();
        this.file = file;
        IndexLookup.super.init(this.map, file);
    }

    @Override
    public void load() throws IOException {
        this.channel = this.file.getChannel();
        this.channel.lock();
        if (this.file.length() == 0L || !this.nodeSectorMap.get(0))   {
            //allocate root sector
            if (this.nodeSectorMap.get(0))  {
                throw new IllegalStateException("Node sector 0 is already set!");
            }
            this.nodeSectorMap.set(0);
            this.file.setLength(1L << NODE_SIZE_SHIFT);
            this.channel.write(ByteBuffer.wrap(EMPTY_NODE), 0L);
            this.rootNode = new TreeNode(0, 0);
            this.nodeSectorMap.save();
        } else {
            this.rootNode = new TreeNode(0L, 0);
        }
    }

    @Override
    public void save() throws IOException {
        this.lock.writeLock().lock();
        try {
            if (this.map == null)   {
                throw new IllegalStateException("not initialized");
            }
            this.nodeSectorMap.save();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public void close() throws IOException {
        this.lock.writeLock().lock();
        try {
            //take advantage of reentrance
            this.save();
            this.channel.close();
            this.file.close();

            //reset values
            this.channel = null;
            this.file = null;
            this.hashLength = -1;
            this.nodeSectorMap = null;
            this.rootNode = null;
            this.keyHasher = null;
            this.map = null;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public long get(K key) {
        return 0;
    }

    @Override
    public void set(K key, long val) {
    }

    @Override
    public boolean contains(K key) {
        return false;
    }

    @Override
    public void remove(K key) {
    }

    @Override
    public boolean isDirty() {
        return this.nodeSectorMap.isDirty();
    }

    @Override
    public void setDirty(boolean dirty) {
        this.nodeSectorMap.setDirty(true);
    }

    protected long findAndAllocateNewSector() throws IOException {
        this.lock.writeLock().lock();
        try {
            //TODO: support 64-bit sectors
            int sector = this.nodeSectorMap.getBitSet().nextClearBit(0);
            this.nodeSectorMap.set(sector);
            //check if we need to expand file
            long totalLength = (sector + 1L) << NODE_SIZE_SHIFT;
            if (this.file.length() < totalLength)   {
                System.out.printf("Expanding index from %d to %d bytes\n", this.file.length(), totalLength);
                this.file.setLength(totalLength);
                this.channel.write(ByteBuffer.wrap(EMPTY_NODE), (long) sector << NODE_SIZE_SHIFT);
            }
            return sector;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Getter
    protected class TreeNode {
        protected final TreeIndexLookup this_ = TreeIndexLookup.this;
        
        protected final ReadWriteLock lock = new ReentrantReadWriteLock();
        protected final SoftReference<TreeNode>[] subNodes;// = new SoftReference[256];
        protected final ByteBuffer bbuf;
        protected final LongBuffer pointers;
        protected final long pos;
        protected final int depth;

        @SuppressWarnings("unchecked")
        protected TreeNode(long offset, int depth) throws IOException {
            this.pos = offset << NODE_SIZE_SHIFT;
            this.bbuf = ByteBuffer.allocateDirect(2048);
            this.this_.channel.read(this.bbuf, this.pos);
            this.bbuf.flip();
            this.pointers = this.bbuf.asLongBuffer();
            this.depth = depth;
            if (this.depth == this.this_.hashLength - 1) {
                this.subNodes = null;
            } else {
                this.subNodes = (SoftReference<TreeNode>[]) new SoftReference[256];
            }
        }

        public Object get(byte[] hash, boolean createIfAbsent) throws IOException {
            this.lock.readLock().lock();
            try {
                int i = hash[this.depth] & 0xFF;
                if (this.subNodes == null) {
                    return this.pointers.get(i);
                }

                //only call SoftReference#get once, you never know when GC may kick in
                TreeNode node;
                if (this.subNodes[i] == null || (node = this.subNodes[i].get()) == null) {
                    //we'll need to read from disk
                    long l = this.pointers.get(i);
                    if (l == -1L && !createIfAbsent) {
                        //not found, don't create
                        return null;
                    }
                    this.lock.writeLock().lock();
                    try {
                        long nextPos = this.this_.findAndAllocateNewSector();
                        TreeNode next = new TreeNode(nextPos, this.depth + 1);
                        this.subNodes[i] = new SoftReference<>(next);
                        this.pointers.put(i, nextPos);
                        this.bbuf.rewind();
                        this.this_.channel.write(this.bbuf, this.pos);
                        return next;
                    } finally {
                        this.lock.writeLock().unlock();
                    }
                } else {
                    //use cache
                    return node;
                }
            } finally {
                this.lock.readLock().unlock();
            }
        }
    }
}
