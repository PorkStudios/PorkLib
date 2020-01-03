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
import lombok.NonNull;
import net.daporkchop.lib.http.StatusCode;
import net.daporkchop.lib.http.entity.ByteBufHttpEntity;
import net.daporkchop.lib.http.entity.HttpEntity;
import net.daporkchop.lib.http.entity.content.type.ContentType;
import net.daporkchop.lib.http.entity.content.type.StandardContentType;

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
     * Sets the body of the response.
     *
     * @param entity the {@link HttpEntity} to respond with
     * @return this {@link ResponseBuilder} instance
     */
    ResponseBuilder body(@NonNull HttpEntity entity);

    /**
     * Sets the body of the response.
     * <p>
     * The response will consist only of the data in the given {@link ByteBuf} (Content-Length will be set to {@link ByteBuf#readableBytes()}).
     *
     * @param buf the {@link ByteBuf} containing the response body
     * @return this {@link ResponseBuilder} instance
     */
    default ResponseBuilder body(@NonNull ByteBuf buf) {
        return this.body(new ByteBufHttpEntity(StandardContentType.APPLICATION_OCTET_STREAM, buf));
    }

    /**
     * Sets the body of the response.
     * <p>
     * The response will consist only of the data in the given {@link ByteBuf} (Content-Length will be set to {@link ByteBuf#readableBytes()}).
     *
     * @param type the {@link ContentType} of the response data
     * @param buf  the {@link ByteBuf} containing the response body
     * @return this {@link ResponseBuilder} instance
     */
    default ResponseBuilder body(@NonNull ContentType type, @NonNull ByteBuf buf) {
        return this.body(new ByteBufHttpEntity(type, buf));
    }
}
