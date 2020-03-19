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

package net.daporkchop.lib.http.message.body;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.http.request.Request;
import net.daporkchop.lib.http.response.ResponseHeaders;
import net.daporkchop.lib.http.util.ProgressHandler;

/**
 * A {@link BodyAggregator} that notifies an external function of the current download progress before delegating data events on to another
 * aggregator.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public final class NotifyingAggregator<A, V> implements BodyAggregator<A, V> {
    protected long progress = 0L;
    protected long total = -1L;

    @NonNull
    protected final BodyAggregator<A, V> delegate;
    @NonNull
    protected final ProgressHandler      handler;

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
