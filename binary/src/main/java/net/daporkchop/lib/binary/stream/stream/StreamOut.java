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

package net.daporkchop.lib.binary.stream.stream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.AbstractHeapDataOut;
import net.daporkchop.lib.binary.stream.DataOut;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Wraps an {@link OutputStream} as a {@link DataOut}.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
@Getter
@Accessors(fluent = true, chain = true)
public class StreamOut extends AbstractHeapDataOut {
    @NonNull
    protected final OutputStream delegate;

    @Override
    protected void write0(int b) throws IOException {
        this.delegate.write(b);
    }

    @Override
    protected int writeSome0(@NonNull byte[] src, int start, int length) throws IOException {
        this.delegate.write(src, start, length);
        return length;
    }

    @Override
    protected void writeAll0(@NonNull byte[] src, int start, int length) throws IOException {
        this.delegate.write(src, start, length);
    }

    @Override
    protected void flush0() throws IOException {
        this.delegate.flush();
    }

    @Override
    protected void close0() throws IOException {
        this.delegate.close();
    }

    /**
     * An extension of {@link StreamOut} which doesn't forward the {@link OutputStream#close()} method to the delegate {@link OutputStream}.
     *
     * @author DaPorkchop_
     */
    public static final class NonClosing extends StreamOut {
        public NonClosing(OutputStream delegate) {
            super(delegate);
        }

        @Override
        protected void close0() throws IOException {
        }
    }
}
