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

package net.daporkchop.lib.noise.engine;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.random.PRandom;

import static net.daporkchop.lib.common.math.PMath.*;

/**
 * An implementation of Porkian v2 noise, which is a simple value noise algorithm.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class PorkianV2NoiseEngine implements NoiseSource {
    protected static final double DOUBLE_UNIT = 0x1.0p-53;

    protected static double fade(double t) {
        return t * t * (-t * 2.0d + 3.0d);
    }

    protected final long seed;

    public PorkianV2NoiseEngine(@NonNull PRandom random) {
        this.seed = random.nextLong();
    }

    @Override
    public double get(double x) {
        final int xI = floorI(x);

        x = fade(x - xI);

        return fade(lerp(this.mix(xI), this.mix(xI + 1), x)) * 2.0d - 1.0d;
    }

    @Override
    public double get(double x, double y) {
        final int xI = floorI(x);
        final int yI = floorI(y);

        x = fade(x - xI);
        y = fade(y - yI);

        return fade(lerp(
                fade(lerp(this.mix(xI, yI), this.mix(xI + 1, yI), x)),
                fade(lerp(this.mix(xI, yI + 1), this.mix(xI + 1, yI + 1), x)), y)) * 2.0d - 1.0d;
    }

    @Override
    public double get(double x, double y, double z) {
        final int xI = floorI(x);
        final int yI = floorI(y);
        final int zI = floorI(z);

        x = fade(x - xI);
        y = fade(y - yI);
        z = fade(z - zI);

        return fade(lerp(
                fade(lerp(
                        fade(lerp(this.mix(xI, yI, zI), this.mix(xI + 1, yI, zI), x)),
                        fade(lerp(this.mix(xI, yI + 1, zI), this.mix(xI + 1, yI + 1, zI), x)), y)),
                fade(lerp(
                        fade(lerp(this.mix(xI, yI, zI + 1), this.mix(xI + 1, yI, zI + 1), x)),
                        fade(lerp(this.mix(xI, yI + 1, zI + 1), this.mix(xI + 1, yI + 1, zI + 1), x)), y)), z)) * 2.0d - 1.0d;
    }

    private double mix(int x) {
        return (mix64(x ^ this.seed) >>> 11L) * DOUBLE_UNIT;
    }

    private double mix(int x, int y) {
        return (mix64(y ^ this.seed ^ mix64(x ^ this.seed)) >>> 11L) * DOUBLE_UNIT;
    }

    private double mix(int x, int y, int z) {
        return (mix64(z ^ this.seed ^ mix64(y ^ this.seed ^ mix64(x ^ this.seed))) >>> 11L) * DOUBLE_UNIT;
    }

    @Override
    public String toString() {
        return this.getClass().getCanonicalName();
    }
}
