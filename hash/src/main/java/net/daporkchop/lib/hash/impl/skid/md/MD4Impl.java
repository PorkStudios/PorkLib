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

package net.daporkchop.lib.hash.impl.skid.md;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import net.daporkchop.lib.hash.impl.skid.BaseHash;

public class MD4Impl extends BaseHash {
    private static final int DIGEST_LENGTH = 16;

    private static final int BLOCK_LENGTH = 64;

    private static final int A = 0x67452301;
    private static final int B = 0xefcdab89;
    private static final int C = 0x98badcfe;
    private static final int D = 0x10325476;

    private static final String DIGEST0 = "31D6CFE0D16AE931B73C59D7E0C089C0";

    private static Boolean valid;

    private int a, b, c, d;

    public MD4Impl() {
        super("md4", DIGEST_LENGTH, BLOCK_LENGTH);
    }

    private MD4Impl(MD4Impl that) {
        this();

        this.a = that.a;
        this.b = that.b;
        this.c = that.c;
        this.d = that.d;
        this.count = that.count;
        this.buffer = (byte[]) that.buffer.clone();
    }

    public Object clone() {
        return new MD4Impl(this);
    }

    protected byte[] getResult() {
        byte[] digest = {
                (byte) a, (byte) (a >>> 8), (byte) (a >>> 16), (byte) (a >>> 24),
                (byte) b, (byte) (b >>> 8), (byte) (b >>> 16), (byte) (b >>> 24),
                (byte) c, (byte) (c >>> 8), (byte) (c >>> 16), (byte) (c >>> 24),
                (byte) d, (byte) (d >>> 8), (byte) (d >>> 16), (byte) (d >>> 24)
        };
        return digest;
    }

    protected void resetContext() {
        a = A;
        b = B;
        c = C;
        d = D;
    }

    public boolean selfTest() {
        if (valid == null) {
            valid = new Boolean(
                    DIGEST0.equals(HexBin.encode(new MD4Impl().digest())));
        }
        return valid.booleanValue();
    }

    protected byte[] padBuffer() {
        return MDUtil.padBuffer(count, BLOCK_LENGTH);
    }

    protected void transform(byte[] in, int i) {
        int X0 = (in[i++] & 0xFF) | (in[i++] & 0xFF) << 8 | (in[i++] & 0xFF) << 16 | in[i++] << 24;
        int X1 = (in[i++] & 0xFF) | (in[i++] & 0xFF) << 8 | (in[i++] & 0xFF) << 16 | in[i++] << 24;
        int X2 = (in[i++] & 0xFF) | (in[i++] & 0xFF) << 8 | (in[i++] & 0xFF) << 16 | in[i++] << 24;
        int X3 = (in[i++] & 0xFF) | (in[i++] & 0xFF) << 8 | (in[i++] & 0xFF) << 16 | in[i++] << 24;
        int X4 = (in[i++] & 0xFF) | (in[i++] & 0xFF) << 8 | (in[i++] & 0xFF) << 16 | in[i++] << 24;
        int X5 = (in[i++] & 0xFF) | (in[i++] & 0xFF) << 8 | (in[i++] & 0xFF) << 16 | in[i++] << 24;
        int X6 = (in[i++] & 0xFF) | (in[i++] & 0xFF) << 8 | (in[i++] & 0xFF) << 16 | in[i++] << 24;
        int X7 = (in[i++] & 0xFF) | (in[i++] & 0xFF) << 8 | (in[i++] & 0xFF) << 16 | in[i++] << 24;
        int X8 = (in[i++] & 0xFF) | (in[i++] & 0xFF) << 8 | (in[i++] & 0xFF) << 16 | in[i++] << 24;
        int X9 = (in[i++] & 0xFF) | (in[i++] & 0xFF) << 8 | (in[i++] & 0xFF) << 16 | in[i++] << 24;
        int X10 = (in[i++] & 0xFF) | (in[i++] & 0xFF) << 8 | (in[i++] & 0xFF) << 16 | in[i++] << 24;
        int X11 = (in[i++] & 0xFF) | (in[i++] & 0xFF) << 8 | (in[i++] & 0xFF) << 16 | in[i++] << 24;
        int X12 = (in[i++] & 0xFF) | (in[i++] & 0xFF) << 8 | (in[i++] & 0xFF) << 16 | in[i++] << 24;
        int X13 = (in[i++] & 0xFF) | (in[i++] & 0xFF) << 8 | (in[i++] & 0xFF) << 16 | in[i++] << 24;
        int X14 = (in[i++] & 0xFF) | (in[i++] & 0xFF) << 8 | (in[i++] & 0xFF) << 16 | in[i++] << 24;
        int X15 = (in[i++] & 0xFF) | (in[i++] & 0xFF) << 8 | (in[i++] & 0xFF) << 16 | in[i] << 24;

        int aa, bb, cc, dd;

        aa = a;
        bb = b;
        cc = c;
        dd = d;

        aa += ((bb & cc) | ((~bb) & dd)) + X0;
        aa = aa << 3 | aa >>> -3;
        dd += ((aa & bb) | ((~aa) & cc)) + X1;
        dd = dd << 7 | dd >>> -7;
        cc += ((dd & aa) | ((~dd) & bb)) + X2;
        cc = cc << 11 | cc >>> -11;
        bb += ((cc & dd) | ((~cc) & aa)) + X3;
        bb = bb << 19 | bb >>> -19;
        aa += ((bb & cc) | ((~bb) & dd)) + X4;
        aa = aa << 3 | aa >>> -3;
        dd += ((aa & bb) | ((~aa) & cc)) + X5;
        dd = dd << 7 | dd >>> -7;
        cc += ((dd & aa) | ((~dd) & bb)) + X6;
        cc = cc << 11 | cc >>> -11;
        bb += ((cc & dd) | ((~cc) & aa)) + X7;
        bb = bb << 19 | bb >>> -19;
        aa += ((bb & cc) | ((~bb) & dd)) + X8;
        aa = aa << 3 | aa >>> -3;
        dd += ((aa & bb) | ((~aa) & cc)) + X9;
        dd = dd << 7 | dd >>> -7;
        cc += ((dd & aa) | ((~dd) & bb)) + X10;
        cc = cc << 11 | cc >>> -11;
        bb += ((cc & dd) | ((~cc) & aa)) + X11;
        bb = bb << 19 | bb >>> -19;
        aa += ((bb & cc) | ((~bb) & dd)) + X12;
        aa = aa << 3 | aa >>> -3;
        dd += ((aa & bb) | ((~aa) & cc)) + X13;
        dd = dd << 7 | dd >>> -7;
        cc += ((dd & aa) | ((~dd) & bb)) + X14;
        cc = cc << 11 | cc >>> -11;
        bb += ((cc & dd) | ((~cc) & aa)) + X15;
        bb = bb << 19 | bb >>> -19;

        aa += ((bb & (cc | dd)) | (cc & dd)) + X0 + 0x5a827999;
        aa = aa << 3 | aa >>> -3;
        dd += ((aa & (bb | cc)) | (bb & cc)) + X4 + 0x5a827999;
        dd = dd << 5 | dd >>> -5;
        cc += ((dd & (aa | bb)) | (aa & bb)) + X8 + 0x5a827999;
        cc = cc << 9 | cc >>> -9;
        bb += ((cc & (dd | aa)) | (dd & aa)) + X12 + 0x5a827999;
        bb = bb << 13 | bb >>> -13;
        aa += ((bb & (cc | dd)) | (cc & dd)) + X1 + 0x5a827999;
        aa = aa << 3 | aa >>> -3;
        dd += ((aa & (bb | cc)) | (bb & cc)) + X5 + 0x5a827999;
        dd = dd << 5 | dd >>> -5;
        cc += ((dd & (aa | bb)) | (aa & bb)) + X9 + 0x5a827999;
        cc = cc << 9 | cc >>> -9;
        bb += ((cc & (dd | aa)) | (dd & aa)) + X13 + 0x5a827999;
        bb = bb << 13 | bb >>> -13;
        aa += ((bb & (cc | dd)) | (cc & dd)) + X2 + 0x5a827999;
        aa = aa << 3 | aa >>> -3;
        dd += ((aa & (bb | cc)) | (bb & cc)) + X6 + 0x5a827999;
        dd = dd << 5 | dd >>> -5;
        cc += ((dd & (aa | bb)) | (aa & bb)) + X10 + 0x5a827999;
        cc = cc << 9 | cc >>> -9;
        bb += ((cc & (dd | aa)) | (dd & aa)) + X14 + 0x5a827999;
        bb = bb << 13 | bb >>> -13;
        aa += ((bb & (cc | dd)) | (cc & dd)) + X3 + 0x5a827999;
        aa = aa << 3 | aa >>> -3;
        dd += ((aa & (bb | cc)) | (bb & cc)) + X7 + 0x5a827999;
        dd = dd << 5 | dd >>> -5;
        cc += ((dd & (aa | bb)) | (aa & bb)) + X11 + 0x5a827999;
        cc = cc << 9 | cc >>> -9;
        bb += ((cc & (dd | aa)) | (dd & aa)) + X15 + 0x5a827999;
        bb = bb << 13 | bb >>> -13;

        aa += (bb ^ cc ^ dd) + X0 + 0x6ed9eba1;
        aa = aa << 3 | aa >>> -3;
        dd += (aa ^ bb ^ cc) + X8 + 0x6ed9eba1;
        dd = dd << 9 | dd >>> -9;
        cc += (dd ^ aa ^ bb) + X4 + 0x6ed9eba1;
        cc = cc << 11 | cc >>> -11;
        bb += (cc ^ dd ^ aa) + X12 + 0x6ed9eba1;
        bb = bb << 15 | bb >>> -15;
        aa += (bb ^ cc ^ dd) + X2 + 0x6ed9eba1;
        aa = aa << 3 | aa >>> -3;
        dd += (aa ^ bb ^ cc) + X10 + 0x6ed9eba1;
        dd = dd << 9 | dd >>> -9;
        cc += (dd ^ aa ^ bb) + X6 + 0x6ed9eba1;
        cc = cc << 11 | cc >>> -11;
        bb += (cc ^ dd ^ aa) + X14 + 0x6ed9eba1;
        bb = bb << 15 | bb >>> -15;
        aa += (bb ^ cc ^ dd) + X1 + 0x6ed9eba1;
        aa = aa << 3 | aa >>> -3;
        dd += (aa ^ bb ^ cc) + X9 + 0x6ed9eba1;
        dd = dd << 9 | dd >>> -9;
        cc += (dd ^ aa ^ bb) + X5 + 0x6ed9eba1;
        cc = cc << 11 | cc >>> -11;
        bb += (cc ^ dd ^ aa) + X13 + 0x6ed9eba1;
        bb = bb << 15 | bb >>> -15;
        aa += (bb ^ cc ^ dd) + X3 + 0x6ed9eba1;
        aa = aa << 3 | aa >>> -3;
        dd += (aa ^ bb ^ cc) + X11 + 0x6ed9eba1;
        dd = dd << 9 | dd >>> -9;
        cc += (dd ^ aa ^ bb) + X7 + 0x6ed9eba1;
        cc = cc << 11 | cc >>> -11;
        bb += (cc ^ dd ^ aa) + X15 + 0x6ed9eba1;
        bb = bb << 15 | bb >>> -15;

        a += aa;
        b += bb;
        c += cc;
        d += dd;
    }
}
