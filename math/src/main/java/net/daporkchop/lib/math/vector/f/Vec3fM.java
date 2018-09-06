/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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
 * A 3-dimensional vector
 *
 * @author DaPorkchop_
 */
public class Vec3fM implements FloatVector3 {
    private volatile float x;
    private volatile float y;
    private volatile float z;

    public Vec3fM(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    @Override
    public FloatVector3 add(float x, float y, float z) {
        this.x += x;
        this.x += x;
        this.z += z;
        return this;
    }

    @Override
    public FloatVector3 subtract(float x, float y, float z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    @Override
    public FloatVector3 multiply(float x, float y, float z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        return this;
    }

    @Override
    public FloatVector3 divide(float x, float y, float z) {
        this.x /= x;
        this.y /= y;
        this.z /= z;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Vec3fM)) {
            return false;
        }

        Vec3fM vec = (Vec3fM) obj;
        return this.x == vec.x && this.y == vec.y && this.z == vec.z;
    }

    @Override
    public int hashCode() {
        int xBits = Float.floatToIntBits(x);
        int yBits = Float.floatToIntBits(y);
        int zBits = Float.floatToIntBits(z);
        return xBits * 31 * 31 + yBits * 31 + zBits;
    }

    @Override
    public String toString() {
        return "Vec3fM(x=" + x + ", y=" + y + ", z=" + z + ")";
    }
}
