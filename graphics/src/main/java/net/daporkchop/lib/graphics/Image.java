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

package net.daporkchop.lib.graphics;

import javax.swing.*;

/**
 * A very simple representation of an image.
 *
 * @author DaPorkchop_
 */
public interface Image {
    ColorModel getColorModel();

    int getWidth();

    int getHeight();

    default boolean isSquare()  {
        return this.getWidth() == this.getHeight();
    }

    default boolean isEmpty()   {
        return this.getWidth() == 0 || this.getHeight() == 0;
    }

    //pixel access methods
    int get(int x, int y);

    default int getAlpha(int x, int y)  {
        return this.getColorModel().getAlpha(this.get(x, y));
    }

    default int getRed(int x, int y)  {
        return this.getColorModel().getRed(this.get(x, y));
    }

    default int getGreen(int x, int y)  {
        return this.getColorModel().getGreen(this.get(x, y));
    }

    default int getBlue(int x, int y)  {
        return this.getColorModel().getBlue(this.get(x, y));
    }

    Image set(int x, int y, int color);

    default Image set(int x, int y, int a, int r, int g, int b) {
        return this.set(x, y, this.getColorModel().getColor(a, r, g, b));
    }

    default Image get(int x, int y, int w, int h, int[] buf)    {
        if (buf.length < w * h)    {
            throw new IllegalArgumentException(String.format("Buffer length %d cannot hold area of size %dx%d!", buf.length, w, h));
        }
        for (int ww = w - 1; ww >= 0; ww--) {
            for (int hh = h - 1; hh >= 0; hh--) {
                buf[ww * h + hh] = this.get(x + ww, y + hh);
            }
        }
        return this;
    }

    default Image set(int x, int y, int w, int h, int[] buf)    {
        if (buf.length < w * h)    {
            throw new IllegalArgumentException(String.format("Buffer length %d cannot hold area of size %dx%d!", buf.length, w, h));
        }
        for (int ww = w - 1; ww >= 0; ww--) {
            for (int hh = h - 1; hh >= 0; hh--) {
                this.set(x + ww, y + hh, buf[ww * h + hh]);
            }
        }
        return this;
    }
}
