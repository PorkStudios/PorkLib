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

package http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import lombok.NonNull;
import net.daporkchop.lib.binary.UTF8;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.session.encode.SendCallback;
import net.daporkchop.lib.network.util.PacketMetadata;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author DaPorkchop_
 */
public class HTTPSession extends AbstractUserSession<HTTPSession> {
    public Promise<String> promise = GlobalEventExecutor.INSTANCE.newPromise();
    public Map<String, String> headers = null;
    public int contentLength = -1;
    public ByteBuf body = null;

    @Override
    public void onReceive(@NonNull DataIn in, @NonNull PacketMetadata metadata) throws IOException {
        switch (metadata.protocolId())  {
            case 0: {
                if (this.headers != null)   {
                    throw new IllegalStateException("Headers already read!");
                } else {
                    this.headers = Arrays.stream(new String(in.readAllAvailableBytes(), UTF8.utf8).split("\r\n"))
                            .map(s -> s.split(": ", 2))
                            .filter(a -> a.length == 2)
                            .collect(Collectors.toMap(a -> a[0], a -> a[1]));
                    this.headers.forEach((k, v) -> this.logger().info("  %s: %s", k, v));
                    if (this.headers.containsKey("Content-length")) {
                        this.contentLength = Integer.parseInt(this.headers.get("Content-length"));
                        this.logger().info("Content-length: %d", this.contentLength);
                        this.body = PooledByteBufAllocator.DEFAULT.ioBuffer(this.contentLength);
                    } else {
                        this.body = PooledByteBufAllocator.DEFAULT.ioBuffer();
                    }
                }
            }
            break;
            case 1: {
                if (in.available() == 0)    {
                    this.logger().debug("Read EOF!");
                    this.closeAsync();
                    return;
                }
                for (int i = in.available() - 1; i >= 0; i--)   {
                    this.body.writeByte(in.read());
                }
                if (this.contentLength != -1)   {
                    if (this.body.readableBytes() > this.contentLength) {
                        throw new IllegalStateException("Read too many bytes!");
                    } else if (this.body.readableBytes() == this.contentLength) {
                        this.promise.trySuccess(this.body.toString(UTF8.utf8));
                        this.body.release();
                        this.closeAsync();
                    }
                }
            }
            break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void encodeMessage(@NonNull Object msg, @NonNull PacketMetadata metadata, @NonNull SendCallback callback) {
        if (msg instanceof String)  {
            callback.send(((String) msg).getBytes(UTF8.utf8), metadata);
        } else {
            throw new IllegalStateException(String.format("Cannot send packet: %s", PorkUtil.className(msg)));
        }
    }

    @Override
    public void onClosed() {
        if (this.headers == null)   {
            this.promise.tryFailure(new IllegalStateException("Closed without reading headers!"));
        } else if (this.contentLength == -1)    {
            this.promise.trySuccess(this.body.toString(UTF8.utf8));
            this.body.release();
        } else {
            this.promise.tryFailure(new IllegalStateException("Closed without reading all bytes!"));
        }
    }

    @Override
    public void onException(@NonNull Exception e) {
        this.promise.tryFailure(e);
        super.onException(e);
    }
}
