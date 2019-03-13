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

package net.daporkchop.lib.dbextensions.defaults.map.index.hashtable;

import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.db.container.map.DBHashMap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An implementation of {@link HashTableIndexLookup} that uses buckets to prevent hash collisions.
 * <p>
 * The cost is that the index size will increase by upwards of 4 times when compared to a plain hashtable (with
 * all value slots filled).
 *
 * @author DaPorkchop_
 * @see HashTableIndexLookup
 * @see BaseHashTableIndexLookup
 */
public class BucketingHashTableIndexLookup<K> extends HashTableIndexLookup<K> {
    private ThreadLocal<ByteBuffer> bucketBufferCache;
    private RandomAccessFile bucketRaf;
    private FileChannel bucketChannel;
    //private PersistentSparseBitSet bucketPointers;
    private long posMultiplier;
    private File dataFile;
    private AtomicLong offset = new AtomicLong();

    public BucketingHashTableIndexLookup(int usedBits, int pointerBytes) {
        super(usedBits, pointerBytes);
    }

    @Override
    protected void doInit(@NonNull DBHashMap<K, ?> map, @NonNull File file) throws IOException {
        super.doInit(map, file);
        //this.bucketPointers = new PersistentSparseBitSet(new File(file, "buckets.index"));
        this.bucketRaf = map.getRAF("index/buckets");
        this.bucketChannel = this.bucketRaf.getChannel();
        this.bucketBufferCache = ThreadLocal.withInitial(() -> ByteBuffer.allocateDirect(this.pointerBytes * 2 + this.keyHasher.getHashLength()));
        this.posMultiplier = this.pointerBytes * 2L + this.keyHasher.getHashLength();
        this.dataFile = map.getFile("index/bucket.meta", out -> {
            out.writeLong(0L);
        }, true);
        try (DataIn in = DataIn.wrap(this.dataFile)) {
            this.offset.set(in.readLong());
        }
    }

    @Override
    protected void doSave() throws IOException {
        super.doSave();

        try (DataOut out = DataOut.wrap(this.dataFile)) {
            out.writeLong(this.offset.get());
        }
    }

    @Override
    protected void doClose() throws IOException {
        super.doClose();
        //this.bucketPointers.close();
        this.bucketChannel.close();
        this.bucketRaf.close();

        //this.bucketPointers = null;
        this.bucketRaf = null;
        this.bucketChannel = null;
        this.bucketBufferCache = null;
    }

    @Override
    protected long getDiskValue(@NonNull byte[] hash) throws IOException {
        ByteBuffer buffer = this.bucketBufferCache.get();
        long next = super.getDiskValue(hash);
        if (next == 0L) {
            return 0L; //if it's not contained then don't break everything
        } else {
            next -= 1L;
        }
        long val;
        do {
            //read hash length + 2 * pointer size bytes after the position
            //position is also multiplied by the size of a bucket entry so that we can get more data in the same space
            buffer.clear();
            this.bucketChannel.read(buffer, next * this.posMultiplier);
            buffer.flip();
            if (this.compare(hash, buffer)) {
                val = this.readFromBuffer(buffer);
                break; //we found a match
            } else {
                this.readFromBuffer(buffer);
                val = 0L; //set val to 0 in case we reached the end of the chain
            }
        } while ((next = this.readFromBuffer(buffer)) != 0L);
        return val;
    }

    @Override
    protected void setDiskValue(@NonNull byte[] hash, long val) throws IOException {
        //TODO: remove unused buckets
        long disk = super.getDiskValue(hash);
        ByteBuffer buffer = this.bucketBufferCache.get();
        //first, check if the hash is currently present on disk
        if (disk == 0L) {
            //the hash is not on disk, so we can add it ourselves
            if (val == 0L) {
                return; //no need to add something that we're about to remove
            }
            long bucketPos = this.offset.getAndIncrement();
            super.setDiskValue(hash, bucketPos + 1L);
            buffer.clear();
            buffer.put(hash);
            this.writeToBuffer(buffer, val);
            this.writeToBuffer(buffer, 0L); //no bucket after this one
            buffer.flip();
            this.bucketChannel.write(buffer, bucketPos * this.posMultiplier);
        } else {
            //the hash is on disk
            //we need to search for the bucket entry that contains this hash, and if not found, create
            // a new bucket and add it to the end of the chain
            long prev;
            long next = disk - 1L;
            do {
                buffer.clear();
                this.bucketChannel.read(buffer, next * this.posMultiplier);
                buffer.flip();
                if (this.compare(hash, buffer)) {
                    //we found a match! we now write the value to the bucket at this position
                    next = (next + 1L) * this.posMultiplier - this.pointerBytes * 2L;
                    buffer.clear();
                    buffer.limit(this.pointerBytes);
                    this.writeToBuffer(buffer, val);
                    buffer.flip();
                    this.bucketChannel.write(buffer, next);
                    return;
                } else {
                    //not a match :(
                    this.readFromBuffer(buffer);
                }
                prev = next;
            } while ((next = this.readFromBuffer(buffer)) != 0L);
            //if we've gotten this far then there's no bucket containing our desired hash, so we need to make one
            long bucketPos = this.offset.getAndIncrement();
            this.dirty = true;
            //write bucket data
            buffer.clear();
            buffer.put(hash);
            this.writeToBuffer(buffer, val);
            this.writeToBuffer(buffer, 0L); //there's nothing after this bucket
            buffer.flip();
            this.bucketChannel.write(buffer, bucketPos * this.posMultiplier);
            //overwrite "next bucket" pointer of previous bucket
            buffer.clear();
            this.bucketChannel.read(buffer, prev * this.posMultiplier);
            buffer.flip();
            buffer.position(hash.length + this.pointerBytes);
            this.writeToBuffer(buffer, bucketPos);
            buffer.flip();
            this.bucketChannel.write(buffer, prev * this.posMultiplier);
        }
    }

    @Override
    protected long doRemove(@NonNull K key) throws IOException {
        //TODO: this can be optimized quite significantly
        byte[] hash = this.getHash(key);
        long oldHash = this.getDiskValue(hash);
        this.setDiskValue(hash, 0L);
        return oldHash;
    }

    protected boolean compare(@NonNull byte[] arr, @NonNull ByteBuffer buffer) {
        boolean flag = true;
        for (int i = 0; i < arr.length; i++) {
            if (buffer.get() != arr[i]) {
                flag = false;
            }
        }
        return flag;
    }
}
