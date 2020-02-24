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

import lombok.NonNull;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.noise.util.NoiseFactory;
import net.daporkchop.lib.random.PRandom;

/**
 * Applies a number of octaves of fractal brownian motion to a given {@link NoiseSource}.
 *
 * @author DaPorkchop_
 */
public final class OctaveFilter extends FilterNoiseSource {
    private final int octaves;

    public OctaveFilter(@NonNull NoiseSource delegate, int octaves) {
        super(delegate);

        this.octaves = PValidation.ensurePositive(octaves);
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
        return String.format("%s(%s,octaves=%d)", this.getClass().getCanonicalName(), this.delegate, this.octaves);
    }
}
