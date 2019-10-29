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
import lombok.NonNull;
import net.daporkchop.lib.binary.util.capability.Closeable;

import java.io.IOException;

/**
 * A source for reading binary data from.
 * <p>
 * This layer of abstraction allows things like {@link net.daporkchop.lib.http.Response} to only have to return one
 * value for the payload, the implementation defines the behavior of the data.
 *
 * @author DaPorkchop_
 */
public interface Source extends Closeable<IOException> {
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
        return this.size() != -1L;
    }

    /**
     * Reads as much data as possible into the given {@link ByteBuf}.
     *
     * @param dst the buffer to write data to
     * @return the number of bytes read
     */
    int read(@NonNull ByteBuf dst);
}
