/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
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

package net.daporkchop.lib.compat.update;

import lombok.NonNull;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

/**
 * A combination of schema changes used when updating legacy data to newer versions.
 *
 * @author DaPorkchop_
 */
public interface DataUpdater<D, V extends Comparable<? super V>, P> {
    /**
     * Gets a function which updates the data passed to it to the given version.
     * <p>
     * The returned function may modify the input data.
     *
     * @param from  the version of the input data
     * @param to    the version to update the input data to
     * @param param the parameter to use
     * @return the updated data
     * @throws IllegalArgumentException if {@code to} is less than {@code from}
     */
    default UnaryOperator<D> update(@NonNull V from, @NonNull V to, @NonNull P param) {
        BiFunction<D, P, D> func = this.update(from, to);
        return d -> func.apply(d, param);
    }

    /**
     * Gets a function which updates the data passed to it to the given version.
     * <p>
     * The returned function may modify the input data.
     *
     * @param from the version of the input data
     * @param to   the version to update the input data to
     * @return the updated data
     * @throws IllegalArgumentException if {@code to} is less than {@code from}
     */
    BiFunction<D, P, D> update(@NonNull V from, @NonNull V to);

    /**
     * Gets a function which updates the data passed to it to the given version.
     * <p>
     * The returned function may modify the input data.
     *
     * @param to    the version to update the input data to
     * @param param the parameter to use
     * @return the updated data
     * @throws IllegalArgumentException if {@code to} is less than {@code from}
     */
    default UnaryOperator<D> update(@NonNull V to, @NonNull P param) {
        BiFunction<D, P, D> func = this.update(to);
        return d -> func.apply(d, param);
    }

    /**
     * Gets a function which updates the data passed to it to the given version.
     * <p>
     * The returned function may modify the input data.
     *
     * @param to   the version to update the input data to
     * @return the updated data
     * @throws IllegalArgumentException if {@code to} is less than {@code from}
     */
    BiFunction<D, P, D> update(@NonNull V to);
}
