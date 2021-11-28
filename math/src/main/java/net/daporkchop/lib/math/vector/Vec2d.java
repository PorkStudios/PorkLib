/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2021 DaPorkchop_
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

package net.daporkchop.lib.math.vector;

import static net.daporkchop.lib.math.vector.Vectors.*;

/**
 * A 2-dimensional vector with {@code double} components.
 * <p>
 * Implementations are expected to redirect {@link Object#equals(Object)}, {@link Object#hashCode()} and {@link Object#toString()} to
 * {@link #equals(Vec2d, Object)}, {@link #hashCode(Vec2d)} and {@link #toString(Vec2d)}, respectively.
 *
 * @author DaPorkchop_
 */
public interface Vec2d {
    /**
     * Gets a {@link Vec2d} with the given coordinates.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @return a {@link Vec2d}
     */
    static Vec2d of(double x, double y) {
        return new Vec2dImpl(x, y);
    }

    static boolean equals(Vec2d _this, Object obj) {
        if (!(obj instanceof Vec2d)) {
            return false;
        }

        Vec2d vec = (Vec2d) obj;
        return _this.x() == vec.x() && _this.y() == vec.y();
    }

    static int hashCode(Vec2d _this) {
        return (int) (Double.doubleToRawLongBits(_this.x()) * HASH0 + Double.doubleToRawLongBits(_this.y()) * HASH1);
    }

    static String toString(Vec2d _this) {
        return "Vec2d(" + _this.x() + ',' + _this.y() + ')';
    }

    /**
     * @return the vector's X component
     */
    double x();

    /**
     * @return the vector's Y component
     */
    double y();

    default Vec2i toInt() {
        return Vec2i.of((int) this.x(), (int) this.y());
    }

    default Vec2f toFloat() {
        return Vec2f.of((float) this.x(), (float) this.y());
    }

    default Vec2d neg() {
        return of(-this.x(), -this.y());
    }

    default Vec2d add(double x, double y) {
        return of(this.x() + x, this.y() + y);
    }

    default Vec2d add(Vec2d vec) {
        return of(this.x() + vec.x(), this.y() + vec.y());
    }

    default Vec2d sub(double x, double y) {
        return of(this.x() - x, this.y() - y);
    }

    default Vec2d sub(Vec2d vec) {
        return of(this.x() - vec.x(), this.y() - vec.y());
    }

    default Vec2d mul(double x, double y) {
        return of(this.x() * x, this.y() * y);
    }

    default Vec2d mul(Vec2d vec) {
        return of(this.x() * vec.x(), this.y() * vec.y());
    }

    default Vec2d div(double x, double y) {
        return of(this.x() / x, this.y() / y);
    }

    default Vec2d div(Vec2d vec) {
        return of(this.x() / vec.x(), this.y() / vec.y());
    }
}
