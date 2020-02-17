/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.random.impl;

import lombok.NonNull;
import net.daporkchop.lib.common.system.PlatformInfo;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.random.PRandom;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.Random;

/**
 * Base class for fast implementations of {@link PRandom}.
 *
 * @author DaPorkchop_
 */
public abstract class AbstractFastPRandom extends Random implements PRandom {
    protected static final double DOUBLE_UNIT = 0x1.0p-53;
    protected static final float  FLOAT_UNIT  = 0x1.0p-24f;

    protected double nextGaussian = Double.NaN;

    @Override
    public abstract int nextInt();

    @Override
    public abstract long nextLong();

    @Override
    public Random asJava() {
        return this;
    }

    @Override
    public boolean nextBoolean() {
        return this.nextInt() != 0;
    }

    @Override
    public byte nextByte() {
        return (byte) (this.nextInt() & 0xFF);
    }

    @Override
    public void nextBytes(@NonNull byte[] dst) {
        this.nextBytes(dst, 0, dst.length);
    }

    @Override
    public void nextBytes(@NonNull byte[] dst, int start, int length) {
        PorkUtil.assertInRangeLen(dst.length, start, length);
        if (length == 0)   {
            return;
        }

        if (PlatformInfo.IS_64BIT)  {
            this.nextBytes64(dst, start, length);
        } else if (PlatformInfo.IS_32BIT)   {
            this.nextBytes32(dst, start, length);
        } else {
            this.nextBytesDefault(dst, start, length);
        }
    }

    private void nextBytes64(byte[] dst, int start, int length) {
        int i = start;
        int end = start + length;
        if (!PlatformInfo.UNALIGNED && (i & 0x7) != 0) {
            //pad to next word boundary
            this.ne
        }

        int words = 0;
    }

    private void nextBytesDefault64(byte[] dst, int start, int length) {
        for (int i = start; i < length; )   {
            for (long rnd = this.nextLong(), n = Math.min(length - i, 8); n-- > 0; rnd >>>= 8) {
                dst[i++] = (byte) rnd;
            }
        }
    }

    private void nextBytes32(byte[] dst, int start, int length) {
        //TODO
    }

    private void nextBytesDefault(byte[] dst, int start, int length) {
        for (int i = start; i < length; )   {
            for (int rnd = this.nextInt(), n = Math.min(length - i, 4); n-- > 0; rnd >>>= 8) {
                dst[i++] = (byte) rnd;
            }
        }
    }

    @Override
    public short nextShort() {
        return (short) (this.nextInt() & 0xFFFF);
    }

    @Override
    public int next(int bits) {
        return this.nextInt() >>> (bits ^ 0x1F);
    }

    @Override
    public int nextUnsignedInt() {
        return this.nextInt() >>> 1;
    }

    @Override
    public int nextInt(int bound) {
        if (bound <= 0) {
            throw new IllegalArgumentException("bound must be positive");
        }

        int r = this.nextInt();
        int m = bound - 1;
        if ((bound & m) == 0) {
            r &= m;
        } else {
            for (int u = r >>> 1; u + m - (r = u % bound) < 0; u = this.nextInt() >>> 1) ;
        }
        return r;
    }

    @Override
    public int nextInt(int origin, int bound) {
        if (bound <= origin) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        int r = this.nextInt();
        int n = bound - origin;
        int m = n - 1;
        if ((n & m) == 0) {
            r = (r & m) + origin;
        } else if (n > 0) {
            for (int u = r >>> 1; u + m - (r = u % n) < 0; u = this.nextInt() >>> 1) ;
            r += origin;
        } else {
            while (r < origin || r >= bound) {
                r = this.nextInt();
            }
        }
        return r;
    }

    @Override
    public long next(long bits) {
        return this.nextLong() >>> (bits ^ 0x3F);
    }

    @Override
    public long nextUnsignedLong() {
        return this.nextLong() >>> 1L;
    }

    @Override
    public long nextLong(long bound) {
        if (bound <= 0L) {
            throw new IllegalArgumentException("bound must be positive");
        }

        long r = this.nextLong();
        long m = bound - 1L;
        if ((bound & m) == 0L) {
            r &= m;
        } else {
            for (long u = r >>> 1; u + m - (r = u % bound) < 0L; u = this.nextLong() >>> 1) ;
        }
        return r;
    }

    @Override
    public long nextLong(int origin, int bound) {
        if (bound <= origin) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        long r = this.nextLong();
        long n = bound - origin, m = n - 1L;
        if ((n & m) == 0L) {
            r = (r & m) + origin;
        } else if (n > 0L) {
            for (long u = r >>> 1L; u + m - (r = u % n) < 0L; u = this.nextLong() >>> 1L) ;
            r += origin;
        } else {
            while (r < origin || r >= bound) {
                r = this.nextLong();
            }
        }
        return r;
    }

    @Override
    public float nextFloat() {
        return (this.nextInt() >>> 8) * FLOAT_UNIT;
    }

    @Override
    public float nextFloat(float bound) {
        if (bound <= 0.0f) {
            throw new IllegalArgumentException("bound must be positive");
        }

        float result = (this.nextInt() >>> 8) * FLOAT_UNIT * bound;
        return (result < bound) ? result : Float.intBitsToFloat(Float.floatToIntBits(bound) - 1);
    }

    @Override
    public float nextFloat(float origin, float bound) {
        if (bound <= origin) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        float result = (this.nextInt() >>> 8) * FLOAT_UNIT * (bound - origin) + origin;
        return (result < bound) ? result : Float.intBitsToFloat(Float.floatToIntBits(bound) - 1);
    }

    @Override
    public float nextGaussianFloat() {
        return (float) this.nextGaussian();
    }

    @Override
    public double nextDouble() {
        return (this.nextLong() >>> 11L) * DOUBLE_UNIT;
    }

    @Override
    public double nextDouble(double bound) {
        if (bound <= 0.0d) {
            throw new IllegalArgumentException("bound must be positive");
        }

        double result = (this.nextLong() >>> 11L) * DOUBLE_UNIT * bound;
        return (result < bound) ? result : Double.longBitsToDouble(Double.doubleToLongBits(bound) - 1L);
    }

    @Override
    public double nextDouble(double origin, double bound) {
        if (bound <= origin) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        double result = (this.nextLong() >>> 11L) * DOUBLE_UNIT * (bound - origin) + origin;
        return (result < bound) ? result : Double.longBitsToDouble(Double.doubleToLongBits(bound) - 1L);
    }

    @Override
    public double nextGaussianDouble() {
        double nextGaussianDouble = this.nextGaussian;
        if (!Double.isNaN(nextGaussianDouble)) {
            this.nextGaussian = Double.NaN;
            return nextGaussianDouble;
        }

        double v1;
        double v2;
        double s;
        do {
            v1 = 2.0d * this.nextDouble() - 1.0d;
            v2 = 2.0d * this.nextDouble() - 1.0d;
            s = v1 * v1 + v2 * v2;
        } while (s >= 1 || s == 0);

        double multiplier = StrictMath.sqrt(-2.0d * StrictMath.log(s) / s);
        this.nextGaussian = v2 * multiplier;
        return v1 * multiplier;
    }

    @Override
    public double nextGaussian() {
        return this.nextGaussianDouble();
    }
}
