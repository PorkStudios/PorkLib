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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A 3-dimensional vector.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
@Getter
public final class Vec3d implements DoubleVector3 {
    protected final double x;
    protected final double y;
    protected final double z;

    @Override
    public DoubleVector3 add(double x, double y, double z) {
        return new Vec3d(this.x + x, this.y + y, this.z + z);
    }

    @Override
    public DoubleVector3 subtract(double x, double y, double z) {
        return new Vec3d(this.x - x, this.y - y, this.z - z);
    }

    @Override
    public DoubleVector3 multiply(double x, double y, double z) {
        return new Vec3d(this.x * x, this.y * y, this.z * z);
    }

    @Override
    public DoubleVector3 divide(double x, double y, double z) {
        return new Vec3d(this.x / x, this.y / y, this.z / z);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DoubleVector3)) {
            return false;
        }

        DoubleVector3 vec = (DoubleVector3) obj;
        return this.x == vec.getX() && this.y == vec.getY() && this.z == vec.getZ();
    }

    @Override
    public int hashCode() {
        long l = (Double.doubleToLongBits(this.x) * 1260314835565697L + Double.doubleToLongBits(this.y)) * 617654310259680119L + Double.doubleToLongBits(this.z);
        return (int) (l ^ (l >>> 32L));
    }

    @Override
    public String toString() {
        return String.format("Vec3d(%f,%f,%f)", this.x, this.y, this.z);
    }
}
