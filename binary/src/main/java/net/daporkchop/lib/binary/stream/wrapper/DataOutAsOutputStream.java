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
import net.daporkchop.lib.binary.stream.DataOut;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Wraps a {@link DataOut} as an {@link OutputStream}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class DataOutAsOutputStream extends OutputStream {
    @NonNull
    protected final DataOut delegate;

    @Override
    public void write(int b) throws IOException {
        this.delegate.write(b);
    }

    @Override
    public void write(@NonNull byte[] src, int start, int length) throws IOException {
        this.delegate.write(src, start, length);
    }

    @Override
    public void flush() throws IOException {
        this.delegate.flush();
    }

    @Override
    public void close() throws IOException {
        this.delegate.close();
    }

    /**
     * An extension of {@link DataOutAsOutputStream} which doesn't forward the {@link OutputStream#close()} method to the delegate {@link DataOut}.
     *
     * @author DaPorkchop_
     */
    public static final class NonClosing extends DataOutAsOutputStream {
        public NonClosing(DataOut delegate) {
            super(delegate);
        }

        @Override
        public void close() throws IOException {
        }
    }
}
