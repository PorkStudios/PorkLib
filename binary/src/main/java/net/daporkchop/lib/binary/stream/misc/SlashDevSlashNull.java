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
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.binary.stream.wrapper.DataOutAsOutputStream;
import net.daporkchop.lib.binary.util.PNioBuffers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * An implementation of {@link DataOut} which emulates the behavior of UNIX's {@code /dev/null}.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SlashDevSlashNull implements DataOut {
    private static final SlashDevSlashNull INSTANCE = new SlashDevSlashNull();

    /**
     * @return an instance of {@link DataOut} which silently discards all data written to it
     */
    public static DataOut getDataOut() {
        return INSTANCE;
    }

    /**
     * @return an instance of {@link OutputStream} which silently discards all data written to it
     */
    public static OutputStream getOutputStream() {
        return OutputStreamImpl.OUTPUT_STREAM_INSTANCE;
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
    // DataOut methods
    //
    //

    @Override
    public void write(int b) {
        //no-op
    }

    @Override
    public void writeBoolean(boolean b) {
        //no-op
    }

    @Override
    public void writeByte(int b) {
        //no-op
    }

    @Override
    public void writeShort(int v) {
        //no-op
    }

    @Override
    public void writeShortLE(int v) {
        //no-op
    }

    @Override
    public void writeShort(int v, @NonNull ByteOrder order) {
        //no-op
    }

    @Override
    public void writeChar(int v) {
        //no-op
    }

    @Override
    public void writeCharLE(int v) {
        //no-op
    }

    @Override
    public void writeChar(int v, @NonNull ByteOrder order) {
        //no-op
    }

    @Override
    public void writeInt(int v) {
        //no-op
    }

    @Override
    public void writeIntLE(int v) {
        //no-op
    }

    @Override
    public void writeInt(int v, @NonNull ByteOrder order) {
        //no-op
    }

    @Override
    public void writeLong(long v) {
        //no-op
    }

    @Override
    public void writeLongLE(long v) {
        //no-op
    }

    @Override
    public void writeLong(long v, @NonNull ByteOrder order) {
        //no-op
    }

    @Override
    public void writeFloat(float f) {
        //no-op
    }

    @Override
    public void writeFloatLE(float f) {
        //no-op
    }

    @Override
    public void writeFloat(float f, @NonNull ByteOrder order) {
        //no-op
    }

    @Override
    public void writeDouble(double d) {
        //no-op
    }

    @Override
    public void writeDoubleLE(double d) {
        //no-op
    }

    @Override
    public void writeDouble(double d, @NonNull ByteOrder order) {
        //no-op
    }

    @Override
    public void writeBytes(@NonNull String text) {
        //no-op
    }

    @Override
    public long writeBytes(@NonNull CharSequence text) {
        return text.length();
    }

    @Override
    public long writeBytes(@NonNull CharSequence text, int start, int length) {
        checkRangeLen(text.length(), start, length);
        return length;
    }

    @Override
    public void writeChars(@NonNull String text) {
        //no-op
    }

    @Override
    public long writeChars(@NonNull CharSequence text) {
        return (long) text.length() << 1L;
    }

    @Override
    public long writeChars(@NonNull CharSequence text, int start, int length) {
        checkRangeLen(text.length(), start, length);
        return (long) length << 1L;
    }

    @Override
    public void writeUTF(@NonNull String text) {
        //no-op
    }

    @Override
    public void writeUTF(@NonNull CharSequence text) {
        //no-op
    }

    @Override
    public void writeVarUTF(@NonNull CharSequence text) {
        //no-op
    }

    @Override
    public void writeString(@NonNull CharSequence text, @NonNull Charset charset) {
        //no-op
    }

    @Override
    public void writeVarString(@NonNull CharSequence text, @NonNull Charset charset) {
        //no-op
    }

    @Override
    public long writeText(@NonNull CharSequence text, @NonNull Charset charset) {
        return text.length();
    }

    @Override
    public long writeText(@NonNull CharSequence text, int start, int length, @NonNull Charset charset) {
        checkRangeLen(text.length(), start, length);
        return length;
    }

    @Override
    public <E extends Enum<E>> void writeEnum(@NonNull E e) {
        //no-op
    }

    @Override
    public void writeVarInt(int value) {
        //no-op
    }

    @Override
    public void writeVarIntZigZag(int value) {
        //no-op
    }

    @Override
    public void writeVarLong(long value) {
        //no-op
    }

    @Override
    public void writeVarLongZigZag(long value) {
        //no-op
    }

    @Override
    public void write(@NonNull byte[] src) {
        //no-op
    }

    @Override
    public void write(@NonNull byte[] src, int start, int length) {
        //no-op
    }

    @Override
    public int write(@NonNull ByteBuffer src) {
        int remaining = src.remaining();
        PNioBuffers.skipForRead(src, remaining);
        return remaining;
    }

    @Override
    public int write(@NonNull ByteBuf src) {
        int readableBytes = src.readableBytes();
        src.skipBytes(readableBytes);
        return readableBytes;
    }

    @Override
    public int write(@NonNull ByteBuf src, int count) {
        checkRangeLen(src.writerIndex(), src.readerIndex(), count);
        src.skipBytes(count);
        return count;
    }

    @Override
    public int write(@NonNull ByteBuf src, int start, int length) {
        checkRangeLen(src.writerIndex(), start, length);
        return length;
    }

    @Override
    public long transferFrom(@NonNull DataIn src) throws IOException {
        long total = 0L;
        long skipped;
        do {
            skipped = src.skipBytes(Long.MAX_VALUE);
            total += skipped;
        } while (skipped > 0L);
        return total;
    }

    @Override
    public long transferFrom(@NonNull DataIn src, long count) throws IOException {
        long total = 0L;
        long skipped;
        do {
            skipped = src.skipBytes(count - total);
            total += skipped;
        } while (skipped > 0L && total < count);
        return total;
    }

    @Override
    public OutputStream asOutputStream() {
        return OutputStreamImpl.OUTPUT_STREAM_INSTANCE;
    }

    @Override
    public void flush() {
        //no-op
    }

    /**
     * Implementation of {@link OutputStream} which emulates the behavior of UNIX's {@code /dev/null}.
     * <p>
     * Used by {@link SlashDevSlashNull#asOutputStream()}.
     *
     * @author DaPorkchop_
     */
    private static final class OutputStreamImpl extends DataOutAsOutputStream {
        public static final OutputStreamImpl OUTPUT_STREAM_INSTANCE = new OutputStreamImpl();

        public OutputStreamImpl() {
            super(SlashDevSlashNull.INSTANCE);
        }

        @Override
        public void write(int b) {
            //no-op
        }

        @Override
        public void write(@NonNull byte[] b) {
            //no-op
        }

        @Override
        public void write(@NonNull byte[] src, int start, int length) {
            checkRangeLen(src.length, start, length);
        }

        @Override
        public void flush() {
            //no-op
        }

        @Override
        public void close() {
            //no-op
        }
    }
}
