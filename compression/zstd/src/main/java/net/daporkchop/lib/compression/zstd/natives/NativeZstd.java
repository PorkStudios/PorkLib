/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
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
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.pool.recycler.Recycler;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.compression.zstd.Zstd;
import net.daporkchop.lib.compression.zstd.ZstdDeflateDictionary;
import net.daporkchop.lib.compression.zstd.ZstdDeflater;
import net.daporkchop.lib.compression.zstd.ZstdInflateDictionary;
import net.daporkchop.lib.compression.zstd.ZstdInflater;
import net.daporkchop.lib.compression.zstd.ZstdProvider;
import net.daporkchop.lib.compression.zstd.options.ZstdDeflaterOptions;
import net.daporkchop.lib.compression.zstd.options.ZstdInflaterOptions;
import net.daporkchop.lib.compression.zstd.util.exception.ContentSizeUnknownException;
import net.daporkchop.lib.natives.NativeException;
import net.daporkchop.lib.natives.NativeFeature;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.nio.ByteBuffer;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
final class NativeZstd extends NativeFeature<ZstdProvider> implements ZstdProvider {
    private static native long frameContentSize0(long src, int srcLen);

    private static native long compressBound0(long srcLen);

    static final int ZSTD_e_continue = 0;
    static final int ZSTD_e_flush = 1;
    static final int ZSTD_e_end = 2;

    static final long ZSTD_CONTENTSIZE_UNKNOWN = -1L;
    static final long ZSTD_CONTENTSIZE_ERROR = -2L;

    static final int FRAME_HEADER_SIZE_MAX = 18;

    static {
        if (PorkUtil.bufferSize() < FRAME_HEADER_SIZE_MAX) {
            throw new AssertionError("PorkUtil.bufferSize() must be at least " + FRAME_HEADER_SIZE_MAX);
        }
    }

    protected final ZstdDeflaterOptions deflateOptions = new ZstdDeflaterOptions(this);
    protected final ZstdInflaterOptions inflateOptions = new ZstdInflaterOptions(this);

    @Override
    public long frameContentSizeLong(@NonNull ByteBuf src) throws ContentSizeUnknownException {
        checkArg(src.isReadable(), "src is not readable!");
        long contentSize;
        if (src.hasMemoryAddress()) {
            contentSize = frameContentSize0(src.memoryAddress() + src.readerIndex(), src.readableBytes());
        } else {
            Recycler<ByteBuffer> recycler = PorkUtil.directBufferRecycler();
            ByteBuffer buf = recycler.allocate();

            buf.limit(min(src.readableBytes(), FRAME_HEADER_SIZE_MAX));
            src.getBytes(src.readerIndex(), buf);
            contentSize = frameContentSize0(PUnsafe.pork_directBufferAddress(buf), buf.limit());

            recycler.release(buf); //release the buffer to the recycler
        }

        if (contentSize >= 0L) {
            return contentSize;
        } else if (contentSize == ZSTD_CONTENTSIZE_UNKNOWN) {
            throw new ContentSizeUnknownException();
        } else if (contentSize == ZSTD_CONTENTSIZE_ERROR) {
            throw new NativeException("ZSTD_CONTENTSIZE_ERROR", contentSize);
        } else {
            throw new NativeException(contentSize);
        }
    }

    @Override
    public long compressBoundLong(long srcSize) {
        return compressBound0(notNegative(srcSize, "srcSize"));
    }

    @Override
    public ZstdDeflater deflater(@NonNull ZstdDeflaterOptions options) {
        checkArg(options.provider() == this, "provider must be %s!", this);
        return new NativeZstdDeflater(options);
    }

    @Override
    public ZstdInflater inflater(@NonNull ZstdInflaterOptions options) {
        checkArg(options.provider() == this, "provider must be %s!", this);
        return new NativeZstdInflater(options);
    }

    @Override
    public ZstdDeflateDictionary loadDeflateDictionary(@NonNull ByteBuf dict, int level) {
        return new NativeZstdDeflateDictionary(this, dict, Zstd.checkLevel(level));
    }

    @Override
    public ZstdInflateDictionary loadInflateDictionary(@NonNull ByteBuf dict) {
        return new NativeZstdInflateDictionary(this, dict);
    }
}
