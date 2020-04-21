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

package net.daporkchop.lib.random.wrapper;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.random.PRandom;

import java.util.Random;

/**
 * A wrapper around {@link Random} to make it a {@link PRandom}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public final class JavaRandomWrapper implements PRandom {
    @NonNull
    private final Random delegate;

    @Override
    public Random asJava() {
        return this.delegate;
    }

    @Override
    public boolean nextBoolean() {
        return this.delegate.nextBoolean();
    }

    @Override
    public byte nextByte() {
        return (byte) (this.delegate.nextInt() & 0xFF);
    }

    @Override
    public void nextBytes(@NonNull byte[] dst) {
        this.delegate.nextBytes(dst);
    }

    @Override
    public void nextBytes(@NonNull byte[] dst, int start, int length) {
        PValidation.checkRangeLen(dst.length, start, length);

        //optimization for filling the whole array
        if (start == 0 && length == dst.length) {
            this.delegate.nextBytes(dst);
            return;
        }

        for (int i = start; i < length; ) {
            for (long rnd = this.delegate.nextLong(), n = Math.min(length - i, Long.SIZE / Byte.SIZE); n-- > 0; rnd >>= Byte.SIZE) {
                dst[i++] = (byte) rnd;
            }
        }
    }

    @Override
    public short nextShort() {
        return (short) (this.delegate.nextInt() & 0xFFFF);
    }

    @Override
    public int next(int bits) {
        return this.delegate.nextInt(1 << bits);
    }

    @Override
    public int nextInt() {
        return this.delegate.nextInt();
    }

    @Override
    public int nextUnsignedInt() {
        return this.delegate.nextInt() >>> 1;
    }

    @Override
    public int nextInt(int bound) {
        return this.delegate.nextInt(bound);
    }

    @Override
    public int nextInt(int origin, int bound) {
        return this.delegate.nextInt(bound - origin) + origin;
    }

    @Override
    public long next(long bits) {
        return this.delegate.nextLong() & ((1L << bits) - 1L);
    }

    @Override
    public long nextLong() {
        return this.delegate.nextLong();
    }

    @Override
    public long nextUnsignedLong() {
        return this.delegate.nextLong() >>> 1L;
    }

    @Override
    public long nextLong(long bound) {
        if (bound <= 0L) {
            throw new IllegalArgumentException("bound must be positive");
        }

        long r = this.delegate.nextLong() >>> 1L;
        long m = bound - 1L;
        if ((bound & m) == 0L) {
            r = r & m;
        } else {
            for (long u = r; u - (r = u % bound) + m < 0; u = this.delegate.nextLong() >>> 1L) ;
        }
        return r;
    }

    @Override
    public long nextLong(int origin, int bound) {
        if (bound <= origin) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        return this.nextLong(bound - origin) + origin;
    }

    @Override
    public float nextFloat() {
        return this.delegate.nextFloat();
    }

    @Override
    public float nextFloat(float bound) {
        if (bound <= 0.0f) {
            throw new IllegalArgumentException("bound must be positive");
        }

        float result = this.delegate.nextFloat() * bound;
        return (result < bound) ? result : Float.intBitsToFloat(Float.floatToIntBits(bound) - 1);
    }

    @Override
    public float nextFloat(float origin, float bound) {
        if (bound <= origin) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        return (this.delegate.nextFloat() * (bound - origin)) + origin;
    }

    @Override
    public float nextGaussianFloat() {
        return (float) this.delegate.nextGaussian();
    }

    @Override
    public double nextDouble() {
        return this.delegate.nextDouble();
    }

    @Override
    public double nextDouble(double bound) {
        if (bound <= 0.0d) {
            throw new IllegalArgumentException("bound must be positive");
        }

        double result = this.delegate.nextDouble() * bound;
        return (result < bound) ?  result : Double.longBitsToDouble(Double.doubleToLongBits(bound) - 1L);
    }

    @Override
    public double nextDouble(double origin, double bound) {
        if (bound <= origin) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        return (this.delegate.nextDouble() * (bound - origin)) + origin;
    }

    @Override
    public double nextGaussianDouble() {
        return this.delegate.nextGaussian();
    }
}
