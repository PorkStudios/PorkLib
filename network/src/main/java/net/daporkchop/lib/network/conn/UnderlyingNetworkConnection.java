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

package net.daporkchop.lib.network.conn;

import lombok.NonNull;
import net.daporkchop.lib.common.function.Void;
import net.daporkchop.lib.network.channel.Channel;
import net.daporkchop.lib.network.packet.UserProtocol;
import net.daporkchop.lib.network.pork.PorkConnection;
import net.daporkchop.lib.network.pork.PorkProtocol;
import net.daporkchop.lib.network.util.reliability.Reliability;

import java.util.Map;

/**
 * @author DaPorkchop_
 */
public interface UnderlyingNetworkConnection extends Connection {
    Map<Class<? extends UserProtocol>, UserConnection> getConnections();

    /**
     * Closes the channel at network level, i.e. with no disconnect packet or whatever
     */
    void disconnectAtNetworkLevel();

    @SuppressWarnings("unchecked")
    default <C extends UserConnection> C getUserConnection(@NonNull Class<? extends UserProtocol<C>> protocolClass) {
        return (C) this.getConnections().get(protocolClass);
    }

    //<C extends UserConnection> void putUserConnection(@NonNull Class<UserProtocol<C>> clazz, @NonNull C connection);
    //xd screw good coding
    default void putUserConnection(@NonNull Class<? extends UserProtocol> clazz, @NonNull UserConnection connection) {
        this.getConnections().put(clazz, connection);
    }

    default void registerTheUnderlyingConnection() {
        PorkConnection porkConnection = this.getUserConnection(PorkProtocol.class);
        porkConnection.setRealConnection(this);
        this.getConnections().values().forEach(conn -> conn.setProtocolConnection(this));
    }

    @Override
    default void send(@NonNull Object packet, boolean blocking, Void callback) {
        this.getDefaultChannel().send(packet, blocking, callback);
    }

    Channel openChannel(Reliability reliability, int requestedId);
}
