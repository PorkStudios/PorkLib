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

import lombok.NonNull;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A re-implementation of {@link java.io.BufferedOutputStream} that doesn't reallocate an
 * array every time it's initialized
 *
 * @author DaPorkchop_
 */
public class NonWastingBufferedOutputStream extends FilterOutputStream {
    private static final ThreadLocal<byte[]> BUFFER_CACHE = ThreadLocal.withInitial(() -> new byte[8192]);

    /**
     * Buffers an {@link OutputStream} with a default buffer size of 8192
     *
     * @param os the {@link OutputStream} to buffer
     * @return a {@link NonWastingBufferedOutputStream} wrapping the given {@link OutputStream}
     */
    public static NonWastingBufferedOutputStream buffer(@NonNull OutputStream os) {
        return buffer(os, 8192);
    }

    /**
     * Buffers an {@link OutputStream} with a default buffer size of 8192
     *
     * @param os   the {@link OutputStream} to buffer
     * @param size the size of the buffer. if the thread-local buffer cache (see {@link #BUFFER_CACHE})
     *             currently stores a buffer that isn't the same size as this parameter, testMethodThing new buffer
     *             will be created and stored as the thread-local buffer.
     * @return a {@link NonWastingBufferedOutputStream} wrapping the given {@link OutputStream}
     */
    public static NonWastingBufferedOutputStream buffer(@NonNull OutputStream os, int size) {
        if (size <= 0) {
            throw new IllegalArgumentException(String.format("Illegal buffer size: %d (must be more than 0)", size));
        }
        byte[] buf = BUFFER_CACHE.get();
        if (buf.length != size) {
            buf = new byte[size];
            BUFFER_CACHE.set(buf);
        }
        return new NonWastingBufferedOutputStream(os, buf);
    }
    protected byte[] buf;
    protected int count;

    public NonWastingBufferedOutputStream(@NonNull OutputStream out) {
        this(out, BUFFER_CACHE.get());
    }

    public NonWastingBufferedOutputStream(@NonNull OutputStream out, @NonNull byte[] buf) {
        super(out);
        if (buf.length <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
        this.buf = buf;
    }

    private void flushBuffer() throws IOException {
        if (count > 0) {
            out.write(buf, 0, count);
            count = 0;
        }
    }

    public synchronized void write(int b) throws IOException {
        if (count >= buf.length) {
            flushBuffer();
        }
        buf[count++] = (byte) b;
    }

    public synchronized void write(byte b[], int off, int len) throws IOException {
        if (len >= buf.length) {
            /* If the request length exceeds the size of the output buffer,
               flush the output buffer and then write the data directly.
               In this way buffered streams will cascade harmlessly. */
            flushBuffer();
            out.write(b, off, len);
            return;
        }
        if (len > buf.length - count) {
            flushBuffer();
        }
        System.arraycopy(b, off, buf, count, len);
        count += len;
    }

    public synchronized void flush() throws IOException {
        flushBuffer();
        out.flush();
    }
}
