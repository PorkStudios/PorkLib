/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.compression.zstd.natives;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.compression.PDeflater;
import net.daporkchop.lib.compression.PInflater;
import net.daporkchop.lib.compression.util.exception.InvalidCompressionLevelException;
import net.daporkchop.lib.compression.zstd.Zstd;
import net.daporkchop.lib.compression.zstd.ZstdCCtx;
import net.daporkchop.lib.compression.zstd.ZstdCDict;
import net.daporkchop.lib.compression.zstd.ZstdDCtx;
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
        return this.doCompressBoundLong(PValidation.ensureNonNegative(srcSize));
    }

    private native long doCompressBoundLong(long srcSize);

    @Override
    public PDeflater deflater(int level) throws InvalidCompressionLevelException {
        throw new UnsupportedOperationException();
    }

    @Override
    public PInflater inflater() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ZstdCCtx compressionContext(int level) throws InvalidCompressionLevelException {
        return new NativeZstdCCtx(this, level);
    }

    @Override
    public ZstdDCtx decompressionContext() {
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
