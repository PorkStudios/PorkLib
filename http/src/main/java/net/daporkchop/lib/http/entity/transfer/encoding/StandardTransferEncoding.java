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

package net.daporkchop.lib.http.entity.transfer.encoding;

/**
 * Standard values for the {@link TransferEncoding} HTTP header.
 *
 * @author DaPorkchop_
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Transfer-Encoding#Directives">Transfer-Encoding§Directives at Mozilla</a>
 * @see net.daporkchop.lib.http.entity.content.encoding.StandardContentEncoding
 */
public enum StandardTransferEncoding implements TransferEncoding {
    /**
     * The data is not encoded in any way, and is transmitted as-is over the connection.
     */
    identity,
    /**
     * Rather than sending a "Content-Length" header, and sending the body by simply the HTTP entity's data directly across the TCP connection, keys is
     * sent in a series of length-prefixed chunks which, on the receiving end, are aggregated back into a single continuous data stream.
     * <p>
     * This is beneficial e.g. for applications where the entity's data is dynamically generated, and the performance impact of generating the entire
     * body in order to know the length, and then writing all the buffered data outweighs the slightly larger transport overhead (due to length-prefixes
     * for each chunk).
     */
    chunked,
    /**
     * The data is compressed using the DEFLATE algorithm.
     */
    deflate,
    /**
     * The data is compressed using the GZIP algorithm.
     */
    gzip,
    /**
     * The data is compressed using the legacy UNIX {@code compress} program.
     * <p>
     * Generally not supported by client or server implementations, only here for legacy reasons.
     */
    @Deprecated
    compress;
}
