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

public class MD2Impl extends BaseHash {
    private static final int DIGEST_LENGTH = 16;

    private static final int BLOCK_LENGTH = 16;

    private static final byte[] PI = {
            41, 46, 67, -55, -94, -40, 124, 1, 61, 54, 84, -95, -20, -16, 6,
            19, 98, -89, 5, -13, -64, -57, 115, -116, -104, -109, 43, -39,
            -68, 76, -126, -54, 30, -101, 87, 60, -3, -44, -32, 22, 103, 66,
            111, 24, -118, 23, -27, 18, -66, 78, -60, -42, -38, -98, -34, 73,
            -96, -5, -11, -114, -69, 47, -18, 122, -87, 104, 121, -111, 21,
            -78, 7, 63, -108, -62, 16, -119, 11, 34, 95, 33, -128, 127, 93,
            -102, 90, -112, 50, 39, 53, 62, -52, -25, -65, -9, -105, 3, -1,
            25, 48, -77, 72, -91, -75, -47, -41, 94, -110, 42, -84, 86, -86,
            -58, 79, -72, 56, -46, -106, -92, 125, -74, 118, -4, 107, -30,
            -100, 116, 4, -15, 69, -99, 112, 89, 100, 113, -121, 32, -122,
            91, -49, 101, -26, 45, -88, 2, 27, 96, 37, -83, -82, -80, -71,
            -10, 28, 70, 97, 105, 52, 64, 126, 15, 85, 71, -93, 35, -35, 81,
            -81, 58, -61, 92, -7, -50, -70, -59, -22, 38, 44, 83, 13, 110,
            -123, 40, -124, 9, -45, -33, -51, -12, 65, -127, 77, 82, 106,
            -36, 55, -56, 108, -63, -85, -6, 36, -31, 123, 8, 12, -67, -79,
            74, 120, -120, -107, -117, -29, 99, -24, 109, -23, -53, -43, -2,
            59, 0, 29, 57, -14, -17, -73, 14, 102, 88, -48, -28, -90, 119,
            114, -8, -21, 117, 75, 10, 49, 68, 80, -76, -113, -19, 31, 26,
            -37, -103, -115, 51, -97, 17, -125, 20
    };

    private static final String DIGEST0 = "8350E5A3E24C153DF2275C9F80692773";

    private static Boolean valid;

    private byte[] checksum;

    private byte[] work;

    public MD2Impl() {
        super("md2", DIGEST_LENGTH, BLOCK_LENGTH);
    }

    private MD2Impl(MD2Impl md2) {
        this();

        this.count = md2.count;
        this.buffer = md2.buffer.clone();

        this.checksum = md2.checksum.clone();
        this.work = md2.work.clone();
    }

    public Object clone() {
        return new MD2Impl(this);
    }

    // Implementation of abstract methods in BaseHash --------------------------

    protected byte[] getResult() {
        byte[] result = new byte[DIGEST_LENGTH];

        // Encrypt checksum as last block.
        this.encryptBlock(this.checksum);

        System.arraycopy(this.work, 0, result, 0, BLOCK_LENGTH);

        return result;
    }

    protected void resetContext() {
        this.checksum = new byte[BLOCK_LENGTH];
        this.work = new byte[BLOCK_LENGTH * 3];
    }

    public boolean selfTest() {
        if (valid == null) {
            valid = DIGEST0.equals(HexBin.encode(new MD2Impl().digest()));
        }
        return valid;
    }

    protected byte[] padBuffer() {
        int length = BLOCK_LENGTH - (int) (this.count % BLOCK_LENGTH);
        if (length == 0) {
            length = BLOCK_LENGTH;
        }
        byte[] pad = new byte[length];
        for (int i = 0; i < length; i++) {
            pad[i] = (byte) length;
        }
        return pad;
    }

    protected void transform(byte[] in, int off) {
        this.updateCheckSumAndEncryptBlock(in, off);
    }

    private void encryptBlock(byte[] in) {
        for (int i = 0; i < BLOCK_LENGTH; i++) {
            byte b = in[0 + i];
            this.work[BLOCK_LENGTH + i] = b;
            this.work[BLOCK_LENGTH * 2 + i] = (byte) (this.work[i] ^ b);
        }

        byte t = 0;
        for (int i = 0; i < 18; i++) {
            for (int j = 0; j < 3 * BLOCK_LENGTH; j++) {
                t = (byte) (this.work[j] ^ PI[t & 0xFF]);
                this.work[j] = t;
            }
            t = (byte) (t + i);
        }
    }

    private void updateCheckSumAndEncryptBlock(byte[] in, int off) {
        byte l = this.checksum[BLOCK_LENGTH - 1];
        for (int i = 0; i < BLOCK_LENGTH; i++) {
            byte b = in[off + i];
            this.work[BLOCK_LENGTH + i] = b;
            this.work[BLOCK_LENGTH * 2 + i] = (byte) (this.work[i] ^ b);
            l = (byte) (this.checksum[i] ^ PI[(b ^ l) & 0xFF]);
            this.checksum[i] = l;
        }

        byte t = 0;
        for (int i = 0; i < 18; i++) {
            for (int j = 0; j < 3 * BLOCK_LENGTH; j++) {
                t = (byte) (this.work[j] ^ PI[t & 0xFF]);
                this.work[j] = t;
            }
            t = (byte) (t + i);
        }
    }
}
