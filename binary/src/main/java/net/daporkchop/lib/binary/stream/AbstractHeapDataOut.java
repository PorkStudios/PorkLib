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

import net.daporkchop.lib.binary.util.NoMoreSpaceException;
import net.daporkchop.lib.common.annotation.param.Positive;
import net.daporkchop.lib.common.pool.recycler.Recycler;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;

import static java.lang.Math.*;

/**
 * Base implementation of {@link DataOut} for heap-only implementations.
 *
 * @author DaPorkchop_
 */
public abstract class AbstractHeapDataOut extends AbstractDataOut {
    static {
        //we copy data between byte[]s and direct buffers
        PUnsafe.requireTightlyPackedPrimitiveArrays();
    }

    @Override
    protected void write0(long addr, @Positive long length) throws NoMoreSpaceException, IOException {
        Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
        byte[] buf = recycler.allocate();

        long total = 0L;
        do {
            int blockSize = (int) min(length - total, PorkUtil.bufferSize());

            //copy to heap buffer
            PUnsafe.copyMemory(null, addr, buf, PUnsafe.arrayByteBaseOffset(), blockSize);

            this.write0(buf, 0, blockSize);

            total += blockSize;
        } while (total < length);

        recycler.release(buf); //release the buffer to the recycler
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
    public void writeShort(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();

        Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
        byte[] buf = recycler.allocate();
        PUnsafe.putUnalignedShortBE(buf, PUnsafe.arrayByteBaseOffset(), (short) v);
        this.write(buf, 0, Short.BYTES); //write exactly Short.BYTES from the buffer
        recycler.release(buf); //release the buffer to the recycler
    }

    @Override
    public void writeShortLE(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();

        Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
        byte[] buf = recycler.allocate();
        PUnsafe.putUnalignedShortLE(buf, PUnsafe.arrayByteBaseOffset(), (short) v);
        this.write(buf, 0, Short.BYTES); //write exactly Short.BYTES from the buffer
        recycler.release(buf); //release the buffer to the recycler
    }

    @Override
    public void writeChar(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();

        Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
        byte[] buf = recycler.allocate();
        PUnsafe.putUnalignedCharBE(buf, PUnsafe.arrayByteBaseOffset(), (char) v);
        this.write(buf, 0, Character.BYTES); //write exactly Character.BYTES from the buffer
        recycler.release(buf); //release the buffer to the recycler
    }

    @Override
    public void writeCharLE(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();

        Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
        byte[] buf = recycler.allocate();
        PUnsafe.putUnalignedCharLE(buf, PUnsafe.arrayByteBaseOffset(), (char) v);
        this.write(buf, 0, Character.BYTES); //write exactly Character.BYTES from the buffer
        recycler.release(buf); //release the buffer to the recycler
    }

    @Override
    public void writeInt(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();

        Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
        byte[] buf = recycler.allocate();
        PUnsafe.putUnalignedIntBE(buf, PUnsafe.arrayByteBaseOffset(), v);
        this.write(buf, 0, Integer.BYTES); //write exactly Integer.BYTES from the buffer
        recycler.release(buf); //release the buffer to the recycler
    }

    @Override
    public void writeIntLE(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();

        Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
        byte[] buf = recycler.allocate();
        PUnsafe.putUnalignedIntLE(buf, PUnsafe.arrayByteBaseOffset(), v);
        this.write(buf, 0, Integer.BYTES); //write exactly Integer.BYTES from the buffer
        recycler.release(buf); //release the buffer to the recycler
    }

    @Override
    public void writeLong(long v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();

        Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
        byte[] buf = recycler.allocate();
        PUnsafe.putUnalignedLongBE(buf, PUnsafe.arrayByteBaseOffset(), v);
        this.write(buf, 0, Long.BYTES); //write exactly Long.BYTES from the buffer
        recycler.release(buf); //release the buffer to the recycler
    }

    @Override
    public void writeLongLE(long v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.ensureOpen();

        Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
        byte[] buf = recycler.allocate();
        PUnsafe.putUnalignedLongLE(buf, PUnsafe.arrayByteBaseOffset(), v);
        this.write(buf, 0, Long.BYTES); //write exactly Long.BYTES from the buffer
        recycler.release(buf); //release the buffer to the recycler
    }
}
