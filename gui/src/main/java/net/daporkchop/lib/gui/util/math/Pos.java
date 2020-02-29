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

/**
 * A point in 2D space
 *
 * @author DaPorkchop_
 */
public interface Pos<Impl extends Pos> extends Constraint {
    static Pos at(int x, int y) {
        return new Default(x, y);
    }

    int getX();

    int getY();

    Impl addXY(int x, int y);

    default Impl addXY(int i) {
        return this.addXY(i, i);
    }

    default Impl subtractXY(int x, int y) {
        return this.addXY(-x, -y);
    }

    default Impl subtractXY(int i) {
        return this.addXY(-i, -i);
    }

    Impl multiplyXY(int x, int y);

    default Impl multiplyXY(int i) {
        return this.multiplyXY(i, i);
    }

    Impl divideXY(int x, int y);

    default Impl divideXY(int i) {
        return this.divideXY(i, i);
    }

    @Override
    default boolean hasXY() {
        return true;
    }

    @Override
    default boolean hasWH() {
        return false;
    }

    @Data
    class Default implements Pos<Default> {
        protected final int x;
        protected final int y;

        @Override
        public Default addXY(int x, int y) {
            return new Default(this.x + x, this.y + y);
        }

        @Override
        public Default multiplyXY(int x, int y) {
            return new Default(this.x * x, this.y * y);
        }

        @Override
        public Default divideXY(int x, int y) {
            return new Default(this.x / x, this.y / y);
        }
    }
}
