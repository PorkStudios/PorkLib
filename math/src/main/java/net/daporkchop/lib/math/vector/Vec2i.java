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
 * A 2-dimensional vector with {@code int} components.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode
@Accessors(fluent = true)
@ValueBased
public final class Vec2i {
    /**
     * Gets a {@link Vec2i} with the given coordinates.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @return a {@link Vec2i}
     */
    public static Vec2i of(int x, int y) {
        return new Vec2i(x, y);
    }

    /**
     * The vector's X component.
     */
    private final int x;

    /**
     * The vector's Y component.
     */
    private final int y;

    public Vec2f toFloat() {
        return Vec2f.of((float) this.x, (float) this.y);
    }

    public Vec2d toDouble() {
        return Vec2d.of((double) this.x, (double) this.y);
    }

    public Vec2i neg() {
        return of(-this.x, -this.y);
    }

    public Vec2i abs() {
        return of(Math.abs(this.x), Math.abs(this.y));
    }

    public Vec2i add(int v) {
        return of(this.x + v, this.y + v);
    }

    public Vec2i add(int x, int y) {
        return of(this.x + x, this.y + y);
    }

    public Vec2i add(Vec2i vec) {
        return of(this.x + vec.x, this.y + vec.y);
    }

    public Vec2i sub(int v) {
        return of(this.x - v, this.y - v);
    }

    public Vec2i sub(int x, int y) {
        return of(this.x - x, this.y - y);
    }

    public Vec2i sub(Vec2i vec) {
        return of(this.x - vec.x, this.y - vec.y);
    }

    public Vec2i mul(int v) {
        return of(this.x * v, this.y * v);
    }

    public Vec2i mul(int x, int y) {
        return of(this.x * x, this.y * y);
    }

    public Vec2i mul(Vec2i vec) {
        return of(this.x * vec.x, this.y * vec.y);
    }

    public Vec2i div(int v) {
        return of(this.x / v, this.y / v);
    }

    public Vec2i div(int x, int y) {
        return of(this.x / x, this.y / y);
    }

    public Vec2i div(Vec2i vec) {
        return of(this.x / vec.x, this.y / vec.y);
    }
}
