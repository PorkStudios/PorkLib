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

package net.daporkchop.lib.network.netty.util.future;

import io.netty.channel.Channel;
import io.netty.channel.DefaultChannelPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.NonNull;
import net.daporkchop.lib.concurrent.future.Promise;
import net.daporkchop.lib.concurrent.util.exception.AlreadyCompleteException;

import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
public class NettyChannelPromise extends DefaultChannelPromise implements Promise {
    public NettyChannelPromise(Channel channel) {
        super(channel);
    }

    public NettyChannelPromise(Channel channel, EventExecutor executor) {
        super(channel, executor);
    }

    @Override
    public void completeSuccessfully() throws AlreadyCompleteException {
        if (!this.trySuccess()) {
            throw new AlreadyCompleteException();
        }
    }

    @Override
    public Exception getError() {
        return (Exception) this.cause();
    }

    @Override
    public NettyChannelPromise sync() throws InterruptedException {
        return (NettyChannelPromise) super.sync();
    }

    @Override
    public NettyChannelPromise syncUninterruptibly() {
        return (NettyChannelPromise) super.syncUninterruptibly();
    }

    @Override
    public void completeError(@NonNull Exception error) throws AlreadyCompleteException {
        if (!this.tryFailure(error))    {
            throw new AlreadyCompleteException();
        }
    }

    @Override
    public void cancel() throws AlreadyCompleteException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Promise addListener(@NonNull Consumer<Promise> callback) {
        super.addListener((GenericFutureListener<NettyChannelPromise>) callback::accept);
        return this;
    }

    @Override
    public Promise addListener(@NonNull Runnable callback) {
        super.addListener((GenericFutureListener<NettyChannelPromise>) f -> callback.run());
        return this;
    }

    @Override
    public Promise addSuccessListener(@NonNull Runnable callback) {
        super.addListener((GenericFutureListener<NettyChannelPromise>) f -> {
            if (f.isSuccess())  {
                callback.run();
            }
        });
        return this;
    }

    @Override
    public Promise addErrorListener(@NonNull Runnable callback) {
        super.addListener((GenericFutureListener<NettyChannelPromise>) f -> {
            if (f.cause() != null)  {
                callback.run();
            }
        });
        return this;
    }

    @Override
    public Promise addErrorListener(@NonNull Consumer<Exception> callback) {
        super.addListener((GenericFutureListener<NettyChannelPromise>) f -> {
            if (f.cause() != null)  {
                callback.accept((Exception) f.cause());
            }
        });
        return this;
    }

    @Override
    public Promise addCancelListener(@NonNull Runnable callback) {
        super.addListener((GenericFutureListener<NettyChannelPromise>) f -> {
            if (f.isCancelled())  {
                callback.run();
            }
        });
        return this;
    }
}
