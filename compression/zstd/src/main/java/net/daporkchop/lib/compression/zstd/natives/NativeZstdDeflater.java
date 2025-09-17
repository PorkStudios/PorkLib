/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2025 DaPorkchop_
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
 */

package net.daporkchop.lib.compression.zstd.natives;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.binary.util.NoMoreSpaceException;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.compression.zstd.Zstd;
import net.daporkchop.lib.compression.zstd.ZstdDeflateDictionary;
import net.daporkchop.lib.compression.zstd.ZstdDeflater;
import net.daporkchop.lib.compression.zstd.options.ZstdDeflaterOptions;

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
final class NativeZstdDeflater extends AbstractNativeZstdContext implements ZstdDeflater {
    @Override
    native long allocate0();

    static native void release0(long ctx);

    static native long newSession0(long ctx);

    static native long compress(
            long ctx,
            long srcDirectAddr, byte[] srcArray, int srcArrayOffset, int srcPosition, int srcRemaining,
            long dstDirectAddr, byte[] dstArray, int dstArrayOffset, int dstPosition, int dstRemaining,
            int level);

    static native long compressWithDict(
            long ctx, 
            long srcDirectAddr, byte[] srcArray, int srcArrayOffset, int srcPosition, int srcRemaining,
            long dstDirectAddr, byte[] dstArray, int dstArrayOffset, int dstPosition, int dstRemaining,
            long dict);

    static native long compressWithDict(
            long ctx, 
            long srcDirectAddr, byte[] srcArray, int srcArrayOffset, int srcPosition, int srcRemaining,
            long dstDirectAddr, byte[] dstArray, int dstArrayOffset, int dstPosition, int dstRemaining,
            long dictDirectAddr, byte[] dictArray, int dictArrayOffset, int dictPosition, int dictRemaining,
            int level);

    //streaming

    static native long newSessionWithLevel0(long ctx, int level);

    static native long newSessionWithDict0(long ctx, long dict);

    static native long newSessionWithDict(
            long ctx,
            long dictDirectAddr, byte[] dictArray, int dictArrayOffset, int dictPosition, int dictRemaining,
            int level);

    static native long update(
            long ctx,
            long srcDirectAddr, byte[] srcArray, int srcArrayOffset, int srcPosition, int srcRemaining,
            long dstDirectAddr, byte[] dstArray, int dstArrayOffset, int dstPosition, int dstRemaining,
            int flush);

    @Getter
    final ZstdDeflaterOptions options;

    NativeZstdDeflater(@NonNull ZstdDeflaterOptions options) {
        this.options = options;
    }

    @Override
    Runnable makeReleaser(long addr) {
        return () -> release0(addr);
    }

    @Override
    @SneakyThrows(IOException.class)
    public boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict, int level) {
        this.ensureOpen();
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
                ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(dict.readableBytes(), dict.readableBytes());
                dict.getBytes(dict.readerIndex(), buf);
                dict = buf;
                releaseDict = true;
            }

            long session = newSession0(this.ctx);
            try {
                //get buffer pointers
                long srcMemoryAddress = 0L, dstMemoryAddress = 0L;
                byte[] srcArray = null, dstArray = null;
                int srcArrayOffset = 0, dstArrayOffset = 0;
                if (src.hasMemoryAddress()) {
                    srcMemoryAddress = src.memoryAddress();
                } else {
                    srcArray = src.array();
                    srcArrayOffset = src.arrayOffset();
                }
                if (dst.hasMemoryAddress()) {
                    dstMemoryAddress = dst.memoryAddress();
                } else {
                    dstArray = dst.array();
                    dstArrayOffset = dst.arrayOffset();
                }

                //invoke the compression routine
                long ret;
                if (hasDict) {
                    long dictMemoryAddress = 0L;
                    byte[] dictArray = null;
                    int dictArrayOffset = 0;
                    if (dict.hasMemoryAddress()) {
                        dictMemoryAddress = dict.memoryAddress();
                    } else {
                        dictArray = dict.array();
                        dictArrayOffset = dict.arrayOffset();
                    }

                    ret = compressWithDict(this.ctx,
                            srcMemoryAddress, srcArray, srcArrayOffset, src.readerIndex(), src.readableBytes(),
                            dstMemoryAddress, dstArray, dstArrayOffset, dst.writerIndex(), dst.writableBytes(),
                            dictMemoryAddress, dictArray, dictArrayOffset, dict.readerIndex(), dict.readableBytes(),
                            level);
                } else {
                    ret = compress(this.ctx,
                            srcMemoryAddress, srcArray, srcArrayOffset, src.readerIndex(), src.readableBytes(),
                            dstMemoryAddress, dstArray, dstArrayOffset, dst.writerIndex(), dst.writableBytes(),
                            level);
                }

                if (ret >= 0L) {
                    src.skipBytes(src.readableBytes());
                    dst.writerIndex(dst.writerIndex() + Math.toIntExact(ret));
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
    public boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ZstdDeflateDictionary dict) {
        this.ensureOpen();
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
        long dictAddr = ((NativeZstdDeflateDictionary) dict).addr();

        long session = newSession0(this.ctx);
        try {
            //get buffer pointers
            long srcMemoryAddress = 0L, dstMemoryAddress = 0L;
            byte[] srcArray = null, dstArray = null;
            int srcArrayOffset = 0, dstArrayOffset = 0;
            if (src.hasMemoryAddress()) {
                srcMemoryAddress = src.memoryAddress();
            } else {
                srcArray = src.array();
                srcArrayOffset = src.arrayOffset();
            }
            if (dst.hasMemoryAddress()) {
                dstMemoryAddress = dst.memoryAddress();
            } else {
                dstArray = dst.array();
                dstArrayOffset = dst.arrayOffset();
            }

            //invoke the compression routine
            long ret = compressWithDict(this.ctx,
                    srcMemoryAddress, srcArray, srcArrayOffset, src.readerIndex(), src.readableBytes(),
                    dstMemoryAddress, dstArray, dstArrayOffset, dst.writerIndex(), dst.writableBytes(),
                    dictAddr);

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
    }

    @Override
    @SneakyThrows(IOException.class)
    public void compressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict, int level) throws IndexOutOfBoundsException {
        this.ensureOpen();
        Zstd.checkLevel(level);

        if (!(src.hasMemoryAddress() || src.hasArray()) || !(dst.hasMemoryAddress() || dst.hasArray())) {
            //the other cases are too much of a pain to implement by hand
            try (DataOut out = this.compressionStream(DataOut.wrapView(dst), null, -1, dict, level)) {
                out.write(src);
                return;
            }
        }

        this.compressGrowing0(src, dst, this.createSessionAndSetDict(dict, level));
    }

    @Override
    @SneakyThrows(IOException.class)
    public void compressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst, ZstdDeflateDictionary dict) throws IndexOutOfBoundsException {
        this.ensureOpen();
        checkArg(dict == null || dict instanceof NativeZstdDeflateDictionary, "invalid dictionary: %s", dict);

        if (!(src.hasMemoryAddress() || src.hasArray()) || !(dst.hasMemoryAddress() || dst.hasArray())) {
            //the other cases are too much of a pain to implement by hand
            try (DataOut out = this.compressionStream(DataOut.wrapView(dst), null, -1, dict)) {
                out.write(src);
                return;
            }
        }

        long session = this.createSessionAndSetDict((NativeZstdDeflateDictionary) dict);
        this.compressGrowing0(src, dst, session);
    }

    private void compressGrowing0(@NonNull ByteBuf src, @NonNull ByteBuf dst, long session) throws IndexOutOfBoundsException {
        try {
            while (true) {
                //get buffer pointers
                long srcMemoryAddress = 0L, dstMemoryAddress = 0L;
                byte[] srcArray = null, dstArray = null;
                int srcArrayOffset = 0, dstArrayOffset = 0;
                if (src.hasMemoryAddress()) {
                    srcMemoryAddress = src.memoryAddress();
                } else {
                    srcArray = src.array();
                    srcArrayOffset = src.arrayOffset();
                }
                if (dst.hasMemoryAddress()) {
                    dstMemoryAddress = dst.memoryAddress();
                } else {
                    dstArray = dst.array();
                    dstArrayOffset = dst.arrayOffset();
                }

                //invoke the compression routine
                long remaining = update(this.ctx,
                        srcMemoryAddress, srcArray, srcArrayOffset, src.readerIndex(), src.readableBytes(),
                        dstMemoryAddress, dstArray, dstArrayOffset, dst.writerIndex(), dst.writableBytes(),
                        ZSTD_e_end);

                //increase indices
                src.skipBytes(Math.toIntExact(this.getRead()));
                dst.writerIndex(dst.writerIndex() + Math.toIntExact(this.getWritten()));

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
    public DataOut compressionStream(@NonNull DataOut out, ByteBufAllocator bufferAlloc, int bufferSize, ByteBuf dict, int level) throws IOException {
        this.ensureOpen();
        Zstd.checkLevel(level);

        if (bufferAlloc == null) {
            bufferAlloc = ByteBufAllocator.DEFAULT;
        }
        if (bufferSize <= 0) {
            bufferSize = PorkUtil.bufferSize();
        }
        return new NativeZstdDeflateStream(out, bufferAlloc.directBuffer(bufferSize, bufferSize), dict, level, this);
    }

    @Override
    public DataOut compressionStream(@NonNull DataOut out, ByteBufAllocator bufferAlloc, int bufferSize, ZstdDeflateDictionary dict) throws IOException {
        this.ensureOpen();
        checkArg(dict == null || dict instanceof NativeZstdDeflateDictionary, "invalid dictionary: %s", dict);

        if (bufferAlloc == null) {
            bufferAlloc = ByteBufAllocator.DEFAULT;
        }
        if (bufferSize <= 0) {
            bufferSize = PorkUtil.bufferSize();
        }
        return new NativeZstdDeflateStream(out, bufferAlloc.directBuffer(bufferSize, bufferSize), (NativeZstdDeflateDictionary) dict, this);
    }

    long createSessionAndSetDict(ByteBuf dict, int level) {
        if (dict == null || !dict.isReadable()) {
            //no dictionary will be used
            return newSessionWithLevel0(this.ctx, level);
        } else if (dict.hasMemoryAddress() || dict.hasArray()) {
            return newSessionWithDict(this.ctx,
                    dict.hasMemoryAddress() ? dict.memoryAddress() : 0L, dict.hasArray() ? dict.array() : null, dict.hasArray() ? dict.arrayOffset() : 0, dict.readerIndex(), dict.readableBytes(),
                    level);
        } else {
            ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(dict.readableBytes(), dict.readableBytes());
            try {
                dict.getBytes(dict.readerIndex(), buf);
                return newSessionWithDict(this.ctx,
                        buf.memoryAddress(), null, 0, buf.readerIndex(), buf.readableBytes(),
                        level);
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
            return newSessionWithDict0(this.ctx, dict.addr());
        }
    }
}
