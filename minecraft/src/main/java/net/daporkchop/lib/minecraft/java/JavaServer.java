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
import net.daporkchop.lib.minecraft.api.exception.AlreadyClosedException;
import net.daporkchop.lib.minecraft.api.exception.BindException;
import net.daporkchop.lib.minecraft.common.BaseServer;
import net.daporkchop.lib.minecraft.data.Platform;
import net.daporkchop.lib.minecraft.data.Protocol;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author DaPorkchop_
 */
public class JavaServer extends BaseServer {
    @NonNull
    private final IoAcceptor acceptor;

    public JavaServer() {
        this(Integer.MAX_VALUE);
    }

    public JavaServer(int maxPlayers) {
        super(Protocol.getLatest(Platform.JAVA), maxPlayers);

        this.acceptor = new NioSocketAcceptor();
        //TODO: codecs (encryption, compression, decoding, etc.)
    }

    @Override
    public Platform getPlatform() {
        return Platform.JAVA;
    }

    @Override
    public void stop() {
        if (!this.isRunning()) {
            throw new AlreadyClosedException();
        }
        this.acceptor.setCloseOnDeactivation(true);
        this.acceptor.unbind();
        this.acceptor.dispose(true);
    }

    @Override
    public boolean isRunning() {
        return this.acceptor.isActive();
    }

    @Override
    public void bind(short port) {
        try {
            this.acceptor.bind(new InetSocketAddress(port & 0xFFFF));
        } catch (IOException e) {
            throw new BindException("Unable to bind to port " + (port & 0xFFFF), e);
        }
    }
}
