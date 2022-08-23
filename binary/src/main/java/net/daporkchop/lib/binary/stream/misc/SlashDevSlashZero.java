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

package net.daporkchop.lib.binary.stream.misc;

import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.binary.chars.ZeroCharSequence;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.binary.stream.wrapper.DataInAsInputStream;
import net.daporkchop.lib.common.pool.recycler.Recycler;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Function;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * An implementation of {@link DataIn} which emulates the behavior of UNIX's {@code /dev/zero}.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SlashDevSlashZero implements DataIn {
    private static final SlashDevSlashZero INSTANCE = new SlashDevSlashZero();

    /**
     * @return an instance of {@link DataIn} which always returns zero on reads
     */
    public static DataIn getDataIn() {
        return INSTANCE;
    }

    /**
     * @return an instance of {@link InputStream} which silently discards all data written to it
     */
    public static InputStream getInputStream() {
        return InputStreamImpl.INPUT_STREAM_INSTANCE;
    }

    @Override
    public boolean isDirect() {
        return true;
    }

    @Override
    public boolean isHeap() {
        return true;
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void close() {
        //no-op
    }

    //
    //
    // DataIn methods
    //
    //

    @Override
    public int read() {
        return 0;
    }

    @Override
    public boolean readBoolean() {
        return false;
    }

    @Override
    public byte readByte() {
        return 0;
    }

    @Override
    public int readUnsignedByte() {
        return 0;
    }

    @Override
    public short readShort() {
        return 0;
    }

    @Override
    public int readUnsignedShort() {
        return 0;
    }

    @Override
    public short readShortLE() {
        return 0;
    }

    @Override
    public int readUnsignedShortLE() {
        return 0;
    }

    @Override
    public short readShort(@NonNull ByteOrder order) {
        return 0;
    }

    @Override
    public int readUnsignedShort(@NonNull ByteOrder order) {
        return 0;
    }

    @Override
    public char readChar() {
        return 0;
    }

    @Override
    public char readCharLE() {
        return 0;
    }

    @Override
    public char readChar(@NonNull ByteOrder order) {
        return 0;
    }

    @Override
    public int readInt() {
        return 0;
    }

    @Override
    public int readIntLE() {
        return 0;
    }

    @Override
    public int readInt(@NonNull ByteOrder order) {
        return 0;
    }

    @Override
    public long readLong() {
        return 0;
    }

    @Override
    public long readLongLE() {
        return 0;
    }

    @Override
    public long readLong(@NonNull ByteOrder order) {
        return 0;
    }

    @Override
    public float readFloat() {
        return 0;
    }

    @Override
    public float readFloatLE() {
        return 0;
    }

    @Override
    public float readFloat(@NonNull ByteOrder order) {
        return 0;
    }

    @Override
    public double readDouble() {
        return 0;
    }

    @Override
    public double readDoubleLE() {
        return 0;
    }

    @Override
    public double readDouble(@NonNull ByteOrder order) {
        return 0;
    }

    @Override
    public String readUTF() {
        return ""; //the length prefix was 0, so we return empty string
    }

    @Override
    public String readVarUTF() {
        return ""; //the length prefix was 0, so we return empty string
    }

    @Override
    public String readString(@NonNull Charset charset) {
        return ""; //the length prefix was 0, so we return empty string
    }

    @Override
    public String readVarString(@NonNull Charset charset) {
        return ""; //the length prefix was 0, so we return empty string
    }

    @Override
    public String readString(long size, @NonNull Charset charset) throws IOException {
        if (notNegative(size, "size") == 0L) {
            return ""; //empty string lol
        }

        int sizeBytes = toInt(size, "size");
        if (sizeBytes <= PorkUtil.bufferSize()) { //sequence small enough that it can fit in a recycled buffer
            Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
            byte[] buf = recycler.allocate();

            String result = new String(this.fill(buf, 0, sizeBytes), 0, sizeBytes, charset);

            recycler.release(buf); //release the buffer to the recycler
            return result;
        } else { //allocate new temporary byte[] (it's automatically zero-filled)
            return new String(new byte[sizeBytes], charset);
        }
    }

    @Override
    public CharSequence readText(long size, @NonNull Charset charset) throws IOException {
        if (notNegative(size, "size") == 0L) {
            return "";
        } else if (charset == StandardCharsets.US_ASCII || charset == StandardCharsets.ISO_8859_1 //always one byte per char
                   || charset == StandardCharsets.UTF_8) { //since we can only read zeroes, all UTF-8 code points will be exactly one byte long
            return ZeroCharSequence.of(toInt(size, "size"));
        } else if (charset == StandardCharsets.UTF_16BE || charset == StandardCharsets.UTF_16LE || charset == StandardCharsets.UTF_16) {
            checkArg((size & 1L) == 0L, "size must be a multiple of 2!");
            return ZeroCharSequence.of(toInt(size >> 1L, "size"));
        } else { //we don't know the charset
            return this.readString(size, charset);
        }
    }

    @Override
    public <E extends Enum<E>> E readEnum(@NonNull Function<String, E> f) {
        return null; //readBoolean() returned false, so we return null
    }

    @Override
    public int readVarInt() {
        return 0;
    }

    @Override
    public int readVarIntZigZag() {
        return 0;
    }

    @Override
    public long readVarLong() {
        return 0;
    }

    @Override
    public long readVarLongZigZag() {
        return 0;
    }

    @Override
    public int read(@NonNull byte[] dst, int start, int length) {
        Arrays.fill(dst, start, start + length, (byte) 0);
        return length;
    }

    @Override
    public void readFully(@NonNull byte[] dst, int start, int length) {
        Arrays.fill(dst, start, start + length, (byte) 0);
    }

    @Override
    public byte[] toByteArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int read(@NonNull ByteBuffer dst) {
        if (dst.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }

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
    public int read(@NonNull ByteBuf dst, int count) {
        notNegative(count, "count");
        dst.ensureWritable(count);
        int writerIndex = dst.writerIndex();
        if (dst.hasMemoryAddress()) {
            PUnsafe.setMemory(dst.memoryAddress() + writerIndex, count, (byte) 0);
        } else if (dst.hasArray()) {
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
    public int read(@NonNull ByteBuf dst, int start, int length) {
        checkRangeLen(dst.maxCapacity(), start, length);
        int writerIndex = dst.writerIndex();
        dst.ensureWritable(start + length - writerIndex);
        if (dst.hasMemoryAddress()) {
            PUnsafe.setMemory(dst.memoryAddress() + start, length, (byte) 0);
        } else if (dst.hasArray()) {
            Arrays.fill(dst.array(), dst.arrayOffset() + start, dst.arrayOffset() + start + length, (byte) 0);
        } else {
            for (int i = 0; i < length; i++) {
                dst.setByte(start + i, 0);
            }
        }
        return length;
    }

    @Override
    public int readFully(@NonNull ByteBuffer dst) {
        return this.read(dst);
    }

    @Override
    public int readFully(@NonNull ByteBuf dst, int count) {
        return this.read(dst, count);
    }

    @Override
    public int readFully(@NonNull ByteBuf dst, int start, int length) {
        return this.read(dst, start, length);
    }

    @Override
    public long transferTo(@NonNull DataOut dst) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long transferTo(@NonNull DataOut dst, long count) throws IOException {
        if (positive(count, "count") == 0L) {
            return 0L;
        }

        long total = 0L;
        if (dst.isDirect()) {
            Recycler<ByteBuffer> recycler = PorkUtil.directBufferRecycler();
            ByteBuffer buf = recycler.allocate();

            PUnsafe.setMemory(PUnsafe.pork_directBufferAddress(buf.clear()), PorkUtil.bufferSize(), (byte) 0);
            do {
                int blockSize = (int) min(count - total, PorkUtil.bufferSize());
                buf.limit(blockSize);
                dst.write(buf);
                total += blockSize;
            } while (total < count);

            recycler.release(buf); //release the buffer to the recycler
        } else {
            Recycler<byte[]> recycler = PorkUtil.heapBufferRecycler();
            byte[] buf = recycler.allocate();

            Arrays.fill(buf, (byte) 0);
            do {
                int blockSize = (int) min(count - total, PorkUtil.bufferSize());
                dst.write(buf, 0, blockSize);
                total += blockSize;
            } while (total < count);

            recycler.release(buf); //release the buffer to the recycler
        }
        return total;
    }

    @Override
    public InputStream asInputStream() {
        return InputStreamImpl.INPUT_STREAM_INSTANCE;
    }

    @Override
    public long remaining() {
        return Long.MAX_VALUE;
    }

    @Override
    public int skipBytes(int n) {
        return n;
    }

    @Override
    public long skipBytes(long n) {
        return n;
    }

    /**
     * Implementation of {@link InputStream} which emulates the behavior of UNIX's {@code /dev/zero}.
     * <p>
     * Used by {@link SlashDevSlashZero#asInputStream()}.
     *
     * @author DaPorkchop_
     */
    private static final class InputStreamImpl extends DataInAsInputStream {
        public static final InputStreamImpl INPUT_STREAM_INSTANCE = new InputStreamImpl();

        public InputStreamImpl() {
            super(INSTANCE);
        }

        @Override
        public int read() {
            return 0;
        }

        @Override
        public int read(@NonNull byte[] dst) {
            Arrays.fill(dst, (byte) 0);
            return dst.length;
        }

        @Override
        public int read(@NonNull byte[] dst, int start, int length) {
            checkRangeLen(dst.length, start, length);
            Arrays.fill(dst, start, start + length, (byte) 0);
            return length;
        }

        @Override
        public long skip(long n) {
            return notNegative(n);
        }

        @Override
        public int available() {
            return 0;
        }

        @Override
        public void close() {
            //no-op
        }
    }
}
