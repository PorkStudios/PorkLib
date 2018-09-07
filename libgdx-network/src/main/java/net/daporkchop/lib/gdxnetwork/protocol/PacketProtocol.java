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

import com.badlogic.gdx.utils.ObjectMap;
import lombok.Data;
import lombok.NonNull;
import net.daporkchop.lib.gdxnetwork.packet.Packet;
import net.daporkchop.lib.gdxnetwork.session.Session;

import java.util.function.Supplier;

/**
 * @author DaPorkchop_
 */
@Data
public abstract class PacketProtocol {
    private final ObjectMap<Integer, RegisteredPacket<?>> registeredPackets = new ObjectMap<>();

    @NonNull
    private final String name;
    private final int version;

    protected synchronized <P extends Packet> void registerPacket(@NonNull Supplier<P> supplier, @NonNull IPacketHandler<P> handler) {
        Packet packet = supplier.get();
        Integer id = packet.getId();
        if (this.registeredPackets.containsKey(id)) {
            throw new IllegalArgumentException("Packet " + id + " already registered!");
        }
        this.registeredPackets.put(id, new RegisteredPacket<>(supplier, handler));
    }

    public Packet getPacket(int id) {
        RegisteredPacket registeredPacket = this.registeredPackets.get(id);
        return (Packet) registeredPacket.supplier.get();
    }

    @SuppressWarnings("unchecked")
    public void handle(@NonNull Packet packet, @NonNull Session session) {
        RegisteredPacket registeredPacket = this.registeredPackets.get(packet.getId());
        ((IPacketHandler<Packet>) registeredPacket.handler).handle(packet, session);
    }

    @Data
    public static final class RegisteredPacket<P extends Packet> {
        @NonNull
        private final Supplier<P> supplier;

        @NonNull
        private final IPacketHandler<P> handler;
    }
}
