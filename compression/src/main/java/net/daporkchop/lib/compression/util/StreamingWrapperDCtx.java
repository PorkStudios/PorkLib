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

package net.daporkchop.lib.compression.util;

import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.compression.context.DCtx;
import net.daporkchop.lib.compression.context.PInflater;
import net.daporkchop.lib.compression.provider.StreamingCompressionProvider;
import net.daporkchop.lib.compression.util.exception.DictionaryNotAllowedException;
import net.daporkchop.lib.natives.util.exception.InvalidBufferTypeException;
import net.daporkchop.lib.unsafe.util.AbstractReleasable;

/**
 * An implementation of {@link DCtx} that wraps a {@link PInflater}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Accessors(fluent = true)
public class StreamingWrapperDCtx extends AbstractReleasable implements DCtx {
    @Getter
    protected final StreamingCompressionProvider provider;
    @NonNull
    protected final PInflater inflater;

    public StreamingWrapperDCtx(@NonNull StreamingCompressionProvider provider) {
        this(provider, provider.inflater());
    }

    @Override
    public boolean decompress(@NonNull ByteBuf src, @NonNull ByteBuf dst) throws InvalidBufferTypeException {
        return this.inflater.reset().fullInflate(src, dst);
    }

    @Override
    public boolean decompress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict) throws InvalidBufferTypeException, DictionaryNotAllowedException {
        if (dict != null && !this.hasDict())    {
            throw new DictionaryNotAllowedException();
        }

        this.inflater.reset();
        if (dict != null) {
            this.inflater.dict(dict);
        }
        return this.inflater.fullInflate(src, dst);
    }

    @Override
    public boolean hasDict() {
        return this.inflater.hasDict();
    }

    @Override
    public boolean directAccepted() {
        return this.inflater.directAccepted();
    }

    @Override
    public boolean heapAccepted() {
        return this.inflater.heapAccepted();
    }

    @Override
    public boolean isAcceptable(@NonNull ByteBuf buf) {
        return this.inflater.isAcceptable(buf);
    }

    @Override
    public ByteBuf assertAcceptable(@NonNull ByteBuf buf) throws InvalidBufferTypeException {
        return this.inflater.assertAcceptable(buf);
    }

    @Override
    protected void doRelease() {
        this.inflater.release();
    }
}
