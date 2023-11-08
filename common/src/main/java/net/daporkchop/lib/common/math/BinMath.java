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

package net.daporkchop.lib.common.math;

import lombok.experimental.UtilityClass;

/**
 * @author DaPorkchop_
 */
@UtilityClass
public class BinMath {
    public static boolean isPow2(long value) {
        return value != 0L && (value & (value - 1L)) == 0L;
    }

    public static boolean isPow2(int value) {
        return value != 0 && (value & (value - 1)) == 0;
    }

    public static boolean isPow2(short value) {
        return value != 0 && (value & (value - 1)) == 0;
    }

    public static boolean isPow2(byte value) {
        return value != 0 && (value & (value - 1)) == 0;
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

    public static int roundToNearestPowerOf2(int value) {
        int i = value - 1;
        i |= i >>> 1;
        i |= i >>> 2;
        i |= i >>> 4;
        i |= i >>> 8;
        i |= i >>> 16;
        return i + 1;
    }

    public static short roundToNearestPowerOf2(short value) {
        short s = (short) (value - 1);
        s |= s >>> 1;
        s |= s >>> 2;
        s |= s >>> 4;
        s |= s >>> 8;
        return (short) (s + 1);
    }

    public static int getFromFlags(int... flags)   {
        int i = 0;
        for (int flag : flags)  {
            i |= 1 << flag;
        }
        return i;
    }

    public static int setFlag(int i, int flag) {
        return i | (1 << flag);
    }

    public static boolean getFlag(int i, int flag)    {
        return (i & (1 << flag)) != 0;
    }

    public static long packXY(int x, int y) {
        return (Integer.toUnsignedLong(x) << 32L) | Integer.toUnsignedLong(y);
    }

    public static int unpackX(long packed)   {
        return (int) (packed >>> 32L);
    }

    public static int unpackY(long packed)   {
        return (int) packed;
    }
}
