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

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.minecraft.world.format.anvil.region.ex.CorruptedRegionException;
import net.daporkchop.lib.minecraft.world.format.anvil.region.ex.ReadOnlyRegionException;
import net.daporkchop.lib.minecraft.world.format.anvil.region.impl.BufferedRegionFile;
import net.daporkchop.lib.minecraft.world.format.anvil.region.impl.MemoryMappedRegionFile;
import net.daporkchop.lib.minecraft.world.format.anvil.region.impl.OverclockedRegionFile;

import java.io.File;
import java.io.IOException;


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
        switch (options.mode) {
            case STANDARD:
                return new OverclockedRegionFile(file, options);
            case BUFFER_FULL:
                return new BufferedRegionFile(file, options);
            case MMAP_FULL:
                return new MemoryMappedRegionFile(file, options);
        }
        throw new IllegalArgumentException(options.toString());
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
     * Writes raw chunk data to the region at the given region-local coordinates.
     *
     * @param x   the chunk's X coordinate
     * @param z   the chunk's Z coordinate
     * @param buf a {@link ByteBuf} containing the raw chunk data. Must be prefixed with the compression version and length. This will be released after
     *            invoking this method
     * @throws ReadOnlyRegionException if the region is opened in read-only mode
     * @throws IOException             if an IO exception occurs you dummy
     */
    default void writeDirect(int x, int z, @NonNull ByteBuf buf) throws ReadOnlyRegionException, IOException {
        this.writeDirect(x, z, buf, System.currentTimeMillis(), true);
    }

    /**
     * Writes raw chunk data to the region at the given region-local coordinates.
     *
     * @param x    the chunk's X coordinate
     * @param z    the chunk's Z coordinate
     * @param buf  a {@link ByteBuf} containing the raw chunk data. Must be prefixed with the compression version and length. This will be released after
     *             invoking this method
     * @param time the UNIX timestamp to set as the "last modified" time for the chunk
     * @throws ReadOnlyRegionException if the region is opened in read-only mode
     * @throws IOException             if an IO exception occurs you dummy
     */
    default void writeDirect(int x, int z, @NonNull ByteBuf buf, long time) throws ReadOnlyRegionException, IOException {
        this.writeDirect(x, z, buf, time, true);
    }

    /**
     * Writes raw chunk data to the region at the given region-local coordinates.
     *
     * @param x              the chunk's X coordinate
     * @param z              the chunk's Z coordinate
     * @param buf            a {@link ByteBuf} containing the raw chunk data. Must be prefixed with the compression version and length. This will be released after
     *                       invoking this method
     * @param time           the UNIX timestamp to set as the "last modified" time for the chunk
     * @param forceOverwrite if {@code true}, existing chunks will be forcibly overwritten. If {@code false}, existing chunk will only be overwritten if
     *                       the given timestamp is newer than the existing one
     * @return whether or not the chunk was written (will only be {@code false} if forceOverwrite is {@code false} and the given timestamp is older than the existing one)
     * @throws ReadOnlyRegionException if the region is opened in read-only mode
     * @throws IOException             if an IO exception occurs you dummy
     */
    boolean writeDirect(int x, int z, @NonNull ByteBuf buf, long time, boolean forceOverwrite) throws ReadOnlyRegionException, IOException;

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
     * Checks whether or not the chunk at the given region-local coordinates is present in the region.
     *
     * @param x the chunk's X coordinate
     * @param z the chunk's Z coordinate
     * @return whether or not the chunk is present
     */
    default boolean hasChunk(int x, int z) {
        return this.getOffset(x, z) != 0;
    }

    /**
     * Gets the encoded offset value for the chunk at the given region-local coordinates.
     *
     * @param x the chunk's X coordinate
     * @param z the chunk's Z coordinate
     * @return the chunk's offset value
     */
    int getOffset(int x, int z);

    /**
     * Gets the last modified timestamp for the chunk at the given region-local coordinates.
     *
     * @param x the chunk's X coordinate
     * @param z the chunk's Z coordinate
     * @return the chunk's last modified timestamp
     */
    long getTimestamp(int x, int z);

    /**
     * @return the underlying {@link File} that this region is stored in
     */
    File file();

    /**
     * @return whether this region is opened in read-only mode
     */
    boolean readOnly();

    /**
     * Ensures that this region is writable.
     *
     * @throws ReadOnlyRegionException if this region is not writable
     */
    default void assertWritable() throws ReadOnlyRegionException {
        if (this.readOnly()) {
            throw new ReadOnlyRegionException(this);
        }
    }

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
         * <p>
         * Only compatible with {@link Access#READ_ONLY}.
         */
        MMAP_FULL;
    }
}
