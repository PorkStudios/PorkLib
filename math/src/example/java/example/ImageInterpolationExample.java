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

package example;

import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.math.grid.Grid2d;
import net.daporkchop.lib.math.interpolation.CubicInterpolation;
import net.daporkchop.lib.math.interpolation.Interpolation;
import net.daporkchop.lib.math.interpolation.NearestNeighborInterpolation;
import net.daporkchop.lib.math.interpolation.LinearInterpolation;

import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static net.daporkchop.lib.math.primitive.PMath.*;

/**
 * @author DaPorkchop_
 */
public class ImageInterpolationExample {
    public static void main(String... args) {
        for (Interpolation engine : Stream.of(null
                , NearestNeighborInterpolation.instance()
                , LinearInterpolation.instance()
                , CubicInterpolation.instance()
        ).filter(Objects::nonNull).toArray(Interpolation[]::new)) {
            System.out.println(engine.getClass().getCanonicalName());

            int radius = engine.requiredRadius();
            int size = 16 + radius * 2;
            int factor = 32;
            int scaled = (size - radius * 2) * factor;

            int[] orig = new int[size * size];
            for (int i = 0; i < orig.length; i++) {
                orig[i] = ThreadLocalRandom.current().nextInt();
            }

            BufferedImage img = new BufferedImage(scaled, scaled, BufferedImage.TYPE_INT_RGB);
            Grid2d grid = Grid2d.of(size, size);
            for (int i = 0; i < 3; i++) {
                int shift = i << 3;
                for (int x = size - 1; x >= 0; x--) {
                    for (int y = size - 1; y >= 0; y--) {
                        grid.setI(x, y, (orig[x * size + y] >>> shift) & 0xFF);
                    }
                }
                for (int x = scaled - 1; x >= 0; x--) {
                    for (int y = scaled - 1; y >= 0; y--) {
                        int newVal = engine.getInterpolatedI((double) x / (double) factor + radius, (double) y / (double) factor + radius, grid);
                        img.setRGB(x, y, img.getRGB(x, y) | (clamp(newVal, 0, 255) << shift));
                    }
                }
            }

            PorkUtil.simpleDisplayImage(true, img);
        }
    }
}
