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

package net.daporkchop.lib.http.client.factory;

import lombok.NonNull;
import net.daporkchop.lib.http.RequestMethod;

import java.net.SocketAddress;

/**
 * Basic implementation of {@link RequestFactory} which implements some of the most common methods.
 *
 * @author DaPorkchop_
 */
public abstract class AbstractRequestFactory<S extends RequestSettings> implements RequestFactory {
    protected S settings = this.newSettings();

    @Override
    public synchronized RequestFactory host(@NonNull String host) {
        this.settings.host = host;
        this.settings.address = null;
        return this;
    }

    @Override
    public synchronized RequestFactory port(int port) {
        if (port <= 0 || port > 65535) throw new IllegalArgumentException(String.format("Port number out of bounds: %d", port));

        this.settings.port = port;
        this.settings.address = null;
        return this;
    }

    @Override
    public synchronized RequestFactory address(@NonNull SocketAddress address) {
        this.settings.address = address;
        this.settings.host = null;
        this.settings.port = 0;
        return this;
    }

    @Override
    public synchronized RequestFactory localAddress(SocketAddress localAddress) {
        this.settings.localAddress = localAddress;
        return this;
    }

    @Override
    public synchronized RequestFactory path(@NonNull String path) {
        this.settings.path = path;
        return this;
    }

    @Override
    public synchronized RequestFactory method(@NonNull RequestMethod method) {
        if (!this.isSupported(method)) throw new IllegalArgumentException(String.format("Request method not supported: %s", method));

        this.settings.method = method;
        return this;
    }

    @Override
    public synchronized RequestFactory https(boolean https) {
        this.settings.https = https;
        return this;
    }

    @Override
    public synchronized RequestFactory reset() {
        this.settings = this.newSettings();

        return this;
    }

    protected boolean isSupported(@NonNull RequestMethod method) {
        return true;
    }

    protected abstract S newSettings();
}
