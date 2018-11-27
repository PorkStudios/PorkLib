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

import lombok.NonNull;
import net.daporkchop.lib.db.container.map.DBMap;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A hashtable-based implementation of {@link net.daporkchop.lib.db.container.map.index.IndexLookup}, where
 * the hashes are dynamically read from disk.
 * <p>
 * This can be rather slow. For faster lookups at a cost of higher memory usage, use {@link MappedHashTableIndexLookup}
 * <p>
 * This does not check for hash collisions! If you would like hash collisions to be
 * checked, use {@link BucketingHashTableIndexLookup}
 *
 * @author DaPorkchop_
 * @see MappedHashTableIndexLookup
 * @see BaseHashTableIndexLookup
 */
public class HashTableIndexLookup<K> extends BaseHashTableIndexLookup<K> {
    public HashTableIndexLookup(int usedBits, int pointerBytes) {
        super(usedBits, pointerBytes);
    }

    @Override
    protected void doInit(@NonNull DBMap<K, ?> map, @NonNull File file) throws IOException {
    }

    @Override
    protected void doClose() throws IOException {
    }

    @Override
    protected void doClear() throws IOException {
        this.tableRaf.seek(0L);
        this.tableRaf.setLength(0L);
        long fullSize = (long) this.pointerBytes * this.tableSize;
        //grow file
        byte[] buf = new byte[1024];
        this.tableRaf.seek(0L);
        for (long l = fullSize / 1024L; l >= 0L; l--) {
            this.tableRaf.write(buf);
        }
    }

    @Override
    protected void doSave() throws IOException {
        //no action is required
    }

    @Override
    protected long getDiskValue(long hashBits) throws IOException {
        long pos = hashBits;
        pos *= this.pointerBytes;
        ByteBuffer buffer = this.valueBufferCache.get();
        buffer.clear();
        this.tableChannel.read(buffer, pos);
        buffer.flip();
        return this.readFromBuffer(buffer);
    }

    protected long readFromBuffer(@NonNull ByteBuffer buffer)   {
        switch (this.pointerBytes)  {
            case 1:
                return buffer.get() & 0xFFL;
            case 2:
                return buffer.getShort() & 0xFFFFL;
            case 3:
                return (buffer.get() & 0xFFL) |
                        ((buffer.get() & 0xFFL) << 8L) |
                        ((buffer.get() & 0xFFL) << 16L);
            case 4:
                return buffer.getInt() & 0xFFFFFFFFL;
            case 5:
                return (buffer.get() & 0xFFL) |
                        ((buffer.get() & 0xFFL) << 8L) |
                        ((buffer.get() & 0xFFL) << 16L) |
                        ((buffer.get() & 0xFFL) << 24L) |
                        ((buffer.get() & 0xFFL) << 32L);
            case 6:
                return (buffer.get() & 0xFFL) |
                        ((buffer.get() & 0xFFL) << 8L) |
                        ((buffer.get() & 0xFFL) << 16L) |
                        ((buffer.get() & 0xFFL) << 24L) |
                        ((buffer.get() & 0xFFL) << 32L) |
                        ((buffer.get() & 0xFFL) << 40L);
            case 7:
                return (buffer.get() & 0xFFL) |
                        ((buffer.get() & 0xFFL) << 8L) |
                        ((buffer.get() & 0xFFL) << 16L) |
                        ((buffer.get() & 0xFFL) << 24L) |
                        ((buffer.get() & 0xFFL) << 32L) |
                        ((buffer.get() & 0xFFL) << 40L) |
                        ((buffer.get() & 0xFFL) << 48L);
            case 8:
                return buffer.getLong();
        }
        throw new IllegalStateException();
    }

    @Override
    protected void setDiskValue(long hashBits, long val) throws IOException {
        long pos = hashBits;
        pos *= this.pointerBytes;
        ByteBuffer buffer = this.valueBufferCache.get();
        buffer.clear();
        this.writeToBuffer(buffer, val);
        buffer.flip();
        this.tableChannel.write(buffer, pos);
    }

    protected void writeToBuffer(@NonNull ByteBuffer buffer, long val)  {
        switch (this.pointerBytes)  {
            case 1: {
                buffer.put((byte) (val & 0xFFL));
            }
            break;
            case 2: {
                buffer.putShort((short) (val & 0xFFFFL));
            }
            break;
            case 3: {
                buffer.put((byte) (val & 0xFFL));
                buffer.put((byte) ((val >>> 8L) & 0xFFL));
                buffer.put((byte) ((val >>> 16L) & 0xFFL));
            }
            break;
            case 4: {
                buffer.putInt((int) (val & 0xFFFFFFFFL));
            }
            break;
            case 5: {
                buffer.put((byte) (val & 0xFFL));
                buffer.put((byte) ((val >>> 8L) & 0xFFL));
                buffer.put((byte) ((val >>> 16L) & 0xFFL));
                buffer.put((byte) ((val >>> 24L) & 0xFFL));
                buffer.put((byte) ((val >>> 32L) & 0xFFL));
            }
            break;
            case 6: {
                buffer.put((byte) (val & 0xFFL));
                buffer.put((byte) ((val >>> 8L) & 0xFFL));
                buffer.put((byte) ((val >>> 16L) & 0xFFL));
                buffer.put((byte) ((val >>> 24L) & 0xFFL));
                buffer.put((byte) ((val >>> 32L) & 0xFFL));
                buffer.put((byte) ((val >>> 40L) & 0xFFL));
            }
            break;
            case 7: {
                buffer.put((byte) (val & 0xFFL));
                buffer.put((byte) ((val >>> 8L) & 0xFFL));
                buffer.put((byte) ((val >>> 16L) & 0xFFL));
                buffer.put((byte) ((val >>> 24L) & 0xFFL));
                buffer.put((byte) ((val >>> 32L) & 0xFFL));
                buffer.put((byte) ((val >>> 40L) & 0xFFL));
                buffer.put((byte) ((val >>> 48L) & 0xFFL));
            }
            break;
            case 8: {
                buffer.putLong(val);
            }
            break;
            default:
                throw new IllegalStateException();
        }
    }
}
