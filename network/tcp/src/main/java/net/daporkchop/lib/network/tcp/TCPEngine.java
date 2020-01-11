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

package net.daporkchop.lib.network.tcp;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.cache.Cache;
import net.daporkchop.lib.common.cache.SoftCache;
import net.daporkchop.lib.network.endpoint.PClient;
import net.daporkchop.lib.network.endpoint.PServer;
import net.daporkchop.lib.network.endpoint.builder.ClientBuilder;
import net.daporkchop.lib.network.endpoint.builder.ServerBuilder;
import net.daporkchop.lib.network.netty.NettyEngine;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.tcp.endpoint.TCPClient;
import net.daporkchop.lib.network.tcp.endpoint.TCPServer;
import net.daporkchop.lib.network.tcp.frame.DefaultFramer;
import net.daporkchop.lib.network.tcp.frame.FramerFactory;
import net.daporkchop.lib.network.transport.TransportEngine;
import net.daporkchop.lib.network.util.reliability.Reliability;

import javax.net.ssl.SSLException;
import java.io.File;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;

/**
 * An implementation of {@link TransportEngine} for the TCP/IP transport protocol.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public final class TCPEngine extends NettyEngine {
    protected static final Collection<Reliability> RELIABILITIES = Collections.singleton(Reliability.RELIABLE_ORDERED);
    protected static final Cache<TCPEngine> DEFAULT_CACHE = Cache.soft(() -> builder().build());

    public static Builder builder() {
        return new Builder();
    }

    public static TCPEngine defaultInstance() {
        return DEFAULT_CACHE.get();
    }

    private final SslContext sslServerContext;
    private final SslContext sslClientContext;

    private final FramerFactory framerFactory;

    protected TCPEngine(@NonNull Builder builder) {
        super(builder);

        this.sslServerContext = builder.sslServerContext();
        this.sslClientContext = builder.sslClientContext();

        this.framerFactory = builder.framerFactory;
    }

    @Override
    public <S extends AbstractUserSession<S>> PClient<S> createClient(@NonNull ClientBuilder<S> builder) {
        return new TCPClient<>(builder);
    }

    @Override
    public <S extends AbstractUserSession<S>> PServer<S> createServer(@NonNull ServerBuilder<S> builder) {
        return new TCPServer<>(builder);
    }

    @Override
    public Collection<Reliability> supportedReliabilities() {
        return RELIABILITIES;
    }

    @Override
    public boolean isReliabilitySupported(@NonNull Reliability reliability) {
        return reliability == Reliability.RELIABLE_ORDERED;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    @Accessors(fluent = true, chain = true)
    public static final class Builder extends NettyEngine.Builder<Builder, TCPEngine> {
        protected static SelfSignedCertificate SELF_SIGNED_CERTIFICATE;

        protected SslContext sslServerContext;
        protected SslContext sslClientContext;

        /**
         * The {@link FramerFactory} to use.
         * <p>
         * If {@code null}, a factory that supplies instances of {@link DefaultFramer} will be used.
         */
        protected FramerFactory framerFactory;

        /**
         * Enables SSL on the server side.
         *
         * @param key          the key file
         * @param keyCertChain the certificate chain file
         * @return this builder
         */
        public Builder enableSSLServer(@NonNull File key, @NonNull File keyCertChain) {
            try {
                return this.enableSSLServer(SslContextBuilder.forServer(keyCertChain, key).build());
            } catch (SSLException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Enables SSL on the server side.
         *
         * @param key          the key data
         * @param keyCertChain the certificate chain data
         * @return this builder
         */
        public Builder enableSSLServer(@NonNull InputStream key, @NonNull InputStream keyCertChain) {
            try {
                return this.enableSSLServer(SslContextBuilder.forServer(keyCertChain, key).build());
            } catch (SSLException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Enables SSL on the server side.
         *
         * @param key          the key data
         * @param keyCertChain the certificate chain data
         * @return this builder
         */
        public Builder enableSSLServer(@NonNull PrivateKey key, @NonNull X509Certificate... keyCertChain) {
            try {
                return this.enableSSLServer(SslContextBuilder.forServer(key, keyCertChain).build());
            } catch (SSLException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Enables SSL on the server side using a self-signed certificate.
         *
         * @return this builder
         */
        public Builder enableSSLServerSelfSigned() {
            if (SELF_SIGNED_CERTIFICATE == null) {
                synchronized (Builder.class) {
                    if (SELF_SIGNED_CERTIFICATE == null) {
                        try {
                            SELF_SIGNED_CERTIFICATE = new SelfSignedCertificate();
                        } catch (CertificateException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            return this.enableSSLServerSelfSigned(SELF_SIGNED_CERTIFICATE);
        }

        /**
         * Enables SSL on the server side using a self-signed certificate.
         *
         * @param certificate the {@link SelfSignedCertificate} to use
         * @return this builder
         */
        public Builder enableSSLServerSelfSigned(@NonNull SelfSignedCertificate certificate) {
            return this.enableSSLServer(certificate.key(), certificate.cert());
        }

        /**
         * Enables SSL on the server side.
         *
         * @param context the SSL context to be used
         * @return this builder
         */
        public Builder enableSSLServer(SslContext context) {
            this.sslServerContext = context;
            return this;
        }

        /**
         * Enables SSL on the client side.
         *
         * @return this builder
         */
        public Builder enableSSLClient() {
            try {
                return this.enableSSLClient(SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).startTls(false).build());
            } catch (SSLException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Enables SSL on the client side.
         *
         * @param context the SSL context to be used
         * @return this builder
         */
        public Builder enableSSLClient(@NonNull SslContext context) {
            this.sslClientContext = context;
            return this;
        }

        public <S extends AbstractUserSession<S>> Builder framerFactory(@NonNull FramerFactory<S> framerFactory) {
            this.framerFactory = framerFactory;
            return this;
        }

        @Override
        protected void validate() {
            super.validate();

            if (this.framerFactory == null) {
                this.framerFactory = DefaultFramer::new;
            }
        }

        @Override
        protected TCPEngine doBuild() {
            return new TCPEngine(this);
        }
    }
}
