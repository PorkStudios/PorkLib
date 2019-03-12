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

package net.daporkchop.lib.graphics.util.bufferedimage.abw;

import net.daporkchop.lib.reflection.PField;

import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;

/**
 * @author DaPorkchop_
 */
public class FastABWColorModel extends ColorModel {
    protected static PField field_numComponents = PField.of(ColorModel.class, "numComponents");
    protected static PField field_numColorComponents = PField.of(ColorModel.class, "numColorComponents");
    protected static PField field_supportsAlpha = PField.of(ColorModel.class, "supportsAlpha");

    public FastABWColorModel() {
        super(16);

        field_numComponents.setInt(this, 2);
        field_numColorComponents.setInt(this, 1);
        field_supportsAlpha.setBoolean(this, true);
    }

    @Override
    public int getAlpha(int pixel) {
        return (pixel >>> 8) & 0xFF;
    }

    @Override
    public int getRed(int pixel) {
        return pixel & 0xFF;
    }

    @Override
    public int getGreen(int pixel) {
        return pixel & 0xFF;
    }

    @Override
    public int getBlue(int pixel) {
        return pixel & 0xFF;
    }

    @Override
    public int getRGB(int pixel) {
        return (pixel << 16) | ((pixel & 0xFF) << 8) | (pixel & 0xFF);
    }

    @Override
    public int getAlpha(Object inData) {
        return (((int[]) inData)[0] >>> 8) & 0xFF;
    }

    @Override
    public int getRed(Object inData) {
        return ((int[]) inData)[0] & 0xFF;
    }

    @Override
    public int getGreen(Object inData) {
        return ((int[]) inData)[0] & 0xFF;
    }

    @Override
    public int getBlue(Object inData) {
        return ((int[]) inData)[0] & 0xFF;
    }

    @Override
    public int getRGB(Object inData) {
        int val = ((int[]) inData)[0] & 0xFFFF;
        return (val << 16) | ((val & 0xFF) << 8) | (val & 0xFF);
    }

    @Override
    public boolean isCompatibleRaster(Raster raster) {
        return raster instanceof ImageABWRaster;
    }

    @Override
    public boolean isCompatibleSampleModel(SampleModel sm) {
        return sm instanceof BiggerABWSampleModel;
    }

    @Override
    public Object getDataElements(int argb, Object pixel) {
        int[] s;
        if (pixel instanceof int[]) {
            s = (int[]) pixel;
        } else {
            s = new int[1];
        }
        s[0] = ((argb >>> 16) | ((argb >>> 8) & 0xFF) | (argb & 0xFF));
        return s;
    }

    @Override
    public int getNumComponents() {
        return 2;
    }

    @Override
    public int getNumColorComponents() {
        return 1;
    }
}
