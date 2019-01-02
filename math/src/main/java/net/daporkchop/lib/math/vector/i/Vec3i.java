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

package net.daporkchop.lib.math.vector.i;

/**
 * A 3-dimensional vector
 *
 * @author DaPorkchop_
 */
public class Vec3i implements IntVector3 {
    private final int x;
    private final int y;
    private final int z;

    public Vec3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    @Override
    public IntVector3 add(int x, int y, int z) {
        return new Vec3i(this.x + x, this.y + y, this.z + z);
    }

    @Override
    public IntVector3 subtract(int x, int y, int z) {
        return new Vec3i(this.x - x, this.y - y, this.z - z);
    }

    @Override
    public IntVector3 multiply(int x, int y, int z) {
        return new Vec3i(this.x * x, this.y * y, this.z * z);
    }

    @Override
    public IntVector3 divide(int x, int y, int z) {
        return new Vec3i(this.x / x, this.y / y, this.z / z);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vec3i)) {
            return false;
        }

        Vec3i vec = (Vec3i) obj;
        return this.x == vec.x && this.y == vec.y && this.z == vec.z;
    }

    @Override
    public int hashCode() {
        return this.x * 31 * 31 + this.y * 31 + this.z;
    }

    @Override
    public String toString() {
        return "Vec3i(x=" + this.x + ", y=" + this.y + ", z=" + this.z + ')';
    }
}
