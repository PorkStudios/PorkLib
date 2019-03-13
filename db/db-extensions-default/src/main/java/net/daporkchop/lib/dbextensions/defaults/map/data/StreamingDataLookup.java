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

package net.daporkchop.lib.dbextensions.defaults.map.data;

import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.binary.stream.data.HugeBufferIn;
import net.daporkchop.lib.binary.stream.data.HugeBufferOut;
import net.daporkchop.lib.common.function.io.IOConsumer;
import net.daporkchop.lib.db.container.map.DBMap;
import net.daporkchop.lib.db.container.map.DataLookup;
import net.daporkchop.lib.db.util.PersistentSparseBitSet;
import net.daporkchop.lib.math.primitive.PMath;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This is totally broken, don't use it
 *
 * @author DaPorkchop_
 */
public class StreamingDataLookup implements DataLookup {
    private final int sectorSize;
    protected PersistentSparseBitSet occupiedSectors;
    protected DBMap<?, ?> map;
    protected RandomAccessFile file;
    protected FileChannel channel;

    public StreamingDataLookup(int sectorSize) {
        if (sectorSize <= 64) {
            throw new IllegalArgumentException(String.format("Invalid sector size: %d", sectorSize));
        }
        this.sectorSize = sectorSize;
    }

    @Override
    public void init(@NonNull DBMap<?, ?> map, @NonNull File file) throws IOException {
        if (this.map != null) {
            throw new IllegalStateException("already initialized");
        }
        this.map = map;
        this.file = new RandomAccessFile(file, "rw");
        this.channel = this.file.getChannel();
        this.channel.lock();
        this.occupiedSectors = new PersistentSparseBitSet(this.map.getFile("index.bitmap"));
        DataLookup.super.init(map, file);
    }

    @Override
    public void load() throws IOException {
        this.occupiedSectors.load();
    }

    @Override
    public void save() throws IOException {
        this.occupiedSectors.save();
    }

    @Override
    public DataIn read(long id) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocateDirect(8);
        this.channel.read(buffer, id * this.sectorSize);
        buffer.flip();
        return new HugeBufferIn(this.channel, id * this.sectorSize + 8L, buffer.getLong());
    }

    @Override
    public long write(long id, @NonNull IOConsumer<DataOut> writer) throws IOException {
        AtomicLong newOff = new AtomicLong(-1L);
        DataOut out = new HugeBufferOut(buffers -> {
            ByteBuffer inBuf = ByteBuffer.allocateDirect(8);
            this.channel.read(inBuf, id * this.sectorSize);
            inBuf.flip();
            long oldLength = PMath.roundUp(inBuf.getLong(), this.sectorSize);
            long newLength = buffers.size() * this.sectorSize;
            if (oldLength >= newLength) {
                oldLength /= this.sectorSize;
                newLength /= this.sectorSize;
                //reuse old sectors
                newOff.set(id);
                long l = id * this.sectorSize;
                for (ByteBuffer buffer : buffers) {
                    this.channel.write(buffer, l);
                    l += this.sectorSize;
                }
                if (oldLength > newLength) {
                    //unmark old sectors
                    synchronized (this.occupiedSectors) {
                        int j = (int) (id * this.sectorSize); //TODO
                        for (int i = (int) newLength; i < (int) oldLength; i++) {
                            this.occupiedSectors.clear(j + i);
                        }
                    }
                }
            } else {
                synchronized (this.occupiedSectors) {
                    oldLength /= this.sectorSize;
                    newLength /= this.sectorSize;
                    //unmark old sectors
                    int j = (int) (id * this.sectorSize); //TODO
                    for (int i = 0; i < (int) oldLength; i++) {
                        this.occupiedSectors.clear(j + i);
                    }
                    //find new usable sector strip
                    j = -1;
                    SEARCH:
                    do {
                        j = this.occupiedSectors.getBitSet().nextClearBit(j + 1);
                        for (int i = 1; i < newLength; i++) {
                            if (this.occupiedSectors.get(j + i)) {
                                j += i;
                                continue SEARCH;
                            }
                        }
                        newOff.set(j);
                    } while (newOff.get() == -1L);
                    long l = newOff.get() * this.sectorSize;
                    for (ByteBuffer buffer : buffers) {
                        this.channel.write(buffer, l);
                        l += this.sectorSize;
                    }
                }
            }
        }, this.sectorSize);
        writer.accept(out);
        return newOff.get();
    }

    @Override
    public void remove(long id) throws IOException {
        //TODO
    }

    @Override
    public void clear() throws IOException {
        this.occupiedSectors.clear();
        this.file.setLength(0L);
    }

    @Override
    public boolean isDirty() {
        return this.occupiedSectors.isDirty();
    }

    @Override
    public void setDirty(boolean dirty) {
        this.occupiedSectors.setDirty(dirty);
    }

    @Override
    public File getFile() {
        return null;
    }
}
