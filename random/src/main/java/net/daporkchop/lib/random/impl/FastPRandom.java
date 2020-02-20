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
import net.daporkchop.lib.random.PRandom;

/**
 * A fast implementation of {@link PRandom} based on Java's {@link java.util.SplittableRandom}.
 * <p>
 * This is NOT thread-safe. Attempting to share an instance of this class among multiple threads is likely to result in duplicate values
 * being returned to multiple threads.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
public final class FastPRandom extends AbstractFastPRandom {
    private static final long GAMMA = 0x9e3779b97f4a7c15L;

    public static long mix64(long z) {
        z = (z ^ (z >>> 33)) * 0xff51afd7ed558ccdL;
        z = (z ^ (z >>> 33)) * 0xc4ceb9fe1a85ec53L;
        return z ^ (z >>> 33);
    }

    public static int mix32(long z) {
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

    @Override
    public int nextInt() {
        return mix32(this.seed += GAMMA);
    }

    @Override
    public long nextLong() {
        return mix64(this.seed += GAMMA);
    }

    @Override
    public void setSeed(long seed) {
        this.seed = seed;
    }
}
