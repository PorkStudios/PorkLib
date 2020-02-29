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

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.math.primitive.PMath;
import net.daporkchop.lib.noise.NoiseSource;

import static net.daporkchop.lib.math.primitive.PMath.lerp;

/**
 * Linearly interpolates between the noise values of two different {@link NoiseSource}s using a third {@link NoiseSource} as the bias {@code t}.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public final class LerpFilter implements NoiseSource {
    @Getter
    private final double min;
    @Getter
    private final double max;

    private final NoiseSource a;
    private final NoiseSource b;
    private final NoiseSource t;

    public LerpFilter(@NonNull NoiseSource a, @NonNull NoiseSource b, @NonNull NoiseSource t)   {
        this.a = a;
        this.b = b;
        this.t = t.toRange(0.0d, 1.0d);

        this.min = Math.min(this.a.min(), this.b.min());
        this.max = Math.max(this.a.max(), this.b.max());
    }

    @Override
    public double get(double x) {
        return lerp(this.a.get(x), this.b.get(x), this.t.get(x));
    }

    @Override
    public double get(double x, double y) {
        return lerp(this.a.get(x, y), this.b.get(x, y), this.t.get(x, y));
    }

    @Override
    public double get(double x, double y, double z) {
        return lerp(this.a.get(x, y, z), this.b.get(x, y, z), this.t.get(x, y, z));
    }

    @Override
    public String toString() {
        return String.format("lerp(a=%s,b=%s,t=%s)", this.a, this.b, this.t);
    }
}
