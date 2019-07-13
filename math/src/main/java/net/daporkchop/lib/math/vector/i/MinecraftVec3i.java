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
 * A simple 3D vector, consisting of an X, Y and Z coordinate
 * Because this is minecraft, we can safely assume that:
 * - X and Z will always be less than 30 million
 * - Y will always be in range 0-256
 * <p>
 * With this in mind, we can have this be backed by a single 64-bit integer, rather than 3 32-bit integers
 *
 * @author DaPorkchop_
 */
public class MinecraftVec3i {
    private static final int NUM_X_BITS = 26;
    private static final int NUM_Z_BITS = NUM_X_BITS;
    private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
    private static final int Y_SHIFT = NUM_Z_BITS;
    private static final int X_SHIFT = Y_SHIFT + NUM_Y_BITS;
    private static final long X_MASK = (1L << NUM_X_BITS) - 1L;
    private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
    private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;

    /**
     * The actual data containing the X,Y,Z coordinates
     */
    private long backing;

    /**
     * Create an empty vector at:
     * x=0,
     * y=0,
     * z=0
     */
    public MinecraftVec3i() {
        this.backing = 0L;
    }

    /**
     * Creates a vector using the specified input coordinates
     *
     * @param x The X coordinate to use
     * @param y The Y coordinate to use
     * @param z The Z coordinate to use
     */
    public MinecraftVec3i(int x, int y, int z) {
        this.reset(x, y, z);
    }

    /**
     * Restores a vector from it's encoded state
     *
     * @param encoded The long containing the coordinates
     */
    public MinecraftVec3i(long encoded) {
        this.backing = encoded;
    }

    /**
     * @return This vector's X coordinate
     */
    public int getX() {
        return (int) (this.backing << 64 - X_SHIFT - NUM_X_BITS >> 64 - NUM_X_BITS);
    }

    /**
     * Sets this vector's X coordinate
     *
     * @param x The new X coordinate
     */
    public void setX(int x) {
        this.backing = (this.backing & (Y_MASK << Y_SHIFT | Z_MASK)) | ((long) x & X_MASK) << X_SHIFT;
    }

    /**
     * @return This vector's Y coordinate
     */
    public int getY() {
        return (int) (this.backing << 64 - Y_SHIFT - NUM_Y_BITS >> 64 - NUM_Y_BITS);
    }

    /**
     * Sets this vector's Y coordinate
     *
     * @param y The new Y coordinate
     */
    public void setY(int y) {
        this.backing = (this.backing & (X_MASK << X_SHIFT | Z_MASK)) | ((long) y & Y_MASK) << Y_SHIFT;
    }

    /**
     * @return This vector's Z coordinate
     */
    public int getZ() {
        return (int) (this.backing << 64 - NUM_Z_BITS >> 64 - NUM_Z_BITS);
    }

    /**
     * Sets this vector's Z coordinate
     *
     * @param z The new Z coordinate
     */
    public void setZ(int z) {
        this.backing = (this.backing & (X_MASK << X_SHIFT | Y_MASK << Y_SHIFT)) | ((long) z & Z_MASK);
    }

    /**
     * Adds an amount to the vector
     *
     * @param x How much to add on the X axis
     * @param y How much to add on the Y axis
     * @param z How much to add on the Z axis
     */
    public void add(int x, int y, int z) {
        this.reset(x + this.getX(), y + this.getY(), z + this.getZ());
    }

    /**
     * Subtracts an amount from the vector
     *
     * @param x How much to subtract from the X axis
     * @param y How much to subtract from the Y axis
     * @param z How much to subtract form the Z axis
     */
    public void subtract(int x, int y, int z) {
        this.reset(this.getX() - x, this.getY() - y, this.getZ() - z);
    }

    /**
     * Reset this vector to the given coordinates
     *
     * @param x The new X coordinate
     * @param y The new Y coordinate
     * @param z The new Z coordinate
     */
    public void reset(int x, int y, int z) {
        this.backing = ((long) x & X_MASK) << X_SHIFT | ((long) y & Y_MASK) << Y_SHIFT | ((long) z & Z_MASK);
    }

    /**
     * Reset this vector to the given encoded vector
     *
     * @param encoded The new long-encoded coordinates
     */
    public void reset(long encoded) {
        this.backing = encoded;
    }

    /**
     * Returns the raw integer backing this vector
     *
     * @return A long-encoded integer containing the X,Y,Z coordinates
     */
    public long toLong() {
        return this.backing;
    }

    /**
     * Make a duplicate of this vector
     *
     * @return A new object with the same coordinates as this vector
     */
    @Override
    public MinecraftVec3i clone() {
        return new MinecraftVec3i(this.backing);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MinecraftVec3i)  {
            return this.backing == ((MinecraftVec3i) obj).backing;
        } else if (obj instanceof IntVector3)   {
            IntVector3 vec = (IntVector3) obj;
            return this.getX() == vec.getX() && this.getY() == vec.getY() && this.getZ() == vec.getZ();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
