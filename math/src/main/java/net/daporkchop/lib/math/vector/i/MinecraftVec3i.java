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
    private static final int NUM_X_BITS = 1 + Log2.log2(Round2.roundInt(30000000));
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
    private volatile long backing;

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
     * Check if this vector is equal to another one
     *
     * @param vec The vector to compare with
     * @return Whether to not this vector stores the same position as the given one
     */
    public boolean equals(MinecraftVec3i vec) {
        return vec != null && vec.backing == this.backing;
    }

    /**
     * Make a duplicate of this vector
     *
     * @return A new object with the same coordinates as this vector
     */
    public MinecraftVec3i clone() {
        MinecraftVec3i minecraftVec3i = this.clone();
        return new MinecraftVec3i(this.backing);
    }

    static class Log2 {
        private static final int[] DE_BRUIJN = {0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};

        private Log2() {
        }

        private static int calculateDeBruijn(int value) {
            value = IsPow2.checkInt(value) ? value : Round2.roundInt(value);
            return DE_BRUIJN[(int) ((long) value * 125613361L >> 27) & 31];
        }

        public static int log2(int value) {
            return calculateDeBruijn(value) - (IsPow2.checkInt(value) ? 0 : 1);
        }
    }

    static class Round2 {
        private Round2() {
        }

        public static long roundLong(long value) {
            long l = value - 1;
            l = l | l >> 1;
            l = l | l >> 2;
            l = l | l >> 4;
            l = l | l >> 8;
            l = l | l >> 16;
            l = l | l >> 32;
            return l + 1;
        }

        public static int roundInt(int value) {
            int i = value - 1;
            i = i | i >> 1;
            i = i | i >> 2;
            i = i | i >> 4;
            i = i | i >> 8;
            i = i | i >> 16;
            return i + 1;
        }

        public static short roundShort(short value) {
            short s = (short) (value - 1);
            s = (short) (s | s >> 1);
            s = (short) (s | s >> 2);
            s = (short) (s | s >> 4);
            s = (short) (s | s >> 8);
            return (short) (s + 1);
        }
    }

    static class IsPow2 {
        private IsPow2() {
        }

        public static boolean checkLong(long value) {
            return value != 0L && (value & value - 1L) == 0L;
        }

        public static boolean checkInt(int value) {
            return value != 0 && (value & value - 1) == 0;
        }

        public static boolean checkShort(short value) {
            return value != 0 && (value & value - 1) == 0;
        }

        public static boolean checkByte(byte value) {
            return value != 0 && (value & value - 1) == 0;
        }
    }
}
