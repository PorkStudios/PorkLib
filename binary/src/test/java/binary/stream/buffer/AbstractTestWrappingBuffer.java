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

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.binary.stream.misc.SlashDevSlashZero;
import net.daporkchop.lib.binary.util.NoMoreSpaceException;
import net.daporkchop.lib.common.annotation.param.NotNegative;
import net.daporkchop.lib.common.annotation.param.Positive;
import net.daporkchop.lib.common.function.io.IOBiConsumer;
import net.daporkchop.lib.common.function.io.IOBiFunction;
import net.daporkchop.lib.common.function.io.IOFunction;
import net.daporkchop.lib.common.function.io.IOTriConsumer;
import net.daporkchop.lib.common.function.plain.TriConsumer;
import org.junit.Test;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.SplittableRandom;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.LongFunction;

import static java.lang.Math.*;

/**
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public abstract class AbstractTestWrappingBuffer {
    protected static final FixedType<?>[] FIXED_TYPES = {
            //boolean
            FixedType.of("boolean",
                    1L, SplittableRandom::nextBoolean,
                    (in, order) -> in.readBoolean(), (buf, order) -> buf.getByte(order) != 0,
                    (out, val, order) -> out.writeBoolean(val), (buf, val, order) -> buf.putByte(val ? (byte) 1 : 0, order)),

            //byte
            FixedType.of("byte",
                    Byte.BYTES, rng -> (byte) rng.nextInt(),
                    (in, order) -> in.readByte(), GenericBuffer::getByte,
                    (out, val, order) -> out.writeByte(val), GenericBuffer::putByte),
            FixedType.of("unsigned byte",
                    Byte.BYTES, rng -> rng.nextInt() & 0xFF,
                    (in, order) -> in.readUnsignedByte(), (buf, order) -> buf.getByte(order) & 0xFF,
                    (out, val, order) -> out.writeByte(val), (buf, val, order) -> buf.putByte((byte) val.intValue(), order)),

            //short
            FixedType.of("short param-ordered",
                    Short.BYTES, rng -> (short) rng.nextInt(),
                    DataIn::readShort, GenericBuffer::getShort,
                    DataOut::writeShort, GenericBuffer::putShort),
            FixedType.<Short>ofOrdered("short method-ordered",
                    Short.BYTES, rng -> (short) rng.nextInt(),
                    DataIn::readShort, DataIn::readShortLE, GenericBuffer::getShort,
                    DataOut::writeShort, DataOut::writeShortLE, GenericBuffer::putShort),
            FixedType.of("unsigned short param-ordered",
                    Short.BYTES, rng -> rng.nextInt() & 0xFFFF,
                    DataIn::readUnsignedShort, (buf, order) -> buf.getShort(order) & 0xFFFF,
                    DataOut::writeShort, (buf, val, order) -> buf.putShort((short) val.intValue(), order)),
            FixedType.ofOrdered("unsigned short method-ordered",
                    Short.BYTES, rng -> rng.nextInt() & 0xFFFF,
                    DataIn::readUnsignedShort, DataIn::readUnsignedShortLE, (buf, order) -> buf.getShort(order) & 0xFFFF,
                    DataOut::writeShort, DataOut::writeShortLE, (buf, val, order) -> buf.putShort((short) val.intValue(), order)),

            //char
            FixedType.of("char param-ordered",
                    Character.BYTES, rng -> (char) rng.nextInt(),
                    DataIn::readChar, GenericBuffer::getChar,
                    DataOut::writeChar, GenericBuffer::putChar),
            FixedType.<Character>ofOrdered("char method-ordered",
                    Character.BYTES, rng -> (char) rng.nextInt(),
                    DataIn::readChar, DataIn::readCharLE, GenericBuffer::getChar,
                    DataOut::writeChar, DataOut::writeCharLE, GenericBuffer::putChar),

            //int
            FixedType.of("int param-ordered",
                    Integer.BYTES, SplittableRandom::nextInt,
                    DataIn::readInt, GenericBuffer::getInt,
                    DataOut::writeInt, GenericBuffer::putInt),
            FixedType.ofOrdered("int method-ordered",
                    Integer.BYTES, SplittableRandom::nextInt,
                    DataIn::readInt, DataIn::readIntLE, GenericBuffer::getInt,
                    DataOut::writeInt, DataOut::writeIntLE, GenericBuffer::putInt),

            //long
            FixedType.of("long param-ordered",
                    Long.BYTES, SplittableRandom::nextLong,
                    DataIn::readLong, GenericBuffer::getLong,
                    DataOut::writeLong, GenericBuffer::putLong),
            FixedType.ofOrdered("long method-ordered",
                    Long.BYTES, SplittableRandom::nextLong,
                    DataIn::readLong, DataIn::readLongLE, GenericBuffer::getLong,
                    DataOut::writeLong, DataOut::writeLongLE, GenericBuffer::putLong),

            //float
            FixedType.of("float param-ordered",
                    Float.BYTES, rng -> Float.intBitsToFloat(rng.nextInt()),
                    DataIn::readFloat, GenericBuffer::getFloat,
                    DataOut::writeFloat, GenericBuffer::putFloat),
            FixedType.ofOrdered("float method-ordered",
                    Float.BYTES, rng -> Float.intBitsToFloat(rng.nextInt()),
                    DataIn::readFloat, DataIn::readFloatLE, GenericBuffer::getFloat,
                    DataOut::writeFloat, DataOut::writeFloatLE, GenericBuffer::putFloat),

            //double
            FixedType.of("double param-ordered",
                    Double.BYTES, rng -> Double.longBitsToDouble(rng.nextLong()),
                    DataIn::readDouble, GenericBuffer::getDouble,
                    DataOut::writeDouble, GenericBuffer::putDouble),
            FixedType.ofOrdered("double method-ordered",
                    Double.BYTES, rng -> Double.longBitsToDouble(rng.nextLong()),
                    DataIn::readDouble, DataIn::readDoubleLE, GenericBuffer::getDouble,
                    DataOut::writeDouble, DataOut::writeDoubleLE, GenericBuffer::putDouble),
    };

    protected abstract Iterable<? extends BufferAllocator> bufferAllocators();

    protected Iterable<? extends FixedType<?>> fixedTypes() {
        return Arrays.asList(FIXED_TYPES);
    }

    @Test
    public void test() {
        this.test(new SplittableRandom(0xDEADBEEF_FEEBDAEDL));
    }

    @SneakyThrows(NamedThrowable.class)
    protected void test(@NonNull SplittableRandom rng) {
        try {
            for (BufferAllocator alloc : this.bufferAllocators()) {
                this.test(rng.split(), alloc);
            }
        } catch (Throwable t) {
            throw NamedThrowable.of(this.getClass().getTypeName(), t);
        }
    }

    protected void test(@NonNull SplittableRandom rng, @NonNull BufferAllocator alloc) throws NamedThrowable {
        try {
            this.testFixed(rng.split(), alloc);

            //TODO: tests for variable-length types
        } catch (Throwable t) {
            throw NamedThrowable.of(alloc.name(), t);
        }
    }

    protected void testFixed(@NonNull SplittableRandom rng, @NonNull BufferAllocator alloc) throws NamedThrowable {
        try {
            for (FixedType<?> type : this.fixedTypes()) {
                this.testFixed(rng.split(), alloc, type);
            }
        } catch (Throwable t) {
            throw NamedThrowable.of("fixed", t);
        }
    }

    protected <T> void testFixed(@NonNull SplittableRandom rng, @NonNull BufferAllocator alloc, @NonNull FixedType<T> type) throws NamedThrowable {
        try {
            this.testFixed(rng.split(), alloc, type, ByteOrder.BIG_ENDIAN);
            this.testFixed(rng.split(), alloc, type, ByteOrder.LITTLE_ENDIAN);
        } catch (Throwable t) {
            throw NamedThrowable.of(type.name(), t);
        }
    }

    protected <T> void testFixed(@NonNull SplittableRandom rng, @NonNull BufferAllocator alloc, @NonNull FixedType<T> type, @NonNull ByteOrder order) throws NamedThrowable {
        try {
            this.testFixed_writePass(rng.split(), alloc, type, order);
            this.testFixed_readPass(rng.split(), alloc, type, order);

            this.testFixed_readLimitWorks(rng.split(), alloc, type, order);

            if (alloc.resizable()) {
                if (alloc.autoGrowing()) {
                    this.testFixed_resizable_autoGrowing_growWorks(rng.split(), alloc, type, order);
                    this.testFixed_resizable_autoGrowing_writeLimitWorks(rng.split(), alloc, type, order);
                }
            }
        } catch (Throwable t) {
            throw NamedThrowable.of(order.toString(), t);
        }
    }

    //write with DataOut, then read directly from the buffer
    @SneakyThrows(IOException.class)
    protected <T> void testFixed_writePass(@NonNull SplittableRandom rng, @NonNull BufferAllocator alloc, @NonNull FixedType<T> type, @NonNull ByteOrder order) {
        Queue<T> allExpected = new LinkedList<>();
        long size = type.size();

        try (GenericBuffer buffer = alloc.allocateFixed(multiplyExact(size, rng.nextLong(1L << 16, 4L << 16)))) {
            //write values
            buffer.clear();
            try (DataOut out = buffer.wrapOutNonGrowing()) {
                do {
                    T value = type.random(rng);
                    type.write(out, value, order);
                    allExpected.add(value);
                } while (true);
            } catch (NoMoreSpaceException e) {
                assert !allExpected.isEmpty() : "no values were written!";
                assert buffer.remaining() == buffer.capacity() % size : "remaining space is less than expected";
            }

            //read values
            buffer.flip();
            while (buffer.remaining() >= size) {
                T value = type.get(buffer, order);
                T expected = allExpected.remove();
                assert Objects.equals(value, expected);
            }
        }
    }

    //write directly to the buffer, then read with DataIn
    @SneakyThrows(IOException.class)
    private <T> void testFixed_readPass(@NonNull SplittableRandom rng, @NonNull BufferAllocator alloc, @NonNull FixedType<T> type, @NonNull ByteOrder order) {
        Queue<T> allExpected = new LinkedList<>();
        long size = type.size();

        try (GenericBuffer buffer = alloc.allocateFixed(multiplyExact(size, rng.nextLong(1L << 16, 4L << 16)))) {
            //write values
            buffer.clear();
            while (buffer.remaining() >= size) {
                T value = type.random(rng);
                type.put(buffer, value, order);
                allExpected.add(value);
            }
            assert buffer.remaining() == buffer.capacity() % size : "remaining space is less than expected";

            //read values
            buffer.flip();
            try (DataIn in = buffer.wrapIn()) {
                do {
                    T value = type.read(in, order);
                    T expected = allExpected.remove();
                    assert Objects.equals(value, expected);
                } while (true);
            } catch (EOFException e) {
                assert allExpected.isEmpty() : "EOFException occurred before all values were processed";
            }
        }
    }

    //make sure that the correct exception is thrown when attempting to read too much data from a buffer through DataIn
    @SneakyThrows(IOException.class)
    protected <T> void testFixed_readLimitWorks(@NonNull SplittableRandom rng, @NonNull BufferAllocator alloc, @NonNull FixedType<T> type, @NonNull ByteOrder order) {
        long size = type.size();
        long bufferSize = (multiplyExact(size, 3L) - 1) >> 1L;
        assert bufferSize < size * 2L;
        assert bufferSize < alloc.maxCapacity();

        try (GenericBuffer buffer = alloc.allocateFixed(bufferSize)) {
            try (DataOut out = buffer.wrapOutNonGrowing()) {
                SlashDevSlashZero.getDataIn().transferToFully(out, bufferSize);
            }
            buffer.flip();

            try (DataIn in = buffer.wrapIn()) {
                assert buffer.remaining() >= size : "not enough space";
                type.read(in, order);

                assert buffer.remaining() < size : "enough space";
                try {
                    type.read(in, order);
                    throw new AssertionError("a value was read even though insufficient readable data remained!");
                } catch (EOFException e) {
                    //silently ignore
                }
                assert buffer.remaining() < size : "enough space";
            }
        }
    }

    //make sure that the buffer is able to automatically grow when writing data through DataOut
    @SneakyThrows(IOException.class)
    protected <T> void testFixed_resizable_autoGrowing_growWorks(@NonNull SplittableRandom rng, @NonNull BufferAllocator alloc, @NonNull FixedType<T> type, @NonNull ByteOrder order) {
        assert alloc.resizable();
        assert alloc.autoGrowing();

        long size = type.size();
        long bufferSize = (multiplyExact(size, 3L) - 1) >> 1L;
        assert bufferSize < size * 2L;
        assert size * 2L < alloc.maxCapacity();

        //make sure it grows when auto-growing stream is requested
        try (GenericBuffer buffer = alloc.allocateResizable(bufferSize, alloc.maxCapacity());
             DataOut out = buffer.wrapOutAutoGrowing()) {
            assert buffer.remaining() >= size : "not enough space";
            type.write(out, type.random(rng), order);

            assert buffer.remaining() < size : "enough space";
            type.write(out, type.random(rng), order);

            assert buffer.remaining() > size : "not WAY more space";
        }

        //make sure it doesn't grow when a non-growing stream is requested
        try (GenericBuffer buffer = alloc.allocateResizable(bufferSize, alloc.maxCapacity());
             DataOut out = buffer.wrapOutNonGrowing()) {
            assert buffer.remaining() >= size : "not enough space";
            type.write(out, type.random(rng), order);

            assert buffer.remaining() < size : "enough space";
            try {
                type.write(out, type.random(rng), order);
                throw new AssertionError("buffer was grown even though we requested a non-growing stream!");
            } catch (NoMoreSpaceException e) {
                //silently ignore
            }
        }
    }

    //make sure that the correct exception is thrown when attempting to write too much data to an auto-growing buffer through DataOut
    @SneakyThrows(IOException.class)
    protected <T> void testFixed_resizable_autoGrowing_writeLimitWorks(@NonNull SplittableRandom rng, @NonNull BufferAllocator alloc, @NonNull FixedType<T> type, @NonNull ByteOrder order) {
        assert alloc.resizable();
        assert alloc.autoGrowing();

        long size = type.size();
        long bufferSize = multiplyExact(size, 3L) >> 1L;
        long bufferMaxSize = size * 2L;
        assert bufferSize < bufferMaxSize;
        assert bufferMaxSize < alloc.maxCapacity();

        try (GenericBuffer buffer = alloc.allocateResizable(bufferSize, bufferMaxSize);
             DataOut out = buffer.wrapOutNonGrowing()) {
            assert buffer.remaining() >= size : "not enough space";
            type.write(out, type.random(rng), order);

            assert buffer.remaining() < size : "enough space";
            try {
                type.write(out, type.random(rng), order);
                throw new AssertionError("buffer was grown even though its max capacity was set!");
            } catch (NoMoreSpaceException e) {
                //silently ignore
            }
            assert buffer.remaining() < size : "enough space";
        }
    }

    protected interface FixedType<T> {
        static <T> FixedType<T> of(@NonNull String name,
                                   @Positive long size, @NonNull Function<SplittableRandom, T> random,
                                   @NonNull IOBiFunction<DataIn, ByteOrder, T> read,
                                   @NonNull BiFunction<GenericBuffer, ByteOrder, T> get,
                                   @NonNull IOTriConsumer<DataOut, T, ByteOrder> write,
                                   @NonNull TriConsumer<GenericBuffer, T, ByteOrder> put) {
            return new FixedType<T>() {
                @Override
                public String name() {
                    return name;
                }

                @Override
                public @Positive long size() {
                    return size;
                }

                @Override
                public T random(SplittableRandom rng) {
                    return random.apply(rng);
                }

                @Override
                public T read(DataIn in, ByteOrder order) throws IOException {
                    return read.applyThrowing(in, order);
                }

                @Override
                public T get(GenericBuffer buffer, ByteOrder order) {
                    return get.apply(buffer, order);
                }

                @Override
                public void write(DataOut out, T value, ByteOrder order) throws IOException {
                    write.acceptThrowing(out, value, order);
                }

                @Override
                public void put(GenericBuffer buffer, T value, ByteOrder order) {
                    put.accept(buffer, value, order);
                }
            };
        }

        static <T> FixedType<T> ofOrdered(@NonNull String name,
                                          @Positive long size, @NonNull Function<SplittableRandom, T> random,
                                          @NonNull IOFunction<DataIn, T> readBE, @NonNull IOFunction<DataIn, T> readLE,
                                          @NonNull BiFunction<GenericBuffer, ByteOrder, T> get,
                                          @NonNull IOBiConsumer<DataOut, T> writeBE, @NonNull IOBiConsumer<DataOut, T> writeLE,
                                          @NonNull TriConsumer<GenericBuffer, T, ByteOrder> put) {
            return new FixedType<T>() {
                @Override
                public String name() {
                    return name;
                }

                @Override
                public @Positive long size() {
                    return size;
                }

                @Override
                public T random(SplittableRandom rng) {
                    return random.apply(rng);
                }

                @Override
                public T read(DataIn in, ByteOrder order) throws IOException {
                    return (order == ByteOrder.BIG_ENDIAN ? readBE : readLE).applyThrowing(in);
                }

                @Override
                public T get(GenericBuffer buffer, ByteOrder order) {
                    return get.apply(buffer, order);
                }

                @Override
                public void write(DataOut out, T value, ByteOrder order) throws IOException {
                    (order == ByteOrder.BIG_ENDIAN ? writeBE : writeLE).acceptThrowing(out, value);
                }

                @Override
                public void put(GenericBuffer buffer, T value, ByteOrder order) {
                    put.accept(buffer, value, order);
                }
            };
        }

        String name();

        @Positive long size();

        T random(SplittableRandom rng);

        T read(DataIn in, ByteOrder order) throws IOException;

        T get(GenericBuffer buffer, ByteOrder order);

        void write(DataOut out, T value, ByteOrder order) throws IOException;

        void put(GenericBuffer buffer, T value, ByteOrder order);
    }

    protected interface BufferAllocator {
        static BufferAllocator ofFixed(@NonNull String name,
                                       @Positive long maxCapacity, @NonNull LongFunction<? extends GenericBuffer> allocate) {
            return new BufferAllocator() {
                @Override
                public String name() {
                    return name;
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
                public @Positive long maxCapacity() {
                    return maxCapacity;
                }

                @Override
                public GenericBuffer allocateFixed(@NotNegative long capacity) {
                    return allocate.apply(capacity);
                }

                @Override
                public GenericBuffer allocateResizable(@NotNegative long capacity, @NotNegative long maxCapacity) {
                    throw new UnsupportedOperationException();
                }
            };
        }

        String name();

        boolean resizable();

        boolean autoGrowing();

        @Positive long maxCapacity();

        GenericBuffer allocateFixed(@NotNegative long capacity);

        GenericBuffer allocateResizable(@NotNegative long capacity, @NotNegative long maxCapacity);
    }

    protected interface GenericBuffer extends AutoCloseable {
        @Override
        void close();

        boolean resizable();

        boolean autoGrowing();

        @NotNegative long capacity();

        void capacity(@NotNegative long capacity) throws UnsupportedOperationException;

        @NotNegative long maxCapacity();

        /**
         * Clears this buffer and begins a write phase.
         */
        void clear();

        /**
         * Ends the current write phase and begins a read phase from the beginning up until the current writer position.
         */
        void flip();

        @NotNegative long remaining();

        DataOut wrapOutNonGrowing();

        DataOut wrapOutAutoGrowing() throws UnsupportedOperationException;

        DataIn wrapIn();

        byte getByte(ByteOrder order);

        void putByte(byte b, ByteOrder order);

        short getShort(ByteOrder order);

        void putShort(short b, ByteOrder order);

        char getChar(ByteOrder order);

        void putChar(char b, ByteOrder order);

        int getInt(ByteOrder order);

        void putInt(int b, ByteOrder order);

        long getLong(ByteOrder order);

        void putLong(long b, ByteOrder order);

        float getFloat(ByteOrder order);

        void putFloat(float b, ByteOrder order);

        double getDouble(ByteOrder order);

        void putDouble(double b, ByteOrder order);
    }

    protected static class NamedThrowable extends Exception {
        static NamedThrowable of(String name, Throwable t) {
            return t instanceof NamedThrowable
                    ? ((NamedThrowable) t).prefix(name)
                    : new NamedThrowable(name, t);
        }

        protected String message;

        public NamedThrowable(String message) {
            super(message);
            this.message = message;
        }

        public NamedThrowable(String message, Throwable cause) {
            super(message, cause);
            this.message = message;
        }

        @Override
        public String getMessage() {
            return this.message;
        }

        public NamedThrowable prefix(String text) {
            this.message = text + " -> " + this.message;
            return this;
        }
    }
}
