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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If applied to a class, indicates that the class is value-based.
 * <p>
 * Instances of value-based classes are not guaranteed to have an object identity and are not guaranteed to be allocated on
 * the heap. Among other limitations, this means that comparing them with {@code ==} is undefined behavior, their
 * {@link System#identityHashCode(Object) identity hash code} is not defined, and they may lack an implicit monitor (and
 * therefore cannot be synchronized on).
 * <p>
 * Currently this annotation has no practical meaning, but it may eventually be used to automatically convert annotated
 * classes into proper <a href="https://openjdk.org/jeps/401">value classes (JEP 401)</a>.
 *
 * @author DaPorkchop_
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE})
public @interface ValueBased {
}
