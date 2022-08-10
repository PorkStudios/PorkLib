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
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.compression.util.exception.DictionaryNotAllowedException;
import net.daporkchop.lib.compression.zlib.ZlibDeflater;
import net.daporkchop.lib.compression.zlib.ZlibMode;
import net.daporkchop.lib.compression.zlib.options.ZlibDeflaterOptions;
import net.daporkchop.lib.common.util.exception.AlreadyReleasedException;

import java.io.IOException;
import java.util.zip.Deflater;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
final class JavaZlibDeflater extends AbstractRefCounted.Synchronized implements ZlibDeflater {
    long sessionCounter = 0L;

    final Deflater deflater;

    @Getter
    final ZlibDeflaterOptions options;

    JavaZlibDeflater(@NonNull ZlibDeflaterOptions options) {
        this.options = options;
        this.deflater = new Deflater(options.level(), options.mode() != ZlibMode.ZLIB);
        this.deflater.setStrategy(options.strategy().ordinal());
    }

    @Override
    protected void doRelease() {
        this.deflater.end();
    }

    @Override
    public boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict) {
        return this.compress0(src, dst, dict, false);
    }

    @Override
    public void compressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict) throws IndexOutOfBoundsException {
        checkState(this.compress0(src, dst, dict, true), "compress0() returned false when it should have grown!");
    }

    protected synchronized boolean compress0(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict, boolean grow) {
        checkArg(src.isReadable(), "src is not readable!");
        checkArg(dst.isWritable(), "dst is not writable!");
        int srcReaderIndex = src.readerIndex();
        int dstWriterIndex = dst.writerIndex();
        try (DataOut out = this.compressionStream(DataOut.wrap(dst, true, grow), dict)) {
            out.write(src);
        } catch (IOException e) {
            //shouldn't be possible
            throw new RuntimeException(e);
        } catch (IndexOutOfBoundsException e) {
            if (grow) {
                //this means that the buffer got way too big, should be re-thrown
                throw e;
            } else {
                src.readerIndex(srcReaderIndex);
                dst.writerIndex(dstWriterIndex);
                return false;
            }
        }
        return true;
    }

    @Override
    public synchronized DataOut compressionStream(@NonNull DataOut out, ByteBufAllocator bufferAlloc, int bufferSize, ByteBuf dict) throws DictionaryNotAllowedException {
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
                return new JavaZlibDeflateStream(out, bufferAlloc.heapBuffer(bufferSize, bufferSize), dict, this);
            case GZIP:
                return new JavaGzipDeflateStream(out, bufferAlloc.heapBuffer(bufferSize, bufferSize), dict, this);
            default:
                throw new IllegalArgumentException(this.options.mode().name());
        }
    }

    @Override
    public JavaZlibDeflater retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }
}
