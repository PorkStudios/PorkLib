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

package net.daporkchop.lib.noise.engine;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.math.primitive.PMath;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.noise.filter.LerpFilter;
import net.daporkchop.lib.noise.filter.math.ScalarAddFilter;
import net.daporkchop.lib.noise.filter.math.ScalarMulFilter;
import net.daporkchop.lib.noise.filter.math.ScalarSubFilter;

/**
 * A {@link NoiseSource} that always returns the same value.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public final class NoopNoiseEngine implements NoiseSource {
    @Getter(AccessLevel.NONE)
    private final double val;

    private final double min;
    private final double max;

    @Override
    public double get(double x) {
        return this.val;
    }

    @Override
    public double get(double x, double y) {
        return this.val;
    }

    @Override
    public double get(double x, double y, double z) {
        return this.val;
    }

    @Override
    public NoiseSource lerped(@NonNull NoiseSource a, @NonNull NoiseSource b) {
        if (this.val == this.min)   {
            return a;
        } else if (this.val == this.max)    {
            return b;
        } else {
            return new LerpFilter(a, b, this);
        }
    }

    @Override
    public NoiseSource scaled(double scale) {
        return this;
    }

    @Override
    public NoiseSource scaled(double scaleX, double scaleY, double scaleZ) {
        return this;
    }

    @Override
    public NoiseSource octaves(int octaves) {
        return this.val == 0.0d ? this : this.mul(octaves);
    }

    @Override
    public NoiseSource weighted() {
        return this;
    }

    @Override
    public NoiseSource toRange(double min, double max) {
        return min == this.min && max == this.max ? this : new NoopNoiseEngine(PMath.lerp(min, max, (this.val - this.min) / (this.max - this.min)), min, max);
    }

    @Override
    public NoiseSource add(double val) {
        return val == 0.0d ? this : new NoopNoiseEngine(this.val + val, this.min + val, this.max + val);
    }

    @Override
    public NoiseSource add(@NonNull NoiseSource val) {
        return this.val == 0.0d ? val : new ScalarAddFilter(val, this.val);
    }

    @Override
    public NoiseSource sub(double val) {
        return val == 0.0d ? this : new NoopNoiseEngine(this.val - val, this.min - val, this.max - val);
    }

    @Override
    public NoiseSource sub(@NonNull NoiseSource val) {
        return this.val == 0.0d ? val : new ScalarSubFilter(val, this.val);
    }

    @Override
    public NoiseSource mul(double val) {
        if (this.val == 0.0d || val == 0.0d) {
            return ZERO;
        } else if (val == 1.0d) {
            return this;
        } else {
            return new NoopNoiseEngine(this.val * val, this.val * val, this.val * val + 1.0d);
        }
    }

    @Override
    public NoiseSource mul(@NonNull NoiseSource val) {
        if (this.val == 0.0d) {
            return ZERO;
        } else if (this.val == 1.0d) {
            return val;
        } else {
            return new ScalarMulFilter(val, this.val);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(this.val);
    }
}
