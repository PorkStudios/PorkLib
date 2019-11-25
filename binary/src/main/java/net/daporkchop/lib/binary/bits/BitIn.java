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
public class BitIn extends InputStream {
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
