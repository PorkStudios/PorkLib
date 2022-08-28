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

package binary.stream.buffer;

import lombok.AllArgsConstructor;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.annotation.param.NotNegative;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
public class TestWrappingByteBuffer extends AbstractTestWrappingBuffer {
    @Override
    protected Iterable<? extends BufferAllocator> bufferAllocators() {
        return Arrays.asList(
                BufferAllocator.ofFixed("heap", Integer.MAX_VALUE, capacity -> new ByteBufferWrapper(ByteBuffer.allocate(toInt(capacity)))),
                BufferAllocator.ofFixed("heap (DataIn backed by read-only slice)", Integer.MAX_VALUE, capacity -> new ByteBufferWrapper_DataInReadOnly(ByteBuffer.allocate(toInt(capacity)))),
                BufferAllocator.ofFixed("direct", Integer.MAX_VALUE, capacity -> new ByteBufferWrapper(ByteBuffer.allocateDirect(toInt(capacity)))),
                BufferAllocator.ofFixed("direct (DataIn backed by read-only slice)", Integer.MAX_VALUE, capacity -> new ByteBufferWrapper_DataInReadOnly(ByteBuffer.allocateDirect(toInt(capacity))))
        );
    }

    @AllArgsConstructor
    protected static class ByteBufferWrapper implements GenericBuffer {
        protected ByteBuffer delegate;

        @Override
        public void close() {
            PUnsafe.pork_releaseBuffer(this.delegate);
            this.delegate = null;
        }

        @Override
        public boolean resizable() {
            return false;
        }

        @Override
        public boolean autoGrowing() {
            return false;
        }

        @Override
        public @NotNegative long capacity() {
            return this.delegate.capacity();
        }

        @Override
        public void capacity(@NotNegative long capacity) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public @NotNegative long maxCapacity() {
            return this.delegate.capacity();
        }

        @Override
        public void clear() {
            this.delegate.clear();
        }

        @Override
        public void flip() {
            this.delegate.flip();
        }

        @Override
        public @NotNegative long remaining() {
            return this.delegate.remaining();
        }

        @Override
        public DataOut wrapOutNonGrowing() {
            return DataOut.wrap(this.delegate);
        }

        @Override
        public DataOut wrapOutAutoGrowing() {
            return DataOut.wrap(this.delegate);
        }

        @Override
        public DataIn wrapIn() {
            return DataIn.wrap(this.delegate);
        }

        @Override
        public byte getByte(ByteOrder order) {
            return this.delegate.get();
        }

        @Override
        public void putByte(byte b, ByteOrder order) {
            this.delegate.put(b);
        }

        @Override
        public short getShort(ByteOrder order) {
            return this.delegate.order(order).getShort();
        }

        @Override
        public void putShort(short b, ByteOrder order) {
            this.delegate.order(order).putShort(b);
        }

        @Override
        public char getChar(ByteOrder order) {
            return this.delegate.order(order).getChar();
        }

        @Override
        public void putChar(char b, ByteOrder order) {
            this.delegate.order(order).putChar(b);
        }

        @Override
        public int getInt(ByteOrder order) {
            return this.delegate.order(order).getInt();
        }

        @Override
        public void putInt(int b, ByteOrder order) {
            this.delegate.order(order).putInt(b);
        }

        @Override
        public long getLong(ByteOrder order) {
            return this.delegate.order(order).getLong();
        }

        @Override
        public void putLong(long b, ByteOrder order) {
            this.delegate.order(order).putLong(b);
        }

        @Override
        public float getFloat(ByteOrder order) {
            return this.delegate.order(order).getFloat();
        }

        @Override
        public void putFloat(float b, ByteOrder order) {
            this.delegate.order(order).putFloat(b);
        }

        @Override
        public double getDouble(ByteOrder order) {
            return this.delegate.order(order).getDouble();
        }

        @Override
        public void putDouble(double b, ByteOrder order) {
            this.delegate.order(order).putDouble(b);
        }
    }

    protected static class ByteBufferWrapper_DataInReadOnly extends ByteBufferWrapper {
        protected ByteBuffer realDelegate;

        public ByteBufferWrapper_DataInReadOnly(ByteBuffer delegate) {
            super(delegate);
            this.realDelegate = delegate;
        }

        @Override
        public void close() {
            this.delegate = this.realDelegate;
            this.realDelegate = null;

            super.close();
        }

        @Override
        public void clear() {
            this.delegate = this.realDelegate;
            super.clear();
        }

        @Override
        public void flip() {
            super.flip();

            assert !this.delegate.isReadOnly() : "already in read mode!";
            this.delegate = this.delegate.asReadOnlyBuffer();
        }
    }
}
