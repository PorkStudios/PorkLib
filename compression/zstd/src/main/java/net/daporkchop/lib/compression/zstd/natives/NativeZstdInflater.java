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
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.compression.zstd.ZstdInflateDictionary;
import net.daporkchop.lib.compression.zstd.ZstdInflater;
import net.daporkchop.lib.compression.zstd.options.ZstdInflaterOptions;
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
final class NativeZstdInflater extends AbstractRefCounted.Synchronized implements ZstdInflater {
    static native long allocate0();

    static native void release0(long ctx);

    static native long newSession0(long ctx);

    static native long decompressD2D0(long ctx, long src, int srcLen, long dst, int dstLen);

    static native long decompressD2H0(long ctx, long src, int srcLen, byte[] dst, int dstOff, int dstLen);

    static native long decompressH2D0(long ctx, byte[] src, int srcOff, int srcLen, long dst, int dstLen);

    static native long decompressH2H0(long ctx, byte[] src, int srcOff, int srcLen, byte[] dst, int dstOff, int dstLen);

    static native long decompressD2DWithDict0(long ctx, long src, int srcLen, long dst, int dstLen, long dict);

    static native long decompressD2HWithDict0(long ctx, long src, int srcLen, byte[] dst, int dstOff, int dstLen, long dict);

    static native long decompressH2DWithDict0(long ctx, byte[] src, int srcOff, int srcLen, long dst, int dstLen, long dict);

    static native long decompressH2HWithDict0(long ctx, byte[] src, int srcOff, int srcLen, byte[] dst, int dstOff, int dstLen, long dict);

    static native long decompressD2DWithDictD0(long ctx, long src, int srcLen, long dst, int dstLen, long dict, int dictLen);

    static native long decompressD2HWithDictD0(long ctx, long src, int srcLen, byte[] dst, int dstOff, int dstLen, long dict, int dictLen);

    static native long decompressH2DWithDictD0(long ctx, byte[] src, int srcOff, int srcLen, long dst, int dstLen, long dict, int dictLen);

    static native long decompressH2HWithDictD0(long ctx, byte[] src, int srcOff, int srcLen, byte[] dst, int dstOff, int dstLen, long dict, int dictLen);

    static native long decompressD2DWithDictH0(long ctx, long src, int srcLen, long dst, int dstLen, byte[] dict, int dictOff, int dictLen);

    static native long decompressD2HWithDictH0(long ctx, long src, int srcLen, byte[] dst, int dstOff, int dstLen, byte[] dict, int dictOff, int dictLen);

    static native long decompressH2DWithDictH0(long ctx, byte[] src, int srcOff, int srcLen, long dst, int dstLen, byte[] dict, int dictOff, int dictLen);

    static native long decompressH2HWithDictH0(long ctx, byte[] src, int srcOff, int srcLen, byte[] dst, int dstOff, int dstLen, byte[] dict, int dictOff, int dictLen);

    //streaming

    static native long newSessionWithDict0(long ctx, long dict);

    static native long newSessionWithDictD0(long ctx, long dict, int dictLen);

    static native long newSessionWithDictH0(long ctx, byte[] dict, int dictOff, int dictLen);

    static native long updateD2D0(long ctx, long src, int srcLen, long dst, int dstLen);

    static native long updateD2H0(long ctx, long src, int srcLen, byte[] dst, int dstOff, int dstLen);

    static native long updateH2D0(long ctx, byte[] src, int srcOff, int srcLen, long dst, int dstLen);

    static native long updateH2H0(long ctx, byte[] src, int srcOff, int srcLen, byte[] dst, int dstOff, int dstLen);

    final long ctx;

    @Getter
    final ZstdInflaterOptions options;

    final PCleaner cleaner;

    NativeZstdInflater(@NonNull ZstdInflaterOptions options) {
        this.options = options;

        this.ctx = allocate0();
        this.cleaner = PCleaner.cleaner(this, new Releaser(this.ctx));
    }

    @Override
    protected void doRelease() {
        this.cleaner.clean();
    }

    @Override
    public synchronized boolean decompress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict) {
        this.ensureNotReleased();

        if (!(src.hasMemoryAddress() || src.hasArray()) || !(dst.hasMemoryAddress() || dst.hasArray())) {
            //the other cases are too much of a pain to implement by hand
            int srcReaderIndex = src.readerIndex();
            int dstWriterIndex = dst.writerIndex();
            try (DataIn in = this.decompressionStream(DataIn.wrap(src), null, -1, dict);
                 DataOut out = DataOut.wrap(dst, true, false)) {
                out.transferFrom(in);
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

        if (dict == null || !dict.isReadable()) {
            return this.decompressNoDict(src, dst);
        }

        boolean releaseDict = false;
        try {
            if (!dict.hasMemoryAddress() && !dict.hasArray()) {
                //dict is composite
                ByteBuf buf = Unpooled.directBuffer(dict.readableBytes(), dict.readableBytes());
                dict.getBytes(dict.readerIndex(), buf);
                dict = buf;
                releaseDict = true;
            }

            long session = newSession0(this.ctx);
            try {
                long ret = -1L;
                if (dict.hasMemoryAddress()) {
                    if (src.hasMemoryAddress()) {
                        if (dst.hasMemoryAddress()) {
                            ret = decompressD2DWithDictD0(this.ctx,
                                    src.memoryAddress() + src.readerIndex(), src.readableBytes(),
                                    dst.memoryAddress() + dst.writerIndex(), dst.writableBytes(),
                                    dict.memoryAddress() + dict.readerIndex(), dict.readableBytes());
                        } else if (dst.hasArray()) {
                            ret = decompressD2HWithDictD0(this.ctx,
                                    src.memoryAddress() + src.readerIndex(), src.readableBytes(),
                                    dst.array(), dst.arrayOffset() + dst.writerIndex(), dst.writableBytes(),
                                    dict.memoryAddress() + dict.readerIndex(), dict.readableBytes());
                        }
                    } else if (src.hasArray()) {
                        if (dst.hasMemoryAddress()) {
                            ret = decompressH2DWithDictD0(this.ctx,
                                    src.array(), src.arrayOffset() + src.readerIndex(), src.readableBytes(),
                                    dst.memoryAddress() + dst.writerIndex(), dst.writableBytes(),
                                    dict.memoryAddress() + dict.readerIndex(), dict.readableBytes());
                        } else if (dst.hasArray()) {
                            ret = decompressH2HWithDictD0(this.ctx,
                                    src.array(), src.arrayOffset() + src.readerIndex(), src.readableBytes(),
                                    dst.array(), dst.arrayOffset() + dst.writerIndex(), dst.writableBytes(),
                                    dict.memoryAddress() + dict.readerIndex(), dict.readableBytes());
                        }
                    }
                } else if (dict.hasArray()) {
                    if (src.hasMemoryAddress()) {
                        if (dst.hasMemoryAddress()) {
                            ret = decompressD2DWithDictH0(this.ctx,
                                    src.memoryAddress() + src.readerIndex(), src.readableBytes(),
                                    dst.memoryAddress() + dst.writerIndex(), dst.writableBytes(),
                                    dict.array(), dict.arrayOffset() + dict.readerIndex(), dict.readableBytes());
                        } else if (dst.hasArray()) {
                            ret = decompressD2HWithDictH0(this.ctx,
                                    src.memoryAddress() + src.readerIndex(), src.readableBytes(),
                                    dst.array(), dst.arrayOffset() + dst.writerIndex(), dst.writableBytes(),
                                    dict.array(), dict.arrayOffset() + dict.readerIndex(), dict.readableBytes());
                        }
                    } else if (src.hasArray()) {
                        if (dst.hasMemoryAddress()) {
                            ret = decompressH2DWithDictH0(this.ctx,
                                    src.array(), src.arrayOffset() + src.readerIndex(), src.readableBytes(),
                                    dst.memoryAddress() + dst.writerIndex(), dst.writableBytes(),
                                    dict.array(), dict.arrayOffset() + dict.readerIndex(), dict.readableBytes());
                        } else if (dst.hasArray()) {
                            ret = decompressH2HWithDictH0(this.ctx,
                                    src.array(), src.arrayOffset() + src.readerIndex(), src.readableBytes(),
                                    dst.array(), dst.arrayOffset() + dst.writerIndex(), dst.writableBytes(),
                                    dict.array(), dict.arrayOffset() + dict.readerIndex(), dict.readableBytes());
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
    public synchronized boolean decompress(@NonNull ByteBuf src, @NonNull ByteBuf dst, ZstdInflateDictionary dict) {
        this.ensureNotReleased();

        if (!(src.hasMemoryAddress() || src.hasArray()) || !(dst.hasMemoryAddress() || dst.hasArray())) {
            //the other cases are too much of a pain to implement by hand
            int srcReaderIndex = src.readerIndex();
            int dstWriterIndex = dst.writerIndex();
            try (DataIn in = this.decompressionStream(DataIn.wrap(src), null, -1, dict);
                 DataOut out = DataOut.wrap(dst, true, false)) {
                out.transferFrom(in);
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

        if (dict == null) {
            return this.decompressNoDict(src, dst);
        }

        checkArg(dict instanceof NativeZstdInflateDictionary, "invalid dictionary: %s", dict);
        long dictAddr = ((NativeZstdInflateDictionary) dict.retain()).addr();

        long session = newSession0(this.ctx);
        try {
            long ret = -1L;
            if (src.hasMemoryAddress()) {
                if (dst.hasMemoryAddress()) {
                    ret = decompressD2DWithDict0(this.ctx,
                            src.memoryAddress() + src.readerIndex(), src.readableBytes(),
                            dst.memoryAddress() + dst.writerIndex(), dst.writableBytes(),
                            dictAddr);
                } else if (dst.hasArray()) {
                    ret = decompressD2HWithDict0(this.ctx,
                            src.memoryAddress() + src.readerIndex(), src.readableBytes(),
                            dst.array(), dst.arrayOffset() + dst.writerIndex(), dst.writableBytes(),
                            dictAddr);
                }
            } else if (src.hasArray()) {
                if (dst.hasMemoryAddress()) {
                    ret = decompressH2DWithDict0(this.ctx,
                            src.array(), src.arrayOffset() + src.readerIndex(), src.readableBytes(),
                            dst.memoryAddress() + dst.writerIndex(), dst.writableBytes(),
                            dictAddr);
                } else if (dst.hasArray()) {
                    ret = decompressH2HWithDict0(this.ctx,
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

    boolean decompressNoDict(@NonNull ByteBuf src, @NonNull ByteBuf dst) {
        long ret = -1L;
        if (src.hasMemoryAddress()) {
            if (dst.hasMemoryAddress()) {
                ret = decompressD2D0(this.ctx,
                        src.memoryAddress() + src.readerIndex(), src.readableBytes(),
                        dst.memoryAddress() + dst.writerIndex(), dst.writableBytes());
            } else if (dst.hasArray()) {
                ret = decompressD2H0(this.ctx,
                        src.memoryAddress() + src.readerIndex(), src.readableBytes(),
                        dst.array(), dst.arrayOffset() + dst.writerIndex(), dst.writableBytes());
            }
        } else if (src.hasArray()) {
            if (dst.hasMemoryAddress()) {
                ret = decompressH2D0(this.ctx,
                        src.array(), src.arrayOffset() + src.readerIndex(), src.readableBytes(),
                        dst.memoryAddress() + dst.writerIndex(), dst.writableBytes());
            } else if (dst.hasArray()) {
                ret = decompressH2H0(this.ctx,
                        src.array(), src.arrayOffset() + src.readerIndex(), src.readableBytes(),
                        dst.array(), dst.arrayOffset() + dst.writerIndex(), dst.writableBytes());
            }
        }

        if (ret >= 0L) {
            src.skipBytes(src.readableBytes());
            dst.writerIndex(dst.writerIndex() + toInt(ret));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public synchronized void decompressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst, ByteBuf dict) throws IndexOutOfBoundsException {
        this.ensureNotReleased();
    }

    @Override
    public synchronized void decompressGrowing(@NonNull ByteBuf src, @NonNull ByteBuf dst, ZstdInflateDictionary dict) throws IndexOutOfBoundsException {
        this.ensureNotReleased();
    }

    @Override
    public synchronized DataIn decompressionStream(@NonNull DataIn in, ByteBufAllocator bufferAlloc, int bufferSize, ByteBuf dict) throws IOException {
        this.ensureNotReleased();
        return null;
    }

    @Override
    public synchronized DataIn decompressionStream(@NonNull DataIn in, ByteBufAllocator bufferAlloc, int bufferSize, ZstdInflateDictionary dict) throws IOException {
        this.ensureNotReleased();
        return null;
    }

    @Override
    public NativeZstdInflater retain() throws AlreadyReleasedException {
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
