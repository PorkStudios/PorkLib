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

package net.daporkchop.lib.binary.stream.order;

import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.binary.stream.wrapper.ForwardingDataOut;

import java.io.IOException;

/**
 * A wrapper around another {@link DataOut} which reverses the byte order used.
 * <p>
 * This class violates the {@link DataOut} contract, and as such should be considered unsafe.
 *
 * @author DaPorkchop_
 */
public class ReverseOrderedDataOut extends ForwardingDataOut {
    public ReverseOrderedDataOut(DataOut delegate) {
        super(delegate);
    }

    @Override
    public void writeShort(int v) throws IOException {
        this.delegate.writeShortLE(v);
    }

    @Override
    public void writeShortLE(int v) throws IOException {
        this.delegate.writeShort(v);
    }

    @Override
    public void writeChar(int v) throws IOException {
        this.delegate.writeCharLE(v);
    }

    @Override
    public void writeCharLE(int v) throws IOException {
        this.delegate.writeChar(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        this.delegate.writeIntLE(v);
    }

    @Override
    public void writeIntLE(int v) throws IOException {
        this.delegate.writeInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        this.delegate.writeLongLE(v);
    }

    @Override
    public void writeLongLE(long v) throws IOException {
        this.delegate.writeLong(v);
    }

    @Override
    public void writeFloat(float f) throws IOException {
        this.delegate.writeFloatLE(f);
    }

    @Override
    public void writeFloatLE(float f) throws IOException {
        this.delegate.writeFloat(f);
    }

    @Override
    public void writeDouble(double d) throws IOException {
        this.delegate.writeDoubleLE(d);
    }

    @Override
    public void writeDoubleLE(double d) throws IOException {
        this.delegate.writeDouble(d);
    }

    @Override
    public void close() throws IOException {
        this.delegate.close();
    }
}
