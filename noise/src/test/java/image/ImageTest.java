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

package image;

import net.daporkchop.lib.noise.Noise;
import net.daporkchop.lib.noise.NoiseEngineType;
import net.daporkchop.lib.noise.engine.INoiseEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author DaPorkchop_
 */
public abstract class ImageTest {
    protected abstract NoiseEngineType getType();

    public void test() {

        INoiseEngine engine = this.getType().getEngine(System.currentTimeMillis());
        BufferedImage image = new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB);

        {
            Noise noise = new Noise(this.getType(), System.currentTimeMillis(), 8, 0.005d, 0.5d);
            noise.forEach(-256d, 512, 1, ((x, d) -> image.setRGB(
                    x
                    //(int) (x * 100.0d + 256.0d)
                    , (int) ((d + 1.0d) * 127.0d), Color.RED.getRGB())));

            JLabel picLabel = new JLabel(new ImageIcon(image));
            JOptionPane.showMessageDialog(null, picLabel, "jeff", JOptionPane.PLAIN_MESSAGE, null);
        }

        {
            for (int x = 0; x < 512; x++) {
                for (int y = 0; y < 512; y++) {
                    image.setRGB(x, y, 0);
                }
            }
            Noise noise = new Noise(this.getType(), System.currentTimeMillis(), 8, 0.02d, 0.5d);
            noise.forEach(-256d, -256d, 512, 512, 16, 16, ((x, y, d) -> {
                int i = (int) ((d + 1.0d) * 127.0d);
                image.setRGB(
                        x,
                        y,
                        (i << 16) | (i << 8) | i);
            }));

            JLabel picLabel = new JLabel(new ImageIcon(image));
            JOptionPane.showMessageDialog(null, picLabel, "jeff", JOptionPane.PLAIN_MESSAGE, null);
        }

        {
            for (int x = 0; x < 512; x++) {
                for (int y = 0; y < 512; y++) {
                    image.setRGB(x, y, 0);
                }
            }
            Noise noise = new Noise(this.getType(), System.currentTimeMillis(), 8, 0.02d, 0.5d);
            noise.forEach(-256d, -256d, -256d, 512, 512, 512, 16, 16, 16, ((x, y, z, d) -> {
                if (z != 123) {
                    return;
                }

                int i = (int) ((d + 1.0d) * 127.0d);
                image.setRGB(
                        x,
                        y,
                        (i << 16) | (i << 8) | i);
            }));

            JLabel picLabel = new JLabel(new ImageIcon(image));
            JOptionPane.showMessageDialog(null, picLabel, "jeff", JOptionPane.PLAIN_MESSAGE, null);
        }

        {
            double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
            for (int x = 0; x < 512; x++) {
                for (int y = 0; y < 512; y++) {
                    double d = engine.get(x * 0.02d - 5.0d, y * 0.02d - 5.0d);
                    //d = Math.floor(d);
                    if (d < min) {
                        min = d;
                    }
                    if (d > max) {
                        max = d;
                    }
                    int i = (int) ((d + 1.0d) * 127.0d);
                    image.setRGB(x, y, (i << 16) | (i << 8) | i);
                }
            }

            JLabel picLabel = new JLabel(new ImageIcon(image));
            JOptionPane.showMessageDialog(null, picLabel, "min: " + min + ", max: " + max, JOptionPane.PLAIN_MESSAGE, null);
        }

        {
            double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
            for (int x = 0; x < 512; x++) {
                double d = engine.get(x * 0.02d - 5.0d);
                //d = Math.floor(d);
                if (d < min) {
                    min = d;
                }
                if (d > max) {
                    max = d;
                }
                int i = (int) ((d + 1.0d) * 127.0d);
                for (int y = 0; y < 512; y++) {
                    image.setRGB(x, y, (i << 16) | (i << 8) | i);
                }
            }

            JLabel picLabel = new JLabel(new ImageIcon(image));
            JOptionPane.showMessageDialog(null, picLabel, "min: " + min + ", max: " + max, JOptionPane.PLAIN_MESSAGE, null);
        }
    }
}
