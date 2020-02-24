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
