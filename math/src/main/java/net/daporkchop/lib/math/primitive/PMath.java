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

import lombok.experimental.UtilityClass;

/**
 * A number of helper math functions.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PMath {
    public long clamp(long val, long min, long max) {
        return min(max(val, min), max);
    }

    public int clamp(int val, int min, int max) {
        return min(max(val, min), max);
    }

    public short clamp(short val, short min, short max) {
        return min(max(val, min), max);
    }

    public byte clamp(byte val, byte min, byte max) {
        return min(max(val, min), max);
    }

    public float clamp(float val, float min, float max) {
        return min(max(val, min), max);
    }

    public double clamp(double val, double min, double max) {
        return min(max(val, min), max);
    }

    public byte divmod(byte[] number, int firstDigit, int base, int divisor) {
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

    public int floorI(double d) {
        int i = (int) d;
        return d < i ? i - 1 : i;
    }

    public long floorL(double d) {
        long l = (long) d;
        return d < l ? l - 1L : l;
    }

    public int floorI(float f) {
        int i = (int) f;
        return f < i ? i - 1 : i;
    }

    public long floorL(float f) {
        long l = (long) f;
        return f < l ? l - 1L : l;
    }

    public int ceilI(double d) {
        int i = (int) d;
        return d < i ? i : i + 1;
    }

    public long ceilL(double d) {
        long l = (long) d;
        return d < l ? l : l + 1L;
    }

    public int ceilI(float f) {
        int i = (int) f;
        return f < i ? i : i + 1;
    }

    public long ceilL(float f) {
        long l = (long) f;
        return f < l ? l : l + 1L;
    }

    public int roundI(double d) {
        return (int) Math.round(d);
    }

    public long roundL(double d) {
        return Math.round(d);
    }

    public int roundI(float f) {
        return Math.round(f);
    }

    public long roundL(float f) {
        return Math.round(f);
    }

    public long max(long a, long b) {
        return a > b ? a : b;
    }

    public int max(int a, int b) {
        return a > b ? a : b;
    }

    public short max(short a, short b) {
        return a > b ? a : b;
    }

    public byte max(byte a, byte b) {
        return a > b ? a : b;
    }

    public float max(float a, float b) {
        return a > b ? a : b;
    }

    public double max(double a, double b) {
        return a > b ? a : b;
    }

    public long min(long a, long b) {
        return a > b ? b : a;
    }

    public int min(int a, int b) {
        return a > b ? b : a;
    }

    public short min(short a, short b) {
        return a > b ? b : a;
    }

    public byte min(byte a, byte b) {
        return a > b ? b : a;
    }

    public float min(float a, float b) {
        return a > b ? b : a;
    }

    public double min(double a, double b) {
        return a > b ? b : a;
    }

    public long pow(long val, long exp) {
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

    public int pow(int val, int exp) {
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

    public short pow(short val, short exp) {
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

    public byte pow(byte val, byte exp) {
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

    public float pow(float val, float exp) {
        return (float) powDouble(val, exp);
    }

    public double powDouble(double val, double exp) {
        if (val == 0.0d || exp == 0.0d)   {
            return 0.0d;
        } else {
            return Math.pow(val, exp);
        }
    }

    public int roundUp(int n, int to) {
        return (n + to - 1) / to * to;
    }

    public long roundUp(long n, long to) {
        return (n + to - 1L) / to * to;
    }
}
