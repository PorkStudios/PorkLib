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

package net.daporkchop.lib.compression.zstd.air;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.compression.zstd.ZstdDeflateDictionary;
import net.daporkchop.lib.compression.zstd.ZstdDeflater;
import net.daporkchop.lib.compression.zstd.options.ZstdDeflaterOptions;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.io.IOException;
import java.nio.ByteBuffer;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Accessors(fluent = true)
final class AirZstdDeflater extends AbstractRefCounted implements ZstdDeflater {
    @NonNull
    final AirZstd provider;
    @Getter
    @NonNull
    final ZstdDeflaterOptions options;

    @Override
    public boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict, int level) {
        checkArg(dict == null, "dictionary not supported!");
        return this.compress0(src, dst);
    }

    @Override
    public boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ZstdDeflateDictionary dict) {
        checkArg(dict == null, "dictionary not supported!");
        return this.compress0(src, dst);
    }

    protected boolean compress0(@NonNull ByteBuf src, @NonNull ByteBuf dst) {
        ByteBuffer srcBuf = src.nioBuffer();
        ByteBuffer dstBuf = dst.nioBuffer(dst.writerIndex(), dst.writableBytes());
        try {
            this.provider.compressor.compress(srcBuf, dstBuf);
            src.skipBytes(src.readableBytes());
            dst.writerIndex(dst.writerIndex() + dstBuf.flip().remaining());
            return true;
        } catch (IllegalArgumentException e) {
            if (e.getMessage() == "Output buffer too small") {
                return false;
            } else {
                throw e;
            }
        }
    }

    @Override
    public void compressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict, int level) throws IndexOutOfBoundsException {
    }

    @Override
    public void compressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst, ZstdDeflateDictionary dict) throws IndexOutOfBoundsException {
    }

    @Override
    public DataOut compressionStream(@NonNull DataOut out, ByteBufAllocator bufferAlloc, int bufferSize, ByteBuf dict, int level) throws IOException {
        return null;
    }

    @Override
    public DataOut compressionStream(@NonNull DataOut out, ByteBufAllocator bufferAlloc, int bufferSize, ZstdDeflateDictionary dict) throws IOException {
        return null;
    }

    @Override
    public AirZstdDeflater retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }

    @Override
    protected void doRelease() {
    }
}
