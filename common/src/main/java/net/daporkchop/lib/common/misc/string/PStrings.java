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

package net.daporkchop.lib.common.misc.string;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.util.PArrays;
import net.daporkchop.lib.common.util.PorkUtil;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author DaPorkchop_
 */
@UtilityClass
public class PStrings {
    public StringGroup split(@NonNull String src, char delimiter) {
        return split(PorkUtil.unwrap(src), delimiter);
    }

    public StringGroup split(@NonNull char[] src, char delimiter) {
        final int length = src.length;
        List<char[]> list = new LinkedList<>(); //probably better performance-wise (due to O(1) add time in all cases)? benchmarks needed

        int off = 0;
        int next;
        while ((next = PArrays.indexOf(src, delimiter, off, length)) != -1) {
            list.add(Arrays.copyOfRange(src, off, next));
            off = next + 1;
        }
        list.add(Arrays.copyOfRange(src, off, length));

        return new StringGroup(list.toArray(new char[list.size()][]));
    }

    public String clone(@NonNull String src)    {
        return PorkUtil.wrap(PorkUtil.unwrap(src).clone());
    }
}
