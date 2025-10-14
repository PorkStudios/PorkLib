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
import lombok.NonNull;
import net.daporkchop.lib.common.annotation.Borrow;
import net.daporkchop.lib.common.util.PNioBuffers;
import net.daporkchop.lib.compression.zstd.Zstd;
import net.daporkchop.lib.compression.zstd.ZstdCompressDictionary;
import net.daporkchop.lib.compression.zstd.ZstdDecompressDictionary;
import net.daporkchop.lib.compression.zstd.ZstdOneshotCompressor;
import net.daporkchop.lib.compression.zstd.ZstdOneshotDecompressor;
import net.daporkchop.lib.natives.util.MemoryPreference;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.nio.ByteBuffer;

/**
 * @author DaPorkchop_
 */
public final class JniZstdProvider extends NativeZstdProvider {
    static {
        //TODO: load jni library
    }

    /*private static native void configure(boolean allowJniCritical);

    static {
        configure(NativeUtils.allowJniCritical());
    }*/

    @Override
    native boolean ZSTD_isError(long code);

    @Override
    native int ZSTD_getErrorCode(long code);

    @Override
    native String ZSTD_getErrorName(long code);

    @Override
    native long ZSTD_createCCtx();

    @Override
    native void ZSTD_freeCCtx(long cctx);

    @Override
    native long ZSTD_CCtx_refCDict(long cctx, long cdict);

    @Override
    native long ZSTD_CCtx_reset(long cctx, int reset);

    @Override
    native long ZSTD_CCtx_setParameter(long cctx, int param, int value);

    @Override
    native void ZSTD_freeCDict(long cdict);

    @Override
    native int ZSTD_getDictID_fromCDict(long cdict);

    private native long ZSTD_createCDict(
            long dictAddr, byte[] dictArray, int dictArrayOffset, int dictPosition, int dictRemaining,
            int level);

    @Override
    native long ZSTD_createDCtx();

    @Override
    native void ZSTD_freeDCtx(long dctx);

    @Override
    native long ZSTD_DCtx_refDDict(long dctx, long ddict);

    @Override
    native long ZSTD_DCtx_reset(long dctx, int reset);

    @Override
    native long ZSTD_DCtx_setParameter(long dctx, int param, int value);

    private native long ZSTD_createDDict(
            long dictAddr, byte[] dictArray, int dictArrayOffset, int dictPosition, int dictRemaining);

    @Override
    native void ZSTD_freeDDict(long ddict);

    @Override
    native int ZSTD_getDictID_fromDDict(long ddict);

    @Override
    public MemoryPreference memoryPreference() {
        //TODO: decide this based on whether or not JNI pinning is enabled?
        return MemoryPreference.PREFER_DIRECT;
    }

    @Override
    public ZstdCompressDictionary makeCompressionDictionary(@Borrow @NonNull ByteBuffer dict, int level) throws IllegalArgumentException {
        Zstd.checkLevel(level);

        long dictMemoryAddress = 0L;
        byte[] dictArray = null;
        int dictArrayOffset = 0;
        if (dict.isDirect()) {
            dictMemoryAddress = PUnsafe.pork_directBufferAddress(dict);
        } else if (dict.hasArray()) {
            dictArray = dict.array();
            dictArrayOffset = dict.arrayOffset();
        } else {
            // This is most likely a read-only heap buffer, or another buffer of some unknown type.
            // Since we can't access the underlying storage, we'll copy it to a heap array (slow!!!)
            dictArray = PNioBuffers.toArray(dict);
        }

        return new NativeZstdCDict(this, this.ZSTD_createCDict(
                dictMemoryAddress, dictArray, dictArrayOffset, dict.position(), dict.remaining(),
                level));
    }

    @Override
    public ZstdCompressDictionary makeCompressionDictionary(@Borrow @NonNull ByteBuf dict, int level) throws IllegalArgumentException {
        Zstd.checkLevel(level);

        //get buffer pointers
        long dictMemoryAddress = 0L;
        byte[] dictArray = null;
        int dictArrayOffset = 0;
        if (dict.hasMemoryAddress()) {
            dictMemoryAddress = dict.memoryAddress();
        } else if (dict.hasArray()) {
            dictArray = dict.array();
            dictArrayOffset = dict.arrayOffset();
        } else {
            // This is most likely a read-only heap buffer or a composite buffer, although it could be another buffer of some unknown type.
            // Since we can't access the underlying storage, we'll copy it to a temporary buffer (slow!!!)
            ByteBuf copy = dict.alloc().directBuffer(dict.readableBytes(), dict.readableBytes());
            try {
                dict.getBytes(dict.readerIndex(), copy);
                return this.makeCompressionDictionary(copy, level);
            } finally {
                copy.release();
            }
        }

        return new NativeZstdCDict(this, this.ZSTD_createCDict(
                dictMemoryAddress, dictArray, dictArrayOffset, dict.readerIndex(), dict.readableBytes(),
                level));
    }

    @Override
    public ZstdDecompressDictionary makeDecompressionDictionary(@Borrow @NonNull ByteBuffer dict) {
        //get buffer pointers
        long dictMemoryAddress = 0L;
        byte[] dictArray = null;
        int dictArrayOffset = 0;
        if (dict.isDirect()) {
            dictMemoryAddress = PUnsafe.pork_directBufferAddress(dict);
        } else if (dict.hasArray()) {
            dictArray = dict.array();
            dictArrayOffset = dict.arrayOffset();
        } else {
            // This is most likely a read-only heap buffer, or another buffer of some unknown type.
            // Since we can't access the underlying storage, we'll copy it to a heap array (slow!!!)
            dictArray = PNioBuffers.toArray(dict);
        }

        return new NativeZstdDDict(this, this.ZSTD_createDDict(
                dictMemoryAddress, dictArray, dictArrayOffset, dict.position(), dict.remaining()));
    }

    @Override
    public ZstdDecompressDictionary makeDecompressionDictionary(@Borrow @NonNull ByteBuf dict) {
        //get buffer pointers
        long dictMemoryAddress = 0L;
        byte[] dictArray = null;
        int dictArrayOffset = 0;
        if (dict.hasMemoryAddress()) {
            dictMemoryAddress = dict.memoryAddress();
        } else if (dict.hasArray()) {
            dictArray = dict.array();
            dictArrayOffset = dict.arrayOffset();
        } else {
            // This is most likely a read-only heap buffer or a composite buffer, although it could be another buffer of some unknown type.
            // Since we can't access the underlying storage, we'll copy it to a temporary buffer (slow!!!)
            ByteBuf copy = dict.alloc().directBuffer(dict.readableBytes(), dict.readableBytes());
            try {
                dict.getBytes(dict.readerIndex(), copy);
                return this.makeDecompressionDictionary(copy);
            } finally {
                copy.release();
            }
        }

        return new NativeZstdDDict(this, this.ZSTD_createDDict(
                dictMemoryAddress, dictArray, dictArrayOffset, dict.readerIndex(), dict.readableBytes()));
    }

    @Override
    public ZstdOneshotCompressor makeOneshotCompressor() {
        return new JniZstdCCtx(this);
    }

    @Override
    public ZstdOneshotDecompressor makeOneshotDecompressor() {
        return null; //TODO
    }
}
