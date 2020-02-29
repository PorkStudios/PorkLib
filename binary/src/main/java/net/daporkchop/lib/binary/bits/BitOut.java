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
import java.io.OutputStream;

/**
 * Allows writing integers n bits long to an output stream
 *
 * @author DaPorkchop_
 */
public final class BitOut extends OutputStream {
    private static int getNumBitsNeededFor(int value) {
        int count = 0;
        while (value > 0) {
            count++;
            value = value >> 1;
        }
        return count;
    }

    @Getter
    protected final OutputStream output;
    private int readBuf;
    @Getter
    private int bufOffset;

    public BitOut(@NonNull OutputStream output) {
        this.output = output;
        this.initialize();
    }

    @Override
    public void write(int b) throws IOException {
        this.writeBits(8, b);
    }

    private void initialize() {
        this.readBuf = 0;
        this.bufOffset = 8;
    }

    @Override
    public void flush() throws IOException {
        this.padToNextByte();

        this.output.flush();
    }

    @Override
    public void close() throws IOException {
        this.flush();
        this.output.close();
    }

    public void writeBits(int bits, int value) throws IOException {
        if (bits == 0) {
            return;
        }

        value &= (1 << bits) - 1;  // only right most bits valid

        while (bits >= this.bufOffset) {
            this.readBuf = (this.readBuf << this.bufOffset) |
                    (value >> (bits - this.bufOffset));
            this.output.write(this.readBuf);

            value &= (1 << (bits - this.bufOffset)) - 1;
            bits -= this.bufOffset;
            this.bufOffset = 8;
            this.readBuf = 0;
        }

        if (bits > 0) {
            this.readBuf = (this.readBuf << bits) | value;
            this.bufOffset -= bits;
        }
    }

    public void writeLength(int length) throws IOException {
        int bits = getNumBitsNeededFor(length);
        this.writeBits(5, bits);
        this.writeBits(bits, length);
    }

    public void padToNextByte() throws IOException {
        if (this.bufOffset != 8) {
            this.write(this.readBuf << this.bufOffset);
            this.readBuf = 0;
            this.bufOffset = 8;
        }
    }

    public void writeBytesFast(@NonNull byte[] b) throws IOException {
        this.writeBytesFast(b, 0, b.length);
    }

    public void writeBytesFast(@NonNull byte[] b, int off, int len) throws IOException {
        this.padToNextByte();
        this.output.write(b, off, len);
    }
}
