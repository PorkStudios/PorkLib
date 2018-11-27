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
import net.daporkchop.lib.hash.alg.base.BlockDigest;

/**
 * Implementation of the MD5 hash algorithm
 *
 * @author Some BouncyCastle dev
 */
public class MD5 extends BlockDigest {
    private static final int DIGEST_LENGTH = 16;
    //
    // round 1 left rotates
    //
    private static final int S11 = 7;
    private static final int S12 = 12;
    private static final int S13 = 17;
    private static final int S14 = 22;
    //
    // round 2 left rotates
    //
    private static final int S21 = 5;
    private static final int S22 = 9;
    private static final int S23 = 14;
    private static final int S24 = 20;
    //
    // round 3 left rotates
    //
    private static final int S31 = 4;
    private static final int S32 = 11;
    private static final int S33 = 16;
    private static final int S34 = 23;
    //
    // round 4 left rotates
    //
    private static final int S41 = 6;
    private static final int S42 = 10;
    private static final int S43 = 15;
    private static final int S44 = 21;
    private final int[] X = new int[16];
    private int H1;
    private int H2;
    private int H3;
    private int H4;
    private int xOff;

    public MD5() {
        this.reset();
    }

    @Override
    public String getAlgorithmName() {
        return "MD5";
    }

    @Override
    public int getDigestSize() {
        return DIGEST_LENGTH;
    }

    @Override
    protected void processWord(@NonNull byte[] in, int inOff) {
        this.X[this.xOff++] = (in[inOff] & 0xff) | ((in[inOff + 1] & 0xff) << 8)
                | ((in[inOff + 2] & 0xff) << 16) | ((in[inOff + 3] & 0xff) << 24);

        if (this.xOff == 16) {
            this.processBlock();
        }
    }

    @Override
    protected void processLength(long bitLength) {
        if (this.xOff > 14) {
            this.processBlock();
        }

        this.X[14] = (int) (bitLength & 0xFFFFFFFFL);
        this.X[15] = (int) (bitLength >>> 32);
    }

    private void unpackWord(int word, byte[] out, int outOff) {
        out[outOff] = (byte) word;
        out[outOff + 1] = (byte) (word >>> 8);
        out[outOff + 2] = (byte) (word >>> 16);
        out[outOff + 3] = (byte) (word >>> 24);
    }

    @Override
    public int doFinal(@NonNull byte[] out, int outOff) {
        this.finish();

        this.unpackWord(this.H1, out, outOff);
        this.unpackWord(this.H2, out, outOff + 4);
        this.unpackWord(this.H3, out, outOff + 8);
        this.unpackWord(this.H4, out, outOff + 12);

        this.reset();

        return DIGEST_LENGTH;
    }

    @Override
    public void reset() {
        super.reset();

        this.H1 = 0x67452301;
        this.H2 = 0xefcdab89;
        this.H3 = 0x98badcfe;
        this.H4 = 0x10325476;

        this.xOff = 0;

        for (int i = 0; i != this.X.length; i++) {
            this.X[i] = 0;
        }
    }

    /*
     * rotate int x left n bits.
     */
    private int rotateLeft(int x, int n) {
        return (x << n) | (x >>> (32 - n));
    }

    /*
     * F, G, H and I are the basic MD5 functions.
     */
    private int F(int u, int v, int w) {
        return (u & v) | (~u & w);
    }

    private int G(int u, int v, int w) {
        return (u & w) | (v & ~w);
    }

    private int H(int u, int v, int w) {
        return u ^ v ^ w;
    }

    private int K(int u, int v, int w) {
        return v ^ (u | ~w);
    }

    protected void processBlock() {
        int a = this.H1;
        int b = this.H2;
        int c = this.H3;
        int d = this.H4;

        //
        // Round 1 - F cycle, 16 times.
        //
        a = this.rotateLeft(a + this.F(b, c, d) + this.X[0] + 0xd76aa478, S11) + b;
        d = this.rotateLeft(d + this.F(a, b, c) + this.X[1] + 0xe8c7b756, S12) + a;
        c = this.rotateLeft(c + this.F(d, a, b) + this.X[2] + 0x242070db, S13) + d;
        b = this.rotateLeft(b + this.F(c, d, a) + this.X[3] + 0xc1bdceee, S14) + c;
        a = this.rotateLeft(a + this.F(b, c, d) + this.X[4] + 0xf57c0faf, S11) + b;
        d = this.rotateLeft(d + this.F(a, b, c) + this.X[5] + 0x4787c62a, S12) + a;
        c = this.rotateLeft(c + this.F(d, a, b) + this.X[6] + 0xa8304613, S13) + d;
        b = this.rotateLeft(b + this.F(c, d, a) + this.X[7] + 0xfd469501, S14) + c;
        a = this.rotateLeft(a + this.F(b, c, d) + this.X[8] + 0x698098d8, S11) + b;
        d = this.rotateLeft(d + this.F(a, b, c) + this.X[9] + 0x8b44f7af, S12) + a;
        c = this.rotateLeft(c + this.F(d, a, b) + this.X[10] + 0xffff5bb1, S13) + d;
        b = this.rotateLeft(b + this.F(c, d, a) + this.X[11] + 0x895cd7be, S14) + c;
        a = this.rotateLeft(a + this.F(b, c, d) + this.X[12] + 0x6b901122, S11) + b;
        d = this.rotateLeft(d + this.F(a, b, c) + this.X[13] + 0xfd987193, S12) + a;
        c = this.rotateLeft(c + this.F(d, a, b) + this.X[14] + 0xa679438e, S13) + d;
        b = this.rotateLeft(b + this.F(c, d, a) + this.X[15] + 0x49b40821, S14) + c;

        //
        // Round 2 - G cycle, 16 times.
        //
        a = this.rotateLeft(a + this.G(b, c, d) + this.X[1] + 0xf61e2562, S21) + b;
        d = this.rotateLeft(d + this.G(a, b, c) + this.X[6] + 0xc040b340, S22) + a;
        c = this.rotateLeft(c + this.G(d, a, b) + this.X[11] + 0x265e5a51, S23) + d;
        b = this.rotateLeft(b + this.G(c, d, a) + this.X[0] + 0xe9b6c7aa, S24) + c;
        a = this.rotateLeft(a + this.G(b, c, d) + this.X[5] + 0xd62f105d, S21) + b;
        d = this.rotateLeft(d + this.G(a, b, c) + this.X[10] + 0x02441453, S22) + a;
        c = this.rotateLeft(c + this.G(d, a, b) + this.X[15] + 0xd8a1e681, S23) + d;
        b = this.rotateLeft(b + this.G(c, d, a) + this.X[4] + 0xe7d3fbc8, S24) + c;
        a = this.rotateLeft(a + this.G(b, c, d) + this.X[9] + 0x21e1cde6, S21) + b;
        d = this.rotateLeft(d + this.G(a, b, c) + this.X[14] + 0xc33707d6, S22) + a;
        c = this.rotateLeft(c + this.G(d, a, b) + this.X[3] + 0xf4d50d87, S23) + d;
        b = this.rotateLeft(b + this.G(c, d, a) + this.X[8] + 0x455a14ed, S24) + c;
        a = this.rotateLeft(a + this.G(b, c, d) + this.X[13] + 0xa9e3e905, S21) + b;
        d = this.rotateLeft(d + this.G(a, b, c) + this.X[2] + 0xfcefa3f8, S22) + a;
        c = this.rotateLeft(c + this.G(d, a, b) + this.X[7] + 0x676f02d9, S23) + d;
        b = this.rotateLeft(b + this.G(c, d, a) + this.X[12] + 0x8d2a4c8a, S24) + c;

        //
        // Round 3 - H cycle, 16 times.
        //
        a = this.rotateLeft(a + this.H(b, c, d) + this.X[5] + 0xfffa3942, S31) + b;
        d = this.rotateLeft(d + this.H(a, b, c) + this.X[8] + 0x8771f681, S32) + a;
        c = this.rotateLeft(c + this.H(d, a, b) + this.X[11] + 0x6d9d6122, S33) + d;
        b = this.rotateLeft(b + this.H(c, d, a) + this.X[14] + 0xfde5380c, S34) + c;
        a = this.rotateLeft(a + this.H(b, c, d) + this.X[1] + 0xa4beea44, S31) + b;
        d = this.rotateLeft(d + this.H(a, b, c) + this.X[4] + 0x4bdecfa9, S32) + a;
        c = this.rotateLeft(c + this.H(d, a, b) + this.X[7] + 0xf6bb4b60, S33) + d;
        b = this.rotateLeft(b + this.H(c, d, a) + this.X[10] + 0xbebfbc70, S34) + c;
        a = this.rotateLeft(a + this.H(b, c, d) + this.X[13] + 0x289b7ec6, S31) + b;
        d = this.rotateLeft(d + this.H(a, b, c) + this.X[0] + 0xeaa127fa, S32) + a;
        c = this.rotateLeft(c + this.H(d, a, b) + this.X[3] + 0xd4ef3085, S33) + d;
        b = this.rotateLeft(b + this.H(c, d, a) + this.X[6] + 0x04881d05, S34) + c;
        a = this.rotateLeft(a + this.H(b, c, d) + this.X[9] + 0xd9d4d039, S31) + b;
        d = this.rotateLeft(d + this.H(a, b, c) + this.X[12] + 0xe6db99e5, S32) + a;
        c = this.rotateLeft(c + this.H(d, a, b) + this.X[15] + 0x1fa27cf8, S33) + d;
        b = this.rotateLeft(b + this.H(c, d, a) + this.X[2] + 0xc4ac5665, S34) + c;

        //
        // Round 4 - K cycle, 16 times.
        //
        a = this.rotateLeft(a + this.K(b, c, d) + this.X[0] + 0xf4292244, S41) + b;
        d = this.rotateLeft(d + this.K(a, b, c) + this.X[7] + 0x432aff97, S42) + a;
        c = this.rotateLeft(c + this.K(d, a, b) + this.X[14] + 0xab9423a7, S43) + d;
        b = this.rotateLeft(b + this.K(c, d, a) + this.X[5] + 0xfc93a039, S44) + c;
        a = this.rotateLeft(a + this.K(b, c, d) + this.X[12] + 0x655b59c3, S41) + b;
        d = this.rotateLeft(d + this.K(a, b, c) + this.X[3] + 0x8f0ccc92, S42) + a;
        c = this.rotateLeft(c + this.K(d, a, b) + this.X[10] + 0xffeff47d, S43) + d;
        b = this.rotateLeft(b + this.K(c, d, a) + this.X[1] + 0x85845dd1, S44) + c;
        a = this.rotateLeft(a + this.K(b, c, d) + this.X[8] + 0x6fa87e4f, S41) + b;
        d = this.rotateLeft(d + this.K(a, b, c) + this.X[15] + 0xfe2ce6e0, S42) + a;
        c = this.rotateLeft(c + this.K(d, a, b) + this.X[6] + 0xa3014314, S43) + d;
        b = this.rotateLeft(b + this.K(c, d, a) + this.X[13] + 0x4e0811a1, S44) + c;
        a = this.rotateLeft(a + this.K(b, c, d) + this.X[4] + 0xf7537e82, S41) + b;
        d = this.rotateLeft(d + this.K(a, b, c) + this.X[11] + 0xbd3af235, S42) + a;
        c = this.rotateLeft(c + this.K(d, a, b) + this.X[2] + 0x2ad7d2bb, S43) + d;
        b = this.rotateLeft(b + this.K(c, d, a) + this.X[9] + 0xeb86d391, S44) + c;

        this.H1 += a;
        this.H2 += b;
        this.H3 += c;
        this.H4 += d;

        //
        // reset the offset and clean out the word buffer.
        //
        this.xOff = 0;
        for (int i = 0; i != this.X.length; i++) {
            this.X[i] = 0;
        }
    }
}
