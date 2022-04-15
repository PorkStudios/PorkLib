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

package net.daporkchop.lib.minecraft.util;

/**
 * A type which allows accessing block information at specific coordinates.
 *
 * @author DaPorkchop_
 */
public interface BlockAccess {
    int getBlockId(int x, int y, int z);

    int getBlockMeta(int x, int y, int z);

    int getBlockLight(int x, int y, int z);

    int getSkyLight(int x, int y, int z);

    int getBiomeId(int x, int z);

    void setBlockId(int x, int y, int z, int id);

    void setBlockMeta(int x, int y, int z, int meta);

    void setBlockLight(int x, int y, int z, int level);

    void setSkyLight(int x, int y, int z, int level);

    void setBiomeId(int x, int z, int id);

    void setBiomeArray(byte[] biomes);

    int getHighestBlock(int x, int z);

    /**
     * @return the minimum X coordinate (inclusive)
     */
    int minX();

    /**
     * @return the minimum Y coordinate (inclusive)
     */
    int minY();

    /**
     * @return the minimum Z coordinate (inclusive)
     */
    int minZ();

    /**
     * @return the maximum X coordinate (exclusive)
     */
    int maxX();

    /**
     * @return the maximum Y coordinate (exclusive)
     */
    int maxY();

    /**
     * @return the maximum Z coordinate (exclusive)
     */
    int maxZ();
}
