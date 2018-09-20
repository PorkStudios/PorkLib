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

package net.daporkchop.lib.db.io.file;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.db.io.FileManager;
import net.daporkchop.lib.db.util.exception.WrappedException;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;

public class SectoredFile extends OpenFile {
    private static final int SECTOR_BYTES = 4096;
    private static final int SECTOR_INTS = 256;

    private static final byte[] emptySector = new byte[SECTOR_BYTES];
    private final int[] lengths;
    private final int[] sectors;
    private final int[] offsets;
    private final AtomicInteger sizeDelta = new AtomicInteger();
    private BitSet sectorsUsed;
    private int totalSectors;
    @Getter
    private long lastModified;

    public SectoredFile(File path, FileManager fileManager) {
        super(fileManager);

        this.lengths = new int[SECTOR_INTS];
        this.sectors = new int[SECTOR_INTS];
        this.offsets = new int[SECTOR_INTS];

        this.sizeDelta.set(0);

        if (path.exists()) {
            this.lastModified = path.lastModified();
        }

        try {
            if (!path.exists()) {
                File parent = path.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                path.createNewFile();
            }
            this.file = new RandomAccessFile(path, "rw");

            int initialLength = (int) this.file.length();

            // if the file size is under 4KB, grow it (4K data offset table)
            if (this.lastModified == 0 || initialLength < 4096) {
                // fast path for new or region files under 4K
                this.file.write(emptySector);
                this.sizeDelta.set(SECTOR_BYTES);
            } else {
                // seek to the end to prepare for grows
                this.file.seek(initialLength);
                if ((initialLength & (SECTOR_BYTES - 1)) != 0) {
                    // if the file size is not a multiple of 4KB, grow it
                    this.sizeDelta.set(initialLength & (SECTOR_BYTES - 1));
                    System.err.println(
                            "Region \"" + path + "\" not aligned: " + initialLength + " increasing by "
                                    + (
                                    SECTOR_BYTES - (initialLength & (SECTOR_BYTES - 1))));

                    for (long i = 0; i < this.sizeDelta.get(); ++i) {
                        this.file.write(0);
                    }
                }
            }

            // set up the available sector map
            this.totalSectors = (int) this.file.length() / SECTOR_BYTES;
            this.sectorsUsed = new BitSet(this.totalSectors);

            this.sectorsUsed.set(0);

            // read offset table and timestamp tables
            this.file.seek(0);

            ByteBuffer header = ByteBuffer.allocate(SECTOR_BYTES - 1024);
            while (header.hasRemaining()) {
                if (this.file.getChannel().read(header) == -1) {
                    throw new EOFException();
                }
            }
            header.clear();

            // populate the tables
            IntBuffer headerAsInts = header.asIntBuffer();
            for (int i = 0; i < SECTOR_INTS; ++i) {
                int startSector = this.offsets[i] = headerAsInts.get();
                int numSectors = this.sectors[i] = headerAsInts.get();
                this.lengths[i] = headerAsInts.get();

                if (startSector != 0 && startSector >= 0 && startSector + numSectors <= this.totalSectors) {
                    for (int sectorNum = 0; sectorNum < numSectors; ++sectorNum) {
                        this.sectorsUsed.set(startSector + sectorNum);
                    }
                } else if (startSector != 0) {
                    System.err.println(
                            "Region \"" + path + "\": offsets[" + i + "] = " + startSector + " -> "
                                    + startSector
                                    + ',' + numSectors + " does not fit");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new WrappedException("Unable to initialize SectoredFile", e);
        }
    }

    private DataInputStream getInputStream(int x) throws IOException {
        assert (x & 0xFF) == x;

        int sectorNumber = this.offsets[x];
        if (sectorNumber == 0) {
            // does not exist
            return null;
        }

        int numSectors = this.sectors[x];
        if (sectorNumber + numSectors > this.totalSectors) {
            throw new IOException(
                    "Invalid sector: " + sectorNumber + '+' + numSectors + " > " + this.totalSectors);
        }

        this.file.seek(sectorNumber * SECTOR_BYTES);

        byte[] data = new byte[this.lengths[x]];
        this.file.read(data);
        return new DataInputStream(new BufferedInputStream(new ByteArrayInputStream(data)));
    }

    private DataOutputStream getOutputStream(int x) {
        assert (x & 0xFF) == x;
        return new DataOutputStream(new DataBuffer(x));
    }

    private void write(int x, byte[] data, int length) throws IOException {
        int sectorNumber = this.offsets[x];
        int sectorsAllocated = this.sectors[x];
        int sectorsNeeded = length / SECTOR_BYTES + 1;

        if (sectorNumber != 0 && sectorsAllocated == sectorsNeeded) {
            /* we can simply overwrite the old sectors */
            this.doWrite(sectorNumber, data, length);
        } else {
            /* we need to allocate new sectors */

            /* mark the sectors previously used for this data as free */
            for (int i = 0; i < sectorsAllocated; ++i) {
                this.sectorsUsed.clear(sectorNumber + i);
            }

            /* scan for a free space large enough to store this data */
            int runStart = 1;
            int runLength = 0;
            int currentSector = 1;
            while (runLength < sectorsNeeded) {
                if (this.sectorsUsed.length() >= currentSector) {
                    // We reached the end, and we will need to allocate a new sector.
                    break;
                }
                int nextSector = this.sectorsUsed.nextClearBit(currentSector + 1);
                if (currentSector + 1 == nextSector) {
                    runLength++;
                } else {
                    runStart = nextSector;
                    runLength = 1;
                }
                currentSector = nextSector;
            }

            if (runLength >= sectorsNeeded) {
                /* we found a free space large enough */
                sectorNumber = runStart;
                this.setOffset(x, sectorNumber, sectorsNeeded, length);
                for (int i = 0; i < sectorsNeeded; ++i) {
                    this.sectorsUsed.set(sectorNumber + i);
                }
                this.doWrite(sectorNumber, data, length);
            } else {
                /*
                 * no free space large enough found -- we need to grow the
                 * file
                 */
                this.file.seek(this.file.length());
                sectorNumber = this.totalSectors;
                for (int i = 0; i < sectorsNeeded; ++i) {
                    this.file.write(emptySector);
                    this.sectorsUsed.set(this.totalSectors + i);
                }
                this.totalSectors += sectorsNeeded;
                this.sizeDelta.addAndGet(SECTOR_BYTES * sectorsNeeded);

                this.doWrite(sectorNumber, data, length);
                this.setOffset(x, sectorNumber, sectorsNeeded, length);
            }
        }
    }

    private void doWrite(int sectorNumber, byte[] data, int length) throws IOException {
        this.file.seek(sectorNumber * SECTOR_BYTES);
        this.file.write(data, 0, length);
    }

    private void setOffset(int x, int offset, int sectors, int length) throws IOException {
        this.offsets[x] = offset;
        this.sectors[x] = sectors;
        this.lengths[x] = length;
        this.file.seek(x * 12);
        this.file.writeInt(offset);
        this.file.writeInt(sectors);
        this.file.writeInt(length);
    }

    @Override
    public void close() {
        this.lock.lock();
        try {
            this.file.getChannel().force(true);
            this.file.close();
            this.file = null;
        } catch (IOException e) {
            e.printStackTrace();
            throw new WrappedException("Unable to close SectoredFile", e);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public byte[] get(int sector) {
        assert (sector & 0xFF) == sector;

        this.lock.lock();
        try {
            int len = this.lengths[sector];
            if (len == -1 || this.offsets[sector] == 0) {
                return null;
            } else if (len == 0) {
                return new byte[0];
            } else {
                DataInputStream inputStream = this.getInputStream(sector);
                if (inputStream == null) {
                    return null;
                } else {
                    byte[] b = new byte[len];
                    inputStream.readFully(b);
                    inputStream.close();
                    return b;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new WrappedException("Unable to get value from SectoredFile", e);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void put(int sector, @NonNull byte[] data) {
        assert (sector & 0xFF) == sector;

        this.lock.lock();
        try {
            DataOutputStream outputStream = this.getOutputStream(sector);
            outputStream.write(data);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new WrappedException("Unable to put value to SectoredFile", e);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void remove(int sector) {
        assert (sector & 0xFF) == sector;

        this.lock.lock();
        try {
            if (this.offsets[sector] != 0 && this.lengths[sector] != -1) {
                this.write(sector, emptySector, 0);
                this.setOffset(sector, 0, -1, -1);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new WrappedException("Unable to remove value from SectoredFile", e);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean contains(int sector) {
        assert (sector & 0xFF) == sector;

        this.lock.lock();
        try {
            return this.offsets[sector] != 0 && this.lengths[sector] != -1;
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    protected void init() {

    }

    @Override
    protected void writeHeaders() {

    }

    @Override
    protected void readHeaders() {

    }

    class DataBuffer extends ByteArrayOutputStream {

        private final int x;

        public DataBuffer(int x) {
            super(SECTOR_BYTES);
            this.x = x;
        }

        @Override
        public void close() throws IOException {
            SectoredFile.this.write(this.x, this.buf, this.count);
        }
    }
}
