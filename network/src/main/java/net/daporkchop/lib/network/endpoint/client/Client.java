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

package net.daporkchop.lib.network.endpoint.client;

import lombok.NonNull;
import net.daporkchop.lib.network.EndpointType;
import net.daporkchop.lib.network.channel.Channel;
import net.daporkchop.lib.network.conn.Connection;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.packet.UserProtocol;
import net.daporkchop.lib.network.pork.PorkProtocol;
import net.daporkchop.lib.network.util.reliability.Reliability;

import java.util.stream.Stream;

/**
 * Represents a network client
 *
 * @author DaPorkchop_
 */
public interface Client extends Endpoint, Connection {
    @Override
    default void close(String reason) {
        this.closeConnection(reason);
    }

    @Override
    default boolean isConnected() {
        return this.isRunning();
    }

    @Override
    default EndpointType getType() {
        return EndpointType.CLIENT;
    }

    @Override
    default <C extends UserConnection> Stream<C> getConnections(@NonNull Class<? extends UserProtocol<C>> protocolClass) {
        return Stream.<C>builder().add(this.getConnection(protocolClass)).build();
    }

    <C extends UserConnection> C getConnection(@NonNull Class<? extends UserProtocol<C>> protocolClass);

    @Override
    default String getName() {
        return "Client";
    }

    @Override
    @SuppressWarnings("unchecked")
    default <E extends Endpoint> E getEndpoint() {
        return (E) this;
    }

    @Override
    default Channel openChannel(@NonNull Reliability reliability) {
        return this.getConnection(PorkProtocol.class).openChannel(reliability);
    }

    @Override
    default Channel getOpenChannel(int id) {
        return this.getConnection(PorkProtocol.class).getOpenChannel(id);
    }

    @Override
    default Channel getDefaultChannel() {
        return this.getConnection(PorkProtocol.class).getDefaultChannel();
    }

    @Override
    default Channel getControlChannel() {
        return this.getConnection(PorkProtocol.class).getControlChannel();
    }

    @Override
    default boolean isClient() {
        return true;
    }

    @Override
    default boolean isServer() {
        return false;
    }
}
