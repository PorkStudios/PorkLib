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

package net.daporkchop.lib.common.annotation;

import net.daporkchop.lib.common.misc.refcount.RefCounted;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When placed on a method parameter for a closeable/releasable type (such as an {@link AutoCloseable} or {@link RefCounted}), indicates that the method temporarily
 * borrows the caller's ownership of the parameter value and returns it to the caller upon completion.
 * <p>
 * When placed on a method which returns a closeable/releasable type (such as an {@link AutoCloseable} or {@link RefCounted}), indicates that the method returns a temporary
 * reference to a value owned by the method receiver (the target instance for virtual/interface methods, or the target class for static methods). Unless otherwise documented
 * by the annotated method, the returned reference remains valid as long as the target instance remains in scope.
 * <p>
 * This is generally the default behavior for method parameters with a closeable/releasable type.
 *
 * @author DaPorkchop_
 * @see ExtendedBorrow
 * @see Move
 */
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.METHOD, ElementType.PARAMETER })
public @interface Borrow {
}
