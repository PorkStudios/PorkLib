/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.minecraft.world;

/**
 * Provides access to light data at given coordinates.
 *
 * @author DaPorkchop_
 */
public interface LightAccess {
    /**
     * Checks whether or not this {@link LightAccess} contains sky light.
     * <p>
     * If {@code false}, {@link #getSkyLight(int, int, int)} will always return {@code 0} and {@link #setSkyLight(int, int, int, int)} will always
     * throw {@link UnsupportedOperationException}.
     *
     * @return whether or not this {@link LightAccess} contains sky light
     */
    boolean hasSkyLight();

    //
    //
    // getters
    //
    //

    /**
     * Gets the block light level at the given coordinates.
     *
     * @param x the X coordinate to get the light level at
     * @param y the Y coordinate to get the light level at
     * @param z the Z coordinate to get the light level at
     * @return the block light level
     */
    int getBlockLight(int x, int y, int z);

    /**
     * Gets the sky light level at the given coordinates.
     *
     * @param x the X coordinate to get the light level at
     * @param y the Y coordinate to get the light level at
     * @param z the Z coordinate to get the light level at
     * @return the sky light level
     */
    int getSkyLight(int x, int y, int z);

    //
    //
    // setters
    //
    //

    /**
     * Sets the block light level at the given coordinates.
     *
     * @param x     the X coordinate to set the light level at
     * @param y     the Y coordinate to set the light level at
     * @param z     the Z coordinate to set the light level at
     * @param level the new block light level
     */
    void setBlockLight(int x, int y, int z, int level);

    /**
     * Sets the sky light level at the given coordinates.
     *
     * @param x     the X coordinate to set the light level at
     * @param y     the Y coordinate to set the light level at
     * @param z     the Z coordinate to set the light level at
     * @param level the new sky light level
     */
    void setSkyLight(int x, int y, int z, int level);
}
