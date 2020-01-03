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

package net.daporkchop.lib.http.response.aggregate;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.http.request.Request;
import net.daporkchop.lib.http.response.ResponseHeaders;
import net.daporkchop.lib.http.util.ProgressHandler;

/**
 * A {@link ResponseAggregator} that notifies an external function of the current download progress before delegating data events on to another
 * aggregator.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public final class NotifyingAggregator<A, V> implements ResponseAggregator<A, V> {
    protected long progress = 0L;
    protected long total = -1L;

    @NonNull
    protected final ResponseAggregator<A, V> delegate;
    @NonNull
    protected final ProgressHandler handler;

    @Override
    public A init(ResponseHeaders response, Request<V> request) throws Exception {
        this.handler.handle(0L, this.total = response.contentLength());
        return this.delegate.init(response, request);
    }

    @Override
    public A add(A temp, ByteBuf data, Request<V> request) throws Exception {
        this.handler.handle(this.progress += data.readableBytes(), this.total);
        return this.delegate.add(temp, data, request);
    }

    @Override
    public V doFinal(A temp, Request<V> request) throws Exception {
        this.handler.handle(this.total < 0L ? this.progress : this.total, this.total);
        return this.delegate.doFinal(temp, request);
    }

    @Override
    public void deinit(A temp, Request<V> request) throws Exception {
        this.delegate.doFinal(temp, request);
    }
}
