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
 * A 3-dimensional vector
 *
 * @author DaPorkchop_
 */
public class Vec3f implements FloatVector3 {
    private final float x;
    private final float y;
    private final float z;

    public Vec3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }

    @Override
    public FloatVector3 add(float x, float y, float z) {
        return new Vec3f(this.x + x, this.y + y, this.z + z);
    }

    @Override
    public FloatVector3 subtract(float x, float y, float z) {
        return new Vec3f(this.x - x, this.y - y, this.z - z);
    }

    @Override
    public FloatVector3 multiply(float x, float y, float z) {
        return new Vec3f(this.x * x, this.y * y, this.z * z);
    }

    @Override
    public FloatVector3 divide(float x, float y, float z) {
        return new Vec3f(this.x / x, this.y / y, this.z / z);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vec3f)) {
            return false;
        }

        Vec3f vec = (Vec3f) obj;
        return this.x == vec.x && this.y == vec.y && this.z == vec.z;
    }

    @Override
    public int hashCode() {
        int xBits = Float.floatToIntBits(this.x);
        int yBits = Float.floatToIntBits(this.y);
        int zBits = Float.floatToIntBits(this.z);
        return xBits * 31 * 31 + yBits * 31 + zBits;
    }

    @Override
    public String toString() {
        return "Vec3f(x=" + this.x + ", y=" + this.y + ", z=" + this.z + ')';
    }
}
