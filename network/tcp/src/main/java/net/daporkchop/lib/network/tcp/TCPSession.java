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

package net.daporkchop.lib.network.tcp;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.NonNull;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.transport.NetSession;

import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * A wrapper on top of {@link NetSession} that defines some additional helper methods that may be used for configuring TCP sessions.
 *
 * @author DaPorkchop_
 */
public interface TCPSession<S extends AbstractUserSession<S>> extends NetSession<S> {
    /**
     * Enables SSL for this session on the server side.
     *
     * @param key          the key file
     * @param keyCertChain the certificate chain file
     * @return this session
     */
    default TCPSession<S> enableSSLServer(@NonNull File key, @NonNull File keyCertChain) {
        try {
            return this.enableSSLServer(SslContextBuilder.forServer(keyCertChain, key).build());
        } catch (SSLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Enables SSL for this session on the server side.
     *
     * @param key          the key data
     * @param keyCertChain the certificate chain data
     * @return this session
     */
    default TCPSession<S> enableSSLServer(@NonNull InputStream key, @NonNull InputStream keyCertChain) {
        try {
            return this.enableSSLServer(SslContextBuilder.forServer(keyCertChain, key).build());
        } catch (SSLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Enables SSL for this session on the server side.
     *
     * @param key          the key data
     * @param keyCertChain the certificate chain data
     * @return this session
     */
    default TCPSession<S> enableSSLServer(@NonNull PrivateKey key, @NonNull X509Certificate... keyCertChain) {
        try {
            return this.enableSSLServer(SslContextBuilder.forServer(key, keyCertChain).build());
        } catch (SSLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Enables SSL for this session on the server side.
     *
     * @param context the SSL context to be used
     * @return this session
     */
    TCPSession<S> enableSSLServer(@NonNull SslContext context);

    /**
     * Enables SSL for this session on the client side.
     *
     * @return this session
     */
    default TCPSession<S> enableSSLClient(@NonNull String host, int port) {
        try {
            return this.enableSSLClient(SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build(), host, port);
        } catch (SSLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Enables SSL for this session on the client side.
     *
     * @param context the SSL context to be used
     * @return this session
     */
    TCPSession<S> enableSSLClient(@NonNull SslContext context, @NonNull String host, int port);
}
