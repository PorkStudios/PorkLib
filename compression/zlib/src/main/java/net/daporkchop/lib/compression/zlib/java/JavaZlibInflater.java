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

package net.daporkchop.lib.compression.zlib.java;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.compression.util.exception.DictionaryNotAllowedException;
import net.daporkchop.lib.compression.zlib.ZlibInflater;
import net.daporkchop.lib.compression.zlib.ZlibMode;
import net.daporkchop.lib.compression.zlib.options.ZlibInflaterOptions;
import net.daporkchop.lib.common.util.exception.AlreadyReleasedException;

import java.io.IOException;
import java.util.zip.Inflater;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
final class JavaZlibInflater extends AbstractRefCounted.Synchronized implements ZlibInflater {
    long sessionCounter = 0L;

    final Inflater inflater;

    @Getter
    final ZlibInflaterOptions options;

    JavaZlibInflater(@NonNull ZlibInflaterOptions options) {
        this.options = options;
        this.inflater = new Inflater(options.mode() != ZlibMode.ZLIB);
    }

    @Override
    protected void doRelease() {
        this.inflater.end();
    }

    @Override
    public boolean decompress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict) {
        return this.decompress0(src, dst, dict, false);
    }

    @Override
    public void decompressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict) throws IndexOutOfBoundsException {
        checkState(this.decompress0(src, dst, dict, true), "decompress0() returned false when it should have grown!");
    }

    protected synchronized boolean decompress0(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict, boolean grow) {
        checkArg(src.isReadable(), "src is not readable!");
        checkArg(dst.isWritable(), "dst is not writable!");
        int srcReaderIndex = src.readerIndex();
        int dstWriterIndex = dst.writerIndex();
        try (DataIn in = this.decompressionStream(DataIn.wrap(src), dict);
             DataOut out = DataOut.wrap(dst, true, grow)) {
            in.transferTo(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (IndexOutOfBoundsException e) {
            if (grow) {
                throw new IllegalStateException(e); //shouldn't be possible...
            } else {
                src.readerIndex(srcReaderIndex);
                dst.writerIndex(dstWriterIndex);
                return false;
            }
        }
        return true;
    }

    @Override
    public synchronized DataIn decompressionStream(@NonNull DataIn in, ByteBufAllocator bufferAlloc, int bufferSize, ByteBuf dict) throws IOException, DictionaryNotAllowedException {
        this.ensureNotReleased();
        if (bufferAlloc == null) {
            bufferAlloc = PooledByteBufAllocator.DEFAULT;
        }
        if (bufferSize <= 0) {
            bufferSize = PorkUtil.BUFFER_SIZE;
        }

        switch (this.options.mode()) {
            case ZLIB:
            case RAW:
                return new JavaZlibInflateStream(in, bufferAlloc.heapBuffer(bufferSize, bufferSize), dict, this);
            case GZIP:
                return new JavaGzipInflateStream(in, bufferAlloc.heapBuffer(bufferSize, bufferSize), dict, this);
            default:
                throw new IllegalArgumentException(this.options.mode().name());
        }
    }

    @Override
    public JavaZlibInflater retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }
}
