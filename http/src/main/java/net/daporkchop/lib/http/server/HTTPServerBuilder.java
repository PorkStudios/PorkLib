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

package net.daporkchop.lib.http.server;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.ssl.SslHandler;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.util.PorkUtil;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * A builder for {@link HTTPServer}
 *
 * @author DaPorkchop_
 */
@Getter
@Setter
@Accessors(chain = true)
public class HTTPServerBuilder {
    public static final EventLoopGroup DEFAULT_GROUP;

    static {
        EventLoopGroup defaultGroup = null;
        try {
            Class clazz = Class.forName("net.daporkchop.lib.network.endpoint.builder.AbstractBuilder");
            Field field = clazz.getDeclaredField("DEFAULT_GROUP");
            field.setAccessible(true);
            defaultGroup = (EventLoopGroup) field.get(null);
        } catch (Exception e)   {
            defaultGroup = new NioEventLoopGroup(0, PorkUtil.DEFAULT_EXECUTOR);
        } finally {
            DEFAULT_GROUP = defaultGroup;
        }
    }

    /**
     * Creates a new {@link HTTPServerBuilder} using the given port
     * @param port the port to listen on
     * @return a new {@link HTTPServerBuilder}
     */
    public static HTTPServerBuilder of(int port)    {
        if (port < 0 || port > 0xFFFF)  {
            throw new IllegalArgumentException("port must be in range 0-65535!");
        } else {
            return of(new InetSocketAddress("0.0.0.0", port));
        }
    }

    /**
     * Creates a new {@link HTTPServerBuilder} using the given address
     * @param address the address to listen on
     * @return a new {@link HTTPServerBuilder}
     */
    public static HTTPServerBuilder of(@NonNull InetSocketAddress address)  {
        return new HTTPServerBuilder().setListenAddress(address);
    }

    /**
     * The address to listen for connections on
     */
    @NonNull
    private InetSocketAddress listenAddress;

    /**
     * The {@link EventLoopGroup} to use for parallel tasks
     */
    @NonNull
    private EventLoopGroup group = DEFAULT_GROUP;

    /**
     * Creates instances of {@link SslHandler} to enable ssl on the connection.
     *
     * If this function returns null, SSL will not be enabled.
     */
    @NonNull
    private Supplier<SslHandler> sslHandlerSupplier = () -> null;

    public HTTPServerBuilder setGroup(@NonNull Executor executor)   {
        this.group = new NioEventLoopGroup(0, executor);
        return this;
    }

    public HTTPServer build()   {
        if (this.listenAddress == null) {
            throw new IllegalStateException("listen address must be set!");
        }
        return new HTTPServer(this);
    }
}
