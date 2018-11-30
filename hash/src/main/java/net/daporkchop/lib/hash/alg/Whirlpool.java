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

package net.daporkchop.lib.hash.alg;

import lombok.NonNull;
import net.daporkchop.lib.hash.util.DigestAlg;

import java.util.Arrays;

/**
 * Implementation of the Whirlpool hash algorithm
 *
 * @author Some BouncyCastle dev
 */
public class Whirlpool implements DigestAlg {
    private static final int BYTE_LENGTH = 64;

    private static final int DIGEST_LENGTH_BYTES = 512 / 8;
    private static final int ROUNDS = 10;
    private static final int REDUCTION_POLYNOMIAL = 0x011d; // 2^8 + 2^4 + 2^3 + 2 + 1;

    private static final int[] SBOX = {
            0x18, 0x23, 0xc6, 0xe8, 0x87, 0xb8, 0x01, 0x4f, 0x36, 0xa6, 0xd2, 0xf5, 0x79, 0x6f, 0x91, 0x52,
            0x60, 0xbc, 0x9b, 0x8e, 0xa3, 0x0c, 0x7b, 0x35, 0x1d, 0xe0, 0xd7, 0xc2, 0x2e, 0x4b, 0xfe, 0x57,
            0x15, 0x77, 0x37, 0xe5, 0x9f, 0xf0, 0x4a, 0xda, 0x58, 0xc9, 0x29, 0x0a, 0xb1, 0xa0, 0x6b, 0x85,
            0xbd, 0x5d, 0x10, 0xf4, 0xcb, 0x3e, 0x05, 0x67, 0xe4, 0x27, 0x41, 0x8b, 0xa7, 0x7d, 0x95, 0xd8,
            0xfb, 0xee, 0x7c, 0x66, 0xdd, 0x17, 0x47, 0x9e, 0xca, 0x2d, 0xbf, 0x07, 0xad, 0x5a, 0x83, 0x33,
            0x63, 0x02, 0xaa, 0x71, 0xc8, 0x19, 0x49, 0xd9, 0xf2, 0xe3, 0x5b, 0x88, 0x9a, 0x26, 0x32, 0xb0,
            0xe9, 0x0f, 0xd5, 0x80, 0xbe, 0xcd, 0x34, 0x48, 0xff, 0x7a, 0x90, 0x5f, 0x20, 0x68, 0x1a, 0xae,
            0xb4, 0x54, 0x93, 0x22, 0x64, 0xf1, 0x73, 0x12, 0x40, 0x08, 0xc3, 0xec, 0xdb, 0xa1, 0x8d, 0x3d,
            0x97, 0x00, 0xcf, 0x2b, 0x76, 0x82, 0xd6, 0x1b, 0xb5, 0xaf, 0x6a, 0x50, 0x45, 0xf3, 0x30, 0xef,
            0x3f, 0x55, 0xa2, 0xea, 0x65, 0xba, 0x2f, 0xc0, 0xde, 0x1c, 0xfd, 0x4d, 0x92, 0x75, 0x06, 0x8a,
            0xb2, 0xe6, 0x0e, 0x1f, 0x62, 0xd4, 0xa8, 0x96, 0xf9, 0xc5, 0x25, 0x59, 0x84, 0x72, 0x39, 0x4c,
            0x5e, 0x78, 0x38, 0x8c, 0xd1, 0xa5, 0xe2, 0x61, 0xb3, 0x21, 0x9c, 0x1e, 0x43, 0xc7, 0xfc, 0x04,
            0x51, 0x99, 0x6d, 0x0d, 0xfa, 0xdf, 0x7e, 0x24, 0x3b, 0xab, 0xce, 0x11, 0x8f, 0x4e, 0xb7, 0xeb,
            0x3c, 0x81, 0x94, 0xf7, 0xb9, 0x13, 0x2c, 0xd3, 0xe7, 0x6e, 0xc4, 0x03, 0x56, 0x44, 0x7f, 0xa9,
            0x2a, 0xbb, 0xc1, 0x53, 0xdc, 0x0b, 0x9d, 0x6c, 0x31, 0x74, 0xf6, 0x46, 0xac, 0x89, 0x14, 0xe1,
            0x16, 0x3a, 0x69, 0x09, 0x70, 0xb6, 0xd0, 0xed, 0xcc, 0x42, 0x98, 0xa4, 0x28, 0x5c, 0xf8, 0x86
    };

    private static final long[] C0 = new long[256];
    private static final long[] C1 = new long[256];
    private static final long[] C2 = new long[256];
    private static final long[] C3 = new long[256];
    private static final long[] C4 = new long[256];
    private static final long[] C5 = new long[256];
    private static final long[] C6 = new long[256];
    private static final long[] C7 = new long[256];
    // -- buffer information --
    private static final int BITCOUNT_ARRAY_SIZE = 32;
    private static final short[] EIGHT = new short[BITCOUNT_ARRAY_SIZE];

    static {
        EIGHT[BITCOUNT_ARRAY_SIZE - 1] = (short) 8;
    }

    private final long[] _rc = new long[ROUNDS + 1];

    // --------------------------------------------------------------------------------------//
    private final byte[] _buffer = new byte[64];
    private int _bufferPos = 0;
    private final short[] _bitCount = new short[BITCOUNT_ARRAY_SIZE];
    // -- internal hash state --
    private final long[] _hash = new long[8];
    private final long[] _K = new long[8]; // the round key
    private final long[] _L = new long[8];
    private final long[] _block = new long[8]; // mu (buffer)
    private final long[] _state = new long[8]; // the current "cipher" state

    public Whirlpool() {
        for (int i = 0; i < 256; i++) {
            int v1 = SBOX[i];
            int v2 = this.maskWithReductionPolynomial(v1 << 1);
            int v4 = this.maskWithReductionPolynomial(v2 << 1);
            int v5 = v4 ^ v1;
            int v8 = this.maskWithReductionPolynomial(v4 << 1);
            int v9 = v8 ^ v1;

            C0[i] = this.packIntoLong(v1, v1, v4, v1, v8, v5, v2, v9);
            C1[i] = this.packIntoLong(v9, v1, v1, v4, v1, v8, v5, v2);
            C2[i] = this.packIntoLong(v2, v9, v1, v1, v4, v1, v8, v5);
            C3[i] = this.packIntoLong(v5, v2, v9, v1, v1, v4, v1, v8);
            C4[i] = this.packIntoLong(v8, v5, v2, v9, v1, v1, v4, v1);
            C5[i] = this.packIntoLong(v1, v8, v5, v2, v9, v1, v1, v4);
            C6[i] = this.packIntoLong(v4, v1, v8, v5, v2, v9, v1, v1);
            C7[i] = this.packIntoLong(v1, v4, v1, v8, v5, v2, v9, v1);

        }

        this._rc[0] = 0L;
        for (int r = 1; r <= ROUNDS; r++) {
            int i = 8 * (r - 1);
            this._rc[r] = (C0[i] & 0xff00000000000000L) ^
                    (C1[i + 1] & 0x00ff000000000000L) ^
                    (C2[i + 2] & 0x0000ff0000000000L) ^
                    (C3[i + 3] & 0x000000ff00000000L) ^
                    (C4[i + 4] & 0x00000000ff000000L) ^
                    (C5[i + 5] & 0x0000000000ff0000L) ^
                    (C6[i + 6] & 0x000000000000ff00L) ^
                    (C7[i + 7] & 0x00000000000000ffL);
        }

    }

    private long packIntoLong(int b7, int b6, int b5, int b4, int b3, int b2, int b1, int b0) {
        return
                ((long) b7 << 56) ^
                        ((long) b6 << 48) ^
                        ((long) b5 << 40) ^
                        ((long) b4 << 32) ^
                        ((long) b3 << 24) ^
                        ((long) b2 << 16) ^
                        ((long) b1 << 8) ^
                        (long) b0;
    }

    private int maskWithReductionPolynomial(int input) {
        int rv = input;
        if ((long) rv >= 0x100L) // high bit set
        {
            rv ^= REDUCTION_POLYNOMIAL; // reduced by the polynomial
        }
        return rv;
    }

    @Override
    public String getAlgorithmName() {
        return "Whirlpool";
    }

    @Override
    public int getDigestSize() {
        return DIGEST_LENGTH_BYTES;
    }

    @Override
    public int doFinal(@NonNull byte[] out, int outOff) {
        // sets out[outOff] .. out[outOff+DIGEST_LENGTH_BYTES]
        this.finish();

        for (int i = 0; i < 8; i++) {
            this.convertLongToByteArray(this._hash[i], out, outOff + (i * 8));
        }

        this.reset();
        return this.getDigestSize();
    }

    @Override
    public void reset() {
        this._bufferPos = 0;
        Arrays.fill(this._bitCount, (short) 0);
        Arrays.fill(this._buffer, (byte) 0);
        Arrays.fill(this._hash, 0L);
        Arrays.fill(this._K, 0L);
        Arrays.fill(this._L, 0L);
        Arrays.fill(this._block, 0L);
        Arrays.fill(this._state, 0L);
    }

    private void processFilledBuffer(byte[] in) {
        for (int i = 0; i < this._state.length; i++) {
            this._block[i] = this.bytesToLongFromBuffer(this._buffer, i * 8);
        }
        this.processBlock();
        this._bufferPos = 0;
        Arrays.fill(this._buffer, (byte) 0);
    }

    private long bytesToLongFromBuffer(byte[] buffer, int startPos) {
        return (((buffer[startPos + 0] & 0xffL) << 56) |
                ((buffer[startPos + 1] & 0xffL) << 48) |
                ((buffer[startPos + 2] & 0xffL) << 40) |
                ((buffer[startPos + 3] & 0xffL) << 32) |
                ((buffer[startPos + 4] & 0xffL) << 24) |
                ((buffer[startPos + 5] & 0xffL) << 16) |
                ((buffer[startPos + 6] & 0xffL) << 8) |
                ((buffer[startPos + 7]) & 0xffL));
    }

    private void convertLongToByteArray(long inputLong, byte[] outputArray, int offSet) {
        for (int i = 0; i < 8; i++) {
            outputArray[offSet + i] = (byte) ((inputLong >> (56 - (i * 8))) & 0xffL);
        }
    }

    protected void processBlock() {
        // buffer contents have been transferred to the _block[] array via
        // processFilledBuffer

        // compute and apply K^0
        for (int i = 0; i < 8; i++) {
            this._state[i] = this._block[i] ^ (this._K[i] = this._hash[i]);
        }

        // iterate over the rounds
        for (int round = 1; round <= ROUNDS; round++) {
            for (int i = 0; i < 8; i++) {
                this._L[i] = 0L;
                this._L[i] ^= C0[(int) (this._K[(i) & 7] >>> 56) & 0xff];
                this._L[i] ^= C1[(int) (this._K[(i - 1) & 7] >>> 48) & 0xff];
                this._L[i] ^= C2[(int) (this._K[(i - 2) & 7] >>> 40) & 0xff];
                this._L[i] ^= C3[(int) (this._K[(i - 3) & 7] >>> 32) & 0xff];
                this._L[i] ^= C4[(int) (this._K[(i - 4) & 7] >>> 24) & 0xff];
                this._L[i] ^= C5[(int) (this._K[(i - 5) & 7] >>> 16) & 0xff];
                this._L[i] ^= C6[(int) (this._K[(i - 6) & 7] >>> 8) & 0xff];
                this._L[i] ^= C7[(int) (this._K[(i - 7) & 7]) & 0xff];
            }

            System.arraycopy(this._L, 0, this._K, 0, this._K.length);

            this._K[0] ^= this._rc[round];

            // apply the round transformation
            for (int i = 0; i < 8; i++) {
                this._L[i] = this._K[i];

                this._L[i] ^= C0[(int) (this._state[(i) & 7] >>> 56) & 0xff];
                this._L[i] ^= C1[(int) (this._state[(i - 1) & 7] >>> 48) & 0xff];
                this._L[i] ^= C2[(int) (this._state[(i - 2) & 7] >>> 40) & 0xff];
                this._L[i] ^= C3[(int) (this._state[(i - 3) & 7] >>> 32) & 0xff];
                this._L[i] ^= C4[(int) (this._state[(i - 4) & 7] >>> 24) & 0xff];
                this._L[i] ^= C5[(int) (this._state[(i - 5) & 7] >>> 16) & 0xff];
                this._L[i] ^= C6[(int) (this._state[(i - 6) & 7] >>> 8) & 0xff];
                this._L[i] ^= C7[(int) (this._state[(i - 7) & 7]) & 0xff];
            }

            // save the current state
            System.arraycopy(this._L, 0, this._state, 0, this._state.length);
        }

        // apply Miuaguchi-Preneel compression
        for (int i = 0; i < 8; i++) {
            this._hash[i] ^= this._state[i] ^ this._block[i];
        }

    }

    @Override
    public void update(byte in) {
        this._buffer[this._bufferPos] = in;

        //System.out.println("adding to buffer = "+_buffer[_bufferPos]);

        ++this._bufferPos;

        if (this._bufferPos == this._buffer.length) {
            this.processFilledBuffer(this._buffer);
        }

        this.increment();
    }

    private void increment() {
        int carry = 0;
        for (int i = this._bitCount.length - 1; i >= 0; i--) {
            int sum = (this._bitCount[i] & 0xff) + EIGHT[i] + carry;

            carry = sum >>> 8;
            this._bitCount[i] = (short) (sum & 0xff);
        }
    }

    @Override
    public void update(@NonNull byte[] in, int inOff, int len) {
        while (len > 0) {
            this.update(in[inOff]);
            ++inOff;
            --len;
        }

    }

    private void finish() {
        byte[] bitLength = this.copyBitLength();

        this._buffer[this._bufferPos++] = (byte) (this._buffer[this._bufferPos++] | 0x80);

        if (this._bufferPos == this._buffer.length) {
            this.processFilledBuffer(this._buffer);
        }

        if (this._bufferPos > 32) {
            while (this._bufferPos != 0) {
                this.update((byte) 0);
            }
        }

        while (this._bufferPos <= 32) {
            this.update((byte) 0);
        }

        System.arraycopy(bitLength, 0, this._buffer, 32, bitLength.length);

        this.processFilledBuffer(this._buffer);
    }

    private byte[] copyBitLength() {
        byte[] rv = new byte[BITCOUNT_ARRAY_SIZE];
        for (int i = 0; i < rv.length; i++) {
            rv[i] = (byte) (this._bitCount[i] & 0xff);
        }
        return rv;
    }

    @Override
    public int getByteLength() {
        return BYTE_LENGTH;
    }
}
