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

package net.daporkchop.lib.binary.oio.appendable;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.misc.string.PUnsafeStrings;
import net.daporkchop.lib.common.system.Endianess;
import net.daporkchop.lib.common.system.PlatformInfo;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.io.IOException;

/**
 * Implementation of {@link PAppendable} that appends UTF-16-encoded text to a {@link ByteBuf}.
 * <p>
 * This implementation is not thread-safe.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public final class UTF16ByteBufAppendable implements PAppendable {
    @NonNull
    protected final ByteBuf   buf;
    @NonNull
    protected final String    lineEnding;
    @NonNull
    protected final Endianess endianess;

    public UTF16ByteBufAppendable(@NonNull ByteBuf buf, @NonNull String lineEnding) {
        this(buf, lineEnding, Endianess.BIG);
    }

    public UTF16ByteBufAppendable(@NonNull ByteBuf buf, @NonNull Endianess endianess) {
        this(buf, PlatformInfo.OPERATING_SYSTEM.lineEnding(), endianess);
    }

    public UTF16ByteBufAppendable(@NonNull ByteBuf buf) {
        this(buf, PlatformInfo.OPERATING_SYSTEM.lineEnding(), Endianess.BIG);
    }

    @Override
    public UTF16ByteBufAppendable append(CharSequence seq) throws IOException {
        return seq == null ? this.append("null", 0, 4) : this.append(seq, 0, seq.length());
    }

    @Override
    public UTF16ByteBufAppendable append(CharSequence seq, int start, int end) throws IOException {
        if (seq == null) {
            seq = "null";
        }

        PorkUtil.assertInRange(seq.length(), start, end);
        if (start == end) {
            return this;
        }

        if (end - start >= (Integer.MAX_VALUE >> 1) - 1) {
            throw new IllegalArgumentException("Range too large!");
        }
        this.buf.ensureWritable((end - start) << 1);

        char[] arr = null;
        if (seq instanceof String) {
            arr = PUnsafeStrings.unwrap((String) seq);
        } else if (seq instanceof StringBuilder) {
            arr = PUnsafeStrings.unwrap((StringBuilder) seq);
        } else if (seq instanceof StringBuffer) {
            arr = PUnsafeStrings.unwrap((StringBuffer) seq);
        }
        if (arr != null) {
            if (this.endianess == Endianess.NATIVE) {
                //turbo mode if endianess is native, we can do it in a single copy

                Object base;
                long offset;
                if (this.buf.hasArray()) {
                    base = this.buf.array();
                    offset = this.buf.arrayOffset();
                } else {
                    base = null;
                    offset = this.buf.memoryAddress() + this.buf.writerIndex();
                }
                PUnsafe.copyMemory(arr, PUnsafe.ARRAY_CHAR_BASE_OFFSET + (start << 1), base, offset, (end - start) << 1);
                this.buf.writerIndex(this.buf.writerIndex() + ((end - start) << 1));
            } else if (this.endianess == Endianess.BIG) {
                for (int i = start; i < end; i++) {
                    this.buf.writeChar(arr[i]);
                }
            } else {
                for (int i = start; i < end; i++) {
                    this.buf.writeChar(Character.reverseBytes(arr[i]));
                }
            }
        } else if (this.endianess == Endianess.BIG) {
            for (int i = start; i < end; i++) {
                this.buf.writeChar(seq.charAt(i));
            }
        } else {
            for (int i = start; i < end; i++) {
                this.buf.writeChar(Character.reverseBytes(seq.charAt(i)));
            }
        }
        return this;
    }

    @Override
    public UTF16ByteBufAppendable append(char c) throws IOException {
        this.buf.writeChar(c);
        return this;
    }

    @Override
    public UTF16ByteBufAppendable appendLn() throws IOException {
        return this.append(this.lineEnding);
    }

    @Override
    public void close() throws IOException {
        //no-op
    }
}
