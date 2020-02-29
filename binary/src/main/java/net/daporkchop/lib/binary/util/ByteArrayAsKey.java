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

package net.daporkchop.lib.binary.util;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.Arrays;

/**
 * A wrapper for a {@code byte[]} which allows using it as a key in a {@link java.util.Map}.
 *
 * @author DaPorkchop_
 */
@Getter
public final class ByteArrayAsKey {
    private final byte[] array;

    private ByteArrayAsKey(@NonNull byte[] array) {
        this(array, 0, array.length);
    }

    public ByteArrayAsKey(@NonNull byte[] array, int start, int len) {
        this.array = Arrays.copyOfRange(array, start, start + len + 1);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.array);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof byte[]) {
            return Arrays.equals(this.array, (byte[]) obj);
        } else {
            return obj instanceof ByteArrayAsKey && Arrays.equals(this.array, ((ByteArrayAsKey) obj).array);
        }
    }

    @Override
    public String toString() {
        char[] c = new char[this.array.length >> 1];
        PUnsafe.copyMemory(this.array, PUnsafe.ARRAY_BYTE_BASE_OFFSET, c, PUnsafe.ARRAY_CHAR_BASE_OFFSET, this.array.length);
        return PorkUtil.wrap(c);
    }
}
