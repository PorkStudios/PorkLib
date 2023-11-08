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

package net.daporkchop.lib.http.entity;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.http.entity.content.type.ContentType;
import net.daporkchop.lib.http.entity.content.type.StandardContentType;
import net.daporkchop.lib.http.entity.transfer.TransferSession;
import net.daporkchop.lib.http.entity.transfer.encoding.StandardTransferEncoding;
import net.daporkchop.lib.http.entity.transfer.encoding.TransferEncoding;

import java.nio.channels.WritableByteChannel;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public final class EmptyHttpEntity implements TransferSession, HttpEntity {
    public static final EmptyHttpEntity INSTANCE = new EmptyHttpEntity(StandardContentType.TEXT_PLAIN);

    @NonNull
    protected final ContentType type;

    @Override
    public TransferSession newSession() throws Exception {
        return this;
    }

    @Override
    public long position() throws Exception {
        return 0L;
    }

    @Override
    public long length() throws Exception {
        return 0L;
    }

    @Override
    public TransferEncoding transferEncoding() throws Exception {
        return StandardTransferEncoding.identity;
    }

    @Override
    public long transfer(long position, @NonNull WritableByteChannel out) throws Exception {
        if (position != 0L) {
            throw new IndexOutOfBoundsException(String.valueOf(position));
        }
        return 0L;
    }

    @Override
    public long transferAllBlocking(long position, @NonNull WritableByteChannel out) throws Exception {
        if (position != 0L) {
            throw new IndexOutOfBoundsException(String.valueOf(position));
        }
        return 0L;
    }

    @Override
    public boolean hasByteBuf() {
        return true;
    }

    @Override
    public ByteBuf getByteBuf() throws Exception {
        return Unpooled.EMPTY_BUFFER;
    }

    @Override
    public boolean reusable() {
        return true;
    }
}
