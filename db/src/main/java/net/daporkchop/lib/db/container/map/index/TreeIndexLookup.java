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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.UncheckedExecutionException;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.binary.util.RequiredBits;
import net.daporkchop.lib.db.container.bitset.PersistentSparseBitSet;
import net.daporkchop.lib.db.container.map.DBMap;
import net.daporkchop.lib.db.data.key.KeyHasher;
import sun.misc.Cleaner;
import sun.nio.ch.DirectBuffer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author DaPorkchop_
 */
@Getter
//TODO: this godawful mess won't work well with concurrent removals
//TODO: additionally, this godawful mess is extremely storage inefficient
//TODO: to preserve my limited sanity, nuke this whole godawful mess
public class TreeIndexLookup<K> implements IndexLookup<K> {
    protected static final int NODE_ENTRIES = 256;
    protected static final int NODE_ENTRY_BYTES = Long.BYTES;
    protected static final int NODE_SIZE_SHIFT = RequiredBits.getNumBitsNeededFor(NODE_ENTRY_BYTES * NODE_ENTRIES);
    protected static final int NODE_LENGTH = 1 << NODE_SIZE_SHIFT;
    protected static final byte[] EMPTY_NODE = new byte[NODE_LENGTH];
    private static final RuntimeException NODE_NOT_FOUND_EXCEPTION = new RuntimeException();

    static {
        LongBuffer buffer = ByteBuffer.wrap(EMPTY_NODE).asLongBuffer();
        for (int i = 255; i >= 0; i--) {
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
    public File getFile() {
        return null;
    }

    @Override
    public void init(@NonNull DBMap<K, ?> map, @NonNull RandomAccessFile file) throws IOException {
        if (this.map != null) {
            throw new IllegalStateException("already initialized");
        }
        this.map = map;
        this.keyHasher = this.map.getKeyHasher();
        this.hashLength = this.keyHasher.getHashLength();
        this.file = file;
        this.nodeSectorMap = new PersistentSparseBitSet(this.map.getFile("index.bitmap"));
        IndexLookup.super.init(this.map, file);
    }

    @Override
    public void load() throws IOException {
        this.channel = this.file.getChannel();
        this.channel.lock();
        this.nodeSectorMap.load();
        if (this.file.length() == 0L || !this.nodeSectorMap.get(0)) {
            //allocate root sector
            if (this.nodeSectorMap.get(0)) {
                throw new IllegalStateException("Node sector 0 is already set!");
            }
            this.nodeSectorMap.set(0);
            this.file.setLength(1L << NODE_SIZE_SHIFT);
            this.channel.write(ByteBuffer.wrap(EMPTY_NODE), 0L);
            this.rootNode = new TreeNode(0, 0, null);
            this.nodeSectorMap.save();
        } else {
            this.rootNode = new TreeNode(0L, 0, null);
        }
    }

    @Override
    public void save() throws IOException {
        this.lock.writeLock().lock();
        try {
            if (this.map == null) {
                throw new IllegalStateException("not initialized");
            }
            this.nodeSectorMap.save();
            this.rootNode.save();
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
            this.rootNode.close();
            this.channel.close();
            this.file.close();

            //reset values
            this.channel = null;
            this.file = null;
            this.hashLength = -1;
            this.nodeSectorMap.close();
            this.nodeSectorMap = null;
            this.rootNode = null;
            this.keyHasher = null;
            this.map = null;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public void clear() throws IOException {
        this.lock.writeLock().lock();
        try {
            this.nodeSectorMap.clear();
            this.nodeSectorMap.set(1);
            this.rootNode = null;
            this.nodeSectorMap.set(0);
            this.file.setLength(1L << NODE_SIZE_SHIFT);
            this.channel.write(ByteBuffer.wrap(EMPTY_NODE), 0L);
            this.rootNode = new TreeNode(0, 0, null);

            this.save();
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public long get(K key) throws IOException {
        byte[] hash = this.hash(key);
        TreeNode node = this.rootNode;
        while ((node = node.getNodeAt(hash, false)) != null) {
            if (node.isEnd()) {
                break;
            }
        }
        return node == null ? -1L : node.getOffset(hash);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void set(K key, long val) throws IOException {
        byte[] hash = this.hash(key);
        TreeNode node = this.rootNode;
        while (!node.isEnd()) {
            node = node.getNodeAt(hash, true);
        }
        node.setOffset(hash, val);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(K key) throws IOException {
        /*byte[] hash = this.hash(key);
        TreeNode node = this.rootNode;
        //TODO
        return false;*/
        long off = this.get(key);
        return off != -1L;
    }

    @Override
    public long remove(K key) throws IOException {
        byte[] hash = this.hash(key);
        TreeNode node = this.rootNode;
        //TODO
        return -1L;
    }

    @Override
    public boolean isDirty() {
        return this.nodeSectorMap.isDirty();
    }

    @Override
    public void setDirty(boolean dirty) {
        this.nodeSectorMap.setDirty(true);
    }

    protected byte[] hash(@NonNull K key) throws IOException {
        return this.keyHasher.hash(key);
    }

    protected long findAndAllocateNewSector() throws IOException {
        this.lock.writeLock().lock();
        try {
            //TODO: support 64-bit sectors
            int sector = this.nodeSectorMap.getBitSet().nextClearBit(0);
            this.nodeSectorMap.set(sector);
            //check if we need to expand file
            long totalLength = (sector + 1L) << NODE_SIZE_SHIFT;
            if (this.file.length() < totalLength) {
                System.out.printf("Expanding index from %d to %d bytes\n", this.file.length(), totalLength);
                this.file.setLength(totalLength);
                this.channel.write(ByteBuffer.wrap(EMPTY_NODE), (long) sector << NODE_SIZE_SHIFT);
            }
            return sector;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    protected void removeNode(long pos) throws IOException {
        this.lock.writeLock().lock();
        try {
            this.nodeSectorMap.clear((int) pos);
            if (this.nodeSectorMap.getBitSet().nextSetBit((int) pos) == -1) {
                //shrink file
                this.channel.truncate(this.channel.size() - NODE_LENGTH);
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Getter
    protected class TreeNode {
        protected final TreeIndexLookup this_ = TreeIndexLookup.this;

        protected final ReadWriteLock lock = new ReentrantReadWriteLock();
        protected final long pos;
        protected final int depth;
        protected final MappedByteBuffer buffer;
        protected final TreeNode parent;

        protected final Cache<Integer, TreeNode> nodeCache;

        @SuppressWarnings("unchecked")
        protected TreeNode(long offset, int depth, TreeNode parent) throws IOException {
            this.parent = parent;
            this.pos = offset << NODE_SIZE_SHIFT;
            this.depth = depth;
            this.buffer = this.this_.channel.map(FileChannel.MapMode.READ_WRITE, this.pos, NODE_LENGTH);
            if (this.depth == this.this_.hashLength - 1) {
                this.nodeCache = null;
            } else {
                this.nodeCache = CacheBuilder.newBuilder()
                        .concurrencyLevel(1)
                        .maximumSize(256L)
                        .expireAfterAccess(15L, TimeUnit.SECONDS)
                        .<Integer, TreeNode>removalListener(notification -> notification.getValue().close())
                        .build();
            }
        }

        protected TreeNode getNodeAt(byte[] hash, boolean create) {
            try {
                int i = hash[this.depth];
                return this.nodeCache.get(Integer.valueOf(i), () -> {
                    long pos = this.buffer.getLong(((byte) i & 0xFF) << 3);
                    if (pos < 0L) {
                        if (create) {
                            //TODO: i don't think we need to lock manually here
                            this.lock.writeLock().lock();
                            try {
                                pos = this.this_.findAndAllocateNewSector();
                                this.buffer.putLong(((byte) i & 0xFF) << 3, pos);
                                if (true) {
                                    this.buffer.force();
                                }
                            } finally {
                                this.lock.writeLock().unlock();
                            }
                        } else {
                            throw NODE_NOT_FOUND_EXCEPTION;
                        }
                    }
                    return new TreeNode(pos, this.depth + 1, this);
                });
            } catch (ExecutionException | UncheckedExecutionException e) {
                if (e.getCause() == NODE_NOT_FOUND_EXCEPTION) {
                    return null;
                } else {
                    throw new RuntimeException(String.format("Could not fetch node at offset %d from node at depth %d", hash[this.depth] & 0xFF, this.depth), e);
                }
            }
        }

        protected long getOffset(byte[] hash) {
            return this.buffer.getLong((hash[this.depth] & 0xFF) << 3);
        }

        protected void setOffset(byte[] hash, long val) {
            if (!this.isEnd()) {
                throw new IllegalStateException("not end node");
            }
            this.buffer.putLong((hash[this.depth] & 0xFF) << 3, val);
            if (true) {
                this.buffer.force();
            }
        }

        protected TreeNode getLoadedNode(byte[] hash) {
            return this.nodeCache.getIfPresent(Integer.valueOf(hash[this.depth]));
        }

        protected boolean containsAt(byte b) {
            return this.buffer.getLong((b & 0xFF) << 3) >= 0;
        }

        protected void remove(byte[] hash) throws IOException {
            this.lock.writeLock().lock();
            try {
                byte b = hash[this.depth];
                if (this.containsAt(b)) {
                    this.buffer.putLong((b & 0xFF) << 3, -1L);
                    if (this.depth != 0 && this.getOccupiedValues() == 0) {
                        //remove this node itself
                        this.this_.removeNode(this.pos >> NODE_SIZE_SHIFT);
                        //TODO: this needs to be fixed up
                        this.parent.nodeCache.invalidate(Integer.valueOf(hash[this.depth - 1]));
                        this.parent.buffer.putLong((hash[this.depth - 1] & 0xFF) << 3, -1L);
                    }
                }
            } finally {
                this.lock.writeLock().unlock();
            }
        }

        protected void close() {
            this.buffer.force();
            if (!this.isEnd()) {
                this.nodeCache.asMap().values().forEach(TreeNode::close);
            }
            Cleaner cleaner = ((DirectBuffer) this.buffer).cleaner();
            if (cleaner != null) {
                cleaner.clean();
            }
        }

        protected int getOccupiedValues() {
            int i = 0;
            for (int j = 255; j >= 0; j--) {
                if (this.buffer.getLong(j << 3) >= 0) {
                    i++;
                }
            }
            return i;
        }

        protected boolean isEnd() {
            return this.nodeCache == null;
        }

        protected void save() {
            this.buffer.force();
            if (!this.isEnd()) {
                this.nodeCache.asMap().values().forEach(TreeNode::save);
            }
        }
    }
}
