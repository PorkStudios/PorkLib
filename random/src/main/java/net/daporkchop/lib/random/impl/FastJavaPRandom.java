/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
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

package net.daporkchop.lib.random.impl;

import lombok.NonNull;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.random.PRandom;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A fast implementation of {@link PRandom} based on Java's {@link java.util.Random}.
 * <p>
 * This is NOT thread-safe. Attempting to share an instance of this class among multiple threads is likely to result in duplicate values
 * being returned to multiple threads.
 *
 * @author DaPorkchop_
 */
public final class FastJavaPRandom extends AbstractFastPRandom {
    private static final long multiplier = 0x5DEECE66DL;
    private static final long addend = 0xBL;
    private static final long mask = (1L << 48) - 1;

    private static final AtomicLong seedUniquifier = new AtomicLong(8682522807148012L);

    private static long initialScramble(long seed) {
        return (seed ^ multiplier) & mask;
    }

    private long seed;

    /**
     * Creates a new {@link FastJavaPRandom} instance using a seed based on the current time.
     */
    public FastJavaPRandom() {
        this(seedUniquifier.updateAndGet(val -> val * 181783497276652981L) ^ System.nanoTime());
    }

    /**
     * Creates a new {@link FastJavaPRandom} instance using the given seed.
     *
     * @param seed the seed to use
     */
    public FastJavaPRandom(long seed) {
        this.seed = initialScramble(seed);
    }

    @Override
    public int nextInt() {
        return (int) ((this.seed = (this.seed * multiplier + addend) & mask) >> 16L);
    }

    @Override
    public int next(int bits) {
        return (int) ((this.seed = (this.seed * multiplier + addend) & mask) >>> (48L - bits));
    }

    @Override
    public long nextLong() {
        return (((long) this.nextInt()) << 32L) + this.nextInt();
    }

    @Override
    public long next(long bits) {
        if (bits <= 32L) {
            return (this.seed = (this.seed * multiplier + addend) & mask) >>> (48L - bits);
        } else {
            return this.nextLong() >>> (bits ^ 0x3F);
        }
    }

    @Override
    public int nextInt(int bound) {
        if (bound <= 0) {
            throw new IllegalArgumentException("bound must be positive");
        }

        int r = this.next(31);
        int m = bound - 1;
        if ((bound & m) == 0) {
            r &= m;
        } else {
            for (int u = r; u - (r = u % bound) + m < 0; u = this.next(31))
                ;
        }
        return r;
    }

    @Override
    public int nextInt(int origin, int bound) {
        if (origin < bound) {
            int n = bound - origin;
            if (n > 0) {
                return this.nextInt(n) + origin;
            } else { //range not representable as int
                int r;
                do {
                    r = this.nextInt();
                } while (r < origin || r >= bound);
                return r;
            }
        } else {
            return this.nextInt();
        }
    }

    @Override
    public long nextLong(long bound) {
        return this.nextLong(0L, bound);
    }

    @Override
    public long nextLong(long origin, long bound) {
        long r = this.nextLong();
        if (origin < bound) {
            long n = bound - origin, m = n - 1;
            if ((n & m) == 0L) { //power of two
                r = (r & m) + origin;
            } else if (n > 0L) {  // reject over-represented candidates
                for (long u = r >>> 1;            // ensure nonnegative
                     u + m - (r = u % n) < 0L;    // rejection check
                     u = this.nextLong() >>> 1) // retry
                    ;
                r += origin;
            } else {              // range not representable as long
                while (r < origin || r >= bound) {
                    r = this.nextLong();
                }
            }
        }
        return r;
    }

    @Override
    public boolean nextBoolean() {
        return this.next(1) != 0;
    }

    @Override
    public float nextFloat() {
        return this.next(24) / ((float) (1 << 24));
    }

    @Override
    public float nextFloat(float bound) {
        return (float) this.nextDouble(0.0d, bound);
    }

    @Override
    public float nextFloat(float origin, float bound) {
        return (float) this.nextDouble(origin, bound);
    }

    @Override
    public double nextDouble() {
        return (((long) this.next(26) << 27L) + this.next(27)) * DOUBLE_UNIT;
    }

    @Override
    public double nextDouble(double bound) {
        return this.nextDouble(0.0d, bound);
    }

    @Override
    public double nextDouble(double origin, double bound) {
        double r = this.nextDouble();
        if (origin < bound) {
            r = r * (bound - origin) + origin;
            if (r >= bound) { //correct for rounding
                r = Double.longBitsToDouble(Double.doubleToLongBits(bound) - 1);
            }
        }
        return r;
    }

    @Override
    public void nextBytes(@NonNull byte[] dst, int start, int length) {
        PValidation.checkRangeLen(dst.length, start, length);
        if (length == 0) {
            return;
        }

        this.nextBytes32LE(dst, start, length);
    }

    @Override
    public void setSeed(long seed) {
        this.seed = initialScramble(seed);
    }
}
