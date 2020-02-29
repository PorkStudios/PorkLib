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

package net.daporkchop.lib.http.response.aggregate;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.http.request.Request;
import net.daporkchop.lib.http.response.ResponseHeaders;

/**
 * Base implementation of a {@link ResponseAggregator} that uses a {@link ByteBuf} as a temporary value.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public abstract class AbstractByteBufAggregator<V> implements ResponseAggregator<ByteBuf, V> {
    @NonNull
    protected final ByteBufAllocator alloc;

    public AbstractByteBufAggregator() {
        this(PooledByteBufAllocator.DEFAULT);
    }

    @Override
    public ByteBuf init(@NonNull ResponseHeaders response, @NonNull Request<V> request) throws Exception {
        long length = response.contentLength();
        if (length < 0L)    {
            return this.alloc.ioBuffer();
        } else if (length > Integer.MAX_VALUE)  {
            throw new IllegalArgumentException(String.format("Content-Length %d is too large!", length));
        } else {
            return this.alloc.ioBuffer((int) length, (int) length);
        }
    }

    @Override
    public ByteBuf add(@NonNull ByteBuf temp, @NonNull ByteBuf data, @NonNull Request<V> request) throws Exception {
        return temp.writeBytes(data);
    }

    @Override
    public void deinit(@NonNull ByteBuf temp, @NonNull Request<V> request) throws Exception {
        temp.release();
    }
}
