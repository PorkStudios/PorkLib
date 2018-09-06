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

public class RipeMD160Impl extends BaseHash {
    private static final int BLOCK_SIZE = 64;

    private static final String DIGEST0 = "9C1185A5C5E9FC54612808977EE8F548B2258D31";

    private static final int[] R = {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
            7, 4, 13, 1, 10, 6, 15, 3, 12, 0, 9, 5, 2, 14, 11, 8,
            3, 10, 14, 4, 9, 15, 8, 1, 2, 7, 0, 6, 13, 11, 5, 12,
            1, 9, 11, 10, 0, 8, 12, 4, 13, 3, 7, 15, 14, 5, 6, 2,
            4, 0, 5, 9, 7, 12, 2, 10, 14, 1, 3, 8, 11, 6, 15, 13
    };

    private static final int[] Rp = {
            5, 14, 7, 0, 9, 2, 11, 4, 13, 6, 15, 8, 1, 10, 3, 12,
            6, 11, 3, 7, 0, 13, 5, 10, 14, 15, 8, 12, 4, 9, 1, 2,
            15, 5, 1, 3, 7, 14, 6, 9, 11, 8, 12, 2, 10, 0, 4, 13,
            8, 6, 4, 1, 3, 11, 15, 0, 5, 12, 2, 13, 9, 7, 10, 14,
            12, 15, 10, 4, 1, 5, 8, 7, 6, 2, 13, 14, 0, 3, 9, 11
    };

    private static final int[] S = {
            11, 14, 15, 12, 5, 8, 7, 9, 11, 13, 14, 15, 6, 7, 9, 8,
            7, 6, 8, 13, 11, 9, 7, 15, 7, 12, 15, 9, 11, 7, 13, 12,
            11, 13, 6, 7, 14, 9, 13, 15, 14, 8, 13, 6, 5, 12, 7, 5,
            11, 12, 14, 15, 14, 15, 9, 8, 9, 14, 5, 6, 8, 6, 5, 12,
            9, 15, 5, 11, 6, 8, 13, 12, 5, 12, 13, 14, 11, 8, 5, 6
    };

    private static final int[] Sp = {
            8, 9, 9, 11, 13, 15, 15, 5, 7, 7, 8, 11, 14, 14, 12, 6,
            9, 13, 15, 7, 12, 8, 9, 11, 7, 7, 12, 7, 6, 15, 13, 11,
            9, 7, 15, 11, 8, 6, 6, 14, 12, 13, 5, 14, 13, 13, 7, 5,
            15, 5, 8, 11, 14, 14, 6, 14, 6, 9, 12, 9, 12, 5, 15, 8,
            8, 5, 12, 9, 12, 5, 14, 6, 8, 13, 6, 5, 15, 13, 11, 11
    };

    private static Boolean valid;

    private int h0, h1, h2, h3, h4;

    private int[] X = new int[16];

    public RipeMD160Impl() {
        super("ripemd160", 20, BLOCK_SIZE);
    }

    private RipeMD160Impl(RipeMD160Impl md) {
        this();

        this.h0 = md.h0;
        this.h1 = md.h1;
        this.h2 = md.h2;
        this.h3 = md.h3;
        this.h4 = md.h4;
        this.count = md.count;
        this.buffer = (byte[]) md.buffer.clone();
    }

    public Object clone() {
        return (new RipeMD160Impl(this));
    }

    // Implementation of concrete methods in BaseHash --------------------------

    protected void transform(byte[] in, int offset) {
        int A, B, C, D, E, Ap, Bp, Cp, Dp, Ep, T, s, i;

        // encode 64 bytes from input block into an array of 16 unsigned integers
        for (i = 0; i < 16; i++) {
            X[i] = (in[offset++] & 0xFF) |
                    (in[offset++] & 0xFF) << 8 |
                    (in[offset++] & 0xFF) << 16 |
                    in[offset++] << 24;
        }

        A = Ap = h0;
        B = Bp = h1;
        C = Cp = h2;
        D = Dp = h3;
        E = Ep = h4;

        for (i = 0; i < 16; i++) { // rounds 0...15
            s = S[i];
            T = A + (B ^ C ^ D) + X[i];
            A = E;
            E = D;
            D = C << 10 | C >>> 22;
            C = B;
            B = (T << s | T >>> (32 - s)) + A;

            s = Sp[i];
            T = Ap + (Bp ^ (Cp | ~Dp)) + X[Rp[i]] + 0x50A28BE6;
            Ap = Ep;
            Ep = Dp;
            Dp = Cp << 10 | Cp >>> 22;
            Cp = Bp;
            Bp = (T << s | T >>> (32 - s)) + Ap;
        }

        for (; i < 32; i++) { // rounds 16...31
            s = S[i];
            T = A + ((B & C) | (~B & D)) + X[R[i]] + 0x5A827999;
            A = E;
            E = D;
            D = C << 10 | C >>> 22;
            C = B;
            B = (T << s | T >>> (32 - s)) + A;

            s = Sp[i];
            T = Ap + ((Bp & Dp) | (Cp & ~Dp)) + X[Rp[i]] + 0x5C4DD124;
            Ap = Ep;
            Ep = Dp;
            Dp = Cp << 10 | Cp >>> 22;
            Cp = Bp;
            Bp = (T << s | T >>> (32 - s)) + Ap;
        }

        for (; i < 48; i++) { // rounds 32...47
            s = S[i];
            T = A + ((B | ~C) ^ D) + X[R[i]] + 0x6ED9EBA1;
            A = E;
            E = D;
            D = C << 10 | C >>> 22;
            C = B;
            B = (T << s | T >>> (32 - s)) + A;

            s = Sp[i];
            T = Ap + ((Bp | ~Cp) ^ Dp) + X[Rp[i]] + 0x6D703EF3;
            Ap = Ep;
            Ep = Dp;
            Dp = Cp << 10 | Cp >>> 22;
            Cp = Bp;
            Bp = (T << s | T >>> (32 - s)) + Ap;
        }

        for (; i < 64; i++) { // rounds 48...63
            s = S[i];
            T = A + ((B & D) | (C & ~D)) + X[R[i]] + 0x8F1BBCDC;
            A = E;
            E = D;
            D = C << 10 | C >>> 22;
            C = B;
            B = (T << s | T >>> (32 - s)) + A;

            s = Sp[i];
            T = Ap + ((Bp & Cp) | (~Bp & Dp)) + X[Rp[i]] + 0x7A6D76E9;
            Ap = Ep;
            Ep = Dp;
            Dp = Cp << 10 | Cp >>> 22;
            Cp = Bp;
            Bp = (T << s | T >>> (32 - s)) + Ap;
        }

        for (; i < 80; i++) { // rounds 64...79
            s = S[i];
            T = A + (B ^ (C | ~D)) + X[R[i]] + 0xA953FD4E;
            A = E;
            E = D;
            D = C << 10 | C >>> 22;
            C = B;
            B = (T << s | T >>> (32 - s)) + A;

            s = Sp[i];
            T = Ap + (Bp ^ Cp ^ Dp) + X[Rp[i]];
            Ap = Ep;
            Ep = Dp;
            Dp = Cp << 10 | Cp >>> 22;
            Cp = Bp;
            Bp = (T << s | T >>> (32 - s)) + Ap;
        }

        T = h1 + C + Dp;
        h1 = h2 + D + Ep;
        h2 = h3 + E + Ap;
        h3 = h4 + A + Bp;
        h4 = h0 + B + Cp;
        h0 = T;
    }

    protected byte[] padBuffer() {
        return MDUtil.padBuffer(count, BLOCK_SIZE);
    }

    protected byte[] getResult() {
        byte[] result = new byte[]{
                (byte) h0, (byte) (h0 >>> 8), (byte) (h0 >>> 16), (byte) (h0 >>> 24),
                (byte) h1, (byte) (h1 >>> 8), (byte) (h1 >>> 16), (byte) (h1 >>> 24),
                (byte) h2, (byte) (h2 >>> 8), (byte) (h2 >>> 16), (byte) (h2 >>> 24),
                (byte) h3, (byte) (h3 >>> 8), (byte) (h3 >>> 16), (byte) (h3 >>> 24),
                (byte) h4, (byte) (h4 >>> 8), (byte) (h4 >>> 16), (byte) (h4 >>> 24)
        };

        return result;
    }

    protected void resetContext() {
        // magic RIPEMD160 initialisation constants
        h0 = 0x67452301;
        h1 = 0xEFCDAB89;
        h2 = 0x98BADCFE;
        h3 = 0x10325476;
        h4 = 0xC3D2E1F0;
    }

    public boolean selfTest() {
        if (valid == null) {
            valid = new Boolean(
                    DIGEST0.equals(HexBin.encode(new RipeMD160Impl().digest())));
        }
        return valid.booleanValue();
    }
}
