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

package net.daporkchop.lib.math.vector.f;

/**
 * A 2-dimensional vector
 *
 * @author DaPorkchop_
 */
public class Vec2f implements FloatVector2 {
    private final float x;
    private final float y;

    public Vec2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    @Override
    public FloatVector2 add(float x, float y) {
        return new Vec2f(this.x + x, this.y + y);
    }

    @Override
    public FloatVector2 subtract(float x, float y) {
        return new Vec2f(this.x - x, this.y - y);
    }

    @Override
    public FloatVector2 multiply(float x, float y) {
        return new Vec2f(this.x * x, this.y * y);
    }

    @Override
    public FloatVector2 divide(float x, float y) {
        return new Vec2f(this.x / x, this.y / y);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vec2f)) {
            return false;
        }

        Vec2f vec = (Vec2f) obj;
        return this.x == vec.x && this.y == vec.y;
    }

    @Override
    public int hashCode() {
        int xBits = Float.floatToIntBits(this.x);
        int yBits = Float.floatToIntBits(this.y);
        return xBits ^ ((yBits >>> 16) | (yBits << 16));
    }

    @Override
    public String toString() {
        return "Vec2f(x=" + this.x + ", y=" + this.y + ')';
    }
}
