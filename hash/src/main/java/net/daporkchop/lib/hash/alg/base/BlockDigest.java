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

package net.daporkchop.lib.hash.alg.base;

import lombok.NonNull;
import net.daporkchop.lib.binary.util.Pack;
import net.daporkchop.lib.hash.util.DigestAlg;

/**
 * A digest algorithm that accepts data in blocks rather than processing it as it's received
 *
 * @author DaPorkchop_
 */
public abstract class BlockDigest implements DigestAlg {
    private static final int BYTE_LENGTH = 64;

    private final byte[] xBuf = new byte[4];
    private int xBufOff;

    private long byteCount;

    protected BlockDigest() {
        this.xBufOff = 0;
    }

    protected BlockDigest(@NonNull byte[] encodedState) {
        System.arraycopy(encodedState, 0, this.xBuf, 0, this.xBuf.length);
        this.xBufOff = Pack.bigEndianToInt(encodedState, 4);
        this.byteCount = Pack.bigEndianToLong(encodedState, 8);
    }

    protected void copyIn(@NonNull BlockDigest t) {
        System.arraycopy(t.xBuf, 0, this.xBuf, 0, t.xBuf.length);

        this.xBufOff = t.xBufOff;
        this.byteCount = t.byteCount;
    }

    public void update(byte in) {
        this.xBuf[this.xBufOff++] = in;

        if (this.xBufOff == this.xBuf.length) {
            this.processWord(this.xBuf, 0);
            this.xBufOff = 0;
        }

        this.byteCount++;
    }

    public void update(byte[] in, int inOff, int len) {
        len = Math.max(0, len);

        // fill the current word
        int i = 0;
        if (this.xBufOff != 0) {
            while (i < len) {
                this.xBuf[this.xBufOff++] = in[inOff + i++];
                if (this.xBufOff == 4) {
                    this.processWord(this.xBuf, 0);
                    this.xBufOff = 0;
                    break;
                }
            }
        }

        // process whole words.
        int limit = ((len - i) & ~3) + i;
        for (; i < limit; i += 4) {
            this.processWord(in, inOff + i);
        }

        // load in the remainder.
        while (i < len) {
            this.xBuf[this.xBufOff++] = in[inOff + i++];
        }

        this.byteCount = (long) (this.byteCount + len);
    }

    public void finish() {
        long bitLength = (this.byteCount << 3);

        // add the pad bytes.
        this.update((byte) 128);

        while (this.xBufOff != 0) {
            this.update((byte) 0);
        }

        this.processLength(bitLength);

        this.processBlock();
    }

    public void reset() {
        this.byteCount = 0L;

        this.xBufOff = 0;
        for (int i = 0; i < this.xBuf.length; i++) {
            this.xBuf[i] = (byte) 0;
        }
    }

    protected void populateState(byte[] state) {
        System.arraycopy(this.xBuf, 0, state, 0, this.xBufOff);
        Pack.intToBigEndian(this.xBufOff, state, 4);
        Pack.longToBigEndian(this.byteCount, state, 8);
    }

    public int getInternalBufferSize() {
        return BYTE_LENGTH;
    }

    protected abstract void processWord(byte[] in, int inOff);

    protected abstract void processLength(long bitLength);

    protected abstract void processBlock();
}
