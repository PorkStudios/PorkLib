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

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.packet.handler.MessageHandler;
import net.daporkchop.lib.network.packet.handler.PacketHandler;
import net.daporkchop.lib.primitive.map.ObjectIntegerMap;
import net.daporkchop.lib.primitive.map.ShortObjectMap;
import net.daporkchop.lib.primitive.map.array.ShortObjectArrayMap;
import net.daporkchop.lib.primitive.map.hashmap.ObjectIntegerHashMap;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author DaPorkchop_
 */
public class PacketRegistry implements Logging {
    private final ShortObjectMap<UserProtocol> idToProtocol = new ShortObjectArrayMap<>();
    private final ObjectIntegerMap<Class<?>> packetToProtocolId = new ObjectIntegerHashMap<>(); //TODO: identityHashMap
    @Getter
    private final Collection<UserProtocol> protocols;

    @SuppressWarnings("unchecked")
    public PacketRegistry(@NonNull Collection<UserProtocol> protocols) {
        if (protocols.size() > 0xFFFF - 1) {
            throw this.exception("Too many protocols: ${0}", protocols.size());
        }
        this.protocols = Collections.unmodifiableCollection(protocols);
        AtomicInteger idCounter = new AtomicInteger(1); //start at id 1 so that we can leave id 0 for raw channels
        for (UserProtocol<UserConnection> protocol : protocols) {
            if (protocol == null) {
                throw new NullPointerException();
            }
            short protocolId = (short) idCounter.getAndIncrement();
            this.idToProtocol.put(protocolId, protocol);
            protocol.registered.forEach((packetId, codec) -> {
                if (codec instanceof PacketHandler) {
                    this.packetToProtocolId.put(((PacketHandler) codec).getPacketClass(), combine(protocolId, packetId));
                }
            });
        }
    }

    public static short getProtocolId(int id) {
        return (short) ((id >>> 16) & 0xFFFF);
    }

    public static short getPacketId(int id) {
        return (short) (id & 0xFFFF);
    }

    public static int combine(short protocolId, short packetId) {
        return ((protocolId & 0xFFFF) << 16) | (packetId & 0xFFFF);
    }

    public <C extends UserConnection> UserProtocol<C> getProtocol(int id) {
        return this.getProtocol(getProtocolId(id));
    }

    @SuppressWarnings("unchecked")
    public <C extends UserConnection> UserProtocol<C> getProtocol(short protocolId) {
        UserProtocol<C> protocol = (UserProtocol<C>) this.idToProtocol.get(protocolId);
        if (protocol == null) {
            throw this.exception("Invalid protocol id: ${0}", protocolId & 0xFFFF);
        } else {
            return protocol;
        }
    }

    public MessageHandler getHandler(int id) {
        return this.getProtocol(id).getHandler(id);
    }

    public <P> int getId(@NonNull Class<P> clazz) {
        if (this.packetToProtocolId.containsKey(clazz)) {
            return this.packetToProtocolId.get(clazz);
        } else {
            throw this.exception("Unregistered packet: ${0}", clazz);
        }
    }

    public <P, C extends UserConnection> Class<? extends UserProtocol<C>> getOwningProtocol(@NonNull Class<P> clazz) {
        return this.getOwningProtocol(getProtocolId(this.getId(clazz)));
    }

    @SuppressWarnings("unchecked")
    public <C extends UserConnection> Class<? extends UserProtocol<C>> getOwningProtocol(short protocolId) {
        return (Class<? extends UserProtocol<C>>) this.getProtocol(protocolId).getClass();
    }
}
