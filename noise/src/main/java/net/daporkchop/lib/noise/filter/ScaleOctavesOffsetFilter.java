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

package net.daporkchop.lib.noise.filter;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.noise.util.NoiseFactory;
import net.daporkchop.lib.random.PRandom;

/**
 * Basically a filter that does everything at once, with the intent of being much faster.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public final class ScaleOctavesOffsetFilter extends FilterNoiseSource {
    private final double scaleX;
    private final double scaleY;
    private final double scaleZ;
    private final int octaves;
    private final double factor;
    private final double offset;

    @Getter
    private final double min;
    @Getter
    private final double max;

    public ScaleOctavesOffsetFilter(@NonNull NoiseSource delegate, double scaleX, double scaleY, double scaleZ, int octaves, double factor, double offset) {
        super(delegate);

        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        this.octaves = PValidation.ensurePositive(octaves);
        this.factor = factor;
        this.offset = offset;

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

        min = center - theoreticalMaxDeviation;
        max = center + theoreticalMaxDeviation;
        this.min = Math.min(min * factor + offset, max * factor + offset);
        this.max = Math.max(min * factor + offset, max * factor + offset);
    }

    public ScaleOctavesOffsetFilter(@NonNull NoiseFactory factory, @NonNull PRandom random, double scaleX, double scaleY, double scaleZ, int octaves, double factor, double offset) {
        this(factory.apply(random), scaleX, scaleY, scaleZ, octaves, factor, offset);
    }

    @Override
    public double get(double x) {
        x *= this.scaleX;

        double val = 0.0d;
        double factor = 1.0d;
        double scale = 1.0d;

        for (int i = this.octaves - 1; i >= 0; i--) {
            val += this.delegate.get(x * scale) * factor;

            factor *= 0.5d;
            scale *= 2.0d;
        }

        return val * this.factor + this.offset;
    }

    @Override
    public double get(double x, double y) {
        x *= this.scaleX;
        y *= this.scaleY;

        double val = 0.0d;
        double factor = 1.0d;
        double scale = 1.0d;

        for (int i = this.octaves - 1; i >= 0; i--) {
            val += this.delegate.get(x * scale, y * scale) * factor;

            factor *= 0.5d;
            scale *= 2.0d;
        }

        return val * this.factor + this.offset;
    }

    @Override
    public double get(double x, double y, double z) {
        x *= this.scaleX;
        y *= this.scaleY;
        z *= this.scaleZ;

        double val = 0.0d;
        double factor = 1.0d;
        double scale = 1.0d;

        for (int i = this.octaves - 1; i >= 0; i--) {
            val += this.delegate.get(x * scale, y * scale, z * scale) * factor;

            factor *= 0.5d;
            scale *= 2.0d;
        }

        return val * this.factor + this.offset;
    }

    @Override
    public String toString() {
        return String.format("ScaleOctavesOffset(%s,scale=(%f,%f,%f),octaves=%d,factor=%f,offset=%f", this.delegate, this.scaleX, this.scaleY, this.scaleZ, this.octaves, this.factor, this.offset);
    }
}
