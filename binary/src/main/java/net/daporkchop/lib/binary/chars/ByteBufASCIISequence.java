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

package net.daporkchop.lib.binary.chars;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.nio.charset.StandardCharsets;

/**
 * A wrapper around a {@link ByteBuf} to allow it to be used as a {@link CharSequence} of ASCII-encoded characters.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public final class ByteBufASCIISequence implements CharSequence {
    @NonNull
    private final ByteBuf buf;

    @Override
    public int length() {
        return this.buf.writerIndex();
    }

    @Override
    public char charAt(int index) {
        return (char) (this.buf.getByte(index) & 0xFF);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return start == 0 && end == this.buf.writerIndex() ? this : new ByteBufASCIISequence(this.buf.slice(start, end - start));
    }

    @Override
    public int hashCode() {
        final ByteBuf buf = this.buf;
        final int len = buf.writerIndex();
        int i = 0;
        for (int j = 0; j < len; j++) {
            i = i * 31 + (buf.getByte(j) & 0xFF);
        }
        return i;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof CharSequence) {
            CharSequence seq = (CharSequence) obj;
            final ByteBuf buf = this.buf;
           final int len = buf.writerIndex();
            if (seq.length() != len) {
                return false;
            }
            int i = 0;
            while (i < len && (char) (buf.getByte(i) & 0xFF) == seq.charAt(i)) {
                i++;
            }
            return i == len;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return this.buf.toString(StandardCharsets.US_ASCII);
    }
}
