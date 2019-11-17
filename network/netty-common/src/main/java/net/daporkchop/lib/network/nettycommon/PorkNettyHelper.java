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

package net.daporkchop.lib.network.nettycommon;

import io.netty.channel.epoll.Epoll;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.network.nettycommon.eventloopgroup.pool.DefaultEventLoopGroupPool;
import net.daporkchop.lib.network.nettycommon.eventloopgroup.pool.EventLoopGroupPool;
import net.daporkchop.lib.network.nettycommon.eventloopgroup.factory.EpollEventLoopGroupFactory;
import net.daporkchop.lib.network.nettycommon.eventloopgroup.factory.NioEventLoopGroupFactory;
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
                STANDARD_EPOLL_POOL = new DefaultEventLoopGroupPool(new EpollEventLoopGroupFactory(), null, Runtime.getRuntime().availableProcessors());
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
                STANDARD_NIO_POOL = new DefaultEventLoopGroupPool(new NioEventLoopGroupFactory(), null, Runtime.getRuntime().availableProcessors());
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
