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

package net.daporkchop.lib.gdxnetwork.protocol;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.gdxnetwork.endpoint.Endpoint;
import net.daporkchop.lib.gdxnetwork.packet.Packet;
import net.daporkchop.lib.gdxnetwork.session.Session;

import java.util.Hashtable;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author DaPorkchop_
 */
@Data
public abstract class PacketProtocol<S extends Session> {
    @Getter(AccessLevel.PRIVATE)
    private final Map<Integer, RegisteredPacket<?>> registeredPackets = new Hashtable<>();

    @NonNull
    private final String name;
    private final int version;

    protected synchronized <P extends Packet> void registerPacket(@NonNull Supplier<P> supplier, @NonNull IPacketHandler<P, S> handler)   {
        Packet packet = supplier.get();
        Integer id = packet.getId();
        if (this.registeredPackets.containsKey(id)) {
            throw new IllegalArgumentException(String.format("Packet %d already registered!", id));
        }
        this.registeredPackets.put(id, new RegisteredPacket<>(supplier, handler));
    }

    public abstract S newSession(@NonNull Endpoint endpoint);

    @SuppressWarnings("unchecked")
    public void handle(int id, @NonNull DataIn in, @NonNull S session)   {
        RegisteredPacket registeredPacket = this.registeredPackets.get(id);
        Packet packet = (Packet) registeredPacket.supplier.get();
        packet.decode(in);
        ((IPacketHandler<Packet, S>) registeredPacket.handler).handle(packet, session);
    }

    public byte[] encode(@NonNull Packet packet)    {
        RegisteredPacket registeredPacket = this.registeredPackets.get(packet.getId());
    }

    @Data
    private final class RegisteredPacket<P extends Packet>  {
        @NonNull
        private final Supplier<P> supplier;

        @NonNull
        private final IPacketHandler<P, S> handler;
    }
}
