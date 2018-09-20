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

import com.esotericsoftware.kryonet.Connection;
import lombok.NonNull;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.packet.Packet;
import net.daporkchop.lib.network.util.PacketReprocessor;

/**
 * @author DaPorkchop_
 */
public interface PorkConnection {
    <S extends Session> S getSession();

    default void setSession(@NonNull Session session)   {
        session.setPorkConnection(this);
    }

    Connection getNetConnection();

    PacketReprocessor getPacketReprocessor();

    String getDisconnectReason();

    void setDisconnectReason(String reason);

    ConnectionState getState();

    void setState(@NonNull ConnectionState state);

    Endpoint getEndpoint();

    default boolean canSetState(@NonNull ConnectionState state)   {
        ConnectionState current = this.getState();
        return current.ordinal() < state.ordinal();
    }

    default void incrementState()   {
        ConnectionState current = this.getState();
        ConnectionState[] values = ConnectionState.values();
        if (current.ordinal() + 1 >= values.length) {
            throw new IllegalStateException("Already on final state!");
        }
        this.setState(values[current.ordinal() + 1]);
    }

    default void send(@NonNull Packet packet)   {
        this.getNetConnection().sendTCP(packet);
    }

    default int getPing()   {
        return this.getNetConnection().getReturnTripTime() >> 1;
    }

    default boolean isConnected()   {
        return this.getNetConnection().isConnected();
    }
}
