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

package net.daporkchop.lib.minecraft.format.anvil.region;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.common.util.exception.ReadOnlyException;

import java.io.File;
import java.io.Flushable;
import java.io.IOException;

/**
 * An Anvil region file, consisting of a 32² area of chunks and serving as the core of the Anvil storage format.
 *
 * @author DaPorkchop_
 */
public interface RegionFile extends Flushable, AutoCloseable {
    /**
     * Reads the chunk at the given coordinates into a buffer.
     * <p>
     * Care should be taken to ensure the chunk is released after use.
     *
     * @param x the chunk's X coordinate
     * @param z the chunk's Z coordinate
     * @return a {@link RawChunk} instance containing the chunk's data
     */
    RawChunk read(int x, int z) throws IOException;

    /**
     * Writes the given chunk to disk.
     *
     * @param x              the chunk's X coordinate
     * @param z              the chunk's Z coordinate
     * @param data           the chunk's data
     * @param version        the compression version used for compressing the chunk's data
     * @param timestamp      the new "last modified" value, in milliseconds since the UNIX epoch
     * @param forceOverwrite whether or not to forcibly overwrite the existing chunk, even if the existing timestamp is newer
     * @return whether or not the chunk was written. Chunks will not be overwritten if the given timestamp is older than the existing one
     * @throws ReadOnlyException if this region is opened in read-only mode
     */
    boolean write(int x, int z, @NonNull ByteBuf data, int version, long timestamp, boolean forceOverwrite) throws ReadOnlyException, IOException;

    /**
     * Deletes the chunk from the region at the given region-local coordinates, either zeroing out any sectors previously occupied by the chunk or shifting
     * data around to fill up the gap.
     *
     * @param x the chunk's X coordinate
     * @param z the chunk's Z coordinate
     * @return whether or not the chunk was removed. If {@code false}, the chunk was not present
     * @throws ReadOnlyException if this region is opened in read-only mode
     */
    boolean delete(int x, int z) throws ReadOnlyException, IOException;

    /**
     * Checks whether or not the given chunk is present in this region.
     *
     * @param x the chunk's X coordinate
     * @param z the chunk's Z coordinate
     * @return whether or not the chunk at the given coordinates is present
     */
    boolean contains(int x, int z) throws IOException;

    /**
     * Gets the timestamp value for the chunk at the given coordinates.
     *
     * @param x the chunk's X coordinate
     * @param z the chunk's Z coordinate
     * @return the chunk's timestamp, or {@code -1} if the chunk wasn't present
     */
    long timestamp(int x, int z) throws IOException;

    /**
     * Attempts to defrag this region file.
     * <p>
     * This will make a best-effort attempt to move chunks around to eliminate any wasted sectors.
     *
     * @throws ReadOnlyException if this region is opened in read-only mode
     */
    void defrag() throws ReadOnlyException, IOException;

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
     */
    default void assertWritable() throws ReadOnlyException {
        if (this.readOnly()) {
            throw new ReadOnlyException(this.file().getAbsolutePath());
        }
    }

    /**
     * Forces any changes that may be buffered internally to be written to disk.
     * <p>
     * This method will block until the data has been completely written.
     */
    @Override
    void flush() throws IOException;

    @Override
    void close() throws IOException;
}
