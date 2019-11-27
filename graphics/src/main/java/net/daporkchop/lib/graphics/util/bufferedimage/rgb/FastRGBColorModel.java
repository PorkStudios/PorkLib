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

package net.daporkchop.lib.graphics.util.bufferedimage.rgb;

import net.daporkchop.lib.reflection.PField;

import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;

/**
 * @author DaPorkchop_
 */
public final class FastRGBColorModel extends ColorModel {
    protected static final PField field_numComponents = PField.of(ColorModel.class, "numComponents");
    protected static final PField field_supportsAlpha = PField.of(ColorModel.class, "supportsAlpha");

    public FastRGBColorModel() {
        super(24);

        field_numComponents.setInt(this, 3);
        field_supportsAlpha.setBoolean(this, false);
    }

    @Override
    public int getAlpha(int pixel) {
        return 0xFF;
    }

    @Override
    public int getRed(int pixel) {
        return (pixel >>> 16) & 0xFF;
    }

    @Override
    public int getGreen(int pixel) {
        return (pixel >>> 8) & 0xFF;
    }

    @Override
    public int getBlue(int pixel) {
        return pixel & 0xFF;
    }

    @Override
    public int getAlpha(Object inData) {
        return 0xFF;
    }

    @Override
    public int getRed(Object inData) {
        return this.getRed(this.getRGB(inData));
    }

    @Override
    public int getGreen(Object inData) {
        return this.getGreen(this.getRGB(inData));
    }

    @Override
    public int getBlue(Object inData) {
        return this.getBlue(this.getRGB(inData));
    }

    @Override
    public int getRGB(Object inData) {
        return 0xFF000000 | ((int[]) inData)[0];
    }

    @Override
    public boolean isCompatibleRaster(Raster raster) {
        return raster instanceof ImageRGBRaster;
    }

    @Override
    public boolean isCompatibleSampleModel(SampleModel sm) {
        return sm instanceof BiggerRGBSampleModel;
    }

    @Override
    public Object getDataElements(int rgb, Object pixel) {
        int[] i;
        if (pixel instanceof int[]) {
            i = (int[]) pixel;
        } else {
            i = new int[1];
        }
        i[0] = rgb & 0xFFFFFF;
        return i;
    }
}
