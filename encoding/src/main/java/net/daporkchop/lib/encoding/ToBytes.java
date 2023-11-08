/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.encoding;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.system.PlatformInfo;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.nio.ByteOrder;
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

    public byte[] toBytes(ByteOrder order, @NonNull short... in) {
        if (in.length == 0) {
            return new byte[0];
        } else {
            int length = in.length;
            byte[] b = new byte[length << 1];
            if (order == null || order == PlatformInfo.BYTE_ORDER) {
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

    public byte[] toBytes(ByteOrder order, @NonNull int... in) {
        if (in.length == 0) {
            return new byte[0];
        } else {
            int length = in.length;
            byte[] b = new byte[length << 2];
            if (order == null || order == PlatformInfo.BYTE_ORDER) {
                PUnsafe.copyMemory(in, PUnsafe.ARRAY_INT_BASE_OFFSET, b, PUnsafe.ARRAY_BYTE_BASE_OFFSET, length << 2);
            } else {
                for (int j = 0; j < length; j++) {
                    PUnsafe.putInt(b, PUnsafe.ARRAY_BYTE_BASE_OFFSET + (j << 2), Integer.reverseBytes(in[j]));
                }
            }
            return b;
        }
    }

    public byte[] toBytes(@NonNull long... in) {
        return toBytes(null, in);
    }

    public byte[] toBytes(ByteOrder order, @NonNull long... in) {
        if (in.length == 0) {
            return new byte[0];
        } else {
            int length = in.length;
            byte[] b = new byte[length << 3];
            if (order == null || order == PlatformInfo.BYTE_ORDER) {
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
        return toShorts(null, in);
    }

    public short[] toShorts(ByteOrder order, @NonNull byte[] in) {
        if (in.length == 0) {
            return new short[0];
        } else {
            int length = in.length >>> 1;
            short[] out = new short[length];
            if (order == null || order == PlatformInfo.BYTE_ORDER) {
                PUnsafe.copyMemory(in, PUnsafe.ARRAY_BYTE_BASE_OFFSET, out, PUnsafe.ARRAY_SHORT_BASE_OFFSET, length << 1);
            } else {
                for (int j = 0; j < length; j++) {
                    out[j] = Short.reverseBytes(PUnsafe.getShort(in, PUnsafe.ARRAY_BYTE_BASE_OFFSET + (j << 1)));
                }
            }
            return out;
        }
    }

    public static int[] toInts(@NonNull byte[] in) {
        return toInts(null, in);
    }

    public static int[] toInts(ByteOrder order, @NonNull byte[] in) {
        if (in.length == 0) {
            return new int[0];
        } else {
            int length = in.length >>> 2;
            int[] out = new int[length];
            if (order == null || order == PlatformInfo.BYTE_ORDER) {
                PUnsafe.copyMemory(in, PUnsafe.ARRAY_BYTE_BASE_OFFSET, out, PUnsafe.ARRAY_INT_BASE_OFFSET, length << 2);
            } else {
                for (int j = 0; j < length; j++) {
                    out[j] = Integer.reverseBytes(PUnsafe.getInt(in, PUnsafe.ARRAY_BYTE_BASE_OFFSET + (j << 2)));
                }
            }
            return out;
        }
    }

    public static long[] toLongs(@NonNull byte[] in) {
        return toLongs(null, in);
    }

    public static long[] toLongs(ByteOrder order, @NonNull byte[] in) {
        if (in.length == 0) {
            return new long[0];
        } else {
            int length = in.length >>> 3;
            long[] out = new long[length];
            if (order == null || order == PlatformInfo.BYTE_ORDER) {
                PUnsafe.copyMemory(in, PUnsafe.ARRAY_BYTE_BASE_OFFSET, out, PUnsafe.ARRAY_LONG_BASE_OFFSET, length << 3);
            } else {
                for (int j = 0; j < length; j++) {
                    out[j] = Long.reverseBytes(PUnsafe.getLong(in, PUnsafe.ARRAY_BYTE_BASE_OFFSET + (j << 3)));
                }
            }
            return out;
        }
    }

    public byte[] toBytes(@NonNull UUID uuid) {
        return toBytes(null, uuid);
    }

    public byte[] toBytes(ByteOrder order, @NonNull UUID uuid) {
        byte[] out = new byte[16];
        if (order == null || order == PlatformInfo.BYTE_ORDER) {
            PUnsafe.putLong(out, PUnsafe.ARRAY_BYTE_BASE_OFFSET, uuid.getMostSignificantBits());
            PUnsafe.putLong(out, PUnsafe.ARRAY_BYTE_BASE_OFFSET + 8, uuid.getLeastSignificantBits());
        } else {
            PUnsafe.putLong(out, PUnsafe.ARRAY_BYTE_BASE_OFFSET, Long.reverseBytes(uuid.getMostSignificantBits()));
            PUnsafe.putLong(out, PUnsafe.ARRAY_BYTE_BASE_OFFSET + 8, Long.reverseBytes(uuid.getLeastSignificantBits()));
        }
        return out;
    }

    public UUID fromBytes(@NonNull byte[] in) {
        return fromBytes(null, in);
    }

    public UUID fromBytes(ByteOrder endianess, @NonNull byte[] in) {
        if (in.length != 16) {
            throw new IllegalArgumentException(String.valueOf(in.length));
        } else if (endianess == null || endianess == PlatformInfo.BYTE_ORDER) {
            return new UUID(
                    PUnsafe.getLong(in, PUnsafe.ARRAY_BYTE_BASE_OFFSET),
                    PUnsafe.getLong(in, PUnsafe.ARRAY_BYTE_BASE_OFFSET + 8)
            );
        } else {
            return new UUID(
                    Long.reverseBytes(PUnsafe.getLong(in, PUnsafe.ARRAY_BYTE_BASE_OFFSET)),
                    Long.reverseBytes(PUnsafe.getLong(in, PUnsafe.ARRAY_BYTE_BASE_OFFSET + 8))
            );
        }
    }
}
