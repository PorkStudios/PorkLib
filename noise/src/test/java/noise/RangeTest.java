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

import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.random.PRandom;
import net.daporkchop.lib.random.impl.FastPRandom;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static noise.NoiseTests.*;

/**
 * @author DaPorkchop_
 */
public class RangeTest {
    private static final double RANGE   = 100000.0d;
    private static final int    SAMPLES = 20000000;

    @Test
    public void testRange() {
        Arrays.stream(DEFAULT_SOURCES).parallel()
                .map(src -> src.toRange(-1.0d, 1.0d))
                .map(src -> {
                    PRandom random = new FastPRandom();
                    long start = System.nanoTime();
                    double min = Double.MAX_VALUE;
                    double max = Double.MIN_VALUE;
                    for (int i = 0; i < SAMPLES; i++) {
                        double val = src.get(random.nextDouble(-RANGE, RANGE));
                        min = Math.min(val, min);
                        max = Math.max(val, max);
                    }
                    if (min < -1.0d || max > 1.0d)  {
                        throw new IllegalStateException(String.format("Out of range [-1,1] %s: min=%f,max=%f", src, min, max));
                    }
                    return new Sample(1, src.toString(), min, max, System.nanoTime() - start);
                })
                .collect(Collectors.toList()).stream().sorted()
                .forEach(Sample::print);
        System.out.println();

        Arrays.stream(DEFAULT_SOURCES).parallel()
                .map(src -> src.toRange(-1.0d, 1.0d))
                .map(src -> {
                    PRandom random = new FastPRandom();
                    long start = System.nanoTime();
                    double min = Double.MAX_VALUE;
                    double max = Double.MIN_VALUE;
                    for (int i = 0; i < SAMPLES; i++) {
                        double val = src.get(random.nextDouble(-RANGE, RANGE), random.nextDouble(-RANGE, RANGE));
                        min = Math.min(val, min);
                        max = Math.max(val, max);
                    }
                    if (min < -1.0d || max > 1.0d)  {
                        throw new IllegalStateException(String.format("Out of range [-1,1] %s: min=%f,max=%f", src, min, max));
                    }
                    return new Sample(2, src.toString(), min, max, System.nanoTime() - start);
                })
                .collect(Collectors.toList()).stream().sorted()
                .forEach(Sample::print);
        System.out.println();

        Arrays.stream(DEFAULT_SOURCES).parallel()
                .map(src -> src.toRange(-1.0d, 1.0d))
                .map(src -> {
                    PRandom random = new FastPRandom();
                    long start = System.nanoTime();
                    double min = Double.MAX_VALUE;
                    double max = Double.MIN_VALUE;
                    for (int i = 0; i < SAMPLES; i++) {
                        double val = src.get(random.nextDouble(-RANGE, RANGE), random.nextDouble(-RANGE, RANGE), random.nextDouble(-RANGE, RANGE));
                        min = Math.min(val, min);
                        max = Math.max(val, max);
                    }
                    if (min < -1.0d || max > 1.0d)  {
                        throw new IllegalStateException(String.format("Out of range [-1,1] %s: min=%f,max=%f", src, min, max));
                    }
                    return new Sample(3, src.toString(), min, max, System.nanoTime() - start);
                })
                .collect(Collectors.toList()).stream().sorted()
                .forEach(Sample::print);
    }

    @RequiredArgsConstructor
    private static final class Sample implements Comparable<Sample> {
        private final int    dimensions;
        private final String string;
        private final double min;
        private final double max;
        private final long   time;

        @Override
        public int compareTo(Sample o) {
            return Long.compare(this.time, o.time);
        }

        public void print() {
            System.out.printf("%dD %s: min=%f, max=%f, time=%.2fs\n", this.dimensions, this.string, this.min, this.max, this.time / 1_000_000_000.0d);
        }
    }
}
