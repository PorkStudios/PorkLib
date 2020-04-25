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

import lombok.NonNull;
import net.daporkchop.lib.compression.context.PDeflater;
import net.daporkchop.lib.compression.context.PInflater;
import net.daporkchop.lib.compression.zlib.ZlibDeflater;
import net.daporkchop.lib.compression.zlib.ZlibInflater;
import net.daporkchop.lib.compression.zlib.ZlibMode;
import net.daporkchop.lib.compression.zlib.ZlibProvider;
import net.daporkchop.lib.compression.zlib.options.ZlibDeflaterOptions;
import net.daporkchop.lib.compression.zlib.options.ZlibInflaterOptions;
import net.daporkchop.lib.natives.impl.NativeFeature;

/**
 * @author DaPorkchop_
 */
public final class NativeZlib extends NativeFeature<ZlibProvider> implements ZlibProvider {
    static {
        NativeZlibDeflater.load();
        NativeZlibInflater.load();
    }

    @Override
    public boolean directAccepted() {
        return true;
    }

    @Override
    public long compressBoundLong(long srcSize, @NonNull ZlibMode mode) {
        return 0;
    }

    @Override
    public ZlibDeflaterOptions defaultDeflaterOptions() {
        return null;
    }

    @Override
    public ZlibInflaterOptions defaultInflaterOptions() {
        return null;
    }

    @Override
    public PDeflater deflater(@NonNull ZlibDeflaterOptions options) {
        return null;
    }

    @Override
    public PInflater inflater(@NonNull ZlibInflaterOptions options) {
        return null;
    }
}
