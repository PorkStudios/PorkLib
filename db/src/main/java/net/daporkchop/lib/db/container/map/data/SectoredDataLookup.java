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

package net.daporkchop.lib.db.container.map.data;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.function.IOConsumer;
import net.daporkchop.lib.db.container.map.DBMap;
import net.daporkchop.lib.db.util.PersistentSparseBitSet;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * An implementation of {@link DataLookup} that stores everything in a single file, split into sectors
 * <p>
 * This eliminates the possibility of completely unused space (as in {@link StreamingDataLookup}), but
 * can result in higher total storage requirements due to headers on each sector.
 * <p>
 * Only superior to {@link SectoredDataLookup} when using large, variable-length data blocks and a
 * high (> 4kb) sector size.
 *
 * @author DaPorkchop_
 */
public class SectoredDataLookup implements DataLookup {
    private static final int SECTOR_HEADER_SIZE = 12; //next sector pointer (64-bit) + length (32-bit)
    private static final ThreadLocal<ByteBuffer> SECTOR_HEADER_CACHE = ThreadLocal.withInitial(() -> ByteBuffer.allocateDirect(SECTOR_HEADER_SIZE));
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final int sectorSize;
    private final byte[] emptySector;
    private final ThreadLocal<ByteBuffer> sectorCache;
    private PersistentSparseBitSet sectors; //TODO: completely disk-based bitset implementation!
    @Getter
    private File file;
    private FileChannel channel;

    public SectoredDataLookup(int sectorSize) {
        if (sectorSize < 64) {
            throw new IllegalArgumentException(String.format("Sector size must be at least 64 bytes (given: %d)", sectorSize));
        }
        this.sectorSize = sectorSize;
        this.emptySector = new byte[sectorSize];
        this.sectorCache = ThreadLocal.withInitial(() -> ByteBuffer.allocateDirect(this.sectorSize));
    }

    @Override
    public void init(@NonNull DBMap<?, ?> map, @NonNull File file) throws IOException {
        if (this.file != null) {
            throw new IllegalStateException("already initialized!");
        }
        this.file = file;
        if (!this.file.exists() && !this.file.mkdirs()) {
            throw new IllegalStateException(String.format("Couldn't create directory: %s", this.file.getAbsolutePath()));
        }
        this.channel = FileChannel.open(new File(this.file, "data").toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        this.sectors = new PersistentSparseBitSet(new File(this.file, "sectors"));

        DataLookup.super.init(map, file);
    }

    @Override
    public void load() throws IOException {
        this.sectors.load();
    }

    @Override
    public void clear() throws IOException {
        this.channel.truncate(0L);
        this.sectors.clear();
    }

    @Override
    public void close() throws IOException {
        if (this.file == null) {
            throw new IllegalStateException("already closed!");
        }
        DataLookup.super.close();

        this.channel.close();
        this.channel = null;
        this.sectors.close();
        this.sectors = null;
        this.file = null;
    }

    @Override
    public DataIn read(long id) throws IOException {
        this.lock.readLock().lock();
        return new DataIn() {
            ByteBuffer buffer = SectoredDataLookup.this.sectorCache.get();
            long next = id;

            {
                this.buffer.clear();
                this.buffer.limit(0);
            }

            @Override
            public int read() throws IOException {
                if (this.buffer != null && !this.buffer.hasRemaining()) {
                    this.readNextBlock();
                }
                if (this.buffer == null) {
                    return -1;
                } else {
                    return this.buffer.get() & 0xFF;
                }
            }

            private void readNextBlock() throws IOException {
                if (this.next == -1L) {
                    this.buffer = null;
                } else {
                    this.buffer.clear();
                    if (SectoredDataLookup.this.channel.read(this.buffer, this.next * SectoredDataLookup.this.sectorSize) == 0) {
                        throw new IllegalStateException(String.format("Invalid sector: %d", this.next));
                    }
                    this.buffer.flip();
                    this.next = this.buffer.getLong();
                    this.buffer.limit(this.buffer.getInt() + SECTOR_HEADER_SIZE);
                }
            }

            @Override
            public void close() throws IOException {
                SectoredDataLookup.this.lock.readLock().unlock();
            }
        };
    }

    @Override
    public long write(long id, @NonNull IOConsumer<DataOut> writer) throws IOException {
        this.lock.writeLock().lock();
        try {
            if (id != -1L) {
                this.remove(id);
            }
            long endId = this.sectors.getBitSet().nextClearBit(0);
            this.sectors.set((int) endId);
            DataOut out = new DataOut() {
                ByteBuffer buffer;
                long id = endId;

                @Override
                public void write(int b) throws IOException {
                    if (this.buffer == null) {
                        this.buffer = SectoredDataLookup.this.sectorCache.get();
                        this.resetBuffer();
                    }
                    this.buffer.put((byte) b);
                    if (!this.buffer.hasRemaining()) {
                        this.flush();
                    }
                }

                private void resetBuffer() {
                    this.buffer.clear();
                    this.buffer.putLong(-1L); //next sector
                    this.buffer.putInt(-1); //sector size
                    this.buffer.position(SECTOR_HEADER_SIZE);
                }

                @Override
                public void flush() throws IOException {
                    this.doFlush(false);
                }

                private void doFlush(boolean end) throws IOException {
                    long nextSector = -1L;
                    if (!end) {
                        //find new sector
                        nextSector = SectoredDataLookup.this.sectors.getBitSet().nextClearBit(0);
                        SectoredDataLookup.this.sectors.set((int) nextSector); //TODO: same thing as everywhere else
                        this.buffer.putLong(0, nextSector);
                    }
                    this.buffer.putInt(8, this.buffer.position() - SECTOR_HEADER_SIZE);
                    this.buffer.flip();
                    SectoredDataLookup.this.channel.write(this.buffer, this.id * SectoredDataLookup.this.sectorSize);
                    this.id = nextSector;
                    this.resetBuffer();
                }

                @Override
                public void close() throws IOException {
                    if (this.buffer != null) {
                        this.doFlush(true);
                        this.buffer = null;
                    }
                }
            };
            writer.accept(out);
            out.close();
            return endId;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public void remove(long id) throws IOException {
        this.lock.writeLock().lock();
        try {
            ByteBuffer buffer = SECTOR_HEADER_CACHE.get();
            do {
                buffer.clear();
                if (this.channel.read(buffer, id * this.sectorSize) != SECTOR_HEADER_SIZE) {
                    throw new IllegalStateException(String.format("Invalid sector: %d", id));
                }
                buffer.flip();
                this.sectors.clear((int) id); //TODO: 64-bit bitset
            } while ((id = buffer.getLong()) != -1L);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public void save() throws IOException {
        this.sectors.save();
    }

    @Override
    public boolean isDirty() {
        return this.sectors.isDirty();
    }

    @Override
    public void setDirty(boolean dirty) {
        this.sectors.setDirty(dirty);
    }
}
