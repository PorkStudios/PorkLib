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

package net.daporkchop.lib.binary.stream.misc;

import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.binary.stream.wrapper.DataInAsInputStream;
import net.daporkchop.lib.binary.stream.wrapper.DataOutAsOutputStream;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.function.Function;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * An implementation of both {@link DataIn} and {@link DataOut} that emulates the behavior of {@code /dev/null}.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SlashDevSlashNull implements DataIn, DataOut {
    public static final SlashDevSlashNull INSTANCE = new SlashDevSlashNull();
    public static final java.io.InputStream INPUT_STREAM = new InputStream();
    public static final java.io.OutputStream OUTPUT_STREAM = new OutputStream();

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void close() throws IOException {
    }

    //
    //
    // DataIn methods
    //
    //

    @Override
    public int read() throws IOException {
        return 0;
    }

    @Override
    public boolean readBoolean() throws IOException {
        return false;
    }

    @Override
    public byte readByte() throws IOException {
        return 0;
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return 0;
    }

    @Override
    public short readShort() throws IOException {
        return 0;
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return 0;
    }

    @Override
    public short readShortLE() throws IOException {
        return 0;
    }

    @Override
    public int readUnsignedShortLE() throws IOException {
        return 0;
    }

    @Override
    public char readChar() throws IOException {
        return 0;
    }

    @Override
    public char readCharLE() throws IOException {
        return 0;
    }

    @Override
    public int readInt() throws IOException {
        return 0;
    }

    @Override
    public int readIntLE() throws IOException {
        return 0;
    }

    @Override
    public long readLong() throws IOException {
        return 0;
    }

    @Override
    public long readLongLE() throws IOException {
        return 0;
    }

    @Override
    public float readFloat() throws IOException {
        return 0;
    }

    @Override
    public float readFloatLE() throws IOException {
        return 0;
    }

    @Override
    public double readDouble() throws IOException {
        return 0;
    }

    @Override
    public double readDoubleLE() throws IOException {
        return 0;
    }

    @Override
    public String readUTF() throws IOException {
        return "";
    }

    @Override
    public String readVarUTF() throws IOException {
        return "";
    }

    @Override
    public String readString(@NonNull Charset charset) throws IOException {
        return "";
    }

    @Override
    public String readVarString(@NonNull Charset charset) throws IOException {
        return "";
    }

    @Override
    public String readString(long size, @NonNull Charset charset) throws IOException {
        return "";
    }

    @Override
    public String readLine() throws IOException {
        return "";
    }

    @Override
    public CharSequence readText(long size, @NonNull Charset charset) throws IOException {
        return "";
    }

    @Override
    public <E extends Enum<E>> E readEnum(@NonNull Function<String, E> f) throws IOException {
        return null;
    }

    @Override
    public int readVarInt() throws IOException {
        return 0;
    }

    @Override
    public int readVarIntZigZag() throws IOException {
        return 0;
    }

    @Override
    public long readVarLong() throws IOException {
        return 0;
    }

    @Override
    public long readVarLongZigZag() throws IOException {
        return 0;
    }

    @Override
    public int read(@NonNull byte[] dst, int start, int length) throws IOException {
        Arrays.fill(dst, start, start + length, (byte) 0);
        return length;
    }

    @Override
    public void readFully(@NonNull byte[] dst, int start, int length) throws IOException {
        Arrays.fill(dst, start, start + length, (byte) 0);
    }

    @Override
    public byte[] toByteArray() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int read(@NonNull ByteBuffer dst) throws IOException {
        int count = dst.remaining();
        if (dst.isDirect()) {
            PUnsafe.setMemory(PUnsafe.pork_directBufferAddress(dst) + dst.position(), count, (byte) 0);
        } else {
            Arrays.fill(dst.array(), dst.arrayOffset() + dst.position(), dst.arrayOffset() + dst.limit(), (byte) 0);
        }
        dst.position(dst.limit());
        return count;
    }

    @Override
    public int read(@NonNull ByteBuf dst, int count) throws IOException {
        notNegative(count, "count");
        dst.ensureWritable(count);
        int writerIndex = dst.writerIndex();
        if (dst.hasMemoryAddress()) {
            PUnsafe.setMemory(dst.memoryAddress() + writerIndex, count, (byte) 0);
        } else if (dst.hasArray())  {
            Arrays.fill(dst.array(), dst.arrayOffset() + writerIndex, dst.arrayOffset() + writerIndex + count, (byte) 0);
        } else {
            for (int i = 0; i < count; i++) {
                dst.setByte(writerIndex + i, 0);
            }
        }
        dst.writerIndex(writerIndex + count);
        return count;
    }

    @Override
    public int read(@NonNull ByteBuf dst, int start, int length) throws IOException {
        checkRangeLen(dst.maxCapacity(), start, length);
        int writerIndex = dst.writerIndex();
        dst.ensureWritable(start + length - writerIndex);
        if (dst.hasMemoryAddress()) {
            PUnsafe.setMemory(dst.memoryAddress() + start, length, (byte) 0);
        } else if (dst.hasArray())  {
            Arrays.fill(dst.array(), dst.arrayOffset() + start, dst.arrayOffset() + start + length, (byte) 0);
        } else {
            for (int i = 0; i < length; i++) {
                dst.setByte(start + i, 0);
            }
        }
        return length;
    }

    @Override
    public int readFully(@NonNull ByteBuffer dst) throws IOException {
        return this.read(dst);
    }

    @Override
    public int readFully(@NonNull ByteBuf dst, int count) throws IOException {
        return this.read(dst, count);
    }

    @Override
    public int readFully(@NonNull ByteBuf dst, int start, int length) throws IOException {
        return this.read(dst, start, length);
    }

    @Override
    public java.io.InputStream asInputStream() throws IOException {
        return INPUT_STREAM;
    }

    @Override
    public long remaining() throws IOException {
        return 0L;
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return n;
    }

    @Override
    public long skipBytes(long n) throws IOException {
        return n;
    }

    //
    //
    // DataOut methods
    //
    //

    @Override
    public void write(int b) throws IOException {
    }

    @Override
    public void writeBoolean(boolean b) throws IOException {
    }

    @Override
    public void writeByte(int b) throws IOException {
    }

    @Override
    public void writeShort(int v) throws IOException {
    }

    @Override
    public void writeShortLE(int v) throws IOException {
    }

    @Override
    public void writeChar(int v) throws IOException {
    }

    @Override
    public void writeCharLE(int v) throws IOException {
    }

    @Override
    public void writeInt(int v) throws IOException {
    }

    @Override
    public void writeIntLE(int v) throws IOException {
    }

    @Override
    public void writeLong(long v) throws IOException {
    }

    @Override
    public void writeLongLE(long v) throws IOException {
    }

    @Override
    public void writeFloat(float f) throws IOException {
    }

    @Override
    public void writeFloatLE(float f) throws IOException {
    }

    @Override
    public void writeDouble(double d) throws IOException {
    }

    @Override
    public void writeDoubleLE(double d) throws IOException {
    }

    @Override
    public void writeBytes(@NonNull String text) throws IOException {
    }

    @Override
    public long writeBytes(@NonNull CharSequence text) throws IOException {
        return text.length();
    }

    @Override
    public long writeBytes(@NonNull CharSequence text, int start, int length) throws IOException {
        checkRangeLen(text.length(), start, length);
        return length;
    }

    @Override
    public void writeChars(@NonNull String text) throws IOException {
    }

    @Override
    public long writeChars(@NonNull CharSequence text) throws IOException {
        return text.length();
    }

    @Override
    public long writeChars(@NonNull CharSequence text, int start, int length) throws IOException {
        checkRangeLen(text.length(), start, length);
        return length;
    }

    @Override
    public void writeUTF(@NonNull String text) throws IOException {
    }

    @Override
    public void writeUTF(@NonNull CharSequence text) throws IOException {
    }

    @Override
    public void writeVarUTF(@NonNull CharSequence text) throws IOException {
    }

    @Override
    public void writeString(@NonNull CharSequence text, @NonNull Charset charset) throws IOException {
    }

    @Override
    public void writeVarString(@NonNull CharSequence text, @NonNull Charset charset) throws IOException {
    }

    @Override
    public long writeText(@NonNull CharSequence text, @NonNull Charset charset) throws IOException {
        return text.length();
    }

    @Override
    public long writeText(@NonNull CharSequence text, int start, int length, @NonNull Charset charset) throws IOException {
        checkRangeLen(text.length(), start, length);
        return length;
    }

    @Override
    public <E extends Enum<E>> void writeEnum(@NonNull E e) throws IOException {
    }

    @Override
    public void writeVarInt(int value) throws IOException {
    }

    @Override
    public void writeVarIntZigZag(int value) throws IOException {
    }

    @Override
    public void writeVarLong(long value) throws IOException {
    }

    @Override
    public void writeVarLongZigZag(long value) throws IOException {
    }

    @Override
    public void write(@NonNull byte[] src) throws IOException {
    }

    @Override
    public void write(@NonNull byte[] src, int start, int length) throws IOException {
    }

    @Override
    public int write(@NonNull ByteBuffer src) throws IOException {
        int remaining = src.remaining();
        src.position(src.limit());
        return remaining;
    }

    @Override
    public int write(@NonNull ByteBuf src) throws IOException {
        int readableBytes = src.readableBytes();
        src.skipBytes(readableBytes);
        return readableBytes;
    }

    @Override
    public int write(@NonNull ByteBuf src, int count) throws IOException {
        checkRangeLen(src.writerIndex(), src.readerIndex(), count);
        src.skipBytes(count);
        return count;
    }

    @Override
    public int write(@NonNull ByteBuf src, int start, int length) throws IOException {
        checkRangeLen(src.writerIndex(), start, length);
        return length;
    }

    @Override
    public java.io.OutputStream asOutputStream() throws IOException {
        return OUTPUT_STREAM;
    }

    @Override
    public void flush() throws IOException {
    }

    private static final class InputStream extends DataInAsInputStream  {
        public InputStream() {
            super(INSTANCE);
        }

        @Override
        public int read() throws IOException {
            return 0;
        }

        @Override
        public int read(@NonNull byte[] dst) throws IOException {
            Arrays.fill(dst, (byte) 0);
            return dst.length;
        }

        @Override
        public int read(@NonNull byte[] dst, int start, int length) throws IOException {
            checkRangeLen(dst.length, start, length);
            Arrays.fill(dst, start, start + length, (byte) 0);
            return length;
        }

        @Override
        public long skip(long n) throws IOException {
            return notNegative(n);
        }

        @Override
        public int available() throws IOException {
            return 0;
        }

        @Override
        public void close() throws IOException {
        }
    }

    private static final class OutputStream extends DataOutAsOutputStream    {
        public OutputStream() {
            super(INSTANCE);
        }

        @Override
        public void write(int b) throws IOException {
        }

        @Override
        public void write(@NonNull byte[] src, int start, int length) throws IOException {
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void close() throws IOException {
        }
    }
}
