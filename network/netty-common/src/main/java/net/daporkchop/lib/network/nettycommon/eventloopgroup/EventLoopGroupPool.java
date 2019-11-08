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

package net.daporkchop.lib.network.nettycommon.eventloopgroup;

import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;
import lombok.NonNull;

/**
 * Allows for sharing a single {@link EventLoopGroup} across multiple bootstraps.
 *
 * @author DaPorkchop_
 */
public interface EventLoopGroupPool {
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
}
