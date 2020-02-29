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

package net.daporkchop.lib.gui.util.math;

import lombok.Data;

import java.awt.*;

/**
 * A size in 2D space
 *
 * @author DaPorkchop_
 */
public interface Size<Impl extends Size> extends Constraint {
    static Size of(int width, int height) {
        return new Default(width, height);
    }

    int getWidth();

    int getHeight();

    Impl addWH(int width, int height);

    default Impl addWH(int i) {
        return this.addWH(i, i);
    }

    default Impl subtractWH(int width, int height) {
        return this.addWH(-width, -height);
    }

    default Impl subtractWH(int i) {
        return this.addWH(-i, -i);
    }

    Impl multiplyWH(int width, int height);

    default Impl multiplyWH(int i) {
        return this.multiplyWH(i, i);
    }

    Impl divideWH(int width, int height);

    default Impl divideWH(int i) {
        return this.divideWH(i, i);
    }

    @Override
    default boolean hasXY() {
        return false;
    }

    @Override
    default boolean hasWH() {
        return true;
    }

    default Dimension toAWTDimension()  {
        return new Dimension(this.getWidth(), this.getHeight());
    }

    @Data
    class Default implements Size<Default> {
        protected final int width;
        protected final int height;

        @Override
        public Default addWH(int width, int height) {
            return new Default(this.width + width, this.height + height);
        }

        @Override
        public Default multiplyWH(int width, int height) {
            return new Default(this.width * width, this.height * height);
        }

        @Override
        public Default divideWH(int width, int height) {
            return new Default(this.width / width, this.height / height);
        }
    }
}
