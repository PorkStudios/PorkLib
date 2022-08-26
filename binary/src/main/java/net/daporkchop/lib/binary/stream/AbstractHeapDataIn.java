/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.binary.stream;

import lombok.NonNull;
import net.daporkchop.lib.common.annotation.param.Positive;
import net.daporkchop.lib.common.pool.recycler.Recycler;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.EOFException;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;

import static java.lang.Math.*;

/**
 * Base implementation of {@link DataIn} for heap-only implementations.
 *
 * @author DaPorkchop_
 */
public abstract class AbstractHeapDataIn extends AbstractDataIn {
    static {
        //we copy data between byte[]s and direct buffers
        PUnsafe.requireTightlyPackedPrimitiveArrays();
    }

    @Override
    protected long read0(long addr, @Positive long length) throws IOException {
        Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
        byte[] buf = recycler.allocate();

        long total = 0L;
        boolean first = true;
        do {
            int read = this.read0(buf, 0, (int) min(length - total, PorkUtil.bufferSize()));
            if (read <= 0) {
                return read < 0 && first ? read : total;
            }

            //copy to direct buffer
            PUnsafe.copyMemory(buf, PUnsafe.arrayByteBaseOffset(), null, addr + total, read);

            total += read;
            first = false;
        } while (total < length);

        recycler.release(buf); //release the buffer to the recycler
        return total;
    }

    @Override
    public boolean isHeap() {
        return true;
    }

    //
    //
    // primitives
    //
    //

    @Override
    public short readShort() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
        byte[] buf = recycler.allocate();
        this.readFully(buf, 0, Short.BYTES); //read exactly Short.BYTES into the buffer
        short val = PUnsafe.getUnalignedShortBE(buf, PUnsafe.arrayByteBaseOffset());
        recycler.release(buf); //release the buffer to the recycler
        return val;
    }

    @Override
    public short readShortLE() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
        byte[] buf = recycler.allocate();
        this.readFully(buf, 0, Short.BYTES); //read exactly Short.BYTES into the buffer
        short val = PUnsafe.getUnalignedShortLE(buf, PUnsafe.arrayByteBaseOffset());
        recycler.release(buf); //release the buffer to the recycler
        return val;
    }

    @Override
    public char readChar() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
        byte[] buf = recycler.allocate();
        this.readFully(buf, 0, Character.BYTES); //read exactly Character.BYTES into the buffer
        char val = PUnsafe.getUnalignedCharBE(buf, PUnsafe.arrayByteBaseOffset());
        recycler.release(buf); //release the buffer to the recycler
        return val;
    }

    @Override
    public char readCharLE() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
        byte[] buf = recycler.allocate();
        this.readFully(buf, 0, Character.BYTES); //read exactly Character.BYTES into the buffer
        char val = PUnsafe.getUnalignedCharLE(buf, PUnsafe.arrayByteBaseOffset());
        recycler.release(buf); //release the buffer to the recycler
        return val;
    }

    @Override
    public int readInt() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
        byte[] buf = recycler.allocate();
        this.readFully(buf, 0, Integer.BYTES); //read exactly Integer.BYTES into the buffer
        int val = PUnsafe.getUnalignedIntBE(buf, PUnsafe.arrayByteBaseOffset());
        recycler.release(buf); //release the buffer to the recycler
        return val;
    }

    @Override
    public int readIntLE() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
        byte[] buf = recycler.allocate();
        this.readFully(buf, 0, Integer.BYTES); //read exactly Integer.BYTES into the buffer
        int val = PUnsafe.getUnalignedIntLE(buf, PUnsafe.arrayByteBaseOffset());
        recycler.release(buf); //release the buffer to the recycler
        return val;
    }

    @Override
    public long readLong() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
        byte[] buf = recycler.allocate();
        this.readFully(buf, 0, Long.BYTES); //read exactly Long.BYTES into the buffer
        long val = PUnsafe.getUnalignedLongBE(buf, PUnsafe.arrayByteBaseOffset());
        recycler.release(buf); //release the buffer to the recycler
        return val;
    }

    @Override
    public long readLongLE() throws ClosedChannelException, EOFException, IOException {
        this.ensureOpen();

        Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
        byte[] buf = recycler.allocate();
        this.readFully(buf, 0, Long.BYTES); //read exactly Long.BYTES into the buffer
        long val = PUnsafe.getUnalignedLongLE(buf, PUnsafe.arrayByteBaseOffset());
        recycler.release(buf); //release the buffer to the recycler
        return val;
    }

    //
    //
    // other stuff
    //
    //

    @Override
    protected long skip0(@Positive long count) throws IOException {
        Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
        byte[] buf = recycler.allocate();

        long total = 0L;
        boolean first = true;
        do {
            int read = this.read0(buf, 0, (int) min(count - total, PorkUtil.bufferSize()));
            if (read <= 0) {
                return read < 0 && first ? read : total;
            }

            total += read;
            first = false;
        } while (total < count);

        recycler.release(buf); //release the buffer to the recycler
        return total;
    }

    @Override
    protected long transfer0(@NonNull DataOut dst, @Positive long count) throws IOException {
        Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
        byte[] buf = recycler.allocate();

        long total = 0L;
        do {
            int read = this.read0(buf, 0, count < 0L ? PorkUtil.bufferSize() : (int) min(count - total, PorkUtil.bufferSize()));
            switch (read) {
                case RESULT_EOF:
                    return total == 0L ? RESULT_EOF : total;
                case 0:
                    return total;
            }

            //write to dst
            dst.write(buf, 0, read);

            total += read;
        } while (count < 0L || total < count);

        recycler.release(buf); //release the buffer to the recycler
        return total;
    }
}
