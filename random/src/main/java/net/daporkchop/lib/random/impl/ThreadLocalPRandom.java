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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.random.PRandom;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * Functionally identical to Java's built-in {@link ThreadLocalRandom}. However, many method implementations are copied to
 * this class to ensure that performance remains optimal.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThreadLocalPRandom extends Random implements PRandom {
    private static final long SEED  = PUnsafe.pork_getOffset(Thread.class, "threadLocalRandomSeed");
    private static final long GAMMA = 0x9e3779b97f4a7c15L;

    private static final double DOUBLE_UNIT = 0x1.0p-53;
    private static final float  FLOAT_UNIT  = 0x1.0p-24f;

    private static final ThreadLocalPRandom INSTANCE = new ThreadLocalPRandom();

    public static ThreadLocalPRandom current() {
        ThreadLocalRandom.current(); //init thread local state
        return INSTANCE;
    }

    private static long mix64(long z) {
        z = (z ^ (z >>> 33)) * 0xff51afd7ed558ccdL;
        z = (z ^ (z >>> 33)) * 0xc4ceb9fe1a85ec53L;
        return z ^ (z >>> 33);
    }

    private static int mix32(long z) {
        z = (z ^ (z >>> 33)) * 0xff51afd7ed558ccdL;
        return (int) (((z ^ (z >>> 33)) * 0xc4ceb9fe1a85ec53L) >>> 32);
    }

    private static long nextSeed() {
        Thread t = Thread.currentThread();
        long r = PUnsafe.getLong(t, SEED) + GAMMA;
        PUnsafe.putLong(t, SEED, r);
        return r;
    }

    @Override
    public Random asJava() {
        return ThreadLocalRandom.current();
    }

    @Override
    public void setSeed(long seed) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean nextBoolean() {
        return mix32(nextSeed()) < 0;
    }

    @Override
    public byte nextByte() {
        return (byte) (mix32(nextSeed()) & 0xFF);
    }

    @Override
    public void nextBytes(@NonNull byte[] dst) {
        ThreadLocalRandom.current().nextBytes(dst);
    }

    @Override
    public void nextBytes(@NonNull byte[] dst, int start, int length) {
        PorkUtil.assertInRangeLen(dst.length, start, length);

        //optimization for filling the whole array
        if (start == 0 && length == dst.length) {
            ThreadLocalRandom.current().nextBytes(dst);
            return;
        }

        for (int i = start; i < length; ) {
            for (long rnd = this.nextLong(), n = Math.min(length - i, Long.SIZE / Byte.SIZE); n-- > 0; rnd >>= Byte.SIZE) {
                dst[i++] = (byte) rnd;
            }
        }
    }

    @Override
    public short nextShort() {
        return (short) (mix32(nextSeed()) & 0xFFFF);
    }

    @Override
    public int next(int bits) {
        //ThreadLocalRandom has the following comment:
        // We must define this, but never use it.
        //why not? need to figure that out...
        return mix32(nextSeed()) >>> (bits ^ 0x1F);
    }

    @Override
    public int nextInt() {
        return mix32(nextSeed());
    }

    @Override
    public int nextUnsignedInt() {
        return mix32(nextSeed()) >>> 1;
    }

    @Override
    public int nextInt(int bound) {
        if (bound <= 0) {
            throw new IllegalArgumentException("bound must be positive");
        }

        int r = mix32(nextSeed());
        int m = bound - 1;
        if ((bound & m) == 0) {
            r &= m;
        } else {
            for (int u = r >>> 1; u + m - (r = u % bound) < 0; u = mix32(nextSeed()) >>> 1) ;
        }
        return r;
    }

    @Override
    public int nextInt(int min, int max) {
        if (max <= min) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        int r = mix32(nextSeed());
        int n = max - min;
        int m = n - 1;
        if ((n & m) == 0) {
            r = (r & m) + min;
        } else if (n > 0) {
            for (int u = r >>> 1; u + m - (r = u % n) < 0; u = mix32(nextSeed()) >>> 1) ;
            r += min;
        } else {
            while (r < min || r >= max) {
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
    public long nextLong(int min, int max) {
        if (max <= min) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        long r = mix64(nextSeed());
        long n = max - min, m = n - 1L;
        if ((n & m) == 0L) {
            r = (r & m) + min;
        } else if (n > 0L) {
            for (long u = r >>> 1L; u + m - (r = u % n) < 0L; u = mix64(nextSeed()) >>> 1L) ;
            r += min;
        } else {
            while (r < min || r >= max) {
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
    public float nextFloat(float min, float max) {
        if (max <= min) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        float result = (mix32(nextSeed()) >>> 8) * FLOAT_UNIT * (max - min) + min;
        return (result < max) ? result : Float.intBitsToFloat(Float.floatToIntBits(max) - 1);
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
    public double nextDouble(double min, double max) {
        if (max <= min) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        double result = (mix64(nextSeed()) >>> 11L) * DOUBLE_UNIT * (max - min) + min;
        return (result < max) ? result : Double.longBitsToDouble(Double.doubleToLongBits(max) - 1L);
    }

    @Override
    public double nextGaussian() {
        return ThreadLocalRandom.current().nextGaussian();
    }

    @Override
    public IntStream ints(long streamSize) {
        return ThreadLocalRandom.current().ints(streamSize);
    }

    @Override
    public IntStream ints() {
        return ThreadLocalRandom.current().ints();
    }

    @Override
    public IntStream ints(long streamSize, int randomNumberMin, int randomNumberBound) {
        return ThreadLocalRandom.current().ints(streamSize, randomNumberMin, randomNumberBound);
    }

    @Override
    public IntStream ints(int randomNumberMin, int randomNumberBound) {
        return ThreadLocalRandom.current().ints(randomNumberMin, randomNumberBound);
    }

    @Override
    public LongStream longs(long streamSize) {
        return ThreadLocalRandom.current().longs(streamSize);
    }

    @Override
    public LongStream longs() {
        return ThreadLocalRandom.current().longs();
    }

    @Override
    public LongStream longs(long streamSize, long randomNumberMin, long randomNumberBound) {
        return ThreadLocalRandom.current().longs(streamSize, randomNumberMin, randomNumberBound);
    }

    @Override
    public LongStream longs(long randomNumberMin, long randomNumberBound) {
        return ThreadLocalRandom.current().longs(randomNumberMin, randomNumberBound);
    }

    @Override
    public DoubleStream doubles(long streamSize) {
        return ThreadLocalRandom.current().doubles(streamSize);
    }

    @Override
    public DoubleStream doubles() {
        return ThreadLocalRandom.current().doubles();
    }

    @Override
    public DoubleStream doubles(long streamSize, double randomNumberMin, double randomNumberBound) {
        return ThreadLocalRandom.current().doubles(streamSize, randomNumberMin, randomNumberBound);
    }

    @Override
    public DoubleStream doubles(double randomNumberMin, double randomNumberBound) {
        return ThreadLocalRandom.current().doubles(randomNumberMin, randomNumberBound);
    }
}
