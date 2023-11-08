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

package net.daporkchop.lib.common.annotation;

import net.daporkchop.lib.common.misc.refcount.RefCounted;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When placed on a method parameter for a closeable/releasable type (such as an {@link AutoCloseable} or {@link RefCounted}), indicates that the method temporarily
 * borrows the caller's ownership of the parameter value and returns it to the caller upon completion, as with {@link BorrowOwnership}. However, unlike
 * {@link BorrowOwnership}, the method is permitted to use the value in a way which would cause the resource to remain accessible to other code beyond the scope of the
 * method. The method's documentation must describe any further actions which must be taken in order to safely close/release the resource.
 * <p>
 * This is intended for utilities which wrap a resource and return an object which provides a view into the resource without invalidating the original resource, but where
 * closing the original resource would cause the wrapper to become invalid. Note that use of such methods should be considered unsafe.
 *
 * @author DaPorkchop_
 * @see BorrowOwnership
 * @see TransferOwnership
 */
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.PARAMETER })
public @interface AliasOwnership {
}
