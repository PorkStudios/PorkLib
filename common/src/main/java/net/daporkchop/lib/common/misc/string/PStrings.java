/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
