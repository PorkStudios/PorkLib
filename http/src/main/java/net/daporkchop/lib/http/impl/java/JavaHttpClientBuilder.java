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

package net.daporkchop.lib.http.impl.java;

import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.ImmediateEventExecutor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.misc.threadfactory.ThreadFactoryBuilder;
import net.daporkchop.lib.common.pool.selection.SelectionPool;
import net.daporkchop.lib.http.request.RequestBuilder;
import net.daporkchop.lib.http.util.Constants;

import java.util.concurrent.ThreadFactory;

/**
 * Builder for {@link JavaHttpClient}.
 * <p>
 * This class is not thread-safe.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor
@Getter
@Setter
@Accessors(fluent = true, chain = true)
public final class JavaHttpClientBuilder {
    @NonNull
    protected ThreadFactory threadFactory;

    @NonNull
    protected EventExecutorGroup group;

    @NonNull
    protected SelectionPool<String> userAgents;

    /**
     * Whether or not the resulting {@link JavaHttpClient} will send blocking requests.
     * <p>
     * If {@code true}, requests will be sent and handled from the thread that invoked {@link RequestBuilder#send()}. This can be beneficial for performance,
     * but means that {@link RequestBuilder#send()} will be blocking and may cause unexpected behavior. Use at your own risk!
     */
    protected boolean blockingRequests = false;

    public JavaHttpClient build() {
        if (this.threadFactory == null) {
            this.threadFactory = ThreadFactoryBuilder.defaultThreadFactory();
        }
        if (this.group == null) {
            this.group = ImmediateEventExecutor.INSTANCE;
        }
        if (this.userAgents == null) {
            this.userAgents = Constants.DEFAULT_USER_AGENT_SELECTION_POOL;
        }

        return this.blockingRequests ? new BlockingJavaHttpClient(this) : new JavaHttpClient(this);
    }
}
