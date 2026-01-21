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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.annotation.param.NotNegative;
import net.daporkchop.lib.common.annotation.param.Positive;

import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.function.BiFunction;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
public class TestWrappingByteBuf extends AbstractTestWrappingBuffer {
    @Override
    protected Iterable<? extends BufferAllocator> bufferAllocators() {
        return Arrays.asList(
                NettyBufferAllocator.of("unpooled-default", UnpooledByteBufAllocator.DEFAULT::buffer),
                NettyBufferAllocator.of("unpooled-heap", UnpooledByteBufAllocator.DEFAULT::heapBuffer),
                NettyBufferAllocator.of("unpooled-direct", UnpooledByteBufAllocator.DEFAULT::directBuffer),
                NettyBufferAllocator.of("pooled-default", PooledByteBufAllocator.DEFAULT::buffer),
                NettyBufferAllocator.of("pooled-heap", PooledByteBufAllocator.DEFAULT::heapBuffer),
                NettyBufferAllocator.of("pooled-direct", PooledByteBufAllocator.DEFAULT::directBuffer)
        );
    }

    private interface NettyBufferAllocator extends BufferAllocator {
        static BufferAllocator of(@NonNull String name, @NonNull BiFunction<Integer, Integer, ByteBuf> delegateAlloc) {
            return new NettyBufferAllocator() {
                @Override
                public String name() {
                    return name;
                }

                @Override
                public GenericBuffer allocateFixed(@NotNegative long capacity) {
                    return new ByteBufWrapper(delegateAlloc.apply(toInt(capacity, "capacity"), (int) capacity), true);
                }

                @Override
                public GenericBuffer allocateResizable(@NotNegative long capacity, @NotNegative long maxCapacity) {
                    return new ByteBufWrapper(delegateAlloc.apply(toInt(capacity, "capacity"), toInt(maxCapacity, "maxCapacity")), false);
                }
            };
        }

        @Override
        default boolean resizable() {
            return true;
        }

        @Override
        default boolean autoGrowing() {
            return true;
        }

        @Override
        default @Positive long maxCapacity() {
            return Integer.MAX_VALUE;
        }
    }

    @RequiredArgsConstructor
    private static class ByteBufWrapper implements GenericBuffer {
        protected final ByteBuf delegate;
        protected final boolean fixed;

        protected boolean reading = false;

        @Override
        public void close() {
            checkState(this.delegate.release());
        }

        @Override
        public boolean resizable() {
            return true;
        }

        @Override
        public boolean autoGrowing() {
            return true;
        }

        @Override
        public @NotNegative long capacity() {
            return this.delegate.capacity();
        }

        @Override
        public void capacity(@NotNegative long capacity) {
            this.delegate.capacity(toInt(capacity, "capacity"));
        }

        @Override
        public @NotNegative long maxCapacity() {
            return this.delegate.maxCapacity();
        }

        @Override
        public void clear() {
            this.delegate.clear();
            this.reading = false;
        }

        @Override
        public void flip() {
            assert !this.reading : "buffer must be in write mode";
            this.reading = true;
        }

        @Override
        public @NotNegative long remaining() {
            return this.reading ? this.delegate.readableBytes() : this.delegate.writableBytes();
        }

        @Override
        public DataOut wrapOutNonGrowing() {
            assert !this.reading : "buffer must be in write mode";
            return DataOut.wrapReleasingNonGrowing(this.delegate.retain());
        }

        @Override
        public DataOut wrapOutAutoGrowing() {
            assert !this.reading : "buffer must be in write mode";
            return DataOut.wrapReleasing(this.delegate.retain());
        }

        @Override
        public DataIn wrapIn() {
            assert this.reading : "buffer must be in read mode";
            return DataIn.wrapReleasing(this.delegate.retain());
        }

        @Override
        public byte getByte(ByteOrder order) {
            assert this.reading : "buffer must be in read mode";
            return this.delegate.readByte();
        }

        @Override
        public void putByte(byte b, ByteOrder order) {
            assert !this.reading : "buffer must be in write mode";
            this.delegate.writeByte(b);
        }

        @Override
        public short getShort(ByteOrder order) {
            assert this.reading : "buffer must be in read mode";
            return order == ByteOrder.BIG_ENDIAN ? this.delegate.readShort() : this.delegate.readShortLE();
        }

        @Override
        public void putShort(short b, ByteOrder order) {
            assert !this.reading : "buffer must be in write mode";
            if (order == ByteOrder.BIG_ENDIAN) {
                this.delegate.writeShort(b);
            } else {
                this.delegate.writeShortLE(b);
            }
        }

        @Override
        public char getChar(ByteOrder order) {
            assert this.reading : "buffer must be in read mode";
            return order == ByteOrder.BIG_ENDIAN ? this.delegate.readChar() : Character.reverseBytes(this.delegate.readChar());
        }

        @Override
        public void putChar(char b, ByteOrder order) {
            assert !this.reading : "buffer must be in write mode";
            if (order == ByteOrder.BIG_ENDIAN) {
                this.delegate.writeChar(b);
            } else {
                this.delegate.writeChar(Character.reverseBytes(b));
            }
        }

        @Override
        public int getInt(ByteOrder order) {
            assert this.reading : "buffer must be in read mode";
            return order == ByteOrder.BIG_ENDIAN ? this.delegate.readInt() : this.delegate.readIntLE();
        }

        @Override
        public void putInt(int b, ByteOrder order) {
            assert !this.reading : "buffer must be in write mode";
            if (order == ByteOrder.BIG_ENDIAN) {
                this.delegate.writeInt(b);
            } else {
                this.delegate.writeIntLE(b);
            }
        }

        @Override
        public long getLong(ByteOrder order) {
            assert this.reading : "buffer must be in read mode";
            return order == ByteOrder.BIG_ENDIAN ? this.delegate.readLong() : this.delegate.readLongLE();
        }

        @Override
        public void putLong(long b, ByteOrder order) {
            assert !this.reading : "buffer must be in write mode";
            if (order == ByteOrder.BIG_ENDIAN) {
                this.delegate.writeLong(b);
            } else {
                this.delegate.writeLongLE(b);
            }
        }

        @Override
        public float getFloat(ByteOrder order) {
            assert this.reading : "buffer must be in read mode";
            return order == ByteOrder.BIG_ENDIAN ? this.delegate.readFloat() : this.delegate.readFloatLE();
        }

        @Override
        public void putFloat(float b, ByteOrder order) {
            assert !this.reading : "buffer must be in write mode";
            if (order == ByteOrder.BIG_ENDIAN) {
                this.delegate.writeFloat(b);
            } else {
                this.delegate.writeFloatLE(b);
            }
        }

        @Override
        public double getDouble(ByteOrder order) {
            assert this.reading : "buffer must be in read mode";
            return order == ByteOrder.BIG_ENDIAN ? this.delegate.readDouble() : this.delegate.readDoubleLE();
        }

        @Override
        public void putDouble(double b, ByteOrder order) {
            assert !this.reading : "buffer must be in write mode";
            if (order == ByteOrder.BIG_ENDIAN) {
                this.delegate.writeDouble(b);
            } else {
                this.delegate.writeDoubleLE(b);
            }
        }
    }
}
