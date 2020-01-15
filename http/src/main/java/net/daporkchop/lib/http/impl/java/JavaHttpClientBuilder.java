/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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
