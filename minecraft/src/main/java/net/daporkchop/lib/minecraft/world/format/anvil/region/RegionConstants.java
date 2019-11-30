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

package net.daporkchop.lib.minecraft.world.format.anvil.region;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.encoding.compression.Compression;
import net.daporkchop.lib.encoding.compression.CompressionHelper;
import net.daporkchop.lib.primitive.map.ByteObjMap;
import net.daporkchop.lib.primitive.map.ObjByteMap;
import net.daporkchop.lib.primitive.map.hash.open.ByteObjOpenHashMap;
import net.daporkchop.lib.primitive.map.hash.open.ObjByteOpenHashMap;

/**
 * Constants used when interacting with region files.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class RegionConstants {
    public static final int     SECTOR_BYTES     = 4096;
    public static final int     SECTOR_INTS      = SECTOR_BYTES >>> 2;
    public static final byte[]  EMPTY_SECTOR     = new byte[SECTOR_BYTES];

    public static final int HEADER_SECTORS = 2;
    public static final int HEADER_BYTES = SECTOR_BYTES * HEADER_SECTORS;
    public static final byte[] EMPTY_HEADERS = new byte[HEADER_BYTES];

    public static final int LENGTH_HEADER_SIZE  = 4;
    public static final int VERSION_HEADER_SIZE = 1;
    public static final int CHUNK_HEADER_SIZE   = LENGTH_HEADER_SIZE + VERSION_HEADER_SIZE;

    /**
     * A bitmask to identify non-official compression versions.
     * <p>
     * Any version containing this mask is unofficial, added by me.
     */
    public static final byte PORKIAN_ID_MASK = (byte) 0x80;

    public static final byte ID_GZIP  = 1; //official, no longer used by vanilla
    public static final byte ID_ZLIB  = 2; //official
    public static final byte ID_NONE  = PORKIAN_ID_MASK | 0;
    public static final byte ID_BZIP2 = PORKIAN_ID_MASK | 1;
    public static final byte ID_LZ4   = PORKIAN_ID_MASK | 2;
    public static final byte ID_LZMA  = PORKIAN_ID_MASK | 3;
    public static final byte ID_XZ    = PORKIAN_ID_MASK | 4;

    public static final ByteObjMap<CompressionHelper> COMPRESSION_IDS         = new ByteObjOpenHashMap<>();
    public static final ObjByteMap<CompressionHelper> REVERSE_COMPRESSION_IDS = new ObjByteOpenHashMap<>();

    public static final boolean DEBUG_SECTORS = false;

    static {
        //id => compression algo
        COMPRESSION_IDS.put(ID_NONE, Compression.NONE);
        COMPRESSION_IDS.put(ID_GZIP, Compression.GZIP_NORMAL);
        COMPRESSION_IDS.put(ID_ZLIB, Compression.DEFLATE_NORMAL);
        COMPRESSION_IDS.put(ID_BZIP2, Compression.BZIP2_NORMAL);
        COMPRESSION_IDS.put(ID_LZ4, Compression.LZ4_BLOCK);
        COMPRESSION_IDS.put(ID_LZMA, Compression.LZMA_NORMAL);
        COMPRESSION_IDS.put(ID_XZ, Compression.XZ_NORMAL);

        //compression algo => id
        REVERSE_COMPRESSION_IDS.put(Compression.NONE, ID_NONE);
        REVERSE_COMPRESSION_IDS.put(Compression.GZIP_LOW, ID_GZIP);
        REVERSE_COMPRESSION_IDS.put(Compression.GZIP_NORMAL, ID_GZIP);
        REVERSE_COMPRESSION_IDS.put(Compression.GZIP_HIGH, ID_GZIP);
        REVERSE_COMPRESSION_IDS.put(Compression.DEFLATE_LOW, ID_ZLIB);
        REVERSE_COMPRESSION_IDS.put(Compression.DEFLATE_NORMAL, ID_ZLIB);
        REVERSE_COMPRESSION_IDS.put(Compression.DEFLATE_HIGH, ID_ZLIB);
        REVERSE_COMPRESSION_IDS.put(Compression.BZIP2_LOW, ID_BZIP2);
        REVERSE_COMPRESSION_IDS.put(Compression.BZIP2_NORMAL, ID_BZIP2);
        REVERSE_COMPRESSION_IDS.put(Compression.BZIP2_HIGH, ID_BZIP2);
        REVERSE_COMPRESSION_IDS.put(Compression.LZ4_BLOCK, ID_LZ4);
        REVERSE_COMPRESSION_IDS.put(Compression.LZ4_FRAMED_64KB, ID_LZ4);
        REVERSE_COMPRESSION_IDS.put(Compression.LZ4_FRAMED_256KB, ID_LZ4);
        REVERSE_COMPRESSION_IDS.put(Compression.LZ4_FRAMED_1MB, ID_LZ4);
        REVERSE_COMPRESSION_IDS.put(Compression.LZ4_FRAMED_4MB, ID_LZ4);
        REVERSE_COMPRESSION_IDS.put(Compression.LZMA_LOW, ID_LZMA);
        REVERSE_COMPRESSION_IDS.put(Compression.LZMA_NORMAL, ID_LZMA);
        REVERSE_COMPRESSION_IDS.put(Compression.LZMA_HIGH, ID_LZMA);
        REVERSE_COMPRESSION_IDS.put(Compression.XZ_LOW, ID_XZ);
        REVERSE_COMPRESSION_IDS.put(Compression.XZ_NORMAL, ID_XZ);
        REVERSE_COMPRESSION_IDS.put(Compression.XZ_HIGH, ID_XZ);
    }

    public static void assertInBounds(int x, int z) {
        if (x < 0 || x >= 32 || z < 0 || z >= 32) {
            throw new IllegalArgumentException(String.format("Coordinates out of bounds: (%d,%d)", x, z));
        }
    }

    public static int getOffsetIndex(int x, int z)  {
        assertInBounds(x, z);
        return (x << 2) | (z << 7);
    }

    public static int getTimestampIndex(int x, int z)  {
        assertInBounds(x, z);
        return ((x << 2) | (z << 7)) + SECTOR_BYTES;
    }
}
