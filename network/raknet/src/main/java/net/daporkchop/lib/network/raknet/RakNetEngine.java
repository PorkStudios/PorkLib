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

package net.daporkchop.lib.network.raknet;

import com.nukkitx.network.raknet.RakNetReliability;
import io.netty.channel.EventLoopGroup;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.network.endpoint.PClient;
import net.daporkchop.lib.network.endpoint.PServer;
import net.daporkchop.lib.network.endpoint.builder.ClientBuilder;
import net.daporkchop.lib.network.endpoint.builder.ServerBuilder;
import net.daporkchop.lib.network.raknet.endpoint.PRakNetClient;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.session.Reliability;
import net.daporkchop.lib.network.transport.TransportEngine;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Accessors(fluent = true)
public class RakNetEngine implements TransportEngine {
    protected static final Set<Reliability> RELIABILITIES = Collections.unmodifiableSet(EnumSet.allOf(Reliability.class));

    protected static final Map<Reliability, RakNetReliability> TO_RAKNET = new EnumMap<>(Reliability.class);
    protected static final Map<RakNetReliability, Reliability> FROM_RAKNET = new EnumMap<>(RakNetReliability.class);

    static {
        TO_RAKNET.put(Reliability.UNRELIABLE, RakNetReliability.UNRELIABLE);
        TO_RAKNET.put(Reliability.UNRELIABLE_SEQUENCED, RakNetReliability.UNRELIABLE_SEQUENCED);
        TO_RAKNET.put(Reliability.RELIABLE, RakNetReliability.RELIABLE);
        TO_RAKNET.put(Reliability.RELIABLE_ORDERED, RakNetReliability.RELIABLE_ORDERED);
        TO_RAKNET.put(Reliability.RELIABLE_SEQUENCED, RakNetReliability.RELIABLE_SEQUENCED);

        FROM_RAKNET.put(RakNetReliability.UNRELIABLE, Reliability.UNRELIABLE);
        FROM_RAKNET.put(RakNetReliability.UNRELIABLE_SEQUENCED, Reliability.UNRELIABLE_SEQUENCED);
        FROM_RAKNET.put(RakNetReliability.RELIABLE, Reliability.RELIABLE);
        FROM_RAKNET.put(RakNetReliability.RELIABLE_ORDERED, Reliability.RELIABLE_ORDERED);
        FROM_RAKNET.put(RakNetReliability.RELIABLE_SEQUENCED, Reliability.RELIABLE_SEQUENCED);
        FROM_RAKNET.put(RakNetReliability.UNRELIABLE_WITH_ACK_RECEIPT, Reliability.UNRELIABLE);
        FROM_RAKNET.put(RakNetReliability.RELIABLE_WITH_ACK_RECEIPT, Reliability.RELIABLE);
        FROM_RAKNET.put(RakNetReliability.RELIABLE_ORDERED_WITH_ACK_RECEIPT, Reliability.RELIABLE_ORDERED);
    }

    public static RakNetReliability toRakNet(@NonNull Reliability reliability)  {
        return TO_RAKNET.get(reliability);
    }

    public static Reliability fromRakNet(@NonNull RakNetReliability reliability)  {
        return FROM_RAKNET.get(reliability);
    }

    public static RakNetEngine defaultInstance() {
        return new RakNetEngine(null, true);
    }

    public static RakNetEngine defaultInstance(@NonNull EventLoopGroup group, boolean autoShutdownGroup) {
        return new RakNetEngine(null, autoShutdownGroup || group == null);
    }

    protected final EventLoopGroup group;
    protected final boolean autoShutdownGroup;
    protected int groupRefCount = 0;

    @Override
    public <S extends AbstractUserSession<S>> PClient<S> createClient(@NonNull ClientBuilder<S> builder) {
        return new PRakNetClient<>(builder);
    }

    @Override
    public <S extends AbstractUserSession<S>> PServer<S> createServer(@NonNull ServerBuilder<S> builder) {
        return null;
    }

    @Override
    public Collection<Reliability> supportedReliabilities() {
        return RELIABILITIES;
    }

    @Override
    public boolean isReliabilitySupported(@NonNull Reliability reliability) {
        return true;
    }

    public synchronized EventLoopGroup useGroup() {
        if (this.groupRefCount >= 0) {
            this.groupRefCount++;
            return this.group;
        } else {
            throw new IllegalStateException("Group closed!");
        }
    }

    public synchronized void returnGroup(@NonNull EventLoopGroup group) {
        if (this.groupRefCount < 0) {
            throw new IllegalStateException("Group closed!");
        } else if (--this.groupRefCount == 0 && this.group != null && this.group == group) {
            group.shutdownGracefully();
            this.groupRefCount = -1;
        }
    }
}
