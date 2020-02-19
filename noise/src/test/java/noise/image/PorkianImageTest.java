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

package noise.image;


import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.noise.NoiseSource;
import net.daporkchop.lib.noise.engine.PorkianNoiseEngine;
import net.daporkchop.lib.random.impl.ThreadLocalPRandom;

import java.awt.image.BufferedImage;

/**
 * @author DaPorkchop_
 */
public class PorkianImageTest {
    public static void main(String... args) {
        BufferedImage img = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);

        NoiseSource src = new PorkianNoiseEngine(ThreadLocalPRandom.current());
        for (int x = 0; x < 512; x++)   {
            for (int y = 0; y < 512; y++)   {
                img.setRGB(x, y, 0xFF000000 | (int) ((src.get(x * 0.025d, y * 0.025d) * 0.5d + 0.5d) * 256.0d));
            }
        }

        PorkUtil.simpleDisplayImage(true, img);
    }
}
