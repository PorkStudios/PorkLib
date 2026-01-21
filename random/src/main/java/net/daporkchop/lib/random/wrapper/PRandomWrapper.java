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
import net.daporkchop.lib.random.PRandom;

import java.util.Random;

/**
 * A wrapper around {@link PRandom} to make it a {@link Random}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public final class PRandomWrapper extends Random {
    @NonNull
    private final PRandom delegate;

    @Override
    public void setSeed(long seed) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected int next(int bits) {
        return this.delegate.next(bits);
    }

    @Override
    public void nextBytes(byte[] bytes) {
        this.delegate.nextBytes(bytes);
    }

    @Override
    public int nextInt() {
        return this.delegate.nextInt();
    }

    @Override
    public int nextInt(int bound) {
        return this.delegate.nextInt(bound);
    }

    @Override
    public long nextLong() {
        return this.delegate.nextLong();
    }

    @Override
    public boolean nextBoolean() {
        return this.delegate.nextBoolean();
    }

    @Override
    public float nextFloat() {
        return this.delegate.nextFloat();
    }

    @Override
    public double nextDouble() {
        return this.delegate.nextDouble();
    }

    @Override
    public double nextGaussian() {
        return this.delegate.nextGaussianDouble();
    }
}
