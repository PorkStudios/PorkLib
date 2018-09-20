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

package net.daporkchop.lib.hash.impl.skid;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

public class WhirlpoolImpl extends BaseHash {
    private static final int BLOCK_SIZE = 64;

    private static final String DIGEST0 =
            "470F0409ABAA446E49667D4EBE12A14387CEDBD10DD17B8243CAD550A089DC0F" +
                    "EEA7AA40F6C2AAAB71C6EBD076E43C7CFCA0AD32567897DCB5969861049A0F5A";

    private static final int R = 10;

    private static final String Sd =
            "\u1823\uc6E8\u87B8\u014F\u36A6\ud2F5\u796F\u9152" +
                    "\u60Bc\u9B8E\uA30c\u7B35\u1dE0\ud7c2\u2E4B\uFE57" +
                    "\u1577\u37E5\u9FF0\u4AdA\u58c9\u290A\uB1A0\u6B85" +
                    "\uBd5d\u10F4\ucB3E\u0567\uE427\u418B\uA77d\u95d8" +
                    "\uFBEE\u7c66\udd17\u479E\ucA2d\uBF07\uAd5A\u8333" +
                    "\u6302\uAA71\uc819\u49d9\uF2E3\u5B88\u9A26\u32B0" +
                    "\uE90F\ud580\uBEcd\u3448\uFF7A\u905F\u2068\u1AAE" +
                    "\uB454\u9322\u64F1\u7312\u4008\uc3Ec\udBA1\u8d3d" +
                    "\u9700\ucF2B\u7682\ud61B\uB5AF\u6A50\u45F3\u30EF" +
                    "\u3F55\uA2EA\u65BA\u2Fc0\udE1c\uFd4d\u9275\u068A" +
                    "\uB2E6\u0E1F\u62d4\uA896\uF9c5\u2559\u8472\u394c" +
                    "\u5E78\u388c\ud1A5\uE261\uB321\u9c1E\u43c7\uFc04" +
                    "\u5199\u6d0d\uFAdF\u7E24\u3BAB\ucE11\u8F4E\uB7EB" +
                    "\u3c81\u94F7\uB913\u2cd3\uE76E\uc403\u5644\u7FA9" +
                    "\u2ABB\uc153\udc0B\u9d6c\u3174\uF646\uAc89\u14E1" +
                    "\u163A\u6909\u70B6\ud0Ed\ucc42\u98A4\u285c\uF886";

    private static final long[] T0 = new long[256];
    private static final long[] T1 = new long[256];
    private static final long[] T2 = new long[256];
    private static final long[] T3 = new long[256];
    private static final long[] T4 = new long[256];
    private static final long[] T5 = new long[256];
    private static final long[] T6 = new long[256];
    private static final long[] T7 = new long[256];
    private static final long[] rc = new long[R];

    private static Boolean valid;

    static {
        int ROOT = 0x11d;
        int i, r, j;
        long s, s2, s3, s4, s5, s8, s9, t;
        char c;
        byte[] S = new byte[256];
        for (i = 0; i < 256; i++) {
            c = Sd.charAt(i >>> 1);

            s = ((i & 1) == 0 ? c >>> 8 : c) & 0xFFL;
            s2 = s << 1;
            if (s2 > 0xFFL) {
                s2 ^= ROOT;
            }
            s3 = s2 ^ s;
            s4 = s2 << 1;
            if (s4 > 0xFFL) {
                s4 ^= ROOT;
            }
            s5 = s4 ^ s;
            s8 = s4 << 1;
            if (s8 > 0xFFL) {
                s8 ^= ROOT;
            }
            s9 = s8 ^ s;

            S[i] = (byte) s;
            T0[i] = t = s << 56 | s << 48 | s3 << 40 | s << 32 |
                    s5 << 24 | s8 << 16 | s9 << 8 | s5;
            T1[i] = t >>> 8 | t << 56;
            T2[i] = t >>> 16 | t << 48;
            T3[i] = t >>> 24 | t << 40;
            T4[i] = t >>> 32 | t << 32;
            T5[i] = t >>> 40 | t << 24;
            T6[i] = t >>> 48 | t << 16;
            T7[i] = t >>> 56 | t << 8;
        }

        for (r = 1, i = 0, j = 0; r < R + 1; r++) {
            rc[i++] = (S[j++] & 0xFFL) << 56 | (S[j++] & 0xFFL) << 48 |
                    (S[j++] & 0xFFL) << 40 | (S[j++] & 0xFFL) << 32 |
                    (S[j++] & 0xFFL) << 24 | (S[j++] & 0xFFL) << 16 |
                    (S[j++] & 0xFFL) << 8 | (S[j++] & 0xFFL);
        }
    }

    private long H0, H1, H2, H3, H4, H5, H6, H7;

    public WhirlpoolImpl() {
        super("whirlpool", 20, BLOCK_SIZE);
    }

    private WhirlpoolImpl(WhirlpoolImpl md) {
        this();

        this.H0 = md.H0;
        this.H1 = md.H1;
        this.H2 = md.H2;
        this.H3 = md.H3;
        this.H4 = md.H4;
        this.H5 = md.H5;
        this.H6 = md.H6;
        this.H7 = md.H7;
        this.count = md.count;
        this.buffer = md.buffer.clone();
    }

    public Object clone() {
        return (new WhirlpoolImpl(this));
    }

    protected void transform(byte[] in, int offset) {
        long n0 = (in[offset++] & 0xFFL) << 56 | (in[offset++] & 0xFFL) << 48 |
                (in[offset++] & 0xFFL) << 40 | (in[offset++] & 0xFFL) << 32 |
                (in[offset++] & 0xFFL) << 24 | (in[offset++] & 0xFFL) << 16 |
                (in[offset++] & 0xFFL) << 8 | (in[offset++] & 0xFFL);
        long n1 = (in[offset++] & 0xFFL) << 56 | (in[offset++] & 0xFFL) << 48 |
                (in[offset++] & 0xFFL) << 40 | (in[offset++] & 0xFFL) << 32 |
                (in[offset++] & 0xFFL) << 24 | (in[offset++] & 0xFFL) << 16 |
                (in[offset++] & 0xFFL) << 8 | (in[offset++] & 0xFFL);
        long n2 = (in[offset++] & 0xFFL) << 56 | (in[offset++] & 0xFFL) << 48 |
                (in[offset++] & 0xFFL) << 40 | (in[offset++] & 0xFFL) << 32 |
                (in[offset++] & 0xFFL) << 24 | (in[offset++] & 0xFFL) << 16 |
                (in[offset++] & 0xFFL) << 8 | (in[offset++] & 0xFFL);
        long n3 = (in[offset++] & 0xFFL) << 56 | (in[offset++] & 0xFFL) << 48 |
                (in[offset++] & 0xFFL) << 40 | (in[offset++] & 0xFFL) << 32 |
                (in[offset++] & 0xFFL) << 24 | (in[offset++] & 0xFFL) << 16 |
                (in[offset++] & 0xFFL) << 8 | (in[offset++] & 0xFFL);
        long n4 = (in[offset++] & 0xFFL) << 56 | (in[offset++] & 0xFFL) << 48 |
                (in[offset++] & 0xFFL) << 40 | (in[offset++] & 0xFFL) << 32 |
                (in[offset++] & 0xFFL) << 24 | (in[offset++] & 0xFFL) << 16 |
                (in[offset++] & 0xFFL) << 8 | (in[offset++] & 0xFFL);
        long n5 = (in[offset++] & 0xFFL) << 56 | (in[offset++] & 0xFFL) << 48 |
                (in[offset++] & 0xFFL) << 40 | (in[offset++] & 0xFFL) << 32 |
                (in[offset++] & 0xFFL) << 24 | (in[offset++] & 0xFFL) << 16 |
                (in[offset++] & 0xFFL) << 8 | (in[offset++] & 0xFFL);
        long n6 = (in[offset++] & 0xFFL) << 56 | (in[offset++] & 0xFFL) << 48 |
                (in[offset++] & 0xFFL) << 40 | (in[offset++] & 0xFFL) << 32 |
                (in[offset++] & 0xFFL) << 24 | (in[offset++] & 0xFFL) << 16 |
                (in[offset++] & 0xFFL) << 8 | (in[offset++] & 0xFFL);
        long n7 = (in[offset++] & 0xFFL) << 56 | (in[offset++] & 0xFFL) << 48 |
                (in[offset++] & 0xFFL) << 40 | (in[offset++] & 0xFFL) << 32 |
                (in[offset++] & 0xFFL) << 24 | (in[offset++] & 0xFFL) << 16 |
                (in[offset++] & 0xFFL) << 8 | (in[offset] & 0xFFL);

        long k00 = this.H0;
        long k01 = this.H1;
        long k02 = this.H2;
        long k03 = this.H3;
        long k04 = this.H4;
        long k05 = this.H5;
        long k06 = this.H6;
        long k07 = this.H7;

        long nn0 = n0 ^ k00;
        long nn1 = n1 ^ k01;
        long nn2 = n2 ^ k02;
        long nn3 = n3 ^ k03;
        long nn4 = n4 ^ k04;
        long nn5 = n5 ^ k05;
        long nn6 = n6 ^ k06;
        long nn7 = n7 ^ k07;

        long w0, w1, w2, w3, w4, w5, w6, w7;
        w0 = w1 = w2 = w3 = w4 = w5 = w6 = w7 = 0L;

        for (int r = 0; r < R; r++) {
            long Kr0 = T0[(int) ((k00 >> 56) & 0xFFL)] ^ T1[(int) ((k07 >> 48) & 0xFFL)] ^
                    T2[(int) ((k06 >> 40) & 0xFFL)] ^ T3[(int) ((k05 >> 32) & 0xFFL)] ^
                    T4[(int) ((k04 >> 24) & 0xFFL)] ^ T5[(int) ((k03 >> 16) & 0xFFL)] ^
                    T6[(int) ((k02 >> 8) & 0xFFL)] ^ T7[(int) (k01 & 0xFFL)] ^
                    rc[r];

            long Kr1 = T0[(int) ((k01 >> 56) & 0xFFL)] ^ T1[(int) ((k00 >> 48) & 0xFFL)] ^
                    T2[(int) ((k07 >> 40) & 0xFFL)] ^ T3[(int) ((k06 >> 32) & 0xFFL)] ^
                    T4[(int) ((k05 >> 24) & 0xFFL)] ^ T5[(int) ((k04 >> 16) & 0xFFL)] ^
                    T6[(int) ((k03 >> 8) & 0xFFL)] ^ T7[(int) (k02 & 0xFFL)];

            long Kr2 = T0[(int) ((k02 >> 56) & 0xFFL)] ^ T1[(int) ((k01 >> 48) & 0xFFL)] ^
                    T2[(int) ((k00 >> 40) & 0xFFL)] ^ T3[(int) ((k07 >> 32) & 0xFFL)] ^
                    T4[(int) ((k06 >> 24) & 0xFFL)] ^ T5[(int) ((k05 >> 16) & 0xFFL)] ^
                    T6[(int) ((k04 >> 8) & 0xFFL)] ^ T7[(int) (k03 & 0xFFL)];

            long Kr3 = T0[(int) ((k03 >> 56) & 0xFFL)] ^ T1[(int) ((k02 >> 48) & 0xFFL)] ^
                    T2[(int) ((k01 >> 40) & 0xFFL)] ^ T3[(int) ((k00 >> 32) & 0xFFL)] ^
                    T4[(int) ((k07 >> 24) & 0xFFL)] ^ T5[(int) ((k06 >> 16) & 0xFFL)] ^
                    T6[(int) ((k05 >> 8) & 0xFFL)] ^ T7[(int) (k04 & 0xFFL)];

            long Kr4 = T0[(int) ((k04 >> 56) & 0xFFL)] ^ T1[(int) ((k03 >> 48) & 0xFFL)] ^
                    T2[(int) ((k02 >> 40) & 0xFFL)] ^ T3[(int) ((k01 >> 32) & 0xFFL)] ^
                    T4[(int) ((k00 >> 24) & 0xFFL)] ^ T5[(int) ((k07 >> 16) & 0xFFL)] ^
                    T6[(int) ((k06 >> 8) & 0xFFL)] ^ T7[(int) (k05 & 0xFFL)];

            long Kr5 = T0[(int) ((k05 >> 56) & 0xFFL)] ^ T1[(int) ((k04 >> 48) & 0xFFL)] ^
                    T2[(int) ((k03 >> 40) & 0xFFL)] ^ T3[(int) ((k02 >> 32) & 0xFFL)] ^
                    T4[(int) ((k01 >> 24) & 0xFFL)] ^ T5[(int) ((k00 >> 16) & 0xFFL)] ^
                    T6[(int) ((k07 >> 8) & 0xFFL)] ^ T7[(int) (k06 & 0xFFL)];

            long Kr6 = T0[(int) ((k06 >> 56) & 0xFFL)] ^ T1[(int) ((k05 >> 48) & 0xFFL)] ^
                    T2[(int) ((k04 >> 40) & 0xFFL)] ^ T3[(int) ((k03 >> 32) & 0xFFL)] ^
                    T4[(int) ((k02 >> 24) & 0xFFL)] ^ T5[(int) ((k01 >> 16) & 0xFFL)] ^
                    T6[(int) ((k00 >> 8) & 0xFFL)] ^ T7[(int) (k07 & 0xFFL)];

            long Kr7 = T0[(int) ((k07 >> 56) & 0xFFL)] ^ T1[(int) ((k06 >> 48) & 0xFFL)] ^
                    T2[(int) ((k05 >> 40) & 0xFFL)] ^ T3[(int) ((k04 >> 32) & 0xFFL)] ^
                    T4[(int) ((k03 >> 24) & 0xFFL)] ^ T5[(int) ((k02 >> 16) & 0xFFL)] ^
                    T6[(int) ((k01 >> 8) & 0xFFL)] ^ T7[(int) (k00 & 0xFFL)];

            k00 = Kr0;
            k01 = Kr1;
            k02 = Kr2;
            k03 = Kr3;
            k04 = Kr4;
            k05 = Kr5;
            k06 = Kr6;
            k07 = Kr7;

            w0 = T0[(int) ((nn0 >> 56) & 0xFFL)] ^ T1[(int) ((nn7 >> 48) & 0xFFL)] ^
                    T2[(int) ((nn6 >> 40) & 0xFFL)] ^ T3[(int) ((nn5 >> 32) & 0xFFL)] ^
                    T4[(int) ((nn4 >> 24) & 0xFFL)] ^ T5[(int) ((nn3 >> 16) & 0xFFL)] ^
                    T6[(int) ((nn2 >> 8) & 0xFFL)] ^ T7[(int) (nn1 & 0xFFL)] ^
                    Kr0;
            w1 = T0[(int) ((nn1 >> 56) & 0xFFL)] ^ T1[(int) ((nn0 >> 48) & 0xFFL)] ^
                    T2[(int) ((nn7 >> 40) & 0xFFL)] ^ T3[(int) ((nn6 >> 32) & 0xFFL)] ^
                    T4[(int) ((nn5 >> 24) & 0xFFL)] ^ T5[(int) ((nn4 >> 16) & 0xFFL)] ^
                    T6[(int) ((nn3 >> 8) & 0xFFL)] ^ T7[(int) (nn2 & 0xFFL)] ^
                    Kr1;
            w2 = T0[(int) ((nn2 >> 56) & 0xFFL)] ^ T1[(int) ((nn1 >> 48) & 0xFFL)] ^
                    T2[(int) ((nn0 >> 40) & 0xFFL)] ^ T3[(int) ((nn7 >> 32) & 0xFFL)] ^
                    T4[(int) ((nn6 >> 24) & 0xFFL)] ^ T5[(int) ((nn5 >> 16) & 0xFFL)] ^
                    T6[(int) ((nn4 >> 8) & 0xFFL)] ^ T7[(int) (nn3 & 0xFFL)] ^
                    Kr2;
            w3 = T0[(int) ((nn3 >> 56) & 0xFFL)] ^ T1[(int) ((nn2 >> 48) & 0xFFL)] ^
                    T2[(int) ((nn1 >> 40) & 0xFFL)] ^ T3[(int) ((nn0 >> 32) & 0xFFL)] ^
                    T4[(int) ((nn7 >> 24) & 0xFFL)] ^ T5[(int) ((nn6 >> 16) & 0xFFL)] ^
                    T6[(int) ((nn5 >> 8) & 0xFFL)] ^ T7[(int) (nn4 & 0xFFL)] ^
                    Kr3;
            w4 = T0[(int) ((nn4 >> 56) & 0xFFL)] ^ T1[(int) ((nn3 >> 48) & 0xFFL)] ^
                    T2[(int) ((nn2 >> 40) & 0xFFL)] ^ T3[(int) ((nn1 >> 32) & 0xFFL)] ^
                    T4[(int) ((nn0 >> 24) & 0xFFL)] ^ T5[(int) ((nn7 >> 16) & 0xFFL)] ^
                    T6[(int) ((nn6 >> 8) & 0xFFL)] ^ T7[(int) (nn5 & 0xFFL)] ^
                    Kr4;
            w5 = T0[(int) ((nn5 >> 56) & 0xFFL)] ^ T1[(int) ((nn4 >> 48) & 0xFFL)] ^
                    T2[(int) ((nn3 >> 40) & 0xFFL)] ^ T3[(int) ((nn2 >> 32) & 0xFFL)] ^
                    T4[(int) ((nn1 >> 24) & 0xFFL)] ^ T5[(int) ((nn0 >> 16) & 0xFFL)] ^
                    T6[(int) ((nn7 >> 8) & 0xFFL)] ^ T7[(int) (nn6 & 0xFFL)] ^
                    Kr5;
            w6 = T0[(int) ((nn6 >> 56) & 0xFFL)] ^ T1[(int) ((nn5 >> 48) & 0xFFL)] ^
                    T2[(int) ((nn4 >> 40) & 0xFFL)] ^ T3[(int) ((nn3 >> 32) & 0xFFL)] ^
                    T4[(int) ((nn2 >> 24) & 0xFFL)] ^ T5[(int) ((nn1 >> 16) & 0xFFL)] ^
                    T6[(int) ((nn0 >> 8) & 0xFFL)] ^ T7[(int) (nn7 & 0xFFL)] ^
                    Kr6;
            w7 = T0[(int) ((nn7 >> 56) & 0xFFL)] ^ T1[(int) ((nn6 >> 48) & 0xFFL)] ^
                    T2[(int) ((nn5 >> 40) & 0xFFL)] ^ T3[(int) ((nn4 >> 32) & 0xFFL)] ^
                    T4[(int) ((nn3 >> 24) & 0xFFL)] ^ T5[(int) ((nn2 >> 16) & 0xFFL)] ^
                    T6[(int) ((nn1 >> 8) & 0xFFL)] ^ T7[(int) (nn0 & 0xFFL)] ^
                    Kr7;

            nn0 = w0;
            nn1 = w1;
            nn2 = w2;
            nn3 = w3;
            nn4 = w4;
            nn5 = w5;
            nn6 = w6;
            nn7 = w7;
        }

        this.H0 ^= w0 ^ n0;
        this.H1 ^= w1 ^ n1;
        this.H2 ^= w2 ^ n2;
        this.H3 ^= w3 ^ n3;
        this.H4 ^= w4 ^ n4;
        this.H5 ^= w5 ^ n5;
        this.H6 ^= w6 ^ n6;
        this.H7 ^= w7 ^ n7;
    }

    protected byte[] padBuffer() {
        int n = (int) ((this.count + 33) % BLOCK_SIZE);
        int padding = n == 0 ? 33 : BLOCK_SIZE - n + 33;

        byte[] result = new byte[padding];

        result[0] = (byte) 0x80;

        long bits = this.count * 8;
        int i = padding - 8;
        result[i++] = (byte) (bits >>> 56);
        result[i++] = (byte) (bits >>> 48);
        result[i++] = (byte) (bits >>> 40);
        result[i++] = (byte) (bits >>> 32);
        result[i++] = (byte) (bits >>> 24);
        result[i++] = (byte) (bits >>> 16);
        result[i++] = (byte) (bits >>> 8);
        result[i] = (byte) bits;

        return result;
    }

    protected byte[] getResult() {

        return new byte[]{
                (byte) (this.H0 >>> 56), (byte) (this.H0 >>> 48), (byte) (this.H0 >>> 40), (byte) (this.H0 >>> 32),
                (byte) (this.H0 >>> 24), (byte) (this.H0 >>> 16), (byte) (this.H0 >>> 8), (byte) this.H0,
                (byte) (this.H1 >>> 56), (byte) (this.H1 >>> 48), (byte) (this.H1 >>> 40), (byte) (this.H1 >>> 32),
                (byte) (this.H1 >>> 24), (byte) (this.H1 >>> 16), (byte) (this.H1 >>> 8), (byte) this.H1,
                (byte) (this.H2 >>> 56), (byte) (this.H2 >>> 48), (byte) (this.H2 >>> 40), (byte) (this.H2 >>> 32),
                (byte) (this.H2 >>> 24), (byte) (this.H2 >>> 16), (byte) (this.H2 >>> 8), (byte) this.H2,
                (byte) (this.H3 >>> 56), (byte) (this.H3 >>> 48), (byte) (this.H3 >>> 40), (byte) (this.H3 >>> 32),
                (byte) (this.H3 >>> 24), (byte) (this.H3 >>> 16), (byte) (this.H3 >>> 8), (byte) this.H3,
                (byte) (this.H4 >>> 56), (byte) (this.H4 >>> 48), (byte) (this.H4 >>> 40), (byte) (this.H4 >>> 32),
                (byte) (this.H4 >>> 24), (byte) (this.H4 >>> 16), (byte) (this.H4 >>> 8), (byte) this.H4,
                (byte) (this.H5 >>> 56), (byte) (this.H5 >>> 48), (byte) (this.H5 >>> 40), (byte) (this.H5 >>> 32),
                (byte) (this.H5 >>> 24), (byte) (this.H5 >>> 16), (byte) (this.H5 >>> 8), (byte) this.H5,
                (byte) (this.H6 >>> 56), (byte) (this.H6 >>> 48), (byte) (this.H6 >>> 40), (byte) (this.H6 >>> 32),
                (byte) (this.H6 >>> 24), (byte) (this.H6 >>> 16), (byte) (this.H6 >>> 8), (byte) this.H6,
                (byte) (this.H7 >>> 56), (byte) (this.H7 >>> 48), (byte) (this.H7 >>> 40), (byte) (this.H7 >>> 32),
                (byte) (this.H7 >>> 24), (byte) (this.H7 >>> 16), (byte) (this.H7 >>> 8), (byte) this.H7
        };
    }

    protected void resetContext() {
        this.H0 = this.H1 = this.H2 = this.H3 = this.H4 = this.H5 = this.H6 = this.H7 = 0L;
    }

    public boolean selfTest() {
        if (valid == null) {
            valid = DIGEST0.equals(HexBin.encode(new WhirlpoolImpl().digest()));
        }
        return valid;
    }

}
