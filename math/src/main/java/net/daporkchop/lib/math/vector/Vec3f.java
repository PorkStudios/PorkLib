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
 * A 3-dimensional vector with {@code float} components.
 * <p>
 * Implementations are expected to redirect {@link Object#equals(Object)}, {@link Object#hashCode()} and {@link Object#toString()} to
 * {@link #equals(Vec3f, Object)}, {@link #hashCode(Vec3f)} and {@link #toString(Vec3f)}, respectively.
 *
 * @author DaPorkchop_
 */
public interface Vec3f {
    /**
     * Gets a {@link Vec3f} with the given coordinates.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Y coordinate
     * @return a {@link Vec3f}
     */
    static Vec3f of(float x, float y, float z) {
        return new Vec3fImpl(x, y, z);
    }

    static boolean equals(Vec3f _this, Object obj) {
        if (!(obj instanceof Vec3f)) {
            return false;
        }

        Vec3f vec = (Vec3f) obj;
        return _this.x() == vec.x() && _this.y() == vec.y() && _this.z() == vec.z();
    }

    static int hashCode(Vec3f _this) {
        return Float.floatToRawIntBits(_this.x()) * HASH0 + Float.floatToRawIntBits(_this.y()) * HASH1 + Float.floatToRawIntBits(_this.z()) * HASH2;
    }

    static String toString(Vec3f _this) {
        return "Vec3f(" + _this.x() + ',' + _this.y() + ',' + _this.z() + ')';
    }

    /**
     * @return the vector's X component
     */
    float x();

    /**
     * @return the vector's Y component
     */
    float y();

    /**
     * @return the vector's Z component
     */
    float z();

    default Vec3i toInt() {
        return Vec3i.of((int) this.x(), (int) this.y(), (int) this.z());
    }

    default Vec3d toDouble() {
        return Vec3d.of((double) this.x(), (double) this.y(), (double) this.z());
    }

    default Vec3f neg() {
        return of(-this.x(), -this.y(), -this.z());
    }

    default Vec3f add(float x, float y, float z) {
        return of(this.x() + x, this.y() + y, this.z() + z);
    }

    default Vec3f add(Vec3f vec) {
        return of(this.x() + vec.x(), this.y() + vec.y(), this.z() + vec.z());
    }

    default Vec3f sub(float x, float y, float z) {
        return of(this.x() - x, this.y() - y, this.z() - z);
    }

    default Vec3f sub(Vec3f vec) {
        return of(this.x() - vec.x(), this.y() - vec.y(), this.z() - vec.z());
    }

    default Vec3f mul(float x, float y, float z) {
        return of(this.x() * x, this.y() * y, this.z() * z);
    }

    default Vec3f mul(Vec3f vec) {
        return of(this.x() * vec.x(), this.y() * vec.y(), this.z() * vec.z());
    }

    default Vec3f div(float x, float y, float z) {
        return of(this.x() / x, this.y() / y, this.z() / z);
    }

    default Vec3f div(Vec3f vec) {
        return of(this.x() / vec.x(), this.y() / vec.y(), this.z() / vec.z());
    }
}
