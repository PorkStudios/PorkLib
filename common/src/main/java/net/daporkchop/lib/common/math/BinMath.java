/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2024 DaPorkchop_
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

package net.daporkchop.lib.common.math;

import lombok.experimental.UtilityClass;

/**
 * @author DaPorkchop_
 */
@UtilityClass
public class BinMath {
    /**
     * @deprecated use {@link #hasSingleBit(long)}
     */
    @Deprecated
    public static boolean isPow2(long value) {
        return hasSingleBit(value);
    }

    /**
     * @deprecated use {@link #hasSingleBit(int)}
     */
    @Deprecated
    public static boolean isPow2(int value) {
        return hasSingleBit(value);
    }

    /**
     * @deprecated use {@link #hasSingleBit(short)}
     */
    @Deprecated
    public static boolean isPow2(short value) {
        return hasSingleBit(value);
    }

    /**
     * @deprecated use {@link #hasSingleBit(byte)}
     */
    @Deprecated
    public static boolean isPow2(byte value) {
        return hasSingleBit(value);
    }

    /**
     * Checks if the given value is an integral power of two.
     *
     * @param value the value to check
     * @return {@code true} if the given value is an integral power of two, {@code false} otherwise
     */
    public static boolean hasSingleBit(byte value) {
        return hasSingleBit(Byte.toUnsignedInt(value));
    }

    /**
     * Checks if the given value is an integral power of two.
     *
     * @param value the value to check
     * @return {@code true} if the given value is an integral power of two, {@code false} otherwise
     */
    public static boolean hasSingleBit(short value) {
        return hasSingleBit(Short.toUnsignedInt(value));
    }

    /**
     * Checks if the given value is an integral power of two.
     *
     * @param value the value to check
     * @return {@code true} if the given value is an integral power of two, {@code false} otherwise
     */
    public static boolean hasSingleBit(int value) {
        //popcnt is a single instruction on virtually all relevant x86-64 machines (it was introduced with SSE4.2).
        //  ARM machines may still benefit from the old 'value != 0 && (value & (value - 1)) == 0' approach... maybe we can auto-detect?
        return Integer.bitCount(value) == 1;
    }

    /**
     * Checks if the given value is an integral power of two.
     *
     * @param value the value to check
     * @return {@code true} if the given value is an integral power of two, {@code false} otherwise
     */
    public static boolean hasSingleBit(long value) {
        return Long.bitCount(value) == 1;
    }

    /**
     * Returns the smallest integral power of two not less than the given <strong>unsigned</strong> value.
     * <p>
     * Examples:
     * <blockquote><pre>{@code
     * unsignedBitCeil(0) == 1
     * unsignedBitCeil(1) == 1
     * unsignedBitCeil(2) == 2
     * unsignedBitCeil(3) == 4
     * unsignedBitCeil(4) == 4
     * unsignedBitCeil(5) == 8
     * unsignedBitCeil(0x7FFFFFFF) == 0x80000000
     * unsignedBitCeil(0x80000000) == 0x80000000
     * unsignedBitCeil(0x80000001) == &lt;undefined&gt;
     * unsignedBitCeil(-1) == &lt;undefined&gt;
     * }</pre></blockquote>
     *
     * @param value value (treated as an unsigned integer)
     * @return the smallest integral power of two not less than the given value, or an undefined value if the value cannot be represented
     */
    public static int unsignedBitCeil(int value) {
        int n = Integer.SIZE - Integer.numberOfLeadingZeros(value - 1);
        assert n != Integer.SIZE || value == 0 : value;
        return 1 << n;
    }

    /**
     * Returns the smallest integral power of two not less than the given <strong>unsigned</strong> value.
     * <p>
     * Examples:
     * <blockquote><pre>{@code
     * unsignedBitCeil(0L) == 1L
     * unsignedBitCeil(1L) == 1L
     * unsignedBitCeil(2L) == 2L
     * unsignedBitCeil(3L) == 4L
     * unsignedBitCeil(4L) == 4L
     * unsignedBitCeil(5L) == 8L
     * unsignedBitCeil(0x7FFFFFFFFFFFFFFFL) == 0x8000000000000000L
     * unsignedBitCeil(0x8000000000000000L) == 0x8000000000000000L
     * unsignedBitCeil(0x8000000000000001L) == &lt;undefined&gt;
     * unsignedBitCeil(-1L) == &lt;undefined&gt;
     * }</pre></blockquote>
     *
     * @param value value (treated as an unsigned integer)
     * @return the smallest integral power of two not less than the given value, or an undefined value if the value cannot be represented
     */
    public static long unsignedBitCeil(long value) {
        int n = Long.SIZE - Long.numberOfLeadingZeros(value - 1L);
        assert n != Long.SIZE || value == 0L : value;
        return 1L << n;
    }

    /**
     * Gets the minimum number of bits required to store a given number
     *
     * @param value the number to store
     * @return the minimum number of bits required
     */
    public static int getNumBitsNeededFor(int value) {
        int count = 0;
        while (value > 0) {
            count++;
            value = value >> 1;
        }
        return count;
    }

    /**
     * @deprecated use {@link #unsignedBitCeil(long)}
     */
    @Deprecated
    public static long roundToNearestPowerOf2(long value) {
        long l = value - 1L;
        l |= l >>> 1L;
        l |= l >>> 2L;
        l |= l >>> 4L;
        l |= l >>> 8L;
        l |= l >>> 16L;
        l |= l >>> 32L;
        return l + 1L;
    }

    /**
     * @deprecated use {@link #unsignedBitCeil(int)}
     */
    @Deprecated
    public static int roundToNearestPowerOf2(int value) {
        int i = value - 1;
        i |= i >>> 1;
        i |= i >>> 2;
        i |= i >>> 4;
        i |= i >>> 8;
        i |= i >>> 16;
        return i + 1;
    }

    public static int getFromFlags(int... flags) {
        int i = 0;
        for (int flag : flags) {
            i |= 1 << flag;
        }
        return i;
    }

    public static int setFlag(int i, int flag) {
        return i | (1 << flag);
    }

    public static boolean getFlag(int i, int flag) {
        return (i & (1 << flag)) != 0;
    }

    public static long packXY(int x, int y) {
        return (Integer.toUnsignedLong(x) << 32L) | Integer.toUnsignedLong(y);
    }

    public static int unpackX(long packed) {
        return (int) (packed >>> 32L);
    }

    public static int unpackY(long packed) {
        return (int) packed;
    }
}
