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

package net.daporkchop.lib.http.entity.transfer;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.http.entity.transfer.encoding.StandardTransferEncoding;
import net.daporkchop.lib.http.entity.transfer.encoding.TransferEncoding;

import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * Handles the actual process of transferring the HTTP entity's data to a single remote peer.
 * <p>
 * Implementations of this interface are not expected to be thread-safe. Attempting to use an instance of {@link TransferSession} from a different
 * thread than it was originally created on will result in undefined behavior.
 *
 * @author DaPorkchop_
 */
//implementing this later on netty won't be too useful if TransferSession is hogging all the worker threads
public interface TransferSession extends AutoCloseable {
    /**
     * @return the starting position of the transfer
     * @throws Exception if an exception occurs
     */
    long position() throws Exception;

    /**
     * Gets the size (in bytes) of this entity's data.
     * <p>
     * If, for whatever reason, the data's size is not known in advance, this method should return {@code -1L}. By default, this will result in the
     * data being sent using the "chunked" Transfer-Encoding rather than simply setting "Content-Length".
     *
     * @return the size (in bytes) of this entity's data, or {@code -1L} if it is not known
     * @throws Exception if an exception occurs
     */
    long length() throws Exception;

    /**
     * @return the {@link TransferEncoding} that will be used for transferring data to the remote endpoint
     * @throws Exception if an exception occurs
     */
    default TransferEncoding transferEncoding() throws Exception {
        return this.length() < 0L ? StandardTransferEncoding.chunked : StandardTransferEncoding.identity;
    }

    /**
     * Transfers a number of bytes to the given {@link WritableByteChannel}.
     *
     * @param position the position to start the transfer at
     * @param out      the {@link WritableByteChannel} to write data to
     * @return the number of bytes transferred
     * @throws Exception if an exception occurs while transferring the data
     */
    long transfer(long position, @NonNull WritableByteChannel out) throws Exception;

    /**
     * Transfers the entire HTTP entity to the given {@link WritableByteChannel} in a blocking fashion, simply waiting until all bytes are written.
     *
     * @param position the position to start the transfer at
     * @param out      the {@link WritableByteChannel} to write data to
     * @return the total number of bytes transferred
     * @throws Exception if an exception occurs while transferring the data
     */
    long transferAllBlocking(long position, @NonNull WritableByteChannel out) throws Exception;

    /**
     * @return whether or not this {@link TransferSession}'s data is available as a {@link ByteBuf}
     */
    default boolean hasByteBuf() {
        return false;
    }

    /**
     * Gets the contents of this {@link TransferSession} as a {@link ByteBuf}.
     * <p>
     * The returned {@link ByteBuf} will be a retained slice of the original (must be released manually).
     *
     * @return the contents of this {@link TransferSession} as a {@link ByteBuf}
     * @throws UnsupportedOperationException if this {@link TransferSession} does not support accessing it's data as a {@link ByteBuf} ({@link #hasByteBuf()} is {@code false})
     * @throws Exception                     if an exception occurs
     */
    default ByteBuf getByteBuf() throws Exception {
        throw new UnsupportedOperationException();
    }

    /**
     * @return whether or not this {@link TransferSession}'s data is available as a {@link ByteBuffer}
     */
    default boolean hasNioBuffer() {
        return false;
    }

    /**
     * Gets the contents of this {@link TransferSession} as a {@link ByteBuffer}.
     * <p>
     * The returned {@link ByteBuffer} will be a slice of the original.
     *
     * @return the contents of this {@link TransferSession} as a {@link ByteBuffer}
     * @throws UnsupportedOperationException if this {@link TransferSession} does not support accessing it's data as a {@link ByteBuffer} ({@link #hasNioBuffer()} is {@code false})
     * @throws Exception                     if an exception occurs
     */
    default ByteBuffer getNioBuffer() throws Exception {
        throw new UnsupportedOperationException();
    }

    /**
     * Closes this {@link TransferSession} instance.
     * <p>
     * This will only be called once once the transfer is completed release any resources allocated by this instance (such as memory, file handles, etc.).
     *
     * @throws Exception if an exception occurs while closing this {@link TransferSession} instance
     */
    @Override
    void close() throws Exception;
}
