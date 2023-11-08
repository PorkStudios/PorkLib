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

package net.daporkchop.lib.network.nettycommon.eventloopgroup.pool;

import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;
import lombok.NonNull;
import net.daporkchop.lib.network.nettycommon.transport.Transport;

/**
 * Allows for sharing a single {@link EventLoopGroup} across multiple bootstraps.
 *
 * @author DaPorkchop_
 */
public interface EventLoopGroupPool extends AutoCloseable {
    /**
     * Gets the {@link Transport} that this pool's {@link EventLoopGroup} uses.
     *
     * @return this pool's {@link EventLoopGroup}'s {@link Transport}
     */
    Transport transport();

    /**
     * Gets the currently active {@link EventLoopGroup}, creating a new one if none is currently active.
     *
     * @return the currently active {@link EventLoopGroup}
     */
    EventLoopGroup get();

    /**
     * Releases a reference to a currently active {@link EventLoopGroup}.
     * <p>
     * Should only be called when you no longer need the {@link EventLoopGroup} in question, as the group may be
     * shut down after calling this method (or queued for shutting down, or something else depending on the
     * implementation).
     *
     * @param group the {@link EventLoopGroup} to release a reference to
     * @return whether or not the reference to the {@link EventLoopGroup} could be released (will be {@code false} e.g. if the given {@link EventLoopGroup} does not belong to this pool)
     */
    boolean release(@NonNull EventLoopGroup group);

    /**
     * Totally shuts down the {@link EventLoopGroup} if one is currently active in this pool, waiting for all tasks
     * on the group to exit gracefully (e.g. close all active channels).
     *
     * @return a future that will be notified when the currently active {@link EventLoopGroup} is shut down. If no group is currently active, this will return a dummy noop future
     */
    Future<Void> shutdown();

    @Override
    default void close() throws Exception {
        this.shutdown().sync();
    }
}
