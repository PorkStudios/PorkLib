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
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * Standard values for the {@link ContentEncoding} HTTP header.
 *
 * @author DaPorkchop_
 */
public enum StandardContentEncoding implements ContentEncoding {
    /**
     * The data is not encoded in any way, and is transmitted as-is over the connection.
     */
    identity {
        @Override
        public OutputStream encodingStream(@NonNull OutputStream delegate) throws IOException, UnsupportedOperationException {
            return delegate;
        }

        @Override
        public InputStream decodingStream(@NonNull InputStream source) throws IOException, UnsupportedOperationException {
            return source;
        }
    },
    /**
     * The data is compressed using the DEFLATE algorithm.
     */
    deflate {
        @Override
        public OutputStream encodingStream(@NonNull OutputStream delegate) throws IOException, UnsupportedOperationException {
            return new DeflaterOutputStream(delegate);
        }

        @Override
        public InputStream decodingStream(@NonNull InputStream source) throws IOException, UnsupportedOperationException {
            return new InflaterInputStream(source);
        }
    },
    /**
     * The data is compressed using the GZIP algorithm.
     */
    gzip {
        @Override
        public OutputStream encodingStream(@NonNull OutputStream delegate) throws IOException, UnsupportedOperationException {
            return new GZIPOutputStream(delegate);
        }

        @Override
        public InputStream decodingStream(@NonNull InputStream source) throws IOException, UnsupportedOperationException {
            return new GZIPInputStream(source);
        }
    },
    /**
     * The data is compressed using the legacy UNIX {@code compress} program.
     * <p>
     * Generally not supported by client or server implementations, only here for legacy reasons.
     */
    @Deprecated
    compress;
}
