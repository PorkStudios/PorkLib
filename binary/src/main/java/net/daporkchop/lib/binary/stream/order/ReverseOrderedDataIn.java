/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
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

import java.io.EOFException;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.StandardCharsets;

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
    public short readShort() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readShortLE();
    }

    @Override
    public int readUnsignedShort() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readUnsignedShortLE();
    }

    @Override
    public short readShortLE() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readShort();
    }

    @Override
    public int readUnsignedShortLE() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readUnsignedShort();
    }

    @Override
    public char readChar() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readCharLE();
    }

    @Override
    public char readCharLE() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readChar();
    }

    @Override
    public int readInt() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readIntLE();
    }

    @Override
    public int readIntLE() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readInt();
    }

    @Override
    public long readLong() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readLongLE();
    }

    @Override
    public long readLongLE() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readLong();
    }

    @Override
    public float readFloat() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readFloatLE();
    }

    @Override
    public float readFloatLE() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readFloat();
    }

    @Override
    public double readDouble() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readDoubleLE();
    }

    @Override
    public double readDoubleLE() throws ClosedChannelException, EOFException, IOException {
        return this.delegate.readDouble();
    }

    @Override
    public String readUTF() throws ClosedChannelException, EOFException, IOException {
        return this.readString(this.readUnsignedShort(), StandardCharsets.UTF_8);
    }

    @Override
    public void close() throws IOException {
        this.delegate.close();
    }
}
