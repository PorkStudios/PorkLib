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

package net.daporkchop.lib.graphics.util.bufferedimage.bw;

import net.daporkchop.lib.reflection.PField;

import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;

/**
 * @author DaPorkchop_
 */
public final class FastBWColorModel extends ColorModel {
    protected static final PField field_numComponents = PField.of(ColorModel.class, "numComponents");
    protected static final PField field_supportsAlpha = PField.of(ColorModel.class, "supportsAlpha");
    protected static final PField field_transferType = PField.of(ColorModel.class, "transferType");

    public FastBWColorModel() {
        super(8);

        field_numComponents.setInt(this, 1);
        field_supportsAlpha.setBoolean(this, false);
        field_transferType.setInt(this, DataBuffer.TYPE_INT);
    }

    @Override
    public int getAlpha(int pixel) {
        return 0xFF;
    }

    @Override
    public int getRed(int pixel) {
        return pixel;
    }

    @Override
    public int getGreen(int pixel) {
        return pixel;
    }

    @Override
    public int getBlue(int pixel) {
        return pixel;
    }

    @Override
    public int getRGB(int pixel) {
        return 0xFF000000 | (pixel << 16) | (pixel << 8) | pixel;
    }

    @Override
    public int getAlpha(Object inData) {
        return 0xFF;
    }

    @Override
    public int getRed(Object inData) {
        return ((int[]) inData)[0];
    }

    @Override
    public int getGreen(Object inData) {
        return ((int[]) inData)[0];
    }

    @Override
    public int getBlue(Object inData) {
        return ((int[]) inData)[0];
    }

    @Override
    public int getRGB(Object inData) {
        int val = ((int[]) inData)[0];
        return 0xFF000000 | (val << 16) | (val << 8) | val;
    }

    @Override
    public boolean isCompatibleRaster(Raster raster) {
        return raster instanceof ImageBWRaster;
    }

    @Override
    public boolean isCompatibleSampleModel(SampleModel sm) {
        return sm instanceof BiggerBWSampleModel;
    }

    @Override
    public Object getDataElements(int argb, Object pixel) {
        int[] i;
        if (pixel instanceof int[]) {
            i = (int[]) pixel;
        } else {
            i = new int[1];
        }
        i[0] = ((argb >>> 16) & 0xFF) | ((argb >>> 8) & 0xFF) | (argb & 0xFF);
        return i;
    }
}
