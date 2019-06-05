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
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.function.TriConsumer;
import net.daporkchop.lib.common.function.io.IOTriConsumer;
import net.daporkchop.lib.common.function.io.IOTriFunction;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.network.session.StatedProtocolSession;
import net.daporkchop.lib.network.util.PacketMetadata;

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

    public StatedPacketProtocol(@NonNull Class<E> clazz)    {
        this.states = new EnumMap<>(clazz);

        this.registerPackets(new Registry());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void encodeMessage(@NonNull S session, @NonNull Object msg, @NonNull DataOut out) throws IOException {
        E enumState = session.state();
        ProtocolState state = this.states.get(enumState);
        Member member = state.classToMember.get(msg.getClass());
        if (member == null) {
            throw new IllegalArgumentException(String.format("Packet \"%s\" is not registered for protocol state \"%s\"!", PorkUtil.className(msg), enumState));
        } else {
            member.encoder.acceptThrowing(session, msg, out);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onReceive(@NonNull S session, @NonNull DataIn in, @NonNull PacketMetadata metadata) throws IOException {
        E enumState = session.state();
        ProtocolState state = this.states.get(enumState);
        Member member = state.idToMember.get(metadata.protocolId());
        if (member == null) {
            throw new IllegalArgumentException(String.format("Packet ID \"%d\" is not registered for protocol state \"%s\"!", metadata.protocolId(), enumState));
        } else {
            member.handler.accept(session, member.decoder.applyThrowing(session, in, metadata), metadata);
        }
    }

    protected abstract void registerPackets(@NonNull Registry registry);

    protected class ProtocolState   {
        protected final IntObjectMap<Member<?>> idToMember = new IntObjectHashMap<>();
        protected final Map<Class<?>, Member<?>> classToMember = new IdentityHashMap<>();
    }

    @RequiredArgsConstructor
    protected class Member<M>  {
        protected final Supplier<M> supplier;
        protected final IOTriConsumer<S, M, DataOut> encoder;
        protected final IOTriFunction<S, DataIn, PacketMetadata, M> decoder;
        protected final TriConsumer<S, M, PacketMetadata> handler;
    }

    protected class Registry {
        //TODO
    }
}
