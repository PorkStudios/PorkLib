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
import net.daporkchop.lib.binary.stream.DataIn;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipException;

/**
 * @author DaPorkchop_
 */
class JavaGzipInflateStream extends JavaZlibInflateStream {
    private final static int GZIP_MAGIC = 0x8B1F;

    private final static int FTEXT = 1;
    private final static int FHCRC = 2;
    private final static int FEXTRA = 4;
    private final static int FNAME = 8;
    private final static int FCOMMENT = 16;

    protected final CRC32 crc = new CRC32();

    protected boolean eos;

    JavaGzipInflateStream(@NonNull DataIn in, @NonNull ByteBuf buf, ByteBuf dict, @NonNull JavaZlibInflater parent) throws IOException {
        super(in, buf, dict, parent);

        this.readHeader(this.in);
    }

    @Override
    protected int read0(@NonNull byte[] dst, int start, int length) throws IOException {
        if (this.eos) {
            return RESULT_EOF;
        }
        int n = super.read0(dst, start, length);
        if (n == RESULT_EOF) {
            if (this.readTrailer()) {
                this.eos = true;
            } else {
                return this.read0(dst, start, length);
            }
        } else {
            this.crc.update(dst, start, n);
        }
        return n;
    }

    private boolean readTrailer() throws IOException {
        DataIn in = this.in;
        int n = this.inf.getRemaining();
        if (n > 0) {
            in = DataIn.wrap(new SequenceInputStream(
                    new ByteArrayInputStream(this.buf.array(), this.buf.arrayOffset() + this.buf.readableBytes() - n, n),
                    new FilterInputStream(this.in.asInputStream()) {
                        @Override
                        public void close() throws IOException {
                        }
                    }));
        }
        if (this.readUInt(in.asInputStream()) != this.crc.getValue()
                || this.readUInt(in.asInputStream()) != (this.inf.getBytesWritten() & 0xFFFFFFFFL)) {
            throw new ZipException("Corrupt GZIP trailer");
        }

        //TODO: don't rely on remaining() being accurate
        if (this.in.remaining() > 0 || n > 26) {
            int m = 8;
            try {
                m += this.readHeader(in);
            } catch (IOException ze) {
                return true;
            }
            this.inf.reset();
            if (n > m) {
                this.inf.setInput(this.buf.array(), this.buf.arrayOffset() + this.buf.readableBytes() - n + m, n - m);
            }
            return false;
        }
        return true;
    }

    private int readHeader(DataIn dataIn) throws IOException {
        InputStream in = new CheckedInputStream(dataIn.asInputStream(), this.crc);
        this.crc.reset();
        if (this.readUShort(in) != GZIP_MAGIC) {
            throw new ZipException("Not in GZIP format");
        }
        if (this.readUByte(in) != 8) {
            throw new ZipException("Unsupported compression method");
        }
        int flg = this.readUByte(in);
        for (int i = 0; i < 6; i++) {
            this.readUByte(in);
        }
        int n = 2 + 2 + 6;
        if ((flg & FEXTRA) == FEXTRA) {
            int m = this.readUShort(in);
            for (int i = 0; i < m; i++) {
                this.readUByte(in);
            }
            n += m + 2;
        }
        if ((flg & FNAME) == FNAME) {
            do {
                n++;
            } while (this.readUByte(in) != 0);
        }
        if ((flg & FCOMMENT) == FCOMMENT) {
            do {
                n++;
            } while (this.readUByte(in) != 0);
        }
        if ((flg & FHCRC) == FHCRC) {
            int v = (int) this.crc.getValue() & 0xFFFF;
            if (this.readUShort(in) != v) {
                throw new ZipException("Corrupt GZIP header");
            }
            n += 2;
        }
        this.crc.reset();
        return n;
    }

    private long readUInt(InputStream in) throws IOException {
        long s = this.readUShort(in);
        return ((long) this.readUShort(in) << 16) | s;
    }

    private int readUShort(InputStream in) throws IOException {
        int b = this.readUByte(in);
        return (this.readUByte(in) << 8) | b;
    }

    private int readUByte(InputStream in) throws IOException {
        int b = in.read();
        if (b < 0) {
            throw new EOFException();
        }
        return b;
    }
}
