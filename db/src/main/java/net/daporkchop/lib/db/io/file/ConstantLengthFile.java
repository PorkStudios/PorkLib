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

import net.daporkchop.lib.db.io.FileManager;
import net.daporkchop.lib.db.util.exception.WrappedException;

import java.io.File;
import java.io.IOException;

/**
 * @author DaPorkchop_
 */
public class ConstantLengthFile extends OpenFile {
    private static final byte[] EMPTY_HEADERS = new byte[1024];

    private final int size;
    private final int[] index = new int[256];

    public ConstantLengthFile(File file, FileManager fileManager) {
        super(file, fileManager);
        if (fileManager.getDb().getValueSerializer().isConstantLength()) {
            this.size = fileManager.getDb().getValueSerializer().getLength();
        } else {
            this.close();
            throw new IllegalStateException("Value serializer must be constant length!");
        }
    }

    @Override
    public byte[] get(int sector) {
        assert sector == (sector & 0xFF);

        this.lock.lock();
        try {
            long offset = this.index[sector];
            if (offset == 0) {
                return null;
            } else {
                byte[] buf = new byte[this.size];
                this.file.seek(offset);
                this.file.read(buf);
                return buf;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new WrappedException("Unable to get constant length data!", e);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void put(int sector, byte[] data) {
        assert sector == (sector & 0xFF);

        this.lock.lock();
        try {
            long offset = this.index[sector];
            if (offset == 0) {
                //find new offset to write at
                long max = 1024L + this.size * 256L;
                A:
                for (offset = 1024L; offset < max; offset += this.size) {
                    //check if offset is already allocated
                    for (int i = 0; i < 256; i++) {
                        if (this.index[i] == offset) {
                            continue A;
                        }
                    }
                    break;
                }
                this.index[sector] = (int) offset;
            }
            //actually write data
            this.file.seek(offset);
            this.file.write(data, 0, this.size);
        } catch (IOException e) {
            e.printStackTrace();
            throw new WrappedException("Unable to put constant length data!", e);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void remove(int sector) {
        assert sector == (sector & 0xFF);

        this.lock.lock();
        try {
            this.index[sector] = 0;
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean contains(int sector) {
        assert sector == (sector & 0xFF);

        this.lock.lock();
        try {
            return this.index[sector] != 0;
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    protected void init() throws IOException {
        this.file.setLength(1024L);
        this.file.seek(0L);
        this.file.write(EMPTY_HEADERS);
    }

    @Override
    protected void readHeaders() throws IOException {
        this.file.seek(0L);
        for (int i = 0; i < 256; i++) {
            this.index[i] = this.file.readInt();
        }
    }

    @Override
    protected void writeHeaders() throws IOException {
        this.file.seek(0L);
        for (int i = 0; i < 256; i++) {
            this.file.writeInt(this.index[i]);
        }
    }
}
