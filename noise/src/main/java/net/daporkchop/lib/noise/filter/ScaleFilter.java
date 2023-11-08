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

import lombok.NonNull;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.noise.util.NoiseFactory;
import net.daporkchop.lib.random.PRandom;

/**
 * Applies a certain scale factor to a given {@link NoiseSource}.
 *
 * @author DaPorkchop_
 */
public final class ScaleFilter extends FilterNoiseSource {
    private final double scaleX;
    private final double scaleY;
    private final double scaleZ;

    public ScaleFilter(@NonNull NoiseSource delegate, double scaleX, double scaleY, double scaleZ) {
        super(delegate);

        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
    }

    public ScaleFilter(@NonNull NoiseFactory factory, @NonNull PRandom random, double scaleX, double scaleY, double scaleZ) {
        this(factory.apply(random), scaleX, scaleY, scaleZ);
    }

    @Override
    public double min() {
        return this.delegate.min();
    }

    @Override
    public double max() {
        return this.delegate.max();
    }

    @Override
    public double get(double x) {
        return this.delegate.get(x * this.scaleX);
    }

    @Override
    public double get(double x, double y) {
        return this.delegate.get(x * this.scaleX, y * this.scaleY);
    }

    @Override
    public double get(double x, double y, double z) {
        return this.delegate.get(x * this.scaleX, y * this.scaleY, z * this.scaleZ);
    }

    @Override
    public String toString() {
        return String.format("Scale(%s,(%f,%f,%f))", this.delegate, this.scaleX, this.scaleY, this.scaleZ);
    }
}
