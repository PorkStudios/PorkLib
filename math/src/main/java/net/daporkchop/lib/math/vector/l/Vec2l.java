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

package net.daporkchop.lib.math.vector.l;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A 2-dimensional vector.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
@Getter
public final class Vec2l implements LongVector2 {
    protected final long x;
    protected final long y;

    @Override
    public LongVector2 add(long x, long y) {
        return new Vec2l(this.x + x, this.y + y);
    }

    @Override
    public LongVector2 subtract(long x, long y) {
        return new Vec2l(this.x - x, this.y - y);
    }

    @Override
    public LongVector2 multiply(long x, long y) {
        return new Vec2l(this.x * x, this.y * y);
    }

    @Override
    public LongVector2 divide(long x, long y) {
        return new Vec2l(this.x / x, this.y / y);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LongVector2)) {
            return false;
        }

        LongVector2 vec = (LongVector2) obj;
        return this.x == vec.getX() && this.y == vec.getY();
    }

    @Override
    public int hashCode() {
        long l = this.x * 134743063592451439L + this.y;
        return (int) (l ^ (l >>> 32L));
    }

    @Override
    public String toString() {
        return String.format("Vec2l(%d,%d)", this.x, this.y);
    }
}
