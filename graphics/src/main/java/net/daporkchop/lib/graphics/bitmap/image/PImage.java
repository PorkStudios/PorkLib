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
import net.daporkchop.lib.graphics.bitmap.icon.PIcon;
import net.daporkchop.lib.graphics.impl.image.DirectImage;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * An abstract representation of an image.
 *
 * @author DaPorkchop_
 */
public interface PImage extends PIcon {
    static PImage randomImage(int w, int h) {
        return randomImage(w, h, false, ThreadLocalRandom.current());
    }

    static PImage randomImage(int w, int h, boolean bw) {
        return randomImage(w, h, bw, ThreadLocalRandom.current());
    }

    static PImage randomImage(int w, int h, boolean bw, @NonNull Random random) {
        PImage img = new DirectImage(w, h, bw);
        if (bw) {
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
    void setARGB(int x, int y, int argb);

    default void setRGB(int x, int y, int rgb)  {
        this.setARGB(x, y, 0xFF000000 | rgb);
    }

    default void setBW(int x, int y, int col)   {
        this.setARGB(x, y, 0xFF000000 | (col << 16) | (col << 8) | col);
    }

    default void copy(@NonNull PIcon src, int srcX, int srcY, int dstX, int dstY, int w, int h) {
        for (int x = w - 1; x >= 0; x--)    {
            for (int y = h - 1; y >= 0; y--)    {
                this.setARGB(dstX + x, dstY + y, src.getARGB(srcX + x, srcY + y));
            }
        }
    }

    //drawing methods
    default void drawRect(int x, int y, int w, int h, int argb) {
        for (; w >= 0; w--) {
            for (int yy = h - 1; yy >= 0; yy--) {
                this.setARGB(x + w, y + yy, argb);
            }
        }
    }

    default void fill(int argb) {
        for (int x = this.getWidth() - 1; x >= 0; x--)  {
            for (int y = this.getHeight() - 1; y >= 0; y--) {
                this.setARGB(x, y, argb);
            }
        }
    }
}
