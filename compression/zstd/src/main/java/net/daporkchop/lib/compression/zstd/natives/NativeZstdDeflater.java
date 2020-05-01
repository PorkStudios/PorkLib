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
import io.netty.buffer.ByteBufAllocator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.compression.util.exception.DictionaryNotAllowedException;
import net.daporkchop.lib.compression.zstd.Zstd;
import net.daporkchop.lib.compression.zstd.ZstdDeflateDictionary;
import net.daporkchop.lib.compression.zstd.ZstdDeflater;
import net.daporkchop.lib.compression.zstd.options.ZstdDeflaterOptions;
import net.daporkchop.lib.unsafe.PCleaner;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.io.IOException;
import java.util.ConcurrentModificationException;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
@SuppressWarnings("Duplicates")
final class NativeZstdDeflater extends AbstractRefCounted.Synchronized implements ZstdDeflater {
    static native long allocate0();

    static native void release0(long ctx);

    static native long newSession0(long ctx);

    static native long compressD2D0(long ctx, long src, int srcLen, long dst, int dstLen, int level);

    static native long compressD2H0(long ctx, long src, int srcLen, byte[] dst, int dstOff, int dstLen, int level);

    static native long compressH2D0(long ctx, byte[] src, int srcOff, int srcLen, long dst, int dstLen, int level);

    static native long compressH2H0(long ctx, byte[] src, int srcOff, int srcLen, byte[] dst, int dstOff, int dstLen, int level);

    static native long compressD2DD0(long ctx, long src, int srcLen, long dst, int dstLen, long dict);

    static native long compressD2HD0(long ctx, long src, int srcLen, byte[] dst, int dstOff, int dstLen, long dict);

    static native long compressH2DD0(long ctx, byte[] src, int srcOff, int srcLen, long dst, int dstLen, long dict);

    static native long compressH2HD0(long ctx, byte[] src, int srcOff, int srcLen, byte[] dst, int dstOff, int dstLen, long dict);

    final long ctx;

    @Getter
    final ZstdDeflaterOptions options;

    final PCleaner cleaner;

    NativeZstdDeflater(@NonNull ZstdDeflaterOptions options) {
        this.options = options;

        this.ctx = allocate0();
        this.cleaner = PCleaner.cleaner(this, new Releaser(this.ctx));
    }

    @Override
    protected void doRelease() {
        this.cleaner.clean();
    }

    @Override
    public synchronized boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict, int level) {
        this.ensureNotReleased();
        Zstd.checkLevel(level);

        if (!(src.hasMemoryAddress() || src.hasArray()) || !(dst.hasMemoryAddress() || dst.hasArray())) {
            //the other cases are too much of a pain to implement by hand
            int srcReaderIndex = src.readerIndex();
            int dstWriterIndex = dst.writerIndex();
            try (DataOut out = this.compressionStream(DataOut.wrap(dst, true, false), dict/*, level*/)) { //TODO: allow setting stream level
                out.write(src);
                return true;
            } catch (IOException e) {
                //shouldn't be possible
                throw new RuntimeException(e);
            } catch (IndexOutOfBoundsException e) {
                src.readerIndex(srcReaderIndex);
                dst.writerIndex(dstWriterIndex);
                return false;
            }
        }

        if (dict != null)   {
            //dictionary is set, digest it first and then do compression
            //TODO: implement the un-digested dictionary compression methods
            try (ZstdDeflateDictionary digested = this.options.provider().loadDeflateDictionary(dict, level))   {
                return this.compress(src, dst, digested);
            }
        }

        long session = newSession0(this.ctx);
        try {
            long ret = -1L;
            if (src.hasMemoryAddress()) {
                if (dst.hasMemoryAddress()) {
                    ret = compressD2D0(this.ctx,
                            src.memoryAddress() + src.readerIndex(), src.readableBytes(),
                            dst.memoryAddress() + dst.writerIndex(), dst.writableBytes(),
                            level);
                } else if (dst.hasArray())  {
                    ret = compressD2H0(this.ctx,
                            src.memoryAddress() + src.readerIndex(), src.readableBytes(),
                            dst.array(), dst.arrayOffset() + dst.writerIndex(), dst.writableBytes(),
                            level);
                }
            } else if (src.hasArray())  {
                if (dst.hasMemoryAddress()) {
                    ret = compressH2D0(this.ctx,
                            src.array(), src.arrayOffset() + src.readerIndex(), src.readableBytes(),
                            dst.memoryAddress() + dst.writerIndex(), dst.writableBytes(),
                            level);
                } else if (dst.hasArray())  {
                    ret = compressH2H0(this.ctx,
                            src.array(), src.arrayOffset() + src.readerIndex(), src.readableBytes(),
                            dst.array(), dst.arrayOffset() + dst.writerIndex(), dst.writableBytes(),
                            level);
                }
            }

            if (ret >= 0L)   {
                src.skipBytes(src.readableBytes());
                dst.writerIndex(dst.writerIndex() + toInt(ret));
                return true;
            } else {
                return false;
            }
        } finally {
            if (session != this.getSession()) {
                throw new ConcurrentModificationException(); //probably impossible
            }
        }
    }

    @Override
    public synchronized boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ZstdDeflateDictionary dict) {
        this.ensureNotReleased();
        if (dict == null)   {
            return this.compress(src, dst, null, this.options.level());
        }

        if (!(src.hasMemoryAddress() || src.hasArray()) || !(dst.hasMemoryAddress() || dst.hasArray())) {
            //the other cases are too much of a pain to implement by hand
            int srcReaderIndex = src.readerIndex();
            int dstWriterIndex = dst.writerIndex();
            try (DataOut out = this.compressionStream(DataOut.wrap(dst, true, false)/*, dict*/)) { //TODO allow setting stream dictionary
                out.write(src);
                return true;
            } catch (IOException e) {
                //shouldn't be possible
                throw new RuntimeException(e);
            } catch (IndexOutOfBoundsException e) {
                src.readerIndex(srcReaderIndex);
                dst.writerIndex(dstWriterIndex);
                return false;
            }
        }

        checkArg(dict instanceof NativeZstdDeflateDictionary, "invalid dictionary: %s", dict);
        long dictAddr = ((NativeZstdDeflateDictionary) dict.retain()).addr();

        long session = newSession0(this.ctx);
        try {
            long ret = -1L;
            if (src.hasMemoryAddress()) {
                if (dst.hasMemoryAddress()) {
                    ret = compressD2DD0(this.ctx,
                            src.memoryAddress() + src.readerIndex(), src.readableBytes(),
                            dst.memoryAddress() + dst.writerIndex(), dst.writableBytes(),
                            dictAddr);
                } else if (dst.hasArray())  {
                    ret = compressD2HD0(this.ctx,
                            src.memoryAddress() + src.readerIndex(), src.readableBytes(),
                            dst.array(), dst.arrayOffset() + dst.writerIndex(), dst.writableBytes(),
                            dictAddr);
                }
            } else if (src.hasArray())  {
                if (dst.hasMemoryAddress()) {
                    ret = compressH2DD0(this.ctx,
                            src.array(), src.arrayOffset() + src.readerIndex(), src.readableBytes(),
                            dst.memoryAddress() + dst.writerIndex(), dst.writableBytes(),
                            dictAddr);
                } else if (dst.hasArray())  {
                    ret = compressH2HD0(this.ctx,
                            src.array(), src.arrayOffset() + src.readerIndex(), src.readableBytes(),
                            dst.array(), dst.arrayOffset() + dst.writerIndex(), dst.writableBytes(),
                            dictAddr);
                }
            }

            if (ret >= 0L)   {
                src.skipBytes(src.readableBytes());
                dst.writerIndex(dst.writerIndex() + toInt(ret));
                return true;
            } else {
                return false;
            }
        } finally {
            dict.release();
            if (session != this.getSession()) {
                throw new ConcurrentModificationException(); //probably impossible
            }
        }
    }

    @Override
    public synchronized void compressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict, int level) throws IndexOutOfBoundsException {
        this.ensureNotReleased();
        Zstd.checkLevel(level);
    }

    @Override
    public synchronized void compressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst, ZstdDeflateDictionary dict) throws IndexOutOfBoundsException {
        this.ensureNotReleased();
    }

    @Override
    public synchronized DataOut compressionStream(@NonNull DataOut out, ByteBufAllocator bufferAlloc, int bufferSize, ByteBuf dict) throws IOException, DictionaryNotAllowedException {
        this.ensureNotReleased();
        throw new UnsupportedOperationException();
    }

    @Override
    public NativeZstdDeflater retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }

    protected long getRead() {
        return PUnsafe.getLongVolatile(null, this.ctx);
    }

    protected long getWritten() {
        return PUnsafe.getLongVolatile(null, this.ctx + 8L);
    }

    protected long getSession() {
        return PUnsafe.getLongVolatile(null, this.ctx + 16L);
    }

    @AllArgsConstructor
    private static final class Releaser implements Runnable {
        protected final long ctx;

        @Override
        public void run() {
            release0(this.ctx);
        }
    }
}
