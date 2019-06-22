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
import lombok.NonNull;
import net.daporkchop.lib.network.netty.session.NettySession;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.util.group.SessionFilter;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Because {@link io.netty.channel.group.DefaultChannelGroup} is dumb and doesn't let me use a custom future class.
 *
 * @author DaPorkchop_
 * @see io.netty.channel.group.DefaultChannelGroup
 */
public class PorkChannelGroup<S extends AbstractUserSession<S>> extends AbstractSet<NettySession<S>> {
    private static final AtomicInteger nextId = new AtomicInteger();

    // Create a safe duplicate of the message to write it to a channel but not affect other writes.
    // See https://github.com/netty/netty/issues/1461
    private static Object safeDuplicate(Object message) {
        if (message instanceof ByteBuf) {
            return ((ByteBuf) message).retainedDuplicate();
        } else if (message instanceof ByteBufHolder) {
            return ((ByteBufHolder) message).retainedDuplicate();
        } else {
            return ReferenceCountUtil.retain(message);
        }
    }

    private final String name;
    private final EventExecutor executor;
    private final ConcurrentMap<ChannelId, NettySession<S>> channels = PlatformDependent.newConcurrentHashMap();
    private final ChannelFutureListener remover = f -> this.remove(f.channel());
    private final boolean stayClosed;
    private volatile boolean closed;

    public PorkChannelGroup(EventExecutor executor) {
        this(executor, false);
    }

    public PorkChannelGroup(String name, EventExecutor executor) {
        this(name, executor, false);
    }

    public PorkChannelGroup(EventExecutor executor, boolean stayClosed) {
        this("group-0x" + Integer.toHexString(nextId.incrementAndGet()), executor, stayClosed);
    }

    public PorkChannelGroup(@NonNull String name, @NonNull EventExecutor executor, boolean stayClosed) {
        this.name = name;
        this.executor = executor;
        this.stayClosed = stayClosed;
    }

    public String name() {
        return this.name;
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
        }
        if (this.stayClosed && this.closed) {
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
            return true;
        }
    }

    @Override
    public void clear() {
        channels.clear();
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

    public PorkChannelGroupFuture<S> write(@NonNull Object message) {
        return write(message, SessionFilter.all());
    }

    public PorkChannelGroupFuture<S> write(@NonNull Object message, @NonNull SessionFilter<S> filter) {
        Map<NettySession<S>, ChannelFuture> futures = new LinkedHashMap<>(size());
        for (NettySession<S> c : this.channels.values()) {
            if (filter.test(c)) {
                futures.put(c, c.write(safeDuplicate(message)));
            }
        }
        PorkChannelGroupFuture<S> future = this.newFuture(futures);
        ReferenceCountUtil.release(message);
        return future;
    }

    public PorkChannelGroupFuture<S> disconnect() {
        return disconnect(SessionFilter.all());
    }

    public PorkChannelGroupFuture<S> disconnect(@NonNull SessionFilter<S> filter) {
        if (filter == null) {
            throw new NullPointerException("matcher");
        }

        Map<NettySession<S>, ChannelFuture> futures = new LinkedHashMap<>(size());
        for (NettySession<S> s : this.channels.values()) {
            if (filter.test(s)) {
                futures.put(s, s.disconnect());
            }
        }
        /*
        this.channels.values().stream()
                .filter(filter)
                .collect(Collectors.toMap(
                        c -> c,
                        NettySession::disconnect,
                        (u, v) -> {
                            throw new IllegalStateException(String.format("Duplicate key %s", u));
                        },
                        LinkedHashMap::new
                ))
         */

        return this.newFuture(futures);
    }

    public PorkChannelGroupFuture<S> close() {
        return close(SessionFilter.all());
    }

    public PorkChannelGroupFuture<S> close(@NonNull SessionFilter<S> filter) {
        if (this.stayClosed) {
            this.closed = true;
        }
        Map<NettySession<S>, ChannelFuture> futures = new LinkedHashMap<>(size());
        for (NettySession<S> s : this.channels.values()) {
            if (filter.test(s)) {
                futures.put(s, s.close());
            }
        }
        return this.newFuture(futures);
    }

    public PorkChannelGroupFuture<S> deregister() {
        return deregister(SessionFilter.all());
    }

    public PorkChannelGroupFuture<S> deregister(@NonNull SessionFilter<S> filter) {
        Map<NettySession<S>, ChannelFuture> futures = new LinkedHashMap<>(size());
        for (NettySession<S> s : this.channels.values()) {
            if (filter.test(s)) {
                futures.put(s, s.deregister());
            }
        }
        return this.newFuture(futures);
    }

    public PorkChannelGroup<S> flush() {
        return flush(SessionFilter.all());
    }

    public PorkChannelGroup<S> flush(@NonNull SessionFilter<S> filter) {
        for (NettySession<S> s : this.channels.values()) {
            if (filter.test(s)) {
                s.flush();
            }
        }
        return this;
    }

    public PorkChannelGroupFuture<S> writeAndFlush(Object message) {
        return writeAndFlush(message, SessionFilter.all());
    }

    public PorkChannelGroupFuture<S> writeAndFlush(@NonNull Object message, @NonNull SessionFilter<S> filter) {
        Map<NettySession<S>, ChannelFuture> futures = new LinkedHashMap<>(size());
        for (NettySession<S> s : this.channels.values()) {
            if (filter.test(s)) {
                futures.put(s, s.writeAndFlush(safeDuplicate(message)));
            }
        }
        PorkChannelGroupFuture<S> future = this.newFuture(futures);
        ReferenceCountUtil.release(message);
        return future;
    }

    public PorkChannelGroupFuture<S> newCloseFuture() {
        return newCloseFuture(SessionFilter.all());
    }

    public PorkChannelGroupFuture<S> newCloseFuture(@NonNull SessionFilter<S> filter) {
        Map<NettySession<S>, ChannelFuture> futures = new LinkedHashMap<>(size());
        for (NettySession<S> s : this.channels.values()) {
            if (filter.test(s)) {
                futures.put(s, s.closeFuture());
            }
        }
        return this.newFuture(futures);
    }

    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + "(name: " + name() + ", size: " + size() + ')';
    }

    protected PorkChannelGroupFuture<S> newFuture(Map<NettySession<S>, ChannelFuture> futures) {
        return new PorkChannelGroupFuture<>(this, futures, this.executor);
    }
}
