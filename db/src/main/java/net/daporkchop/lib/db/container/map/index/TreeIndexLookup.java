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
import lombok.Setter;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.db.data.key.KeyHasher;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author DaPorkchop_
 */
@Getter
public class TreeIndexLookup<K> implements IndexLookup<K> {
    private KeyHasher<K> keyHasher;
    private int hashLength;

    private RandomAccessFile file;
    private FileChannel channel;

    @Override
    public void init(KeyHasher<K> keyHasher, RandomAccessFile file) throws IOException {
        this.keyHasher = keyHasher;
        this.hashLength = this.keyHasher.getHashLength();
        this.file = file;
        IndexLookup.super.init(keyHasher, file);
    }

    @Override
    public void load() throws IOException {
        this.channel = this.file.getChannel();
        this.channel.lock();
    }

    @Override
    public void save() throws IOException {
    }

    @Override
    public void close() throws IOException {
        this.channel.close();
        this.file.close();
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
        return false;
    }

    @Override
    public void setDirty(boolean dirty) {

    }

    @Getter
    protected class TreeNode {
        private static final long NODE_SIZE_SHIFT = 11L;

        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        private final SoftReference<TreeNode>[] subNodes;// = new SoftReference[256];
        private final LongBuffer pointers;
        private final long pos;
        private final int depth;

        @SuppressWarnings("unchecked")
        public TreeNode(long offset, int depth) throws IOException    {
            this.pos = offset << NODE_SIZE_SHIFT;
            ByteBuffer buffer = ByteBuffer.allocateDirect(2048);
            TreeIndexLookup.this.channel.read(buffer, this.pos);
            this.pointers = buffer.asLongBuffer();
            this.depth = depth;
            if (this.depth == TreeIndexLookup.this.hashLength - 1)  {
                this.subNodes = null;
            } else {
                this.subNodes = (SoftReference<TreeNode>[]) new SoftReference[256];
            }
        }

        public Object get(byte[] hash, boolean createIfAbsent)  {
            this.lock.readLock().lock();
            try {
                int i = hash[this.depth] & 0xFF;
                if(this.subNodes == null)   {
                    return this.pointers.get(i);
                }

                //only call SoftReference#get once, you never know when GC may kick in
                TreeNode node;
                if (this.subNodes[i] == null || (node = this.subNodes[i].get()) == null) {
                    //we'll need to read from disk
                    long l = this.pointers.get(i);
                    if (l == -1L)   {

                    }
                    this.lock.writeLock().lock();
                    try {
                        long l
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
