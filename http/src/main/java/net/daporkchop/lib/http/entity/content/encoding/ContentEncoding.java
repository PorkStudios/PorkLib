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

package net.daporkchop.lib.http.entity.content.encoding;

import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A value for the "Content-Encoding" HTTP header.
 *
 * @author DaPorkchop_
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Encoding">Content-Encoding at Mozilla</a>
 */
public interface ContentEncoding {
    /**
     * Gets a {@link ContentEncoding} which matches the given name.
     *
     * @param name the name of the {@link ContentEncoding}
     * @return a {@link ContentEncoding} which matches the given name
     */
    static ContentEncoding of(@NonNull String name) {
        try {
            return StandardContentEncoding.valueOf(name);
        } catch (IllegalArgumentException e) {
            return new UnknownContentEncoding(name);
        }
    }

    /**
     * @return the textual name of this {@link ContentEncoding} method
     */
    String name();

    /**
     * Wraps the given {@link OutputStream} with a new {@link OutputStream} that will encode any data written to it using this content encoding and
     * forward the encoded data on to the given {@link OutputStream}.
     * <p>
     * Closing the {@link OutputStream} returned by this method will always result in the given {@link OutputStream} being closed as well.
     *
     * @param delegate the {@link OutputStream} to write encoded data to
     * @return an {@link OutputStream} that will encode all data written to it and forward it to the given {@link OutputStream}
     * @throws IOException                   if an IO exception occurs while wrapping the given {@link OutputStream}
     * @throws UnsupportedOperationException if this {@link ContentEncoding} does not support encoding
     */
    default OutputStream encodingStream(@NonNull OutputStream delegate) throws IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException("encodingStream");
    }

    /**
     * Wraps the given {@link InputStream} with a new {@link InputStream} that will read and decode data using this content encoding.
     * <p>
     * Closing the {@link InputStream} returned by this method will always result in the given {@link InputStream} being closed as well.
     *
     * @param source the {@link InputStream} to read encoded data from
     * @return an {@link InputStream} that will read and decode data from the given {@link InputStream}
     * @throws IOException                   if an IO exception occurs while wrapping the given {@link InputStream}
     * @throws UnsupportedOperationException if this {@link ContentEncoding} does not support decoding
     */
    default InputStream decodingStream(@NonNull InputStream source) throws IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException("decodingStream");
    }
}
