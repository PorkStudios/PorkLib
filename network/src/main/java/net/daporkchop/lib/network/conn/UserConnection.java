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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.daporkchop.lib.common.function.Void;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.channel.Channel;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.util.reliability.Reliability;

import java.net.InetSocketAddress;

/**
 * Every protocol registered to a connection gets to create an instance of {@link UserConnection}. This can be used to store information
 * about e.g. the current connection state.
 *
 * @author DaPorkchop_
 */
public abstract class UserConnection implements Connection, Logging {
    @Setter(AccessLevel.PACKAGE)
    @Getter
    @NonNull
    private UnderlyingNetworkConnection protocolConnection;

    @Override
    public final <E extends Endpoint> E getEndpoint() {
        return this.protocolConnection.getEndpoint();
    }

    @Override
    public final void closeConnection(String reason) {
        this.protocolConnection.closeConnection(reason);
    }

    @Override
    public final boolean isConnected() {
        return this.protocolConnection.isConnected();
    }

    @Override
    public final InetSocketAddress getAddress() {
        return this.protocolConnection.getAddress();
    }

    @Override
    public final void send(@NonNull Object message, boolean blocking, Void callback) {
        this.protocolConnection.send(message, blocking, callback);
    }

    @Override
    public final Channel openChannel(@NonNull Reliability reliability) {
        return this.protocolConnection.openChannel(reliability);
    }

    @Override
    public final Channel getOpenChannel(int id) {
        return this.protocolConnection.getOpenChannel(id);
    }

    /**
     * Called when a connection is established.
     * <p>
     * This will be called after the connection is completely ready to go (i.e. handshake has been completed, cryptography
     * has been initialized, etc.)
     */
    public void onConnect() {
    }

    /**
     * Called when a connection is closed.
     * <p>
     * This can be called either when this side closes the connection, or after it was closed remotely.
     *
     * @param reason the reason message for disconnecting. can be null
     */
    public void onDisconnect(String reason) {
    }
}
