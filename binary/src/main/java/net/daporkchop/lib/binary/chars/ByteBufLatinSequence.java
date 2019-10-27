/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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

package net.daporkchop.lib.binary.chars;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.unsafe.PUnsafe;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public final class ByteBufLatinSequence implements CharSequence {
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
        return start == 0 && end == this.buf.writerIndex() ? this : new ByteBufLatinSequence(this.buf.slice(start, end - start));
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
        //TODO: optimize this
        char[] arr = new char[this.length()];
        for (int i = this.length() - 1; i >= 0; i--)    {
            arr[i] = this.charAt(i);
        }
        return PorkUtil.wrap(arr);
    }
}
