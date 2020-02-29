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

package net.daporkchop.lib.minecraft.world.format.anvil.region;

import lombok.experimental.UtilityClass;

/**
 * Constants used when interacting with region files.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class RegionConstants {
    public static final int    SECTOR_BYTES = 4096;
    public static final int    SECTOR_INTS  = SECTOR_BYTES >>> 2;
    public static final byte[] EMPTY_SECTOR = new byte[SECTOR_BYTES];

    public static final int HEADER_SECTORS = 2;
    public static final int HEADER_BYTES   = SECTOR_BYTES * HEADER_SECTORS;

    public static final int LENGTH_HEADER_SIZE  = 4;
    public static final int VERSION_HEADER_SIZE = 1;
    public static final int CHUNK_HEADER_SIZE   = LENGTH_HEADER_SIZE + VERSION_HEADER_SIZE;

    public static final byte ID_GZIP  = 1; //official, no longer used by vanilla
    public static final byte ID_ZLIB  = 2; //official

    public static void assertInBounds(int x, int z) {
        if (x < 0 || x >= 32 || z < 0 || z >= 32) {
            throw new IllegalArgumentException(String.format("Coordinates out of bounds: (%d,%d)", x, z));
        }
    }

    public static int getOffsetIndex(int x, int z) {
        assertInBounds(x, z);
        return (x << 2) | (z << 7);
    }

    public static int getTimestampIndex(int x, int z) {
        assertInBounds(x, z);
        return ((x << 2) | (z << 7)) + SECTOR_BYTES;
    }
}
