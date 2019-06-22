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

package net.daporkchop.lib.network.netty.util.group;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelId;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.concurrent.future.Promise;
import net.daporkchop.lib.network.netty.session.NettySession;
import net.daporkchop.lib.network.netty.util.future.NettyPromiseWrapper;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.util.Priority;
import net.daporkchop.lib.network.util.group.Broadcaster;
import net.daporkchop.lib.network.util.group.SessionFilter;
import net.daporkchop.lib.network.util.reliability.Reliability;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Because {@link io.netty.channel.group.DefaultChannelGroup} is dumb and doesn't let me use a custom future class.
 * <p>
 * To be totally honest this class is now quite a bit different from the original one, I made a lot of API changes because reasons.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true, chain = true)
public class PorkChannelGroup<S extends AbstractUserSession<S>> extends AbstractSet<NettySession<S>> implements Broadcaster<PorkChannelGroup<S>, S> {
    protected static final long COUNT_OFFSET = PUnsafe.pork_getOffset(PorkChannelGroup.class, "count");
    protected static final AtomicInteger NEXT_ID = new AtomicInteger();

    protected static Object safeDuplicate(Object message) {
        if (message instanceof ByteBuf) {
            return ((ByteBuf) message).retainedDuplicate();
        } else if (message instanceof ByteBufHolder) {
            return ((ByteBufHolder) message).retainedDuplicate();
        } else {
            return ReferenceCountUtil.retain(message);
        }
    }

    @Getter
    protected final String name;
    protected final EventExecutor executor;
    protected final ConcurrentMap<ChannelId, NettySession<S>> channels = PlatformDependent.newConcurrentHashMap();
    protected final ChannelFutureListener remover = f -> this.remove(f.channel());
    @Getter
    protected final Promise closePromise;
    @Getter
    @Setter
    protected Reliability fallbackReliability;
    protected volatile int count = 0;
    protected volatile boolean closed;

    public PorkChannelGroup(EventExecutor executor) {
        this("group-0x" + Integer.toHexString(NEXT_ID.incrementAndGet()), executor);
    }

    public PorkChannelGroup(@NonNull String name, @NonNull EventExecutor executor) {
        this.name = name;
        this.executor = executor;
        this.closePromise = new NettyPromiseWrapper(executor.newPromise());
    }

    @Override
    public boolean isEmpty() {
        return this.channels.isEmpty();
    }

    @Override
    public int size() {
        return this.channels.size();
    }

    @Override
    public boolean contains(Object o) {
        return o instanceof NettySession && this.channels.containsKey(o);
    }

    @Override
    public boolean add(@NonNull NettySession<S> channel) {
        boolean added = this.channels.putIfAbsent(channel.id(), channel) == null;
        if (added) {
            channel.closeFuture().addListener(this.remover);
            PUnsafe.getAndAddInt(this, COUNT_OFFSET, 1);
        }
        if (this.closed) {
            channel.closeAsync();
        }
        return added;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(Object o) {
        NettySession<S> c = null;
        if (o instanceof ChannelId) {
            c = this.channels.remove(o);
        } else if (o instanceof NettySession) {
            c = this.channels.remove(((NettySession<S>) o).id());
        }
        if (c == null) {
            return false;
        } else {
            c.closeFuture().removeListener(this.remover);
            if (PUnsafe.getAndAddInt(this, COUNT_OFFSET, -1) == 1 && this.closed)  {
                this.closePromise.tryCompleteSuccessfully();
            }
            return true;
        }
    }

    @Override
    public void clear() {
        this.channels.clear();
    }

    @Override
    public Iterator<NettySession<S>> iterator() {
        return this.channels.values().iterator();
    }

    @Override
    public Object[] toArray() {
        return new ArrayList<>(this.channels.values()).toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return new ArrayList<>(this.channels.values()).toArray(a);
    }

    @Override
    public Promise broadcast(@NonNull Object message, int channel, Reliability reliability, Priority priority, int flags) {
        Map<NettySession<S>, ChannelFuture> futures = new LinkedHashMap<>(this.size());
        for (NettySession<S> s : this.channels.values()) {
            futures.put(s, s.send(message, channel, reliability, priority, flags));
        }
        return this.newFuture(futures);
    }

    @Override
    public Promise broadcast(@NonNull SessionFilter<S> filter, @NonNull Object message, int channel, Reliability reliability, Priority priority, int flags) {
        Map<NettySession<S>, ChannelFuture> futures = new LinkedHashMap<>(this.size());
        for (NettySession<S> s : this.channels.values()) {
            if (filter.test(s)) {
                futures.put(s, s.send(message, channel, reliability, priority, flags));
            }
        }
        return this.newFuture(futures);
    }

    @Override
    public void flushBuffer() {
        this.channels.forEach((id, session) -> session.flush()); //this causes fewer object allocations
    }

    @Override
    public void flushBuffer(@NonNull SessionFilter<S> filter) {
        for (NettySession<S> s : this.channels.values()) {
            if (filter.test(s)) {
                s.flush();
            }
        }
    }

    @Override
    public Promise closeSessions() {
        Map<NettySession<S>, ChannelFuture> futures = new LinkedHashMap<>(this.size());
        for (NettySession<S> s : this.channels.values()) {
            futures.put(s, s.close());
        }
        return this.newFuture(futures);
    }

    @Override
    public Promise closeSessions(@NonNull SessionFilter<S> filter) {
        Map<NettySession<S>, ChannelFuture> futures = new LinkedHashMap<>(this.size());
        for (NettySession<S> s : this.channels.values()) {
            if (filter.test(s)) {
                futures.put(s, s.close());
            }
        }
        return this.newFuture(futures);
    }

    @Override
    public Promise closeAsync() {
        this.closed = true;
        this.channels.forEach((id, session) -> session.close()); //this causes fewer object allocations
        return this.closePromise;
    }

    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + "(name: " + name() + ", size: " + size() + ')';
    }

    protected PorkChannelGroupFuture<S> newFuture(Map<NettySession<S>, ChannelFuture> futures) {
        return new PorkChannelGroupFuture<>(this, futures, this.executor);
    }
}
