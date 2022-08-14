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

package net.daporkchop.lib.compression.zlib.java;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.pool.recycler.Recycler;
import net.daporkchop.lib.common.util.PorkUtil;

import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Deflater;

/**
 * @author DaPorkchop_
 */
class JavaGzipDeflateStream extends JavaZlibDeflateStream {
    private static final int GZIP_MAGIC = 0x8B1F;

    private static final byte[] HEADER = {
            (byte) GZIP_MAGIC,
            (byte) (GZIP_MAGIC >> 8),
            Deflater.DEFLATED,
            0,
            0,
            0,
            0,
            0,
            0,
            0
    };

    protected final CRC32 crc = new CRC32();

    JavaGzipDeflateStream(@NonNull DataOut out, @NonNull ByteBuf buf, ByteBuf dict, @NonNull JavaZlibDeflater parent) {
        super(out, buf, dict, parent);

        try {
            out.write(HEADER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.crc.reset();
    }

    @Override
    protected void write0(@NonNull byte[] src, int start, int length) throws IOException {
        super.write0(src, start, length);
        this.crc.update(src, start, length);
    }

    @Override
    protected void close0() throws IOException {
        this.ensureValidSession();

        this.def.finish();
        while (!this.def.finished()) {
            int len = this.def.deflate(this.buf.array(), this.buf.arrayOffset(), this.buf.capacity());
            if (len > 0) {
                this.out.write(this.buf.array(), this.buf.arrayOffset(), len);
            }
        }

        { //write trailer
            Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
            byte[] trailer = recycler.allocate();

            this.writeInt((int) this.crc.getValue(), trailer, 0);
            this.writeInt(this.def.getTotalIn(), trailer, 4);
            this.out.write(trailer, 0, 8);

            recycler.release(trailer); //release the buffer to the recycler
        }

        this.out.close();
        this.buf.release();
        this.parent.release();
    }

    private void writeInt(int i, @NonNull byte[] buf, int offset) throws IOException {
        this.writeShort(i & 0xFFFF, buf, offset);
        this.writeShort((i >> 16) & 0xFFFF, buf, offset + 2);
    }

    private void writeShort(int s, @NonNull byte[] buf, int offset) throws IOException {
        buf[offset] = (byte) (s & 0xFF);
        buf[offset + 1] = (byte) ((s >> 8) & 0xFF);
    }
}
