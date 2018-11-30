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

import net.daporkchop.lib.binary.util.Pack;
import net.daporkchop.lib.hash.util.DigestAlg;

import java.util.Arrays;

/**
 * Implementation of the Keccak hash algorithm
 *
 * @author Some BouncyCastle dev
 */
public class Keccak implements DigestAlg {
    private static final long[] KeccakRoundConstants = new long[]{0x0000000000000001L, 0x0000000000008082L,
            0x800000000000808aL, 0x8000000080008000L, 0x000000000000808bL, 0x0000000080000001L, 0x8000000080008081L,
            0x8000000000008009L, 0x000000000000008aL, 0x0000000000000088L, 0x0000000080008009L, 0x000000008000000aL,
            0x000000008000808bL, 0x800000000000008bL, 0x8000000000008089L, 0x8000000000008003L, 0x8000000000008002L,
            0x8000000000000080L, 0x000000000000800aL, 0x800000008000000aL, 0x8000000080008081L, 0x8000000000008080L,
            0x0000000080000001L, 0x8000000080008008L};

    protected final long[] state = new long[25];
    protected final byte[] dataQueue = new byte[192];
    protected int rate;
    protected int bitsInQueue;
    protected int fixedOutputLength;
    protected boolean squeezing;

    public Keccak() {
        this(288);
    }

    public Keccak(int bitLength) {
        this.init(bitLength);
    }

    @Override
    public String getAlgorithmName() {
        return String.format("Keccak-%d", this.fixedOutputLength);
    }

    @Override
    public int getHashSize() {
        return this.fixedOutputLength / 8;
    }

    @Override
    public void update(byte in) {
        this.absorb(new byte[]{in}, 0, 1);
    }

    @Override
    public void update(byte[] in, int inOff, int len) {
        this.absorb(in, inOff, len);
    }

    @Override
    public int doFinal(byte[] out, int outOff) {
        this.squeeze(out, outOff, (long) this.fixedOutputLength);

        this.reset();

        return this.getHashSize();
    }

    protected int doFinal(byte[] out, int outOff, byte partialByte, int partialBits) {
        if (partialBits > 0) {
            this.absorbBits((int) partialByte, partialBits);
        }

        this.squeeze(out, outOff, (long) this.fixedOutputLength);

        this.reset();

        return this.getHashSize();
    }

    @Override
    public void reset() {
        this.init(this.fixedOutputLength);
    }

    @Override
    public int getInternalBufferSize() {
        return this.rate / 8;
    }

    private void init(int bitLength) {
        switch (bitLength) {
            case 128:
            case 224:
            case 256:
            case 288:
            case 384:
            case 512:
                this.initSponge(1600 - (bitLength << 1));
                break;
            default:
                throw new IllegalArgumentException("bitLength must be one of 128, 224, 256, 288, 384, or 512.");
        }
    }

    private void initSponge(int rate) {
        if ((rate <= 0) || (rate >= 1600) || ((rate % 64) != 0)) {
            throw new IllegalStateException("invalid rate value");
        }

        this.rate = rate;
        for (int i = 0; i < this.state.length; ++i) {
            this.state[i] = 0L;
        }
        Arrays.fill(this.dataQueue, (byte) 0);
        this.bitsInQueue = 0;
        this.squeezing = false;
        this.fixedOutputLength = (1600 - rate) / 2;
    }

    protected void absorb(byte[] data, int off, int len) {
        if ((this.bitsInQueue % 8) != 0) {
            throw new IllegalStateException("attempt to absorb with odd length queue");
        }
        if (this.squeezing) {
            throw new IllegalStateException("attempt to absorb while squeezing");
        }

        int bytesInQueue = this.bitsInQueue >> 3;
        int rateBytes = this.rate >> 3;

        int count = 0;
        while (count < len) {
            if (bytesInQueue == 0 && count <= (len - rateBytes)) {
                do {
                    this.KeccakAbsorb(data, off + count);
                    count += rateBytes;
                }
                while (count <= (len - rateBytes));
            } else {
                int partialBlock = Math.min(rateBytes - bytesInQueue, len - count);
                System.arraycopy(data, off + count, this.dataQueue, bytesInQueue, partialBlock);

                bytesInQueue += partialBlock;
                count += partialBlock;

                if (bytesInQueue == rateBytes) {
                    this.KeccakAbsorb(this.dataQueue, 0);
                    bytesInQueue = 0;
                }
            }
        }

        this.bitsInQueue = bytesInQueue << 3;
    }

    protected void absorbBits(int data, int bits) {
        if (bits < 1 || bits > 7) {
            throw new IllegalArgumentException("'bits' must be in the range 1 to 7");
        }
        if ((this.bitsInQueue % 8) != 0) {
            throw new IllegalStateException("attempt to absorb with odd length queue");
        }
        if (this.squeezing) {
            throw new IllegalStateException("attempt to absorb while squeezing");
        }

        int mask = (1 << bits) - 1;
        this.dataQueue[this.bitsInQueue >> 3] = (byte) (data & mask);

        // NOTE: After this, bitsInQueue is no longer a multiple of 8, so no more absorbs will work
        this.bitsInQueue += bits;
    }

    private void padAndSwitchToSqueezingPhase() {
        this.dataQueue[this.bitsInQueue >> 3] |= (byte) (1L << (this.bitsInQueue & 7));

        if (++this.bitsInQueue == this.rate) {
            this.KeccakAbsorb(this.dataQueue, 0);
            this.bitsInQueue = 0;
        }

        {
            int full = this.bitsInQueue >> 6;
            int partial = this.bitsInQueue & 63;
            int off = 0;
            for (int i = 0; i < full; ++i) {
                this.state[i] ^= Pack.littleEndianToLong(this.dataQueue, off);
                off += 8;
            }
            if (partial > 0) {
                long mask = (1L << partial) - 1L;
                this.state[full] ^= Pack.littleEndianToLong(this.dataQueue, off) & mask;
            }
            this.state[(this.rate - 1) >> 6] ^= (1L << 63);
        }

        this.KeccakPermutation();

        this.KeccakExtract();
        this.bitsInQueue = this.rate;

        this.squeezing = true;
    }

    protected void squeeze(byte[] output, int offset, long outputLength) {
        if (!this.squeezing) {
            this.padAndSwitchToSqueezingPhase();
        }
        if ((outputLength % 8L) != 0L) {
            throw new IllegalStateException("outputLength not a multiple of 8");
        }

        long i = 0L;
        while (i < outputLength) {
            if (this.bitsInQueue == 0) {
                this.KeccakPermutation();
                this.KeccakExtract();
                this.bitsInQueue = this.rate;
            }
            int partialBlock = (int) Math.min((long) this.bitsInQueue, outputLength - i);
            System.arraycopy(this.dataQueue, (this.rate - this.bitsInQueue) / 8, output, offset + (int) (i / 8L), partialBlock / 8);
            this.bitsInQueue -= partialBlock;
            i = (long) (i + partialBlock);
        }
    }

    private void KeccakAbsorb(byte[] data, int off) {
        int count = this.rate >> 6;
        for (int i = 0; i < count; ++i) {
            this.state[i] ^= Pack.littleEndianToLong(data, off);
            off += 8;
        }

        this.KeccakPermutation();
    }

    private void KeccakExtract() {
        Pack.longToLittleEndian(this.state, 0, this.rate >> 6, this.dataQueue, 0);
    }

    private void KeccakPermutation() {
        long[] A = this.state;

        long a00 = A[0];
        long a01 = A[1];
        long a02 = A[2];
        long a03 = A[3];
        long a04 = A[4];
        long a05 = A[5];
        long a06 = A[6];
        long a07 = A[7];
        long a08 = A[8];
        long a09 = A[9];
        long a10 = A[10];
        long a11 = A[11];
        long a12 = A[12];
        long a13 = A[13];
        long a14 = A[14];
        long a15 = A[15];
        long a16 = A[16];
        long a17 = A[17];
        long a18 = A[18];
        long a19 = A[19];
        long a20 = A[20];
        long a21 = A[21];
        long a22 = A[22];
        long a23 = A[23];
        long a24 = A[24];

        for (int i = 0; i < 24; i++) {
            // theta
            long c0 = a00 ^ a05 ^ a10 ^ a15 ^ a20;
            long c1 = a01 ^ a06 ^ a11 ^ a16 ^ a21;
            long c2 = a02 ^ a07 ^ a12 ^ a17 ^ a22;
            long c3 = a03 ^ a08 ^ a13 ^ a18 ^ a23;
            long c4 = a04 ^ a09 ^ a14 ^ a19 ^ a24;

            long d1 = (c1 << 1 | c1 >>> -1) ^ c4;
            long d2 = (c2 << 1 | c2 >>> -1) ^ c0;
            long d3 = (c3 << 1 | c3 >>> -1) ^ c1;
            long d4 = (c4 << 1 | c4 >>> -1) ^ c2;
            long d0 = (c0 << 1 | c0 >>> -1) ^ c3;

            a00 ^= d1;
            a05 ^= d1;
            a10 ^= d1;
            a15 ^= d1;
            a20 ^= d1;
            a01 ^= d2;
            a06 ^= d2;
            a11 ^= d2;
            a16 ^= d2;
            a21 ^= d2;
            a02 ^= d3;
            a07 ^= d3;
            a12 ^= d3;
            a17 ^= d3;
            a22 ^= d3;
            a03 ^= d4;
            a08 ^= d4;
            a13 ^= d4;
            a18 ^= d4;
            a23 ^= d4;
            a04 ^= d0;
            a09 ^= d0;
            a14 ^= d0;
            a19 ^= d0;
            a24 ^= d0;

            // rho/pi
            c1 = a01 << 1 | a01 >>> 63;
            a01 = a06 << 44 | a06 >>> 20;
            a06 = a09 << 20 | a09 >>> 44;
            a09 = a22 << 61 | a22 >>> 3;
            a22 = a14 << 39 | a14 >>> 25;
            a14 = a20 << 18 | a20 >>> 46;
            a20 = a02 << 62 | a02 >>> 2;
            a02 = a12 << 43 | a12 >>> 21;
            a12 = a13 << 25 | a13 >>> 39;
            a13 = a19 << 8 | a19 >>> 56;
            a19 = a23 << 56 | a23 >>> 8;
            a23 = a15 << 41 | a15 >>> 23;
            a15 = a04 << 27 | a04 >>> 37;
            a04 = a24 << 14 | a24 >>> 50;
            a24 = a21 << 2 | a21 >>> 62;
            a21 = a08 << 55 | a08 >>> 9;
            a08 = a16 << 45 | a16 >>> 19;
            a16 = a05 << 36 | a05 >>> 28;
            a05 = a03 << 28 | a03 >>> 36;
            a03 = a18 << 21 | a18 >>> 43;
            a18 = a17 << 15 | a17 >>> 49;
            a17 = a11 << 10 | a11 >>> 54;
            a11 = a07 << 6 | a07 >>> 58;
            a07 = a10 << 3 | a10 >>> 61;
            a10 = c1;

            // chi
            c0 = a00 ^ (~a01 & a02);
            c1 = a01 ^ (~a02 & a03);
            a02 ^= ~a03 & a04;
            a03 ^= ~a04 & a00;
            a04 ^= ~a00 & a01;
            a00 = c0;
            a01 = c1;

            c0 = a05 ^ (~a06 & a07);
            c1 = a06 ^ (~a07 & a08);
            a07 ^= ~a08 & a09;
            a08 ^= ~a09 & a05;
            a09 ^= ~a05 & a06;
            a05 = c0;
            a06 = c1;

            c0 = a10 ^ (~a11 & a12);
            c1 = a11 ^ (~a12 & a13);
            a12 ^= ~a13 & a14;
            a13 ^= ~a14 & a10;
            a14 ^= ~a10 & a11;
            a10 = c0;
            a11 = c1;

            c0 = a15 ^ (~a16 & a17);
            c1 = a16 ^ (~a17 & a18);
            a17 ^= ~a18 & a19;
            a18 ^= ~a19 & a15;
            a19 ^= ~a15 & a16;
            a15 = c0;
            a16 = c1;

            c0 = a20 ^ (~a21 & a22);
            c1 = a21 ^ (~a22 & a23);
            a22 ^= ~a23 & a24;
            a23 ^= ~a24 & a20;
            a24 ^= ~a20 & a21;
            a20 = c0;
            a21 = c1;

            // iota
            a00 ^= KeccakRoundConstants[i];
        }

        A[0] = a00;
        A[1] = a01;
        A[2] = a02;
        A[3] = a03;
        A[4] = a04;
        A[5] = a05;
        A[6] = a06;
        A[7] = a07;
        A[8] = a08;
        A[9] = a09;
        A[10] = a10;
        A[11] = a11;
        A[12] = a12;
        A[13] = a13;
        A[14] = a14;
        A[15] = a15;
        A[16] = a16;
        A[17] = a17;
        A[18] = a18;
        A[19] = a19;
        A[20] = a20;
        A[21] = a21;
        A[22] = a22;
        A[23] = a23;
        A[24] = a24;
    }
}
