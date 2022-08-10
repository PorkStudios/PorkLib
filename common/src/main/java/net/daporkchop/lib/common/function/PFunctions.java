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

package net.daporkchop.lib.common.function;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.function.exception.ESupplier;

import java.lang.reflect.Constructor;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Some useful methods for dealing with (((functions))) i.e. in this case functional interfaces, which in most cases will be lambda expressions.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PFunctions {
    /**
     * Creates a {@link Function} which will throw an exception when invoked.
     *
     * @param clazz the class of the exception to throw. Must have a simple no-args constructor
     * @return a {@link Function} which will throw an exception when invoked
     */
    public <T, R> Function<T, R> throwing(@NonNull Class<? extends Throwable> clazz) {
        try {
            Constructor<? extends Throwable> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return throwing((ESupplier<Throwable>) constructor::newInstance);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a {@link Function} which will throw an exception when invoked.
     *
     * @param supplier a {@link Supplier} which will supply instances of {@link Throwable} to be thrown
     * @return a {@link Function} which will throw an exception when invoked
     */
    public <T, R> Function<T, R> throwing(@NonNull Supplier<Throwable> supplier) {
        return t -> {
            throw new RuntimeException(supplier.get());
        };
    }

    /**
     * Logically inverts a {@link Predicate}.
     * <p>
     * Convenience method because calling {@link Predicate#negate()} directly on a lambda method reference requires an explicit cast to the target {@link Predicate} type.
     *
     * @param predicate the predicate to invert
     * @return a {@link Predicate} that will return the opposite value of whatever is returned by the original
     */
    public <T> Predicate<T> not(@NonNull Predicate<T> predicate) {
        return predicate.negate();
    }

    /**
     * @deprecated use {@link Function#identity()}
     */
    @Deprecated
    public <T> Function<T, T> identity() {
        return Function.identity();
    }
}
