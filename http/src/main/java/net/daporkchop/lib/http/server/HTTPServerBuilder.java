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

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.util.PConstants;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.common.util.SystemInfo;
import net.daporkchop.lib.http.ResponseCode;
import net.daporkchop.lib.http.parameter.def.ParameterHost;
import net.daporkchop.lib.http.server.handler.RequestHandler;

import javax.net.ssl.SSLException;
import java.io.File;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

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
        } catch (Exception e) {
            defaultGroup = new NioEventLoopGroup(0, PorkUtil.DEFAULT_EXECUTOR);
        } finally {
            DEFAULT_GROUP = defaultGroup;
        }
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
     * <p>
     * If this function returns null, SSL will not be enabled.
     */
    @NonNull
    private Function<Channel, SslHandler> sslHandlerSupplier = channel -> null;
    /**
     * The {@link RequestHandler} that will be used if a request cannot find a matching handler.
     * <p>
     * By default, responds with a simple 404 message similar to that sent by Apache.
     */
    @NonNull
    private RequestHandler defaultHandler = (request, response) -> response.writeSimple404(request);

    /**
     * Creates a new {@link HTTPServerBuilder} using the given port
     *
     * @param port the port to listen on
     * @return a new {@link HTTPServerBuilder}
     */
    public static HTTPServerBuilder of(int port) {
        if (port < 0 || port > 0xFFFF) {
            throw new IllegalArgumentException("port must be in range 0-65535!");
        } else {
            return of(new InetSocketAddress("0.0.0.0", port));
        }
    }

    /**
     * Creates a new {@link HTTPServerBuilder} using the given address
     *
     * @param address the address to listen on
     * @return a new {@link HTTPServerBuilder}
     */
    public static HTTPServerBuilder of(@NonNull InetSocketAddress address) {
        return new HTTPServerBuilder().setListenAddress(address);
    }

    /**
     * Enables SSL using a given key and certificate
     *
     * @param key  the key to use
     * @param cert the certificate to use
     * @return this builder
     */
    public HTTPServerBuilder enableSSL(@NonNull File key, @NonNull File cert) {
        try {
            SslContext context = SslContextBuilder.forServer(cert, key).build();
            this.sslHandlerSupplier = channel -> new SslHandler(context.newEngine(channel.alloc()));
            return this;
        } catch (SSLException e) {
            throw PConstants.p_exception(e);
        }
    }

    /**
     * Enables SSL using a self-signed certificate
     *
     * @return this builder
     * @see #enableSSL(File, File)
     */
    public HTTPServerBuilder enableSSL() {
        try {
            long range = TimeUnit.DAYS.toMillis(365L << 1L);
            long time = System.currentTimeMillis();
            SelfSignedCertificate certificate = new SelfSignedCertificate(
                    new Date(time - range), new Date(time + range)
            );
            return this.enableSSL(certificate.privateKey(), certificate.certificate());
        } catch (CertificateException e) {
            throw PConstants.p_exception(e);
        }
    }

    /**
     * Sets this server's executor group
     *
     * @param executor the new {@link Executor} to use
     * @return this builder
     * @see #setGroup(EventLoopGroup)
     */
    public HTTPServerBuilder setExecutor(@NonNull Executor executor) {
        this.group = new NioEventLoopGroup(0, executor);
        return this;
    }

    /**
     * Builds a new {@link HTTPServer} instance using this builder's settings
     *
     * @return a new instance of {@link HTTPServer}
     */
    public HTTPServer build() {
        if (this.listenAddress == null) {
            throw new IllegalStateException("listen address must be set!");
        }
        return new HTTPServer(this);
    }
}
