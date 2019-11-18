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

package net.daporkchop.lib.encoding;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.binary.Endianess;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.UUID;

/**
 * Helper class containing various methods for converting primitive arrays into {@link byte[]}s and back.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class ToBytes {
    public byte[] toBytes(@NonNull short... in) {
        return toBytes(null, in);
    }

    public byte[] toBytes(Endianess endianess, @NonNull short... in) {
        if (in.length == 0) {
            return new byte[0];
        } else {
            int length = in.length;
            byte[] b = new byte[length << 1];
            if (endianess == null || endianess == Endianess.NATIVE) {
                PUnsafe.copyMemory(in, PUnsafe.ARRAY_SHORT_BASE_OFFSET, b, PUnsafe.ARRAY_BYTE_BASE_OFFSET, length << 1);
            } else {
                for (int j = 0; j < length; j++) {
                    PUnsafe.putShort(b, PUnsafe.ARRAY_BYTE_BASE_OFFSET + (j << 1), Short.reverseBytes(in[j]));
                }
            }
            return b;
        }
    }

    public byte[] toBytes(@NonNull int... in) {
        return toBytes(null, in);
    }

    public byte[] toBytes(Endianess endianess, @NonNull int... in) {
        if (in.length == 0) {
            return new byte[0];
        } else {
            int length = in.length;
            byte[] b = new byte[length << 2];
            if (endianess == null || endianess == Endianess.NATIVE) {
                PUnsafe.copyMemory(in, PUnsafe.ARRAY_INT_BASE_OFFSET, b, PUnsafe.ARRAY_BYTE_BASE_OFFSET, length << 2);
            } else {
                for (int j = 0; j < length; j++) {
                    PUnsafe.putInt(b, PUnsafe.ARRAY_BYTE_BASE_OFFSET + (j << 2), Integer.reverseBytes(in[j]));
                }
            }
            return b;
        }
    }

    public byte[] toBytes(@NonNull long... in)  {
        return toBytes(null, in);
    }

    public byte[] toBytes(Endianess endianess, @NonNull long... in) {
        if (in.length == 0) {
            return new byte[0];
        } else {
            int length = in.length;
            byte[] b = new byte[length << 3];
            if (endianess == null || endianess == Endianess.NATIVE) {
                PUnsafe.copyMemory(in, PUnsafe.ARRAY_LONG_BASE_OFFSET, b, PUnsafe.ARRAY_BYTE_BASE_OFFSET, length << 3);
            } else {
                for (int j = 0; j < length; j++) {
                    PUnsafe.putLong(b, PUnsafe.ARRAY_BYTE_BASE_OFFSET + (j << 3), Long.reverseBytes(in[j]));
                }
            }
            return b;
        }
    }

    public short[] toShorts(@NonNull byte[] in) {
        if (in.length == 0) {
            return new short[0];
        } else {
            short[] s = new short[in.length >>> 1];
            for (int j = 0; j < s.length; j++) {
                int k = j << 1;
                s[j] = (short) ((in[k] & 0xFF) |
                        ((in[k + 1] & 0xFF) << 8));
            }
            return s;
        }
    }

    public static int[] toInts(@NonNull byte[] in) {
        if (in.length == 0) {
            return new int[0];
        } else {
            int[] i = new int[in.length >>> 2];
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

    public static long[] toLongs(@NonNull byte[] in) {
        if (in.length == 0) {
            return new long[0];
        } else {
            long[] l = new long[in.length >>> 3];
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

    public byte[] toBytes(@NonNull UUID uuid) {
        return toBytes(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
    }

    public UUID fromBytes(@NonNull byte[] bytes) {
        if (bytes.length != 16) {
            throw new IllegalArgumentException("Data must be 16 bytes long!");
        }
        long[] longs = toLongs(bytes);
        return new UUID(longs[0], longs[1]);
    }
}
