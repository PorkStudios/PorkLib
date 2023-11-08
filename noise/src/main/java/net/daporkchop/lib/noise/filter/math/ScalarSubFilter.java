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

package net.daporkchop.lib.noise.filter.math;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.noise.filter.FilterNoiseSource;
import net.daporkchop.lib.noise.util.NoiseFactory;
import net.daporkchop.lib.random.PRandom;

/**
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public final class ScalarSubFilter extends FilterNoiseSource {
    private final double val;

    @Getter
    private final double min;
    @Getter
    private final double max;

    public ScalarSubFilter(@NonNull NoiseSource delegate, double val) {
        super(delegate);

        this.val = val;

        this.min = delegate.min() - val;
        this.max = delegate.max() - val;
    }

    public ScalarSubFilter(@NonNull NoiseFactory factory, @NonNull PRandom random, double val) {
        this(factory.apply(random), val);
    }

    @Override
    public double get(double x) {
        return this.delegate.get(x) - this.val;
    }

    @Override
    public double get(double x, double y) {
        return this.delegate.get(x, y) - this.val;
    }

    @Override
    public double get(double x, double y, double z) {
        return this.delegate.get(x, y, z) - this.val;
    }

    @Override
    public String toString() {
        return String.format("%s - %f", this.delegate, this.val);
    }
}
