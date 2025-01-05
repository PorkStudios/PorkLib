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
 * A 3-dimensional vector with {@code int} components.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode
@Accessors(fluent = true)
@ValueBased
public final class Vec3i {
    /**
     * Gets a {@link Vec3i} with the given coordinates.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Y coordinate
     * @return a {@link Vec3i}
     */
    public static Vec3i of(int x, int y, int z) {
        return new Vec3i(x, y, z);
    }

    /**
     * The vector's X component.
     */
    private final int x;

    /**
     * The vector's Y component.
     */
    private final int y;

    /**
     * The vector's Z component.
     */
    private final int z;

    public Vec3f toFloat() {
        return Vec3f.of((float) this.x, (float) this.y, (float) this.z);
    }

    public Vec3d toDouble() {
        return Vec3d.of((double) this.x, (double) this.y, (double) this.z);
    }

    public Vec3i neg() {
        return of(-this.x, -this.y, -this.z);
    }

    public Vec3i abs() {
        return of(Math.abs(this.x), Math.abs(this.y), Math.abs(this.z));
    }

    public Vec3i add(int v) {
        return of(this.x + v, this.y + v, this.z + v);
    }

    public Vec3i add(int x, int y, int z) {
        return of(this.x + x, this.y + y, this.z + z);
    }

    public Vec3i add(Vec3i vec) {
        return of(this.x + vec.x, this.y + vec.y, this.z + vec.z);
    }

    public Vec3i sub(int v) {
        return of(this.x - v, this.y - v, this.z - v);
    }

    public Vec3i sub(int x, int y, int z) {
        return of(this.x - x, this.y - y, this.z - z);
    }

    public Vec3i sub(Vec3i vec) {
        return of(this.x - vec.x, this.y - vec.y, this.z - vec.z);
    }

    public Vec3i mul(int v) {
        return of(this.x * v, this.y * v, this.z * v);
    }

    public Vec3i mul(int x, int y, int z) {
        return of(this.x * x, this.y * y, this.z * z);
    }

    public Vec3i mul(Vec3i vec) {
        return of(this.x * vec.x, this.y * vec.y, this.z * vec.z);
    }

    public Vec3i div(int v) {
        return of(this.x / v, this.y / v, this.z / v);
    }

    public Vec3i div(int x, int y, int z) {
        return of(this.x / x, this.y / y, this.z / z);
    }

    public Vec3i div(Vec3i vec) {
        return of(this.x / vec.x, this.y / vec.y, this.z / vec.z);
    }
}
