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

import io.airlift.compress.MalformedInputException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.compression.zstd.ZstdInflateDictionary;
import net.daporkchop.lib.compression.zstd.ZstdInflater;
import net.daporkchop.lib.compression.zstd.options.ZstdInflaterOptions;
import net.daporkchop.lib.compression.zstd.util.exception.ContentSizeUnknownException;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.io.IOException;
import java.nio.ByteBuffer;

import static java.lang.Math.min;
import static net.daporkchop.lib.common.util.PValidation.checkArg;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Accessors(fluent = true)
final class AirZstdInflater extends AbstractRefCounted implements ZstdInflater {
    @NonNull
    final AirZstd provider;
    @Getter
    @NonNull
    final ZstdInflaterOptions options;

    @Override
    public boolean decompress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict) {
        checkArg(dict == null, "dictionary not supported!");
        return this.decompress0(src, dst);
    }

    @Override
    public boolean decompress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ZstdInflateDictionary dict) {
        checkArg(dict == null, "dictionary not supported!");
        return this.decompress0(src, dst);
    }

    protected boolean decompress0(@NonNull ByteBuf src, @NonNull ByteBuf dst) {
        ByteBuffer srcBuf = src.nioBuffer();
        ByteBuffer dstBuf = dst.nioBuffer(dst.writerIndex(), dst.writableBytes());
        try {
            this.provider.decompressor.decompress(srcBuf, dstBuf);
            src.skipBytes(src.readableBytes());
            dst.writerIndex(dst.writerIndex() + dstBuf.flip().remaining());
            return true;
        } catch (IllegalArgumentException | MalformedInputException e) {
            if (e.getMessage().contains("Output buffer too small")) {
                return false;
            } else {
                throw e;
            }
        }
    }

    @Override
    public void decompressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict) throws IndexOutOfBoundsException {
        checkArg(dict == null, "dictionary not supported!");
        this.decompressGrowing0(src, dst);
    }

    @Override
    public void decompressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst, ZstdInflateDictionary dict) throws IndexOutOfBoundsException {
        checkArg(dict == null, "dictionary not supported!");
        this.decompressGrowing0(src, dst);
    }

    protected void decompressGrowing0(@NonNull ByteBuf src, @NonNull ByteBuf dst) {
        int curr = 256;
        try {
            curr = this.provider.frameContentSize(src);
        } catch (ContentSizeUnknownException e) {
            //ignore
        }

        ByteBuffer srcBuf = src.nioBuffer();

        while (true) {
            dst.ensureWritable(curr); //grow dst buffer as needed
            ByteBuffer dstBuf = dst.nioBuffer(dst.writerIndex(), dst.writableBytes());
            try {
                this.provider.decompressor.decompress(srcBuf, dstBuf);
                src.skipBytes(src.readableBytes());
                dst.writerIndex(dst.writerIndex() + dstBuf.flip().remaining());
                return;
            } catch (IllegalArgumentException | MalformedInputException e) {
                if (!e.getMessage().contains("Output buffer too small")) {
                    throw e;
                }
            }

            curr <<= 1;
        }
    }

    @Override
    public DataIn decompressionStream(@NonNull DataIn in, ByteBufAllocator bufferAlloc, int bufferSize, ByteBuf dict) throws IOException {
        throw new UnsupportedOperationException("stream");
    }

    @Override
    public DataIn decompressionStream(@NonNull DataIn in, ByteBufAllocator bufferAlloc, int bufferSize, ZstdInflateDictionary dict) throws IOException {
        throw new UnsupportedOperationException("stream");
    }

    @Override
    public AirZstdInflater retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }

    @Override
    protected void doRelease() {
    }
}
