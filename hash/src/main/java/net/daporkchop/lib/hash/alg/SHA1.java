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

package net.daporkchop.lib.hash.alg;

import lombok.NonNull;
import net.daporkchop.lib.binary.util.Pack;
import net.daporkchop.lib.hash.alg.base.BlockDigest;

/**
 * Implementation of the SHA-1 hash algorithm
 *
 * @author Some BouncyCastle dev
 */
public class SHA1 extends BlockDigest {
    private static final int DIGEST_LENGTH = 20;
    // Additive constants
    private static final int Y1 = 0x5a827999;
    private static final int Y2 = 0x6ed9eba1;
    private static final int Y3 = 0x8f1bbcdc;
    private static final int Y4 = 0xca62c1d6;

    private static int f(int u, int v, int w) {
        return (u & v) | ((~u) & w);
    }

    private static int h(int u, int v, int w) {
        return u ^ v ^ w;
    }

    private static int g(int u, int v, int w) {
        return (u & v) | (u & w) | (v & w);
    }
    private int H1;
    private int H2;
    private int H3;
    private int H4;
    private int H5;
    private final int[] X = new int[80];
    private int xOff;

    public SHA1() {
        this.reset();
    }

    @Override
    public String getAlgorithmName() {
        return "SHA-1";
    }

    @Override
    public int getHashSize() {
        return DIGEST_LENGTH;
    }

    @Override
    protected void processWord(@NonNull byte[] in, int inOff) {
        // Note: Inlined for performance
//        X[xOff] = Pack.bigEndianToInt(in, inOff);
        int n = in[inOff] << 24;
        n |= (in[++inOff] & 0xff) << 16;
        n |= (in[++inOff] & 0xff) << 8;
        n |= (in[++inOff] & 0xff);
        this.X[this.xOff] = n;

        if (++this.xOff == 16) {
            this.processBlock();
        }
    }

    @Override
    protected void processLength(long bitLength) {
        if (this.xOff > 14) {
            this.processBlock();
        }

        this.X[14] = (int) (bitLength >>> 32);
        this.X[15] = (int) bitLength;
    }

    @Override
    public int doFinal(@NonNull byte[] out, int outOff) {
        this.finish();

        Pack.intToBigEndian(this.H1, out, outOff);
        Pack.intToBigEndian(this.H2, out, outOff + 4);
        Pack.intToBigEndian(this.H3, out, outOff + 8);
        Pack.intToBigEndian(this.H4, out, outOff + 12);
        Pack.intToBigEndian(this.H5, out, outOff + 16);

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
        this.H5 = 0xc3d2e1f0;

        this.xOff = 0;
        for (int i = 0; i != this.X.length; i++) {
            this.X[i] = 0;
        }
    }

    @Override
    protected void processBlock() {
        // expand 16 word block into 80 word block.
        for (int i = 16; i < 80; i++) {
            int t = this.X[i - 3] ^ this.X[i - 8] ^ this.X[i - 14] ^ this.X[i - 16];
            this.X[i] = t << 1 | t >>> 31;
        }

        // set up working variables.
        int A = this.H1;
        int B = this.H2;
        int C = this.H3;
        int D = this.H4;
        int E = this.H5;

        // round 1
        int idx = 0;

        for (int j = 0; j < 4; j++) {
            E += (A << 5 | A >>> 27) + f(B, C, D) + this.X[idx++] + Y1;
            B = B << 30 | B >>> 2;

            D += (E << 5 | E >>> 27) + f(A, B, C) + this.X[idx++] + Y1;
            A = A << 30 | A >>> 2;

            C += (D << 5 | D >>> 27) + f(E, A, B) + this.X[idx++] + Y1;
            E = E << 30 | E >>> 2;

            B += (C << 5 | C >>> 27) + f(D, E, A) + this.X[idx++] + Y1;
            D = D << 30 | D >>> 2;

            A += (B << 5 | B >>> 27) + f(C, D, E) + this.X[idx++] + Y1;
            C = C << 30 | C >>> 2;
        }

        // round 2
        for (int j = 0; j < 4; j++) {
            E += (A << 5 | A >>> 27) + h(B, C, D) + this.X[idx++] + Y2;
            B = B << 30 | B >>> 2;

            D += (E << 5 | E >>> 27) + h(A, B, C) + this.X[idx++] + Y2;
            A = A << 30 | A >>> 2;

            C += (D << 5 | D >>> 27) + h(E, A, B) + this.X[idx++] + Y2;
            E = E << 30 | E >>> 2;

            B += (C << 5 | C >>> 27) + h(D, E, A) + this.X[idx++] + Y2;
            D = D << 30 | D >>> 2;

            A += (B << 5 | B >>> 27) + h(C, D, E) + this.X[idx++] + Y2;
            C = C << 30 | C >>> 2;
        }

        // round 3
        for (int j = 0; j < 4; j++) {
            E += (A << 5 | A >>> 27) + g(B, C, D) + this.X[idx++] + Y3;
            B = B << 30 | B >>> 2;

            D += (E << 5 | E >>> 27) + g(A, B, C) + this.X[idx++] + Y3;
            A = A << 30 | A >>> 2;

            C += (D << 5 | D >>> 27) + g(E, A, B) + this.X[idx++] + Y3;
            E = E << 30 | E >>> 2;

            B += (C << 5 | C >>> 27) + g(D, E, A) + this.X[idx++] + Y3;
            D = D << 30 | D >>> 2;

            A += (B << 5 | B >>> 27) + g(C, D, E) + this.X[idx++] + Y3;
            C = C << 30 | C >>> 2;
        }

        // round 4
        for (int j = 0; j <= 3; j++) {
            E += (A << 5 | A >>> 27) + h(B, C, D) + this.X[idx++] + Y4;
            B = B << 30 | B >>> 2;

            D += (E << 5 | E >>> 27) + h(A, B, C) + this.X[idx++] + Y4;
            A = A << 30 | A >>> 2;

            C += (D << 5 | D >>> 27) + h(E, A, B) + this.X[idx++] + Y4;
            E = E << 30 | E >>> 2;

            B += (C << 5 | C >>> 27) + h(D, E, A) + this.X[idx++] + Y4;
            D = D << 30 | D >>> 2;

            A += (B << 5 | B >>> 27) + h(C, D, E) + this.X[idx++] + Y4;
            C = C << 30 | C >>> 2;
        }


        this.H1 += A;
        this.H2 += B;
        this.H3 += C;
        this.H4 += D;
        this.H5 += E;

        // reset start of the buffer.
        this.xOff = 0;
        for (int i = 0; i < 16; i++) {
            this.X[i] = 0;
        }
    }
}
