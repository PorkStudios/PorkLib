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
 *
 */

package net.daporkchop.lib.common.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.math.PMath;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * Additional general-purpose methods for dealing with arrays, intended to compliment {@link Arrays}.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PArrays {
    //
    // SHUFFLE
    //

    /**
     * Shuffles the elements of the given array.
     *
     * @param arr the array
     */
    public static void shuffle(boolean @NonNull [] arr) {
        shuffle(arr, ThreadLocalRandom.current());
    }

    /**
     * Shuffles the elements of the given array using the provided {@link Random} to generate random numbers.
     *
     * @param arr    the array
     * @param random the {@link Random} to use
     */
    public static void shuffle(boolean @NonNull [] arr, @NonNull Random random) {
        for (int i = 0, length = arr.length; i < length; i++) {
            swap(arr, i, random.nextInt(length));
        }
    }

    /**
     * Shuffles the elements of the given array using the provided {@link IntUnaryOperator} to generate random numbers.
     *
     * @param arr    the array
     * @param random the {@link IntUnaryOperator} to use as a source of random numbers. Given an exclusive upper bound, it should return a random number uniformly
     *               distributed in the range {@code [0-bound)}, as with {@link Random#nextInt(int)}.
     */
    public static void shuffle(boolean @NonNull [] arr, @NonNull IntUnaryOperator random) {
        for (int i = 0, length = arr.length; i < length; i++) {
            swap(arr, i, random.applyAsInt(length));
        }
    }

    /**
     * Shuffles the elements of the given array.
     *
     * @param arr the array
     */
    public static void shuffle(byte @NonNull [] arr) {
        shuffle(arr, ThreadLocalRandom.current());
    }

    /**
     * Shuffles the elements of the given array using the provided {@link Random} to generate random numbers.
     *
     * @param arr    the array
     * @param random the {@link Random} to use
     */
    public static void shuffle(byte @NonNull [] arr, @NonNull Random random) {
        for (int i = 0, length = arr.length; i < length; i++) {
            swap(arr, i, random.nextInt(length));
        }
    }

    /**
     * Shuffles the elements of the given array using the provided {@link IntUnaryOperator} to generate random numbers.
     *
     * @param arr    the array
     * @param random the {@link IntUnaryOperator} to use as a source of random numbers. Given an exclusive upper bound, it should return a random number uniformly
     *               distributed in the range {@code [0-bound)}, as with {@link Random#nextInt(int)}.
     */
    public static void shuffle(byte @NonNull [] arr, @NonNull IntUnaryOperator random) {
        for (int i = 0, length = arr.length; i < length; i++) {
            swap(arr, i, random.applyAsInt(length));
        }
    }

    /**
     * Shuffles the elements of the given array.
     *
     * @param arr the array
     */
    public static void shuffle(short @NonNull [] arr) {
        shuffle(arr, ThreadLocalRandom.current());
    }

    /**
     * Shuffles the elements of the given array using the provided {@link Random} to generate random numbers.
     *
     * @param arr    the array
     * @param random the {@link Random} to use
     */
    public static void shuffle(short @NonNull [] arr, @NonNull Random random) {
        for (int i = 0, length = arr.length; i < length; i++) {
            swap(arr, i, random.nextInt(length));
        }
    }

    /**
     * Shuffles the elements of the given array using the provided {@link IntUnaryOperator} to generate random numbers.
     *
     * @param arr    the array
     * @param random the {@link IntUnaryOperator} to use as a source of random numbers. Given an exclusive upper bound, it should return a random number uniformly
     *               distributed in the range {@code [0-bound)}, as with {@link Random#nextInt(int)}.
     */
    public static void shuffle(short @NonNull [] arr, @NonNull IntUnaryOperator random) {
        for (int i = 0, length = arr.length; i < length; i++) {
            swap(arr, i, random.applyAsInt(length));
        }
    }

    /**
     * Shuffles the elements of the given array.
     *
     * @param arr the array
     */
    public static void shuffle(char @NonNull [] arr) {
        shuffle(arr, ThreadLocalRandom.current());
    }

    /**
     * Shuffles the elements of the given array using the provided {@link Random} to generate random numbers.
     *
     * @param arr    the array
     * @param random the {@link Random} to use
     */
    public static void shuffle(char @NonNull [] arr, @NonNull Random random) {
        for (int i = 0, length = arr.length; i < length; i++) {
            swap(arr, i, random.nextInt(length));
        }
    }

    /**
     * Shuffles the elements of the given array using the provided {@link IntUnaryOperator} to generate random numbers.
     *
     * @param arr    the array
     * @param random the {@link IntUnaryOperator} to use as a source of random numbers. Given an exclusive upper bound, it should return a random number uniformly
     *               distributed in the range {@code [0-bound)}, as with {@link Random#nextInt(int)}.
     */
    public static void shuffle(char @NonNull [] arr, @NonNull IntUnaryOperator random) {
        for (int i = 0, length = arr.length; i < length; i++) {
            swap(arr, i, random.applyAsInt(length));
        }
    }

    /**
     * Shuffles the elements of the given array.
     *
     * @param arr the array
     */
    public static void shuffle(int @NonNull [] arr) {
        shuffle(arr, ThreadLocalRandom.current());
    }

    /**
     * Shuffles the elements of the given array using the provided {@link Random} to generate random numbers.
     *
     * @param arr    the array
     * @param random the {@link Random} to use
     */
    public static void shuffle(int @NonNull [] arr, @NonNull Random random) {
        for (int i = 0, length = arr.length; i < length; i++) {
            swap(arr, i, random.nextInt(length));
        }
    }

    /**
     * Shuffles the elements of the given array using the provided {@link IntUnaryOperator} to generate random numbers.
     *
     * @param arr    the array
     * @param random the {@link IntUnaryOperator} to use as a source of random numbers. Given an exclusive upper bound, it should return a random number uniformly
     *               distributed in the range {@code [0-bound)}, as with {@link Random#nextInt(int)}.
     */
    public static void shuffle(int @NonNull [] arr, @NonNull IntUnaryOperator random) {
        for (int i = 0, length = arr.length; i < length; i++) {
            swap(arr, i, random.applyAsInt(length));
        }
    }

    /**
     * Shuffles the elements of the given array.
     *
     * @param arr the array
     */
    public static void shuffle(long @NonNull [] arr) {
        shuffle(arr, ThreadLocalRandom.current());
    }

    /**
     * Shuffles the elements of the given array using the provided {@link Random} to generate random numbers.
     *
     * @param arr    the array
     * @param random the {@link Random} to use
     */
    public static void shuffle(long @NonNull [] arr, @NonNull Random random) {
        for (int i = 0, length = arr.length; i < length; i++) {
            swap(arr, i, random.nextInt(length));
        }
    }

    /**
     * Shuffles the elements of the given array using the provided {@link IntUnaryOperator} to generate random numbers.
     *
     * @param arr    the array
     * @param random the {@link IntUnaryOperator} to use as a source of random numbers. Given an exclusive upper bound, it should return a random number uniformly
     *               distributed in the range {@code [0-bound)}, as with {@link Random#nextInt(int)}.
     */
    public static void shuffle(long @NonNull [] arr, @NonNull IntUnaryOperator random) {
        for (int i = 0, length = arr.length; i < length; i++) {
            swap(arr, i, random.applyAsInt(length));
        }
    }

    /**
     * Shuffles the elements of the given array.
     *
     * @param arr the array
     */
    public static void shuffle(float @NonNull [] arr) {
        shuffle(arr, ThreadLocalRandom.current());
    }

    /**
     * Shuffles the elements of the given array using the provided {@link Random} to generate random numbers.
     *
     * @param arr    the array
     * @param random the {@link Random} to use
     */
    public static void shuffle(float @NonNull [] arr, @NonNull Random random) {
        for (int i = 0, length = arr.length; i < length; i++) {
            swap(arr, i, random.nextInt(length));
        }
    }

    /**
     * Shuffles the elements of the given array using the provided {@link IntUnaryOperator} to generate random numbers.
     *
     * @param arr    the array
     * @param random the {@link IntUnaryOperator} to use as a source of random numbers. Given an exclusive upper bound, it should return a random number uniformly
     *               distributed in the range {@code [0-bound)}, as with {@link Random#nextInt(int)}.
     */
    public static void shuffle(float @NonNull [] arr, @NonNull IntUnaryOperator random) {
        for (int i = 0, length = arr.length; i < length; i++) {
            swap(arr, i, random.applyAsInt(length));
        }
    }

    /**
     * Shuffles the elements of the given array.
     *
     * @param arr the array
     */
    public static void shuffle(double @NonNull [] arr) {
        shuffle(arr, ThreadLocalRandom.current());
    }

    /**
     * Shuffles the elements of the given array using the provided {@link Random} to generate random numbers.
     *
     * @param arr    the array
     * @param random the {@link Random} to use
     */
    public static void shuffle(double @NonNull [] arr, @NonNull Random random) {
        for (int i = 0, length = arr.length; i < length; i++) {
            swap(arr, i, random.nextInt(length));
        }
    }

    /**
     * Shuffles the elements of the given array using the provided {@link IntUnaryOperator} to generate random numbers.
     *
     * @param arr    the array
     * @param random the {@link IntUnaryOperator} to use as a source of random numbers. Given an exclusive upper bound, it should return a random number uniformly
     *               distributed in the range {@code [0-bound)}, as with {@link Random#nextInt(int)}.
     */
    public static void shuffle(double @NonNull [] arr, @NonNull IntUnaryOperator random) {
        for (int i = 0, length = arr.length; i < length; i++) {
            swap(arr, i, random.applyAsInt(length));
        }
    }

    /**
     * Shuffles the elements of the given array.
     *
     * @param arr the array
     */
    public static void shuffle(Object @NonNull [] arr) {
        shuffle(arr, ThreadLocalRandom.current());
    }

    /**
     * Shuffles the elements of the given array using the provided {@link Random} to generate random numbers.
     *
     * @param arr    the array
     * @param random the {@link Random} to use
     */
    public static void shuffle(Object @NonNull [] arr, @NonNull Random random) {
        for (int i = 0, length = arr.length; i < length; i++) {
            swap(arr, i, random.nextInt(length));
        }
    }

    /**
     * Shuffles the elements of the given array using the provided {@link IntUnaryOperator} to generate random numbers.
     *
     * @param arr    the array
     * @param random the {@link IntUnaryOperator} to use as a source of random numbers. Given an exclusive upper bound, it should return a random number uniformly
     *               distributed in the range {@code [0-bound)}, as with {@link Random#nextInt(int)}.
     */
    public static void shuffle(Object @NonNull [] arr, @NonNull IntUnaryOperator random) {
        for (int i = 0, length = arr.length; i < length; i++) {
            swap(arr, i, random.applyAsInt(length));
        }
    }

    //
    // FILLED ARRAYS
    //

    /**
     * Allocates a new array of the given length with all elements set to the given value.
     *
     * @param length the length of the new array
     * @param value  the default value for elements in the new array
     * @return the new array
     */
    public static boolean[] filled(int length, boolean value) {
        boolean[] arr = PUnsafe.allocateUninitializedBooleanArray(length);
        Arrays.fill(arr, value);
        return arr;
    }

    /**
     * Allocates a new array of the given length with all elements set to the given value.
     *
     * @param length the length of the new array
     * @param value  the default value for elements in the new array
     * @return the new array
     */
    public static byte[] filled(int length, byte value) {
        byte[] arr = PUnsafe.allocateUninitializedByteArray(length);
        Arrays.fill(arr, value);
        return arr;
    }

    /**
     * Allocates a new array of the given length with all elements set to the given value.
     *
     * @param length the length of the new array
     * @param value  the default value for elements in the new array
     * @return the new array
     */
    public static short[] filled(int length, short value) {
        short[] arr = PUnsafe.allocateUninitializedShortArray(length);
        Arrays.fill(arr, value);
        return arr;
    }

    /**
     * Allocates a new array of the given length with all elements set to the given value.
     *
     * @param length the length of the new array
     * @param value  the default value for elements in the new array
     * @return the new array
     */
    public static char[] filled(int length, char value) {
        char[] arr = PUnsafe.allocateUninitializedCharArray(length);
        Arrays.fill(arr, value);
        return arr;
    }

    /**
     * Allocates a new array of the given length with all elements set to the given value.
     *
     * @param length the length of the new array
     * @param value  the default value for elements in the new array
     * @return the new array
     */
    public static int[] filled(int length, int value) {
        int[] arr = PUnsafe.allocateUninitializedIntArray(length);
        Arrays.fill(arr, value);
        return arr;
    }

    /**
     * Allocates a new array of the given length with all elements set to the given value.
     *
     * @param length the length of the new array
     * @param value  the default value for elements in the new array
     * @return the new array
     */
    public static long[] filled(int length, long value) {
        long[] arr = PUnsafe.allocateUninitializedLongArray(length);
        Arrays.fill(arr, value);
        return arr;
    }

    /**
     * Allocates a new array of the given length with all elements set to the given value.
     *
     * @param length the length of the new array
     * @param value  the default value for elements in the new array
     * @return the new array
     */
    public static float[] filled(int length, float value) {
        float[] arr = PUnsafe.allocateUninitializedFloatArray(length);
        Arrays.fill(arr, value);
        return arr;
    }

    /**
     * Allocates a new array of the given length with all elements set to the given value.
     *
     * @param length the length of the new array
     * @param value  the default value for elements in the new array
     * @return the new array
     */
    public static double[] filled(int length, double value) {
        double[] arr = PUnsafe.allocateUninitializedDoubleArray(length);
        Arrays.fill(arr, value);
        return arr;
    }

    /**
     * Allocates a new array of the given length with all elements set to the given value.
     *
     * @param length        the length of the new array
     * @param componentType the erased type of {@link T}
     * @param value         the default value for elements in the new array
     * @return the new array
     */
    public static <T> T[] filled(int length, @NonNull Class<T> componentType, T value) {
        T[] arr = uncheckedCast(Array.newInstance(componentType, length));
        if (value != null) { //array elements are initialized to null by default, so we only have to fill the array if the default value is non-null
            Arrays.fill(arr, value);
        }
        return arr;
    }

    /**
     * Allocates a new array of the given length with all elements set to the given value.
     *
     * @param length       the length of the new array
     * @param arrayCreator a function to use for allocating the new array. Assumed to be a lambda in the form {@code T[]::new}.
     * @param value        the default value for elements in the new array
     * @return the new array
     */
    public static <T> T[] filled(int length, @NonNull IntFunction<T[]> arrayCreator, T value) {
        T[] arr = arrayCreator.apply(length);
        if (value != null) { //array elements are initialized to null by default, so we only have to fill the array if the default value is non-null
            Arrays.fill(arr, value);
        }
        return arr;
    }

    /**
     * Allocates a new array of the given length with all elements set to the given value.
     * <p>
     * Note that the returned array's component type will be that of the given default value, not necessarily {@link T}.
     *
     * @param length the length of the new array
     * @param value  the default value for elements in the new array
     * @return the new array
     * @see #filled(int, Class, Object)
     * @see #filled(int, IntFunction, Object)
     */
    public static <T> T[] filledUnchecked(int length, @NonNull T value) {
        return filled(length, PorkUtil.<Class<T>>uncheckedCast(value.getClass()), value);
    }

    //
    // FILLED ARRAYS FROM SUPPLIER
    //

    /**
     * Allocates a new array of the given length with all elements set to values returned by the given {@link BooleanSupplier}.
     * <p>
     * The {@link BooleanSupplier} is evaluated once for each element. To initialize all elements to the same value, use {@link #filled(int, boolean)}.
     *
     * @param length   the length of the new array
     * @param supplier a {@link BooleanSupplier} which supplies the default value for each element in the array
     * @return the new array
     */
    public static boolean[] filledFrom(int length, @NonNull BooleanSupplier supplier) {
        boolean[] arr = PUnsafe.allocateUninitializedBooleanArray(length);
        for (int i = 0; i < length; i++) {
            arr[i] = supplier.getAsBoolean();
        }
        return arr;
    }

    //TODO: byte
    //TODO: short
    //TODO: char

    /**
     * Allocates a new array of the given length with all elements set to values returned by the given {@link IntSupplier}.
     * <p>
     * The {@link IntSupplier} is evaluated once for each element. To initialize all elements to the same value, use {@link #filled(int, int)}.
     *
     * @param length   the length of the new array
     * @param supplier a {@link IntSupplier} which supplies the default value for each element in the array
     * @return the new array
     */
    public static int[] filledFrom(int length, @NonNull IntSupplier supplier) {
        int[] arr = PUnsafe.allocateUninitializedIntArray(length);
        for (int i = 0; i < length; i++) {
            arr[i] = supplier.getAsInt();
        }
        return arr;
    }

    /**
     * Allocates a new array of the given length with all elements set to values returned by the given {@link LongSupplier}.
     * <p>
     * The {@link LongSupplier} is evaluated once for each element. To initialize all elements to the same value, use {@link #filled(int, long)}.
     *
     * @param length   the length of the new array
     * @param supplier a {@link LongSupplier} which supplies the default value for each element in the array
     * @return the new array
     */
    public static long[] filledFrom(int length, @NonNull LongSupplier supplier) {
        long[] arr = PUnsafe.allocateUninitializedLongArray(length);
        for (int i = 0; i < length; i++) {
            arr[i] = supplier.getAsLong();
        }
        return arr;
    }

    //TODO: float

    /**
     * Allocates a new array of the given length with all elements set to values returned by the given {@link DoubleSupplier}.
     * <p>
     * The {@link DoubleSupplier} is evaluated once for each element. To initialize all elements to the same value, use {@link #filled(int, double)}.
     *
     * @param length   the length of the new array
     * @param supplier a {@link DoubleSupplier} which supplies the default value for each element in the array
     * @return the new array
     */
    public static double[] filledFrom(int length, @NonNull DoubleSupplier supplier) {
        double[] arr = PUnsafe.allocateUninitializedDoubleArray(length);
        for (int i = 0; i < length; i++) {
            arr[i] = supplier.getAsDouble();
        }
        return arr;
    }

    /**
     * Allocates a new array of the given length with all elements set to values returned by the given {@link Supplier}.
     * <p>
     * The {@link Supplier} is evaluated once for each element. To initialize all elements to the same value, use {@link #filled(int, Class, Object)}.
     *
     * @param length        the length of the new array
     * @param componentType the erased type of {@link T}
     * @param supplier      a {@link Supplier} which supplies the default value for each element in the array
     * @return the new array
     */
    public static <T> T[] filledFrom(int length, @NonNull Class<T> componentType, @NonNull Supplier<? extends T> supplier) {
        T[] arr = uncheckedCast(Array.newInstance(componentType, length));
        for (int i = 0; i < length; i++) {
            arr[i] = supplier.get();
        }
        return arr;
    }

    /**
     * Allocates a new array of the given length with all elements set to values returned by the given {@link Supplier}.
     * <p>
     * The {@link Supplier} is evaluated once for each element. To initialize all elements to the same value, use {@link #filled(int, IntFunction, Object)}.
     *
     * @param length       the length of the new array
     * @param arrayCreator a function to use for allocating the new array. Assumed to be a lambda in the form {@code T[]::new}.
     * @param supplier     a {@link Supplier} which supplies the default value for each element in the array
     * @return the new array
     */
    public static <T> T[] filledFrom(int length, @NonNull IntFunction<T[]> arrayCreator, @NonNull Supplier<? extends T> supplier) {
        T[] arr = arrayCreator.apply(length);
        for (int i = 0; i < length; i++) {
            arr[i] = supplier.get();
        }
        return arr;
    }

    //
    // FILLED ARRAYS BY FUNCTION OF INDEX
    //

    /**
     * Allocates a new array of the given length with all elements set to values returned by the given {@link IntPredicate} for the element index.
     *
     * @param length   the length of the new array
     * @param function a {@link IntPredicate} which computes the default value for each element in the array from the element index
     * @return the new array
     */
    public static boolean[] filledBy(int length, @NonNull IntPredicate function) {
        boolean[] arr = PUnsafe.allocateUninitializedBooleanArray(length);
        for (int i = 0; i < length; i++) {
            arr[i] = function.test(i);
        }
        return arr;
    }

    //TODO: byte
    //TODO: short
    //TODO: char

    /**
     * Allocates a new array of the given length with all elements set to values returned by the given {@link IntUnaryOperator} for the element index.
     *
     * @param length   the length of the new array
     * @param function a {@link IntUnaryOperator} which computes the default value for each element in the array from the element index
     * @return the new array
     */
    public static int[] filledBy(int length, @NonNull IntUnaryOperator function) {
        int[] arr = PUnsafe.allocateUninitializedIntArray(length);
        for (int i = 0; i < length; i++) {
            arr[i] = function.applyAsInt(i);
        }
        return arr;
    }

    /**
     * Allocates a new array of the given length with all elements set to values returned by the given {@link IntToLongFunction} for the element index.
     *
     * @param length   the length of the new array
     * @param function a {@link IntToLongFunction} which computes the default value for each element in the array from the element index
     * @return the new array
     */
    public static long[] filledBy(int length, @NonNull IntToLongFunction function) {
        long[] arr = PUnsafe.allocateUninitializedLongArray(length);
        for (int i = 0; i < length; i++) {
            arr[i] = function.applyAsLong(i);
        }
        return arr;
    }

    //TODO: float

    /**
     * Allocates a new array of the given length with all elements set to values returned by the given {@link IntToDoubleFunction} for the element index.
     *
     * @param length   the length of the new array
     * @param function a {@link IntToDoubleFunction} which computes the default value for each element in the array from the element index
     * @return the new array
     */
    public static double[] filledBy(int length, @NonNull IntToDoubleFunction function) {
        double[] arr = PUnsafe.allocateUninitializedDoubleArray(length);
        for (int i = 0; i < length; i++) {
            arr[i] = function.applyAsDouble(i);
        }
        return arr;
    }

    /**
     * Allocates a new array of the given length with all elements set to values returned by the given {@link IntFunction} for the element index.
     *
     * @param length        the length of the new array
     * @param componentType the erased type of {@link T}
     * @param function      a {@link IntFunction} which computes the default value for each element in the array from the element index
     * @return the new array
     */
    public static <T> T[] filledBy(int length, @NonNull Class<T> componentType, @NonNull IntFunction<? extends T> function) {
        T[] arr = uncheckedCast(Array.newInstance(componentType, length));
        for (int i = 0; i < length; i++) {
            arr[i] = function.apply(i);
        }
        return arr;
    }

    /**
     * Allocates a new array of the given length with all elements set to values returned by the given {@link IntFunction} for the element index.
     *
     * @param length       the length of the new array
     * @param arrayCreator a function to use for allocating the new array. Assumed to be a lambda in the form {@code T[]::new}.
     * @param function     a {@link IntFunction} which computes the default value for each element in the array from the element index
     * @return the new array
     */
    public static <T> T[] filledBy(int length, @NonNull IntFunction<T[]> arrayCreator, @NonNull IntFunction<? extends T> function) {
        T[] arr = arrayCreator.apply(length);
        for (int i = 0; i < length; i++) {
            arr[i] = function.apply(i);
        }
        return arr;
    }

    //
    // LINEAR SEARCH
    //

    /**
     * Does a linear search to find the first index in the array containing the given value.
     *
     * @param arr the array to search
     * @param val the value to search for
     * @return the index at which the value was found, or {@code -1} if not present
     */
    public static int linearSearch(boolean @NonNull [] arr, boolean val) {
        return linearSearch(arr, 0, arr.length, val);
    }

    /**
     * Does a linear search to find the first index in the array containing the given value.
     *
     * @param arr  the array to search
     * @param from the first index in the array to search (inclusive)
     * @param to   the final index in the array to search (exclusive)
     * @param val  the value to search for
     * @return the index at which the value was found, or {@code -1} if not present
     */
    public static int linearSearch(boolean @NonNull [] arr, int from, int to, boolean val) {
        checkRange(arr.length, from, to);
        for (int i = from; i < to; i++) {
            if (arr[i] == val) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Does a linear search to find the first index in the array containing the given value.
     *
     * @param arr the array to search
     * @param val the value to search for
     * @return the index at which the value was found, or {@code -1} if not present
     */
    public static int linearSearch(byte @NonNull [] arr, byte val) {
        return linearSearch(arr, 0, arr.length, val);
    }

    /**
     * Does a linear search to find the first index in the array containing the given value.
     *
     * @param arr  the array to search
     * @param from the first index in the array to search (inclusive)
     * @param to   the final index in the array to search (exclusive)
     * @param val  the value to search for
     * @return the index at which the value was found, or {@code -1} if not present
     */
    public static int linearSearch(byte @NonNull [] arr, int from, int to, byte val) {
        checkRange(arr.length, from, to);
        for (int i = from; i < to; i++) {
            if (arr[i] == val) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Does a linear search to find the first index in the array containing the given value.
     *
     * @param arr the array to search
     * @param val the value to search for
     * @return the index at which the value was found, or {@code -1} if not present
     */
    public static int linearSearch(short @NonNull [] arr, short val) {
        return linearSearch(arr, 0, arr.length, val);
    }

    /**
     * Does a linear search to find the first index in the array containing the given value.
     *
     * @param arr  the array to search
     * @param from the first index in the array to search (inclusive)
     * @param to   the final index in the array to search (exclusive)
     * @param val  the value to search for
     * @return the index at which the value was found, or {@code -1} if not present
     */
    public static int linearSearch(short @NonNull [] arr, int from, int to, short val) {
        checkRange(arr.length, from, to);
        for (int i = from; i < to; i++) {
            if (arr[i] == val) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Does a linear search to find the first index in the array containing the given value.
     *
     * @param arr the array to search
     * @param val the value to search for
     * @return the index at which the value was found, or {@code -1} if not present
     */
    public static int linearSearch(char @NonNull [] arr, char val) {
        return linearSearch(arr, 0, arr.length, val);
    }

    /**
     * Does a linear search to find the first index in the array containing the given value.
     *
     * @param arr  the array to search
     * @param from the first index in the array to search (inclusive)
     * @param to   the final index in the array to search (exclusive)
     * @param val  the value to search for
     * @return the index at which the value was found, or {@code -1} if not present
     */
    public static int linearSearch(char @NonNull [] arr, int from, int to, char val) {
        checkRange(arr.length, from, to);
        for (int i = from; i < to; i++) {
            if (arr[i] == val) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Does a linear search to find the first index in the array containing the given value.
     *
     * @param arr the array to search
     * @param val the value to search for
     * @return the index at which the value was found, or {@code -1} if not present
     */
    public static int linearSearch(int @NonNull [] arr, int val) {
        return linearSearch(arr, 0, arr.length, val);
    }

    /**
     * Does a linear search to find the first index in the array containing the given value.
     *
     * @param arr  the array to search
     * @param from the first index in the array to search (inclusive)
     * @param to   the final index in the array to search (exclusive)
     * @param val  the value to search for
     * @return the index at which the value was found, or {@code -1} if not present
     */
    public static int linearSearch(int @NonNull [] arr, int from, int to, int val) {
        checkRange(arr.length, from, to);
        for (int i = from; i < to; i++) {
            if (arr[i] == val) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Does a linear search to find the first index in the array containing the given value.
     *
     * @param arr the array to search
     * @param val the value to search for
     * @return the index at which the value was found, or {@code -1} if not present
     */
    public static int linearSearch(long @NonNull [] arr, long val) {
        return linearSearch(arr, 0, arr.length, val);
    }

    /**
     * Does a linear search to find the first index in the array containing the given value.
     *
     * @param arr  the array to search
     * @param from the first index in the array to search (inclusive)
     * @param to   the final index in the array to search (exclusive)
     * @param val  the value to search for
     * @return the index at which the value was found, or {@code -1} if not present
     */
    public static int linearSearch(long @NonNull [] arr, int from, int to, long val) {
        checkRange(arr.length, from, to);
        for (int i = from; i < to; i++) {
            if (arr[i] == val) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Does a linear search to find the first index in the array containing the given value.
     * <p>
     * Values are compared using the {@code ==} operator.
     *
     * @param arr the array to search
     * @param val the value to search for
     * @return the index at which the value was found, or {@code -1} if not present
     */
    public static int linearSearchDefault(float @NonNull [] arr, float val) {
        return linearSearchDefault(arr, 0, arr.length, val);
    }

    /**
     * Does a linear search to find the first index in the array containing the given value.
     * <p>
     * Values are compared using the {@code ==} operator.
     *
     * @param arr  the array to search
     * @param from the first index in the array to search (inclusive)
     * @param to   the final index in the array to search (exclusive)
     * @param val  the value to search for
     * @return the index at which the value was found, or {@code -1} if not present
     */
    public static int linearSearchDefault(float @NonNull [] arr, int from, int to, float val) {
        checkRange(arr.length, from, to);
        for (int i = from; i < to; i++) {
            if (arr[i] == val) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Does a linear search to find the first index in the array containing the given value.
     * <p>
     * Values are compared using {@link PMath#floatEquals(float, float)}.
     *
     * @param arr the array to search
     * @param val the value to search for
     * @return the index at which the value was found, or {@code -1} if not present
     */
    public static int linearSearchEquals(float @NonNull [] arr, float val) {
        return linearSearchEquals(arr, 0, arr.length, val);
    }

    /**
     * Does a linear search to find the first index in the array containing the given value.
     * <p>
     * Values are compared using {@link PMath#floatEquals(float, float)}.
     *
     * @param arr  the array to search
     * @param from the first index in the array to search (inclusive)
     * @param to   the final index in the array to search (exclusive)
     * @param val  the value to search for
     * @return the index at which the value was found, or {@code -1} if not present
     */
    public static int linearSearchEquals(float @NonNull [] arr, int from, int to, float val) {
        checkRange(arr.length, from, to);
        for (int i = from; i < to; i++) {
            if (PMath.floatEquals(arr[i], val)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Does a linear search to find the first index in the array containing the given value.
     * <p>
     * Values are compared using {@link PMath#floatBitwiseEquals(float, float)}.
     *
     * @param arr the array to search
     * @param val the value to search for
     * @return the index at which the value was found, or {@code -1} if not present
     */
    public static int linearSearchBitwiseEquals(float @NonNull [] arr, float val) {
        return linearSearchBitwiseEquals(arr, 0, arr.length, val);
    }

    /**
     * Does a linear search to find the first index in the array containing the given value.
     * <p>
     * Values are compared using {@link PMath#floatBitwiseEquals(float, float)}.
     *
     * @param arr  the array to search
     * @param from the first index in the array to search (inclusive)
     * @param to   the final index in the array to search (exclusive)
     * @param val  the value to search for
     * @return the index at which the value was found, or {@code -1} if not present
     */
    public static int linearSearchBitwiseEquals(float @NonNull [] arr, int from, int to, float val) {
        checkRange(arr.length, from, to);
        for (int i = from; i < to; i++) {
            if (PMath.floatBitwiseEquals(arr[i], val)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Does a linear search to find the first index in the array containing the given value.
     * <p>
     * Values are compared using the {@code ==} operator.
     *
     * @param arr the array to search
     * @param val the value to search for
     * @return the index at which the value was found, or {@code -1} if not present
     */
    public static int linearSearchDefault(double @NonNull [] arr, double val) {
        return linearSearchDefault(arr, 0, arr.length, val);
    }

    /**
     * Does a linear search to find the first index in the array containing the given value.
     * <p>
     * Values are compared using the {@code ==} operator.
     *
     * @param arr  the array to search
     * @param from the first index in the array to search (inclusive)
     * @param to   the final index in the array to search (exclusive)
     * @param val  the value to search for
     * @return the index at which the value was found, or {@code -1} if not present
     */
    public static int linearSearchDefault(double @NonNull [] arr, int from, int to, double val) {
        checkRange(arr.length, from, to);
        for (int i = from; i < to; i++) {
            if (arr[i] == val) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Does a linear search to find the first index in the array containing the given value.
     * <p>
     * Values are compared using {@link PMath#doubleEquals(double, double)}.
     *
     * @param arr the array to search
     * @param val the value to search for
     * @return the index at which the value was found, or {@code -1} if not present
     */
    public static int linearSearchEquals(double @NonNull [] arr, double val) {
        return linearSearchEquals(arr, 0, arr.length, val);
    }

    /**
     * Does a linear search to find the first index in the array containing the given value.
     * <p>
     * Values are compared using {@link PMath#doubleEquals(double, double)}.
     *
     * @param arr  the array to search
     * @param from the first index in the array to search (inclusive)
     * @param to   the final index in the array to search (exclusive)
     * @param val  the value to search for
     * @return the index at which the value was found, or {@code -1} if not present
     */
    public static int linearSearchEquals(double @NonNull [] arr, int from, int to, double val) {
        checkRange(arr.length, from, to);
        for (int i = from; i < to; i++) {
            if (PMath.doubleEquals(arr[i], val)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Does a linear search to find the first index in the array containing the given value.
     * <p>
     * Values are compared using {@link PMath#doubleBitwiseEquals(double, double)}.
     *
     * @param arr the array to search
     * @param val the value to search for
     * @return the index at which the value was found, or {@code -1} if not present
     */
    public static int linearSearchBitwiseEquals(double @NonNull [] arr, double val) {
        return linearSearchBitwiseEquals(arr, 0, arr.length, val);
    }

    /**
     * Does a linear search to find the first index in the array containing the given value.
     * <p>
     * Values are compared using {@link PMath#doubleBitwiseEquals(double, double)}.
     *
     * @param arr  the array to search
     * @param from the first index in the array to search (inclusive)
     * @param to   the final index in the array to search (exclusive)
     * @param val  the value to search for
     * @return the index at which the value was found, or {@code -1} if not present
     */
    public static int linearSearchBitwiseEquals(double @NonNull [] arr, int from, int to, double val) {
        checkRange(arr.length, from, to);
        for (int i = from; i < to; i++) {
            if (PMath.doubleBitwiseEquals(arr[i], val)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Does a linear search to find the first index in the array containing the given value.
     * <p>
     * Values are compared using {@link java.util.Objects#equals(Object, Object)}.
     *
     * @param arr the array to search
     * @param val the value to search for
     * @return the index at which the value was found, or {@code -1} if not present
     */
    public static int linearSearch(Object @NonNull [] arr, Object val) {
        return linearSearch(arr, 0, arr.length, val);
    }

    /**
     * Does a linear search to find the first index in the array containing the given value.
     * <p>
     * Values are compared using {@link java.util.Objects#equals(Object, Object)}.
     *
     * @param arr  the array to search
     * @param from the first index in the array to search (inclusive)
     * @param to   the final index in the array to search (exclusive)
     * @param val  the value to search for
     * @return the index at which the value was found, or {@code -1} if not present
     */
    public static int linearSearch(Object @NonNull [] arr, int from, int to, Object val) {
        checkRange(arr.length, from, to);

        if (val == null) { //if value is null, equality is effectively by identity
            return linearSearchIdentity(arr, from, to, null);
        }

        for (int i = from; i < to; i++) {
            if (arr[i] != null && val.equals(arr[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Does a linear search to find the first index in the array containing the given value.
     * <p>
     * Values are compared using the {@code ==} operator.
     *
     * @param arr the array to search
     * @param val the value to search for
     * @return the index at which the value was found, or {@code -1} if not present
     */
    public static int linearSearchIdentity(Object @NonNull [] arr, Object val) {
        return linearSearchIdentity(arr, 0, arr.length, val);
    }

    /**
     * Does a linear search to find the first index in the array containing the given value.
     * <p>
     * Values are compared using the {@code ==} operator.
     *
     * @param arr  the array to search
     * @param from the first index in the array to search (inclusive)
     * @param to   the final index in the array to search (exclusive)
     * @param val  the value to search for
     * @return the index at which the value was found, or {@code -1} if not present
     */
    public static int linearSearchIdentity(Object @NonNull [] arr, int from, int to, Object val) {
        checkRange(arr.length, from, to);
        for (int i = from; i < to; i++) {
            if (arr[i] == val) {
                return i;
            }
        }
        return -1;
    }

    //
    // CONTAINS
    //

    /**
     * Does a linear search to check if the given array contains the given value.
     *
     * @param arr the array to search
     * @param val the value to search for
     * @return {@code true} if the value was found, or {@code false} if not present
     */
    public static boolean contains(boolean @NonNull [] arr, boolean val) {
        return contains(arr, 0, arr.length, val);
    }

    /**
     * Does a linear search to check if the given array contains the given value.
     *
     * @param arr  the array to search
     * @param from the first index in the array to search (inclusive)
     * @param to   the final index in the array to search (exclusive)
     * @param val  the value to search for
     * @return {@code true} if the value was found, or {@code false} if not present
     */
    public static boolean contains(boolean @NonNull [] arr, int from, int to, boolean val) {
        return linearSearch(arr, from, to, val) >= 0;
    }

    /**
     * Does a linear search to check if the given array contains the given value.
     *
     * @param arr the array to search
     * @param val the value to search for
     * @return {@code true} if the value was found, or {@code false} if not present
     */
    public static boolean contains(byte @NonNull [] arr, byte val) {
        return contains(arr, 0, arr.length, val);
    }

    /**
     * Does a linear search to check if the given array contains the given value.
     *
     * @param arr  the array to search
     * @param from the first index in the array to search (inclusive)
     * @param to   the final index in the array to search (exclusive)
     * @param val  the value to search for
     * @return {@code true} if the value was found, or {@code false} if not present
     */
    public static boolean contains(byte @NonNull [] arr, int from, int to, byte val) {
        return linearSearch(arr, from, to, val) >= 0;
    }

    /**
     * Does a linear search to check if the given array contains the given value.
     *
     * @param arr the array to search
     * @param val the value to search for
     * @return {@code true} if the value was found, or {@code false} if not present
     */
    public static boolean contains(short @NonNull [] arr, short val) {
        return contains(arr, 0, arr.length, val);
    }

    /**
     * Does a linear search to check if the given array contains the given value.
     *
     * @param arr  the array to search
     * @param from the first index in the array to search (inclusive)
     * @param to   the final index in the array to search (exclusive)
     * @param val  the value to search for
     * @return {@code true} if the value was found, or {@code false} if not present
     */
    public static boolean contains(short @NonNull [] arr, int from, int to, short val) {
        return linearSearch(arr, from, to, val) >= 0;
    }

    /**
     * Does a linear search to check if the given array contains the given value.
     *
     * @param arr the array to search
     * @param val the value to search for
     * @return {@code true} if the value was found, or {@code false} if not present
     */
    public static boolean contains(char @NonNull [] arr, char val) {
        return contains(arr, 0, arr.length, val);
    }

    /**
     * Does a linear search to check if the given array contains the given value.
     *
     * @param arr  the array to search
     * @param from the first index in the array to search (inclusive)
     * @param to   the final index in the array to search (exclusive)
     * @param val  the value to search for
     * @return {@code true} if the value was found, or {@code false} if not present
     */
    public static boolean contains(char @NonNull [] arr, int from, int to, char val) {
        return linearSearch(arr, from, to, val) >= 0;
    }

    /**
     * Does a linear search to check if the given array contains the given value.
     *
     * @param arr the array to search
     * @param val the value to search for
     * @return {@code true} if the value was found, or {@code false} if not present
     */
    public static boolean contains(int @NonNull [] arr, int val) {
        return contains(arr, 0, arr.length, val);
    }

    /**
     * Does a linear search to check if the given array contains the given value.
     *
     * @param arr  the array to search
     * @param from the first index in the array to search (inclusive)
     * @param to   the final index in the array to search (exclusive)
     * @param val  the value to search for
     * @return {@code true} if the value was found, or {@code false} if not present
     */
    public static boolean contains(int @NonNull [] arr, int from, int to, int val) {
        return linearSearch(arr, from, to, val) >= 0;
    }

    /**
     * Does a linear search to check if the given array contains the given value.
     *
     * @param arr the array to search
     * @param val the value to search for
     * @return {@code true} if the value was found, or {@code false} if not present
     */
    public static boolean contains(long @NonNull [] arr, long val) {
        return contains(arr, 0, arr.length, val);
    }

    /**
     * Does a linear search to check if the given array contains the given value.
     *
     * @param arr  the array to search
     * @param from the first index in the array to search (inclusive)
     * @param to   the final index in the array to search (exclusive)
     * @param val  the value to search for
     * @return {@code true} if the value was found, or {@code false} if not present
     */
    public static boolean contains(long @NonNull [] arr, int from, int to, long val) {
        return linearSearch(arr, from, to, val) >= 0;
    }

    /**
     * Does a linear search to check if the given array contains the given value.
     * <p>
     * Values are compared using the {@code ==} operator.
     *
     * @param arr the array to search
     * @param val the value to search for
     * @return {@code true} if the value was found, or {@code false} if not present
     */
    public static boolean containsDefault(float @NonNull [] arr, float val) {
        return containsDefault(arr, 0, arr.length, val);
    }

    /**
     * Does a linear search to check if the given array contains the given value.
     * <p>
     * Values are compared using the {@code ==} operator.
     *
     * @param arr  the array to search
     * @param from the first index in the array to search (inclusive)
     * @param to   the final index in the array to search (exclusive)
     * @param val  the value to search for
     * @return {@code true} if the value was found, or {@code false} if not present
     */
    public static boolean containsDefault(float @NonNull [] arr, int from, int to, float val) {
        return linearSearchDefault(arr, from, to, val) >= 0;
    }

    /**
     * Does a linear search to check if the given array contains the given value.
     * <p>
     * Values are compared using {@link PMath#floatEquals(float, float)}.
     *
     * @param arr the array to search
     * @param val the value to search for
     * @return {@code true} if the value was found, or {@code false} if not present
     */
    public static boolean containsEquals(float @NonNull [] arr, float val) {
        return containsEquals(arr, 0, arr.length, val);
    }

    /**
     * Does a linear search to check if the given array contains the given value.
     * <p>
     * Values are compared using {@link PMath#floatEquals(float, float)}.
     *
     * @param arr  the array to search
     * @param from the first index in the array to search (inclusive)
     * @param to   the final index in the array to search (exclusive)
     * @param val  the value to search for
     * @return {@code true} if the value was found, or {@code false} if not present
     */
    public static boolean containsEquals(float @NonNull [] arr, int from, int to, float val) {
        return linearSearchEquals(arr, from, to, val) >= 0;
    }

    /**
     * Does a linear search to check if the given array contains the given value.
     * <p>
     * Values are compared using {@link PMath#floatBitwiseEquals(float, float)}.
     *
     * @param arr the array to search
     * @param val the value to search for
     * @return {@code true} if the value was found, or {@code false} if not present
     */
    public static boolean containsBitwiseEquals(float @NonNull [] arr, float val) {
        return containsBitwiseEquals(arr, 0, arr.length, val);
    }

    /**
     * Does a linear search to check if the given array contains the given value.
     * <p>
     * Values are compared using {@link PMath#floatBitwiseEquals(float, float)}.
     *
     * @param arr  the array to search
     * @param from the first index in the array to search (inclusive)
     * @param to   the final index in the array to search (exclusive)
     * @param val  the value to search for
     * @return {@code true} if the value was found, or {@code false} if not present
     */
    public static boolean containsBitwiseEquals(float @NonNull [] arr, int from, int to, float val) {
        return linearSearchBitwiseEquals(arr, from, to, val) >= 0;
    }

    /**
     * Does a linear search to check if the given array contains the given value.
     * <p>
     * Values are compared using the {@code ==} operator.
     *
     * @param arr the array to search
     * @param val the value to search for
     * @return {@code true} if the value was found, or {@code false} if not present
     */
    public static boolean containsDefault(double @NonNull [] arr, double val) {
        return containsDefault(arr, 0, arr.length, val);
    }

    /**
     * Does a linear search to check if the given array contains the given value.
     * <p>
     * Values are compared using the {@code ==} operator.
     *
     * @param arr  the array to search
     * @param from the first index in the array to search (inclusive)
     * @param to   the final index in the array to search (exclusive)
     * @param val  the value to search for
     * @return {@code true} if the value was found, or {@code false} if not present
     */
    public static boolean containsDefault(double @NonNull [] arr, int from, int to, double val) {
        return linearSearchDefault(arr, from, to, val) >= 0;
    }

    /**
     * Does a linear search to check if the given array contains the given value.
     * <p>
     * Values are compared using {@link PMath#doubleEquals(double, double)}.
     *
     * @param arr the array to search
     * @param val the value to search for
     * @return {@code true} if the value was found, or {@code false} if not present
     */
    public static boolean containsEquals(double @NonNull [] arr, double val) {
        return containsEquals(arr, 0, arr.length, val);
    }

    /**
     * Does a linear search to check if the given array contains the given value.
     * <p>
     * Values are compared using {@link PMath#doubleEquals(double, double)}.
     *
     * @param arr  the array to search
     * @param from the first index in the array to search (inclusive)
     * @param to   the final index in the array to search (exclusive)
     * @param val  the value to search for
     * @return {@code true} if the value was found, or {@code false} if not present
     */
    public static boolean containsEquals(double @NonNull [] arr, int from, int to, double val) {
        return linearSearchEquals(arr, from, to, val) >= 0;
    }

    /**
     * Does a linear search to check if the given array contains the given value.
     * <p>
     * Values are compared using {@link PMath#doubleBitwiseEquals(double, double)}.
     *
     * @param arr the array to search
     * @param val the value to search for
     * @return {@code true} if the value was found, or {@code false} if not present
     */
    public static boolean containsBitwiseEquals(double @NonNull [] arr, double val) {
        return containsBitwiseEquals(arr, 0, arr.length, val);
    }

    /**
     * Does a linear search to check if the given array contains the given value.
     * <p>
     * Values are compared using {@link PMath#doubleBitwiseEquals(double, double)}.
     *
     * @param arr  the array to search
     * @param from the first index in the array to search (inclusive)
     * @param to   the final index in the array to search (exclusive)
     * @param val  the value to search for
     * @return {@code true} if the value was found, or {@code false} if not present
     */
    public static boolean containsBitwiseEquals(double @NonNull [] arr, int from, int to, double val) {
        return linearSearchBitwiseEquals(arr, from, to, val) >= 0;
    }

    /**
     * Does a linear search to check if the given array contains the given value.
     * <p>
     * Values are compared using {@link java.util.Objects#equals(Object, Object)}.
     *
     * @param arr the array to search
     * @param val the value to search for
     * @return {@code true} if the value was found, or {@code false} if not present
     */
    public static boolean contains(Object @NonNull [] arr, Object val) {
        return contains(arr, 0, arr.length, val);
    }

    /**
     * Does a linear search to check if the given array contains the given value.
     * <p>
     * Values are compared using {@link java.util.Objects#equals(Object, Object)}.
     *
     * @param arr  the array to search
     * @param from the first index in the array to search (inclusive)
     * @param to   the final index in the array to search (exclusive)
     * @param val  the value to search for
     * @return {@code true} if the value was found, or {@code false} if not present
     */
    public static boolean contains(Object @NonNull [] arr, int from, int to, Object val) {
        return linearSearch(arr, from, to, val) >= 0;
    }

    /**
     * Does a linear search to check if the given array contains the given value.
     * <p>
     * Values are compared using the {@code ==} operator.
     *
     * @param arr the array to search
     * @param val the value to search for
     * @return {@code true} if the value was found, or {@code false} if not present
     */
    public static boolean containsIdentity(Object @NonNull [] arr, Object val) {
        return containsIdentity(arr, 0, arr.length, val);
    }

    /**
     * Does a linear search to check if the given array contains the given value.
     * <p>
     * Values are compared using the {@code ==} operator.
     *
     * @param arr  the array to search
     * @param from the first index in the array to search (inclusive)
     * @param to   the final index in the array to search (exclusive)
     * @param val  the value to search for
     * @return {@code true} if the value was found, or {@code false} if not present
     */
    public static boolean containsIdentity(Object @NonNull [] arr, int from, int to, Object val) {
        return linearSearchIdentity(arr, from, to, val) >= 0;
    }

    //
    // ARRAY ELEMENT VALUE SWAPS
    //

    /**
     * Swaps the values stored at the two given array indices.
     *
     * @param arr the array
     * @param i0  an array index
     * @param i1  an array index
     */
    public static void swap(boolean @NonNull [] arr, int i0, int i1) {
        boolean val = arr[i0];
        arr[i0] = arr[i1];
        arr[i1] = val;
    }

    /**
     * Swaps the values stored at the two given array indices.
     *
     * @param arr the array
     * @param i0  an array index
     * @param i1  an array index
     */
    public static void swap(byte @NonNull [] arr, int i0, int i1) {
        byte val = arr[i0];
        arr[i0] = arr[i1];
        arr[i1] = val;
    }

    /**
     * Swaps the values stored at the two given array indices.
     *
     * @param arr the array
     * @param i0  an array index
     * @param i1  an array index
     */
    public static void swap(short @NonNull [] arr, int i0, int i1) {
        short val = arr[i0];
        arr[i0] = arr[i1];
        arr[i1] = val;
    }

    /**
     * Swaps the values stored at the two given array indices.
     *
     * @param arr the array
     * @param i0  an array index
     * @param i1  an array index
     */
    public static void swap(char @NonNull [] arr, int i0, int i1) {
        char val = arr[i0];
        arr[i0] = arr[i1];
        arr[i1] = val;
    }

    /**
     * Swaps the values stored at the two given array indices.
     *
     * @param arr the array
     * @param i0  an array index
     * @param i1  an array index
     */
    public static void swap(int @NonNull [] arr, int i0, int i1) {
        int val = arr[i0];
        arr[i0] = arr[i1];
        arr[i1] = val;
    }

    /**
     * Swaps the values stored at the two given array indices.
     *
     * @param arr the array
     * @param i0  an array index
     * @param i1  an array index
     */
    public static void swap(long @NonNull [] arr, int i0, int i1) {
        long val = arr[i0];
        arr[i0] = arr[i1];
        arr[i1] = val;
    }

    /**
     * Swaps the values stored at the two given array indices.
     *
     * @param arr the array
     * @param i0  an array index
     * @param i1  an array index
     */
    public static void swap(float @NonNull [] arr, int i0, int i1) {
        float val = arr[i0];
        arr[i0] = arr[i1];
        arr[i1] = val;
    }

    /**
     * Swaps the values stored at the two given array indices.
     *
     * @param arr the array
     * @param i0  an array index
     * @param i1  an array index
     */
    public static void swap(double @NonNull [] arr, int i0, int i1) {
        double val = arr[i0];
        arr[i0] = arr[i1];
        arr[i1] = val;
    }

    /**
     * Swaps the values stored at the two given array indices.
     *
     * @param arr the array
     * @param i0  an array index
     * @param i1  an array index
     */
    public static void swap(Object @NonNull [] arr, int i0, int i1) {
        Object val = arr[i0];
        arr[i0] = arr[i1];
        arr[i1] = val;
    }

    //
    // ARRAY CONCATENATION
    //

    /**
     * Concatenates the two given arrays.
     *
     * @param first  the first array
     * @param second the second array
     * @return an array containing the concatenated array elements
     */
    public static boolean[] concat(boolean @NonNull [] first, boolean @NonNull [] second) {
        boolean[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Concatenates the two given arrays.
     *
     * @param first  the first array
     * @param second the second array
     * @return an array containing the concatenated array elements
     */
    public static byte[] concat(byte @NonNull [] first, byte @NonNull [] second) {
        byte[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Concatenates the two given arrays.
     *
     * @param first  the first array
     * @param second the second array
     * @return an array containing the concatenated array elements
     */
    public static short[] concat(short @NonNull [] first, short @NonNull [] second) {
        short[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Concatenates the two given arrays.
     *
     * @param first  the first array
     * @param second the second array
     * @return an array containing the concatenated array elements
     */
    public static char[] concat(char @NonNull [] first, char @NonNull [] second) {
        char[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Concatenates the two given arrays.
     *
     * @param first  the first array
     * @param second the second array
     * @return an array containing the concatenated array elements
     */
    public static int[] concat(int @NonNull [] first, int @NonNull [] second) {
        int[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Concatenates the two given arrays.
     *
     * @param first  the first array
     * @param second the second array
     * @return an array containing the concatenated array elements
     */
    public static long[] concat(long @NonNull [] first, long @NonNull [] second) {
        long[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Concatenates the two given arrays.
     *
     * @param first  the first array
     * @param second the second array
     * @return an array containing the concatenated array elements
     */
    public static float[] concat(float @NonNull [] first, float @NonNull [] second) {
        float[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Concatenates the two given arrays.
     *
     * @param first  the first array
     * @param second the second array
     * @return an array containing the concatenated array elements
     */
    public static double[] concat(double @NonNull [] first, double @NonNull [] second) {
        double[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Concatenates the two given arrays.
     *
     * @param first  the first array
     * @param second the second array
     * @param <E>    the array element type
     * @return an array containing the concatenated array elements
     */
    public static <E> E[] concat(E @NonNull [] first, E @NonNull [] second) {
        E[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * Checks if the given arrays contain the same elements as one another, comparing elements by object identity.
     *
     * @param a0 an array
     * @param a1 an array
     * @return {@code true} if the given arrays are equal
     */
    public static boolean identityEquals(Object[] a0, Object[] a1) {
        //noinspection ArrayEquality
        if (a0 == a1) {
            return true;
        } else if (a0 == null || a1 == null || a0.length != a1.length) {
            return false;
        }

        for (int i = 0; i < a0.length; i++) {
            if (a0[i] != a1[i]) {
                return false;
            }
        }
        return true;
    }
}
