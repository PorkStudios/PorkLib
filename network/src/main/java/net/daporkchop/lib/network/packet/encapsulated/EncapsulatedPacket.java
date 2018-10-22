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

package net.daporkchop.lib.network.packet.encapsulated;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.network.conn.Session;
import net.daporkchop.lib.network.packet.Codec;
import net.daporkchop.lib.network.packet.Packet;
import net.daporkchop.lib.network.packet.protocol.PacketProtocol;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author DaPorkchop_
 */
public interface EncapsulatedPacket extends Packet {
    int ENCAPSULATED_VERSION = 2;

    PacketProtocol PROTOCOL = new EncapsulatedPacketProtocol(false);
    PacketProtocol P2P_PROTOCOL = new EncapsulatedPacketProtocol(true);

    EncapsulatedType getType();
}

class EncapsulatedPacketProtocol extends PacketProtocol {
    private final boolean p2p;

    public EncapsulatedPacketProtocol(boolean p2p) {
        super(p2p ? "PorkLib Network - p2p mode" : "PorkLib Network", EncapsulatedPacket.ENCAPSULATED_VERSION);

        this.p2p = p2p;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void registerPackets(PacketRegistry registry) {
        Arrays.stream(EncapsulatedType.values())
                .filter(this.p2p ? a -> true : ((Predicate<EncapsulatedType>) EncapsulatedType::isP2pOnly).negate())
                .map(EncapsulatedType::getSupplier)
                .collect(Collectors.toSet())
                .forEach(supplier -> registry.register(new NonhandlingCodec<>(supplier)));
    }

    @Override
    public Session newSession() {
        return null;
    }

    @AllArgsConstructor
    private static class NonhandlingCodec<P extends EncapsulatedPacket> implements Codec<P, Session> {
        @NonNull
        private final Supplier<P> supplier;

        @Override
        public void handle(P packet, Session session) {
        }

        @Override
        public P newPacket() {
            return this.supplier.get();
        }
    }
}
