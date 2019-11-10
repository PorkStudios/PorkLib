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

package net.daporkchop.lib.http.client.request;

import net.daporkchop.lib.http.StatusCode;
import net.daporkchop.lib.http.util.header.HeaderMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An HTTP request that uses blocking IO operations.
 *
 * @author DaPorkchop_
 */
public interface BlockingRequest extends ClientRequest<BlockingRequest> {
    /**
     * @return the status code that the server responded with
     */
    StatusCode statusCode();

    /**
     * @return the list of headers that the server responded with
     */
    HeaderMap headers();

    /**
     * @return an {@link OutputStream} to which data to to the remote server may be written
     * @throws IOException if an IO exception occurs
     */
    OutputStream output() throws IOException;

    /**
     * @return an {@link InputStream} from which data from the remote server may be read
     * @throws IOException if an IO exception occurs
     */
    InputStream input() throws IOException;

    /**
     * Closes this HTTP request.
     *
     * @throws IOException if an IO exception occurs while closing the connection
     */
    void close() throws IOException;
}
