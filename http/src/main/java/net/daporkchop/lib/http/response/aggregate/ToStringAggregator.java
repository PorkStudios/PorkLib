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
import io.netty.buffer.ByteBufAllocator;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.http.request.Request;

import java.nio.charset.Charset;

/**
 * Aggregates received data into a {@link String}.
 *
 * @author DaPorkchop_
 */
//TODO: detect charset from response headers
//TODO: that means i'll probably need to come up with some way of storing custom values on a request instance (attribute map)
@Getter
@Accessors(fluent = true)
public final class ToStringAggregator extends AbstractByteBufAggregator<String> {
    protected final Charset charset;

    public ToStringAggregator(@NonNull ByteBufAllocator alloc, @NonNull Charset charset) {
        super(alloc);

        this.charset = charset;
    }

    public ToStringAggregator(@NonNull Charset charset) {
        super();

        this.charset = charset;
    }

    @Override
    public String doFinal(@NonNull ByteBuf temp, @NonNull Request<String> request) throws Exception {
        return temp.toString(this.charset);
    }
}
