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
import io.netty.util.concurrent.SucceededFuture;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.network.nettycommon.eventloopgroup.factory.EventLoopGroupFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.function.Supplier;

import static java.lang.Integer.max;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public final class DefaultEventLoopGroupPool implements EventLoopGroupPool {
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
