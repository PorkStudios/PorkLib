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
 *
 */

package net.daporkchop.lib.hash.impl.sha;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import net.daporkchop.lib.hash.util.Digest;

public class Sha512Impl extends Digest {
    private static final long[] k = {
            0x428a2f98d728ae22L, 0x7137449123ef65cdL, 0xb5c0fbcfec4d3b2fL, 0xe9b5dba58189dbbcL,
            0x3956c25bf348b538L, 0x59f111f1b605d019L, 0x923f82a4af194f9bL, 0xab1c5ed5da6d8118L,
            0xd807aa98a3030242L, 0x12835b0145706fbeL, 0x243185be4ee4b28cL, 0x550c7dc3d5ffb4e2L,
            0x72be5d74f27b896fL, 0x80deb1fe3b1696b1L, 0x9bdc06a725c71235L, 0xc19bf174cf692694L,
            0xe49b69c19ef14ad2L, 0xefbe4786384f25e3L, 0x0fc19dc68b8cd5b5L, 0x240ca1cc77ac9c65L,
            0x2de92c6f592b0275L, 0x4a7484aa6ea6e483L, 0x5cb0a9dcbd41fbd4L, 0x76f988da831153b5L,
            0x983e5152ee66dfabL, 0xa831c66d2db43210L, 0xb00327c898fb213fL, 0xbf597fc7beef0ee4L,
            0xc6e00bf33da88fc2L, 0xd5a79147930aa725L, 0x06ca6351e003826fL, 0x142929670a0e6e70L,
            0x27b70a8546d22ffcL, 0x2e1b21385c26c926L, 0x4d2c6dfc5ac42aedL, 0x53380d139d95b3dfL,
            0x650a73548baf63deL, 0x766a0abb3c77b2a8L, 0x81c2c92e47edaee6L, 0x92722c851482353bL,
            0xa2bfe8a14cf10364L, 0xa81a664bbc423001L, 0xc24b8b70d0f89791L, 0xc76c51a30654be30L,
            0xd192e819d6ef5218L, 0xd69906245565a910L, 0xf40e35855771202aL, 0x106aa07032bbd1b8L,
            0x19a4c116b8d2d0c8L, 0x1e376c085141ab53L, 0x2748774cdf8eeb99L, 0x34b0bcb5e19b48a8L,
            0x391c0cb3c5c95a63L, 0x4ed8aa4ae3418acbL, 0x5b9cca4f7763e373L, 0x682e6ff3d6b2b8a3L,
            0x748f82ee5defb2fcL, 0x78a5636f43172f60L, 0x84c87814a1f0ab72L, 0x8cc702081a6439ecL,
            0x90befffa23631e28L, 0xa4506cebde82bde9L, 0xbef9a3f7b2c67915L, 0xc67178f2e372532bL,
            0xca273eceea26619cL, 0xd186b8c721c0c207L, 0xeada7dd6cde0eb1eL, 0xf57d4f7fee6ed178L,
            0x06f067aa72176fbaL, 0x0a637dc5a2c898a6L, 0x113f9804bef90daeL, 0x1b710b35131c471bL,
            0x28db77f523047d84L, 0x32caab7b40c72493L, 0x3c9ebe0a15c9bebcL, 0x431d67c49c100d4cL,
            0x4cc5d4becb3e42b6L, 0x597f299cfc657e2aL, 0x5fcb6fab3ad6faecL, 0x6c44198c4a475817L
    };

    private static final int BLOCK_SIZE = 128;

    private static final String DIGEST0 =
            "DDAF35A193617ABACC417349AE20413112E6FA4E89A97EA20A9EEEE64B55D39A" +
                    "2192992A274FC1A836BA3C23A3FEEBBD454D4423643CE80E2A9AC94FA54CA49F";

    private static final long[] w = new long[80];

    private static Boolean valid;

    private long h0, h1, h2, h3, h4, h5, h6, h7;

    public Sha512Impl() {
        super("sha-512", 64, BLOCK_SIZE);
    }

    private Sha512Impl(Sha512Impl md) {
        this();

        this.h0 = md.h0;
        this.h1 = md.h1;
        this.h2 = md.h2;
        this.h3 = md.h3;
        this.h4 = md.h4;
        this.h5 = md.h5;
        this.h6 = md.h6;
        this.h7 = md.h7;
        this.count = md.count;
        this.buffer = md.buffer.clone();
    }

    public static final long[] G(long hh0, long hh1, long hh2, long hh3, long hh4,
                                 long hh5, long hh6, long hh7, byte[] in, int offset) {
        return sha(hh0, hh1, hh2, hh3, hh4, hh5, hh6, hh7, in, offset);
    }

    private static final synchronized long[]
    sha(long hh0, long hh1, long hh2, long hh3, long hh4, long hh5, long hh6, long hh7, byte[] in, int offset) {
        long A = hh0;
        long B = hh1;
        long C = hh2;
        long D = hh3;
        long E = hh4;
        long F = hh5;
        long G = hh6;
        long H = hh7;
        long T, T2;
        int r;

        for (r = 0; r < 16; r++) {
            w[r] = (long) in[offset++] << 56 |
                    ((long) in[offset++] & 0xFF) << 48 |
                    ((long) in[offset++] & 0xFF) << 40 |
                    ((long) in[offset++] & 0xFF) << 32 |
                    ((long) in[offset++] & 0xFF) << 24 |
                    ((long) in[offset++] & 0xFF) << 16 |
                    ((long) in[offset++] & 0xFF) << 8 |
                    ((long) in[offset++] & 0xFF);
        }
        for (r = 16; r < 80; r++) {
            T = w[r - 2];
            T2 = w[r - 15];
            w[r] = (((T >>> 19) | (T << 45)) ^ ((T >>> 61) | (T << 3)) ^ (T >>> 6)) + w[r - 7] + (((T2 >>> 1) | (T2 << 63)) ^ ((T2 >>> 8) | (T2 << 56)) ^ (T2 >>> 7)) + w[r - 16];
        }

        for (r = 0; r < 80; r++) {
            T = H + (((E >>> 14) | (E << 50)) ^ ((E >>> 18) | (E << 46)) ^ ((E >>> 41) | (E << 23))) + ((E & F) ^ ((~E) & G)) + k[r] + w[r];
            T2 = (((A >>> 28) | (A << 36)) ^ ((A >>> 34) | (A << 30)) ^ ((A >>> 39) | (A << 25))) + ((A & B) ^ (A & C) ^ (B & C));
            H = G;
            G = F;
            F = E;
            E = D + T;
            D = C;
            C = B;
            B = A;
            A = T + T2;
        }

        return new long[]{
                hh0 + A, hh1 + B, hh2 + C, hh3 + D, hh4 + E, hh5 + F, hh6 + G, hh7 + H
        };
    }

    public Object clone() {
        return new Sha512Impl(this);
    }

    protected void transform(byte[] in, int offset) {
        long[] result = sha(this.h0, this.h1, this.h2, this.h3, this.h4, this.h5, this.h6, this.h7, in, offset);

        this.h0 = result[0];
        this.h1 = result[1];
        this.h2 = result[2];
        this.h3 = result[3];
        this.h4 = result[4];
        this.h5 = result[5];
        this.h6 = result[6];
        this.h7 = result[7];
    }

    protected byte[] padBuffer() {
        return SHAUtil.padBuffer2(this.count, BLOCK_SIZE);
    }

    protected byte[] getResult() {
        return new byte[]{
                (byte) (this.h0 >>> 56), (byte) (this.h0 >>> 48), (byte) (this.h0 >>> 40), (byte) (this.h0 >>> 32),
                (byte) (this.h0 >>> 24), (byte) (this.h0 >>> 16), (byte) (this.h0 >>> 8), (byte) this.h0,
                (byte) (this.h1 >>> 56), (byte) (this.h1 >>> 48), (byte) (this.h1 >>> 40), (byte) (this.h1 >>> 32),
                (byte) (this.h1 >>> 24), (byte) (this.h1 >>> 16), (byte) (this.h1 >>> 8), (byte) this.h1,
                (byte) (this.h2 >>> 56), (byte) (this.h2 >>> 48), (byte) (this.h2 >>> 40), (byte) (this.h2 >>> 32),
                (byte) (this.h2 >>> 24), (byte) (this.h2 >>> 16), (byte) (this.h2 >>> 8), (byte) this.h2,
                (byte) (this.h3 >>> 56), (byte) (this.h3 >>> 48), (byte) (this.h3 >>> 40), (byte) (this.h3 >>> 32),
                (byte) (this.h3 >>> 24), (byte) (this.h3 >>> 16), (byte) (this.h3 >>> 8), (byte) this.h3,
                (byte) (this.h4 >>> 56), (byte) (this.h4 >>> 48), (byte) (this.h4 >>> 40), (byte) (this.h4 >>> 32),
                (byte) (this.h4 >>> 24), (byte) (this.h4 >>> 16), (byte) (this.h4 >>> 8), (byte) this.h4,
                (byte) (this.h5 >>> 56), (byte) (this.h5 >>> 48), (byte) (this.h5 >>> 40), (byte) (this.h5 >>> 32),
                (byte) (this.h5 >>> 24), (byte) (this.h5 >>> 16), (byte) (this.h5 >>> 8), (byte) this.h5,
                (byte) (this.h6 >>> 56), (byte) (this.h6 >>> 48), (byte) (this.h6 >>> 40), (byte) (this.h6 >>> 32),
                (byte) (this.h6 >>> 24), (byte) (this.h6 >>> 16), (byte) (this.h6 >>> 8), (byte) this.h6,
                (byte) (this.h7 >>> 56), (byte) (this.h7 >>> 48), (byte) (this.h7 >>> 40), (byte) (this.h7 >>> 32),
                (byte) (this.h7 >>> 24), (byte) (this.h7 >>> 16), (byte) (this.h7 >>> 8), (byte) this.h7
        };
    }

    protected void resetContext() {
        // magic SHA-512 initialisation constants
        this.h0 = 0x6a09e667f3bcc908L;
        this.h1 = 0xbb67ae8584caa73bL;
        this.h2 = 0x3c6ef372fe94f82bL;
        this.h3 = 0xa54ff53a5f1d36f1L;
        this.h4 = 0x510e527fade682d1L;
        this.h5 = 0x9b05688c2b3e6c1fL;
        this.h6 = 0x1f83d9abfb41bd6bL;
        this.h7 = 0x5be0cd19137e2179L;
    }

    // SHA specific methods ----------------------------------------------------

    public boolean selfTest() {
        if (valid == null) {
            Sha512Impl md = new Sha512Impl();
            md.update((byte) 0x61); // a
            md.update((byte) 0x62); // b
            md.update((byte) 0x63); // c
            String result = HexBin.encode(md.digest());
            valid = DIGEST0.equals(result);
        }
        return valid;
    }
}
