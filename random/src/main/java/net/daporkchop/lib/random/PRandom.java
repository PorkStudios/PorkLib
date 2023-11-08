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

package net.daporkchop.lib.random;

import lombok.NonNull;
import net.daporkchop.lib.common.util.PArrays;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.random.impl.AbstractFastPRandom;
import net.daporkchop.lib.random.impl.ThreadLocalPRandom;
import net.daporkchop.lib.random.wrapper.JavaRandomWrapper;
import net.daporkchop.lib.random.wrapper.PRandomWrapper;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates random numbers.
 *
 * @author DaPorkchop_
 */
public interface PRandom {
    /**
     * Gets an instance of {@link PRandom} which provides a view of the given {@link Random} instance.
     * <p>
     * The returned instance will either be a proxy to the given {@link Random} instance, or the given instance directly. As
     * such, all concurrency characteristics of the given {@link Random} instance are preserved.
     *
     * @return a view of the given {@link Random} as a {@link PRandom}
     */
    static PRandom wrap(@NonNull Random random) {
        if (random instanceof PRandomWrapper) {
            return ((PRandomWrapper) random).delegate();
        } else if (random instanceof ThreadLocalRandom) {
            return ThreadLocalPRandom.current();
        } else if (random instanceof AbstractFastPRandom) {
            return (AbstractFastPRandom) random;
        } else {
            return new JavaRandomWrapper(random);
        }
    }

    /**
     * Gets an instance of {@link Random} which provides a view of this {@link PRandom} instance.
     * <p>
     * The returned instance will either be a proxy to this {@link PRandom} instance, or this instance directly. As such, all
     * concurrency characteristics of this {@link PRandom} instance are preserved.
     *
     * @return a view of this {@link PRandom} as a {@link Random}
     */
    Random asJava();
    
    //rng methods

    /**
     * @return a random boolean
     */
    boolean nextBoolean();

    /**
     * @return a random byte
     */
    byte nextByte();

    /**
     * Fills the given {@code byte[]} with random bytes.
     *
     * @param dst the {@code byte[]} to fill
     */
    default void nextBytes(@NonNull byte[] dst) {
        this.nextBytes(dst, 0, dst.length);
    }

    /**
     * Fills the given region of the given {@code byte[]} with random bytes.
     *
     * @param dst    the {@code byte[]} to fill
     * @param start  the first index in the {@code byte[]} to start filling at
     * @param length the number of bytes to fill
     */
    void nextBytes(@NonNull byte[] dst, int start, int length);

    /**
     * @return a random short
     */
    short nextShort();

    /**
     * Gets a random int value with the given number of bits.
     * <p>
     * Equivalent to {@code nextInt() & ((1 << bits) - 1)}
     *
     * @param bits the number of bits to generate
     * @return a random value
     * @see #next(long)
     */
    int next(int bits);

    /**
     * @return a random int
     */
    int nextInt();

    /**
     * @return a random, unsigned int
     */
    int nextUnsignedInt();

    /**
     * Gets a random, unsigned int.
     *
     * @param bound the maximum value to generate (exclusive)
     * @return a random int between {@code 0} and the given bound
     */
    int nextInt(int bound);

    /**
     * Gets a random int.
     *
     * @param origin the minimum value to generate (inclusive)
     * @param bound  the maximum value to generate (exclusive)
     * @return a random int between the given minimum and maximum values
     */
    int nextInt(int origin, int bound);

    /**
     * Gets a random long value with the given number of bits.
     * <p>
     * Equivalent to {@code nextLong() & ((1L << bits) - 1L)}
     *
     * @param bits the number of bits to generate
     * @return a random value
     * @see #next(int)
     */
    long next(long bits);

    /**
     * @return a random long
     */
    long nextLong();

    /**
     * @return a random, unsigned long
     */
    long nextUnsignedLong();

    /**
     * Gets a random, unsigned long.
     *
     * @param bound the maximum value to generate (exclusive)
     * @return a random long between {@code 0} and the given bound
     */
    long nextLong(long bound);

    /**
     * Gets a random long.
     *
     * @param origin the minimum value to generate (inclusive)
     * @param bound  the maximum value to generate (exclusive)
     * @return a random long between the given minimum and maximum values
     */
    long nextLong(long origin, long bound);

    /**
     * @return a random float in the range {@code 0} - {@code 1}
     */
    float nextFloat();

    /**
     * Gets a random float.
     *
     * @param bound the maximum value to generate (exclusive)
     * @return a random float between {@code 0} and the given bound
     */
    float nextFloat(float bound);

    /**
     * Gets a random float.
     *
     * @param origin the minimum value to generate (inclusive)
     * @param bound  the maximum value to generate (exclusive)
     * @return a random float between the given minimum and maximum values
     */
    float nextFloat(float origin, float bound);

    /**
     * @return a random float with gaussian distribution centered on {@code 0} and with a standard deviation of {@code 1}
     */
    float nextGaussianFloat();

    /**
     * @return a random double in the range {@code 0} - {@code 1}
     */
    double nextDouble();

    /**
     * Gets a random double.
     *
     * @param bound the maximum value to generate (exclusive)
     * @return a random double between {@code 0} and the given bound
     */
    double nextDouble(double bound);

    /**
     * Gets a random double.
     *
     * @param origin the minimum value to generate (inclusive)
     * @param bound  the maximum value to generate (exclusive)
     * @return a random double between the given minimum and maximum values
     */
    double nextDouble(double origin, double bound);

    /**
     * @return a random double with gaussian distribution centered on {@code 0} and with a standard deviation of {@code 1}
     */
    double nextGaussianDouble();
    
    //shuffle methods

    /**
     * Shuffles the given {@code byte[]}.
     *
     * @param arr the {@code byte[]} to shuffle
     * @return the {@code byte[]}
     */
    default byte[] shuffle(@NonNull byte[] arr) {
        return this.shuffle(arr, 0, arr.length);
    }

    /**
     * Shuffles the given range of the given {@code byte[]}.
     *
     * @param arr    the {@code byte[]} to shuffle
     * @param start  the index to begin shuffling at (inclusive)
     * @param length the length of the range to shuffle
     * @return the {@code byte[]}
     */
    default byte[] shuffle(@NonNull byte[] arr, int start, int length){
        PValidation.checkRangeLen(arr.length, start, length);
        final int end = start + length;
        for (int i = start; i < end; i++)   {
            PArrays.swap(arr, i, this.nextInt(start, length));
        }
        return arr;
    }

    /**
     * Shuffles the given {@code short[]}.
     *
     * @param arr the {@code short[]} to shuffle
     * @return the {@code short[]}
     */
    default short[] shuffle(@NonNull short[] arr) {
        return this.shuffle(arr, 0, arr.length);
    }

    /**
     * Shuffles the given range of the given {@code short[]}.
     *
     * @param arr    the {@code short[]} to shuffle
     * @param start  the index to begin shuffling at (inclusive)
     * @param length the length of the range to shuffle
     * @return the {@code short[]}
     */
    default short[] shuffle(@NonNull short[] arr, int start, int length){
        PValidation.checkRangeLen(arr.length, start, length);
        final int end = start + length;
        for (int i = start; i < end; i++)   {
            PArrays.swap(arr, i, this.nextInt(start, length));
        }
        return arr;
    }

    /**
     * Shuffles the given {@code char[]}.
     *
     * @param arr the {@code char[]} to shuffle
     * @return the {@code char[]}
     */
    default char[] shuffle(@NonNull char[] arr) {
        return this.shuffle(arr, 0, arr.length);
    }

    /**
     * Shuffles the given range of the given {@code char[]}.
     *
     * @param arr    the {@code char[]} to shuffle
     * @param start  the index to begin shuffling at (inclusive)
     * @param length the length of the range to shuffle
     * @return the {@code char[]}
     */
    default char[] shuffle(@NonNull char[] arr, int start, int length){
        PValidation.checkRangeLen(arr.length, start, length);
        final int end = start + length;
        for (int i = start; i < end; i++)   {
            PArrays.swap(arr, i, this.nextInt(start, length));
        }
        return arr;
    }

    /**
     * Shuffles the given {@code int[]}.
     *
     * @param arr the {@code int[]} to shuffle
     * @return the {@code int[]}
     */
    default int[] shuffle(@NonNull int[] arr) {
        return this.shuffle(arr, 0, arr.length);
    }

    /**
     * Shuffles the given range of the given {@code int[]}.
     *
     * @param arr    the {@code int[]} to shuffle
     * @param start  the index to begin shuffling at (inclusive)
     * @param length the length of the range to shuffle
     * @return the {@code int[]}
     */
    default int[] shuffle(@NonNull int[] arr, int start, int length){
        PValidation.checkRangeLen(arr.length, start, length);
        final int end = start + length;
        for (int i = start; i < end; i++)   {
            PArrays.swap(arr, i, this.nextInt(start, length));
        }
        return arr;
    }

    /**
     * Shuffles the given {@code long[]}.
     *
     * @param arr the {@code long[]} to shuffle
     * @return the {@code long[]}
     */
    default long[] shuffle(@NonNull long[] arr) {
        return this.shuffle(arr, 0, arr.length);
    }

    /**
     * Shuffles the given range of the given {@code long[]}.
     *
     * @param arr    the {@code long[]} to shuffle
     * @param start  the index to begin shuffling at (inclusive)
     * @param length the length of the range to shuffle
     * @return the {@code long[]}
     */
    default long[] shuffle(@NonNull long[] arr, int start, int length){
        PValidation.checkRangeLen(arr.length, start, length);
        final int end = start + length;
        for (int i = start; i < end; i++)   {
            PArrays.swap(arr, i, this.nextInt(start, length));
        }
        return arr;
    }

    /**
     * Shuffles the given {@code float[]}.
     *
     * @param arr the {@code float[]} to shuffle
     * @return the {@code float[]}
     */
    default float[] shuffle(@NonNull float[] arr) {
        return this.shuffle(arr, 0, arr.length);
    }

    /**
     * Shuffles the given range of the given {@code float[]}.
     *
     * @param arr    the {@code float[]} to shuffle
     * @param start  the index to begin shuffling at (inclusive)
     * @param length the length of the range to shuffle
     * @return the {@code float[]}
     */
    default float[] shuffle(@NonNull float[] arr, int start, int length){
        PValidation.checkRangeLen(arr.length, start, length);
        final int end = start + length;
        for (int i = start; i < end; i++)   {
            PArrays.swap(arr, i, this.nextInt(start, length));
        }
        return arr;
    }

    /**
     * Shuffles the given {@code double[]}.
     *
     * @param arr the {@code double[]} to shuffle
     * @return the {@code double[]}
     */
    default double[] shuffle(@NonNull double[] arr) {
        return this.shuffle(arr, 0, arr.length);
    }

    /**
     * Shuffles the given range of the given {@code double[]}.
     *
     * @param arr    the {@code double[]} to shuffle
     * @param start  the index to begin shuffling at (inclusive)
     * @param length the length of the range to shuffle
     * @return the {@code double[]}
     */
    default double[] shuffle(@NonNull double[] arr, int start, int length){
        PValidation.checkRangeLen(arr.length, start, length);
        final int end = start + length;
        for (int i = start; i < end; i++)   {
            PArrays.swap(arr, i, this.nextInt(start, length));
        }
        return arr;
    }

    /**
     * Shuffles the given {@code Object[]}.
     *
     * @param arr the {@code Object[]} to shuffle
     * @return the {@code Object[]}
     */
    default <T> T[] shuffle(@NonNull T[] arr) {
        return this.shuffle(arr, 0, arr.length);
    }

    /**
     * Shuffles the given range of the given {@code Object[]}.
     *
     * @param arr    the {@code Object[]} to shuffle
     * @param start  the index to begin shuffling at (inclusive)
     * @param length the length of the range to shuffle
     * @return the {@code Object[]}
     */
    default <T> T[] shuffle(@NonNull T[] arr, int start, int length){
        PValidation.checkRangeLen(arr.length, start, length);
        final int end = start + length;
        for (int i = start; i < end; i++)   {
            PArrays.swap(arr, i, this.nextInt(start, length));
        }
        return arr;
    }
}
