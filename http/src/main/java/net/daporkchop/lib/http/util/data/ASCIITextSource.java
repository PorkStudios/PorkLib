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

package net.daporkchop.lib.http.util.data;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * A simple {@link Source} that encodes text from a {@link CharSequence} using the US-ASCII charset.
 *
 * All code points with a value greater than {@code 127} will be silently corrupted (higher bits will be stripped).
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public final class ASCIITextSource implements Source {
    @NonNull
    protected final CharSequence text;

    @Override
    public long size() {
        return this.text.length();
    }

    @Override
    public ByteBuf read(long pos, @NonNull ByteBufAllocator alloc) {
        if (pos != 0L) throw new IndexOutOfBoundsException(String.valueOf(pos));

        ByteBuf buf = alloc.ioBuffer(this.text.length());
        buf.writeCharSequence(this.text, StandardCharsets.US_ASCII);
        return buf;
    }
}
