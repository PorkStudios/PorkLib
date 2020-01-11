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

package net.daporkchop.lib.http.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.NonNull;
import net.daporkchop.lib.binary.util.ReferenceCountedFileChannel;
import net.daporkchop.lib.http.StatusCode;
import net.daporkchop.lib.http.entity.ByteArrayHttpEntity;
import net.daporkchop.lib.http.entity.ByteBufHttpEntity;
import net.daporkchop.lib.http.entity.FileRegionHttpEntity;
import net.daporkchop.lib.http.entity.HttpEntity;
import net.daporkchop.lib.http.entity.content.type.ContentType;
import net.daporkchop.lib.http.entity.content.type.StandardContentType;
import net.daporkchop.lib.http.header.Header;
import net.daporkchop.lib.http.header.map.HeaderMap;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Used for building a response to an incoming HTTP request.
 *
 * @author DaPorkchop_
 */
public interface ResponseBuilder {
    /**
     * Sets the {@link StatusCode} of the response.
     *
     * @param status the {@link StatusCode} to respond with
     * @return this {@link ResponseBuilder} instance
     */
    ResponseBuilder status(@NonNull StatusCode status);

    /**
     * @return a {@link HeaderMap} containing the headers that will be sent with the request
     */
    HeaderMap headers();

    /**
     * @return this {@link ResponseBuilder} instance
     * @see net.daporkchop.lib.http.header.map.MutableHeaderMap#put(String, String)
     */
    ResponseBuilder putHeader(@NonNull String key, @NonNull String value);

    /**
     * @return this {@link ResponseBuilder} instance
     * @see net.daporkchop.lib.http.header.map.MutableHeaderMap#put(String, List)
     */
    ResponseBuilder putHeader(@NonNull String key, @NonNull List<String> values);

    /**
     * @return this {@link ResponseBuilder} instance
     * @see net.daporkchop.lib.http.header.map.MutableHeaderMap#put(Header)
     */
    ResponseBuilder putHeader(@NonNull Header header);

    /**
     * @return this {@link ResponseBuilder} instance
     * @see net.daporkchop.lib.http.header.map.MutableHeaderMap#add(String, String)
     */
    ResponseBuilder addHeader(@NonNull String key, @NonNull String value);

    /**
     * @return this {@link ResponseBuilder} instance
     * @see net.daporkchop.lib.http.header.map.MutableHeaderMap#add(String, List)
     */
    ResponseBuilder addHeader(@NonNull String key, @NonNull List<String> values);

    /**
     * @return this {@link ResponseBuilder} instance
     * @see net.daporkchop.lib.http.header.map.MutableHeaderMap#add(Header)
     */
    ResponseBuilder addHeader(@NonNull Header header);

    /**
     * Sets the body of the response.
     *
     * @param entity the {@link HttpEntity} to respond with
     * @return this {@link ResponseBuilder} instance
     */
    ResponseBuilder body(@NonNull HttpEntity entity);

    /**
     * Sets the body of the response.
     *
     * @param buf the {@link ByteBuf} containing the response body
     * @return this {@link ResponseBuilder} instance
     */
    default ResponseBuilder body(@NonNull ByteBuf buf) {
        return this.body(new ByteBufHttpEntity(StandardContentType.APPLICATION_OCTET_STREAM, buf));
    }

    /**
     * Sets the body of the response.
     *
     * @param type the {@link ContentType} of the response data
     * @param buf  the {@link ByteBuf} containing the response body
     * @return this {@link ResponseBuilder} instance
     */
    default ResponseBuilder body(@NonNull ContentType type, @NonNull ByteBuf buf) {
        return this.body(new ByteBufHttpEntity(type, buf));
    }

    /**
     * Sets the body of the response.
     *
     * @param arr the {@code byte[]} containing the response body
     * @return this {@link ResponseBuilder} instance
     */
    default ResponseBuilder body(@NonNull byte[] arr) {
        return this.body(new ByteArrayHttpEntity(StandardContentType.APPLICATION_OCTET_STREAM, arr));
    }

    /**
     * Sets the body of the response.
     *
     * @param type the {@link ContentType} of the response data
     * @param arr  the {@code byte[]} containing the response body
     * @return this {@link ResponseBuilder} instance
     */
    default ResponseBuilder body(@NonNull ContentType type, @NonNull byte[] arr) {
        return this.body(new ByteArrayHttpEntity(type, arr));
    }

    /**
     * @see #bodyTextUTF8(CharSequence)
     */
    default ResponseBuilder body(@NonNull CharSequence text) {
        return this.body(text, StandardCharsets.UTF_8, "text/plain");
    }

    /**
     * Sets the body of the response.
     * <p>
     * The body will be encoded using the {@link StandardCharsets#UTF_8} charset, and sent with a content-type of {@code text/plain}.
     *
     * @param text the {@link CharSequence} containing the response body
     * @return this {@link ResponseBuilder} instance
     */
    default ResponseBuilder bodyTextUTF8(@NonNull CharSequence text) {
        return this.body(text, StandardCharsets.UTF_8, "text/plain");
    }

    /**
     * Sets the body of the response.
     * <p>
     * The body will be encoded using the {@link StandardCharsets#UTF_8} charset, and sent with a content-type of {@code text/html}.
     *
     * @param text the {@link CharSequence} containing the response body
     * @return this {@link ResponseBuilder} instance
     */
    default ResponseBuilder bodyHtmlUTF8(@NonNull CharSequence text) {
        return this.body(text, StandardCharsets.UTF_8, "text/html");
    }

    /**
     * Sets the body of the response.
     * <p>
     * The body will be encoded using the {@link StandardCharsets#US_ASCII} charset, and sent with a content-type of {@code text/plain}.
     *
     * @param text the {@link CharSequence} containing the response body
     * @return this {@link ResponseBuilder} instance
     */
    default ResponseBuilder bodyTextASCII(@NonNull CharSequence text) {
        return this.body(text, StandardCharsets.US_ASCII, "text/plain");
    }

    /**
     * Sets the body of the response.
     * <p>
     * The body will be encoded using the {@link StandardCharsets#US_ASCII} charset, and sent with a content-type of {@code text/html}.
     *
     * @param text the {@link CharSequence} containing the response body
     * @return this {@link ResponseBuilder} instance
     */
    default ResponseBuilder bodyHtmlASCII(@NonNull CharSequence text) {
        return this.body(text, StandardCharsets.US_ASCII, "text/html");
    }

    /**
     * Sets the body of the response.
     *
     * @param text     the {@link CharSequence} containing the response body
     * @param charset  the {@link Charset} to encode the response body in
     * @param mimeType the MIME type of the body
     * @return this {@link ResponseBuilder} instance
     */
    default ResponseBuilder body(@NonNull CharSequence text, @NonNull Charset charset, @NonNull String mimeType) {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.ioBuffer(text.length());
        try {
            buf.writeCharSequence(text, charset);
            return this.body(ContentType.of(mimeType, charset.name()), buf);
        } catch (Exception e) {
            try {
                buf.release();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            PUnsafe.throwException(e);
            throw new IllegalStateException();
        }
    }

    /**
     * Sets the body of the response.
     *
     * @param file the {@link File} whose content should be sent
     * @return this {@link ResponseBuilder} instance
     */
    default ResponseBuilder body(@NonNull File file) {
        return this.body(StandardContentType.APPLICATION_OCTET_STREAM, file);
    }

    /**
     * Sets the body of the response.
     *
     * @param type the {@link ContentType} of the response data
     * @param file the {@link File} whose content should be sent
     * @return this {@link ResponseBuilder} instance
     */
    default ResponseBuilder body(@NonNull ContentType type, @NonNull File file) {
        try {
            FileRegionHttpEntity entity = new FileRegionHttpEntity(type, file);
            if (entity.release())   {
                throw new IllegalStateException();
            }
            return this.body(entity);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the body of the response.
     *
     * @param file the {@link File} whose content should be sent
     * @param position the start position (inclusive) in the file to start reading from
     * @param length   the size (in bytes) of the region to read from the file
     * @return this {@link ResponseBuilder} instance
     */
    default ResponseBuilder body(@NonNull File file, long position, long length) {
        return this.body(StandardContentType.APPLICATION_OCTET_STREAM, file, position, length);
    }

    /**
     * Sets the body of the response.
     *
     * @param type the {@link ContentType} of the response data
     * @param file the {@link File} whose content should be sent
     * @param position the start position (inclusive) in the file to start reading from
     * @param length   the size (in bytes) of the region to read from the file
     * @return this {@link ResponseBuilder} instance
     */
    default ResponseBuilder body(@NonNull ContentType type, @NonNull File file, long position, long length) {
        try {
            FileRegionHttpEntity entity = new FileRegionHttpEntity(type, file, position, length);
            if (entity.release())   {
                throw new IllegalStateException();
            }
            return this.body(entity);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the body of the response.
     *
     * @param channel the {@link ReferenceCountedFileChannel} whose content should be sent
     * @return this {@link ResponseBuilder} instance
     */
    default ResponseBuilder body(@NonNull ReferenceCountedFileChannel channel) {
        return this.body(StandardContentType.APPLICATION_OCTET_STREAM, channel);
    }

    /**
     * Sets the body of the response.
     *
     * @param type     the {@link ContentType} of the response data
     * @param channel  the {@link ReferenceCountedFileChannel} whose content should be sent
     * @return this {@link ResponseBuilder} instance
     */
    default ResponseBuilder body(@NonNull ContentType type, @NonNull ReferenceCountedFileChannel channel) {
        try {
            FileRegionHttpEntity entity = new FileRegionHttpEntity(type, channel, true);
            if (entity.release())   {
                throw new IllegalStateException();
            }
            return this.body(entity);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the body of the response.
     *
     * @param channel the {@link ReferenceCountedFileChannel} whose content should be sent
     * @param position the start position (inclusive) in the file to start reading from
     * @param length   the size (in bytes) of the region to read from the file
     * @return this {@link ResponseBuilder} instance
     */
    default ResponseBuilder body(@NonNull ReferenceCountedFileChannel channel, long position, long length) {
        return this.body(StandardContentType.APPLICATION_OCTET_STREAM, channel, position, length);
    }

    /**
     * Sets the body of the response.
     *
     * @param type     the {@link ContentType} of the response data
     * @param channel  the {@link ReferenceCountedFileChannel} whose content should be sent
     * @param position the start position (inclusive) in the file to start reading from
     * @param length   the size (in bytes) of the region to read from the file
     * @return this {@link ResponseBuilder} instance
     */
    default ResponseBuilder body(@NonNull ContentType type, @NonNull ReferenceCountedFileChannel channel, long position, long length) {
        try {
            FileRegionHttpEntity entity = new FileRegionHttpEntity(type, channel, position, length, true);
            if (entity.release())   {
                throw new IllegalStateException();
            }
            return this.body(entity);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
