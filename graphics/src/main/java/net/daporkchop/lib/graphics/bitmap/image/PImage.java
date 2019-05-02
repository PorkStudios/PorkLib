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

package net.daporkchop.lib.graphics.bitmap.image;

import lombok.NonNull;
import net.daporkchop.lib.graphics.bitmap.ColorFormat;
import net.daporkchop.lib.graphics.bitmap.icon.PIcon;
import net.daporkchop.lib.graphics.render.GraphicsRenderer2d;
import net.daporkchop.lib.graphics.render.Renderer2d;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * An abstract representation of an image.
 *
 * @author DaPorkchop_
 */
public interface PImage extends PIcon {
    static PImage randomImage(int w, int h) {
        return randomImage(w, h, ColorFormat.RGB, ThreadLocalRandom.current());
    }

    static PImage randomImage(int w, int h, @NonNull ColorFormat format) {
        return randomImage(w, h, format, ThreadLocalRandom.current());
    }

    static PImage randomImage(int w, int h, @NonNull ColorFormat format, @NonNull Random random) {
        PImage img = format.createImage(w, h);
        if (format.isBw()) {
            for (int x = w - 1; x >= 0; x--)    {
                for (int y = h - 1; y >= 0; y--)    {
                    img.setBW(x, y, random.nextInt());
                }
            }
        } else {
            for (int x = w - 1; x >= 0; x--)    {
                for (int y = h - 1; y >= 0; y--)    {
                    img.setRGB(x, y, random.nextInt());
                }
            }
        }
        return img;
    }

    //pixel stuff
    void setARGB(int x, int y, int col);

    void setRGB(int x, int y, int col);

    void setABW(int x, int y, int col);

    void setBW(int x, int y, int col);

    default void copy(@NonNull PIcon src, int srcX, int srcY, int dstX, int dstY, int w, int h) {
        for (int x = w - 1; x >= 0; x--)    {
            for (int y = h - 1; y >= 0; y--)    {
                this.setARGB(dstX + x, dstY + y, src.getARGB(srcX + x, srcY + y));
            }
        }
    }

    default void fill(int argb) {
        this.fillARGB(argb);
    }

    default void fillARGB(int col) {
        for (int x = this.getWidth() - 1; x >= 0; x--)  {
            for (int y = this.getHeight() - 1; y >= 0; y--) {
                this.setARGB(x, y, col);
            }
        }
    }

    default void fillRGB(int col) {
        for (int x = this.getWidth() - 1; x >= 0; x--)  {
            for (int y = this.getHeight() - 1; y >= 0; y--) {
                this.setRGB(x, y, col);
            }
        }
    }

    default void fillABW(int col) {
        for (int x = this.getWidth() - 1; x >= 0; x--)  {
            for (int y = this.getHeight() - 1; y >= 0; y--) {
                this.setABW(x, y, col);
            }
        }
    }

    default void fillBW(int col) {
        for (int x = this.getWidth() - 1; x >= 0; x--)  {
            for (int y = this.getHeight() - 1; y >= 0; y--) {
                this.setBW(x, y, col);
            }
        }
    }

    /**
     * Gets an instance of {@link Renderer2d} that will draw to this image.
     * @return an instance of {@link Renderer2d}
     */
    default Renderer2d getRenderer()    {
        return new GraphicsRenderer2d(this.getAsImage().getGraphics());
    }
}
