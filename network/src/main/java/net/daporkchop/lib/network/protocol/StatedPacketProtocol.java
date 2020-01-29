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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.function.plain.TriConsumer;
import net.daporkchop.lib.common.function.io.IOQuadConsumer;
import net.daporkchop.lib.common.function.io.IOTriConsumer;
import net.daporkchop.lib.common.util.GenericMatcher;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.network.protocol.packet.IncomingPacket;
import net.daporkchop.lib.network.protocol.packet.OutboundPacket;
import net.daporkchop.lib.network.session.StatedProtocolSession;
import net.daporkchop.lib.network.util.PacketMetadata;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author DaPorkchop_
 */
public abstract class StatedPacketProtocol<P extends StatedProtocol<P, S, E>, S extends StatedProtocolSession<S, P, E>, E extends Enum<E>> implements StatedProtocol<P, S, E> {
    private final Map<E, ProtocolState> states;

    public StatedPacketProtocol()    {
        this.states = new EnumMap<>(GenericMatcher.<E, StatedProtocol>uncheckedFind(this.getClass(), StatedProtocol.class, "E"));

        this.registerPackets(new Registry());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void encodeMessage(@NonNull S session, @NonNull Object msg, @NonNull DataOut out, @NonNull PacketMetadata metadata) throws IOException {
        E enumState = session.state();
        ProtocolState state = this.states.get(enumState);
        Member member = state.outbound.get(msg.getClass());
        if (member == null) {
            throw new IllegalArgumentException(String.format("Packet \"%s\" is not registered for protocol state \"%s\"!", PorkUtil.className(msg), enumState));
        } else {
            metadata.protocolId(member.id);
            member.encoder.acceptThrowing(session, msg, out);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onReceive(@NonNull S session, @NonNull DataIn in, @NonNull PacketMetadata metadata) throws IOException {
        //session.logger().debug("Protocol received packet @ %d bytes, with ID: %d", in.available(), metadata.protocolId());
        E enumState = session.state();
        ProtocolState state = this.states.get(enumState);
        Member member = state.incoming.get(metadata.protocolId());
        if (member == null) {
            throw new IllegalArgumentException(String.format("Packet ID %d is not registered for protocol state \"%s\"!", metadata.protocolId(), enumState));
        } else {
            Object msg = member.supplier.get();
            member.decoder.accept(session, msg, in, metadata);
            member.handler.accept(session, msg, metadata);
        }
    }

    protected abstract void registerPackets(@NonNull Registry registry);

    @RequiredArgsConstructor
    protected class ProtocolState   {
        @NonNull
        protected final E state;
        protected final IntObjectMap<Member<?>> incoming = new IntObjectHashMap<>();
        protected final Map<Class<?>, Member<?>> outbound = new IdentityHashMap<>();
    }

    @RequiredArgsConstructor
    protected class Member<M>  {
        protected final Class<M> clazz;
        protected final Supplier<M> supplier;
        protected final IOTriConsumer<S, M, DataOut> encoder;
        protected final IOQuadConsumer<S, M, DataIn, PacketMetadata> decoder;
        protected final TriConsumer<S, M, PacketMetadata> handler;
        protected final int id;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    protected class Registry {
        public <M extends OutboundPacket<S> & IncomingPacket<S>> Registry register(@NonNull E state, int id, @NonNull Class<M> clazz)   {
            return this.register(
                    state,
                    id,
                    clazz,
                    () -> PUnsafe.allocateInstance(clazz),
                    (session, msg, out) -> msg.encode(out, session),
                    (sesssion, msg, in, metadata) -> msg.decode(in, sesssion),
                    (session, msg, metadata) -> msg.handle(session)
            );
        }

        public <M> Registry register(@NonNull E state, int id, @NonNull Class<M> clazz, @NonNull Supplier<M> supplier, @NonNull IOTriConsumer<S, M, DataOut> encoder, @NonNull IOQuadConsumer<S, M, DataIn, PacketMetadata> decoder, @NonNull TriConsumer<S, M, PacketMetadata> handler)  {
            synchronized (StatedPacketProtocol.this) {
                ProtocolState protocolState = StatedPacketProtocol.this.states.computeIfAbsent(state, ProtocolState::new);
                if (protocolState.incoming.containsKey(id)) {
                    throw new IllegalStateException(String.format("Packet ID %d is already registered for protocol state \"%s\"!", id, state));
                } else if (protocolState.outbound.containsKey(clazz))  {
                    throw new IllegalStateException(String.format("Packet class \"%s\" is already registered for protocol state \"%s\"!", clazz.getCanonicalName(), state));
                } else {
                    Member<M> member = new Member<>(clazz, supplier, encoder, decoder, handler, id);
                    protocolState.incoming.put(id, member);
                    protocolState.outbound.put(clazz, member);
                }
            }
            return this;
        }

        public <M extends OutboundPacket<S>> Registry registerOutbound(@NonNull E state, int id, @NonNull Class<M> clazz)   {
            return this.registerOutbound(
                    state,
                    id,
                    clazz,
                    (session, msg, out) -> msg.encode(out, session)
            );
        }

        public <M> Registry registerOutbound(@NonNull E state, int id, @NonNull Class<M> clazz, @NonNull IOTriConsumer<S, M, DataOut> encoder)  {
            synchronized (StatedPacketProtocol.this) {
                ProtocolState protocolState = StatedPacketProtocol.this.states.computeIfAbsent(state, ProtocolState::new);
                if (protocolState.outbound.containsKey(clazz))  {
                    throw new IllegalStateException(String.format("Packet class \"%s\" is already registered for protocol state \"%s\"!", clazz.getCanonicalName(), state));
                } else {
                    Member<M> member = new Member<>(clazz, null, encoder, null, null, id);
                    protocolState.outbound.put(clazz, member);
                }
            }
            return this;
        }

        public <M extends IncomingPacket<S>> Registry registerIncoming(@NonNull E state, int id, @NonNull Class<M> clazz)   {
            return this.registerIncoming(
                    state,
                    id,
                    clazz,
                    () -> PUnsafe.allocateInstance(clazz),
                    (sesssion, msg, in, metadata) -> msg.decode(in, sesssion),
                    (session, msg, metadata) -> msg.handle(session)
            );
        }

        public <M> Registry registerIncoming(@NonNull E state, int id, @NonNull Class<M> clazz, @NonNull Supplier<M> supplier, @NonNull IOQuadConsumer<S, M, DataIn, PacketMetadata> decoder, @NonNull TriConsumer<S, M, PacketMetadata> handler)  {
            synchronized (StatedPacketProtocol.this) {
                ProtocolState protocolState = StatedPacketProtocol.this.states.computeIfAbsent(state, ProtocolState::new);
                if (protocolState.incoming.containsKey(id)) {
                    throw new IllegalStateException(String.format("Packet ID %d is already registered for protocol state \"%s\"!", id, state));
                } else {
                    Member<M> member = new Member<>(clazz, supplier, null, decoder, handler, id);
                    protocolState.incoming.put(id, member);
                }
            }
            return this;
        }
    }
}
