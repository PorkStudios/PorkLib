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

public class Sha1Impl extends Digest {
    private static final int BLOCK_SIZE = 64; // inner block size in bytes

    private static final String DIGEST0 = "A9993E364706816ABA3E25717850C26C9CD0D89D";

    private static final int[] w = new int[80];

    private static Boolean valid;

    private int h0, h1, h2, h3, h4;

    public Sha1Impl() {
        super("sha-160", 20, BLOCK_SIZE);
    }

    /**
     * <p>Private constructor for cloning purposes.</p>
     *
     * @param md the instance to clone.
     */
    private Sha1Impl(Sha1Impl md) {
        this();

        this.h0 = md.h0;
        this.h1 = md.h1;
        this.h2 = md.h2;
        this.h3 = md.h3;
        this.h4 = md.h4;
        this.count = md.count;
        this.buffer = md.buffer.clone();
    }

    public static final int[]
    G(int hh0, int hh1, int hh2, int hh3, int hh4, byte[] in, int offset) {
//      int[] w = new int[80];
//      int i, T;
//      for (i = 0; i < 16; i++) {
//         w[i] = in[offset++]         << 24 |
//               (in[offset++] & 0xFF) << 16 |
//               (in[offset++] & 0xFF) <<  8 |
//               (in[offset++] & 0xFF);
//      }
//      for (i = 16; i < 80; i++) {
//         T = w[i-3] ^ w[i-8] ^ w[i-14] ^ w[i-16];
//         w[i] = T << 1 | T >>> 31;
//      }

//      return sha(hh0, hh1, hh2, hh3, hh4, in, offset, w);
        return sha(hh0, hh1, hh2, hh3, hh4, in, offset);
    }

    // Instance methods
    // -------------------------------------------------------------------------

    // java.lang.Cloneable interface implementation ----------------------------

    private static final synchronized int[]
//   sha(int hh0, int hh1, int hh2, int hh3, int hh4, byte[] in, int offset, int[] w) {
    sha(int hh0, int hh1, int hh2, int hh3, int hh4, byte[] in, int offset) {
        int A = hh0;
        int B = hh1;
        int C = hh2;
        int D = hh3;
        int E = hh4;
        int r, T;

        for (r = 0; r < 16; r++) {
            w[r] = in[offset++] << 24 |
                    (in[offset++] & 0xFF) << 16 |
                    (in[offset++] & 0xFF) << 8 |
                    (in[offset++] & 0xFF);
        }
        for (r = 16; r < 80; r++) {
            T = w[r - 3] ^ w[r - 8] ^ w[r - 14] ^ w[r - 16];
            w[r] = T << 1 | T >>> 31;
        }

        // rounds 0-19
        for (r = 0; r < 20; r++) {
            T = (A << 5 | A >>> 27) + ((B & C) | (~B & D)) + E + w[r] + 0x5A827999;
            E = D;
            D = C;
            C = B << 30 | B >>> 2;
            B = A;
            A = T;
        }

        // rounds 20-39
        for (r = 20; r < 40; r++) {
            T = (A << 5 | A >>> 27) + (B ^ C ^ D) + E + w[r] + 0x6ED9EBA1;
            E = D;
            D = C;
            C = B << 30 | B >>> 2;
            B = A;
            A = T;
        }

        // rounds 40-59
        for (r = 40; r < 60; r++) {
            T = (A << 5 | A >>> 27) + (B & C | B & D | C & D) + E + w[r] + 0x8F1BBCDC;
            E = D;
            D = C;
            C = B << 30 | B >>> 2;
            B = A;
            A = T;
        }

        // rounds 60-79
        for (r = 60; r < 80; r++) {
            T = (A << 5 | A >>> 27) + (B ^ C ^ D) + E + w[r] + 0xCA62C1D6;
            E = D;
            D = C;
            C = B << 30 | B >>> 2;
            B = A;
            A = T;
        }

        return new int[]{hh0 + A, hh1 + B, hh2 + C, hh3 + D, hh4 + E};
    }

    // Implementation of concrete methods in Digest --------------------------

    public Object clone() {
        return new Sha1Impl(this);
    }

    protected void transform(byte[] in, int offset) {
//      int i, T;
//      for (i = 0; i < 16; i++) {
//         W[i] = in[offset++]         << 24 |
//               (in[offset++] & 0xFF) << 16 |
//               (in[offset++] & 0xFF) <<  8 |
//               (in[offset++] & 0xFF);
//      }
//      for (i = 16; i < 80; i++) {
//         T = W[i-3] ^ W[i-8] ^ W[i-14] ^ W[i-16];
//         W[i] = T << 1 | T >>> 31;
//      }

//      int[] result = sha(h0, h1, h2, h3, h4, in, offset, W);
        int[] result = sha(this.h0, this.h1, this.h2, this.h3, this.h4, in, offset);

        this.h0 = result[0];
        this.h1 = result[1];
        this.h2 = result[2];
        this.h3 = result[3];
        this.h4 = result[4];
    }

    protected byte[] padBuffer() {
        return SHAUtil.padBuffer(this.count, BLOCK_SIZE);
    }

    protected byte[] getResult() {

        return new byte[]{
                (byte) (this.h0 >>> 24), (byte) (this.h0 >>> 16), (byte) (this.h0 >>> 8), (byte) this.h0,
                (byte) (this.h1 >>> 24), (byte) (this.h1 >>> 16), (byte) (this.h1 >>> 8), (byte) this.h1,
                (byte) (this.h2 >>> 24), (byte) (this.h2 >>> 16), (byte) (this.h2 >>> 8), (byte) this.h2,
                (byte) (this.h3 >>> 24), (byte) (this.h3 >>> 16), (byte) (this.h3 >>> 8), (byte) this.h3,
                (byte) (this.h4 >>> 24), (byte) (this.h4 >>> 16), (byte) (this.h4 >>> 8), (byte) this.h4
        };
    }

    protected void resetContext() {
        // magic SHA-1/RIPEMD160 initialisation constants
        this.h0 = 0x67452301;
        this.h1 = 0xEFCDAB89;
        this.h2 = 0x98BADCFE;
        this.h3 = 0x10325476;
        this.h4 = 0xC3D2E1F0;
    }

    // SHA specific methods ----------------------------------------------------

    public boolean selfTest() {
        if (valid == null) {
            Sha1Impl md = new Sha1Impl();
            md.update((byte) 0x61); // a
            md.update((byte) 0x62); // b
            md.update((byte) 0x63); // c
            String result = HexBin.encode(md.digest());
            valid = DIGEST0.equals(result);
        }
        return valid;
    }
}
