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

package net.daporkchop.lib.random.impl;

import lombok.AllArgsConstructor;
import net.daporkchop.lib.common.math.PMath;
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

    private long seed;

    /**
     * Creates a new {@link FastPRandom} instance using a seed based on the current time.
     */
    public FastPRandom() {
        this(PMath.mix64(System.currentTimeMillis()) ^ PMath.mix64(System.nanoTime()));
    }

    @Override
    public int nextInt() {
        return PMath.mix32(this.seed += GAMMA);
    }

    @Override
    public long nextLong() {
        return PMath.mix64(this.seed += GAMMA);
    }

    @Override
    public void setSeed(long seed) {
        this.seed = seed;
    }
}
