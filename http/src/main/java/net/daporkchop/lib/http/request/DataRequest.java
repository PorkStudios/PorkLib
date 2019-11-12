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

package net.daporkchop.lib.http.request;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * A {@link Request} which provides a traditional {@link InputStream} and {@link OutputStream} for transferring
 * data to/from the remote server.
 *
 * @author DaPorkchop_
 */
public interface DataRequest extends Request<Void> {
    /**
     * Gets an {@link OutputStream} for sending data to the remote server.
     * <p>
     * This is only supported for some HTTP methods.
     * <p>
     * For some methods (such as POST) data will not start to be received until this {@link OutputStream} has
     * been closed (indicating the end of the data).
     *
     * @return an {@link OutputStream} for sending data to the remote server
     * @throws UnsupportedOperationException if the request's HTTP method doesn't support sending data
     * @throws IllegalStateException         if the request's HTTP method does support sending data, but the {@link OutputStream} has already been closed
     */
    OutputStream output() throws UnsupportedOperationException, IllegalStateException;

    /**
     * Gets an {@link InputStream} for receiving data from the remote server.
     * <p>
     * This is only supported for some HTTP methods.
     * <p>
     * Closing the stream (using {@link InputStream#close()} will close this request.
     *
     * @return an {@link InputStream} for receiving data from the remote server
     * @throws UnsupportedOperationException if the request's HTTP method doesn't support receiving data
     */
    InputStream input() throws UnsupportedOperationException;
}
