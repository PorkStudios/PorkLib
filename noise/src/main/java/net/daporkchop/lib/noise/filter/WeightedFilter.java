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
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.noise.util.NoiseFactory;
import net.daporkchop.lib.random.PRandom;

/**
 * Weights values from a {@link NoiseSource} towards the outer bounds, providing far more valley and peaks that approach -1 and 1.
 *
 * @author DaPorkchop_
 */
public final class WeightedFilter extends FilterNoiseSource {
    private static double fade(double t) {
        return t * t * t * (t * (t * 6.0d - 15.0d) + 10.0d);
    }

    public WeightedFilter(@NonNull NoiseSource delegate) {
        super(delegate);
    }

    public WeightedFilter(@NonNull NoiseFactory factory, @NonNull PRandom random) {
        super(factory, random);
    }

    @Override
    public double get(double x) {
        return fade(this.delegate.get(x) * 0.5d + 0.5d) * 2.0d - 1.0d;
    }

    @Override
    public double get(double x, double y) {
        return fade(this.delegate.get(x, y) * 0.5d + 0.5d) * 2.0d - 1.0d;
    }

    @Override
    public double get(double x, double y, double z) {
        return fade(this.delegate.get(x, y, z) * 0.5d + 0.5d) * 2.0d - 1.0d;
    }
}
