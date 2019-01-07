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
 * Implementation of the SHA-256 hash algorithm
 *
 * @author Some BouncyCastle dev
 */
public class SHA256 extends BlockDigest {
    private static final int DIGEST_LENGTH = 32;
    private static final int K[] = {
            0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
            0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
            0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
            0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
            0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
            0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
            0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
            0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
    };

    //i made all these methods static so that they're more likely to be inlined by JIT
    private static int Ch(int x, int y, int z) {
        return (x & y) ^ ((~x) & z);
    }

    private static int Maj(int x, int y, int z) {
        return (x & y) ^ (x & z) ^ (y & z);
    }

    private static int Sum0(int x) {
        return ((x >>> 2) | (x << 30)) ^ ((x >>> 13) | (x << 19)) ^ ((x >>> 22) | (x << 10));
    }

    private static int Sum1(int x) {
        return ((x >>> 6) | (x << 26)) ^ ((x >>> 11) | (x << 21)) ^ ((x >>> 25) | (x << 7));
    }

    private static int Theta0(int x) {
        return ((x >>> 7) | (x << 25)) ^ ((x >>> 18) | (x << 14)) ^ (x >>> 3);
    }

    private static int Theta1(int x) {
        return ((x >>> 17) | (x << 15)) ^ ((x >>> 19) | (x << 13)) ^ (x >>> 10);
    }
    private int H1;
    private int H2;
    private int H3;
    private int H4;
    private int H5;
    private int H6;
    private int H7;
    private int H8;
    private final int[] X = new int[64];
    private int xOff;

    public SHA256() {
        this.reset();
    }

    @Override
    public String getAlgorithmName() {
        return "SHA-256";
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
        this.X[15] = (int) (bitLength & 0xffffffffL);
    }

    @Override
    public int doFinal(@NonNull byte[] out, int outOff) {
        this.finish();

        Pack.intToBigEndian(this.H1, out, outOff);
        Pack.intToBigEndian(this.H2, out, outOff + 4);
        Pack.intToBigEndian(this.H3, out, outOff + 8);
        Pack.intToBigEndian(this.H4, out, outOff + 12);
        Pack.intToBigEndian(this.H5, out, outOff + 16);
        Pack.intToBigEndian(this.H6, out, outOff + 20);
        Pack.intToBigEndian(this.H7, out, outOff + 24);
        Pack.intToBigEndian(this.H8, out, outOff + 28);

        this.reset();

        return DIGEST_LENGTH;
    }

    @Override
    public void reset() {
        super.reset();

        this.H1 = 0x6a09e667;
        this.H2 = 0xbb67ae85;
        this.H3 = 0x3c6ef372;
        this.H4 = 0xa54ff53a;
        this.H5 = 0x510e527f;
        this.H6 = 0x9b05688c;
        this.H7 = 0x1f83d9ab;
        this.H8 = 0x5be0cd19;

        this.xOff = 0;
        for (int i = 0; i != this.X.length; i++) {
            this.X[i] = 0;
        }
    }

    @Override
    protected void processBlock() {
        // expand 16 word block into 64 word blocks.
        for (int t = 16; t <= 63; t++) {
            this.X[t] = Theta1(this.X[t - 2]) + this.X[t - 7] + Theta0(this.X[t - 15]) + this.X[t - 16];
        }

        // set up working variables.
        int a = this.H1;
        int b = this.H2;
        int c = this.H3;
        int d = this.H4;
        int e = this.H5;
        int f = this.H6;
        int g = this.H7;
        int h = this.H8;

        int t = 0;
        for (int i = 0; i < 8; i++) {
            // t = 8 * i
            h += Sum1(e) + Ch(e, f, g) + K[t] + this.X[t];
            d += h;
            h += Sum0(a) + Maj(a, b, c);
            ++t;

            // t = 8 * i + 1
            g += Sum1(d) + Ch(d, e, f) + K[t] + this.X[t];
            c += g;
            g += Sum0(h) + Maj(h, a, b);
            ++t;

            // t = 8 * i + 2
            f += Sum1(c) + Ch(c, d, e) + K[t] + this.X[t];
            b += f;
            f += Sum0(g) + Maj(g, h, a);
            ++t;

            // t = 8 * i + 3
            e += Sum1(b) + Ch(b, c, d) + K[t] + this.X[t];
            a += e;
            e += Sum0(f) + Maj(f, g, h);
            ++t;

            // t = 8 * i + 4
            d += Sum1(a) + Ch(a, b, c) + K[t] + this.X[t];
            h += d;
            d += Sum0(e) + Maj(e, f, g);
            ++t;

            // t = 8 * i + 5
            c += Sum1(h) + Ch(h, a, b) + K[t] + this.X[t];
            g += c;
            c += Sum0(d) + Maj(d, e, f);
            ++t;

            // t = 8 * i + 6
            b += Sum1(g) + Ch(g, h, a) + K[t] + this.X[t];
            f += b;
            b += Sum0(c) + Maj(c, d, e);
            ++t;

            // t = 8 * i + 7
            a += Sum1(f) + Ch(f, g, h) + K[t] + this.X[t];
            e += a;
            a += Sum0(b) + Maj(b, c, d);
            ++t;
        }

        this.H1 += a;
        this.H2 += b;
        this.H3 += c;
        this.H4 += d;
        this.H5 += e;
        this.H6 += f;
        this.H7 += g;
        this.H8 += h;

        // reset the offset and clean out the word buffer.
        this.xOff = 0;
        for (int i = 0; i < 16; i++) {
            this.X[i] = 0;
        }
    }
}
