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

package net.daporkchop.lib.network.raknet.endpoint;

import com.nukkitx.network.raknet.RakNetClientSession;
import com.nukkitx.network.raknet.RakNetServer;
import com.nukkitx.network.raknet.RakNetServerListener;
import com.nukkitx.network.raknet.RakNetServerSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.network.endpoint.PServer;
import net.daporkchop.lib.network.endpoint.builder.ServerBuilder;
import net.daporkchop.lib.network.raknet.impl.PRakNetSession;
import net.daporkchop.lib.network.session.AbstractUserSession;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author DaPorkchop_
 */
public class PRakNetServer<S extends AbstractUserSession<S>> extends RakNetEndpoint<PServer<S>, S, RakNetServer> implements PServer<S> {
    protected final Set<S> sessions = new HashSet<>();

    public PRakNetServer(@NonNull ServerBuilder<S> builder) {
        super(builder);
        if (this.group != null) {
            this.rakNet = new RakNetServer(builder.bind(), PorkUtil.CPU_COUNT, this.group);
        } else {
            this.rakNet = new RakNetServer(builder.bind(), PorkUtil.CPU_COUNT);
        }

        Predicate<InetSocketAddress> connectionFilter = builder.connectionFilter();
        Function<InetSocketAddress, byte[]> pingHandler = builder.pingHandler();
        this.rakNet.setListener(new RakNetServerListener() {
            @Override
            public boolean onConnectionRequest(InetSocketAddress address) {
                return connectionFilter.test(address);
            }

            @Override
            public byte[] onQuery(InetSocketAddress address) {
                return pingHandler.apply(address);
            }

            @Override
            public void onSessionCreation(RakNetServerSession session) {
                PRakNetSession<S> realSession = new PRakNetSession<>(PRakNetServer.this, session);
                session.setListener(realSession);
                realSession.connectFuture().addListener(v -> PRakNetServer.this.sessions.add(realSession.userSession()));
                realSession.disconnectFuture().addListener(v -> PRakNetServer.this.sessions.remove(realSession.userSession()));
            }

            @Override
            public void onUnhandledDatagram(ChannelHandlerContext ctx, DatagramPacket packet) {
            }
        });
        this.rakNet.bind();
    }

    @Override
    public Collection<S> sessions() {
        return Collections.unmodifiableSet(this.sessions);
    }
}
