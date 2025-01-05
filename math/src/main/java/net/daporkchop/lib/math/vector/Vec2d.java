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
 * A 2-dimensional vector with {@code double} components.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode
@Accessors(fluent = true)
@ValueBased
public final class Vec2d {
    /**
     * Gets a {@link Vec2d} with the given coordinates.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @return a {@link Vec2d}
     */
    public static Vec2d of(double x, double y) {
        return new Vec2d(x, y);
    }

    /**
     * The vector's X component.
     */
    private final double x;

    /**
     * The vector's Y component.
     */
    private final double y;

    public Vec2i toInt() {
        return Vec2i.of((int) this.x, (int) this.y);
    }

    public Vec2f toFloat() {
        return Vec2f.of((float) this.x, (float) this.y);
    }

    public Vec2d neg() {
        return of(-this.x, -this.y);
    }

    public Vec2d abs() {
        return of(Math.abs(this.x), Math.abs(this.y));
    }

    public Vec2d add(double v) {
        return of(this.x + v, this.y + v);
    }

    public Vec2d add(double x, double y) {
        return of(this.x + x, this.y + y);
    }

    public Vec2d add(Vec2d vec) {
        return of(this.x + vec.x, this.y + vec.y);
    }

    public Vec2d sub(double v) {
        return of(this.x - v, this.y - v);
    }

    public Vec2d sub(double x, double y) {
        return of(this.x - x, this.y - y);
    }

    public Vec2d sub(Vec2d vec) {
        return of(this.x - vec.x, this.y - vec.y);
    }

    public Vec2d mul(double v) {
        return of(this.x * v, this.y * v);
    }

    public Vec2d mul(double x, double y) {
        return of(this.x * x, this.y * y);
    }

    public Vec2d mul(Vec2d vec) {
        return of(this.x * vec.x, this.y * vec.y);
    }

    public Vec2d div(double v) {
        return of(this.x / v, this.y / v);
    }

    public Vec2d div(double x, double y) {
        return of(this.x / x, this.y / y);
    }

    public Vec2d div(Vec2d vec) {
        return of(this.x / vec.x, this.y / vec.y);
    }
}
