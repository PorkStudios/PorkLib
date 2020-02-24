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
