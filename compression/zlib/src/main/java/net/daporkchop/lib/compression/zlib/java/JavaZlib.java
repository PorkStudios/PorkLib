/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.compression.zlib.java;

import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.compression.zlib.Zlib;
import net.daporkchop.lib.compression.zlib.ZlibCCtx;
import net.daporkchop.lib.compression.zlib.ZlibDCtx;
import net.daporkchop.lib.compression.zlib.ZlibDeflater;
import net.daporkchop.lib.compression.zlib.ZlibInflater;
import net.daporkchop.lib.compression.zlib.ZlibProvider;
import net.daporkchop.lib.natives.FeatureBuilder;
import net.daporkchop.lib.natives.impl.NativeFeature;

/**
 * @author DaPorkchop_
 */
public final class JavaZlib extends NativeFeature<ZlibProvider> implements ZlibProvider {
    @Override
    public boolean directAccepted() {
        return false;
    }

    @Override
    public long compressBoundLong(long srcSize, int mode) {
        //extracted from deflate.c, i'm assuming that the java implementation has the same limits
        PValidation.ensureNonNegative(srcSize);
        long conservativeUpperBound = srcSize + ((srcSize + 7L) >> 3L) + ((srcSize + 63L) >> 6L) + 5L;
        switch (mode)   {
            case Zlib.MODE_ZLIB:
                return conservativeUpperBound + 6L + 4L; //additional +4 in case `strstart`? whatever that means
            case Zlib.MODE_GZIP:
                return conservativeUpperBound + 18L; //assume there is no gzip message
            case Zlib.MODE_RAW:
                return conservativeUpperBound;
            default:
                throw new IllegalArgumentException("Invalid Zlib compression mode: " + mode);
        }
    }

    @Override
    public ZlibDeflater deflater(int level, int strategy, int mode) {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public ZlibInflater inflater(int mode) {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public ZlibCCtx compressionContext(int level, int strategy, int mode) {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public ZlibDCtx decompressionContext(int mode) {
        throw new UnsupportedOperationException(); //TODO
    }
}
