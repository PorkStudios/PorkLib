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

package net.daporkchop.lib.http.impl.java.client;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.http.client.HttpClient;
import net.daporkchop.lib.http.client.builder.AsyncRequestBuilder;
import net.daporkchop.lib.http.client.builder.BlockingRequestBuilder;
import net.daporkchop.lib.http.impl.java.client.builder.BlockingJavaRequestBuilder;

/**
 * A simple implementation of {@link HttpClient} using Java's built-in HTTP client features.
 *
 * @author DaPorkchop_
 */
//TODO: proxy config
@Accessors(fluent = true)
public final class JavaHttpClient implements HttpClient {
    @Getter
    protected final Promise<Void> closeFuture = GlobalEventExecutor.INSTANCE.newPromise();

    @Override
    public AsyncRequestBuilder prepareAsync() {
        throw new UnsupportedOperationException("async request");
    }

    @Override
    public BlockingRequestBuilder prepareBlocking() {
        return new BlockingJavaRequestBuilder(this);
    }

    @Override
    public Future<Void> close() {
        this.closeFuture.trySuccess(null);
        return this.closeFuture;
    }
}
