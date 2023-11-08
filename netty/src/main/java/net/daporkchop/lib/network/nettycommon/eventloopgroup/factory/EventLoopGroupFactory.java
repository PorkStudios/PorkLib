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

package net.daporkchop.lib.network.nettycommon.eventloopgroup.factory;

import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.ThreadPerTaskExecutor;
import lombok.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

/**
 * Provides instances of {@link EventLoopGroup}.
 *
 * @author DaPorkchop_
 */
@FunctionalInterface
public interface EventLoopGroupFactory {
    default EventLoopGroup create(int threads) {
        return this.create(threads, new ThreadPerTaskExecutor(Thread::new));
    }

    default EventLoopGroup create(int threads, @NonNull ThreadFactory threadFactory) {
        return this.create(threads, new ThreadPerTaskExecutor(threadFactory));
    }

    /**
     * Creates a new {@link EventLoopGroup}.
     *
     * @param threads  the number of threads that the new {@link EventLoopGroup} will use
     * @param executor the executor that the threads will be run on
     * @return a new {@link EventLoopGroup}
     */
    EventLoopGroup create(int threads, Executor executor);
}
