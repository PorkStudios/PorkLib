/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2021 DaPorkchop_
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

package net.daporkchop.lib.common.reference;

import net.daporkchop.lib.unsafe.PCleaner;

import java.lang.ref.Reference;

/**
 * Interface which may be implemented by {@link Reference} subclasses to define a behavior to be executed at some point after the referent
 * is garbage-collected.
 * <p>
 * Unlike a {@link PCleaner}, this code is not guaranteed to be executed as long as the JVM isn't shut down early. {@link #handle()} is only guaranteed to
 * be called at some point after the referent is collected <strong>if</strong> the reference itself has not been collected. If the reference is collected
 * earlier or at the same time as the referent, it will not run.
 * <p>
 * This is not magic. {@link #handle()} will only be called automatically if invoked externally, such as through {@link PReferenceHandler}.
 *
 * @author DaPorkchop_
 */
public interface HandleableReference {
    /**
     * Fired after the referent is garbage-collected.
     */
    void handle();
}
