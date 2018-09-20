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

package net.daporkchop.lib.hash.impl.skid.ripemd;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import net.daporkchop.lib.hash.impl.skid.BaseHash;
import net.daporkchop.lib.hash.impl.skid.md.MDUtil;

public class RipeMD128Impl extends BaseHash {
    private static final int BLOCK_SIZE = 64;

    private static final String DIGEST0 = "CDF26213A150DC3ECB610F18F6B38B46";

    private static final int[] R = {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
            7, 4, 13, 1, 10, 6, 15, 3, 12, 0, 9, 5, 2, 14, 11, 8,
            3, 10, 14, 4, 9, 15, 8, 1, 2, 7, 0, 6, 13, 11, 5, 12,
            1, 9, 11, 10, 0, 8, 12, 4, 13, 3, 7, 15, 14, 5, 6, 2
    };

    private static final int[] Rp = {
            5, 14, 7, 0, 9, 2, 11, 4, 13, 6, 15, 8, 1, 10, 3, 12,
            6, 11, 3, 7, 0, 13, 5, 10, 14, 15, 8, 12, 4, 9, 1, 2,
            15, 5, 1, 3, 7, 14, 6, 9, 11, 8, 12, 2, 10, 0, 4, 13,
            8, 6, 4, 1, 3, 11, 15, 0, 5, 12, 2, 13, 9, 7, 10, 14
    };

    private static final int[] S = {
            11, 14, 15, 12, 5, 8, 7, 9, 11, 13, 14, 15, 6, 7, 9, 8,
            7, 6, 8, 13, 11, 9, 7, 15, 7, 12, 15, 9, 11, 7, 13, 12,
            11, 13, 6, 7, 14, 9, 13, 15, 14, 8, 13, 6, 5, 12, 7, 5,
            11, 12, 14, 15, 14, 15, 9, 8, 9, 14, 5, 6, 8, 6, 5, 12
    };

    private static final int[] Sp = {
            8, 9, 9, 11, 13, 15, 15, 5, 7, 7, 8, 11, 14, 14, 12, 6,
            9, 13, 15, 7, 12, 8, 9, 11, 7, 7, 12, 7, 6, 15, 13, 11,
            9, 7, 15, 11, 8, 6, 6, 14, 12, 13, 5, 14, 13, 13, 7, 5,
            15, 5, 8, 11, 14, 14, 6, 14, 6, 9, 12, 9, 12, 5, 15, 8
    };

    private static Boolean valid;

    private int h0, h1, h2, h3;

    private int[] X = new int[16];

    public RipeMD128Impl() {
        super("ripemd128", 16, BLOCK_SIZE);
    }

    private RipeMD128Impl(RipeMD128Impl md) {
        this();

        this.h0 = md.h0;
        this.h1 = md.h1;
        this.h2 = md.h2;
        this.h3 = md.h3;
        this.count = md.count;
        this.buffer = md.buffer.clone();
    }

    public Object clone() {
        return new RipeMD128Impl(this);
    }

    // Implementation of concrete methods in BaseHash --------------------------

    protected void transform(byte[] in, int offset) {
        int A, B, C, D, Ap, Bp, Cp, Dp, T, s, i;

        // encode 64 bytes from input block into an array of 16 unsigned
        // integers.
        for (i = 0; i < 16; i++) {
            this.X[i] = (in[offset++] & 0xFF) |
                    (in[offset++] & 0xFF) << 8 |
                    (in[offset++] & 0xFF) << 16 |
                    in[offset++] << 24;
        }

        A = Ap = this.h0;
        B = Bp = this.h1;
        C = Cp = this.h2;
        D = Dp = this.h3;

        for (i = 0; i < 16; i++) { // rounds 0...15
            s = S[i];
            T = A + (B ^ C ^ D) + this.X[i];
            A = D;
            D = C;
            C = B;
            B = T << s | T >>> (32 - s);

            s = Sp[i];
            T = Ap + ((Bp & Dp) | (Cp & ~Dp)) + this.X[Rp[i]] + 0x50A28BE6;
            Ap = Dp;
            Dp = Cp;
            Cp = Bp;
            Bp = T << s | T >>> (32 - s);
        }

        for (; i < 32; i++) { // rounds 16...31
            s = S[i];
            T = A + ((B & C) | (~B & D)) + this.X[R[i]] + 0x5A827999;
            A = D;
            D = C;
            C = B;
            B = T << s | T >>> (32 - s);

            s = Sp[i];
            T = Ap + ((Bp | ~Cp) ^ Dp) + this.X[Rp[i]] + 0x5C4DD124;
            Ap = Dp;
            Dp = Cp;
            Cp = Bp;
            Bp = T << s | T >>> (32 - s);
        }

        for (; i < 48; i++) { // rounds 32...47
            s = S[i];
            T = A + ((B | ~C) ^ D) + this.X[R[i]] + 0x6ED9EBA1;
            A = D;
            D = C;
            C = B;
            B = T << s | T >>> (32 - s);

            s = Sp[i];
            T = Ap + ((Bp & Cp) | (~Bp & Dp)) + this.X[Rp[i]] + 0x6D703EF3;
            Ap = Dp;
            Dp = Cp;
            Cp = Bp;
            Bp = T << s | T >>> (32 - s);
        }

        for (; i < 64; i++) { // rounds 48...63
            s = S[i];
            T = A + ((B & D) | (C & ~D)) + this.X[R[i]] + 0x8F1BBCDC;
            A = D;
            D = C;
            C = B;
            B = T << s | T >>> (32 - s);

            s = Sp[i];
            T = Ap + (Bp ^ Cp ^ Dp) + this.X[Rp[i]];
            Ap = Dp;
            Dp = Cp;
            Cp = Bp;
            Bp = T << s | T >>> (32 - s);
        }

        T = this.h1 + C + Dp;
        this.h1 = this.h2 + D + Ap;
        this.h2 = this.h3 + A + Bp;
        this.h3 = this.h0 + B + Cp;
        this.h0 = T;
    }

    protected byte[] padBuffer() {
        return MDUtil.padBuffer(this.count, BLOCK_SIZE);
    }

    protected byte[] getResult() {

        return new byte[]{
                (byte) this.h0, (byte) (this.h0 >>> 8), (byte) (this.h0 >>> 16), (byte) (this.h0 >>> 24),
                (byte) this.h1, (byte) (this.h1 >>> 8), (byte) (this.h1 >>> 16), (byte) (this.h1 >>> 24),
                (byte) this.h2, (byte) (this.h2 >>> 8), (byte) (this.h2 >>> 16), (byte) (this.h2 >>> 24),
                (byte) this.h3, (byte) (this.h3 >>> 8), (byte) (this.h3 >>> 16), (byte) (this.h3 >>> 24)
        };
    }

    protected void resetContext() {
        // magic RIPEMD128 initialisation constants
        this.h0 = 0x67452301;
        this.h1 = 0xEFCDAB89;
        this.h2 = 0x98BADCFE;
        this.h3 = 0x10325476;
    }

    public boolean selfTest() {
        if (valid == null) {
            valid = DIGEST0.equals(HexBin.encode(new RipeMD128Impl().digest()));
        }
        return valid;
    }
}
