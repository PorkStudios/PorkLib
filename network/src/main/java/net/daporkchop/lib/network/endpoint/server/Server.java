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

package net.daporkchop.lib.network.endpoint.server;

import lombok.NonNull;
import net.daporkchop.lib.common.function.Void;
import net.daporkchop.lib.network.EndpointType;
import net.daporkchop.lib.network.endpoint.Endpoint;
import net.daporkchop.lib.network.packet.Packet;

/**
 * @author DaPorkchop_
 */
public interface Server extends Endpoint {
    @Override
    default EndpointType getType() {
        return EndpointType.SERVER;
    }

    default void broadcast(@NonNull Packet... packets) {
        for (Packet packet : packets)   {
            this.broadcast(packet);
        }
    }

    default void broadcastBlocking(@NonNull Packet... packets) {
        for (Packet packet : packets)   {
            this.broadcastBlocking(packet);
        }
    }

    default void broadcast(@NonNull Packet packet)   {
        this.broadcast(packet, false, null);
    }

    default void broadcast(@NonNull Packet packet, Void callback)   {
        this.broadcast(packet, false, callback);
    }

    default void broadcastBlocking(@NonNull Packet packet)   {
        this.broadcast(packet, true, null);
    }

    default void broadcastBlocking(@NonNull Packet packet, Void callback)   {
        this.broadcast(packet, true, callback);
    }

    default void broadcast(@NonNull Packet packet, boolean blocking) {
        this.broadcast(packet, blocking, null);
    }

    void broadcast(@NonNull Packet packet, boolean blocking, Void callback);

    @Override
    default String getName() {
        return "Server";
    }
}
