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
import net.daporkchop.lib.common.util.PArrays;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.random.PRandom;

import java.util.stream.IntStream;

import static net.daporkchop.lib.math.primitive.PMath.*;

/**
 * An implementation of Porkian v2 noise, which is a simple value noise algorithm.
 *
 * @author DaPorkchop_
 */
public final class PorkianNoiseEngine implements NoiseSource {
    private static final double[] DEFAULT_STATE = IntStream.range(-128, 128)
            .mapToDouble(i -> i / 128.0d)
            .toArray();

    private static int mix(int x) {
        return ((x * 1711285531) ^ x) & 0xFF;
    }

    private static int mix(int x, int y) {
        return (((x * 1711285531) ^ x) + ((y * 2046617201) ^ y)) & 0xFF;
    }

    private static int mix(int x, int y, int z) {
        return (((x * 1711285531) ^ x) + ((y * 2046617201) ^ y) + ((z * 1275136279) ^ z)) & 0xFF;
    }

    private final double[] state;

    public PorkianNoiseEngine(@NonNull PRandom random) {
        PArrays.shuffle(this.state = DEFAULT_STATE.clone(), random.asJava());
    }

    @Override
    public double get(double x) {
        final int xI = floorI(x);

        return lerp(this.state[mix(xI)], this.state[mix(xI + 1)], x - xI);
    }

    @Override
    public double get(double x, double y) {
        final int xI = floorI(x);
        final int yI = floorI(y);

        return lerp(
                lerp(this.state[mix(xI, yI)], this.state[mix(xI + 1, yI)], x - xI),
                lerp(this.state[mix(xI, yI + 1)], this.state[mix(xI + 1, yI + 1)], x - xI), y - yI);
    }

    @Override
    public double get(double x, double y, double z) {
        final int xI = floorI(x);
        final int yI = floorI(y);
        final int zI = floorI(z);

        return lerp(
                lerp(
                        lerp(this.state[mix(xI, yI, zI)], this.state[mix(xI + 1, yI, zI)], x - xI),
                        lerp(this.state[mix(xI, yI + 1, zI)], this.state[mix(xI + 1, yI + 1, zI)], x - xI), y - yI),
                lerp(
                        lerp(this.state[mix(xI, yI, zI + 1)], this.state[mix(xI + 1, yI, zI + 1)], x - xI),
                        lerp(this.state[mix(xI, yI + 1, zI + 1)], this.state[mix(xI + 1, yI + 1, zI + 1)], x - xI), y - yI), z - zI);
    }
}
