/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2025 DaPorkchop_
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
 */

package net.daporkchop.lib.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;

/**
 * Utility methods for working with {@link Throwable}s.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PThrowables {
    /**
     * Identical to {@link Throwable#initCause(Throwable)}, but automatically casts the return value back to the original type.
     *
     * @param instance the {@link Throwable} instance
     * @param cause    the cause
     * @return the original {@link Throwable} instance
     */
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> T initCause(@NonNull T instance, Throwable cause) {
        return (T) instance.initCause(cause);
    }

    /**
     * Identical to {@link Throwable#addSuppressed(Throwable)}, but returns the original {@link Throwable} for convenience.
     *
     * @param instance  the {@link Throwable} instance
     * @param exception the suppressed exception
     * @return the original {@link Throwable} instance
     */
    public static <T extends Throwable> T addSuppressed(@NonNull T instance, Throwable exception) {
        instance.addSuppressed(exception);
        return instance;
    }

    /**
     * Invokes {@link Throwable#addSuppressed(Throwable)} for each of the given exceptions.
     *
     * @param instance   the {@link Throwable} instance
     * @param exceptions the suppressed exceptions
     * @return the original {@link Throwable} instance
     */
    public static <T extends Throwable> T addSuppressed(@NonNull T instance, Throwable... exceptions) {
        for (val exception : exceptions) {
            instance.addSuppressed(exception);
        }
        return instance;
    }

    /**
     * Invokes {@link Throwable#addSuppressed(Throwable)} for each of the given exceptions.
     *
     * @param instance   the {@link Throwable} instance
     * @param exceptions the suppressed exceptions
     * @return the original {@link Throwable} instance
     */
    public static <T extends Throwable> T addSuppressed(@NonNull T instance, Iterable<? extends Throwable> exceptions) {
        for (val exception : exceptions) {
            instance.addSuppressed(exception);
        }
        return instance;
    }

    /**
     * Identical to {@link Throwable#setStackTrace(StackTraceElement[])}, but returns the original {@link Throwable} for convenience.
     *
     * @param instance   the {@link Throwable} instance
     * @param stackTrace the new stack trace
     * @return the original {@link Throwable} instance
     */
    public static <T extends Throwable> T setStackTrace(@NonNull T instance, StackTraceElement[] stackTrace) {
        instance.setStackTrace(stackTrace);
        return instance;
    }

    /**
     * Sets the given {@link Throwable}'s stack trace to an empty array.
     *
     * @param instance the {@link Throwable} instance
     * @return the original {@link Throwable} instance
     */
    public static <T extends Throwable> T setUnknownStackTrace(@NonNull T instance) {
        instance.setStackTrace(new StackTraceElement[0]);
        return instance;
    }
}
