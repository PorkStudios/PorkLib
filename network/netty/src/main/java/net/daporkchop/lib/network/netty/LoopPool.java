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

package net.daporkchop.lib.network.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PorkUtil;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A very simple pool for instances of {@link EventLoopGroup}. Automatically reference counts them, shutting them down
 * if required.
 *
 * @author DaPorkchop_
 */
public class LoopPool {
    protected static EventLoopGroup DEFAULT_GROUP;
    protected static final Map<EventLoopGroup, AtomicInteger> GROUP_CACHE = new IdentityHashMap<>();

    /**
     * Provides a default instance of an {@link EventLoopGroup}. For most applications, this should suffice. This
     * default group will create as many threads as there are CPU cores, and will be automatically shut down when
     * no longer required by any endpoints.
     */
    public static EventLoopGroup defaultGroup() {
        synchronized (GROUP_CACHE) {
            if (DEFAULT_GROUP == null) {
                DEFAULT_GROUP = new NioEventLoopGroup(PorkUtil.CPU_COUNT);
            }
            return useGroup(DEFAULT_GROUP);
        }
    }

    public static EventLoopGroup useGroup(@NonNull EventLoopGroup group) {
        synchronized (GROUP_CACHE) {
            GROUP_CACHE.computeIfAbsent(group, g -> new AtomicInteger(0)).incrementAndGet();
        }
        return group;
    }

    public static void returnGroup(@NonNull EventLoopGroup group) {
        synchronized (GROUP_CACHE) {
            if (GROUP_CACHE.containsKey(group) && GROUP_CACHE.get(group).decrementAndGet() <= 0) {
                GROUP_CACHE.remove(group);
                group.shutdownGracefully();
                if (group == DEFAULT_GROUP) {
                    DEFAULT_GROUP = null;
                }
            }
        }
    }
}
