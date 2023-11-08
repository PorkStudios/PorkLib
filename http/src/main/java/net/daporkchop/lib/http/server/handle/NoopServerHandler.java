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

package net.daporkchop.lib.http.server.handle;

import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.http.HttpMethod;
import net.daporkchop.lib.http.header.map.HeaderMap;
import net.daporkchop.lib.http.message.Message;
import net.daporkchop.lib.http.request.query.Query;
import net.daporkchop.lib.http.server.ResponseBuilder;
import net.daporkchop.lib.http.util.StatusCodes;
import net.daporkchop.lib.http.util.exception.GenericHttpException;

/**
 * An implementation of {@link ServerHandler} that simply replies with {@link StatusCodes#OK}.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NoopServerHandler implements ServerHandler {
    public static final NoopServerHandler INSTANCE = new NoopServerHandler();

    @Override
    public int maxBodySize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handleQuery(@NonNull Query query) throws Exception {
        if (query.method() != HttpMethod.GET)   {
            throw StatusCodes.METHOD_NOT_ALLOWED.exception();
        }
    }

    @Override
    public void handle(@NonNull Query query, @NonNull Message message, @NonNull ResponseBuilder response) throws Exception {
        response.status(StatusCodes.OK);
    }
}
