/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.network.nettycommon;

import io.netty.channel.epoll.Epoll;
import io.netty.util.Version;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.network.nettycommon.eventloopgroup.factory.EpollEventLoopGroupFactory;
import net.daporkchop.lib.network.nettycommon.eventloopgroup.factory.NioEventLoopGroupFactory;
import net.daporkchop.lib.network.nettycommon.eventloopgroup.pool.DefaultEventLoopGroupPool;
import net.daporkchop.lib.network.nettycommon.eventloopgroup.pool.EventLoopGroupPool;
import net.daporkchop.lib.network.nettycommon.transport.EpollTransport;
import net.daporkchop.lib.network.nettycommon.transport.NioTransport;
import net.daporkchop.lib.network.nettycommon.transport.Transport;

/**
 * Helper class for dealing with Netty.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PorkNettyHelper {
    private EventLoopGroupPool STANDARD_EPOLL_POOL;
    private EventLoopGroupPool STANDARD_NIO_POOL;

    private Transport STANDARD_EPOLL_TRANSPORT;
    private Transport STANDARD_NIO_TRANSPORT;

    private final ThreadLocal<String> NETTY_VERSION = ThreadLocal.withInitial(() -> Version.identify().get("netty-common").artifactVersion());

    /**
     * @return the current version of netty-common
     */
    public String getNettyVersion() {
        return NETTY_VERSION.get();
    }

    /**
     * @return an {@link EventLoopGroupPool} usable for TCP
     */
    public EventLoopGroupPool getPoolTCP() {
        return getPoolEpollIfAvailable();
    }

    /**
     * @return an {@link EventLoopGroupPool} usable for UDP
     */
    public EventLoopGroupPool getPoolUDP() {
        return getPoolEpollIfAvailable();
    }

    /**
     * @return an {@link EventLoopGroupPool} usable for SCTP
     */
    public EventLoopGroupPool getPoolSCTP() {
        return getPoolNio();
    }

    /**
     * @return an {@link EventLoopGroupPool} backed by Epoll if possible, falling back to Java NIO otherwise
     */
    public EventLoopGroupPool getPoolEpollIfAvailable() {
        return Epoll.isAvailable() ? getPoolEpoll() : getPoolNio();
    }

    /**
     * @return an {@link EventLoopGroupPool} backed by Epoll
     */
    public EventLoopGroupPool getPoolEpoll() {
        Epoll.ensureAvailability();
        synchronized (PorkNettyHelper.class) {
            if (STANDARD_EPOLL_POOL == null) {
                STANDARD_EPOLL_POOL = new DefaultEventLoopGroupPool(new EpollEventLoopGroupFactory(), null, PorkUtil.CPU_COUNT) {
                    @Override
                    public Transport transport() {
                        return getTransportEpoll();
                    }
                };
            }
            return STANDARD_EPOLL_POOL;
        }
    }

    /**
     * @return an {@link EventLoopGroupPool} backed by Java NIO
     */
    public EventLoopGroupPool getPoolNio() {
        synchronized (PorkNettyHelper.class) {
            if (STANDARD_NIO_POOL == null) {
                STANDARD_NIO_POOL = new DefaultEventLoopGroupPool(new NioEventLoopGroupFactory(), null, PorkUtil.CPU_COUNT) {
                    @Override
                    public Transport transport() {
                        return getTransportNio();
                    }
                };
            }
            return STANDARD_NIO_POOL;
        }
    }

    /**
     * @return a {@link Transport} usable for TCP
     */
    public Transport getTransportTCP() {
        return getTransportEpollIfAvailable();
    }

    /**
     * @return a {@link Transport} usable for UDP
     */
    public Transport getTransportUDP() {
        return getTransportEpollIfAvailable();
    }

    /**
     * @return a {@link Transport} usable for SCTP
     */
    public Transport getTransportSCTP() {
        return getTransportNio();
    }

    /**
     * @return a {@link Transport} backed by Epoll if possible, falling back to Java NIO otherwise
     */
    public Transport getTransportEpollIfAvailable() {
        return Epoll.isAvailable() ? getTransportEpoll() : getTransportNio();
    }

    /**
     * @return a {@link Transport} backed by Epoll
     */
    public Transport getTransportEpoll() {
        Epoll.ensureAvailability();
        synchronized (PorkNettyHelper.class) {
            if (STANDARD_EPOLL_TRANSPORT == null) {
                STANDARD_EPOLL_TRANSPORT = new EpollTransport(getPoolEpoll());
            }
            return STANDARD_EPOLL_TRANSPORT;
        }
    }

    /**
     * @return a {@link Transport} backed by Java NIO
     */
    public Transport getTransportNio() {
        synchronized (PorkNettyHelper.class) {
            if (STANDARD_NIO_TRANSPORT == null) {
                STANDARD_NIO_TRANSPORT = new NioTransport(getPoolNio());
            }
            return STANDARD_NIO_TRANSPORT;
        }
    }
}
