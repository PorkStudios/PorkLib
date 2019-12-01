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

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.binary.netty.NettyUtil;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.encoding.compression.CompressionHelper;
import net.daporkchop.lib.minecraft.world.format.anvil.region.ex.CorruptedRegionException;
import net.daporkchop.lib.minecraft.world.format.anvil.region.ex.ReadOnlyRegionException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static net.daporkchop.lib.minecraft.world.format.anvil.region.RegionConstants.*;

/**
 * A 32² collection of chunks, a key part of the Anvil storage format.
 *
 * @author DaPorkchop_
 */
public interface RegionFile extends AutoCloseable {
    /**
     * Opens the given region using the given options.
     *
     * @param file    the region file to open
     * @param options the {@link RegionOpenOptions} to be used when opening the region
     * @return the opened region
     * @throws CorruptedRegionException if the contents of the region file are corrupt
     * @throws IOException              if an IO exception occurs while opening the region
     */
    static RegionFile open(@NonNull File file, @NonNull RegionOpenOptions options) throws CorruptedRegionException, IOException {
        //TODO
        switch (options.access) {
            case WRITE_REQUIRED:
                return new OverclockedRegionFile(file, false, true);
            case WRITE_OPTIONAL:
                return new OverclockedRegionFile(file, false, false);
            case READ_ONLY:
                return new OverclockedRegionFile(file, true, false);
        }
        throw new IllegalArgumentException(options.toString());
    }

    /**
     * Gets an {@link InputStream} that can inflate and read the contents of the chunk at the given coordinates (relative to this region).
     *
     * @param x the chunk's X coordinate
     * @param z the chunk's Z coordinate
     * @return an {@link InputStream} that can inflate and read the contents of the chunk at the given coordinates, or {@code null} if the chunk is not present
     * @throws IOException if an IO exception occurs you dummy
     */
    default InputStream read(int x, int z) throws IOException {
        ByteBuf buf = this.readDirect(x, z);
        if (buf == null) {
            return null;
        } else {
            InputStream in = NettyUtil.wrapIn(buf, true);
            byte compressionId = buf.readByte();
            return compressionId == ID_NONE ? in : COMPRESSION_IDS.get(compressionId).inflate(in);
        }
    }

    /**
     * Reads the raw contents of the chunk at the given coordinates into a buffer.
     * <p>
     * Make sure to release the buffer after use!
     *
     * @param x the chunk's X coordinate
     * @param z the chunk's Z coordinate
     * @return a buffer containing the raw contents of the chunk, or {@code null} if the chunk is not present
     * @throws IOException if an IO exception occurs you dummy
     */
    ByteBuf readDirect(int x, int z) throws IOException;

    /**
     * Gets a {@link DataOut} that will compress data written to it using the given compression type. The compressed data will be written to disk at the specified
     * region-local chunk coordinates when the {@link DataOut} instance is closed (using {@link DataOut#close()}.
     *
     * @param x           the chunk's X coordinate
     * @param z           the chunk's Z coordinate
     * @param compression the type of compression to use
     * @return a {@link DataOut} for writing data to the given chunk
     * @throws ReadOnlyRegionException if the region is opened in read-only mode
     * @throws IOException             if an IO exception occurs you dummy
     */
    DataOut write(int x, int z, @NonNull CompressionHelper compression) throws ReadOnlyRegionException, IOException;

    /**
     * Gets a {@link DataOut} that will compress data written to it using the given compression type. The compressed data will be written to disk at the specified
     * region-local chunk coordinates when the {@link DataOut} instance is closed (using {@link DataOut#close()}.
     *
     * @param x             the chunk's X coordinate
     * @param z             the chunk's Z coordinate
     * @param compression   the type of compression to use
     * @param compressionId the compression's ID, for writing to disk for decompression
     * @return a {@link DataOut} for writing data to the given chunk
     * @throws ReadOnlyRegionException if the region is opened in read-only mode
     * @throws IOException             if an IO exception occurs you dummy
     */
    DataOut write(int x, int z, @NonNull CompressionHelper compression, byte compressionId) throws ReadOnlyRegionException, IOException;

    /**
     * Writes raw chunk data to the region at the given region-local coordinates.
     * <p>
     * The length header will be added automatically.
     *
     * @param x the chunk's X coordinate
     * @param z the chunk's Z coordinate
     * @param b a {@code byte[]} containing the raw chunk data. Must be prefixed with the compression version
     * @throws ReadOnlyRegionException if the region is opened in read-only mode
     * @throws IOException             if an IO exception occurs you dummy
     */
    void writeDirect(int x, int z, @NonNull byte[] b) throws ReadOnlyRegionException, IOException;

    /**
     * Writes raw chunk data to the region at the given region-local coordinates.
     *
     * @param x   the chunk's X coordinate
     * @param z   the chunk's Z coordinate
     * @param buf a {@link ByteBuf} containing the raw chunk data. Must be prefixed with the compression version and length. This will be released after
     *            invoking this method
     * @throws ReadOnlyRegionException if the region is opened in read-only mode
     * @throws IOException             if an IO exception occurs you dummy
     */
    void writeDirect(int x, int z, @NonNull ByteBuf buf) throws ReadOnlyRegionException, IOException;

    /**
     * Deletes the chunk from the region at the given region-local coordinates, overwriting sectors previously occupied by the chunk with zeroes.
     *
     * @param x the chunk's X coordinate
     * @param z the chunk's Z coordinate
     * @return whether or not the chunk was removed. If {@code false}, the chunk was not present
     * @throws ReadOnlyRegionException if the region is opened in read-only mode
     * @throws IOException             if an IO exception occurs you dummy
     */
    default boolean delete(int x, int z) throws ReadOnlyRegionException, IOException {
        return this.delete(x, z, true);
    }

    /**
     * Deletes the chunk from the region at the given region-local coordinates.
     *
     * @param x     the chunk's X coordinate
     * @param z     the chunk's Z coordinate
     * @param erase whether or not to erase the sectors previously occupied by the chunk (i.e. overwrite them with zeroes)
     * @return whether or not the chunk was removed. If {@code false}, the chunk was not present
     * @throws ReadOnlyRegionException if the region is opened in read-only mode
     * @throws IOException             if an IO exception occurs you dummy
     */
    boolean delete(int x, int z, boolean erase) throws ReadOnlyRegionException, IOException;

    /**
     * @return whether this region is opened in read-only mode
     */
    boolean readOnly();

    /**
     * Forces any pending write operations to be written to disk.
     * <p>
     * Implicitly called by an invocation of {@link #close()}.
     * <p>
     * This may not do anything, depending on the implementation.
     *
     * @throws IOException             if an IO exception occurs while trying to flush data to disk
     * @throws ReadOnlyRegionException if this region is opened in read-only mode
     */
    void flush() throws IOException, ReadOnlyRegionException;

    @Override
    void close() throws IOException;

    /**
     * The different access levels that an {@link RegionFile} may be opened using.
     *
     * @author DaPorkchop_
     */
    enum Access {
        /**
         * Opens the region in read-write mode, throwing an exception in the case of failure.
         */
        WRITE_REQUIRED,
        /**
         * Tries to open the region in read-write mode, but falls back to read-only access in case of failure.
         * <p>
         * Throws an exception if opening the region in read-only mode fails as well.
         */
        WRITE_OPTIONAL,
        /**
         * Opens the region in read-only mode, throwing an exception in the case of failure.
         */
        READ_ONLY;
    }

    /**
     * The different modes that an {@link RegionFile} may be opened in.
     *
     * @author DaPorkchop_
     */
    enum Mode {
        /**
         * DaPorkchop_'s high-performance implementation of {@link RegionFile}, copied from OverclockedRegionFile from RegionMerger.
         * <p>
         * Provides fast access to the region headers by memory-mapping the first 8KiB of the region file, and does I/O of each chunk using a single read
         * invocation.
         * <p>
         * This mode is recommended to be used unless you have a good reason not to.
         */
        STANDARD,
        /**
         * Reads the contents of the entire region file into memory upon opening it, not flushing contents until instructed to using {@link #flush()} or
         * {@link #close()}.
         */
        BUFFER_FULL,
        /**
         * Memory-maps the contents of the entire region file.
         */
        MMAP_FULL;
    }
}
