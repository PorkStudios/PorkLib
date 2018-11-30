/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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

    public int getByteLength() {
        return BYTE_LENGTH;
    }

    protected abstract void processWord(byte[] in, int inOff);

    protected abstract void processLength(long bitLength);

    protected abstract void processBlock();
}
