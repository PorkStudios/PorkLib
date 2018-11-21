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
 *
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
        for (int i = this.buffer.capacity() - 1; i >= 0; i--)    {
            this.buffer.put(i, (byte) 0);
        }
    }

    @Override
    protected long doGet(@NonNull K key) throws IOException {
        return this.bufferReader.apply(this.buffer, this.getRelevantHashBits(key));
    }

    @Override
    protected void doSet(@NonNull K key, long val) throws IOException {
        this.bufferWriter.write(this.buffer, this.getRelevantHashBits(key), val);
    }

    @Override
    protected boolean doContains(@NonNull K key) throws IOException {
        return this.bufferReader.apply(this.buffer, this.getRelevantHashBits(key)) != 0;
    }

    @Override
    protected long doRemove(@NonNull K key) throws IOException {
        byte[] hash = this.getHash(key);
        int relevantBits = this.getRelevantHashBits(hash);
        long oldHash = this.bufferReader.apply(this.buffer, relevantBits);
        this.bufferWriter.write(this.buffer, relevantBits, 0L);
        return oldHash;
    }
}
