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

package net.daporkchop.lib.binary.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.nio.Buffer;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Some rather hacky utilities for dealing with NIO {@link Buffer}s.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PNioBuffers {
    private static final long BUFFER_POSITION_OFFSET = PUnsafe.pork_getOffset(Buffer.class, "position");

    /**
     * Skips the given number of elements in the given {@link Buffer}.
     *
     * @param buffer the {@link Buffer}
     * @param count  the number of elements to skip. Must be positive and may not exceed the {@code buffer}'s {@link Buffer#remaining() remaining elements}
     * @return the {@link Buffer}'s position prior to skipping the given number of elements
     */
    public static int skipForRead(@NonNull Buffer buffer, int count) throws BufferUnderflowException {
        int oldPosition = buffer.position();
        int newPosition = oldPosition + notNegative(count, "count");
        if (newPosition < 0 || newPosition > buffer.limit()) {
            throw new BufferUnderflowException();
        }

        if (BUFFER_POSITION_OFFSET >= 0L) { //we can unsafely update the position field
            PUnsafe.putInt(buffer, BUFFER_POSITION_OFFSET, newPosition);
        } else { //fall back to regular approach
            buffer.position(newPosition);
        }

        return oldPosition;
    }

    /**
     * Skips the given number of elements in the given {@link Buffer}.
     *
     * @param buffer the {@link Buffer}
     * @param count  the number of elements to skip. Must be positive and may not exceed the {@code buffer}'s {@link Buffer#remaining() remaining elements}
     * @return the {@link Buffer}'s position prior to skipping the given number of elements
     */
    public static int skipForWrite(@NonNull Buffer buffer, int count) throws BufferOverflowException {
        int oldPosition = buffer.position();
        int newPosition = oldPosition + notNegative(count, "count");
        if (newPosition < 0 || newPosition > buffer.limit()) {
            throw new BufferOverflowException();
        }

        if (BUFFER_POSITION_OFFSET >= 0L) { //we can unsafely update the position field
            PUnsafe.putInt(buffer, BUFFER_POSITION_OFFSET, newPosition);
        } else { //fall back to regular approach
            buffer.position(newPosition);
        }

        return oldPosition;
    }
}
