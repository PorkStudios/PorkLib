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
import net.daporkchop.lib.binary.util.NoMoreSpaceException;
import net.daporkchop.lib.common.annotation.param.Positive;
import net.daporkchop.lib.common.pool.recycler.Recycler;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;

import static java.lang.Math.*;

/**
 * Base implementation of {@link DataOut} for direct-only implementations.
 *
 * @author DaPorkchop_
 */
public abstract class AbstractDirectDataOut extends AbstractDataOut {
    static {
        //we copy data between byte[]s and direct buffers
        PUnsafe.requireTightlyPackedPrimitiveArrays();
    }

    @Override
    protected void write0(@NonNull byte[] src, int start, @Positive int length) throws NoMoreSpaceException, IOException {
        Recycler<ByteBuffer> recycler = PorkUtil.directBufferRecycler();
        ByteBuffer buf = recycler.allocate();

        long addr = PUnsafe.pork_directBufferAddress(buf);
        int total = 0;
        do {
            int blockSize = min(length - total, PorkUtil.bufferSize());

            //copy to direct buffer
            PUnsafe.copyMemory(src, PUnsafe.arrayByteElementOffset(start + total), null, addr, blockSize);

            this.write0(addr, blockSize);

            total += blockSize;
        } while (total < length);

        recycler.release(buf); //release the buffer to the recycler
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
    public void writeShort(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();

        Recycler<ByteBuffer> recycler = PorkUtil.directBufferRecycler();
        ByteBuffer buf = recycler.allocate();
        PUnsafe.putUnalignedShortBE(PUnsafe.pork_directBufferAddress(buf), (short) v);
        this.write((ByteBuffer) buf.limit(Short.BYTES)); //write exactly Short.BYTES from the buffer
        recycler.release(buf); //release the buffer to the recycler
    }

    @Override
    public void writeShortLE(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();

        Recycler<ByteBuffer> recycler = PorkUtil.directBufferRecycler();
        ByteBuffer buf = recycler.allocate();
        PUnsafe.putUnalignedShortLE(PUnsafe.pork_directBufferAddress(buf), (short) v);
        this.write((ByteBuffer) buf.limit(Short.BYTES)); //write exactly Short.BYTES from the buffer
        recycler.release(buf); //release the buffer to the recycler
    }

    @Override
    public void writeChar(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();

        Recycler<ByteBuffer> recycler = PorkUtil.directBufferRecycler();
        ByteBuffer buf = recycler.allocate();
        PUnsafe.putUnalignedCharBE(PUnsafe.pork_directBufferAddress(buf), (char) v);
        this.write((ByteBuffer) buf.limit(Character.BYTES)); //write exactly Character.BYTES from the buffer
        recycler.release(buf); //release the buffer to the recycler
    }

    @Override
    public void writeCharLE(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();

        Recycler<ByteBuffer> recycler = PorkUtil.directBufferRecycler();
        ByteBuffer buf = recycler.allocate();
        PUnsafe.putUnalignedCharLE(PUnsafe.pork_directBufferAddress(buf), (char) v);
        this.write((ByteBuffer) buf.limit(Character.BYTES)); //write exactly Character.BYTES from the buffer
        recycler.release(buf); //release the buffer to the recycler
    }

    @Override
    public void writeInt(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();

        Recycler<ByteBuffer> recycler = PorkUtil.directBufferRecycler();
        ByteBuffer buf = recycler.allocate();
        PUnsafe.putUnalignedIntBE(PUnsafe.pork_directBufferAddress(buf), v);
        this.write((ByteBuffer) buf.limit(Integer.BYTES)); //write exactly Integer.BYTES from the buffer
        recycler.release(buf); //release the buffer to the recycler
    }

    @Override
    public void writeIntLE(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();

        Recycler<ByteBuffer> recycler = PorkUtil.directBufferRecycler();
        ByteBuffer buf = recycler.allocate();
        PUnsafe.putUnalignedIntLE(PUnsafe.pork_directBufferAddress(buf), v);
        this.write((ByteBuffer) buf.limit(Integer.BYTES)); //write exactly Integer.BYTES from the buffer
        recycler.release(buf); //release the buffer to the recycler
    }

    @Override
    public void writeLong(long v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();

        Recycler<ByteBuffer> recycler = PorkUtil.directBufferRecycler();
        ByteBuffer buf = recycler.allocate();
        PUnsafe.putUnalignedLongBE(PUnsafe.pork_directBufferAddress(buf), v);
        this.write((ByteBuffer) buf.limit(Long.BYTES)); //write exactly Long.BYTES from the buffer
        recycler.release(buf); //release the buffer to the recycler
    }

    @Override
    public void writeLongLE(long v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();

        Recycler<ByteBuffer> recycler = PorkUtil.directBufferRecycler();
        ByteBuffer buf = recycler.allocate();
        PUnsafe.putUnalignedLongLE(PUnsafe.pork_directBufferAddress(buf), v);
        this.write((ByteBuffer) buf.limit(Long.BYTES)); //write exactly Long.BYTES from the buffer
        recycler.release(buf); //release the buffer to the recycler
    }
}
