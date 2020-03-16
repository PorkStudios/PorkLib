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

package net.daporkchop.lib.concurrent;

import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.concurrent.compatibility.CompletableFutureAsPFuture;
import net.daporkchop.lib.concurrent.compatibility.NettyFutureAsCompletableFuture;
import net.daporkchop.lib.concurrent.compatibility.NettyFutureAsPFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static net.daporkchop.lib.common.util.PorkUtil.*;
import static net.daporkchop.lib.unsafe.PUnsafe.*;

/**
 * Helpers for dealing with various implementations of a future value.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PFutures {
    protected final long DEFAULTPROMISE_EXECUTOR_OFFSET = pork_getOffset(DefaultPromise.class, "executor");

    public static <V> PFuture<V> wrap(@NonNull CompletionStage<? extends V> toWrap) {
        if (toWrap instanceof PFuture) {
            return uncheckedCast(toWrap);
        } else if (toWrap instanceof Future) {
            //TODO: cache instances of this...
            return new NettyFutureAsPFuture<>(uncheckedCast(toWrap));
        } else if (toWrap instanceof CompletableFuture) {
            if (toWrap instanceof NettyFutureAsCompletableFuture) {
                return wrap(uncheckedCast(((NettyFutureAsCompletableFuture) toWrap).delegate()));
            } else {
                //TODO: cache instances of this...
                return new CompletableFutureAsPFuture<>(uncheckedCast(toWrap));
            }
        }
        throw new IllegalArgumentException(className(toWrap));
    }

    public static EventExecutor executor(@NonNull Future<?> future) {
        if (future instanceof PFuture) {
            return ((PFuture) future).executor();
        } else if (future instanceof DefaultPromise) {
            return getObject(future, DEFAULTPROMISE_EXECUTOR_OFFSET);
        } else {
            return null;
        }
    }
}
