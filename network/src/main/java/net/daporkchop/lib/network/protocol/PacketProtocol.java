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

package net.daporkchop.lib.network.protocol;

import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.pipeline.Pipeline;
import net.daporkchop.lib.network.protocol.packet.InboundPacket;
import net.daporkchop.lib.network.protocol.packet.OutboundPacket;
import net.daporkchop.lib.network.protocol.packet.Packet;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author DaPorkchop_
 */
public abstract class PacketProtocol<S extends AbstractUserSession<S>> implements SimpleDataProtocol<S>, SimpleHandlingProtocol<S> {
    protected final IntObjectMap<Class<? extends InboundPacket<S>>> inbound;
    protected final Map<Class<? extends OutboundPacket<S>>, Integer> outbound;
    protected final Map<Class<?>, Supplier<?>> classToSupplier;

    public PacketProtocol() {
        Registerer registerer = new Registerer();
        this.registerPackets(registerer);
        this.inbound = registerer.inbound;
        this.outbound = registerer.outbound;
        this.classToSupplier = registerer.classToSupplier;
    }

    protected abstract void registerPackets(@NonNull Registerer registerer);

    @Override
    public void initPipeline(@NonNull Pipeline<S> pipeline, @NonNull S session) {
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object decode(@NonNull S session, @NonNull DataIn in, int channel) throws IOException {
        Logging.logger.debug("Packet size: %d bytes", in.available());
        int id = in.readVarInt();
        Logging.logger.debug("Packet id: %d", id);
        Class<? extends InboundPacket<S>> clazz = this.inbound.get(id);
        Supplier<?> supplier = this.classToSupplier.get(clazz);

        InboundPacket<S> packet = supplier == null ? PUnsafe.allocateInstance(clazz) : (InboundPacket<S>) supplier.get();
        packet.decode(in, session);
        return packet;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void encode(@NonNull DataOut out, @NonNull S session, @NonNull Object msg, int channel) throws IOException {
        if (!(msg instanceof OutboundPacket)) {
            throw new IllegalArgumentException(msg.getClass().getCanonicalName());
        }
        Integer id = this.outbound.get(msg.getClass());
        if (id == null) {
            throw new IllegalArgumentException(String.format("Unregistered packet: %s", msg.getClass().getCanonicalName()));
        } else {
            out.writeVarInt(id);
            ((OutboundPacket<S>) msg).encode(out, session);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onReceived(@NonNull S session, @NonNull Object msg, int channel) {
        if (msg instanceof InboundPacket)  {
            ((InboundPacket<S>) msg).handle(session);
        } else {
            throw new IllegalArgumentException(msg.getClass().getCanonicalName());
        }
    }

    @Override
    public void onBinary(@NonNull S session, @NonNull DataIn in, int channel) throws IOException {
        throw new IllegalStateException();
    }

    protected final class Registerer    {
        protected final IntObjectMap<Class<? extends InboundPacket<S>>> inbound = new IntObjectHashMap<>();
        protected final Map<Class<? extends OutboundPacket<S>>, Integer> outbound = new IdentityHashMap<>();
        protected final Map<Class<?>, Supplier<?>> classToSupplier = new IdentityHashMap<>();

        public <P extends OutboundPacket<S>> Registerer outbound(int id, @NonNull Class<P> clazz) {
            if (this.outbound.putIfAbsent(clazz, id) != null)  {
                throw new IllegalStateException(String.format("Packet class \"%s\" already registered!", clazz.getCanonicalName()));
            }
            return this;
        }

        public <P extends OutboundPacket<S>> Registerer outbound(int id, @NonNull Class<P> clazz, @NonNull Supplier<P> supplier) {
            if (this.outbound.putIfAbsent(clazz, id) != null || this.classToSupplier.putIfAbsent(clazz, supplier) != null)  {
                throw new IllegalStateException(String.format("Packet class \"%s\" already registered!", clazz.getCanonicalName()));
            }
            return this;
        }

        public <P extends InboundPacket<S>> Registerer inbound(int id, @NonNull Class<P> clazz) {
            if (this.inbound.putIfAbsent(id, clazz) != null)  {
                throw new IllegalStateException(String.format("Packet class \"%s\" already registered!", clazz.getCanonicalName()));
            }
            return this;
        }

        public <P extends InboundPacket<S>> Registerer inbound(int id, @NonNull Class<P> clazz, @NonNull Supplier<P> supplier) {
            if (this.inbound.putIfAbsent(id, clazz) != null || this.classToSupplier.putIfAbsent(clazz, supplier) != null)  {
                throw new IllegalStateException(String.format("Packet class \"%s\" already registered!", clazz.getCanonicalName()));
            }
            return this;
        }

        public <P extends Packet<S>> Registerer bidirectional(int id, @NonNull Class<P> clazz) {
            if (this.outbound.putIfAbsent(clazz, id) != null || this.inbound.putIfAbsent(id, clazz) != null)  {
                throw new IllegalStateException(String.format("Packet class \"%s\" already registered!", clazz.getCanonicalName()));
            }
            return this;
        }

        public <P extends Packet<S>> Registerer bidirectional(int id, @NonNull Class<P> clazz, @NonNull Supplier<P> supplier) {
            if (this.outbound.putIfAbsent(clazz, id) != null || this.inbound.putIfAbsent(id, clazz) != null || this.classToSupplier.putIfAbsent(clazz, supplier) != null)  {
                throw new IllegalStateException(String.format("Packet class \"%s\" already registered!", clazz.getCanonicalName()));
            }
            return this;
        }
    }
}
