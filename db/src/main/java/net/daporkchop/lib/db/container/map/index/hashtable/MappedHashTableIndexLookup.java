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
import lombok.Setter;
import net.daporkchop.lib.db.container.map.DBMap;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * A hashtable-based implementation of {@link net.daporkchop.lib.db.container.map.index.IndexLookup}, where
 * the entire hash table file is mapped into memory.
 * <p>
 * This can use a significant amount of memory for large tables. If you want a large table and can accept a
 * small decrease in performance, look at the plain old {@link HashTableIndexLookup}.
 * <p>
 * This does not check for hash collisions! If you would like hash collisions to be checked, use //TODO
 *
 * @author DaPorkchop_
 */
public class MappedHashTableIndexLookup<K> extends BaseHashTableIndexLookup<K> {
    private MappedByteBuffer buffer;
    @Getter
    @Setter
    private volatile boolean dirty;

    public MappedHashTableIndexLookup(int usedBits, int pointerBytes) {
        super(usedBits, pointerBytes);
    }

    @Override
    protected void doInit(@NonNull DBMap<K, ?> map, @NonNull File file) throws IOException {
        long fullSize = (long) this.tableSize * (long) this.pointerBytes;
        this.buffer = this.tableChannel.map(FileChannel.MapMode.READ_WRITE, 0L, fullSize);
    }

    @Override
    protected void doSave() throws IOException {
        this.buffer.force();
    }

    @Override
    protected void doClose() throws IOException {
        this.buffer = null;
    }

    @Override
    protected void doClear() throws IOException {
        for (int i = this.buffer.capacity() - 1; i >= 0; i--) {
            this.buffer.put(i, (byte) 0);
        }
    }

    /*@Override
    protected long doGet(@NonNull K key) throws IOException {
        return this.getDiskValue(key);
    }

    @Override
    protected void doSet(@NonNull K key, long val) throws IOException {
        this.setDiskValue(key, val);
    }

    @Override
    protected boolean doContains(@NonNull K key) throws IOException {
        return this.getDiskValue(key) != 0L;
    }

    @Override
    protected long doRemove(@NonNull K key) throws IOException {
        byte[] hash = this.getHash(key);
        long relevantBits = this.getRelevantHashBits(hash);
        long oldHash = this.getDiskValue(relevantBits);
        this.setDiskValue(relevantBits, 0L);
        return oldHash;
    }*/

    @Override
    protected long getDiskValue(long hashBits)  throws IOException{
        int pos = (int) hashBits;
        if (hashBits < 0L || pos < 0 || hashBits > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(String.format("index too big: %d", hashBits));
        }
        pos *= this.pointerBytes;
        switch (this.pointerBytes)  {
            case 1:
                return this.buffer.get(pos) & 0xFFL;
            case 2:
                return this.buffer.getShort(pos) & 0xFFFFL;
            case 3:
                return (this.buffer.get(pos) & 0xFFL) |
                        ((this.buffer.get(pos + 1) & 0xFFL) << 8L) |
                        ((this.buffer.get(pos + 2) & 0xFFL) << 16L);
            case 4:
                return this.buffer.getInt(pos) & 0xFFFFFFFFL;
            case 5:
                return (this.buffer.get(pos) & 0xFFL) |
                        ((this.buffer.get(pos + 1) & 0xFFL) << 8L) |
                        ((this.buffer.get(pos + 2) & 0xFFL) << 16L) |
                        ((this.buffer.get(pos + 3) & 0xFFL) << 24L) |
                        ((this.buffer.get(pos + 4) & 0xFFL) << 32L);
            case 6:
                return (this.buffer.get(pos) & 0xFFL) |
                        ((this.buffer.get(pos + 1) & 0xFFL) << 8L) |
                        ((this.buffer.get(pos + 2) & 0xFFL) << 16L) |
                        ((this.buffer.get(pos + 3) & 0xFFL) << 24L) |
                        ((this.buffer.get(pos + 4) & 0xFFL) << 32L) |
                        ((this.buffer.get(pos + 5) & 0xFFL) << 40L);
            case 7:
                return (this.buffer.get(pos) & 0xFFL) |
                        ((this.buffer.get(pos + 1) & 0xFFL) << 8L) |
                        ((this.buffer.get(pos + 2) & 0xFFL) << 16L) |
                        ((this.buffer.get(pos + 3) & 0xFFL) << 24L) |
                        ((this.buffer.get(pos + 4) & 0xFFL) << 32L) |
                        ((this.buffer.get(pos + 5) & 0xFFL) << 40L) |
                        ((this.buffer.get(pos + 6) & 0xFFL) << 48L);
            case 8:
                return this.buffer.getLong(pos);
        }
        throw new IllegalStateException();
    }

    @Override
    protected void setDiskValue(long hashBits, long val) throws IOException {
        int pos = (int) hashBits;
        if (hashBits < 0L || pos < 0 || hashBits > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(String.format("index too big: %d", hashBits));
        }
        pos *= this.pointerBytes;
        switch (this.pointerBytes)  {
            case 1: {
                this.buffer.put(pos, (byte) (val & 0xFFL));
            }
            break;
            case 2: {
                this.buffer.putShort(pos, (short) (val & 0xFFFFL));
            }
            break;
            case 3: {
                this.buffer.put(pos, (byte) (val & 0xFFL));
                this.buffer.put(pos + 1, (byte) ((val >>> 8L) & 0xFFL));
                this.buffer.put(pos + 2, (byte) ((val >>> 16L) & 0xFFL));
            }
            break;
            case 4: {
                this.buffer.putInt(pos, (int) (val & 0xFFFFFFFFL));
            }
            break;
            case 5: {
                this.buffer.put(pos, (byte) (val & 0xFFL));
                this.buffer.put(pos + 1, (byte) ((val >>> 8L) & 0xFFL));
                this.buffer.put(pos + 2, (byte) ((val >>> 16L) & 0xFFL));
                this.buffer.put(pos + 3, (byte) ((val >>> 24L) & 0xFFL));
                this.buffer.put(pos + 4, (byte) ((val >>> 32L) & 0xFFL));
            }
            break;
            case 6: {
                this.buffer.put(pos, (byte) (val & 0xFFL));
                this.buffer.put(pos + 1, (byte) ((val >>> 8L) & 0xFFL));
                this.buffer.put(pos + 2, (byte) ((val >>> 16L) & 0xFFL));
                this.buffer.put(pos + 3, (byte) ((val >>> 24L) & 0xFFL));
                this.buffer.put(pos + 4, (byte) ((val >>> 32L) & 0xFFL));
                this.buffer.put(pos + 5, (byte) ((val >>> 40L) & 0xFFL));
            }
            break;
            case 7: {
                this.buffer.put(pos, (byte) (val & 0xFFL));
                this.buffer.put(pos + 1, (byte) ((val >>> 8L) & 0xFFL));
                this.buffer.put(pos + 2, (byte) ((val >>> 16L) & 0xFFL));
                this.buffer.put(pos + 3, (byte) ((val >>> 24L) & 0xFFL));
                this.buffer.put(pos + 4, (byte) ((val >>> 32L) & 0xFFL));
                this.buffer.put(pos + 5, (byte) ((val >>> 40L) & 0xFFL));
                this.buffer.put(pos + 6, (byte) ((val >>> 48L) & 0xFFL));
            }
            break;
            case 8: {
                this.buffer.putLong(pos, val);
            }
            break;
            default:
                throw new IllegalStateException();
        }
    }
}
