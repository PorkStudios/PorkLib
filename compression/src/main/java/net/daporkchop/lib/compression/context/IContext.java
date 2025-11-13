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

package net.daporkchop.lib.compression.context;

import net.daporkchop.lib.common.annotation.NotThreadSafe;
import net.daporkchop.lib.common.closeable.QuietCloseable;
import net.daporkchop.lib.compression.ICompressionProvider;
import net.daporkchop.lib.natives.util.MemoryPreference;

/**
 * Base interface for {@link OneshotContext} and {@link StreamingContext}.
 *
 * @author DaPorkchop_
 */
@NotThreadSafe
public interface IContext extends QuietCloseable {
    /**
     * @return the {@link ICompressionProvider} that created this context
     */
    ICompressionProvider provider();

    /**
     * @return the type of memory preferred by this context
     */
    default MemoryPreference memoryPreference() {
        return this.provider().memoryPreference();
    }

    /**
     * Resets this context's parameters to the defaults.
     * <p>
     * Parameters may only be reset between sessions (i.e. no [de]compression is currently ongoing).
     * <p>
     * Parameters are sticky and will remain until explicitly reset.
     *
     * @throws IllegalStateException if a [de]compression session is currently ongoing
     */
    void resetParameters() throws IllegalStateException;
}
