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
import net.daporkchop.lib.common.pool.recycler.Recycler;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;
import java.nio.ByteBuffer;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Base implementation of {@link DataIn} for direct-only implementations.
 *
 * @author DaPorkchop_
 */
public abstract class AbstractDirectDataIn extends AbstractDataIn {
    static {
        //we copy data between byte[]s and direct buffers
        PUnsafe.requireTightlyPackedPrimitiveArrays();
    }

    @Override
    protected int read0(@NonNull byte[] dst, int start, int length) throws IOException {
        Recycler<ByteBuffer> recycler = PorkUtil.directBufferRecycler();
        ByteBuffer buf = recycler.allocate();

        long addr = PUnsafe.pork_directBufferAddress(buf);
        int total = 0;
        boolean first = true;
        do {
            int read = toInt(this.read0(addr, min(length - total, PorkUtil.bufferSize())));
            if (read <= 0) {
                return read < 0 && first ? read : total;
            }

            //copy to heap buffer
            PUnsafe.copyMemory(null, addr, dst, PUnsafe.arrayByteElementOffset(start + total), read);

            total += read;
            first = false;
        } while (total < length);

        recycler.release(buf); //release the buffer to the recycler
        return total;
    }

    @Override
    public boolean isDirect() {
        return true;
    }

    //
    //
    // primitives
    //
    //

    @Override
    public short readShort() throws IOException {
        Recycler<ByteBuffer> recycler = PorkUtil.directBufferRecycler();
        ByteBuffer buf = recycler.allocate();
        this.readFully((ByteBuffer) buf.limit(Short.BYTES)); //read exactly Short.BYTES into the buffer
        short val = PUnsafe.getUnalignedShortBE(PUnsafe.pork_directBufferAddress(buf));
        recycler.release(buf); //release the buffer to the recycler
        return val;
    }

    @Override
    public short readShortLE() throws IOException {
        Recycler<ByteBuffer> recycler = PorkUtil.directBufferRecycler();
        ByteBuffer buf = recycler.allocate();
        this.readFully((ByteBuffer) buf.limit(Short.BYTES)); //read exactly Short.BYTES into the buffer
        short val = PUnsafe.getUnalignedShortLE(PUnsafe.pork_directBufferAddress(buf));
        recycler.release(buf); //release the buffer to the recycler
        return val;
    }

    @Override
    public char readChar() throws IOException {
        Recycler<ByteBuffer> recycler = PorkUtil.directBufferRecycler();
        ByteBuffer buf = recycler.allocate();
        this.readFully((ByteBuffer) buf.limit(Character.BYTES)); //read exactly Character.BYTES into the buffer
        char val = PUnsafe.getUnalignedCharBE(PUnsafe.pork_directBufferAddress(buf));
        recycler.release(buf); //release the buffer to the recycler
        return val;
    }

    @Override
    public char readCharLE() throws IOException {
        Recycler<ByteBuffer> recycler = PorkUtil.directBufferRecycler();
        ByteBuffer buf = recycler.allocate();
        this.readFully((ByteBuffer) buf.limit(Character.BYTES)); //read exactly Character.BYTES into the buffer
        char val = PUnsafe.getUnalignedCharLE(PUnsafe.pork_directBufferAddress(buf));
        recycler.release(buf); //release the buffer to the recycler
        return val;
    }

    @Override
    public int readInt() throws IOException {
        Recycler<ByteBuffer> recycler = PorkUtil.directBufferRecycler();
        ByteBuffer buf = recycler.allocate();
        this.readFully((ByteBuffer) buf.limit(Integer.BYTES)); //read exactly Integer.BYTES into the buffer
        int val = PUnsafe.getUnalignedIntBE(PUnsafe.pork_directBufferAddress(buf));
        recycler.release(buf); //release the buffer to the recycler
        return val;
    }

    @Override
    public int readIntLE() throws IOException {
        Recycler<ByteBuffer> recycler = PorkUtil.directBufferRecycler();
        ByteBuffer buf = recycler.allocate();
        this.readFully((ByteBuffer) buf.limit(Integer.BYTES)); //read exactly Integer.BYTES into the buffer
        int val = PUnsafe.getUnalignedIntLE(PUnsafe.pork_directBufferAddress(buf));
        recycler.release(buf); //release the buffer to the recycler
        return val;
    }

    @Override
    public long readLong() throws IOException {
        Recycler<ByteBuffer> recycler = PorkUtil.directBufferRecycler();
        ByteBuffer buf = recycler.allocate();
        this.readFully((ByteBuffer) buf.limit(Long.BYTES)); //read exactly Long.BYTES into the buffer
        long val = PUnsafe.getUnalignedLongBE(PUnsafe.pork_directBufferAddress(buf));
        recycler.release(buf); //release the buffer to the recycler
        return val;
    }

    @Override
    public long readLongLE() throws IOException {
        Recycler<ByteBuffer> recycler = PorkUtil.directBufferRecycler();
        ByteBuffer buf = recycler.allocate();
        this.readFully((ByteBuffer) buf.limit(Long.BYTES)); //read exactly Long.BYTES into the buffer
        long val = PUnsafe.getUnalignedLongLE(PUnsafe.pork_directBufferAddress(buf));
        recycler.release(buf); //release the buffer to the recycler
        return val;
    }

    //
    //
    // other stuff
    //
    //

    @Override
    protected long skip0(long count) throws IOException {
        Recycler<ByteBuffer> recycler = PorkUtil.directBufferRecycler();
        ByteBuffer buf = recycler.allocate();

        long addr = PUnsafe.pork_directBufferAddress(buf);
        long total = 0L;
        boolean first = true;
        do {
            long read = this.read0(addr, min(count - total, PorkUtil.bufferSize()));
            if (read <= 0L) {
                return read < 0L && first ? read : total;
            }

            total += read;
            first = false;
        } while (total < count);

        recycler.release(buf); //release the buffer to the recycler
        return total;
    }

    @Override
    protected long transfer0(@NonNull DataOut dst, long count) throws IOException {
        Recycler<ByteBuffer> recycler = PorkUtil.directBufferRecycler();
        ByteBuffer buf = recycler.allocate();

        long addr = PUnsafe.pork_directBufferAddress(buf);
        long total = 0L;
        boolean first = true;
        do {
            int read = toInt(this.read0(addr, count < 0L ? PorkUtil.bufferSize() : min(count - total, PorkUtil.bufferSize())));
            if (read <= 0) {
                return read < 0 && first ? read : total;
            }

            //write to dst
            buf.position(0).limit(read);
            dst.write(buf);

            total += read;
            first = false;
        } while (count < 0L || total < count);

        recycler.release(buf); //release the buffer to the recycler
        return total;
    }
}
