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

package net.daporkchop.lib.binary.stream.wrapper;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataIn;

import java.io.IOException;
import java.io.InputStream;

/**
 * Wraps a {@link DataIn} as an {@link InputStream}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class DataInAsInputStream extends InputStream {
    @NonNull
    protected final DataIn delegate;

    @Override
    public int read() throws IOException {
        return this.delegate.read();
    }

    @Override
    public int read(@NonNull byte[] dst) throws IOException {
        return this.delegate.read(dst);
    }

    @Override
    public int read(@NonNull byte[] dst, int off, int len) throws IOException {
        return this.delegate.read(dst, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return this.delegate.skipBytes(n);
    }

    @Override
    public void close() throws IOException {
        this.delegate.close();
    }

    /**
     * An extension of {@link DataInAsInputStream} which doesn't forward the {@link InputStream#close()} method to the delegate {@link DataIn}.
     *
     * @author DaPorkchop_
     */
    public static final class NonClosing extends DataInAsInputStream {
        public NonClosing(DataIn in) {
            super(in);
        }

        @Override
        public void close() throws IOException {
        }
    }
}
