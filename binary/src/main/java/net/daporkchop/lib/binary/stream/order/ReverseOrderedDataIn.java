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

import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.wrapper.ForwardingDataIn;

import java.io.IOException;

/**
 * A wrapper around another {@link DataIn} which reverses the byte order used.
 * <p>
 * This class violates the {@link DataIn} contract, and as such should be considered unsafe.
 *
 * @author DaPorkchop_
 */
public class ReverseOrderedDataIn extends ForwardingDataIn {
    public ReverseOrderedDataIn(DataIn delegate) {
        super(delegate);
    }

    @Override
    public short readShort() throws IOException {
        return this.delegate.readShortLE();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return this.delegate.readUnsignedShortLE();
    }

    @Override
    public short readShortLE() throws IOException {
        return this.delegate.readShort();
    }

    @Override
    public int readUnsignedShortLE() throws IOException {
        return this.delegate.readUnsignedShort();
    }

    @Override
    public char readChar() throws IOException {
        return this.delegate.readCharLE();
    }

    @Override
    public char readCharLE() throws IOException {
        return this.delegate.readChar();
    }

    @Override
    public int readInt() throws IOException {
        return this.delegate.readIntLE();
    }

    @Override
    public int readIntLE() throws IOException {
        return this.delegate.readInt();
    }

    @Override
    public long readLong() throws IOException {
        return this.delegate.readLongLE();
    }

    @Override
    public long readLongLE() throws IOException {
        return this.delegate.readLong();
    }

    @Override
    public float readFloat() throws IOException {
        return this.delegate.readFloatLE();
    }

    @Override
    public float readFloatLE() throws IOException {
        return this.delegate.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return this.delegate.readDoubleLE();
    }

    @Override
    public double readDoubleLE() throws IOException {
        return this.delegate.readDouble();
    }

    @Override
    public void close() throws IOException {
        this.delegate.close();
    }
}
