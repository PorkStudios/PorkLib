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

import lombok.NonNull;

import java.io.OutputStream;
import java.nio.channels.GatheringByteChannel;
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
     * Transfers a number of bytes to the given {@link WritableByteChannel}.
     *
     * @param out the {@link WritableByteChannel} to write data to
     * @return the number of bytes transferred
     * @throws Exception if an exception occurs while transferring the data
     */
    long transfer(@NonNull WritableByteChannel out) throws Exception;

    /**
     * Transfers the entire HTTP entity to the given {@link WritableByteChannel} in a blocking fashion, simply waiting until all bytes are written.
     *
     * @param out the {@link WritableByteChannel} to write data to
     * @return the total number of bytes transferred
     * @throws Exception if an exception occurs while transferring the data
     */
    long transferAllBlocking(@NonNull WritableByteChannel out) throws Exception;

    /**
     * @return whether or not this transfer is complete
     */
    boolean complete();

    /**
     * Closes this {@link TransferSession} instance.
     * <p>
     * This will only be called once per instance to release any resources allocated by this instance (such as memory, file handles, etc.).
     *
     * @throws Exception if an exception occurs while closing this {@link TransferSession} instance
     */
    @Override
    void close() throws Exception;
}
