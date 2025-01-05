/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2025 DaPorkchop_
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
 */

package net.daporkchop.lib.math.vector;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.annotation.ValueBased;

/**
 * A 3-dimensional vector with {@code double} components.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode
@Accessors(fluent = true)
@ValueBased
public final class Vec3d {
    /**
     * Gets a {@link Vec3d} with the given coordinates.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Y coordinate
     * @return a {@link Vec3d}
     */
    public static Vec3d of(double x, double y, double z) {
        return new Vec3d(x, y, z);
    }

    /**
     * The vector's X component.
     */
    private final double x;

    /**
     * The vector's Y component.
     */
    private final double y;

    /**
     * The vector's Z component.
     */
    private final double z;

    public Vec3i toInt() {
        return Vec3i.of((int) this.x, (int) this.y, (int) this.z);
    }

    public Vec3f toFloat() {
        return Vec3f.of((float) this.x, (float) this.y, (float) this.z);
    }

    public Vec3d neg() {
        return of(-this.x, -this.y, -this.z);
    }

    public Vec3d abs() {
        return of(Math.abs(this.x), Math.abs(this.y), Math.abs(this.z));
    }

    public Vec3d add(double v) {
        return of(this.x + v, this.y + v, this.z + v);
    }

    public Vec3d add(double x, double y, double z) {
        return of(this.x + x, this.y + y, this.z + z);
    }

    public Vec3d add(Vec3d vec) {
        return of(this.x + vec.x, this.y + vec.y, this.z + vec.z);
    }

    public Vec3d sub(double v) {
        return of(this.x - v, this.y - v, this.z - v);
    }

    public Vec3d sub(double x, double y, double z) {
        return of(this.x - x, this.y - y, this.z - z);
    }

    public Vec3d sub(Vec3d vec) {
        return of(this.x - vec.x, this.y - vec.y, this.z - vec.z);
    }

    public Vec3d mul(double v) {
        return of(this.x * v, this.y * v, this.z * v);
    }

    public Vec3d mul(double x, double y, double z) {
        return of(this.x * x, this.y * y, this.z * z);
    }

    public Vec3d mul(Vec3d vec) {
        return of(this.x * vec.x, this.y * vec.y, this.z * vec.z);
    }

    public Vec3d div(double v) {
        return of(this.x / v, this.y / v, this.z / v);
    }

    public Vec3d div(double x, double y, double z) {
        return of(this.x / x, this.y / y, this.z / z);
    }

    public Vec3d div(Vec3d vec) {
        return of(this.x / vec.x, this.y / vec.y, this.z / vec.z);
    }
}
