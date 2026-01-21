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
import net.daporkchop.lib.binary.util.NoMoreSpaceException;
import net.daporkchop.lib.binary.util.PNioBuffers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
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
    public void write(int b) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeBoolean(boolean b) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeByte(int b) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeShort(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeShortLE(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeShort(int v, @NonNull ByteOrder order) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeChar(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeCharLE(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeChar(int v, @NonNull ByteOrder order) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeInt(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeIntLE(int v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeInt(int v, @NonNull ByteOrder order) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeLong(long v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeLongLE(long v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeLong(long v, @NonNull ByteOrder order) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeFloat(float v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeFloatLE(float v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeFloat(float v, @NonNull ByteOrder order) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeDouble(double v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeDoubleLE(double v) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeDouble(double v, @NonNull ByteOrder order) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeBytes(@NonNull String text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public long writeBytes(@NonNull CharSequence text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return text.length();
    }

    @Override
    public long writeBytes(@NonNull CharSequence text, int offset, int length) throws ClosedChannelException, NoMoreSpaceException, IOException {
        checkRangeLen(text.length(), offset, length);
        return length;
    }

    @Override
    public void writeChars(@NonNull String text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public long writeChars(@NonNull CharSequence text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return (long) text.length() << 1L;
    }

    @Override
    public long writeChars(@NonNull CharSequence text, int offset, int length) throws ClosedChannelException, NoMoreSpaceException, IOException {
        checkRangeLen(text.length(), offset, length);
        return (long) length << 1L;
    }

    @Override
    public void writeCharsLE(@NonNull String text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public long writeCharsLE(@NonNull CharSequence text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return (long) text.length() << 1L;
    }

    @Override
    public long writeCharsLE(@NonNull CharSequence text, int offset, int length) throws ClosedChannelException, NoMoreSpaceException, IOException {
        checkRangeLen(text.length(), offset, length);
        return (long) length << 1L;
    }

    @Override
    public void writeUTF(@NonNull String text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeUTF(@NonNull CharSequence text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeVarUTF(@NonNull CharSequence text) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeString(@NonNull CharSequence text, @NonNull Charset charset) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeVarString(@NonNull CharSequence text, @NonNull Charset charset) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public long writeText(@NonNull CharSequence text, @NonNull Charset charset) throws ClosedChannelException, NoMoreSpaceException, IOException {
        return text.length();
    }

    @Override
    public long writeText(@NonNull CharSequence text, int offset, int length, @NonNull Charset charset) throws ClosedChannelException, NoMoreSpaceException, IOException {
        checkRangeLen(text.length(), offset, length);
        return length;
    }

    @Override
    public <E extends Enum<E>> void writeEnum(@NonNull E e) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeVarInt(int value) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeVarIntZigZag(int value) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeVarLong(long value) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void writeVarLongZigZag(long value) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void write(@NonNull byte[] src) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public void write(@NonNull byte[] src, int offset, int length) throws ClosedChannelException, NoMoreSpaceException, IOException {
        //no-op
    }

    @Override
    public int write(@NonNull ByteBuffer src) throws ClosedChannelException, NoMoreSpaceException, IOException {
        int remaining = src.remaining();
        PNioBuffers.skipForRead(src, remaining);
        return remaining;
    }

    @Override
    public long write(@NonNull ByteBuffer[] srcs) throws ClosedChannelException, NoMoreSpaceException, IOException {
        long total = 0L;
        for (ByteBuffer src : srcs) {
            int remaining = src.remaining();
            PNioBuffers.skipForRead(src, remaining);
            total += remaining;
        }
        return total;
    }

    @Override
    public long write(@NonNull ByteBuffer[] srcs, int offset, int length) throws ClosedChannelException, NoMoreSpaceException, IOException {
        checkRangeLen(srcs.length, offset, length);
        long total = 0L;
        for (int i = 0; i < length; i++) {
            ByteBuffer src = srcs[offset + i];
            int remaining = src.remaining();
            PNioBuffers.skipForRead(src, remaining);
            total += remaining;
        }
        return total;
    }

    @Override
    public int write(@NonNull ByteBuf src) throws ClosedChannelException, NoMoreSpaceException, IOException {
        int readableBytes = src.readableBytes();
        src.skipBytes(readableBytes);
        return readableBytes;
    }

    @Override
    public int write(@NonNull ByteBuf src, int count) throws ClosedChannelException, NoMoreSpaceException, IOException {
        checkRangeLen(src.writerIndex(), src.readerIndex(), count);
        src.skipBytes(count);
        return count;
    }

    @Override
    public int write(@NonNull ByteBuf src, int offset, int length) throws ClosedChannelException, NoMoreSpaceException, IOException {
        checkRangeLen(src.writerIndex(), offset, length);
        return length;
    }

    @Override
    public long transferFrom(@NonNull DataIn src) throws ClosedChannelException, NoMoreSpaceException, IOException {
        long total = 0L;
        long skipped;
        do {
            skipped = src.skipBytes(Long.MAX_VALUE);
            total += skipped;
        } while (skipped > 0L);
        return total;
    }

    @Override
    public long transferFrom(@NonNull DataIn src, long count) throws ClosedChannelException, NoMoreSpaceException, IOException {
        if (notNegative(count, "count") == 0L) {
            return 0L;
        }

        long total = 0L;
        long skipped;
        do {
            skipped = src.skipBytes(count - total);
            total += skipped;
        } while (skipped > 0L && total < count);
        return total;
    }

    @Override
    public OutputStream asOutputStream() throws ClosedChannelException, IOException {
        return OutputStreamImpl.OUTPUT_STREAM_INSTANCE;
    }

    @Override
    public void flush() throws ClosedChannelException, IOException {
        //no-op
    }

    /**
     * Implementation of {@link OutputStream} which emulates the behavior of UNIX's {@code /dev/null}.
     * <p>
     * Used by {@link DataOut#asOutputStream()}.
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
