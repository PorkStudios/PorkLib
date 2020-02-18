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
