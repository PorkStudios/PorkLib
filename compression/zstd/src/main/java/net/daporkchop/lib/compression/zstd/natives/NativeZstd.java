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

package net.daporkchop.lib.compression.zstd.natives;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.compression.util.exception.InvalidCompressionLevelException;
import net.daporkchop.lib.compression.zstd.Zstd;
import net.daporkchop.lib.compression.zstd.ZstdDeflater;
import net.daporkchop.lib.compression.zstd.ZstdCDict;
import net.daporkchop.lib.compression.zstd.ZstdInflater;
import net.daporkchop.lib.compression.zstd.ZstdDDict;
import net.daporkchop.lib.compression.zstd.ZstdProvider;
import net.daporkchop.lib.compression.zstd.util.exception.ContentSizeUnknownException;
import net.daporkchop.lib.natives.impl.NativeFeature;
import net.daporkchop.lib.natives.util.exception.InvalidBufferTypeException;

/**
 * @author DaPorkchop_
 */
public final class NativeZstd extends NativeFeature<ZstdProvider> implements ZstdProvider {
    @Override
    public boolean directAccepted() {
        return true;
    }

    @Override
    public int levelFast() {
        return Zstd.LEVEL_MIN;
    }

    @Override
    public int levelDefault() {
        return Zstd.LEVEL_DEFAULT;
    }

    @Override
    public int levelBest() {
        return Zstd.LEVEL_MAX;
    }

    @Override
    public boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst, int compressionLevel) throws InvalidBufferTypeException {
        int val = this.doCompress(this.assertAcceptable(src).memoryAddress() + src.readerIndex(), src.readableBytes(),
                this.assertAcceptable(dst).memoryAddress() + dst.writerIndex(), dst.writableBytes(),
                compressionLevel);

        return NativeZstdHelper.finalizeOneShot(src, dst, val);
    }

    private native int doCompress(long srcAddr, int srcSize, long dstAddr, int dstSize, int compressionLevel);

    @Override
    public boolean decompress(@NonNull ByteBuf src, @NonNull ByteBuf dst) throws InvalidBufferTypeException {
        int val = this.doDecompress(this.assertAcceptable(src).memoryAddress() + src.readerIndex(), src.readableBytes(),
                this.assertAcceptable(dst).memoryAddress() + dst.writerIndex(), dst.writableBytes());

        return NativeZstdHelper.finalizeOneShot(src, dst, val);
    }

    private native int doDecompress(long srcAddr, int srcSize, long dstAddr, int dstSize);

    @Override
    public long frameContentSizeLong(@NonNull ByteBuf src) throws InvalidBufferTypeException, ContentSizeUnknownException {
        long size = this.doFrameContentSizeLong(this.assertAcceptable(src).memoryAddress() + src.readerIndex(), src.readableBytes());
        if (size >= 0L) {
            return size;
        } else {
            throw new ContentSizeUnknownException();
        }
    }

    private native long doFrameContentSizeLong(long srcAddr, int srcSize);

    @Override
    public long compressBoundLong(long srcSize) {
        return this.doCompressBoundLong(PValidation.notNegative(srcSize));
    }

    private native long doCompressBoundLong(long srcSize);

    @Override
    public ZstdDeflater compressionContext(int level) throws InvalidCompressionLevelException {
        return new NativeZstdCCtx(this, level);
    }

    @Override
    public ZstdInflater decompressionContext() {
        return new NativeZstdDCtx(this);
    }

    @Override
    public ZstdCDict compressionDictionary(@NonNull ByteBuf dict, int level) throws InvalidBufferTypeException {
        return new NativeZstdCDict(this, dict, level, true);
    }

    @Override
    public ZstdDDict decompressionDictionary(@NonNull ByteBuf dict) throws InvalidBufferTypeException {
        return new NativeZstdDDict(this, dict, true);
    }
}
