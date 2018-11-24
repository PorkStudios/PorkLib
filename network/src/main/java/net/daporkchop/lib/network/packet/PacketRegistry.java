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
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.primitive.map.IntegerObjectMap;
import net.daporkchop.lib.primitive.map.ObjectIntegerMap;
import net.daporkchop.lib.primitive.map.hashmap.IntegerObjectHashMap;
import net.daporkchop.lib.primitive.map.hashmap.ObjectIntegerHashMap;

import java.util.Arrays;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author DaPorkchop_
 */
public class PacketRegistry {
    private final IntegerObjectMap<Codec<Packet, UserConnection>> registeredCodecs = new IntegerObjectHashMap<>();
    private final ObjectIntegerMap<Class<? extends Packet>> packetIds = new ObjectIntegerHashMap<>();
    private final Map<Class<? extends Packet>, Class<? extends UserProtocol>> supplyingProtocol = new IdentityHashMap<>();
    @Getter
    private final Collection<UserProtocol> protocols;

    @SuppressWarnings("unchecked")
    public PacketRegistry(@NonNull Collection<UserProtocol> protocols)    {
        this.protocols = protocols;
        AtomicInteger idCounter = new AtomicInteger(0);
        for (UserProtocol<UserConnection> protocol : protocols)  {
            if (protocol == null)   {
                throw new NullPointerException();
            }
            protocol.registered.forEach(codec -> {
                int id = idCounter.getAndIncrement();
                this.registeredCodecs.put(id, codec);
                Packet packet = codec.createInstance();
                if (packet == null) {
                    throw new NullPointerException();
                }
                this.packetIds.put(packet.getClass(), id);
                this.supplyingProtocol.put(packet.getClass(), protocol.getClass());
            });
        }
    }

    @SuppressWarnings("unchecked")
    public <C extends UserConnection> Codec<Packet, C> getCodec(int id)  {
        return (Codec<Packet, C>) this.registeredCodecs.get(id);
    }

    public int getId(@NonNull Class<? extends Packet> clazz)    {
        return this.packetIds.getOrDefault(clazz, -1);
    }

    public <C extends UserConnection> Codec<Packet, C> getCodec(@NonNull Class<? extends Packet> clazz)  {
        return this.getCodec(this.getId(clazz));
    }

    @SuppressWarnings("unchecked")
    public <C extends UserConnection> Class<? extends UserProtocol<C>> getOwningProtocol(@NonNull Class<? extends Packet> clazz)  {
        return (Class<? extends UserProtocol<C>>) this.supplyingProtocol.get(clazz);
    }
}
