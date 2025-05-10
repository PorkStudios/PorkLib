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

package net.daporkchop.lib.common.function;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.closeable.QuietCloseable;
import net.daporkchop.lib.common.closeable.TypedCloseable;
import net.daporkchop.lib.common.function.exception.ESupplier;

import java.io.Closeable;
import java.lang.reflect.Constructor;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ObjIntConsumer;
import java.util.function.ObjLongConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;

/**
 * Some useful methods for dealing with (((functions))) i.e. in this case functional interfaces, which in most cases will be lambda expressions.
 * <p>
 * For the most part, these exist to reduce the number of lambda metaclasses which need to be generated and loaded for common lambda expressions.
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
     * @deprecated this is pretty ugly and should never have existed in the first place!
     */
    @Deprecated
    public static <T, R> Function<T, R> throwing(@NonNull Class<? extends Throwable> clazz) {
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
    public static <T, R> Function<T, R> throwing(@NonNull Supplier<Throwable> supplier) {
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
    public static <T> Predicate<T> not(@NonNull Predicate<T> predicate) {
        return predicate.negate();
    }

    /**
     * @deprecated use {@link Function#identity()} or {@link UnaryOperator#identity()}
     */
    @Deprecated
    public static <T> Function<T, T> identity() {
        return Function.identity();
    }

    /**
     * @return a {@link Consumer} which performs no action
     */
    public static Runnable noopRunnable() {
        return () -> {};
    }

    /**
     * @return a {@link Consumer} which performs no action
     */
    public static <T> Consumer<T> noopConsumer() {
        return ignored -> {};
    }

    /**
     * @return an {@link IntConsumer} which performs no action
     */
    public static IntConsumer noopIntConsumer() {
        return ignored -> {};
    }

    /**
     * @return a {@link LongConsumer} which performs no action
     */
    public static LongConsumer noopLongConsumer() {
        return ignored -> {};
    }

    /**
     * @return a {@link DoubleConsumer} which performs no action
     */
    public static DoubleConsumer noopDoubleConsumer() {
        return ignored -> {};
    }

    /**
     * @return a {@link BiConsumer} which performs no action
     */
    public static <T, U> BiConsumer<T, U> noopBiConsumer() {
        return (ignored0, ignored1) -> {};
    }

    /**
     * @return an {@link ObjIntConsumer} which performs no action
     */
    public static <T> ObjIntConsumer<T> noopObjIntConsumer() {
        return (ignored0, ignored1) -> {};
    }

    /**
     * @return an {@link ObjLongConsumer} which performs no action
     */
    public static <T> ObjLongConsumer<T> noopObjLongConsumer() {
        return (ignored0, ignored1) -> {};
    }

    /**
     * @return an {@link ObjDoubleConsumer} which performs no action
     */
    public static <T> ObjDoubleConsumer<T> noopObjDoubleConsumer() {
        return (ignored0, ignored1) -> {};
    }

    /**
     * @return an {@link AutoCloseable} which performs no action
     */
    public static AutoCloseable noopAutoCloseable() {
        return noopIoCloseable();
    }

    /**
     * @return a {@link Closeable} which performs no action
     */
    public static Closeable noopIoCloseable() {
        return () -> {};
    }

    /**
     * @return a {@link TypedCloseable} which performs no action
     */
    public static <E extends Exception> TypedCloseable<E> noopTypedCloseable() {
        return TypedCloseable.noop();
    }

    /**
     * @return a {@link QuietCloseable} which performs no action
     */
    public static QuietCloseable noopQuietCloseable() {
        return QuietCloseable.noop();
    }

    /**
     * @return a {@link Predicate} which performs no action and returns {@code true}
     */
    public static <T> Predicate<T> truePredicate() {
        return ignored -> true;
    }

    /**
     * @return a {@link Predicate} which performs no action and returns {@code false}
     */
    public static <T> Predicate<T> falsePredicate() {
        return ignored -> false;
    }

    /**
     * @return a {@link Predicate} which performs no action and returns the given value
     */
    public static <T> Predicate<T> constantPredicate(boolean value) {
        return ignored -> value;
    }

    /**
     * @return a {@link Supplier} which performs no action and returns {@code null}
     */
    public static <T> Supplier<T> nullSupplier() {
        return () -> null;
    }

    /**
     * @return a {@link Callable} which performs no action and returns {@code null}
     */
    public static <T> Callable<T> nullCallable() {
        return () -> null;
    }

    /**
     * @return a {@link Function} which performs no action and returns {@code null}
     */
    public static <T, R> Function<T, R> nullFunction() {
        return ignored -> null;
    }

    /**
     * @return a {@link Supplier} which performs no action and returns the given value
     */
    public static <T> Supplier<T> constantSupplier(T value) {
        return () -> value;
    }

    /**
     * @return a {@link Callable} which performs no action and returns the given value
     */
    public static <T> Callable<T> constantCallable(T value) {
        return () -> value;
    }

    /**
     * @return a {@link Function} which performs no action and returns the given value
     */
    public static <T, R> Function<T, R> constantFunction(R value) {
        return ignored -> value;
    }

    /**
     * @return a {@link Predicate} which calls {@link Objects#isNull(Object)}
     */
    public static <T> Predicate<T> isNullPredicate() {
        return Objects::isNull;
    }

    /**
     * @return a {@link Predicate} which calls {@link Objects#nonNull(Object)}
     */
    public static <T> Predicate<T> nonNullPredicate() {
        return Objects::nonNull;
    }

    /**
     * @return a {@link Function} which calls {@link String#valueOf}
     */
    public static <T> Function<T, String> toStringFunction() {
        return String::valueOf;
    }

    /**
     * @return an {@link IntFunction} which calls {@link String#valueOf(int)}
     */
    public static IntFunction<String> toStringIntFunction() {
        return String::valueOf;
    }

    /**
     * @return a {@link LongFunction} which calls {@link String#valueOf(long)}
     */
    public static LongFunction<String> toStringLongFunction() {
        return String::valueOf;
    }

    /**
     * @return a {@link DoubleFunction} which calls {@link String#valueOf(double)}
     */
    public static DoubleFunction<String> toStringDoubleFunction() {
        return String::valueOf;
    }
}
