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

import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.binary.stream.wrapper.ForwardingDataOut;
import net.daporkchop.lib.binary.util.NoMoreSpaceException;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.StandardCharsets;

import static net.daporkchop.lib.common.util.PValidation.*;

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
    public void writeShort(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeShortLE(v);
    }

    @Override
    public void writeShortLE(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeShort(v);
    }

    @Override
    public void writeChar(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeCharLE(v);
    }

    @Override
    public void writeCharLE(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeChar(v);
    }

    @Override
    public void writeInt(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeIntLE(v);
    }

    @Override
    public void writeIntLE(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeInt(v);
    }

    @Override
    public void writeLong(long v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeLongLE(v);
    }

    @Override
    public void writeLongLE(long v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeLong(v);
    }

    @Override
    public void writeFloat(float v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeFloatLE(v);
    }

    @Override
    public void writeFloatLE(float v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeFloat(v);
    }

    @Override
    public void writeDouble(double v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeDoubleLE(v);
    }

    @Override
    public void writeDoubleLE(double v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeDouble(v);
    }

    @Override
    public void writeUTF(@NonNull String text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        byte[] arr = text.getBytes(StandardCharsets.UTF_8);
        checkArg(arr.length <= Character.MAX_VALUE, "encoded value is too large (%d > %d)", arr.length, Character.MAX_VALUE);
        this.writeShort(arr.length);
        this.write(arr);
    }

    @Override
    public void writeChars(@NonNull String text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeCharsLE(text);
    }

    @Override
    public long writeChars(@NonNull CharSequence text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return this.delegate.writeCharsLE(text);
    }

    @Override
    public long writeChars(@NonNull CharSequence text, int offset, int length) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return this.delegate.writeCharsLE(text, offset, length);
    }

    @Override
    public void writeCharsLE(@NonNull String text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        this.delegate.writeChars(text);
    }

    @Override
    public long writeCharsLE(@NonNull CharSequence text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return this.delegate.writeChars(text);
    }

    @Override
    public long writeCharsLE(@NonNull CharSequence text, int offset, int length) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return this.delegate.writeChars(text, offset, length);
    }

    @Override
    public void close() throws IOException {
        this.delegate.close();
    }
}
