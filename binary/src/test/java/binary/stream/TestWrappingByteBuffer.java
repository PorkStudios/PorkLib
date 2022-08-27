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

package binary.stream;

import lombok.NonNull;
import lombok.SneakyThrows;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.function.io.IOBiConsumer;
import net.daporkchop.lib.common.function.io.IOFunction;
import org.junit.Test;

import java.io.EOFException;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author DaPorkchop_
 */
public class TestWrappingByteBuffer {
    protected int minimumCapacity() {
        return 1 << 20; //1 MiB
    }

    private void forEachByteOrder(@NonNull ByteBuffer buffer, @NonNull Runnable action) {
        ByteOrder original = buffer.order();
        try {
            buffer.order(ByteOrder.BIG_ENDIAN).clear();
            action.run();

            buffer.order(ByteOrder.LITTLE_ENDIAN).clear();
            action.run();
        } finally {
            buffer.order(original);
        }
    }

    private <T> void testWritePass(@NonNull ByteBuffer buffer, @NonNull IOBiConsumer<DataOut, T> write, @NonNull Function<ByteBuffer, T> read, @NonNull Function<Random, T> rng) {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        Queue<T> allExpected = new LinkedList<>();

        //write values
        buffer.clear();
        try (DataOut out = DataOut.wrap(buffer)) {
            do {
                T value = rng.apply(r);
                write.acceptThrowing(out, value);
                allExpected.add(value);
            } while (true);
        } catch (IOException e) {
            assert e.getCause() instanceof BufferOverflowException : Objects.toString(e.getCause());
            assert !allExpected.isEmpty() : "no values were written!";
        }

        //read values
        buffer.flip();
        try {
            do {
                T value = read.apply(buffer);
                T expected = allExpected.remove();
                assert Objects.equals(value, expected);
            } while (true);
        } catch (BufferUnderflowException e) {
            assert allExpected.isEmpty() : "BufferUnderflowException occurred before all values were processed";
        }
    }

    protected void testWrite(@NonNull ByteBuffer buffer) {
        assert buffer.capacity() >= this.minimumCapacity();

        //byte
        this.forEachByteOrder(buffer, () -> {
            this.<Byte>testWritePass(buffer, DataOut::write, ByteBuffer::get, r -> (byte) r.nextInt());
            this.<Byte>testWritePass(buffer, DataOut::writeByte, ByteBuffer::get, r -> (byte) r.nextInt());
        });

        //short
        this.<Short>testWritePass(buffer.order(ByteOrder.BIG_ENDIAN), DataOut::writeShort, ByteBuffer::getShort, r -> (short) r.nextInt());
        this.<Short>testWritePass(buffer.order(ByteOrder.LITTLE_ENDIAN), DataOut::writeShortLE, ByteBuffer::getShort, r -> (short) r.nextInt());

        //char
        this.<Character>testWritePass(buffer.order(ByteOrder.BIG_ENDIAN), DataOut::writeChar, ByteBuffer::getChar, r -> (char) r.nextInt());
        this.<Character>testWritePass(buffer.order(ByteOrder.LITTLE_ENDIAN), DataOut::writeCharLE, ByteBuffer::getChar, r -> (char) r.nextInt());

        //int
        this.testWritePass(buffer.order(ByteOrder.BIG_ENDIAN), DataOut::writeInt, ByteBuffer::getInt, Random::nextInt);
        this.testWritePass(buffer.order(ByteOrder.LITTLE_ENDIAN), DataOut::writeIntLE, ByteBuffer::getInt, Random::nextInt);

        //long
        this.testWritePass(buffer.order(ByteOrder.BIG_ENDIAN), DataOut::writeLong, ByteBuffer::getLong, Random::nextLong);
        this.testWritePass(buffer.order(ByteOrder.LITTLE_ENDIAN), DataOut::writeLongLE, ByteBuffer::getLong, Random::nextLong);

        //float
        this.testWritePass(buffer.order(ByteOrder.BIG_ENDIAN), DataOut::writeFloat, ByteBuffer::getFloat, Random::nextFloat);
        this.testWritePass(buffer.order(ByteOrder.LITTLE_ENDIAN), DataOut::writeFloatLE, ByteBuffer::getFloat, Random::nextFloat);

        //double
        this.testWritePass(buffer.order(ByteOrder.BIG_ENDIAN), DataOut::writeDouble, ByteBuffer::getDouble, Random::nextDouble);
        this.testWritePass(buffer.order(ByteOrder.LITTLE_ENDIAN), DataOut::writeDoubleLE, ByteBuffer::getDouble, Random::nextDouble);

        //TODO: add tests for other methods!
    }

    @Test
    public void testWriteHeap() {
        this.testWrite(ByteBuffer.allocate(this.minimumCapacity()));
    }

    @Test
    public void testWriteDirect() {
        this.testWrite(ByteBuffer.allocateDirect(this.minimumCapacity()));
    }

    @SneakyThrows(IOException.class)
    private <T> void testReadPass(@NonNull ByteBuffer buffer, @NonNull BiConsumer<ByteBuffer, T> write, @NonNull IOFunction<DataIn, T> read, @NonNull Function<Random, T> rng) {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        Queue<T> allExpected = new LinkedList<>();

        //write values
        buffer.clear();
        try {
            do {
                T value = rng.apply(r);
                write.accept(buffer, value);
                allExpected.add(value);
            } while (true);
        } catch (BufferOverflowException e) {
            assert !allExpected.isEmpty() : "no values were written!";
        }

        //read values
        buffer.flip();
        try (DataIn in = DataIn.wrap(buffer)) {
            do {
                T value = read.applyThrowing(in);
                T expected = allExpected.remove();
                assert Objects.equals(value, expected);
            } while (true);
        } catch (EOFException e) {
            assert allExpected.isEmpty() : "EOFException occurred before all values were processed";
        }
    }

    protected void testRead(@NonNull ByteBuffer buffer) {
        assert buffer.capacity() >= this.minimumCapacity();

        //byte
        this.forEachByteOrder(buffer, () -> {
            this.<Byte>testReadPass(buffer, ByteBuffer::put, DataIn::readByte, r -> (byte) r.nextInt());
            this.<Byte>testReadPass(buffer, ByteBuffer::put, in -> (byte) in.readUnsignedByte(), r -> (byte) r.nextInt());
        });

        //short
        this.<Short>testReadPass(buffer.order(ByteOrder.BIG_ENDIAN), ByteBuffer::putShort, DataIn::readShort, r -> (short) r.nextInt());
        this.<Short>testReadPass(buffer.order(ByteOrder.LITTLE_ENDIAN), ByteBuffer::putShort, DataIn::readShortLE, r -> (short) r.nextInt());

        //char
        this.<Character>testReadPass(buffer.order(ByteOrder.BIG_ENDIAN), ByteBuffer::putChar, DataIn::readChar, r -> (char) r.nextInt());
        this.<Character>testReadPass(buffer.order(ByteOrder.LITTLE_ENDIAN), ByteBuffer::putChar, DataIn::readCharLE, r -> (char) r.nextInt());

        //int
        this.testReadPass(buffer.order(ByteOrder.BIG_ENDIAN), ByteBuffer::putInt, DataIn::readInt, Random::nextInt);
        this.testReadPass(buffer.order(ByteOrder.LITTLE_ENDIAN), ByteBuffer::putInt, DataIn::readIntLE, Random::nextInt);

        //long
        this.testReadPass(buffer.order(ByteOrder.BIG_ENDIAN), ByteBuffer::putLong, DataIn::readLong, Random::nextLong);
        this.testReadPass(buffer.order(ByteOrder.LITTLE_ENDIAN), ByteBuffer::putLong, DataIn::readLongLE, Random::nextLong);

        //float
        this.testReadPass(buffer.order(ByteOrder.BIG_ENDIAN), ByteBuffer::putFloat, DataIn::readFloat, Random::nextFloat);
        this.testReadPass(buffer.order(ByteOrder.LITTLE_ENDIAN), ByteBuffer::putFloat, DataIn::readFloatLE, Random::nextFloat);

        //double
        this.testReadPass(buffer.order(ByteOrder.BIG_ENDIAN), ByteBuffer::putDouble, DataIn::readDouble, Random::nextDouble);
        this.testReadPass(buffer.order(ByteOrder.LITTLE_ENDIAN), ByteBuffer::putDouble, DataIn::readDoubleLE, Random::nextDouble);

        //TODO: add tests for other methods!
    }

    @Test
    public void testReadHeap() {
        this.testRead(ByteBuffer.allocate(this.minimumCapacity()));
    }

    @Test
    public void testReadDirect() {
        this.testRead(ByteBuffer.allocateDirect(this.minimumCapacity()));
    }
}
