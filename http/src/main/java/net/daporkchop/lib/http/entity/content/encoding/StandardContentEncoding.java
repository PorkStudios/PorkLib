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
