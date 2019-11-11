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

package net.daporkchop.lib.http.impl.java.client.old.builder;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.http.client.builder.AbstractRequestBuilder;
import net.daporkchop.lib.http.client.builder.RequestBuilder;
import net.daporkchop.lib.http.impl.java.client.JavaHttpClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Basic implementation of {@link RequestBuilder} for {@link JavaHttpClient}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Accessors(fluent = true)
public abstract class JavaRequestBuilder<I extends RequestBuilder<I>> extends AbstractRequestBuilder<I> {
    @Getter
    @NonNull
    protected final JavaHttpClient client;

    protected synchronized HttpURLConnection toConnection() throws IOException {
        this.assertConfigured();

        URL url;
        try {
            String protocol = this.https ? "https" : "http";
            if (this.address == null) {
                url = new URL(protocol, this.host, this.port, this.path);
            } else {
                url = new URL(protocol, ((InetSocketAddress) this.address).getHostString(), ((InetSocketAddress) this.address).getPort(), this.path);
            }
        } catch (MalformedURLException e)   {
            throw new RuntimeException(e);
        }
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(this.method.name());
        //TODO: allow configuration of headers
        return connection;
    }
}
