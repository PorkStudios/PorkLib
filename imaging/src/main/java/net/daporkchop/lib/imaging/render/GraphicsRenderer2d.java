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

package net.daporkchop.lib.imaging.render;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.imaging.bitmap.PBitmap;

import java.awt.*;

/**
 * Implementation of {@link Renderer2d}
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public final class GraphicsRenderer2d implements Renderer2d {
    protected final Graphics delegate;

    protected int color = 0;

    @Override
    public int width() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int height() {
        return Integer.MAX_VALUE;
    }

    @Override
    public GraphicsRenderer2d setColor(int argb) {
        if (argb != this.color) {
            this.delegate.setColor(new Color(this.color = argb));
        }
        return this;
    }

    @Override
    public GraphicsRenderer2d setColor(@NonNull Color color) {
        if (color.getRGB() != this.color)   {
            this.delegate.setColor(color);
            this.color = color.getRGB();
        }
        return this;
    }

    @Override
    public int getColor() {
        return this.delegate.getColor().getRGB();
    }

    @Override
    public GraphicsRenderer2d pixel(int x, int y, int argb) {
        this.setColor(argb);
        this.delegate.drawLine(x, y, x, y);
        return this;
    }

    @Override
    public GraphicsRenderer2d line(int x1, int y1, int x2, int y2, int argb) {
        this.setColor(argb);
        this.delegate.drawLine(x1, y1, x2, y2);
        return this;
    }

    @Override
    public GraphicsRenderer2d rect(int x, int y, int w, int h, int argb, boolean fill) {
        this.setColor(argb);
        if (fill)   {
            this.delegate.fillRect(x, y, w, h);
        } else {
            this.delegate.drawRect(x, y, w, h);
        }
        return this;
    }

    @Override
    public GraphicsRenderer2d roundRect(int x, int y, int w, int h, int arcW, int arcH, int argb, boolean fill) {
        this.setColor(argb);
        if (fill)   {
            this.delegate.fillRoundRect(x, y, w, h, arcW, arcH);
        } else {
            this.delegate.drawRoundRect(x, y, w, h, arcW, arcH);
        }
        return this;
    }

    @Override
    public GraphicsRenderer2d oval(int x, int y, int w, int h, int argb, boolean fill) {
        this.setColor(argb);
        if (fill)   {
            this.delegate.fillOval(x, y, w, h);
        } else {
            this.delegate.drawOval(x, y, w, h);
        }
        return this;
    }

    @Override
    public GraphicsRenderer2d arc(int x, int y, int w, int h, int startAngle, int angle, int argb, boolean fill) {
        this.setColor(argb);
        if (fill)   {
            this.delegate.fillArc(x, y, w, h, startAngle, angle);
        } else {
            this.delegate.drawArc(x, y, w, h, startAngle, angle);
        }
        return this;
    }

    @Override
    public GraphicsRenderer2d polyline(@NonNull int[] xPoints, @NonNull int[] yPoints, int length, int argb) {
        this.setColor(argb).delegate.drawPolyline(xPoints, yPoints, length);
        return this;
    }

    @Override
    public GraphicsRenderer2d polygon(@NonNull int[] xPoints, @NonNull int[] yPoints, int length, int argb) {
        this.setColor(argb).delegate.drawPolygon(xPoints, yPoints, length);
        return this;
    }

    @Override
    public GraphicsRenderer2d fillPolygon(@NonNull int[] xPoints, @NonNull int[] yPoints, int length, int argb) {
        this.setColor(argb).delegate.fillPolygon(xPoints, yPoints, length);
        return this;
    }

    @Override
    public GraphicsRenderer2d text(@NonNull String str, int x, int y, int argb) {
        this.setColor(argb).delegate.drawString(str, x, y);
        return this;
    }

    @Override
    public GraphicsRenderer2d text(@NonNull CharSequence seq, int x, int y, int argb) {
        this.setColor(argb).delegate.drawString(seq.toString(), x, y);
        return this;
    }

    @Override
    public GraphicsRenderer2d text(@NonNull char[] chars, int x, int y, int offset, int len, int argb) {
        this.setColor(argb).delegate.drawChars(chars, offset, len, x, y);
        return this;
    }

    @Override
    public GraphicsRenderer2d text(@NonNull byte[] chars, int x, int y, int offset, int len, int argb) {
        this.setColor(argb).delegate.drawBytes(chars, offset, len, x, y);
        return this;
    }

    @Override
    public GraphicsRenderer2d image(@NonNull PBitmap icon, int x, int y) {
        this.delegate.drawImage(icon.asImage(), x, y, null);
        return this;
    }

    @Override
    public GraphicsRenderer2d image(@NonNull PBitmap icon, int x, int y, int w, int h) {
        this.delegate.drawImage(icon.asImage(), x, y, w, h, null);
        return this;
    }

    @Override
    public GraphicsRenderer2d image(@NonNull PBitmap icon, int x, int y, int bgArgb) {
        this.delegate.drawImage(icon.asImage(), x, y, new Color(bgArgb), null);
        return this;
    }

    @Override
    public GraphicsRenderer2d image(@NonNull PBitmap icon, int x, int y, int w, int h, int bgArgb) {
        this.delegate.drawImage(icon.asImage(), x, y, w, h, new Color(bgArgb), null);
        return this;
    }

    @Override
    public GraphicsRenderer2d image(@NonNull PBitmap icon, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2) {
        this.delegate.drawImage(icon.asImage(), dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy1, null);
        return this;
    }

    @Override
    public GraphicsRenderer2d image(@NonNull PBitmap icon, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, int bgArgb) {
        this.delegate.drawImage(icon.asImage(), dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy1, new Color(bgArgb), null);
        return this;
    }

    @Override
    public GraphicsRenderer2d copy(int srcX, int srcY, int w, int h, int dstX, int dstY) {
        this.delegate.copyArea(srcX, srcY, w, h, dstX, dstY);
        return this;
    }

    @Override
    public GraphicsRenderer2d clear(int x, int y, int w, int h) {
        this.delegate.clearRect(x, y, w, h);
        return this;
    }
}
