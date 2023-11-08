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

package example.graphics;

import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.imaging.bitmap.PBitmap;
import net.daporkchop.lib.imaging.bitmap.PImage;
import net.daporkchop.lib.imaging.color.ColorFormat;
import net.daporkchop.lib.imaging.interpolation.ImageInterpolator;
import net.daporkchop.lib.imaging.util.Thumbnail;
import net.daporkchop.lib.math.interpolation.CubicInterpolation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

/**
 * @author DaPorkchop_
 */
public class TestingDisplayingOfImages {
    public static void main(String... args) throws InterruptedException, IOException {
        int size = 32;
        for (ColorFormat format : Stream.of(
                null
                , ColorFormat.ARGB
                , ColorFormat.RGB
                , ColorFormat.ABW
                , ColorFormat.BW
        ).filter(Objects::nonNull).toArray(ColorFormat[]::new)) {
            PImage image = format.createImage(size, size);
            long mask = (1L << (long) format.encodedBits()) - 1L;
            for (int x = size - 1; x >= 0; x--) {
                for (int y = size - 1; y >= 0; y--) {
                    image.setRaw(x, y, ThreadLocalRandom.current().nextLong() & mask);
                    //image.setARGB(x, y, ThreadLocalRandom.current().nextInt());
                }
            }

            if (true) {
                image.renderer().fillPolygon(
                        new int[]{
                                5, 10, 5, 0
                        },
                        new int[]{
                                0, 5, 10, 5
                        },
                        4,
                        0xFFFF0000
                );
            }

            if (!ImageIO.write(image.asBufferedImage(), "png", PFiles.ensureParentDirectoryExists(new File(String.format("./test_out/%s.png", PorkUtil.className(format)))))) {
                throw new IllegalStateException("Didn't write image!");
            }

            ImageInterpolator interpolator = new ImageInterpolator(CubicInterpolation.instance());

            if (true) {
                Thumbnail thumbnail = new Thumbnail(64, 32, 16, 8, image.width()).submit(image).bake(interpolator);
                PorkUtil.simpleDisplayImage(true, Arrays.stream(thumbnail.getIcons()).map(PBitmap::asBufferedImage).toArray(BufferedImage[]::new));
            } else {
                PImage scaled = interpolator.interp(image, 16.0d);
                PorkUtil.simpleDisplayImage(true, image.asBufferedImage(), scaled.asBufferedImage());
            }
        }
    }
}
