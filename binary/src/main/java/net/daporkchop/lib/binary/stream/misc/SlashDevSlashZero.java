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
import net.daporkchop.lib.common.annotation.param.NotNegative;
import net.daporkchop.lib.common.pool.recycler.Recycler;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.ClosedChannelException;
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
    public int read() throws ClosedChannelException, IOException {
        return 0;
    }

    @Override
    public boolean readBoolean() throws ClosedChannelException, EOFException, IOException {
        return false;
    }

    @Override
    public byte readByte() throws ClosedChannelException, EOFException, IOException {
        return 0;
    }

    @Override
    public int readUnsignedByte() throws ClosedChannelException, EOFException, IOException {
        return 0;
    }

    @Override
    public short readShort() throws ClosedChannelException, EOFException, IOException {
        return 0;
    }

    @Override
    public int readUnsignedShort() throws ClosedChannelException, EOFException, IOException {
        return 0;
    }

    @Override
    public short readShortLE() throws ClosedChannelException, EOFException, IOException {
        return 0;
    }

    @Override
    public int readUnsignedShortLE() throws ClosedChannelException, EOFException, IOException {
        return 0;
    }

    @Override
    public short readShort(@NonNull ByteOrder order) throws ClosedChannelException, EOFException, IOException {
        return 0;
    }

    @Override
    public int readUnsignedShort(@NonNull ByteOrder order) throws ClosedChannelException, EOFException, IOException {
        return 0;
    }

    @Override
    public char readChar() throws ClosedChannelException, EOFException, IOException {
        return 0;
    }

    @Override
    public char readCharLE() throws ClosedChannelException, EOFException, IOException {
        return 0;
    }

    @Override
    public char readChar(@NonNull ByteOrder order) throws ClosedChannelException, EOFException, IOException {
        return 0;
    }

    @Override
    public int readInt() throws ClosedChannelException, EOFException, IOException {
        return 0;
    }

    @Override
    public int readIntLE() throws ClosedChannelException, EOFException, IOException {
        return 0;
    }

    @Override
    public int readInt(@NonNull ByteOrder order) throws ClosedChannelException, EOFException, IOException {
        return 0;
    }

    @Override
    public long readLong() throws ClosedChannelException, EOFException, IOException {
        return 0;
    }

    @Override
    public long readLongLE() throws ClosedChannelException, EOFException, IOException {
        return 0;
    }

    @Override
    public long readLong(@NonNull ByteOrder order) throws ClosedChannelException, EOFException, IOException {
        return 0;
    }

    @Override
    public float readFloat() throws ClosedChannelException, EOFException, IOException {
        return 0;
    }

    @Override
    public float readFloatLE() throws ClosedChannelException, EOFException, IOException {
        return 0;
    }

    @Override
    public float readFloat(@NonNull ByteOrder order) throws ClosedChannelException, EOFException, IOException {
        return 0;
    }

    @Override
    public double readDouble() throws ClosedChannelException, EOFException, IOException {
        return 0;
    }

    @Override
    public double readDoubleLE() throws ClosedChannelException, EOFException, IOException {
        return 0;
    }

    @Override
    public double readDouble(@NonNull ByteOrder order) throws ClosedChannelException, EOFException, IOException {
        return 0;
    }

    @Override
    public String readUTF() throws ClosedChannelException, EOFException, IOException {
        return ""; //the length prefix was 0, so we return empty string
    }

    @Override
    public String readVarUTF() throws ClosedChannelException, EOFException, IOException {
        return ""; //the length prefix was 0, so we return empty string
    }

    @Override
    public String readString(@NonNull Charset charset) throws ClosedChannelException, EOFException, IOException {
        return ""; //the length prefix was 0, so we return empty string
    }

    @Override
    public String readVarString(@NonNull Charset charset) throws ClosedChannelException, EOFException, IOException {
        return ""; //the length prefix was 0, so we return empty string
    }

    @Override
    public String readString(@NotNegative long size, @NonNull Charset charset) throws ClosedChannelException, EOFException, IOException {
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
    public CharSequence readText(long size, @NonNull Charset charset) throws ClosedChannelException, EOFException, IOException {
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
    public <E extends Enum<E>> E readEnum(@NonNull Function<String, E> f) throws ClosedChannelException, EOFException, IOException {
        return null; //readBoolean() returned false, so we return null
    }

    @Override
    public int readVarInt() throws ClosedChannelException, EOFException, IOException {
        return 0;
    }

    @Override
    public int readVarIntZigZag() throws ClosedChannelException, EOFException, IOException {
        return 0;
    }

    @Override
    public long readVarLong() throws ClosedChannelException, EOFException, IOException {
        return 0;
    }

    @Override
    public long readVarLongZigZag() throws ClosedChannelException, EOFException, IOException {
        return 0;
    }

    @Override
    public int read(@NonNull byte[] dst, int start, int length) throws ClosedChannelException, IOException {
        Arrays.fill(dst, start, start + length, (byte) 0);
        return length;
    }

    @Override
    public void readFully(@NonNull byte[] dst, int start, int length) throws ClosedChannelException, EOFException, IOException {
        Arrays.fill(dst, start, start + length, (byte) 0);
    }

    @Override
    public byte[] toByteArray() throws ClosedChannelException, IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int read(@NonNull ByteBuffer dst) throws ClosedChannelException, IOException {
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
    public int read(@NonNull ByteBuf dst, int count) throws ClosedChannelException, IOException {
        dst.ensureWritable(notNegative(count, "count"));
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
    public int read(@NonNull ByteBuf dst, int offset, int length) throws ClosedChannelException, IOException {
        checkRangeLen(dst.capacity(), offset, length);

        if (dst.hasMemoryAddress()) {
            PUnsafe.setMemory(dst.memoryAddress() + offset, length, (byte) 0);
        } else if (dst.hasArray()) {
            Arrays.fill(dst.array(), dst.arrayOffset() + offset, dst.arrayOffset() + offset + length, (byte) 0);
        } else {
            for (int i = 0; i < length; i++) {
                dst.setByte(offset + i, 0);
            }
        }
        return length;
    }

    @Override
    public int readFully(@NonNull ByteBuffer dst) throws ClosedChannelException, EOFException, IOException {
        return this.read(dst);
    }

    @Override
    public int readFully(@NonNull ByteBuf dst, int count) throws ClosedChannelException, EOFException, IOException {
        try {
            return this.read(dst, count);
        } catch (ClosedChannelException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int readFully(@NonNull ByteBuf dst, int start, int length) throws ClosedChannelException, EOFException, IOException {
        try {
            return this.read(dst, start, length);
        } catch (ClosedChannelException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long transferTo(@NonNull DataOut dst) throws ClosedChannelException, IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long transferTo(@NonNull DataOut dst, long count) throws ClosedChannelException, IOException {
        if (notNegative(count, "count") == 0L) {
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
    public InputStream asInputStream() throws ClosedChannelException, IOException {
        return InputStreamImpl.INPUT_STREAM_INSTANCE;
    }

    @Override
    public long remaining() throws ClosedChannelException, IOException {
        return Long.MAX_VALUE;
    }

    @Override
    public int skipBytes(int n) throws ClosedChannelException, IOException {
        return max(n, 0);
    }

    @Override
    public long skipBytes(long n) throws ClosedChannelException, IOException {
        return max(n, 0L);
    }

    @Override
    public int skipBytesFully(int n) throws ClosedChannelException, EOFException, IOException {
        return max(n, 0);
    }

    @Override
    public long skipBytesFully(long n) throws ClosedChannelException, EOFException, IOException {
        return max(n, 0L);
    }

    /**
     * Implementation of {@link InputStream} which emulates the behavior of UNIX's {@code /dev/zero}.
     * <p>
     * Used by {@link DataIn#asInputStream()}.
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
            return Integer.MAX_VALUE;
        }

        @Override
        public void close() {
            //no-op
        }
    }
}
