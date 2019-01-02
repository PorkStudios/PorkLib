/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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

import com.zaxxer.sparsebits.SparseBitSet;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.conn.UserConnection;
import net.daporkchop.lib.network.packet.handler.MessageHandler;
import net.daporkchop.lib.network.packet.handler.PacketHandler;
import net.daporkchop.lib.network.pork.PorkProtocol;
import net.daporkchop.lib.network.util.Version;
import net.daporkchop.lib.primitive.map.ObjectShortMap;
import net.daporkchop.lib.primitive.map.ShortObjectMap;
import net.daporkchop.lib.primitive.map.array.ShortObjectArrayMap;
import net.daporkchop.lib.primitive.map.hashmap.ObjectShortHashMap;

/**
 * A {@link UserProtocol} defines a mapping of packet IDs to packet handlers, and can be used to contain per-connection data
 * via a {@link UserConnection}.
 *
 * @author DaPorkchop_
 */
public abstract class UserProtocol<C extends UserConnection> implements Logging {
    final ShortObjectMap<MessageHandler> registered = new ShortObjectArrayMap<>();
    final ObjectShortMap<Class<?>> packets = new ObjectShortHashMap<>();

    @Getter
    private final String name;
    @Getter
    private final int version;
    @Getter
    private final int requestedId;
    private SparseBitSet ids;

    public UserProtocol(@NonNull String name, int version) {
        this(name, version, -1);
    }

    public UserProtocol(@NonNull String name, int version, int requestedId) {
        if (requestedId > 0xFFFF || requestedId < -1) {
            throw new IllegalArgumentException("Requested ID must be in range 0-65535, or -1 to ignore!");
        } else if (requestedId == 0 && !(this instanceof PorkProtocol)) {
            throw new IllegalArgumentException("Protocol ID 0 is reserved for PorkLib network!");
        } else {
            this.name = name;
            this.version = version;
            this.requestedId = requestedId;

            this.ids = new SparseBitSet();
            this.registerPackets();
            this.ids = null;
        }
    }

    public boolean isCompatible(@NonNull UserProtocol<C> protocol) {
        return this.isCompatible(protocol.name, protocol.version);
    }

    public boolean isCompatible(@NonNull String name, int version) {
        return this.name.equals(name) && this.version == version;
    }

    protected abstract void registerPackets();

    protected void register(@NonNull MessageHandler handler) {
        if (this.ids == null) {
            throw new IllegalStateException("Protocol has already been populated!");
        } else {
            synchronized (this.registered) {
                int id = this.ids.nextClearBit(0);
                if (id > 0xFFFF) {
                    throw new IllegalStateException("Too many packets registered!");
                } else {
                    this.ids.set(id);
                    this.registered.put((short) id, handler);
                    if (handler instanceof PacketHandler) {
                        this.packets.put(((PacketHandler) handler).getPacketClass(), (short) id);
                    }
                }
            }
        }
    }

    protected void register(@NonNull MessageHandler handler, int id) {
        if (id < 0 || id > 0xFFFF) {
            throw this.exception("Id must be in range 0-65535, but found: ${0}!", id);
        } else if (this.ids == null) {
            throw new IllegalStateException("Protocol has already been populated!");
        } else {
            synchronized (this.registered) {
                if (this.ids.get(id)) {
                    throw this.exception("Packet id ${0} already taken!", id);
                } else {
                    this.ids.set(id);
                    this.registered.put((short) id, handler);
                    if (handler instanceof PacketHandler) {
                        this.packets.put(((PacketHandler) handler).getPacketClass(), (short) id);
                    }
                }
            }
        }
    }

    protected void register(@NonNull MessageHandler... handlers) {
        for (MessageHandler handler : handlers) {
            this.register(handler);
        }
    }

    public abstract C newConnection();

    public MessageHandler getHandler(int id) {
        return this.getHandler(PacketRegistry.getPacketId(id));
    }

    public MessageHandler getHandler(short packetId) {
        MessageHandler handler = this.registered.get(packetId);
        if (handler == null) {
            throw this.exception("Invalid packet id: ${0}", packetId & 0xFFFF);
        } else {
            return handler;
        }
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() * 31 + this.version;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof UserProtocol) {
            UserProtocol other = (UserProtocol) obj;
            return this.name.equals(other.name) && this.version == other.version;
        } else if (obj instanceof Version) {
            Version other = (Version) obj;
            return this.name.equals(other.getName()) && this.version == other.getVersion();
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return this.format("${0} v${1}", this.name, this.version);
    }
}
