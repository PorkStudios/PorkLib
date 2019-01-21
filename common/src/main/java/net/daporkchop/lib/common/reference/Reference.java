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

package net.daporkchop.lib.common.reference;

import lombok.NonNull;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author DaPorkchop_
 */
public interface Reference<T> {
    T get();

    T set(T val);

    default T swap(T val) {
        T old = this.get();
        this.set(val);
        return old;
    }

    default boolean missing() {
        return this.get() == null;
    }

    default boolean has() {
        return this.get() != null;
    }

    default T getOrDefault(T def) {
        return this.missing() ? def : this.get();
    }

    default T getOrSet(T def) {
        return this.missing() ? this.set(def) : this.get();
    }

    default T compute(@NonNull Supplier<T> supplier) {
        return this.set(supplier.get());
    }

    default T computeIfAbsent(@NonNull Supplier<T> supplier) {
        return this.missing() ? this.set(supplier.get()) : this.get();
    }

    default T computeIfPresent(@NonNull Supplier<T> supplier) {
        return this.has() ? this.set(supplier.get()) : null;
    }

    default T map(@NonNull Function<T, T> function) {
        return this.set(function.apply(this.get()));
    }

    default T mapIfPresent(@NonNull Function<T, T> function) {
        return this.has() ? this.set(function.apply(this.get())) : null;
    }
}
