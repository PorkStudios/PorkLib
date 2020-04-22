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
import net.daporkchop.lib.binary.oio.StreamUtil;
import net.daporkchop.lib.binary.stream.DataIn;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * An implementation of {@link DataIn} that can read data from an {@link InputStream}
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public class StreamIn implements DataIn {
    @NonNull
    protected final InputStream in;

    @Override
    public int read() throws IOException {
        return this.in.read();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        int b = this.in.read();
        if (b >= 0) {
            return b;
        } else {
            throw new EOFException();
        }
    }

    @Override
    public int read(@NonNull byte[] dst, int start, int length) throws IOException {
        return this.in.read(dst, start, length);
    }

    @Override
    public void readFully(@NonNull byte[] dst, int start, int length) throws IOException {
        StreamUtil.readFully(this.in, dst, start, length);
    }

    @Override
    public long skipBytes(long n) throws IOException {
        return this.in.skip(n);
    }

    @Override
    public InputStream asStream() throws IOException {
        return this.in;
    }

    @Override
    public void close() throws IOException {
        this.in.close();
    }

    /**
     * An extension of {@link StreamIn} which doesn't forwards the {@link DataIn#close()} method to the delegate {@link InputStream}.
     *
     * @author DaPorkchop_
     */
    public static final class NonClosing extends StreamIn  {
        public NonClosing(InputStream in) {
            super(in);
        }

        @Override
        public void close() throws IOException {
        }
    }
}
