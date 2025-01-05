/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2025 DaPorkchop_
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
 */

package net.daporkchop.lib.common.math;

import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.annotation.FastMath;
import net.daporkchop.lib.common.annotation.param.NotNegative;

import static java.lang.Math.*;

/**
 * A number of helper math functions.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PMath {
    public static byte clamp(byte val, byte min, byte max) {
        return val < min ? min : val > max ? max : val;
    }

    public static short clamp(short val, short min, short max) {
        return val < min ? min : val > max ? max : val;
    }

    public static int clamp(int val, int min, int max) {
        return min(max(val, min), max);
    }

    public static long clamp(long val, long min, long max) {
        return min(max(val, min), max);
    }

    public static float clamp(float val, float min, float max) {
        return min(max(val, min), max);
    }

    public static double clamp(double val, double min, double max) {
        return min(max(val, min), max);
    }

    public static byte divmod(byte[] number, int firstDigit, int base, int divisor) {
        // this is just long division which accounts for the base of the input digits
        int remainder = 0;
        for (int i = firstDigit; i < number.length; i++) {
            int digit = (int) number[i] & 0xFF;
            int temp = remainder * base + digit;
            number[i] = (byte) (temp / divisor);
            remainder = temp % divisor;
        }
        return (byte) remainder;
    }

    /**
     * Converts the given floating-point value to an integer, rounding towards negative infinity.
     *
     * @param f the floating-point value
     * @return the rounded integer value
     */
    public static int floorI(float f) {
        return (int) Math.floor(f);
    }

    /**
     * Converts the given floating-point value to an integer, rounding towards negative infinity.
     *
     * @param f the floating-point value
     * @return the rounded integer value
     */
    public static long floorL(float f) {
        return (long) Math.floor(f);
    }

    /**
     * Converts the given floating-point value to an integer, rounding towards negative infinity.
     *
     * @param d the floating-point value
     * @return the rounded integer value
     */
    public static int floorI(double d) {
        return (int) Math.floor(d);
    }

    /**
     * Converts the given floating-point value to an integer, rounding towards negative infinity.
     *
     * @param d the floating-point value
     * @return the rounded integer value
     */
    public static long floorL(double d) {
        return (long) Math.floor(d);
    }

    /**
     * Converts the given floating-point value to an integer, rounding towards positive infinity.
     *
     * @param f the floating-point value
     * @return the rounded integer value
     */
    public static int ceilI(float f) {
        return (int) Math.ceil(f);
    }

    /**
     * Converts the given floating-point value to an integer, rounding towards positive infinity.
     *
     * @param f the floating-point value
     * @return the rounded integer value
     */
    public static long ceilL(float f) {
        return (long) Math.ceil(f);
    }

    /**
     * Converts the given floating-point value to an integer, rounding towards positive infinity.
     *
     * @param d the floating-point value
     * @return the rounded integer value
     */
    public static int ceilI(double d) {
        return (int) Math.ceil(d);
    }

    /**
     * Converts the given floating-point value to an integer, rounding towards positive infinity.
     *
     * @param d the floating-point value
     * @return the rounded integer value
     */
    public static long ceilL(double d) {
        return (long) Math.ceil(d);
    }

    @Deprecated
    public static int roundI(float f) {
        return Math.round(f);
    }

    @Deprecated
    public static long roundL(float f) {
        return Math.round(f);
    }

    @Deprecated
    public static int roundI(double d) {
        return (int) Math.round(d);
    }

    @Deprecated
    public static long roundL(double d) {
        return Math.round(d);
    }

    public static long pow(long val, long exp) {
        if (val == 0 || exp == 0) {
            return 0;
        } else {
            long a = val;
            for (; a > 0; a--) {
                a *= val;
            }
            return a;
        }
    }

    public static int pow(int val, int exp) {
        if (val == 0 || exp == 0) {
            return 0;
        } else {
            int a = val;
            for (int i = exp; i > 0; i--) {
                a *= val;
            }
            return a;
        }
    }

    public static short pow(short val, short exp) {
        if (val == 0 || exp == 0) {
            return 0;
        } else {
            short a = val;
            for (short i = exp; i > 0; i--) {
                a *= val;
            }
            return a;
        }
    }

    public static byte pow(byte val, byte exp) {
        if (val == 0 || exp == 0) {
            return 0;
        } else {
            byte a = val;
            for (byte i = val; i > 0; i--) {
                a *= val;
            }
            return a;
        }
    }

    public static float pow(float val, float exp) {
        return (float) powDouble(val, exp);
    }

    public static double powDouble(double val, double exp) {
        if (val == 0.0d || exp == 0.0d) {
            return 0.0d;
        } else {
            return Math.pow(val, exp);
        }
    }

    public static int roundUp(int n, int to) {
        return (n + to - 1) / to * to;
    }

    public static long roundUp(long n, long to) {
        return (n + to - 1L) / to * to;
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    @Deprecated
    public static int lerpI(int a, int b, float t) {
        return floorI(a + (b - a) * t);
    }

    @Deprecated
    public static int lerpI(int a, int b, double t) {
        return floorI(a + (b - a) * t);
    }

    public static long mix64(long z) {
        z = (z ^ (z >>> 33L)) * 0xFF51AFD7ED558CCDL;
        z = (z ^ (z >>> 33L)) * 0xC4CEB9FE1A85EC53L;
        return z ^ (z >>> 33L);
    }

    public static int mix32(long z) {
        z = (z ^ (z >>> 33L)) * 0xFF51AFD7ED558CCDL;
        return (int) (((z ^ (z >>> 33L)) * 0xC4CEB9FE1A85EC53L) >>> 32L);
    }

    /**
     * Computes the new capacity of a buffer/array when growing it to fit at least 1 additional element.
     *
     * @param oldCapacity the previous capacity
     * @return the new capacity, guaranteed to be at least {@code oldCapacity + 1}
     */
    public static int growCapacity1(@NotNegative int oldCapacity) {
        //this could theoretically be optimized more in the future
        return growCapacityBy(oldCapacity, 1);
    }

    /**
     * Computes the new capacity of a buffer/array when growing it to fit at least 1 additional element.
     *
     * @param oldCapacity the previous capacity
     * @return the new capacity, guaranteed to be at least {@code oldCapacity + 1}
     */
    public static long growCapacity1(@NotNegative long oldCapacity) {
        //this could theoretically be optimized more in the future
        return growCapacityBy(oldCapacity, 1L);
    }

    /**
     * Computes the new capacity of a buffer/array when growing it to fit the given number of additional elements.
     *
     * @param oldCapacity the previous capacity
     * @param increment   the minimum amount by which the capacity should be increased
     * @return the new capacity, guaranteed to be at least {@code oldCapacity + increment}
     */
    public static int growCapacityBy(@NotNegative int oldCapacity, @NotNegative int increment) {
        if ((oldCapacity | increment | (oldCapacity + increment)) < 0) {
            throw new IllegalArgumentException(badGrowCapacityBy(oldCapacity, increment));
        }

        //'oldCapacity + increment' is never negative, therefore this can never result in undefined behavior:
        int roundedUp = BinMath.unsignedBitCeil(oldCapacity + increment);
        if (roundedUp < 0) {
            //the result overflowed to Integer.MIN_VALUE! however, since we know that 'oldCapacity + increment' is positive, we'll simply return
            //  Integer.MAX_VALUE as the largest possible size.
            return Integer.MAX_VALUE;
        }

        return roundedUp;
    }

    /**
     * Computes the new capacity of a buffer/array when growing it to fit the given number of additional elements.
     *
     * @param oldCapacity the previous capacity
     * @param increment   the minimum amount by which the capacity should be increased
     * @return the new capacity, guaranteed to be at least {@code oldCapacity + increment}
     */
    public static long growCapacityBy(@NotNegative long oldCapacity, @NotNegative long increment) {
        if ((oldCapacity | increment | (oldCapacity + increment)) < 0L) {
            throw new IllegalArgumentException(badGrowCapacityBy(oldCapacity, increment));
        }

        //'oldCapacity + increment' is never negative, therefore this can never result in undefined behavior:
        long roundedUp = BinMath.unsignedBitCeil(oldCapacity + increment);
        if (roundedUp < 0L) {
            //the result overflowed to Long.MIN_VALUE! however, since we know that 'oldCapacity + increment' is positive, we'll simply return
            //  Long.MAX_VALUE as the largest possible size.
            return Long.MAX_VALUE;
        }

        return roundedUp;
    }

    private static String badGrowCapacityBy(long oldCapacity, long increment) {
        if (oldCapacity < 0) {
            return "negative oldCapacity: " + oldCapacity;
        } else if (increment < 0) {
            return "negative increment: " + increment;
        } else { //oldCapacity + increment would overflow
            return "integer overflow: " + oldCapacity + " + " + increment;
        }
    }

    /**
     * Checks if the given {@code float}s are equal, as returned by {@link Float#equals}.
     * <p>
     * This differs from the {@code ==} operator in two cases:
     * <ul>
     *     <li>If both arguments are NaN, the result is {@code true}. The NaN payload is ignored.</li>
     *     <li>If one argument is {@code +0.0} and the other is {@code -0.0}, the result is {@code false}.</li>
     * </ul>
     *
     * @param a a {@code float}
     * @param b a {@code float}
     * @return {@code true} if the given {@code float}s are equal
     */
    public static boolean floatEquals(float a, float b) {
        return Float.floatToIntBits(a) == Float.floatToIntBits(b);
    }

    /**
     * Checks if the given {@code double}s are equal, as returned by {@link Double#equals}.
     * <p>
     * This differs from the {@code ==} operator in two cases:
     * <ul>
     *     <li>If both arguments are NaN, the result is {@code true}. The NaN payload is ignored.</li>
     *     <li>If one argument is {@code +0.0} and the other is {@code -0.0}, the result is {@code false}.</li>
     * </ul>
     *
     * @param a a {@code double}
     * @param b a {@code double}
     * @return {@code true} if the given {@code double}s are equal
     */
    public static boolean doubleEquals(double a, double b) {
        return Double.doubleToLongBits(a) == Double.doubleToLongBits(b);
    }

    /**
     * Checks if the given {@code float}s are bitwise equal.
     * <p>
     * This differs from the {@code ==} operator in two cases:
     * <ul>
     *     <li>If both arguments are NaN, the result is {@code true} iff both NaNs have the same payload.</li>
     *     <li>If one argument is {@code +0.0} and the other is {@code -0.0}, the result is {@code false}.</li>
     * </ul>
     *
     * @param a a {@code float}
     * @param b a {@code float}
     * @return {@code true} if the given {@code float}s are bitwise equal
     */
    public static boolean floatBitwiseEquals(float a, float b) {
        return Float.floatToRawIntBits(a) == Float.floatToRawIntBits(b);
    }

    /**
     * Checks if the given {@code double}s are bitwise equal.
     * <p>
     * This differs from the {@code ==} operator in two cases:
     * <ul>
     *     <li>If both arguments are NaN, the result is {@code true} iff both NaNs have the same payload.</li>
     *     <li>If one argument is {@code +0.0} and the other is {@code -0.0}, the result is {@code false}.</li>
     * </ul>
     *
     * @param a a {@code double}
     * @param b a {@code double}
     * @return {@code true} if the given {@code double}s are bitwise equal
     */
    public static boolean doubleBitwiseEquals(double a, double b) {
        return Double.doubleToRawLongBits(a) == Double.doubleToRawLongBits(b);
    }

    /**
     * Returns {@code a * b + c}, except that the operation may be performed with higher precision.
     *
     * @param a a value
     * @param b a value
     * @param c a value
     * @return {@code a * b + c}
     */
    @FastMath
    public static float fmaFast(float a, float b, float c) {
        return a * b + c;
    }

    /**
     * Returns {@code a * b + c}, except that the operation may be performed with higher precision.
     *
     * @param a a value
     * @param b a value
     * @param c a value
     * @return {@code a * b + c}
     */
    @FastMath
    public static double fmaFast(double a, double b, double c) {
        return a * b + c;
    }
}
