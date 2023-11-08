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

package net.daporkchop.lib.noise.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.noise.util.NoiseFactory;
import net.daporkchop.lib.random.PRandom;

/**
 * Applies a number of octaves of fractal brownian motion to a given {@link NoiseSource}.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public final class OctaveFilter extends FilterNoiseSource {
    private final double min;
    private final double max;

    @Getter(AccessLevel.NONE)
    private final int octaves;

    public OctaveFilter(@NonNull NoiseSource delegate, int octaves) {
        super(delegate);

        this.octaves = PValidation.positive(octaves);

        double min = delegate.min();
        double max = delegate.max();
        double center = (min + max) / 2.0d;

        //there's got to be a more efficient way of calculating this...
        double octaveDeviation = Math.abs(max - center);
        double theoreticalMaxDeviation = 0.0d;
        for (int i = octaves - 1; i >= 0; i--) {
            theoreticalMaxDeviation += octaveDeviation;
            octaveDeviation *= 0.5d;
        }

        this.min = center - theoreticalMaxDeviation;
        this.max = center + theoreticalMaxDeviation;
    }

    public OctaveFilter(@NonNull NoiseFactory factory, @NonNull PRandom random, int octaves) {
        this(factory.apply(random), octaves);
    }

    @Override
    public double get(double x) {
        double val = 0.0d;
        double factor = 1.0d;
        double scale = 1.0d;

        for (int i = this.octaves - 1; i >= 0; i--) {
            val += this.delegate.get(x * scale) * factor;

            factor *= 0.5d;
            scale *= 2.0d;
        }

        return val;
    }

    @Override
    public double get(double x, double y) {
        double val = 0.0d;
        double factor = 1.0d;
        double scale = 1.0d;

        for (int i = this.octaves - 1; i >= 0; i--) {
            val += this.delegate.get(x * scale, y * scale) * factor;

            factor *= 0.5d;
            scale *= 2.0d;
        }

        return val;
    }

    @Override
    public double get(double x, double y, double z) {
        double val = 0.0d;
        double factor = 1.0d;
        double scale = 1.0d;

        for (int i = this.octaves - 1; i >= 0; i--) {
            val += this.delegate.get(x * scale, y * scale, z * scale) * factor;

            factor *= 0.5d;
            scale *= 2.0d;
        }

        return val;
    }

    @Override
    public String toString() {
        return String.format("Octaves(%s,%d)", this.delegate, this.octaves);
    }
}
