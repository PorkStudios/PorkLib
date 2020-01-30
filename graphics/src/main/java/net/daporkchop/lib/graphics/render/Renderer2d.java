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

package net.daporkchop.lib.graphics.render;

import lombok.NonNull;
import net.daporkchop.lib.graphics.bitmap.PBitmap;

import java.awt.*;

/**
 * Used for rendering simple 2D shapes.
 * 
 * @author DaPorkchop_
 */
public interface Renderer2d {
    int width();
    int height();

    Renderer2d setColor(int argb);
    default Renderer2d setColor(@NonNull Color color) {
        return this.setColor(color.getRGB());
    }
    default Renderer2d setColor(int a, int r, int g, int b)   {
        return this.setColor((a << 24) | (r << 16) | (g << 8) | b);
    }
    int getColor();

    Renderer2d pixel(int x, int y, int argb);
    default Renderer2d pixel(int x, int y)    {
        return this.pixel(x, y, this.getColor());
    }

    Renderer2d line(int x1, int y1, int x2, int y2, int argb);
    default Renderer2d line(int x1, int y1, int x2, int y2)   {
        return this.line(x1, y1, x2, y2, this.getColor());
    }

    Renderer2d rect(int x, int y, int w, int h, int argb, boolean fill);
    default Renderer2d rect(int x, int y, int w, int h) {
        return this.rect(x, y, w, h, this.getColor(), true);
    }
    default Renderer2d rect(int x, int y, int w, int h, int argb) {
        return this.rect(x, y, w, h, argb, true);
    }
    default Renderer2d rect(int x, int y, int w, int h, boolean fill) {
        return this.rect(x, y, w, h, this.getColor(), fill);
    }

    Renderer2d roundRect(int x, int y, int w, int h, int arcW, int arcH, int argb, boolean fill);
    default Renderer2d roundRect(int x, int y, int w, int h, int arcW, int arcH)  {
        return this.roundRect(x, y, w, h, arcW, arcH, this.getColor(), true);
    }
    default Renderer2d roundRect(int x, int y, int w, int h, int arcW, int arcH, int argb)  {
        return this.roundRect(x, y, w, h, arcW, arcH, argb, true);
    }
    default Renderer2d roundRect(int x, int y, int w, int h, int arcW, int arcH, boolean fill)  {
        return this.roundRect(x, y, w, h, arcW, arcH, this.getColor(), fill);
    }

    Renderer2d oval(int x, int y, int w, int h, int argb, boolean fill);
    default Renderer2d oval(int x, int y, int w, int h)   {
        return this.oval(x, y, w, h, this.getColor(), true);
    }
    default Renderer2d oval(int x, int y, int w, int h, int argb)   {
        return this.oval(x, y, w, h, argb, true);
    }
    default Renderer2d oval(int x, int y, int w, int h, boolean fill)   {
        return this.oval(x, y, w, h, this.getColor(), fill);
    }

    Renderer2d arc(int x, int y, int w, int h, int startAngle, int angle, int argb, boolean fill);
    default Renderer2d arc(int x, int y, int w, int h, int startAngle, int angle) {
        return this.arc(x, y, w, h, startAngle, angle, this.getColor(), true);
    }
    default Renderer2d arc(int x, int y, int w, int h, int startAngle, int angle, int argb) {
        return this.arc(x, y, w, h, startAngle, angle, argb, true);
    }
    default Renderer2d arc(int x, int y, int w, int h, int startAngle, int angle, boolean fill) {
        return this.arc(x, y, w, h, startAngle, angle, this.getColor(), fill);
    }

    @SuppressWarnings("unchecked")
    default Renderer2d polyline(@NonNull int[] xPoints, @NonNull int[] yPoints, int length, int argb) {
        if (xPoints.length < length)   {
            throw new IllegalArgumentException(String.format("Invalid number of X points: %d (required: %d)", xPoints.length, length));
        } else if (yPoints.length < length) {
            throw new IllegalArgumentException(String.format("Invalid number of Y points: %d (required: %d)", yPoints.length, length));
        } else {
            for (int i = length - 2; i >= 0; i--)    {
                this.line(xPoints[i + 1], yPoints[i + 1], xPoints[i], yPoints[i], argb);
            }
            return this;
        }
    }
    default Renderer2d polyline(@NonNull int[] xPoints, @NonNull int[] yPoints, int length) {
        return this.polyline(xPoints, yPoints, length, this.getColor());
    }

    default Renderer2d polygon(@NonNull int[] xPoints, @NonNull int[] yPoints, int length, int argb)  {
        if (xPoints.length < length)   {
            throw new IllegalArgumentException(String.format("Invalid number of X points: %d (required: %d)", xPoints.length, length));
        } else if (yPoints.length < length) {
            throw new IllegalArgumentException(String.format("Invalid number of Y points: %d (required: %d)", yPoints.length, length));
        } else {
            int prevI = length - 1;
            for (int i = 0; i < length; i++)    {
                this.line(xPoints[i], yPoints[i], xPoints[prevI], yPoints[prevI], argb);
                prevI = i;
            }
            return this;
        }
    }
    default Renderer2d polygon(@NonNull int[] xPoints, @NonNull int[] yPoints, int length) {
        return this.polygon(xPoints, yPoints, length, this.getColor());
    }

    Renderer2d fillPolygon(@NonNull int[] xPoints, @NonNull int[] yPoints, int length, int argb);
    default Renderer2d fillPolygon(@NonNull int[] xPoints, @NonNull int[] yPoints, int length) {
        return this.fillPolygon(xPoints, yPoints, length, this.getColor());
    }

    Renderer2d text(@NonNull String str, int x, int y, int argb);
    default Renderer2d text(@NonNull String str, int x, int y)  {
        return this.text(str, x, y, this.getColor());
    }

    Renderer2d text(@NonNull CharSequence seq, int x, int y, int argb);
    default Renderer2d text(@NonNull CharSequence seq, int x, int y)  {
        return this.text(seq, x, y, this.getColor());
    }

    Renderer2d text(@NonNull char[] chars, int x, int y, int offset, int len, int argb);
    default Renderer2d text(@NonNull char[] chars, int x, int y)   {
        return this.text(chars, x, y, 0, chars.length, this.getColor());
    }
    default Renderer2d text(@NonNull char[] chars, int x, int y, int offset, int len)   {
        return this.text(chars, x, y, offset, len, this.getColor());
    }
    default Renderer2d text(@NonNull char[] chars, int x, int y, int argb)   {
        return this.text(chars, x, y, 0, chars.length, argb);
    }

    Renderer2d text(@NonNull byte[] chars, int x, int y, int offset, int len, int argb);
    default Renderer2d text(@NonNull byte[] chars, int x, int y)   {
        return this.text(chars, x, y, 0, chars.length, this.getColor());
    }
    default Renderer2d text(@NonNull byte[] chars, int x, int y, int offset, int len)   {
        return this.text(chars, x, y, offset, len, this.getColor());
    }
    default Renderer2d text(@NonNull byte[] chars, int x, int y, int argb)   {
        return this.text(chars, x, y, 0, chars.length, argb);
    }

    Renderer2d image(@NonNull PBitmap icon, int x, int y);
    Renderer2d image(@NonNull PBitmap icon, int x, int y, int w, int h);
    Renderer2d image(@NonNull PBitmap icon, int x, int y, int bgArgb);
    Renderer2d image(@NonNull PBitmap icon, int x, int y, int w, int h, int bgArgb);
    Renderer2d image(@NonNull PBitmap icon, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2);
    Renderer2d image(@NonNull PBitmap icon, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, int bgArgb);

    Renderer2d copy(int srcX, int srcY, int w, int h, int dstX, int dstY);

    Renderer2d clear(int x, int y, int w, int h);
    default Renderer2d clear()    {
        return this.clear(0, 0, this.width(), this.height());
    }
}
