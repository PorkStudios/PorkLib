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
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import lombok.AccessLevel;
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

import java.nio.ByteBuffer;
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
    @Setter(AccessLevel.PRIVATE)
    private boolean sent = false;

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
        if (this.sent) {
            throw new IllegalStateException("Already sent request!");
        } else {
            ByteBuf buf = this.channel.alloc().ioBuffer();
            buf.writeBytes(String.format("%s %d OK", HTTPVersion.V1_1.getIdentifierName(), this.status.getCode()).getBytes(UTF8.utf8));
            this.parameters.forEach((key, value) -> buf.writeBytes(NEWLINE).writeBytes(key.getBytes(UTF8.utf8)).writeBytes(PARAMETER_SEPARATOR).writeBytes(value));
            buf.writeBytes(NEWLINE).writeBytes(NEWLINE);
            //buf.writeBytes("Hello world v2!".getBytes(UTF8.utf8));
            this.channel.write(buf);
            this.sent = true;
            return this;
        }
    }

    /**
     * Closes this response, flushing and closing the TCP channel if required
     */
    @Override
    public void close() {
        if (!this.sent) {
            this.send();
        }
        if (this.channel.isOpen()) {
            this.channel.flush().close();
            this.parameters.clear();
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

    /**
     * Writes raw bytes to this connection as part of the message body.
     * <p>
     * May not be called until the {@link #send()} has been called.
     *
     * @param buf the data to write
     * @return this response
     */
    public Response write(@NonNull ByteBuf buf) {
        if (this.sent) {
            if (buf.readableBytes() > 0) {
                this.channel.write(buf);
            }
        } else {
            throw new IllegalStateException("send() not called before writing data!");
        }
        return this;
    }

    /**
     * Writes raw bytes to this connection as part of the message body.
     * <p>
     * May not be called until the {@link #send()} has been called.
     *
     * @param b the data to write
     * @return this response
     */
    public Response write(@NonNull byte[] b) {
        return this.write(Unpooled.wrappedBuffer(b));
    }

    /**
     * Writes raw bytes to this connection as part of the message body.
     * <p>
     * May not be called until the {@link #send()} has been called.
     *
     * @param buf the data to write
     * @return this response
     */
    public Response write(@NonNull ByteBuffer buf) {
        return this.write(Unpooled.wrappedBuffer(buf));
    }

    /**
     * Writes a single byte to this connection as part of the message body.
     * <p>
     * May not be called until the {@link #send()} has been called.
     *
     * @param b the byte to write
     * @return this response
     */
    public Response write(byte b) {
        return this.write(this.channel.alloc().buffer(1).writeByte(b & 0xFF));
    }

    /**
     * Writes a single short to this connection as part of the message body.
     * <p>
     * May not be called until the {@link #send()} has been called.
     *
     * @param s the short to write
     * @return this response
     */
    public Response write(short s) {
        return this.write(this.channel.alloc().buffer(2).writeShort(s & 0xFFFF));
    }

    /**
     * Writes a single int to this connection as part of the message body.
     * <p>
     * May not be called until the {@link #send()} has been called.
     *
     * @param i the int to write
     * @return this response
     */
    public Response write(int i) {
        return this.write(this.channel.alloc().buffer(4).writeInt(i));
    }

    /**
     * Writes a single long to this connection as part of the message body.
     * <p>
     * May not be called until the {@link #send()} has been called.
     *
     * @param l the long to write
     * @return this response
     */
    public Response write(long l) {
        return this.write(this.channel.alloc().buffer(8).writeLong(l));
    }

    /**
     * Writes a single float to this connection as part of the message body.
     * <p>
     * May not be called until the {@link #send()} has been called.
     *
     * @param f the float to write
     * @return this response
     */
    public Response write(float f) {
        return this.write(this.channel.alloc().buffer(4).writeFloat(f));
    }

    /**
     * Writes a single double to this connection as part of the message body.
     * <p>
     * May not be called until the {@link #send()} has been called.
     *
     * @param d the double to write
     * @return this response
     */
    public Response write(double d) {
        return this.write(this.channel.alloc().buffer(8).writeDouble(d));
    }

    /**
     * Writes a UTF-8 encoded string to this connection as part of the message body.
     * <p>
     * May not be called until the {@link #send()} has been called.
     *
     * @param s the {@link String} to write
     * @return this response
     */
    public Response write(@NonNull String s) {
        return this.write(s.getBytes(UTF8.utf8));
    }
}
