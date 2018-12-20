/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

package net.daporkchop.lib.http.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.UTF8;
import net.daporkchop.lib.concurrent.cache.SoftThreadCache;
import net.daporkchop.lib.concurrent.cache.ThreadCache;
import net.daporkchop.lib.http.HTTPVersion;
import net.daporkchop.lib.http.ResponseCode;

import java.util.HashMap;
import java.util.Map;

/**
 * Used for sending data back to a client
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class Response implements AutoCloseable {
    private static final ThreadCache<Map<String, byte[]>> PARAMETER_MAP_CACHE = SoftThreadCache.of(HashMap::new);
    private static final byte[] NEWLINE = "\r\n".getBytes(UTF8.utf8);
    private static final byte[] PARAMETER_SEPARATOR = ": ".getBytes(UTF8.utf8);

    @NonNull
    private final Channel channel;
    private final Map<String, byte[]> parameters = PARAMETER_MAP_CACHE.get();
    private ResponseCode status = ResponseCode.Internal_Server_Error;

    /**
     * Sets a parameter to a given value
     *
     * @param name  the name of the parameter to set
     * @param value the value to set it to
     * @return this response
     */
    public Response setParameter(@NonNull String name, @NonNull byte[] value) {
        this.parameters.put(name, value);
        return this;
    }

    /**
     * Sets a parameter to a given value
     *
     * @param name  the name of the parameter to set
     * @param value the value to set it to
     * @return this response
     */
    public Response setParameter(@NonNull String name, @NonNull String value) {
        this.parameters.put(name, value.getBytes(UTF8.utf8));
        return this;
    }

    /**
     * Sends the response data (response code and headers) to the client.
     * <p>
     * This method must be called before writing any data.
     *
     * @return this response
     */
    public Response send() {
        ByteBuf buf = this.channel.alloc().ioBuffer();
        buf.writeBytes(String.format("%s %d OK", HTTPVersion.V1_1.getIdentifierName(), this.status.getCode()).getBytes(UTF8.utf8));
        this.parameters.forEach((key, value) -> buf.writeBytes(NEWLINE).writeBytes(key.getBytes(UTF8.utf8)).writeBytes(PARAMETER_SEPARATOR).writeBytes(value));
        buf.writeBytes(NEWLINE).writeBytes(NEWLINE);
        buf.writeBytes("Hello world v2!".getBytes(UTF8.utf8));
        this.channel.writeAndFlush(buf);
        return this;
    }

    /**
     * Closes this response, flushing and closing the TCP channel if required
     */
    @Override
    public void close() {
        if (this.channel.isOpen()) {
            this.channel.flush().close();
        }
    }

    /**
     * Sets the response's content type
     *
     * @param contentType the content type
     * @return this response
     */
    public Response setContentType(@NonNull String contentType) {
        this.setParameter("Content-Type", contentType);
        return this;
    }
}
