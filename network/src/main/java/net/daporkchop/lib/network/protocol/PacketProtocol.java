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

package net.daporkchop.lib.network.protocol;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.network.endpoint.AbstractSession;
import net.daporkchop.lib.network.protocol.encapsulated.EncapsulatedProtocol;
import net.daporkchop.lib.primitive.lambda.supplier.ObjectSupplier;
import org.apache.mina.core.session.IoSession;

import java.lang.reflect.Array;

/**
 * A protocol maps packet IDs to the packets themselves
 *
 * @author DaPorkchop_
 */
@Data
public abstract class PacketProtocol<T extends Packet, S extends AbstractSession> {
    /**
     * The protocol's version number
     */
    private final int version;

    /**
     * The name of the protocol
     */
    @NonNull
    private final String name;

    /**
     * The handler for connections using this protocol
     */
    @NonNull
    private final NetHandler<S> handler;

    /**
     * All packets that are registered to this protocol
     */
    @SuppressWarnings("unchecked")
    @Getter(AccessLevel.PRIVATE)
    private final ObjectSupplier<T>[] packets = (ObjectSupplier<T>[]) Array.newInstance(ObjectSupplier.class, 256);

    /**
     * All packet handlers for this protocol
     */
    @SuppressWarnings("unchecked")
    private final PacketHandler<T, S>[] handlers = (PacketHandler<T, S>[]) Array.newInstance(PacketHandler.class, 256);

    public PacketProtocol(int version, @NonNull String name) {
        this(version, name, new NetHandler.NoopHandler<>());
    }

    public PacketProtocol(int version, @NonNull String name, @NonNull NetHandler<S> handler) {
        if (version < 0) {
            throw new IllegalArgumentException("Protocol version may not be less than 0 (given: " + version + ")");
        } else if (name.isEmpty() && !(this instanceof EncapsulatedProtocol)) {
            throw new IllegalArgumentException("Protocol name may not be empty!");
        }
        this.version = version;
        this.name = name;
        this.handler = handler;
    }

    /**
     * Register a packet
     *
     * @param supplier a supplier for new instances of this packet
     */
    @SuppressWarnings("unchecked")
    protected <P extends T> void registerPacket(@NonNull ObjectSupplier<P> supplier, @NonNull PacketHandler<P, S> handler) {
        T packet = supplier.get();
        if (packet == null) {
            throw new NullPointerException("packet");
        }
        if (this.packets[packet.getId() & 0xFF] != null) {
            throw new IllegalStateException("Packet already registered at ID " + packet.getId());
        }
        this.packets[packet.getId() & 0xFF] = (ObjectSupplier<T>) supplier;
        this.handlers[packet.getId() & 0xFF] = (PacketHandler<T, S>) handler;
    }

    /**
     * Get a new packet instance for a given ID
     *
     * @param id the packet's ID
     * @return a new instance of a packet for the given ID
     */
    public T newPacket(byte id) {
        if (this.packets[id & 0xFF] == null) {
            throw new IllegalArgumentException("Invalid packet ID: " + id);
        }
        return this.packets[id & 0xFF].get();
    }

    /**
     * Handles a packet
     *
     * @param packet  the packet to handle
     * @param session the session the packet was received on
     * @throws ClassCastException if the packet object was not an instance of T or the session was not an instance of S
     */
    @SuppressWarnings("unchecked")
    public void handle(@NonNull T packet, @NonNull S session) {
        if (session.isServer()) {
            if (packet.getDirection() == PacketDirection.CLIENTBOUND) {
                throw new IllegalStateException(packet.getClass().getCanonicalName() + " cannot be sent to the server!");
            }
        } else if (packet.getDirection() == PacketDirection.SERVERBOUND) {
            throw new IllegalStateException(packet.getClass().getCanonicalName() + " cannot be sent to the client!");
        }
        this.handlers[packet.getId() & 0xFF].handle(packet, session);
    }

    /**
     * Gets a new session instance for this protocol
     *
     * @param session the backing network session
     * @param server  whether or not this method is being invoked on a server
     * @return a new instance of S
     */
    public abstract S newSession(@NonNull IoSession session, boolean server);
}
