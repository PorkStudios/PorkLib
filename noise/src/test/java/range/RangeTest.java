/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

package range;

import net.daporkchop.lib.noise.Noise;
import net.daporkchop.lib.noise.NoiseEngineType;
import net.daporkchop.lib.noise.engine.INoiseEngine;
import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author DaPorkchop_
 */
public abstract class RangeTest {
    private static final int SAMPLES = 256000;

    @Test
    public void test1d_raw() {
        INoiseEngine engine = this.getEngine().getEngine(System.currentTimeMillis());
        double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        for (int j = 0; j < SAMPLES; j++) {
            //double val = engine.get1d(ThreadLocalRandom.current().nextDouble(-1000000d, 1000000d)); //TODO: make this work with non floating-point values
            double val = engine.get(ThreadLocalRandom.current().nextDouble(-1000000d, 1000000d)); //TODO: make this work with non floating-point values
            if (val < min) {
                min = val;
            }
            if (val > max) {
                max = val;
            }
        }
        System.out.println("1D RAW NOISE min: " + min + ", max: " + max);
    }

    /*@Test
    public void test1d_octaves() {
        for (int i = 1; i < 17; i++) {
            Noise noise = getNoise(i);
            double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
            for (int j = 0; j < SAMPLES; j++) {
                double val = noise.get1d(ThreadLocalRandom.current().nextDouble(-1000000d, 1000000d));
                if (val < min) {
                    min = val;
                }
                if (val > max) {
                    max = val;
                }
            }
            System.out.println("1D Octaves: " + i + ", min: " + min + ", max: " + max);
        }
    }*/

    @Test
    public void test2d_raw() {
        INoiseEngine engine = this.getEngine().getEngine(System.currentTimeMillis());
        double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        for (int j = 0; j < SAMPLES; j++) {
            double val = engine.get(
                    ThreadLocalRandom.current().nextDouble(-1000000d, 1000000d),
                    ThreadLocalRandom.current().nextDouble(-1000000d, 1000000d));
            if (val < min) {
                min = val;
            }
            if (val > max) {
                max = val;
            }
        }
        System.out.println("2D RAW NOISE min: " + min + ", max: " + max);
    }

    @Test
    public void test3d_raw() {
        INoiseEngine engine = this.getEngine().getEngine(System.currentTimeMillis());
        double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        for (int j = 0; j < SAMPLES; j++) {
            double val = engine.get(
                    ThreadLocalRandom.current().nextDouble(-1000000d, 1000000d),
                    ThreadLocalRandom.current().nextDouble(-1000000d, 1000000d),
                    ThreadLocalRandom.current().nextDouble(-1000000d, 1000000d));
            if (val < min) {
                min = val;
            }
            if (val > max) {
                max = val;
            }
        }
        System.out.println("3D RAW NOISE min: " + min + ", max: " + max);
    }

    @Test
    public void test4d_raw() {
        INoiseEngine engine = this.getEngine().getEngine(System.currentTimeMillis());
        double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        for (int j = 0; j < SAMPLES; j++) {
            double val = engine.get(
                    ThreadLocalRandom.current().nextDouble(-1000000d, 1000000d),
                    ThreadLocalRandom.current().nextDouble(-1000000d, 1000000d),
                    ThreadLocalRandom.current().nextDouble(-1000000d, 1000000d),
                    ThreadLocalRandom.current().nextDouble(-1000000d, 1000000d));
            if (val < min) {
                min = val;
            }
            if (val > max) {
                max = val;
            }
        }
        System.out.println("4D RAW NOISE min: " + min + ", max: " + max);
    }

    private final Noise getNoise(int octaves) {
        return new Noise(this.getEngine(), System.currentTimeMillis(), octaves, 0.001352D, 2 / 4F);
    }

    protected abstract NoiseEngineType getEngine();
}
