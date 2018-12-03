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

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author DaPorkchop_
 */
public abstract class UserProtocol<C extends UserConnection> {
    final List<Codec<Packet, C>> registered = new LinkedList<>();

    @Getter
    private final String name;

    @Getter
    private final int version;

    public UserProtocol(@NonNull String name, int version) {
        this.name = name;
        this.version = version;

        this.registerPackets();
    }

    public boolean isCompatible(@NonNull UserProtocol<C> protocol) {
        return this.isCompatible(protocol.name, protocol.version);
    }

    public boolean isCompatible(@NonNull String name, int version) {
        return this.name.equals(name) && this.version == version;
    }

    protected abstract void registerPackets();

    @SuppressWarnings("unchecked")
    protected <P extends Packet> void register(@NonNull Codec<P, C> codec) {
        synchronized (this.registered) {
            this.registered.add((Codec<Packet, C>) codec);
        }
    }

    protected <P extends Packet> void register(@NonNull Codec<P, C>... codecs) {
        for (Codec<P, C> codec : codecs) {
            this.register(codec);
        }
    }

    protected <P extends Packet> void register(@NonNull PacketHandler<P, C> handler, @NonNull Supplier<P> supplier) {
        this.register(new Codec.SimpleCodec<>(handler, supplier));
    }

    public abstract C newConnection();
}
