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

package net.daporkchop.lib.natives;

import lombok.val;
import net.daporkchop.lib.common.util.PThrowables;

/**
 * A feature which may or may not be available on the current platform.
 *
 * @author DaPorkchop_
 */
public interface OptionalFeature {
    /**
     * @return {@code true} iff. this feature is available
     */
    default boolean isAvailable() {
        return this.unavailabilityCause() == null;
    }

    /**
     * Ensures that this feature is available.
     *
     * @throws UnsatisfiedLinkError if this feature is unavailable
     */
    default void ensureAvailability() throws UnsatisfiedLinkError {
        val unavailabilityCause = this.unavailabilityCause();
        if (unavailabilityCause != null) {
            throw PThrowables.initCause(new UnsatisfiedLinkError("feature not available: " + this), unavailabilityCause);
        }
    }

    /**
     * @return a {@link Throwable} describing the reason why this feature is currently unavailable, or {@code null} if this feature is available
     */
    Throwable unavailabilityCause();
}
