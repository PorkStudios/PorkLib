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

package net.daporkchop.lib.math.vector.d;

/**
 * A 2-dimensional vector
 *
 * @author DaPorkchop_
 */
public class Vec2d implements DoubleVector2 {
    private final double x;
    private final double y;

    public Vec2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    @Override
    public DoubleVector2 add(double x, double y) {
        return new Vec2d(this.x + x, this.y + y);
    }

    @Override
    public DoubleVector2 subtract(double x, double y) {
        return new Vec2d(this.x - x, this.y - y);
    }

    @Override
    public DoubleVector2 multiply(double x, double y) {
        return new Vec2d(this.x * x, this.y * y);
    }

    @Override
    public DoubleVector2 divide(double x, double y) {
        return new Vec2d(this.x / x, this.y / y);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vec2d)) {
            return false;
        }

        Vec2d vec = (Vec2d) obj;
        return this.x == vec.x && this.y == vec.y;
    }

    @Override
    public int hashCode() {
        long xBits = Double.doubleToLongBits(this.x);
        long yBits = Double.doubleToLongBits(this.y);
        return 31 * (int) (xBits ^ (xBits >>> 32)) + (int) (yBits ^ (yBits >>> 32));
    }

    @Override
    public String toString() {
        return "Vec2d(x=" + this.x + ", y=" + this.y + ')';
    }
}
