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

import io.airlift.compress.zstd.ZstdCompressor;
import io.airlift.compress.zstd.ZstdDecompressor;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.compression.zstd.ZstdDeflateDictionary;
import net.daporkchop.lib.compression.zstd.ZstdDeflater;
import net.daporkchop.lib.compression.zstd.ZstdInflateDictionary;
import net.daporkchop.lib.compression.zstd.ZstdInflater;
import net.daporkchop.lib.compression.zstd.ZstdProvider;
import net.daporkchop.lib.compression.zstd.options.ZstdDeflaterOptions;
import net.daporkchop.lib.compression.zstd.options.ZstdInflaterOptions;
import net.daporkchop.lib.compression.zstd.util.exception.ContentSizeUnknownException;
import net.daporkchop.lib.natives.NativeException;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.lang.reflect.Method;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
final class AirZstd implements ZstdProvider {
    static final int FRAME_HEADER_SIZE_MAX = 18;

    protected final ZstdDeflaterOptions deflateOptions = new ZstdDeflaterOptions(this);
    protected final ZstdInflaterOptions inflateOptions = new ZstdInflaterOptions(this);

    protected final ZstdCompressor compressor = new ZstdCompressor();
    protected final ZstdDecompressor decompressor = new ZstdDecompressor();

    @Override
    public long frameContentSizeLong(@NonNull ByteBuf src) throws ContentSizeUnknownException {
        checkArg(src.isReadable(), "src is not readable!");

        byte[] srcArr;
        int srcOff;
        int srcLen;
        if (src.hasArray()) {
            srcArr = src.array();
            srcOff = src.arrayOffset() + src.readerIndex();
            srcLen = src.readableBytes();
        } else {
            src.getBytes(src.readerIndex(), srcArr = new byte[min(src.readableBytes(), FRAME_HEADER_SIZE_MAX)]);
            srcOff = 0;
            srcLen = srcArr.length;
        }

        long contentSize = ZstdDecompressor.getDecompressedSize(srcArr, srcOff, srcLen);
        if (contentSize >= 0L) {
            return contentSize;
        } else if (contentSize == -1L) {
            throw new ContentSizeUnknownException();
        } else {
            throw new NativeException(contentSize);
        }
    }

    @Override
    public long compressBoundLong(long srcSize) {
        return this.compressor.maxCompressedLength(toInt(srcSize, "srcSize"));
    }

    @Override
    public ZstdDeflater deflater(@NonNull ZstdDeflaterOptions options) {
        checkArg(options.provider() == this, "provider must be %s!", this);
        return new AirZstdDeflater(this, options);
    }

    @Override
    public ZstdInflater inflater(@NonNull ZstdInflaterOptions options) {
        checkArg(options.provider() == this, "provider must be %s!", this);
        return new AirZstdInflater(this, options);
    }

    @Override
    public ZstdDeflateDictionary loadDeflateDictionary(@NonNull ByteBuf dict, int level) {
        throw new UnsupportedOperationException("dictionary");
    }

    @Override
    public ZstdInflateDictionary loadInflateDictionary(@NonNull ByteBuf dict) {
        throw new UnsupportedOperationException("dictionary");
    }

    @Override
    public boolean isNative() {
        return false;
    }
}
