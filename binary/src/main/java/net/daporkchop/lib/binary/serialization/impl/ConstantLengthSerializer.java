/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.binary.serialization.impl;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.binary.serialization.Serializer;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Implementations of this serializer will always write data that is the same number required bytes.
 *
 * @author DaPorkchop_
 */
public abstract class ConstantLengthSerializer<T> implements Serializer<T> {
    /**
     * An implementation of {@link ConstantLengthSerializer} that writes a
     * single, 1-byte {@link Boolean} value
     */
    public static final ConstantLengthSerializer<Boolean> BOOLEAN = new ConstantLengthSerializer<Boolean>(1) {
        @Override
        protected void doWrite(@NonNull Boolean val, @NonNull DataOut out) throws IOException {
            out.writeBoolean(val);
        }

        @Override
        protected Boolean doRead(@NonNull DataIn in) throws IOException {
            return in.readBoolean();
        }
    };

    /**
     * An implementation of {@link ConstantLengthSerializer} that writes a
     * single, 1-byte {@link Byte} value
     */
    public static final ConstantLengthSerializer<Byte> BYTE = new ConstantLengthSerializer<Byte>(1) {
        @Override
        protected void doWrite(@NonNull Byte val, @NonNull DataOut out) throws IOException {
            out.writeByte(val);
        }

        @Override
        protected Byte doRead(@NonNull DataIn in) throws IOException {
            return in.readByte();
        }
    };

    /**
     * An implementation of {@link ConstantLengthSerializer} that writes a
     * single, 2-byte {@link Short} value
     */
    public static final ConstantLengthSerializer<Short> SHORT = new ConstantLengthSerializer<Short>(2) {
        @Override
        protected void doWrite(@NonNull Short val, @NonNull DataOut out) throws IOException {
            out.writeShort(val);
        }

        @Override
        protected Short doRead(@NonNull DataIn in) throws IOException {
            return in.readShort();
        }
    };

    /**
     * An implementation of {@link ConstantLengthSerializer} that writes a
     * single, 4-byte {@link Integer} value
     */
    public static final ConstantLengthSerializer<Integer> INT = new ConstantLengthSerializer<Integer>(4) {
        @Override
        protected void doWrite(@NonNull Integer val, @NonNull DataOut out) throws IOException {
            out.writeInt(val);
        }

        @Override
        protected Integer doRead(@NonNull DataIn in) throws IOException {
            return in.readInt();
        }
    };

    /**
     * An implementation of {@link ConstantLengthSerializer} that writes a
     * single, 8-byte {@link Long} value
     */
    public static final ConstantLengthSerializer<Long> LONG = new ConstantLengthSerializer<Long>(8) {
        @Override
        protected void doWrite(@NonNull Long val, @NonNull DataOut out) throws IOException {
            out.writeLong(val);
        }

        @Override
        protected Long doRead(@NonNull DataIn in) throws IOException {
            return in.readLong();
        }
    };

    /**
     * An implementation of {@link ConstantLengthSerializer} that writes a
     * single, 4-byte {@link Float} value
     */
    public static final ConstantLengthSerializer<Float> FLOAT = new ConstantLengthSerializer<Float>(4) {
        @Override
        protected void doWrite(@NonNull Float val, @NonNull DataOut out) throws IOException {
            out.writeFloat(val);
        }

        @Override
        protected Float doRead(@NonNull DataIn in) throws IOException {
            return in.readFloat();
        }
    };

    /**
     * An implementation of {@link ConstantLengthSerializer} that writes a
     * single, 8-byte {@link Double} value
     */
    public static final ConstantLengthSerializer<Double> DOUBLE = new ConstantLengthSerializer<Double>(8) {
        @Override
        protected void doWrite(@NonNull Double val, @NonNull DataOut out) throws IOException {
            out.writeDouble(val);
        }

        @Override
        protected Double doRead(@NonNull DataIn in) throws IOException {
            return in.readDouble();
        }
    };

    /**
     * Gets a {@link ConstantLengthSerializer} for constant-length byte arrays
     *
     * @param arraySize the size of the byte arrays that will be written/read
     * @return a {@link ConstantLengthSerializer} that can serialize constant-length byte arrays
     */
    public static ConstantLengthSerializer<byte[]> byteArray(int arraySize) {
        if (arraySize <= 0) {
            throw new IllegalArgumentException(String.format("Illegal array size: %d", arraySize));
        }
        return new ConstantLengthSerializer<byte[]>(arraySize) {
            @Override
            protected void doWrite(@NonNull byte[] val, @NonNull DataOut out) throws IOException {
                if (val.length != arraySize) {
                    throw new IllegalArgumentException(String.format("Illegal array size: %d, expected %d", val.length, arraySize));
                }
                out.write(val);
            }

            @Override
            protected byte[] doRead(@NonNull DataIn in) throws IOException {
                byte[] b = new byte[arraySize];
                in.readFully(b, 0, arraySize);
                return b;
            }
        };
    }
    @Getter
    private final int size;

    public ConstantLengthSerializer(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException(String.format("Illegal size: %d (must be more than 0)", size));
        }
        this.size = size;
    }

    @Override
    public void write(@NonNull T val, @NonNull DataOut out) throws IOException {
        this.doWrite(val, out);
    }

    @Override
    public T read(@NonNull DataIn in) throws IOException {
        return this.doRead(in);
    }

    protected abstract void doWrite(@NonNull T val, @NonNull DataOut out) throws IOException;

    protected abstract T doRead(@NonNull DataIn in) throws IOException;
}
