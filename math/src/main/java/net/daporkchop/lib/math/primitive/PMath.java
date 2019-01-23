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

package net.daporkchop.lib.math.primitive;

/**
 * Math-related utilities
 *
 * @author DaPorkchop_
 */
public interface PMath {
    static long clamp(long val, long min, long max) {
        return min(max(val, min), max);
    }

    static int clamp(int val, int min, int max) {
        return min(max(val, min), max);
    }

    static short clamp(short val, short min, short max) {
        return min(max(val, min), max);
    }

    static byte clamp(byte val, byte min, byte max) {
        return min(max(val, min), max);
    }

    static float clamp(float val, float min, float max) {
        return min(max(val, min), max);
    }

    static double clamp(double val, double min, double max) {
        return min(max(val, min), max);
    }

    static byte divmod(byte[] number, int firstDigit, int base, int divisor) {
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

    static int floorI(double d) {
        int i = (int) d;
        return d < i ? i - 1 : i;
    }

    static long floorL(double d) {
        long l = (long) d;
        return d < l ? l - 1L : l;
    }

    static int floorI(float d) {
        int i = (int) d;
        return d < i ? i - 1 : i;
    }

    static long floorL(float d) {
        long l = (long) d;
        return d < l ? l - 1L : l;
    }

    static long max(long a, long b) {
        return a > b ? a : b;
    }

    static int max(int a, int b) {
        return a > b ? a : b;
    }

    static short max(short a, short b) {
        return a > b ? a : b;
    }

    static byte max(byte a, byte b) {
        return a > b ? a : b;
    }

    static float max(float a, float b) {
        return a > b ? a : b;
    }

    static double max(double a, double b) {
        return a > b ? a : b;
    }

    static long min(long a, long b) {
        return a > b ? b : a;
    }

    static int min(int a, int b) {
        return a > b ? b : a;
    }

    static short min(short a, short b) {
        return a > b ? b : a;
    }

    static byte min(byte a, byte b) {
        return a > b ? b : a;
    }

    static float min(float a, float b) {
        return a > b ? b : a;
    }

    static double min(double a, double b) {
        return a > b ? b : a;
    }

    static long pow(long val, long exp) {
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

    static int pow(int val, int exp) {
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

    static short pow(short val, short exp) {
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

    static byte pow(byte val, byte exp) {
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

    static float pow(float val, float exp) {
        return (float) powDouble(val, exp);
    }

    static double powDouble(double val, double exp) {
        if (val == 0.0d || exp == 0.0d)   {
            return 0.0d;
        } else {
            return Math.pow(val, exp);
        }
    }

    static int roundUp(int n, int to) {
        return (n + to - 1) / to * to;
    }

    static long roundUp(long n, long to) {
        return (n + to - 1L) / to * to;
    }
}
