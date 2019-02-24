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

package net.daporkchop.lib.graphics.util;

import lombok.NonNull;

import java.awt.*;

/**
 * Not a drawer like you store stuff in, more like draw-er as in a thing that draws
 * <p>
 * you're welcome for these very meaningful javadocs
 *
 * @author DaPorkchop_
 */
public interface Drawer<Impl extends Drawer> {
    Impl setColor(int argb);
    default Impl setColor(@NonNull Color color) {
        return this.setColor(color.getRGB());
    }
    default Impl setColor(int a, int r, int g, int b)   {
        return this.setColor((a << 24) | (r << 16) | (g << 8) | b);
    }
    int getColor();

    Impl pixel(int x, int y, int argb);
    default Impl pixel(int x, int y)    {
        return this.pixel(x, y, this.getColor());
    }

    Impl rect(int x, int y, int w, int h, int argb);
    default Impl rect(int x, int y, int w, int h) {
        return this.rect(x, y, w, h, this.getColor());
    }
}
