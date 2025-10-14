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
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.binary.util.NoMoreSpaceException;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.compression.zstd.ZstdDecompressDictionary;
import net.daporkchop.lib.compression.zstd.ZstdInflater;
import net.daporkchop.lib.compression.zstd.options.ZstdInflaterCreateOptions;

import java.io.IOException;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
@SuppressWarnings("Duplicates")
final class NativeZstdInflater extends AbstractNativeZstdContext implements ZstdInflater {
    @Override
    native long createCtx();

    static native void release0(long ctx);

    static native long newSession0(long ctx);

    static native long decompress(
            long ctx,
            long srcDirectAddr, byte[] srcArray, int srcArrayOffset, int srcPosition, int srcRemaining,
            long dstDirectAddr, byte[] dstArray, int dstArrayOffset, int dstPosition, int dstRemaining);

    static native long decompressWithDict(
            long ctx,
            long srcDirectAddr, byte[] srcArray, int srcArrayOffset, int srcPosition, int srcRemaining,
            long dstDirectAddr, byte[] dstArray, int dstArrayOffset, int dstPosition, int dstRemaining,
            long dict);

    static native long decompressWithDict(
            long ctx,
            long srcDirectAddr, byte[] srcArray, int srcArrayOffset, int srcPosition, int srcRemaining,
            long dstDirectAddr, byte[] dstArray, int dstArrayOffset, int dstPosition, int dstRemaining,
            long dictDirectAddr, byte[] dictArray, int dictArrayOffset, int dictPosition, int dictRemaining);

    //streaming

    static native long newSessionWithDict0(long ctx, long dict);

    static native long newSessionWithDict(
            long ctx,
            long dictDirectAddr, byte[] dictArray, int dictArrayOffset, int dictPosition, int dictRemaining);

    static native long update(
            long ctx,
            long srcDirectAddr, byte[] srcArray, int srcArrayOffset, int srcPosition, int srcRemaining,
            long dstDirectAddr, byte[] dstArray, int dstArrayOffset, int dstPosition, int dstRemaining);

    @Getter
    final ZstdInflaterCreateOptions options;

    NativeZstdInflater(@NonNull ZstdInflaterCreateOptions options) {
        this.options = options;
    }

    @Override
    Runnable freeCtxRunnable(long ctx) {
        return () -> release0(ctx);
    }

    @Override
    @SneakyThrows(IOException.class)
    public boolean decompress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict) {
        this.ensureOpen();

        if (!(src.hasMemoryAddress() || src.hasArray()) || !(dst.hasMemoryAddress() || dst.hasArray())) {
            //the other cases are too much of a pain to implement by hand
            int srcReaderIndex = src.readerIndex();
            int dstWriterIndex = dst.writerIndex();
            try (DataIn in = this.decompressionStream(DataIn.wrapView(src), null, -1, dict);
                 DataOut out = DataOut.wrapViewNonGrowing(dst)) {
                out.transferFrom(in);
                return true;
            } catch (NoMoreSpaceException e) { //buffer reached capacity and isn't allowed to grow
                src.readerIndex(srcReaderIndex);
                dst.writerIndex(dstWriterIndex);
                return false;
            }
        }

        if (dict == null || !dict.isReadable()) {
            return this.decompressNoDict(src, dst);
        } else if (!dict.hasMemoryAddress() && !dict.hasArray()) {
            //dict is composite, copy it into a single buffer and try again
            ByteBuf buf = dict.alloc().directBuffer(dict.readableBytes(), dict.readableBytes());
            try {
                dict.getBytes(dict.readerIndex(), buf);
                return this.decompress(src, dst, buf);
            } finally {
                buf.release();
            }
        }

        long session = newSession0(this.ctx);

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

        long dictMemoryAddress = 0L;
        byte[] dictArray = null;
        int dictArrayOffset = 0;
        if (dict.hasMemoryAddress()) {
            dictMemoryAddress = dict.memoryAddress();
        } else {
            dictArray = dict.array();
            dictArrayOffset = dict.arrayOffset();
        }

        long ret = decompressWithDict(this.ctx,
                srcMemoryAddress, srcArray, srcArrayOffset, src.readerIndex(), src.readableBytes(),
                dstMemoryAddress, dstArray, dstArrayOffset, dst.writerIndex(), dst.writableBytes(),
                dictMemoryAddress, dictArray, dictArrayOffset, dict.readerIndex(), dict.readableBytes());

        return this.handleDecompressResult(src, dst, session, ret);
    }

    @Override
    @SneakyThrows(IOException.class)
    public boolean decompress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ZstdDecompressDictionary dict) {
        this.ensureOpen();

        if (!(src.hasMemoryAddress() || src.hasArray()) || !(dst.hasMemoryAddress() || dst.hasArray())) {
            //the other cases are too much of a pain to implement by hand
            int srcReaderIndex = src.readerIndex();
            int dstWriterIndex = dst.writerIndex();
            try (DataIn in = this.decompressionStream(DataIn.wrapView(src), null, -1, dict);
                 DataOut out = DataOut.wrapViewNonGrowing(dst)) {
                out.transferFrom(in);
                return true;
            } catch (NoMoreSpaceException e) { //buffer reached capacity and isn't allowed to grow
                src.readerIndex(srcReaderIndex);
                dst.writerIndex(dstWriterIndex);
                return false;
            }
        }

        if (dict == null) {
            return this.decompressNoDict(src, dst);
        }

        checkArg(dict instanceof NativeZstdInflateDictionary, "invalid dictionary: %s", dict);
        long dictAddr = ((NativeZstdInflateDictionary) dict).addr();

        long session = newSession0(this.ctx);

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

        long ret = decompressWithDict(this.ctx,
                srcMemoryAddress, srcArray, srcArrayOffset, src.readerIndex(), src.readableBytes(),
                dstMemoryAddress, dstArray, dstArrayOffset, dst.writerIndex(), dst.writableBytes(),
                dictAddr);

        return this.handleDecompressResult(src, dst, session, ret);
    }

    private boolean decompressNoDict(@NonNull ByteBuf src, @NonNull ByteBuf dst) {
        long session = newSession0(this.ctx);

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

        //invoke the decompression routine
        long ret = decompress(this.ctx,
                srcMemoryAddress, srcArray, srcArrayOffset, src.readerIndex(), src.readableBytes(),
                dstMemoryAddress, dstArray, dstArrayOffset, dst.writerIndex(), dst.writableBytes());

        return this.handleDecompressResult(src, dst, session, ret);
    }

    private boolean handleDecompressResult(ByteBuf src, ByteBuf dst, long session, long ret) {
        this.checkSession(session);

        if (ret >= 0L) {
            src.skipBytes(src.readableBytes());

            //this can't overflow unless dst.writerIndex() is changed concurrently
            dst.writerIndex(dst.writerIndex() + Math.toIntExact(ret));
            return true;
        } else {
            return false;
        }
    }

    @Override
    @SneakyThrows(IOException.class)
    public void decompressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict) throws IndexOutOfBoundsException {
        this.ensureOpen();

        if (!(src.hasMemoryAddress() || src.hasArray()) || !(dst.hasMemoryAddress() || dst.hasArray())) {
            //the other cases are too much of a pain to implement by hand
            try (DataIn in = this.decompressionStream(DataIn.wrapView(src), null, -1, dict);
                 DataOut out = DataOut.wrapView(dst)) {
                out.transferFrom(in);
                return;
            }
        }

        this.decompressGrowing0(src, dst, this.createSessionAndSetDict(dict));
    }

    @Override
    @SneakyThrows(IOException.class)
    public void decompressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst, ZstdDecompressDictionary dict) throws IndexOutOfBoundsException {
        this.ensureOpen();
        checkArg(dict == null || dict instanceof NativeZstdInflateDictionary, "invalid dictionary: %s", dict);

        if (!(src.hasMemoryAddress() || src.hasArray()) || !(dst.hasMemoryAddress() || dst.hasArray())) {
            //the other cases are too much of a pain to implement by hand
            try (DataIn in = this.decompressionStream(DataIn.wrapView(src), null, -1, dict);
                 DataOut out = DataOut.wrapView(dst)) {
                out.transferFrom(in);
                return;
            }
        }

        long session = this.createSessionAndSetDict((NativeZstdInflateDictionary) dict);
        this.decompressGrowing0(src, dst, session);
    }

    void decompressGrowing0(@NonNull ByteBuf src, @NonNull ByteBuf dst, long session) throws IndexOutOfBoundsException {
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

                //invoke the decompression routine
                long remaining = update(this.ctx,
                        srcMemoryAddress, srcArray, srcArrayOffset, src.readerIndex(), src.readableBytes(),
                        dstMemoryAddress, dstArray, dstArrayOffset, dst.writerIndex(), dst.writableBytes());

                //increase indices
                src.skipBytes(Math.toIntExact(this.getRead()));
                dst.writerIndex(Math.addExact(dst.writerIndex(), Math.toIntExact(this.getWritten())));

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
            this.checkSession(session);
        }
    }

    @Override
    public DataIn decompressionStream(@NonNull DataIn in, ByteBufAllocator bufferAlloc, int bufferSize, ByteBuf dict) throws IOException {
        this.ensureOpen();

        if (bufferAlloc == null) {
            bufferAlloc = ByteBufAllocator.DEFAULT;
        }
        if (bufferSize <= 0) {
            bufferSize = PorkUtil.bufferSize();
        }
        ByteBuf buf;
        if (in.isDirect()) {
            buf = bufferAlloc.directBuffer(bufferSize, bufferSize);
        } else if (in.isHeap()) {
            buf = bufferAlloc.heapBuffer(bufferSize, bufferSize);
        } else {
            buf = bufferAlloc.buffer(bufferSize, bufferSize);
        }
        return new NativeZstdInflateStream(in, buf, dict, this);
    }

    @Override
    public DataIn decompressionStream(@NonNull DataIn in, ByteBufAllocator bufferAlloc, int bufferSize, ZstdDecompressDictionary dict) throws IOException {
        this.ensureOpen();
        checkArg(dict == null || dict instanceof NativeZstdInflateDictionary, "invalid dictionary: %s", dict);

        if (bufferAlloc == null) {
            bufferAlloc = ByteBufAllocator.DEFAULT;
        }
        if (bufferSize <= 0) {
            bufferSize = PorkUtil.bufferSize();
        }
        ByteBuf buf;
        if (in.isDirect()) {
            buf = bufferAlloc.directBuffer(bufferSize, bufferSize);
        } else if (in.isHeap()) {
            buf = bufferAlloc.heapBuffer(bufferSize, bufferSize);
        } else {
            buf = bufferAlloc.buffer(bufferSize, bufferSize);
        }
        return new NativeZstdInflateStream(in, buf, (NativeZstdInflateDictionary) dict, this);
    }

    long createSessionAndSetDict(ByteBuf dict) {
        if (dict == null || !dict.isReadable()) {
            //no dictionary will be used
            return newSession0(this.ctx);
        } else if (dict.hasMemoryAddress()) {
            return newSessionWithDict(this.ctx,
                    dict.memoryAddress(), null, 0, dict.readerIndex(), dict.readableBytes());
        } else if (dict.hasArray()) {
            return newSessionWithDict(this.ctx,
                    0L, dict.array(), dict.arrayOffset(), dict.readerIndex(), dict.readableBytes());
        } else {
            ByteBuf buf = dict.alloc().directBuffer(dict.readableBytes(), dict.readableBytes());
            try {
                dict.getBytes(dict.readerIndex(), buf);
                return newSessionWithDict(this.ctx,
                        buf.memoryAddress(), null, 0, buf.readerIndex(), buf.readableBytes());
            } finally {
                buf.release();
            }
        }
    }

    long createSessionAndSetDict(NativeZstdInflateDictionary dict) {
        if (dict == null) {
            //no dictionary will be used
            return newSession0(this.ctx);
        } else {
            return newSessionWithDict0(this.ctx, dict.addr());
        }
    }
}
