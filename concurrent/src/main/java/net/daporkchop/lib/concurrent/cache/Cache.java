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

package net.daporkchop.lib.concurrent.cache;

import lombok.NonNull;

import java.util.function.Supplier;

/**
 * A cache holds a reference to an object
 *
 * @author DaPorkchop_
 */
public interface Cache<T> {
    /**
     * Gets a simple cache that won't hold a reference to the object until requested
     *
     * @param supplier the supplier for the object type
     * @param <T>      the type
     * @return a cache
     */
    static <T> Cache<T> of(@NonNull Supplier<T> supplier) {
        return new Cache<T>() {
            private T val;

            @Override
            public synchronized T get() {
                if (this.val == null && (this.val = supplier.get()) == null) {
                    throw new NullPointerException();
                }
                return this.val;
            }
        };
    }

    /**
     * Get an instance
     *
     * @return an instance
     */
    T get();
}
