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

package net.daporkchop.lib.network.packet;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.network.channel.Channel;
import net.daporkchop.lib.network.conn.UserConnection;

import java.util.function.Supplier;

/**
 * @author DaPorkchop_
 */
public interface Codec<P extends Packet, C extends UserConnection> extends PacketHandler<P, C> {
    @Override
    void handle(P packet, Channel channel, C connection);

    /**
     * Create a new instance of this codec's packet
     *
     * @return a new, blank instance of this codec's packet
     */
    P createInstance();

    @RequiredArgsConstructor
    class SimpleCodec<P extends Packet, C extends UserConnection> implements Codec<P, C> {
        @NonNull
        private final PacketHandler<P, C> handler;

        @NonNull
        private final Supplier<P> packetSupplier;

        @Override
        public void handle(@NonNull P packet, @NonNull Channel channel, @NonNull C connection) {
            this.handler.handle(packet, channel, connection);
        }

        @Override
        public P createInstance() {
            return this.packetSupplier.get();
        }
    }
}
