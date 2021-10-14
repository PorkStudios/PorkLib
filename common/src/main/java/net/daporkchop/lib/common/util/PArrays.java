/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2021 DaPorkchop_
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
import java.util.function.LongUnaryOperator;
import java.util.function.Supplier;

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
    public void shuffle(@NonNull byte[] arr) {
        shuffle(arr, ThreadLocalRandom.current());
    }

    /**
     * Shuffles the elements of the given array using the provided {@link Random} to generate random numbers.
     *
     * @param arr    the array
     * @param random the {@link Random} to use
     */
    public void shuffle(@NonNull byte[] arr, @NonNull Random random) {
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
    public void shuffle(@NonNull byte[] arr, @NonNull IntUnaryOperator random) {
        for (int i = 0, length = arr.length; i < length; i++) {
            swap(arr, i, random.applyAsInt(length));
        }
    }

    /**
     * Shuffles the elements of the given array.
     *
     * @param arr the array
     */
    public void shuffle(@NonNull short[] arr) {
        shuffle(arr, ThreadLocalRandom.current());
    }

    /**
     * Shuffles the elements of the given array using the provided {@link Random} to generate random numbers.
     *
     * @param arr    the array
     * @param random the {@link Random} to use
     */
    public void shuffle(@NonNull short[] arr, @NonNull Random random) {
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
    public void shuffle(@NonNull short[] arr, @NonNull IntUnaryOperator random) {
        for (int i = 0, length = arr.length; i < length; i++) {
            swap(arr, i, random.applyAsInt(length));
        }
    }

    /**
     * Shuffles the elements of the given array.
     *
     * @param arr the array
     */
    public void shuffle(@NonNull char[] arr) {
        shuffle(arr, ThreadLocalRandom.current());
    }

    /**
     * Shuffles the elements of the given array using the provided {@link Random} to generate random numbers.
     *
     * @param arr    the array
     * @param random the {@link Random} to use
     */
    public void shuffle(@NonNull char[] arr, @NonNull Random random) {
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
    public void shuffle(@NonNull char[] arr, @NonNull IntUnaryOperator random) {
        for (int i = 0, length = arr.length; i < length; i++) {
            swap(arr, i, random.applyAsInt(length));
        }
    }

    /**
     * Shuffles the elements of the given array.
     *
     * @param arr the array
     */
    public void shuffle(@NonNull int[] arr) {
        shuffle(arr, ThreadLocalRandom.current());
    }

    /**
     * Shuffles the elements of the given array using the provided {@link Random} to generate random numbers.
     *
     * @param arr    the array
     * @param random the {@link Random} to use
     */
    public void shuffle(@NonNull int[] arr, @NonNull Random random) {
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
    public void shuffle(@NonNull int[] arr, @NonNull IntUnaryOperator random) {
        for (int i = 0, length = arr.length; i < length; i++) {
            swap(arr, i, random.applyAsInt(length));
        }
    }

    /**
     * Shuffles the elements of the given array.
     *
     * @param arr the array
     */
    public void shuffle(@NonNull long[] arr) {
        shuffle(arr, ThreadLocalRandom.current());
    }

    /**
     * Shuffles the elements of the given array using the provided {@link Random} to generate random numbers.
     *
     * @param arr    the array
     * @param random the {@link Random} to use
     */
    public void shuffle(@NonNull long[] arr, @NonNull Random random) {
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
    public void shuffle(@NonNull long[] arr, @NonNull IntUnaryOperator random) {
        for (int i = 0, length = arr.length; i < length; i++) {
            swap(arr, i, random.applyAsInt(length));
        }
    }

    /**
     * Shuffles the elements of the given array.
     *
     * @param arr the array
     */
    public void shuffle(@NonNull float[] arr) {
        shuffle(arr, ThreadLocalRandom.current());
    }

    /**
     * Shuffles the elements of the given array using the provided {@link Random} to generate random numbers.
     *
     * @param arr    the array
     * @param random the {@link Random} to use
     */
    public void shuffle(@NonNull float[] arr, @NonNull Random random) {
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
    public void shuffle(@NonNull float[] arr, @NonNull IntUnaryOperator random) {
        for (int i = 0, length = arr.length; i < length; i++) {
            swap(arr, i, random.applyAsInt(length));
        }
    }

    /**
     * Shuffles the elements of the given array.
     *
     * @param arr the array
     */
    public void shuffle(@NonNull double[] arr) {
        shuffle(arr, ThreadLocalRandom.current());
    }

    /**
     * Shuffles the elements of the given array using the provided {@link Random} to generate random numbers.
     *
     * @param arr    the array
     * @param random the {@link Random} to use
     */
    public void shuffle(@NonNull double[] arr, @NonNull Random random) {
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
    public void shuffle(@NonNull double[] arr, @NonNull IntUnaryOperator random) {
        for (int i = 0, length = arr.length; i < length; i++) {
            swap(arr, i, random.applyAsInt(length));
        }
    }

    /**
     * Shuffles the elements of the given array.
     *
     * @param arr the array
     */
    public <T> void shuffle(@NonNull T[] arr) {
        shuffle(arr, ThreadLocalRandom.current());
    }

    /**
     * Shuffles the elements of the given array using the provided {@link Random} to generate random numbers.
     *
     * @param arr    the array
     * @param random the {@link Random} to use
     */
    public <T> void shuffle(@NonNull T[] arr, @NonNull Random random) {
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
    public <T> void shuffle(@NonNull T[] arr, @NonNull IntUnaryOperator random) {
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
    public boolean[] filled(int length, boolean value) {
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
    public byte[] filled(int length, byte value) {
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
    public short[] filled(int length, short value) {
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
    public char[] filled(int length, char value) {
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
    public int[] filled(int length, int value) {
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
    public long[] filled(int length, long value) {
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
    public float[] filled(int length, float value) {
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
    public double[] filled(int length, double value) {
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
    public <T> T[] filled(int length, @NonNull Class<T> componentType, T value) {
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
    public <T> T[] filled(int length, @NonNull IntFunction<T[]> arrayCreator, T value) {
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
    public <T> T[] filledUnchecked(int length, @NonNull T value) {
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
    public boolean[] filledFrom(int length, @NonNull BooleanSupplier supplier) {
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
    public int[] filledFrom(int length, @NonNull IntSupplier supplier) {
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
    public long[] filledFrom(int length, @NonNull LongSupplier supplier) {
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
    public double[] filledFrom(int length, @NonNull DoubleSupplier supplier) {
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
    public <T> T[] filledFrom(int length, @NonNull Class<T> componentType, @NonNull Supplier<? extends T> supplier) {
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
    public <T> T[] filledFrom(int length, @NonNull IntFunction<T[]> arrayCreator, @NonNull Supplier<? extends T> supplier) {
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
    public boolean[] filledBy(int length, @NonNull IntPredicate function) {
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
    public int[] filledBy(int length, @NonNull IntUnaryOperator function) {
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
    public long[] filledBy(int length, @NonNull IntToLongFunction function) {
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
    public double[] filledBy(int length, @NonNull IntToDoubleFunction function) {
        double[] arr = PUnsafe.allocateUninitializedDoubleArray(length);
        for (int i = 0; i < length; i++) {
            arr[i] = function.applyAsDouble(i);
        }
        return arr;
    }

    /**
     * Allocates a new array of the given length with all elements set to values returned by the given {@link IntFunction} for the element index.
     *
     * @param length   the length of the new array
     * @param componentType the erased type of {@link T}
     * @param function a {@link IntFunction} which computes the default value for each element in the array from the element index
     * @return the new array
     */
    public <T> T[] filledBy(int length, @NonNull Class<T> componentType, @NonNull IntFunction<? extends T> function) {
        T[] arr = uncheckedCast(Array.newInstance(componentType, length));
        for (int i = 0; i < length; i++) {
            arr[i] = function.apply(i);
        }
        return arr;
    }

    /**
     * Allocates a new array of the given length with all elements set to values returned by the given {@link IntFunction} for the element index.
     *
     * @param length   the length of the new array
     * @param arrayCreator a function to use for allocating the new array. Assumed to be a lambda in the form {@code T[]::new}.
     * @param function a {@link IntFunction} which computes the default value for each element in the array from the element index
     * @return the new array
     */
    public <T> T[] filledBy(int length, @NonNull IntFunction<T[]> arrayCreator, @NonNull IntFunction<? extends T> function) {
        T[] arr = arrayCreator.apply(length);
        for (int i = 0; i < length; i++) {
            arr[i] = function.apply(i);
        }
        return arr;
    }

    public <T> Object[] toObjects(@NonNull T[] src) {
        if (src.getClass() == Object[].class) {
            return src;
        }
        Object[] dst = new Object[src.length];
        System.arraycopy(src, 0, dst, 0, src.length);
        return dst;
    }

    public int indexOf(@NonNull byte[] arr, byte val) {
        for (int i = 0, length = arr.length; i < length; i++) {
            if (arr[i] == val) {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(@NonNull byte[] arr, byte val, int from, int to) {
        PValidation.checkRange(arr.length, from, to);
        for (; from < to; from++) {
            if (arr[from] == val) {
                return from;
            }
        }
        return -1;
    }

    public int indexOf(@NonNull short[] arr, short val) {
        for (int i = 0, length = arr.length; i < length; i++) {
            if (arr[i] == val) {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(@NonNull short[] arr, short val, int from, int to) {
        PValidation.checkRange(arr.length, from, to);
        for (; from < to; from++) {
            if (arr[from] == val) {
                return from;
            }
        }
        return -1;
    }

    public int indexOf(@NonNull char[] arr, char val) {
        for (int i = 0, length = arr.length; i < length; i++) {
            if (arr[i] == val) {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(@NonNull char[] arr, char val, int from, int to) {
        PValidation.checkRange(arr.length, from, to);
        for (; from < to; from++) {
            if (arr[from] == val) {
                return from;
            }
        }
        return -1;
    }

    public int indexOf(@NonNull int[] arr, int val) {
        for (int i = 0, length = arr.length; i < length; i++) {
            if (arr[i] == val) {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(@NonNull int[] arr, int val, int from, int to) {
        PValidation.checkRange(arr.length, from, to);
        for (; from < to; from++) {
            if (arr[from] == val) {
                return from;
            }
        }
        return -1;
    }

    public int indexOf(@NonNull long[] arr, long val) {
        for (int i = 0, length = arr.length; i < length; i++) {
            if (arr[i] == val) {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(@NonNull long[] arr, long val, int from, int to) {
        PValidation.checkRange(arr.length, from, to);
        for (; from < to; from++) {
            if (arr[from] == val) {
                return from;
            }
        }
        return -1;
    }

    public int indexOf(@NonNull float[] arr, float val) {
        for (int i = 0, length = arr.length; i < length; i++) {
            if (arr[i] == val) {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(@NonNull float[] arr, float val, int from, int to) {
        PValidation.checkRange(arr.length, from, to);
        for (; from < to; from++) {
            if (arr[from] == val) {
                return from;
            }
        }
        return -1;
    }

    public int indexOf(@NonNull double[] arr, double val) {
        for (int i = 0, length = arr.length; i < length; i++) {
            if (arr[i] == val) {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(@NonNull double[] arr, double val, int from, int to) {
        PValidation.checkRange(arr.length, from, to);
        for (; from < to; from++) {
            if (arr[from] == val) {
                return from;
            }
        }
        return -1;
    }

    public <T> int indexOf(@NonNull T[] arr, @NonNull T val) {
        for (int i = 0, length = arr.length; i < length; i++) {
            if (arr[i] == val) {
                return i;
            }
        }
        return -1;
    }

    public byte max(@NonNull byte[] arr) {
        if (arr.length == 0) {
            throw new IllegalArgumentException("Array may not be empty!");
        }
        byte val = Byte.MIN_VALUE;
        for (int i = 0, length = arr.length; i < length; i++) {
            if (arr[i] > val) {
                val = arr[i];
            }
        }
        return val;
    }

    public short max(@NonNull short[] arr) {
        if (arr.length == 0) {
            throw new IllegalArgumentException("Array may not be empty!");
        }
        short val = Short.MIN_VALUE;
        for (int i = 0, length = arr.length; i < length; i++) {
            if (arr[i] > val) {
                val = arr[i];
            }
        }
        return val;
    }

    public char max(@NonNull char[] arr) {
        if (arr.length == 0) {
            throw new IllegalArgumentException("Array may not be empty!");
        }
        char val = Character.MIN_VALUE;
        for (int i = 0, length = arr.length; i < length; i++) {
            if (arr[i] > val) {
                val = arr[i];
            }
        }
        return val;
    }

    public int max(@NonNull int[] arr) {
        if (arr.length == 0) {
            throw new IllegalArgumentException("Array may not be empty!");
        }
        int val = Integer.MIN_VALUE;
        for (int i = 0, length = arr.length; i < length; i++) {
            if (arr[i] > val) {
                val = arr[i];
            }
        }
        return val;
    }

    public long max(@NonNull long[] arr) {
        if (arr.length == 0) {
            throw new IllegalArgumentException("Array may not be empty!");
        }
        long val = Long.MIN_VALUE;
        for (int i = 0, length = arr.length; i < length; i++) {
            if (arr[i] > val) {
                val = arr[i];
            }
        }
        return val;
    }

    public float max(@NonNull float[] arr) {
        if (arr.length == 0) {
            throw new IllegalArgumentException("Array may not be empty!");
        }
        float val = Float.MIN_VALUE;
        for (int i = 0, length = arr.length; i < length; i++) {
            if (arr[i] > val) {
                val = arr[i];
            }
        }
        return val;
    }

    public double max(@NonNull double[] arr) {
        if (arr.length == 0) {
            throw new IllegalArgumentException("Array may not be empty!");
        }
        double val = Double.MIN_VALUE;
        for (int i = 0, length = arr.length; i < length; i++) {
            if (arr[i] > val) {
                val = arr[i];
            }
        }
        return val;
    }

    public byte min(@NonNull byte[] arr) {
        if (arr.length == 0) {
            throw new IllegalArgumentException("Array may not be empty!");
        }
        byte val = Byte.MAX_VALUE;
        for (int i = 0, length = arr.length; i < length; i++) {
            if (arr[i] < val) {
                val = arr[i];
            }
        }
        return val;
    }

    public short min(@NonNull short[] arr) {
        if (arr.length == 0) {
            throw new IllegalArgumentException("Array may not be empty!");
        }
        short val = Short.MAX_VALUE;
        for (int i = 0, length = arr.length; i < length; i++) {
            if (arr[i] < val) {
                val = arr[i];
            }
        }
        return val;
    }

    public char min(@NonNull char[] arr) {
        if (arr.length == 0) {
            throw new IllegalArgumentException("Array may not be empty!");
        }
        char val = Character.MAX_VALUE;
        for (int i = 0, length = arr.length; i < length; i++) {
            if (arr[i] < val) {
                val = arr[i];
            }
        }
        return val;
    }

    public int min(@NonNull int[] arr) {
        if (arr.length == 0) {
            throw new IllegalArgumentException("Array may not be empty!");
        }
        int val = Integer.MAX_VALUE;
        for (int i = 0, length = arr.length; i < length; i++) {
            if (arr[i] < val) {
                val = arr[i];
            }
        }
        return val;
    }

    public long min(@NonNull long[] arr) {
        if (arr.length == 0) {
            throw new IllegalArgumentException("Array may not be empty!");
        }
        long val = Long.MAX_VALUE;
        for (int i = 0, length = arr.length; i < length; i++) {
            if (arr[i] < val) {
                val = arr[i];
            }
        }
        return val;
    }

    public float min(@NonNull float[] arr) {
        if (arr.length == 0) {
            throw new IllegalArgumentException("Array may not be empty!");
        }
        float val = Float.MAX_VALUE;
        for (int i = 0, length = arr.length; i < length; i++) {
            if (arr[i] < val) {
                val = arr[i];
            }
        }
        return val;
    }

    public double min(@NonNull double[] arr) {
        if (arr.length == 0) {
            throw new IllegalArgumentException("Array may not be empty!");
        }
        double val = Double.MAX_VALUE;
        for (int i = 0, length = arr.length; i < length; i++) {
            if (arr[i] < val) {
                val = arr[i];
            }
        }
        return val;
    }

    public void swap(@NonNull byte[] arr, int i1, int i2) {
        byte val = arr[i1];
        arr[i1] = arr[i2];
        arr[i2] = val;
    }

    public void swap(@NonNull short[] arr, int i1, int i2) {
        short val = arr[i1];
        arr[i1] = arr[i2];
        arr[i2] = val;
    }

    public void swap(@NonNull char[] arr, int i1, int i2) {
        char val = arr[i1];
        arr[i1] = arr[i2];
        arr[i2] = val;
    }

    public void swap(@NonNull int[] arr, int i1, int i2) {
        int val = arr[i1];
        arr[i1] = arr[i2];
        arr[i2] = val;
    }

    public void swap(@NonNull long[] arr, int i1, int i2) {
        long val = arr[i1];
        arr[i1] = arr[i2];
        arr[i2] = val;
    }

    public void swap(@NonNull float[] arr, int i1, int i2) {
        float val = arr[i1];
        arr[i1] = arr[i2];
        arr[i2] = val;
    }

    public void swap(@NonNull double[] arr, int i1, int i2) {
        double val = arr[i1];
        arr[i1] = arr[i2];
        arr[i2] = val;
    }

    public <T> void swap(@NonNull T[] arr, int i1, int i2) {
        T val = arr[i1];
        arr[i1] = arr[i2];
        arr[i2] = val;
    }
}
