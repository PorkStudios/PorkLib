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

package net.daporkchop.lib.http.entity.transfer;

import net.daporkchop.lib.http.entity.transfer.encoding.StandardTransferEncoding;
import net.daporkchop.lib.http.entity.transfer.encoding.TransferEncoding;

/**
 * Handles the actual process of transferring the HTTP entity's data to a single remote peer.
 *
 * @author DaPorkchop_
 */
public interface TransferSession extends AutoCloseable {
    /**
     * Gets the size (in bytes) of this entity's data.
     * <p>
     * If, for whatever reason, the data's size is not known in advance, this method should return {@code -1L}. By default, this will result in the
     * data being sent using the "chunked" Transfer-Encoding rather than simply setting "Content-Length".
     *
     * @return the size (in bytes) of this entity's data, or {@code -1L} if it is not known
     */
    long length();

    /**
     * @return the {@link TransferEncoding} that will be used for transferring data to the remote endpoint
     */
    default TransferEncoding encoding() {
        return this.length() < 0L ? StandardTransferEncoding.chunked : StandardTransferEncoding.identity;
    }

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
