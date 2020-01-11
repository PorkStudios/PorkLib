/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.common.cache;

import lombok.NonNull;

import java.util.function.Supplier;

/**
 * A cache holds a reference to an object
 *
 * @author DaPorkchop_
 */
public interface Cache<T> extends Supplier<T> {
    /**
     * Gets a simple {@link Cache} will compute the value using the given {@link Supplier} once first requested.
     *
     * @param factory the {@link Supplier} for the value
     * @param <T>     the value type
     * @return a {@link Cache}
     */
    static <T> Cache<T> late(@NonNull Supplier<T> factory) {
        return new LateReferencedCache<>(factory);
    }

    /**
     * Gets a simple {@link Cache} will compute the value using the given {@link Supplier} once first requested, and store it in a soft reference,
     * allowing it to be garbage-collected later on if the garbage-collector deems it necessary. If garbage-collected, it will be re-computed using the
     * {@link Supplier} and cached again.
     *
     * @param factory the {@link Supplier} for the value
     * @param <T>     the value type
     * @return a {@link Cache}
     */
    static <T> Cache<T> soft(@NonNull Supplier<T> factory) {
        return new SoftCache<>(factory);
    }

    /**
     * Get an instance
     *
     * @return an instance
     */
    T get();
}
