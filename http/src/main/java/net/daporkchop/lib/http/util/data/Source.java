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

package net.daporkchop.lib.http.util.data;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.NonNull;

/**
 * A source for reading binary data from.
 * <p>
 * This layer of abstraction allows things like {@link net.daporkchop.lib.http.Response} to only have to return one
 * value for the payload, the implementation defines the behavior of the data.
 *
 * @author DaPorkchop_
 */
public interface Source {
    /**
     * The total size of the data (in bytes).
     * <p>
     * If the size cannot be computed for any reason, this method should return {@code -1}.
     * <p>
     * The value returned by this method must remain constant throughout the entire lifetime of an instance of this
     * class, otherwise the behavior of other code using it is undefined.
     *
     * @return the total size of the data
     */
    long size();

    /**
     * Checks whether or not the size of the data is known in advance.
     *
     * @return whether or not the size of the data is known
     */
    default boolean isSizeKnown() {
        return this.size() >= 0;
    }

    /**
     * Gets an arbitrary amount of data.
     * <p>
     * This may return {@link io.netty.buffer.Unpooled#EMPTY_BUFFER} (e.g. if still waiting for a response from a remote data source), however if
     * the data most certainly cannot be read for any reason (e.g. underlying file handle has been closed), this should return {@code null}.
     *
     * @param pos   the number of bytes that have been read up to now
     * @param alloc a {@link ByteBufAllocator} that may be used to allocate buffers
     * @return a {@link ByteBuf} containing the read data
     */
    ByteBuf read(long pos, @NonNull ByteBufAllocator alloc);

    /**
     * Called when this source's data no longer needs to be read.
     * <p>
     * The data will not necessarily have been read fully when this method is called.
     */
    default void done() {
    }
}
