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

package net.daporkchop.lib.math.vector.l;

/**
 * A 3-dimensional vector
 *
 * @author DaPorkchop_
 */
public class Vec3l implements LongVector3 {
    private final long x;
    private final long y;
    private final long z;

    public Vec3l(long x, long y, long z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public long getX() {
        return this.x;
    }

    public long getY() {
        return this.y;
    }

    public long getZ() {
        return this.z;
    }

    @Override
    public LongVector3 add(long x, long y, long z) {
        return new Vec3l(this.x + x, this.y + y, this.z + z);
    }

    @Override
    public LongVector3 subtract(long x, long y, long z) {
        return new Vec3l(this.x - x, this.y - y, this.z - z);
    }

    @Override
    public LongVector3 multiply(long x, long y, long z) {
        return new Vec3l(this.x * x, this.y * y, this.z * z);
    }

    @Override
    public LongVector3 divide(long x, long y, long z) {
        return new Vec3l(this.x / x, this.y / y, this.z / z);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vec3l)) {
            return false;
        }

        Vec3l vec = (Vec3l) obj;
        return this.x == vec.x && this.y == vec.y && this.z == vec.z;
    }

    @Override
    public int hashCode() {
        return 31 * 31 * (int) (this.x ^ (this.x >>> 32)) + 31 * (int) (this.y ^ (this.y >>> 32)) + (int) (this.z ^ (this.z >>> 32));
    }

    @Override
    public String toString() {
        return "Vec3i(x=" + this.x + ", y=" + this.y + ", z=" + this.z + ')';
    }
}
