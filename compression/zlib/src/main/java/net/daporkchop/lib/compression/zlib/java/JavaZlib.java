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

import net.daporkchop.lib.compression.zlib.ZlibDeflater;
import net.daporkchop.lib.compression.zlib.ZlibInflater;
import net.daporkchop.lib.compression.zlib.ZlibProvider;
import net.daporkchop.lib.natives.impl.NativeFeature;

/**
 * There is (currently) no Java implementation of Zstd.
 *
 * @author DaPorkchop_
 */
public final class JavaZlib extends NativeFeature<ZlibProvider> implements ZlibProvider {
    @Override
    public boolean directAccepted() {
        return false;
    }

    @Override
    public long compressBoundLong(long srcSize, int mode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ZlibDeflater deflater(int level, int strategy, int mode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ZlibInflater inflater(int mode) {
        throw new UnsupportedOperationException();
    }
}
