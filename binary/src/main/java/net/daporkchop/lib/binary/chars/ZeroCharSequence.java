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

package net.daporkchop.lib.binary.chars;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.stream.IntStream;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Implementation of a {@link CharSequence} with a predetermined length which consists entirely of {@code \NUL} characters.
 *
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public class ZeroCharSequence implements CharSequence {
    /**
     * Gets a {@link CharSequence} of the given length which consists entirely of {@code \NUL} characters.
     *
     * @param length the length
     * @return a {@link CharSequence}
     */
    public static CharSequence of(int length) {
        return length == 0 ? "" : new ZeroCharSequence(length);
    }

    protected final int length;

    protected ZeroCharSequence(int length) {
        this.length = notNegative(length, "length");
    }

    @Override
    public char charAt(int index) {
        return 0;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        checkRange(this.length, start, end);
        return start == 0 && end == this.length ? this : of(end - start);
    }

    @Override
    public IntStream chars() {
        return IntStream.range(0, this.length).map(i -> 0);
    }

    @Override
    public IntStream codePoints() {
        return this.chars();
    }
}
