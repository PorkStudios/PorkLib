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

import lombok.NonNull;
import net.daporkchop.lib.minecraft.util.BlockAccess;

import java.io.IOException;

/**
 * An arbitrarily sized snapshot of block data from a given space in a world.
 *
 * @author DaPorkchop_
 */
public interface BlockArea extends BlockAccess, AutoCloseable {
    int LAYER_BLOCK       = 1 << 0;
    int LAYER_META        = 1 << 1;
    int LAYER_BLOCK_LIGHT = 1 << 2;
    int LAYER_SKY_LIGHT   = 1 << 3;

    int ALL_LAYERS = LAYER_BLOCK | LAYER_META | LAYER_BLOCK_LIGHT | LAYER_SKY_LIGHT;

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

    default void load(@NonNull BlockAccess src, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.load(src, minX, minY, minZ, maxX, maxY, maxZ, ALL_LAYERS);
    }

    default void load(@NonNull BlockAccess src, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int layers) {
        if (minX < maxX) throw new IllegalArgumentException("minX < maxX!");
        if (minY < maxY) throw new IllegalArgumentException("minY < maxY!");
        if (minZ < maxZ) throw new IllegalArgumentException("minZ < maxZ!");

        this.resize(maxX - minX, maxY - minY, maxZ - minZ, layers);

        if ((layers & LAYER_BLOCK) != 0)    {
            for (int x = minX; x < maxX; x++)   {
                for (int y = minY; y < maxY; y++)   {
                    for (int z = minZ; z < maxZ; z++)   {
                        this.setBlockId(x, y, z, src.getBlockId(x, y, z));
                    }
                }
            }
        }
        if ((layers & LAYER_META) != 0) {
            for (int x = minX; x < maxX; x++)   {
                for (int y = minY; y < maxY; y++)   {
                    for (int z = minZ; z < maxZ; z++)   {
                        this.setBlockMeta(x, y, z, src.getBlockMeta(x, y, z));
                    }
                }
            }
        }
        if ((layers & LAYER_BLOCK_LIGHT) != 0)  {
            for (int x = minX; x < maxX; x++)   {
                for (int y = minY; y < maxY; y++)   {
                    for (int z = minZ; z < maxZ; z++)   {
                        this.setBlockLight(x, y, z, src.getBlockLight(x, y, z));
                    }
                }
            }
        }
        if ((layers & LAYER_SKY_LIGHT) != 0)    {
            for (int x = minX; x < maxX; x++)   {
                for (int y = minY; y < maxY; y++)   {
                    for (int z = minZ; z < maxZ; z++)   {
                        this.setSkyLight(x, y, z, src.getSkyLight(x, y, z));
                    }
                }
            }
        }
    }

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
