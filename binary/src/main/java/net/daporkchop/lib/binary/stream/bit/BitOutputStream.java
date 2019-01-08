/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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

package net.daporkchop.lib.binary.stream.bit;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.math.primitive.binary.RequiredBits;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Allows writing integers n bits long to an output stream
 *
 * @author DaPorkchop_
 */
public class BitOutputStream extends OutputStream {
    @Getter
    protected final OutputStream output;
    private int readBuf;
    @Getter
    private int bufOffset;

    public BitOutputStream(@NonNull OutputStream output) {
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
        int bits = RequiredBits.getNumBitsNeededFor(length);
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
