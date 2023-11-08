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

package net.daporkchop.lib.http.entity;

import lombok.NonNull;
import net.daporkchop.lib.http.entity.content.encoding.ContentEncoding;
import net.daporkchop.lib.http.entity.content.encoding.StandardContentEncoding;
import net.daporkchop.lib.http.entity.content.type.ContentType;
import net.daporkchop.lib.http.entity.content.type.StandardContentType;
import net.daporkchop.lib.http.entity.transfer.TransferSession;
import net.daporkchop.lib.http.entity.transfer.encoding.StandardTransferEncoding;
import net.daporkchop.lib.http.entity.transfer.encoding.TransferEncoding;

/**
 * Represents some form of content that will be sent over an HTTP connection.
 *
 * @author DaPorkchop_
 */
public interface HttpEntity {
    /**
     * Wraps the given {@code byte[]} into a {@link HttpEntity} instance with the content type of {@link StandardContentType#APPLICATION_OCTET_STREAM}.
     *
     * @param data the data to wrap
     * @return a {@link HttpEntity} instance with the given data
     */
    static HttpEntity of(@NonNull byte[] data) {
        return of(StandardContentType.APPLICATION_OCTET_STREAM, data);
    }

    /**
     * Wraps the given {@code byte[]} into a {@link HttpEntity} instance with the given content type.
     *
     * @param contentType the content type of the data
     * @param data        the data to wrap
     * @return a {@link HttpEntity} instance with the given data
     */
    static HttpEntity of(@NonNull String contentType, @NonNull byte[] data) {
        return of(ContentType.parse(contentType), data);
    }

    /**
     * Wraps the given {@code byte[]} into a {@link HttpEntity} instance with the given {@link ContentType}.
     *
     * @param contentType the {@link ContentType} of the data
     * @param data        the data to wrap
     * @return a {@link HttpEntity} instance with the given data
     */
    static HttpEntity of(@NonNull ContentType contentType, @NonNull byte[] data) {
        return new ByteArrayHttpEntity(contentType, data);
    }

    /**
     * @return this entity's {@link ContentType}
     */
    ContentType type();

    /**
     * Gets the {@link ContentEncoding} used for encoding this entity's data.
     * <p>
     * The data returned by this {@link HttpEntity}'s various keys methods must already be encoded using the {@link ContentEncoding} returned by this
     * method, as the internal HTTP engine will NOT do the encoding automatically.
     *
     * @return this entity's {@link ContentEncoding}
     */
    default ContentEncoding encoding() {
        return StandardContentEncoding.identity;
    }

    /**
     * @return a new {@link TransferSession} instance for transferring this entity's data to a single remote peer
     * @throws Exception if an exception occurs while creating the session
     */
    TransferSession newSession() throws Exception;
}
