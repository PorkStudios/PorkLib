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

package net.daporkchop.lib.encoding;

import java.util.UUID;

/**
 * @author DaPorkchop_
 */
public class ToBytes {
    public static byte[] toBytes(short... in) {
        if (in.length == 0) {
            return new byte[0];
        } else {
            byte[] b = new byte[in.length << 1];
            for (int j = 0; j < in.length; j++) {
                short s = in[j];
                b[j] = (byte) (s & 0xFF);
                b[j + 1] = (byte) ((s >> 8) & 0xFF);
            }
            return b;
        }
    }

    public static byte[] toBytes(int... in) {
        if (in.length == 0) {
            return new byte[0];
        } else {
            byte[] b = new byte[in.length << 2];
            for (int j = 0; j < in.length; j++) {
                int i = in[j];
                b[j] = (byte) (i & 0xFF);
                b[j + 1] = (byte) ((i >> 8) & 0xFF);
                b[j + 2] = (byte) ((i >> 16) & 0xFF);
                b[j + 3] = (byte) ((i >> 24) & 0xFF);
            }
            return b;
        }
    }

    public static byte[] toBytes(long... in) {
        if (in.length == 0) {
            return new byte[0];
        } else {
            byte[] b = new byte[in.length << 3];
            for (int j = 0; j < in.length; j++) {
                long l = in[j];
                b[j] = (byte) (l & 0xFFL);
                b[j + 1] = (byte) ((l >> 8L) & 0xFFL);
                b[j + 2] = (byte) ((l >> 16L) & 0xFFL);
                b[j + 3] = (byte) ((l >> 24L) & 0xFFL);
                b[j + 4] = (byte) ((l >> 32L) & 0xFFL);
                b[j + 5] = (byte) ((l >> 40L) & 0xFFL);
                b[j + 6] = (byte) ((l >> 48L) & 0xFFL);
                b[j + 7] = (byte) ((l >> 56L) & 0xFFL);
            }
            return b;
        }
    }

    public static short[] toShorts(byte[] in) {
        if (in.length == 0) {
            return new short[0];
        } else {
            short[] s = new short[in.length >> 1];
            for (int j = 0; j < s.length; j++) {
                int k = j << 1;
                s[j] = (short) ((in[k] & 0xFF) |
                        ((in[k + 1] & 0xFF) << 8));
            }
            return s;
        }
    }

    public static int[] toInts(byte[] in) {
        if (in.length == 0) {
            return new int[0];
        } else {
            int[] i = new int[in.length >> 2];
            for (int j = 0; j < i.length; j++) {
                int k = j << 2;
                i[j] = (in[k] & 0xFF) |
                        ((in[k + 1] & 0xFF) << 8) |
                        ((in[k + 2] & 0xFF) << 16) |
                        ((in[k + 3] & 0xFF) << 24);
            }
            return i;
        }
    }

    public static long[] toLongs(byte[] in) {
        if (in.length == 0) {
            return new long[0];
        } else {
            long[] l = new long[in.length >> 3];
            for (int j = 0; j < l.length; j++) {
                int k = j << 3;
                l[j] = (in[k] & 0xFFL) |
                        ((in[k + 1] & 0xFFL) << 8L) |
                        ((in[k + 2] & 0xFFL) << 16L) |
                        ((in[k + 3] & 0xFFL) << 24L) |
                        ((in[k + 4] & 0xFFL) << 32L) |
                        ((in[k + 5] & 0xFFL) << 40L) |
                        ((in[k + 6] & 0xFFL) << 48L) |
                        ((in[k + 7] & 0xFFL) << 56L);
            }
            return l;
        }
    }

    public static byte[] toBytes(UUID uuid) {
        return toBytes(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
    }

    public static UUID fromBytes(byte[] bytes) {
        if (bytes.length != 16) {
            throw new IllegalArgumentException("Data must be 16 bytes long!");
        }
        long[] longs = toLongs(bytes);
        return new UUID(longs[0], longs[1]);
    }
}
