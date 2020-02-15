/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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
