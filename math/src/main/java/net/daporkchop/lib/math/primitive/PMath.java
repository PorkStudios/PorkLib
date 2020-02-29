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

package net.daporkchop.lib.math.primitive;

import lombok.experimental.UtilityClass;

/**
 * A number of helper math functions.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PMath {
    public static long clamp(long val, long min, long max) {
        return min(max(val, min), max);
    }

    public static int clamp(int val, int min, int max) {
        return min(max(val, min), max);
    }

    public static short clamp(short val, short min, short max) {
        return min(max(val, min), max);
    }

    public static byte clamp(byte val, byte min, byte max) {
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

    public static int floorI(float f) {
        int i = (int) f;
        return f < i ? i - 1 : i;
    }

    public static long floorL(float f) {
        long l = (long) f;
        return f < l ? l - 1L : l;
    }

    public static int floorI(double d) {
        int i = (int) d;
        return d < i ? i - 1 : i;
    }

    public static long floorL(double d) {
        long l = (long) d;
        return d < l ? l - 1L : l;
    }

    public static int ceilI(float f) {
        int i = (int) f;
        return f < i ? i : i + 1;
    }

    public static long ceilL(float f) {
        long l = (long) f;
        return f < l ? l : l + 1L;
    }

    public static int ceilI(double d) {
        int i = (int) d;
        return d < i ? i : i + 1;
    }

    public static long ceilL(double d) {
        long l = (long) d;
        return d < l ? l : l + 1L;
    }

    public static int roundI(float f) {
        return Math.round(f);
    }

    public static long roundL(float f) {
        return Math.round(f);
    }

    public static int roundI(double d) {
        return (int) Math.round(d);
    }

    public static long roundL(double d) {
        return Math.round(d);
    }

    public static long max(long a, long b) {
        return a > b ? a : b;
    }

    public static int max(int a, int b) {
        return a > b ? a : b;
    }

    public static short max(short a, short b) {
        return a > b ? a : b;
    }

    public static byte max(byte a, byte b) {
        return a > b ? a : b;
    }

    public static float max(float a, float b) {
        return a > b ? a : b;
    }

    public static double max(double a, double b) {
        return a > b ? a : b;
    }

    public static long min(long a, long b) {
        return a > b ? b : a;
    }

    public static int min(int a, int b) {
        return a > b ? b : a;
    }

    public static short min(short a, short b) {
        return a > b ? b : a;
    }

    public static byte min(byte a, byte b) {
        return a > b ? b : a;
    }

    public static float min(float a, float b) {
        return a > b ? b : a;
    }

    public static double min(double a, double b) {
        return a > b ? b : a;
    }

    public static long pow(long val, long exp) {
        if (val == 0 || exp == 0)   {
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
        if (val == 0 || exp == 0)   {
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
        if (val == 0 || exp == 0)   {
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
        if (val == 0 || exp == 0)   {
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
        if (val == 0.0d || exp == 0.0d)   {
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

    public static float lerp(float a, float b, float t)    {
        return a + (b - a) * t;
    }

    public static double lerp(double a, double b, double t)    {
        return a + (b - a) * t;
    }

    public static int lerpI(int a, int b, float t)    {
        return floorI(a + (b - a) * t);
    }

    public static int lerpI(int a, int b, double t)    {
        return floorI(a + (b - a) * t);
    }
}
