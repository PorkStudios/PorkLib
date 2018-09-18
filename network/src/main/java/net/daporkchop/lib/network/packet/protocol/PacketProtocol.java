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

package net.daporkchop.lib.network.packet.protocol;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.network.conn.Session;
import net.daporkchop.lib.network.packet.Codec;
import net.daporkchop.lib.network.packet.Packet;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
@Getter
public abstract class PacketProtocol<S extends Session> {
    private final Map<Class, Codec<Packet, S>> classCodecMap = new IdentityHashMap<>();
    private final List<Class> classes = new ArrayList<>();
    private final List<Codec<Packet, S>> codecs = new ArrayList<>();

    private final String name;
    private final int version;

    public PacketProtocol(@NonNull String name, int version) {
        this.name = name;
        this.version = version;

        PacketRegistry registry = new PacketRegistry();
        this.registerPackets(registry);
        registry.codecs.forEach((clazz, codec) -> {
            PacketProtocol.this.classCodecMap.put(clazz, codec);
            PacketProtocol.this.classes.add(clazz);
            PacketProtocol.this.codecs.add(codec);
        });
    }

    protected abstract void registerPackets(@NonNull PacketRegistry registry);

    public abstract S newSession();

    public int getId(@NonNull Class clazz) {
        int id = this.classes.indexOf(clazz);
        if (id == -1) {
            throw new IllegalArgumentException(String.format("Invalid packet class: %s", clazz.getCanonicalName()));
        }
        return id;
    }

    public Packet newPacket(int id) {
        Codec<Packet, S> codec = this.codecs.get(id);
        if (codec == null) {
            throw new IllegalArgumentException(String.format("Invalid packet id: %d", id));
        }
        return codec.newPacket();
    }

    public void handle(@NonNull Packet packet, @NonNull S session) {
        Codec<Packet, S> codec = this.classCodecMap.get(packet.getClass());
        if (codec == null) {
            throw new IllegalArgumentException(String.format("Invalid packet class: %s", packet.getClass().getCanonicalName()));
        }
        codec.handle(packet, session);
    }

    public boolean isCompatible(@NonNull PacketProtocol protocol) {
        return this.isCompatible(protocol.name, protocol.version);
    }

    public boolean isCompatible(@NonNull String name, int version) {
        return name.equals(this.name) && version == this.version;
    }

    protected final class PacketRegistry {
        private final Map<Class, Codec<Packet, S>> codecs = new IdentityHashMap<>();

        public void register(@NonNull Codec<Packet, S>... codecs) {
            for (Codec<Packet, S> codec : codecs) {
                if (codec == null) {
                    throw new NullPointerException("supplier");
                }
                this.register(codec);
            }
        }

        public synchronized void register(@NonNull Codec<Packet, S> codec) {
            Packet packet = codec.newPacket();
            Class clazz = packet.getClass();

            if (this.codecs.containsKey(clazz)) {
                throw new IllegalStateException(String.format("Packet class %s already registered!", clazz.getCanonicalName()));
            }
            this.codecs.put(clazz, codec);
        }
    }
}
