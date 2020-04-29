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

package net.daporkchop.lib.compression.zlib.natives;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.compression.util.exception.DictionaryNotAllowedException;
import net.daporkchop.lib.compression.zlib.ZlibInflater;
import net.daporkchop.lib.compression.zlib.options.ZlibInflaterOptions;
import net.daporkchop.lib.unsafe.PCleaner;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

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
final class NativeZlibInflater extends AbstractRefCounted.Synchronized implements ZlibInflater {
    static native long allocate0(int mode);

    static native void release0(long ctx);

    static native long newSession0(long ctx, long dict, int dictLen);

    static native int update0(long ctx, long src, int srcLen, long dst, int dstLen, int flush);

    final long ctx;

    @Getter
    final ZlibInflaterOptions options;

    final PCleaner cleaner;

    NativeZlibInflater(@NonNull ZlibInflaterOptions options) {
        this.options = options;

        this.ctx = allocate0(options.mode().ordinal());
        this.cleaner = PCleaner.cleaner(this, new Releaser(this.ctx));
    }

    @Override
    protected void doRelease() {
        checkState(this.cleaner.clean(), "already cleaned?!?");
    }

    @Override
    public boolean decompress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict) throws DictionaryNotAllowedException {
        return this.decompress0(src, dst, dict, false);
    }

    @Override
    public void decompressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict) throws DictionaryNotAllowedException, IndexOutOfBoundsException {
        checkState(this.decompress0(src, dst, dict, true), "decompress0() returned false when it should have grown!");
    }

    protected synchronized boolean decompress0(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict, boolean grow) {
        this.ensureNotReleased();
        checkArg(src.isReadable(), "src is not readable!");
        checkArg(dst.isWritable(), "dst is not writable!");
        if (src.hasMemoryAddress()) {
            long session;
            if (dict == null || dict.readableBytes() == 0) {
                //empty dictionary, no dictionary will be used
                session = newSession0(this.ctx, 0L, 0);
            } else if (dict.hasMemoryAddress()) {
                //dictionary is direct (this is fast)
                session = newSession0(this.ctx, dict.memoryAddress() + dict.readerIndex(), dict.readableBytes());
            } else {
                //dictionary isn't direct, we have to manually copy it into a new buffer
                try (Handle<ByteBuffer> handle = PorkUtil.DIRECT_BUFFER_POOL.get()) {
                    //this buffer is more than large enough to handle the largest valid dictionary, if a dictionary is WAY too large it'll probably break but you shouldn't be trying to do that in the first place :P
                    ByteBuffer buffer = handle.get();
                    buffer.clear().limit(min(buffer.capacity(), dict.readableBytes()));
                    dict.getBytes(dict.readerIndex(), buffer);
                    session = newSession0(this.ctx, PUnsafe.pork_directBufferAddress(buffer.position(0)), buffer.limit());
                }
            }

            try {
                return dst.hasMemoryAddress()
                       ? this.decompressNativeToNative0(src, dst, grow) //both source and destination are direct
                       : this.decompressNativeToOther0(src, dst, grow); //source is direct, destination is not
            } finally {
                if (session != this.getSession()) {
                    throw new ConcurrentModificationException(); //probably impossible
                }
            }
        } else {
            //the other two cases are too much of a pain to implement by hand
            int srcReaderIndex = src.readerIndex();
            int dstWriterIndex = dst.writerIndex();
            try (DataIn in = this.decompressionStream(DataIn.wrap(src), dict);
                 DataOut out = DataOut.wrap(dst, true, grow)) {
                in.transferTo(out);
            } catch (IOException e) {
                //shouldn't be possible
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
    }

    protected boolean decompressNativeToNative0(@NonNull ByteBuf src, @NonNull ByteBuf dst, boolean grow) {
        long srcAddr = src.memoryAddress() + src.readerIndex();
        int srcLen = src.readableBytes();

        int totalRead = 0;
        int totalWritten = 0;
        int status;
        do {
            status = update0(this.ctx,
                    srcAddr + totalRead,
                    srcLen - totalRead,
                    dst.memoryAddress() + dst.writerIndex() + totalWritten,
                    dst.writableBytes() - totalWritten,
                    Z_NO_FLUSH);

            totalRead += toInt(this.getRead(), "read");
            totalWritten += toInt(this.getWritten(), "written");

            if (status == Z_OK) {
                if (grow) {
                    dst.ensureWritable(dst.writableBytes() == dst.maxWritableBytes() ? 8192 : min(dst.maxWritableBytes(), totalWritten + 8192));
                } else {
                    return false;
                }
            }
        } while (status != Z_STREAM_END);

        src.skipBytes(totalRead);
        dst.writerIndex(dst.writerIndex() + totalWritten);
        return true;
    }

    protected boolean decompressNativeToOther0(@NonNull ByteBuf src, @NonNull ByteBuf dst, boolean grow) {
        int dstWriterIndex = dst.writerIndex();
        try (Handle<ByteBuffer> dstHandle = PorkUtil.DIRECT_BUFFER_POOL.get()) {
            ByteBuffer tempDst = dstHandle.get();

            long srcAddr = src.memoryAddress() + src.readerIndex();
            int srcLen = src.readableBytes();
            long dstAddr = PUnsafe.pork_directBufferAddress(tempDst);

            int totalRead = 0;
            int status;
            do {
                status = update0(this.ctx, srcAddr + totalRead, srcLen - totalRead, dstAddr, PorkUtil.BUFFER_SIZE, Z_NO_FLUSH);

                totalRead += toInt(this.getRead(), "read");
                int written = toInt(this.getWritten(), "written");

                tempDst.position(0).limit(written);
                if (!grow && written > dst.writableBytes()) {
                    dst.writerIndex(dstWriterIndex);
                    return false;
                }
                dst.writeBytes(tempDst);
            } while (status != Z_STREAM_END);

            src.skipBytes(totalRead);
            return true;
        }
    }

    @Override
    public synchronized DataIn decompressionStream(@NonNull DataIn in, ByteBufAllocator bufferAlloc, int bufferSize, ByteBuf dict) throws DictionaryNotAllowedException {
        this.ensureNotReleased();
        if (bufferAlloc == null) {
            bufferAlloc = PooledByteBufAllocator.DEFAULT;
        }
        if (bufferSize <= 0) {
            bufferSize = PorkUtil.BUFFER_SIZE;
        }
        return new NativeZlibInflateStream(in, bufferAlloc.directBuffer(bufferSize, bufferSize), dict, this);
    }

    @Override
    public NativeZlibInflater retain() throws AlreadyReleasedException {
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
