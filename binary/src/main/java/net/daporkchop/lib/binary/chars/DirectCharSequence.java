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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.misc.string.PUnsafeStrings;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.PUnsafe;

/**
 * A wrapper around a direct memory address to allow it to be used as a {@link CharSequence} of 2-byte characters (aka. UTF-16, just like a normal Java
 * {@link String}).
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public final class DirectCharSequence implements CharSequence {
    private final long addr;
    private final int  length;

    @Override
    public char charAt(int index) {
        if (index < 0 || index >= this.length) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return PUnsafe.getChar(this.addr + index * PUnsafe.ARRAY_CHAR_INDEX_SCALE);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        PorkUtil.assertInRange(this.length, start, end);
        return start == 0 && end == this.length ? this : new DirectCharSequence(this.addr + start * PUnsafe.ARRAY_CHAR_INDEX_SCALE, end - start);
    }

    @Override
    public int hashCode() {
        int i = 0;
        for (long addr = this.addr, end = addr + this.length * PUnsafe.ARRAY_CHAR_INDEX_SCALE; addr != end; addr += PUnsafe.ARRAY_CHAR_INDEX_SCALE) {
            i = i * 31 + PUnsafe.getChar(addr);
        }
        return i;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof CharSequence) {
            CharSequence seq = (CharSequence) obj;
            final long addr = this.addr;
            final int len = this.length;
            if (seq.length() != len) {
                return false;
            }
            int i = 0;
            while (i < len && PUnsafe.getChar(addr + i * PUnsafe.ARRAY_CHAR_INDEX_SCALE) == seq.charAt(i)) {
                i++;
            }
            return i == len;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        final int len = this.length;
        char[] arr = new char[len];
        PUnsafe.copyMemory(null, this.addr, arr, PUnsafe.ARRAY_CHAR_BASE_OFFSET, len * PUnsafe.ARRAY_CHAR_INDEX_SCALE);
        return PUnsafeStrings.wrap(arr);
    }
}
