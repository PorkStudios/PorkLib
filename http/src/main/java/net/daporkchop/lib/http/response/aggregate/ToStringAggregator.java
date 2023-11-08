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
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.http.entity.content.type.ContentType;
import net.daporkchop.lib.http.header.map.HeaderMap;
import net.daporkchop.lib.http.request.Request;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Aggregates received data into a {@link String}.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public final class ToStringAggregator extends AbstractByteBufAggregator<String> {
    public ToStringAggregator(@NonNull ByteBufAllocator alloc) {
        super(alloc);
    }

    public ToStringAggregator() {
    }

    @Override
    public String doFinal(@NonNull ByteBuf temp, @NonNull Request<String> request) throws Exception {
        HeaderMap headers = request.headersFuture().getNow().headers();
        Charset charset = StandardCharsets.UTF_8;
        if (headers.hasKey("content-type"))  {
            ContentType type = ContentType.parse(headers.getValue("content-type"));
            if (type.charsetName() != null) {
                charset = Charset.forName(type.charsetName());
            }
        }
        return temp.toString(charset);
    }
}
