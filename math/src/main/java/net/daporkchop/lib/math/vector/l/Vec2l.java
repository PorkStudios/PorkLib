/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.math.vector.l;

/**
 * A 2-dimensional vector
 *
 * @author DaPorkchop_
 */
public class Vec2l implements LongVector2 {
    private final long x;
    private final long y;

    public Vec2l(long x, long y) {
        this.x = x;
        this.y = y;
    }

    public long getX() {
        return this.x;
    }

    public long getY() {
        return this.y;
    }

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
        if (!(obj instanceof Vec2l)) {
            return false;
        }

        Vec2l vec = (Vec2l) obj;
        return this.x == vec.x && this.y == vec.y;
    }

    @Override
    public int hashCode() {
        return 31 * (int) (this.x ^ (this.x >>> 32)) + (int) (this.y ^ (this.y >>> 32));
    }

    @Override
    public String toString() {
        return "Vec2l(x=" + this.x + ", y=" + this.y + ')';
    }
}
