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

package net.daporkchop.lib.noise.filter.range;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.noise.filter.FilterNoiseSource;
import net.daporkchop.lib.noise.util.NoiseFactory;
import net.daporkchop.lib.random.PRandom;

/**
 * Converts the output range of a given {@link net.daporkchop.lib.noise.NoiseSource} to a given range.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public final class RangeConversionFilter extends FilterNoiseSource {
    private final double fromMin;
    private final double factor;
    private final double min;
    private final double max;

    public RangeConversionFilter(@NonNull NoiseSource delegate, double min, double max) {
        super(delegate);

        this.factor = (max - min) / (delegate.max() - (this.fromMin = delegate.min()));

        this.min = min;
        this.max = max;
    }

    public RangeConversionFilter(@NonNull NoiseFactory factory, @NonNull PRandom random, double min, double max) {
        this(factory.apply(random), min, max);
    }

    @Override
    public double get(double x) {
        return (this.delegate.get(x) - this.fromMin) * this.factor + this.min;
    }

    @Override
    public double get(double x, double y) {
        return (this.delegate.get(x, y) - this.fromMin) * this.factor + this.min;
    }

    @Override
    public double get(double x, double y, double z) {
        return (this.delegate.get(x, y, z) - this.fromMin) * this.factor + this.min;
    }

    @Override
    public String toString() {
        return String.format("Range(%s,min=%f,max=%f)", this.delegate, this.min, this.max);
    }
}
