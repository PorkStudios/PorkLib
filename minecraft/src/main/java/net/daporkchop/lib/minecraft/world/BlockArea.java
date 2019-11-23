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

package net.daporkchop.lib.minecraft.world;

import net.daporkchop.lib.minecraft.util.BlockDataAccess;

import java.io.IOException;

/**
 * An arbitrarily sized snapshot of block data from a given space in a world.
 *
 * @author DaPorkchop_
 */
public interface BlockArea extends BlockDataAccess, AutoCloseable {
    int LAYER_BLOCK       = 1 << 0;
    int LAYER_META        = 1 << 1;
    int LAYER_SKY_LIGHT   = 1 << 2;
    int LAYER_BLOCK_LIGHT = 1 << 3;

    /**
     * Resizes the contents of this {@link BlockArea}.
     * <p>
     * This method will prepare this {@link BlockArea} instance to be able to store an area up to the given dimensions.
     * <p>
     * What exactly happens when calling this method is implementation-defined.
     *
     * @param sizeX  the new maximum size of this {@link BlockArea} along the X axis
     * @param sizeY  the new maximum size of this {@link BlockArea} along the X axis
     * @param sizeZ  the new maximum size of this {@link BlockArea} along the X axis
     * @param layers all the layers that should be enabled
     */
    void resize(int sizeX, int sizeY, int sizeZ, int layers);

    /**
     * Gets all the currently enabled layers.
     * <p>
     * Layers that are not enabled will simply return 0 for all accesses.
     *
     * @return all the currently enabled layers
     */
    int layers();

    /**
     * Clears the contents of this {@link BlockArea}.
     * <p>
     * This means that all values of all enabled layers will be reset to {@code 0}.
     */
    void clear();

    /**
     * Releases the contents of this {@link BlockArea}.
     * <p>
     * After this method has been called, calling any other method on this instance will result in undefined behavior.
     *
     * @throws IOException if an IO exception occurs while closing this instance
     */
    @Override
    void close() throws IOException;
}
