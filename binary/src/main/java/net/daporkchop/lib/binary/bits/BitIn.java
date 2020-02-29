/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
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

package net.daporkchop.lib.binary.bits;

import lombok.Getter;
import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * Allows reading integers n bits in length from a byte array
 *
 * @author DaPorkchop_
 */
public final class BitIn extends InputStream {
    @Getter
    private final InputStream input;
    private int bitOffset;
    private int readBuf;

    public BitIn(@NonNull InputStream input) {
        this.input = input;
    }

    @Override
    public void close() throws IOException {
        if (this.input != null) {
            this.input.close();
        }
    }

    public int readBits(int bits) throws IOException {
        if (bits == 0) {
            return 0;
        }

        int toReturn = 0;
        if (this.input == null) {
            return -1;
        }

        while (bits > this.bitOffset) {
            toReturn |= (this.readBuf << (bits - this.bitOffset));
            bits -= this.bitOffset;
            if ((this.readBuf = this.input.read()) == -1) {
                return -1;
            }
            this.bitOffset = 8;
        }

        if (bits > 0) {
            toReturn |= this.readBuf >> (this.bitOffset - bits);
            this.readBuf &= (1 << (this.bitOffset - bits)) - 1;
            this.bitOffset -= bits;
        }
        return toReturn;
    }

    @Override
    public int read() throws IOException {
        return this.readBits(8);
    }

    @Override
    public int read(@NonNull byte[] b, int off, int len) throws IOException {
        for (int i = 0; i < len; i++) {
            int j = this.read();
            if (j == -1) {
                return i;
            } else {
                b[off + i] = (byte) j;
            }
        }
        return len;
    }

    public int readLength() throws IOException {
        int bits = this.readBits(5);
        return this.readBits(bits);
    }

    public void padToNextByte() throws IOException {
        this.readBits(this.bitOffset);
    }
}
