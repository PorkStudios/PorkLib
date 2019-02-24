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

package net.daporkchop.lib.graphics.swing;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.graphics.AbstractImageProducer;
import net.daporkchop.lib.graphics.PIcon;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.lang.ref.SoftReference;

/**
 * @author DaPorkchop_
 */
@Getter
public class PIconAWTImage extends Image {
    protected final PIcon delegate;

    protected final ImageProducer source;

    public PIconAWTImage(@NonNull PIcon delegate)   {
        this.delegate = delegate;

        this.source = new ImplImageProducer();
    }

    @Override
    public int getWidth(ImageObserver observer) {
        return this.delegate.getWidth();
    }

    @Override
    public int getHeight(ImageObserver observer) {
        return this.delegate.getHeight();
    }

    @Override
    public Graphics getGraphics() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(new BufferedImage(1, 1, this.delegate.isBW() ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_INT_ARGB));
    }

    @Override
    public Object getProperty(String name, ImageObserver observer) {
        return Image.UndefinedProperty;
    }

    @Getter
    protected class ImplImageProducer extends AbstractImageProducer {
        protected final ColorModel colorModel;
        protected SoftReference buf;

        public ImplImageProducer() {
            super(PIconAWTImage.this.delegate.getWidth(), PIconAWTImage.this.delegate.getHeight());

            if (PIconAWTImage.this.delegate.isBW())   {
                ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
                int[] nBits = {8};
                this.colorModel = new ComponentColorModel(cs, nBits, false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
            } else {
                this.colorModel = ColorModel.getRGBdefault();
            }
        }

        @Override
        protected void sendPixels(@NonNull ImageConsumer ic, int x, int y, int w, int h) {
            Object o;
            if (this.buf == null || (o = this.buf.get()) == null) {
                this.buf = new SoftReference<>(o = PIconAWTImage.this.delegate.isBW() ? new byte[this.width * this.height] : new int[this.width * this.height]);
            }
            if (PIconAWTImage.this.delegate.isBW())   {
                ic.setPixels(x, y, w, h, this.colorModel, (byte[]) o, 0, w);
            } else {
                ic.setPixels(x, y, w, h, this.colorModel, (int[]) o, 0, w);
            }
        }
    }
}
