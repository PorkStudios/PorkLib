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

package net.daporkchop.lib.compression.zlib.natives;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.common.pool.recycler.Recycler;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.common.util.exception.AlreadyReleasedException;
import net.daporkchop.lib.compression.util.exception.DictionaryNotAllowedException;
import net.daporkchop.lib.compression.zlib.ZlibDeflater;
import net.daporkchop.lib.compression.zlib.options.ZlibDeflaterOptions;
import net.daporkchop.lib.unsafe.PCleaner;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ConcurrentModificationException;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.compression.zlib.natives.NativeZlib.*;

/**
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
@SuppressWarnings("Duplicates")
final class NativeZlibDeflater extends AbstractRefCounted.Synchronized implements ZlibDeflater {
    static native long allocate0(int level, int mode, int strategy);

    static native void release0(long ctx);

    static native long newSession0(long ctx, long dict, int dictLen);

    static native int updateD2D0(long ctx, long src, int srcLen, long dst, int dstLen, int flush);

    static native int updateD2H0(long ctx, long src, int srcLen, byte[] dst, int dstOff, int dstLen, int flush);

    static native int updateH2D0(long ctx, byte[] src, int srcOff, int srcLen, long dst, int dstLen, int flush);

    static native int updateH2H0(long ctx, byte[] src, int srcOff, int srcLen, byte[] dst, int dstOff, int dstLen, int flush);

    final long ctx;

    @Getter
    final ZlibDeflaterOptions options;

    final PCleaner cleaner;

    NativeZlibDeflater(@NonNull ZlibDeflaterOptions options) {
        this.options = options;

        this.ctx = allocate0(options.level(), options.mode().ordinal(), options.strategy().ordinal());
        this.cleaner = PCleaner.cleaner(this, new Releaser(this.ctx));
    }

    @Override
    protected void doRelease() {
        checkState(this.cleaner.clean(), "already cleaned?!?");
    }

    @Override
    public boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict) {
        this.ensureNotReleased();
        checkArg(src.isReadable(), "src is not readable!");
        checkArg(dst.isWritable(), "dst is not writable!");

        if (!(src.hasMemoryAddress() || src.hasArray()) || !(dst.hasMemoryAddress() || dst.hasArray())) {
            //the other cases are too much of a pain to implement by hand
            int srcReaderIndex = src.readerIndex();
            int dstWriterIndex = dst.writerIndex();
            try (DataOut out = this.compressionStream(DataOut.wrap(dst, true, false), dict)) {
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

        long session = this.createSessionAndSetDict(dict);
        try {
            int status = this.update(src, dst, Z_FINISH);

            if (status != -1) {
                //increase indices
                if (status != Z_STREAM_END) {
                    return false;
                }

                src.skipBytes(toInt(this.getRead(), "read"));
                dst.writerIndex(dst.writerIndex() + toInt(this.getWritten(), "written"));
                return true;
            }
        } finally {
            if (session != this.getSession()) {
                throw new ConcurrentModificationException(); //probably impossible
            }
        }

        throw new IllegalStateException();
    }

    @Override
    public void compressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict) throws IndexOutOfBoundsException {
        this.ensureNotReleased();
        checkArg(src.isReadable(), "src is not readable!");
        checkArg(dst.isWritable(), "dst is not writable!");

        if (!(src.hasMemoryAddress() || src.hasArray()) || !(dst.hasMemoryAddress() || dst.hasArray())) {
            //the other cases are too much of a pain to implement by hand
            try (DataOut out = this.compressionStream(DataOut.wrap(dst, true), dict)) {
                out.write(src);
                return;
            } catch (IOException e) {
                //shouldn't be possible
                throw new RuntimeException(e);
            }
        }

        long session = this.createSessionAndSetDict(dict);
        try {
            while (true) {
                int status = this.update(src, dst, Z_FINISH);

                //increase indices
                src.skipBytes(toInt(this.getRead(), "read"));
                dst.writerIndex(dst.writerIndex() + toInt(this.getWritten(), "written"));

                if (status == Z_STREAM_END) {
                    break;
                } else if (status == Z_OK) {
                    checkIndex(dst.writerIndex() < dst.maxCapacity());
                    dst.ensureWritable(min(dst.maxWritableBytes(), 8192));
                } else {
                    throw new IllegalStateException(String.valueOf(status));
                }
            }
            checkState(!src.isReadable());
        } finally {
            if (session != this.getSession()) {
                throw new ConcurrentModificationException(); //probably impossible
            }
        }
    }

    long createSessionAndSetDict(ByteBuf dict) {
        if (dict == null || dict.readableBytes() == 0) {
            //empty dictionary, no dictionary will be used
            return newSession0(this.ctx, 0L, 0);
        } else if (dict.hasMemoryAddress()) {
            //dictionary is direct (this is fast)
            return newSession0(this.ctx, dict.memoryAddress() + dict.readerIndex(), dict.readableBytes());
        } else { //dictionary isn't direct, we have to manually copy it into a new buffer
            if (dict.readableBytes() <= PorkUtil.bufferSize()) { //dictionary is small enough to fit into a recycled buffer
                Recycler<ByteBuffer> recycler = PorkUtil.directBufferRecycler();
                ByteBuffer buffer = recycler.allocate();

                buffer.limit(min(buffer.capacity(), dict.readableBytes()));
                dict.getBytes(dict.readerIndex(), buffer);
                long session = newSession0(this.ctx, PUnsafe.pork_directBufferAddress(buffer.position(0)), buffer.limit());

                recycler.release(buffer); //release the buffer to the recycler
                return session;
            } else { //dictionary is very large, we need to allocate a new temporary direct buffer
                ByteBuffer buffer = ByteBuffer.allocateDirect(dict.readableBytes());
                dict.getBytes(dict.readerIndex(), buffer);
                return newSession0(this.ctx, PUnsafe.pork_directBufferAddress(buffer.position(0)), buffer.limit());
            }
        }
    }

    int update(@NonNull ByteBuf src, @NonNull ByteBuf dst, int flush) {
        if (src.hasMemoryAddress()) {
            if (dst.hasMemoryAddress()) {
                return updateD2D0(this.ctx,
                        src.memoryAddress() + src.readerIndex(), src.readableBytes(),
                        dst.memoryAddress() + dst.writerIndex(), dst.writableBytes(),
                        flush);
            } else if (dst.hasArray()) {
                return updateD2H0(this.ctx,
                        src.memoryAddress() + src.readerIndex(), src.readableBytes(),
                        dst.array(), dst.arrayOffset() + dst.writerIndex(), dst.writableBytes(),
                        flush);
            }
        } else if (src.hasArray()) {
            if (dst.hasMemoryAddress()) {
                return updateH2D0(this.ctx,
                        src.array(), src.arrayOffset() + src.readerIndex(), src.readableBytes(),
                        dst.memoryAddress() + dst.writerIndex(), dst.writableBytes(),
                        flush);
            } else if (dst.hasArray()) {
                return updateH2H0(this.ctx,
                        src.array(), src.arrayOffset() + src.readerIndex(), src.readableBytes(),
                        dst.array(), dst.arrayOffset() + dst.writerIndex(), dst.writableBytes(),
                        flush);
            }
        }
        throw new IllegalStateException(src + " " + dst);
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
        ByteBuf buf;
        if (out.isDirect()) {
            buf = bufferAlloc.directBuffer(bufferSize, bufferSize);
        } else if (out.isHeap()) {
            buf = bufferAlloc.heapBuffer(bufferSize, bufferSize);
        } else {
            buf = bufferAlloc.buffer(bufferSize, bufferSize);
        }
        return new NativeZlibDeflateStream(out, buf, dict, this);
    }

    @Override
    public NativeZlibDeflater retain() throws AlreadyReleasedException {
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

    @RequiredArgsConstructor
    private static final class Releaser implements Runnable {
        private final long ctx;

        @Override
        public void run() {
            release0(this.ctx);
        }
    }
}
