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

package noise;

import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.noise.engine.PerlinNoiseEngine;
import net.daporkchop.lib.noise.engine.PorkianV2NoiseEngine;
import net.daporkchop.lib.noise.engine.WeightedPerlinNoiseEngine;
import net.daporkchop.lib.random.PRandom;
import net.daporkchop.lib.random.impl.FastPRandom;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author DaPorkchop_
 */
public class RangeTest {
    private static final double RANGE = 100000.0d;
    private static final int SAMPLES = 100000000;

    @Test
    public void testRange() {
        NoiseSource[] sources = {
                new PorkianV2NoiseEngine(new FastPRandom()),
                new PerlinNoiseEngine(new FastPRandom()),
                new WeightedPerlinNoiseEngine(new FastPRandom())
        };

        Arrays.stream(sources).parallel()
                .forEach(src -> {
                    PRandom random = new FastPRandom();
                    long start = System.currentTimeMillis();
                    double min = Double.MAX_VALUE;
                    double max = Double.MIN_VALUE;
                    for (int i = 0; i < SAMPLES; i++)   {
                        double val = src.get(random.nextDouble(-RANGE, RANGE));
                        min = Math.min(val, min);
                        max = Math.max(val, max);
                    }
                    System.out.printf("1D %s: min=%f, max=%f, time=%.2fs\n", src.getClass().getCanonicalName(), min, max, (System.currentTimeMillis() - start) / 1000.0d);
                });

        Arrays.stream(sources).parallel()
                .forEach(src -> {
                    PRandom random = new FastPRandom();
                    long start = System.currentTimeMillis();
                    double min = Double.MAX_VALUE;
                    double max = Double.MIN_VALUE;
                    for (int i = 0; i < SAMPLES; i++)   {
                        double val = src.get(random.nextDouble(-RANGE, RANGE), random.nextDouble(-RANGE, RANGE));
                        min = Math.min(val, min);
                        max = Math.max(val, max);
                    }
                    System.out.printf("2D %s: min=%f, max=%f, time=%.2fs\n", src.getClass().getCanonicalName(), min, max, (System.currentTimeMillis() - start) / 1000.0d);
                });

        Arrays.stream(sources).parallel()
                .forEach(src -> {
                    PRandom random = new FastPRandom();
                    long start = System.currentTimeMillis();
                    double min = Double.MAX_VALUE;
                    double max = Double.MIN_VALUE;
                    for (int i = 0; i < SAMPLES; i++)   {
                        double val = src.get(random.nextDouble(-RANGE, RANGE), random.nextDouble(-RANGE, RANGE), random.nextDouble(-RANGE, RANGE));
                        min = Math.min(val, min);
                        max = Math.max(val, max);
                    }
                    System.out.printf("3D %s: min=%f, max=%f, time=%.2fs\n", src.getClass().getCanonicalName(), min, max, (System.currentTimeMillis() - start) / 1000.0d);
                });
    }
}
