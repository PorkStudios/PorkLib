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

package net.daporkchop.lib.random;

import lombok.NonNull;

import java.util.Random;

/**
 * Generates random numbers.
 *
 * @author DaPorkchop_
 */
public interface PRandom {
    /**
     * Gets an instance of {@link Random} which provides a view of this {@link PRandom} instance.
     * <p>
     * The returned instance will either be a proxy to this {@link PRandom} instance, or this instance directly. As such, all
     * concurrency characteristics of this {@link PRandom} instance are preserved.
     *
     * @return a view of this {@link PRandom} as a {@link Random}
     */
    Random asJava();

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
     * @param min the minimum value to generate (inclusive)
     * @param max the maximum value to generate (exclusive)
     * @return a random int between the given minimum and maximum values
     */
    int nextInt(int min, int max);

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
     * @param min the minimum value to generate (inclusive)
     * @param max the maximum value to generate (exclusive)
     * @return a random long between the given minimum and maximum values
     */
    long nextLong(int min, int max);

    /**
     * @return a random float in the range {@code 0} - {@code 1}
     */
    float nextFloat();

    /**
     * Gets a random float.
     *
     * @param min the minimum value to generate (inclusive)
     * @param max the maximum value to generate (exclusive)
     * @return a random float between the given minimum and maximum values
     */
    float nextFloat(float min, float max);

    /**
     * @return a random double in the range {@code 0} - {@code 1}
     */
    double nextDouble();

    /**
     * Gets a random double.
     *
     * @param min the minimum value to generate (inclusive)
     * @param max the maximum value to generate (exclusive)
     * @return a random double between the given minimum and maximum values
     */
    double nextDouble(double min, double max);
}
