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
 * A 2-dimensional vector with {@code int} components.
 * <p>
 * Implementations are expected to redirect {@link Object#equals(Object)}, {@link Object#hashCode()} and {@link Object#toString()} to
 * {@link #equals(Vec2i, Object)}, {@link #hashCode(Vec2i)} and {@link #toString(Vec2i)}, respectively.
 *
 * @author DaPorkchop_
 */
public interface Vec2i {
    /**
     * Gets a {@link Vec2i} with the given coordinates.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @return a {@link Vec2i}
     */
    static Vec2i of(int x, int y) {
        return new Vectors.Vec2iImpl(x, y);
    }

    static boolean equals(Vec2i _this, Object obj) {
        if (!(obj instanceof Vec2i)) {
            return false;
        }

        Vec2i vec = (Vec2i) obj;
        return _this.x() == vec.x() && _this.y() == vec.y();
    }

    static int hashCode(Vec2i _this) {
        return _this.x() * HASH0 + _this.y() * HASH1;
    }

    static String toString(Vec2i _this) {
        return "Vec2i(" + _this.x() + ',' + _this.y() + ')';
    }

    /**
     * @return the vector's X component
     */
    int x();

    /**
     * @return the vector's Y component
     */
    int y();

    default Vec2f toFloat() {
        return Vec2f.of((float) this.x(), (float) this.y());
    }

    default Vec2d toDouble() {
        return Vec2d.of((double) this.x(), (double) this.y());
    }

    default Vec2i neg() {
        return of(-this.x(), -this.y());
    }

    default Vec2i add(int x, int y) {
        return of(this.x() + x, this.y() + y);
    }

    default Vec2i add(Vec2i vec) {
        return of(this.x() + vec.x(), this.y() + vec.y());
    }

    default Vec2i sub(int x, int y) {
        return of(this.x() - x, this.y() - y);
    }

    default Vec2i sub(Vec2i vec) {
        return of(this.x() - vec.x(), this.y() - vec.y());
    }

    default Vec2i mul(int x, int y) {
        return of(this.x() * x, this.y() * y);
    }

    default Vec2i mul(Vec2i vec) {
        return of(this.x() * vec.x(), this.y() * vec.y());
    }

    default Vec2i div(int x, int y) {
        return of(this.x() / x, this.y() / y);
    }

    default Vec2i div(Vec2i vec) {
        return of(this.x() / vec.x(), this.y() / vec.y());
    }
}
