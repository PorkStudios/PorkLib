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
 * A 2-dimensional vector with {@code float} components.
 * <p>
 * Implementations are expected to redirect {@link Object#equals(Object)}, {@link Object#hashCode()} and {@link Object#toString()} to
 * {@link #equals(Vec2f, Object)}, {@link #hashCode(Vec2f)} and {@link #toString(Vec2f)}, respectively.
 *
 * @author DaPorkchop_
 */
public interface Vec2f {
    /**
     * Gets a {@link Vec2f} with the given coordinates.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @return a {@link Vec2f}
     */
    static Vec2f of(float x, float y) {
        return new Vec2fImpl(x, y);
    }

    static boolean equals(Vec2f _this, Object obj) {
        if (!(obj instanceof Vec2f)) {
            return false;
        }

        Vec2f vec = (Vec2f) obj;
        return _this.x() == vec.x() && _this.y() == vec.y();
    }

    static int hashCode(Vec2f _this) {
        return Float.floatToRawIntBits(_this.x()) * HASH0 + Float.floatToRawIntBits(_this.y()) * HASH1;
    }

    static String toString(Vec2f _this) {
        return "Vec2f(" + _this.x() + ',' + _this.y() + ')';
    }

    /**
     * @return the vector's X component
     */
    float x();

    /**
     * @return the vector's Y component
     */
    float y();

    default Vec2i toInt() {
        return Vec2i.of((int) this.x(), (int) this.y());
    }

    default Vec2d toDouble() {
        return Vec2d.of((double) this.x(), (double) this.y());
    }

    default Vec2f neg() {
        return of(-this.x(), -this.y());
    }

    default Vec2f add(float x, float y) {
        return of(this.x() + x, this.y() + y);
    }

    default Vec2f add(Vec2f vec) {
        return of(this.x() + vec.x(), this.y() + vec.y());
    }

    default Vec2f sub(float x, float y) {
        return of(this.x() - x, this.y() - y);
    }

    default Vec2f sub(Vec2f vec) {
        return of(this.x() - vec.x(), this.y() - vec.y());
    }

    default Vec2f mul(float x, float y) {
        return of(this.x() * x, this.y() * y);
    }

    default Vec2f mul(Vec2f vec) {
        return of(this.x() * vec.x(), this.y() * vec.y());
    }

    default Vec2f div(float x, float y) {
        return of(this.x() / x, this.y() / y);
    }

    default Vec2f div(Vec2f vec) {
        return of(this.x() / vec.x(), this.y() / vec.y());
    }
}
