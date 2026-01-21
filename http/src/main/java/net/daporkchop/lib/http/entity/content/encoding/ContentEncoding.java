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
