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
import lombok.NonNull;
import lombok.Setter;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.packet.Packet;

import java.net.InetSocketAddress;

/**
 * @author DaPorkchop_
 */
@Setter(AccessLevel.PACKAGE)
public abstract class Session {
    @NonNull
    private PorkConnection porkConnection;

    public void send(@NonNull Packet packet) {
        this.porkConnection.send(packet);
    }

    public int getPing() {
        return this.porkConnection.getPing();
    }

    public boolean isConnected() {
        return this.porkConnection.isConnected();
    }

    @SuppressWarnings("unchecked")
    public <E extends Endpoint> E getEndpoint() {
        return (E) this.porkConnection.getEndpoint();
    }

    public InetSocketAddress getRemoteAddress() {
        return this.porkConnection.getNetConnection().getRemoteAddressTCP();
    }
}
