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

package net.daporkchop.lib.common.function;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PConstants;

import java.lang.reflect.Constructor;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Some useful methods for dealing with (((functions))) i.e. in this case functional interfaces, which in
 * most cases will be lambda expressions.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class PFunctions {
    /**
     * Creates a {@link Function} which will throw an exception when invoked.
     *
     * @param clazz the class of the exception to throw. Must have a simple no-args constructor
     * @return a {@link Function} which will throw an exception when invoked
     */
    public static <T, R> Function<T, R> throwing(@NonNull Class<? extends Throwable> clazz) {
        try {
            Constructor<? extends Throwable> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return throwing((ThrowingSupplier<Throwable>) constructor::newInstance);
        } catch (NoSuchMethodException e) {
            throw PConstants.p_exception(e);
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
            throw PConstants.p_exception(supplier.get());
        };
    }

    /**
     * Logically inverts a {@link Predicate}.
     *
     * @param predicate the predicate to invert
     * @return a {@link Predicate} that will return the opposite value of whatever is returned by the original
     */
    public static <T> Predicate<T> not(@NonNull Predicate<T> predicate) {
        return t -> !predicate.test(t);
    }

    /**
     * Gets a {@link Function} which returns the identity of the given input value.
     *
     * @param <T> the type of value
     * @return the identity function of the type
     */
    public static <T> Function<T, T> identity() {
        return o -> o;
    }
}
