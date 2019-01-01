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

package net.daporkchop.lib.binary.stream.optimizations;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.BufferOverflowException;

/**
 * An implementation of {@link java.io.ByteArrayOutputStream} that doesn't expand, and
 * will therefore only be able to write the number of bytes it was initialized with
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class NonExpandingByteArrayOutputStream extends OutputStream {
    @NonNull
    private final byte[] buf;
    private int pos;

    /**
     * Creates a new {@link NonExpandingByteArrayOutputStream}, wrapping a given buffer.
     * <p>
     * This has no advantage over the normal constructor, just a convenience method if you prefer
     * static constructor-like things
     *
     * @param buf the buffer to use
     * @return an instance of {@link NonExpandingByteArrayOutputStream} wrapping the given buffer
     */
    public static NonExpandingByteArrayOutputStream wrap(@NonNull byte[] buf) {
        return new NonExpandingByteArrayOutputStream(buf);
    }

    @Override
    public void write(int b) throws IOException {
        if (this.pos >= this.buf.length) {
            throw new BufferOverflowException();
        }
        this.buf[this.pos++] = (byte) b;
    }
}
