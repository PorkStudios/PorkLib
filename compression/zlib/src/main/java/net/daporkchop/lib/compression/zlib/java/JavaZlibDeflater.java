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

package net.daporkchop.lib.compression.zlib.java;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.compression.util.exception.DictionaryNotAllowedException;
import net.daporkchop.lib.compression.zlib.ZlibDeflater;
import net.daporkchop.lib.compression.zlib.ZlibMode;
import net.daporkchop.lib.compression.zlib.options.ZlibDeflaterOptions;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.util.zip.Deflater;

import static java.lang.Math.min;
import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
class JavaZlibDeflater extends AbstractRefCounted.Synchronized implements ZlibDeflater {
    final Deflater deflater;

    @Getter
    final ZlibDeflaterOptions options;

    JavaZlibDeflater(@NonNull ZlibDeflaterOptions options)  {
        this.options = options;
        this.deflater = new Deflater(options.level(), options.mode() == ZlibMode.RAW);
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
        this.ensureNotReleased();
        checkArg(src.isReadable(), "src is not readable!");
        checkArg(dst.isWritable(), "dst is not writable!");
        throw new UnsupportedOperationException();
    }

    @Override
    public DataOut compressionStream(@NonNull DataOut out, ByteBufAllocator bufferAlloc, int bufferSize, ByteBuf dict) throws DictionaryNotAllowedException {
        return null;
    }

    @Override
    public JavaZlibDeflater retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }
}
