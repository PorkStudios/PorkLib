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

package net.daporkchop.lib.noise.engine;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.util.PArrays;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.random.PRandom;

import java.util.stream.IntStream;

import static net.daporkchop.lib.math.interpolation.PEasing.*;
import static net.daporkchop.lib.math.primitive.PMath.*;
import static net.daporkchop.lib.random.impl.FastPRandom.mix64;

/**
 * An implementation of Porkian v2 noise, which is a simple value noise algorithm.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class PorkianV2NoiseEngine implements NoiseSource {
    protected static final double DOUBLE_UNIT = 0x1.0p-53;

    private static double fade(double t) {
        return t * t * t * (t * (t * 6.0d - 15.0d) + 10.0d);
    }

    private final long seed;

    public PorkianV2NoiseEngine(@NonNull PRandom random) {
        this.seed = random.nextLong();
    }

    @Override
    public double get(double x) {
        final int xI = floorI(x);

        x = fade(x - xI);

        return lerp(this.mix(xI), this.mix(xI + 1), x) * 2.0d - 1.0d;
    }

    @Override
    public double get(double x, double y) {
        final int xI = floorI(x);
        final int yI = floorI(y);

        x = fade(x - xI);
        y = fade(y - yI);

        return lerp(
                lerp(this.mix(xI, yI), this.mix(xI + 1, yI), x),
                lerp(this.mix(xI, yI + 1), this.mix(xI + 1, yI + 1), x), y) * 2.0d - 1.0d;
    }

    @Override
    public double get(double x, double y, double z) {
        final int xI = floorI(x);
        final int yI = floorI(y);
        final int zI = floorI(z);

        x = fade(x - xI);
        y = fade(y - yI);
        z = fade(z - zI);

        return lerp(
                lerp(
                        lerp(this.mix(xI, yI, zI), this.mix(xI + 1, yI, zI), x),
                        lerp(this.mix(xI, yI + 1, zI), this.mix(xI + 1, yI + 1, zI), x), y),
                lerp(
                        lerp(this.mix(xI, yI, zI + 1), this.mix(xI + 1, yI, zI + 1), x),
                        lerp(this.mix(xI, yI + 1, zI + 1), this.mix(xI + 1, yI + 1, zI + 1), x), y), z) * 2.0d - 1.0d;
    }

    private double mix(int x) {
        return (mix64(x ^ this.seed) >>> 11L) * DOUBLE_UNIT;
    }

    private double mix(int x, int y) {
        return ((mix64(x ^ this.seed)
                + mix64(y ^ this.seed)) >>> 11L) * DOUBLE_UNIT;
    }

    private double mix(int x, int y, int z) {
        return ((mix64(x ^ this.seed)
                + mix64(y ^ this.seed)
                + mix64(z ^ this.seed)) >>> 11L) * DOUBLE_UNIT;
    }
}
