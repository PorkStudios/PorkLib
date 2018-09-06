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

package net.daporkchop.lib.minecraft.java;

import lombok.NonNull;
import net.daporkchop.lib.minecraft.api.PingData;
import net.daporkchop.lib.minecraft.api.exception.AlreadyClosedException;
import net.daporkchop.lib.minecraft.api.exception.AlreadyConnectedException;
import net.daporkchop.lib.minecraft.api.exception.IllegalProtocolException;
import net.daporkchop.lib.minecraft.common.BaseClient;
import net.daporkchop.lib.minecraft.data.Platform;
import net.daporkchop.lib.minecraft.data.Protocol;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

/**
 * @author DaPorkchop_
 */
public class JavaClient extends BaseClient {
    @NonNull
    private final IoConnector connector;

    public JavaClient(Protocol protocol) {
        super(protocol);
        if (protocol.getPlatform() != this.getPlatform()) {
            throw new IllegalProtocolException(protocol);
        }

        this.connector = new NioSocketConnector();

        //TODO: codecs (encryption, compression, decoding, etc.)
    }

    @Override
    public Platform getPlatform() {
        return Platform.JAVA;
    }

    @Override
    public boolean isRunning() {
        synchronized (this.connected) {
            return this.connected.get();
        }
    }

    @Override
    public void disconnect() {
        synchronized (this.connected) {
            if (!this.isRunning()) {
                throw new AlreadyClosedException();
            }
            this.connector.dispose(true);
            this.connected.set(false);
        }
    }

    @Override
    public void connect(InetSocketAddress address) {
        synchronized (this.connected) {
            if (this.isRunning()) {
                throw new AlreadyConnectedException();
            }
            this.connector.connect(address).awaitUninterruptibly();
        }
    }

    @Override
    public PingData ping(InetSocketAddress address) {
        throw new UnsupportedOperationException();
    }
}
