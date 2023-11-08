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
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.binary.util.NoMoreSpaceException;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.common.util.exception.AlreadyReleasedException;
import net.daporkchop.lib.compression.zstd.Zstd;
import net.daporkchop.lib.compression.zstd.ZstdDeflateDictionary;
import net.daporkchop.lib.compression.zstd.ZstdDeflater;
import net.daporkchop.lib.compression.zstd.options.ZstdDeflaterOptions;
import net.daporkchop.lib.unsafe.PCleaner;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;
import java.util.ConcurrentModificationException;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.compression.zstd.natives.NativeZstd.*;

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

    static native long compressD2DWithDict0(long ctx, long src, int srcLen, long dst, int dstLen, long dict);

    static native long compressD2HWithDict0(long ctx, long src, int srcLen, byte[] dst, int dstOff, int dstLen, long dict);

    static native long compressH2DWithDict0(long ctx, byte[] src, int srcOff, int srcLen, long dst, int dstLen, long dict);

    static native long compressH2HWithDict0(long ctx, byte[] src, int srcOff, int srcLen, byte[] dst, int dstOff, int dstLen, long dict);

    static native long compressD2DWithDictD0(long ctx, long src, int srcLen, long dst, int dstLen, long dict, int dictLen, int level);

    static native long compressD2HWithDictD0(long ctx, long src, int srcLen, byte[] dst, int dstOff, int dstLen, long dict, int dictLen, int level);

    static native long compressH2DWithDictD0(long ctx, byte[] src, int srcOff, int srcLen, long dst, int dstLen, long dict, int dictLen, int level);

    static native long compressH2HWithDictD0(long ctx, byte[] src, int srcOff, int srcLen, byte[] dst, int dstOff, int dstLen, long dict, int dictLen, int level);

    static native long compressD2DWithDictH0(long ctx, long src, int srcLen, long dst, int dstLen, byte[] dict, int dictOff, int dictLen, int level);

    static native long compressD2HWithDictH0(long ctx, long src, int srcLen, byte[] dst, int dstOff, int dstLen, byte[] dict, int dictOff, int dictLen, int level);

    static native long compressH2DWithDictH0(long ctx, byte[] src, int srcOff, int srcLen, long dst, int dstLen, byte[] dict, int dictOff, int dictLen, int level);

    static native long compressH2HWithDictH0(long ctx, byte[] src, int srcOff, int srcLen, byte[] dst, int dstOff, int dstLen, byte[] dict, int dictOff, int dictLen, int level);

    //streaming

    static native long newSessionWithLevel0(long ctx, int level);

    static native long newSessionWithDict0(long ctx, long dict);

    static native long newSessionWithDictD0(long ctx, long dict, int dictLen, int level);

    static native long newSessionWithDictH0(long ctx, byte[] dict, int dictOff, int dictLen, int level);

    static native long updateD2D0(long ctx, long src, int srcLen, long dst, int dstLen, int flush);

    static native long updateD2H0(long ctx, long src, int srcLen, byte[] dst, int dstOff, int dstLen, int flush);

    static native long updateH2D0(long ctx, byte[] src, int srcOff, int srcLen, long dst, int dstLen, int flush);

    static native long updateH2H0(long ctx, byte[] src, int srcOff, int srcLen, byte[] dst, int dstOff, int dstLen, int flush);

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
    @SneakyThrows(IOException.class)
    public synchronized boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict, int level) {
        this.ensureNotReleased();
        Zstd.checkLevel(level);

        if (!(src.hasMemoryAddress() || src.hasArray()) || !(dst.hasMemoryAddress() || dst.hasArray())) {
            //the other cases are too much of a pain to implement by hand
            int srcReaderIndex = src.readerIndex();
            int dstWriterIndex = dst.writerIndex();
            try (DataOut out = this.compressionStream(DataOut.wrapViewNonGrowing(dst), null, -1, dict, level)) {
                out.write(src);
                return true;
            } catch (NoMoreSpaceException e) { //buffer reached capacity and isn't allowed to grow
                src.readerIndex(srcReaderIndex);
                dst.writerIndex(dstWriterIndex);
                return false;
            }
        }

        boolean hasDict = dict != null && dict.isReadable();
        boolean releaseDict = false;
        try {
            if (hasDict && !dict.hasMemoryAddress() && !dict.hasArray()) {
                //dict is composite
                ByteBuf buf = PooledByteBufAllocator.DEFAULT.directBuffer(dict.readableBytes(), dict.readableBytes());
                dict.getBytes(dict.readerIndex(), buf);
                dict = buf;
                releaseDict = true;
            }

            long session = newSession0(this.ctx);
            try {
                long ret = -1L;
                if (hasDict && dict.hasMemoryAddress()) {
                    if (src.hasMemoryAddress()) {
                        if (dst.hasMemoryAddress()) {
                            ret = compressD2DWithDictD0(this.ctx,
                                    src.memoryAddress() + src.readerIndex(), src.readableBytes(),
                                    dst.memoryAddress() + dst.writerIndex(), dst.writableBytes(),
                                    dict.memoryAddress() + dict.readerIndex(), dict.readableBytes(),
                                    level);
                        } else if (dst.hasArray()) {
                            ret = compressD2HWithDictD0(this.ctx,
                                    src.memoryAddress() + src.readerIndex(), src.readableBytes(),
                                    dst.array(), dst.arrayOffset() + dst.writerIndex(), dst.writableBytes(),
                                    dict.memoryAddress() + dict.readerIndex(), dict.readableBytes(),
                                    level);
                        }
                    } else if (src.hasArray()) {
                        if (dst.hasMemoryAddress()) {
                            ret = compressH2DWithDictD0(this.ctx,
                                    src.array(), src.arrayOffset() + src.readerIndex(), src.readableBytes(),
                                    dst.memoryAddress() + dst.writerIndex(), dst.writableBytes(),
                                    dict.memoryAddress() + dict.readerIndex(), dict.readableBytes(),
                                    level);
                        } else if (dst.hasArray()) {
                            ret = compressH2HWithDictD0(this.ctx,
                                    src.array(), src.arrayOffset() + src.readerIndex(), src.readableBytes(),
                                    dst.array(), dst.arrayOffset() + dst.writerIndex(), dst.writableBytes(),
                                    dict.memoryAddress() + dict.readerIndex(), dict.readableBytes(),
                                    level);
                        }
                    }
                } else if (hasDict && dict.hasArray()) {
                    if (src.hasMemoryAddress()) {
                        if (dst.hasMemoryAddress()) {
                            ret = compressD2DWithDictH0(this.ctx,
                                    src.memoryAddress() + src.readerIndex(), src.readableBytes(),
                                    dst.memoryAddress() + dst.writerIndex(), dst.writableBytes(),
                                    dict.array(), dict.arrayOffset() + dict.readerIndex(), dict.readableBytes(),
                                    level);
                        } else if (dst.hasArray()) {
                            ret = compressD2HWithDictH0(this.ctx,
                                    src.memoryAddress() + src.readerIndex(), src.readableBytes(),
                                    dst.array(), dst.arrayOffset() + dst.writerIndex(), dst.writableBytes(),
                                    dict.array(), dict.arrayOffset() + dict.readerIndex(), dict.readableBytes(),
                                    level);
                        }
                    } else if (src.hasArray()) {
                        if (dst.hasMemoryAddress()) {
                            ret = compressH2DWithDictH0(this.ctx,
                                    src.array(), src.arrayOffset() + src.readerIndex(), src.readableBytes(),
                                    dst.memoryAddress() + dst.writerIndex(), dst.writableBytes(),
                                    dict.array(), dict.arrayOffset() + dict.readerIndex(), dict.readableBytes(),
                                    level);
                        } else if (dst.hasArray()) {
                            ret = compressH2HWithDictH0(this.ctx,
                                    src.array(), src.arrayOffset() + src.readerIndex(), src.readableBytes(),
                                    dst.array(), dst.arrayOffset() + dst.writerIndex(), dst.writableBytes(),
                                    dict.array(), dict.arrayOffset() + dict.readerIndex(), dict.readableBytes(),
                                    level);
                        }
                    }
                } else {
                    if (src.hasMemoryAddress()) {
                        if (dst.hasMemoryAddress()) {
                            ret = compressD2D0(this.ctx,
                                    src.memoryAddress() + src.readerIndex(), src.readableBytes(),
                                    dst.memoryAddress() + dst.writerIndex(), dst.writableBytes(),
                                    level);
                        } else if (dst.hasArray()) {
                            ret = compressD2H0(this.ctx,
                                    src.memoryAddress() + src.readerIndex(), src.readableBytes(),
                                    dst.array(), dst.arrayOffset() + dst.writerIndex(), dst.writableBytes(),
                                    level);
                        }
                    } else if (src.hasArray()) {
                        if (dst.hasMemoryAddress()) {
                            ret = compressH2D0(this.ctx,
                                    src.array(), src.arrayOffset() + src.readerIndex(), src.readableBytes(),
                                    dst.memoryAddress() + dst.writerIndex(), dst.writableBytes(),
                                    level);
                        } else if (dst.hasArray()) {
                            ret = compressH2H0(this.ctx,
                                    src.array(), src.arrayOffset() + src.readerIndex(), src.readableBytes(),
                                    dst.array(), dst.arrayOffset() + dst.writerIndex(), dst.writableBytes(),
                                    level);
                        }
                    }
                }

                if (ret >= 0L) {
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
        } finally {
            if (releaseDict) {
                dict.release();
            }
        }
    }

    @Override
    @SneakyThrows(IOException.class)
    public synchronized boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ZstdDeflateDictionary dict) {
        this.ensureNotReleased();
        if (dict == null) {
            return this.compress(src, dst, null, this.options.level());
        }

        if (!(src.hasMemoryAddress() || src.hasArray()) || !(dst.hasMemoryAddress() || dst.hasArray())) {
            //the other cases are too much of a pain to implement by hand
            int srcReaderIndex = src.readerIndex();
            int dstWriterIndex = dst.writerIndex();
            try (DataOut out = this.compressionStream(DataOut.wrapViewNonGrowing(dst), null, -1, dict)) {
                out.write(src);
                return true;
            } catch (NoMoreSpaceException e) { //buffer reached capacity and isn't allowed to grow
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
                    ret = compressD2DWithDict0(this.ctx,
                            src.memoryAddress() + src.readerIndex(), src.readableBytes(),
                            dst.memoryAddress() + dst.writerIndex(), dst.writableBytes(),
                            dictAddr);
                } else if (dst.hasArray()) {
                    ret = compressD2HWithDict0(this.ctx,
                            src.memoryAddress() + src.readerIndex(), src.readableBytes(),
                            dst.array(), dst.arrayOffset() + dst.writerIndex(), dst.writableBytes(),
                            dictAddr);
                }
            } else if (src.hasArray()) {
                if (dst.hasMemoryAddress()) {
                    ret = compressH2DWithDict0(this.ctx,
                            src.array(), src.arrayOffset() + src.readerIndex(), src.readableBytes(),
                            dst.memoryAddress() + dst.writerIndex(), dst.writableBytes(),
                            dictAddr);
                } else if (dst.hasArray()) {
                    ret = compressH2HWithDict0(this.ctx,
                            src.array(), src.arrayOffset() + src.readerIndex(), src.readableBytes(),
                            dst.array(), dst.arrayOffset() + dst.writerIndex(), dst.writableBytes(),
                            dictAddr);
                }
            }

            if (ret >= 0L) {
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

        if (!(src.hasMemoryAddress() || src.hasArray()) || !(dst.hasMemoryAddress() || dst.hasArray())) {
            //the other cases are too much of a pain to implement by hand
            try (DataOut out = this.compressionStream(DataOut.wrapView(dst), null, -1, dict, level)) {
                out.write(src);
                return;
            } catch (IOException e) {
                //shouldn't be possible
                throw new RuntimeException(e);
            }
        }

        this.compressGrowing0(src, dst, this.createSessionAndSetDict(dict, level));
    }

    @Override
    public synchronized void compressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst, ZstdDeflateDictionary dict) throws IndexOutOfBoundsException {
        this.ensureNotReleased();
        checkArg(dict == null || dict instanceof NativeZstdDeflateDictionary, "invalid dictionary: %s", dict);

        if (!(src.hasMemoryAddress() || src.hasArray()) || !(dst.hasMemoryAddress() || dst.hasArray())) {
            //the other cases are too much of a pain to implement by hand
            try (DataOut out = this.compressionStream(DataOut.wrapView(dst), null, -1, dict)) {
                out.write(src);
                return;
            } catch (IOException e) {
                //shouldn't be possible
                throw new RuntimeException(e);
            }
        }

        long session = this.createSessionAndSetDict((NativeZstdDeflateDictionary) dict);
        try {
            this.compressGrowing0(src, dst, session);
        } finally {
            if (dict != null) {
                dict.release();
            }
        }
    }

    void compressGrowing0(@NonNull ByteBuf src, @NonNull ByteBuf dst, long session) throws IndexOutOfBoundsException {
        try {
            while (true) {
                long remaining;
                if (src.hasMemoryAddress()) {
                    if (dst.hasMemoryAddress()) {
                        remaining = updateD2D0(this.ctx,
                                src.memoryAddress() + src.readerIndex(), src.readableBytes(),
                                dst.memoryAddress() + dst.writerIndex(), dst.writableBytes(),
                                ZSTD_e_end);
                    } else if (dst.hasArray()) {
                        remaining = updateD2H0(this.ctx,
                                src.memoryAddress() + src.readerIndex(), src.readableBytes(),
                                dst.array(), dst.arrayOffset() + dst.writerIndex(), dst.writableBytes(),
                                ZSTD_e_end);
                    } else {
                        throw new IllegalStateException(src + " " + dst);
                    }
                } else if (src.hasArray()) {
                    if (dst.hasMemoryAddress()) {
                        remaining = updateH2D0(this.ctx,
                                src.array(), src.arrayOffset() + src.readerIndex(), src.readableBytes(),
                                dst.memoryAddress() + dst.writerIndex(), dst.writableBytes(),
                                ZSTD_e_end);
                    } else if (dst.hasArray()) {
                        remaining = updateH2H0(this.ctx,
                                src.array(), src.arrayOffset() + src.readerIndex(), src.readableBytes(),
                                dst.array(), dst.arrayOffset() + dst.writerIndex(), dst.writableBytes(),
                                ZSTD_e_end);
                    } else {
                        throw new IllegalStateException(src + " " + dst);
                    }
                } else {
                    throw new IllegalStateException(src + " " + dst);
                }

                //increase indices
                src.skipBytes(toInt(this.getRead(), "read"));
                dst.writerIndex(dst.writerIndex() + toInt(this.getWritten(), "written"));

                if (remaining == 0L && !src.isReadable()) {
                    break;
                } else if (!dst.isWritable()) {
                    checkIndex(dst.writerIndex() < dst.maxCapacity());
                    dst.ensureWritable(min(dst.maxWritableBytes(), 8192));
                } else {
                    throw new IllegalStateException(String.valueOf(remaining));
                }
            }
            checkState(!src.isReadable());
        } finally {
            if (session != this.getSession()) {
                throw new ConcurrentModificationException(); //probably impossible
            }
        }
    }

    @Override
    public synchronized DataOut compressionStream(@NonNull DataOut out, ByteBufAllocator bufferAlloc, int bufferSize, ByteBuf dict, int level) throws IOException {
        this.ensureNotReleased();
        Zstd.checkLevel(level);

        if (bufferAlloc == null) {
            bufferAlloc = PooledByteBufAllocator.DEFAULT;
        }
        if (bufferSize <= 0) {
            bufferSize = PorkUtil.BUFFER_SIZE;
        }
        ByteBuf buf;
        if (out.isDirect()) {
            buf = bufferAlloc.directBuffer(bufferSize, bufferSize);
        } else if (out.isHeap()) {
            buf = bufferAlloc.heapBuffer(bufferSize, bufferSize);
        } else {
            buf = bufferAlloc.buffer(bufferSize, bufferSize);
        }
        return new NativeZstdDeflateStream(out, buf, dict, level, this);
    }

    @Override
    public synchronized DataOut compressionStream(@NonNull DataOut out, ByteBufAllocator bufferAlloc, int bufferSize, ZstdDeflateDictionary dict) throws IOException {
        this.ensureNotReleased();
        checkArg(dict == null || dict instanceof NativeZstdDeflateDictionary, "invalid dictionary: %s", dict);

        if (bufferAlloc == null) {
            bufferAlloc = PooledByteBufAllocator.DEFAULT;
        }
        if (bufferSize <= 0) {
            bufferSize = PorkUtil.BUFFER_SIZE;
        }
        ByteBuf buf;
        if (out.isDirect()) {
            buf = bufferAlloc.directBuffer(bufferSize, bufferSize);
        } else if (out.isHeap()) {
            buf = bufferAlloc.heapBuffer(bufferSize, bufferSize);
        } else {
            buf = bufferAlloc.buffer(bufferSize, bufferSize);
        }
        return new NativeZstdDeflateStream(out, buf, (NativeZstdDeflateDictionary) dict, this);
    }

    long createSessionAndSetDict(ByteBuf dict, int level) {
        if (dict == null || !dict.isReadable()) {
            //no dictionary will be used
            return newSessionWithLevel0(this.ctx, level);
        } else if (dict.hasMemoryAddress()) {
            return newSessionWithDictD0(this.ctx, dict.memoryAddress() + dict.readerIndex(), dict.readableBytes(), level);
        } else if (dict.hasArray()) {
            return newSessionWithDictH0(this.ctx, dict.array(), dict.arrayOffset() + dict.readerIndex(), dict.readableBytes(), level);
        } else {
            ByteBuf buf = PooledByteBufAllocator.DEFAULT.directBuffer(dict.readableBytes(), dict.readableBytes());
            try {
                dict.getBytes(dict.readerIndex(), buf);
                return newSessionWithDictD0(this.ctx, buf.memoryAddress() + buf.readerIndex(), buf.readableBytes(), level);
            } finally {
                buf.release();
            }
        }
    }

    long createSessionAndSetDict(NativeZstdDeflateDictionary dict) {
        if (dict == null) {
            //no dictionary will be used
            return newSessionWithLevel0(this.ctx, this.options.level());
        } else {
            dict.retain();
            return newSessionWithDict0(this.ctx, dict.addr());
        }
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
