/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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

package example.graphics;

import net.daporkchop.lib.graphics.bitmap.ColorFormat;
import net.daporkchop.lib.graphics.bitmap.icon.PIcon;
import net.daporkchop.lib.graphics.bitmap.image.PImage;
import net.daporkchop.lib.graphics.util.ImageInterpolator;
import net.daporkchop.lib.graphics.util.Thumbnail;
import net.daporkchop.lib.math.interpolation.CubicInterpolationEngine;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author DaPorkchop_
 */
public class TestingDisplayingOfImages {
    public static void main(String... args) throws InterruptedException, IOException {
        int size = 20;
        for (ColorFormat format : ColorFormat.values()) {
            PImage image = format.createImage(size, size);
            for (int x = size - 1; x >= 0; x--) {
                for (int y = size - 1; y >= 0; y--) {
                    if (format.isBw()) {
                        image.setABW(x, y, (ThreadLocalRandom.current().nextInt() & 0xFF) | ((y & 1) == 0 ? 0xFF00 : 0x7700));
                    } else {
                        image.setARGB(x, y, (ThreadLocalRandom.current().nextInt() & 0xFFFFFF) | ((y & 1) == 0 ? 0xFF000000 : 0x77000000));
                    }
                }
            }

            if (!ImageIO.write(image.getAsBufferedImage(), "png", new File("./test_out/out.png"))) {
                throw new IllegalStateException("Didn't write image!");
            }

            ImageInterpolator interpolator = new ImageInterpolator(new CubicInterpolationEngine());
            image = interpolator.interp(image, 32.0d);

            Thumbnail thumbnail = new Thumbnail(64, 32, 16, 8, image.getWidth()).submit(image).bake();

            JFrame frame = new JFrame();
            frame.getContentPane().setLayout(new FlowLayout());
            frame.getContentPane().add(new JLabel(format.name()));
            for (PIcon icon : thumbnail.getIcons()) {
                frame.getContentPane().add(new JLabel(icon.getAsSwingIcon()));
            }
            frame.pack();
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    synchronized (frame) {
                        frame.notify();
                    }
                }
            });
            synchronized (frame) {
                frame.wait();
            }
        }
    }
}
