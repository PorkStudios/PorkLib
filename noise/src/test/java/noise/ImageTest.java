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

package noise;


import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.imaging.color.ColorFormatBW;
import net.daporkchop.lib.imaging.color.ColorFormatRGB;
import net.daporkchop.lib.noise.NoiseSource;

import java.awt.image.BufferedImage;

import static net.daporkchop.lib.common.math.PMath.*;
import static noise.NoiseTests.*;

/**
 * @author DaPorkchop_
 */
public class ImageTest {
    public static void main(String... args) {
        int size = 512;
        double scale = 0.025d;

        BufferedImage img = new BufferedImage(size << 1, size, BufferedImage.TYPE_INT_ARGB);

        for (NoiseSource src : ALL_SOURCES) {
            src = src.toRange(-1.0d, 1.0d);
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    double val = src.get(x * scale, y * scale);
                    if (val < -1.0d || val > 1.0d) {
                        throw new IllegalStateException(String.format("(%d,%d) (%f,%f): %f", x, y, x * scale, y * scale, val));
                    }
                    int col = val < 0.0d
                            ? lerpI(0x00, 0xFF, -val) << 16
                            : lerpI(0x00, 0xFF, val) << 8;
                    img.setRGB(x, y, ColorFormatRGB.toARGB(col));

                    img.setRGB(size + x, y, ColorFormatBW.toARGB(lerpI(0x00, 0xFF, val * 0.5d + 0.5d)));
                }
            }
            System.out.println(src);
            PorkUtil.simpleDisplayImage(true, img);
        }
    }
}
