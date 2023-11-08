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
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.network.nettycommon.eventloopgroup.factory.EventLoopGroupFactory;
import net.daporkchop.lib.network.nettycommon.transport.Transport;

import java.util.concurrent.Executor;

import static java.lang.Integer.max;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public abstract class DefaultEventLoopGroupPool implements EventLoopGroupPool {
    @NonNull
    protected final EventLoopGroupFactory factory;

    protected final Executor executor;
    protected final int threads;

    protected Future<Void> shutdownFuture;
    protected EventLoopGroup group;
    protected int referenceCount;

    @Override
    public synchronized EventLoopGroup get() {
        if (this.group == null) {
            this.referenceCount = 1;
            this.group = this.factory.create(this.threads, this.executor);
        }
        return this.group;
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized boolean release(@NonNull EventLoopGroup group) {
        if (this.group == group && (this.referenceCount = max(0, this.referenceCount - 1)) == 0)   {
            this.shutdownFuture = (Future<Void>) (Future) this.group.shutdownGracefully();
            this.group = null;
            return true;
        } else {
            return false;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized Future<Void> shutdown() {
        if (this.group != null) {
            this.shutdownFuture = (Future<Void>) (Future) this.group.shutdownGracefully();
            this.group = null;
            this.referenceCount = 0;
        }
        return this.shutdownFuture;
    }
}
