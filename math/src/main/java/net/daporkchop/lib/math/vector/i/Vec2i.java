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

package net.daporkchop.lib.math.vector.i;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A 2-dimensional vector
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public final class Vec2i implements IntVector2 {
    protected final int x;
    protected final int y;

    public Vec2i(long encoded) {
        this.x = (int) (encoded >> 32);
        this.y = (int) encoded;
    }

    @Override
    public IntVector2 add(int x, int y) {
        return new Vec2i(this.x + x, this.y + y);
    }

    @Override
    public IntVector2 subtract(int x, int y) {
        return new Vec2i(this.x - x, this.y - y);
    }

    @Override
    public IntVector2 multiply(int x, int y) {
        return new Vec2i(this.x * x, this.y * y);
    }

    @Override
    public IntVector2 divide(int x, int y) {
        return new Vec2i(this.x / x, this.y / y);
    }

    public long encodeLong() {
        return (((long) this.x) << 32) | (this.y & 0xffffffffL);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IntVector2)) {
            return false;
        }

        IntVector2 vec = (IntVector2) obj;
        return this.x == vec.getX() && this.y == vec.getY();
    }

    @Override
    public int hashCode() {
        return this.x * 1799125309 + this.y;
    }

    @Override
    public String toString() {
        return String.format("Vec2i(%d,%d)", this.x, this.y);
    }
}
