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

import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.random.PRandom;
import net.daporkchop.lib.random.wrapper.PRandomWrapper;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A fast implementation of {@link PRandom} based on Java's {@link java.util.SplittableRandom}.
 * <p>
 * This is NOT thread-safe. Attempting to share an instance of this class among multiple threads is likely to result in duplicate values
 * being returned to multiple threads.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
public final class FastPRandom implements PRandom {
    private static final long GAMMA = 0x9e3779b97f4a7c15L;

    private static final double DOUBLE_UNIT = 0x1.0p-53;
    private static final float  FLOAT_UNIT  = 0x1.0p-24f;

    private static long mix64(long z) {
        z = (z ^ (z >>> 33)) * 0xff51afd7ed558ccdL;
        z = (z ^ (z >>> 33)) * 0xc4ceb9fe1a85ec53L;
        return z ^ (z >>> 33);
    }

    private static int mix32(long z) {
        z = (z ^ (z >>> 33)) * 0xff51afd7ed558ccdL;
        return (int) (((z ^ (z >>> 33)) * 0xc4ceb9fe1a85ec53L) >>> 32);
    }

    private long seed;

    /**
     * Creates a new {@link FastPRandom} instance using a seed based on the current time.
     */
    public FastPRandom() {
        this(mix64(System.currentTimeMillis()) ^ mix64(System.nanoTime()));
    }

    private long nextSeed() {
        return this.seed += GAMMA;
    }

    @Override
    public Random asJava() {
        return new PRandomWrapper(this);
    }

    @Override
    public boolean nextBoolean() {
        return mix32(this.nextSeed()) != 0;
    }

    @Override
    public byte nextByte() {
        return (byte) (mix32(this.nextSeed()) & 0xFF);
    }

    @Override
    public void nextBytes(@NonNull byte[] dst, int start, int length) {
        PorkUtil.assertInRangeLen(dst.length, start, length);
    }

    @Override
    public short nextShort() {
        return (short) (mix32(this.nextSeed()) & 0xFFFF);
    }

    @Override
    public int next(int bits) {
        return mix32(nextSeed()) >>> (bits ^ 0x1F);
    }

    @Override
    public int nextInt() {
        return mix32(this.nextSeed());
    }

    @Override
    public int nextUnsignedInt() {
        return mix32(this.nextSeed()) >>> 1;
    }

    @Override
    public int nextInt(int bound) {
        if (bound <= 0) {
            throw new IllegalArgumentException("bound must be positive");
        }

        int r = mix32(this.nextSeed());
        int m = bound - 1;
        if ((bound & m) == 0) {
            r &= m;
        } else {
            for (int u = r >>> 1; u + m - (r = u % bound) < 0; u = mix32(nextSeed()) >>> 1) ;
        }
        return r;
    }

    @Override
    public int nextInt(int origin, int bound) {
        if (bound <= origin) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        int r = mix32(nextSeed());
        int n = bound - origin;
        int m = n - 1;
        if ((n & m) == 0) {
            r = (r & m) + origin;
        } else if (n > 0) {
            for (int u = r >>> 1; u + m - (r = u % n) < 0; u = mix32(nextSeed()) >>> 1) ;
            r += origin;
        } else {
            while (r < origin || r >= bound) {
                r = mix32(nextSeed());
            }
        }
        return r;
    }

    @Override
    public long next(long bits) {
        return mix64(nextSeed()) >>> (bits ^ 0x3F);
    }

    @Override
    public long nextLong() {
        return mix64(nextSeed());
    }

    @Override
    public long nextUnsignedLong() {
        return mix64(nextSeed()) >>> 1L;
    }

    @Override
    public long nextLong(long bound) {
        if (bound <= 0L) {
            throw new IllegalArgumentException("bound must be positive");
        }

        long r = mix64(nextSeed());
        long m = bound - 1L;
        if ((bound & m) == 0L) {
            r &= m;
        } else {
            for (long u = r >>> 1; u + m - (r = u % bound) < 0L; u = mix64(nextSeed()) >>> 1) ;
        }
        return r;
    }

    @Override
    public long nextLong(int origin, int bound) {
        if (bound <= origin) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        long r = mix64(nextSeed());
        long n = bound - origin, m = n - 1L;
        if ((n & m) == 0L) {
            r = (r & m) + origin;
        } else if (n > 0L) {
            for (long u = r >>> 1L; u + m - (r = u % n) < 0L; u = mix64(nextSeed()) >>> 1L) ;
            r += origin;
        } else {
            while (r < origin || r >= bound) {
                r = mix64(nextSeed());
            }
        }
        return r;
    }

    @Override
    public float nextFloat() {
        return (mix32(nextSeed()) >>> 8) * FLOAT_UNIT;
    }

    @Override
    public float nextFloat(float bound) {
        if (bound <= 0.0f) {
            throw new IllegalArgumentException("bound must be positive");
        }

        float result = (mix32(nextSeed()) >>> 8) * FLOAT_UNIT * bound;
        return (result < bound) ? result : Float.intBitsToFloat(Float.floatToIntBits(bound) - 1);
    }

    @Override
    public float nextFloat(float origin, float bound) {
        if (bound <= origin) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        float result = (mix32(nextSeed()) >>> 8) * FLOAT_UNIT * (bound - origin) + origin;
        return (result < bound) ? result : Float.intBitsToFloat(Float.floatToIntBits(bound) - 1);
    }

    @Override
    public double nextDouble() {
        return (mix64(nextSeed()) >>> 11L) * DOUBLE_UNIT;
    }

    @Override
    public double nextDouble(double bound) {
        if (bound <= 0.0d) {
            throw new IllegalArgumentException("bound must be positive");
        }

        double result = (mix64(nextSeed()) >>> 11L) * DOUBLE_UNIT * bound;
        return (result < bound) ? result : Double.longBitsToDouble(Double.doubleToLongBits(bound) - 1L);
    }

    @Override
    public double nextDouble(double origin, double bound) {
        if (bound <= origin) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        double result = (mix64(nextSeed()) >>> 11L) * DOUBLE_UNIT * (bound - origin) + origin;
        return (result < bound) ? result : Double.longBitsToDouble(Double.doubleToLongBits(bound) - 1L);
    }
}
