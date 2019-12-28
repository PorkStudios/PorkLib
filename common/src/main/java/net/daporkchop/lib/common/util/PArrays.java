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

package net.daporkchop.lib.common.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;
import java.util.function.LongSupplier;
import java.util.function.LongUnaryOperator;
import java.util.function.Supplier;

/**
 * Utilities for dealing with arrays.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PArrays {
    public void shuffle(@NonNull byte[] arr) {
        shuffle(arr, ThreadLocalRandom.current());
    }

    public void shuffle(@NonNull byte[] arr, @NonNull Random random) {
        for (int i = arr.length - 1; i >= 0; i--) {
            int j = random.nextInt(arr.length);
            byte curr = arr[i];
            arr[i] = arr[j];
            arr[j] = curr;
        }
    }

    public void shuffle(@NonNull short[] arr) {
        shuffle(arr, ThreadLocalRandom.current());
    }

    public void shuffle(@NonNull short[] arr, @NonNull Random random) {
        for (int i = arr.length - 1; i >= 0; i--) {
            int j = random.nextInt(arr.length);
            short curr = arr[i];
            arr[i] = arr[j];
            arr[j] = curr;
        }
    }

    public void shuffle(@NonNull char[] arr) {
        shuffle(arr, ThreadLocalRandom.current());
    }

    public void shuffle(@NonNull char[] arr, @NonNull Random random) {
        for (int i = arr.length - 1; i >= 0; i--) {
            int j = random.nextInt(arr.length);
            char curr = arr[i];
            arr[i] = arr[j];
            arr[j] = curr;
        }
    }

    public void shuffle(@NonNull int[] arr) {
        shuffle(arr, ThreadLocalRandom.current());
    }

    public void shuffle(@NonNull int[] arr, @NonNull Random random) {
        for (int i = arr.length - 1; i >= 0; i--) {
            int j = random.nextInt(arr.length);
            int curr = arr[i];
            arr[i] = arr[j];
            arr[j] = curr;
        }
    }

    public void shuffle(@NonNull long[] arr) {
        shuffle(arr, ThreadLocalRandom.current());
    }

    public void shuffle(@NonNull long[] arr, @NonNull Random random) {
        for (int i = arr.length - 1; i >= 0; i--) {
            int j = random.nextInt(arr.length);
            long curr = arr[i];
            arr[i] = arr[j];
            arr[j] = curr;
        }
    }

    public void shuffle(@NonNull float[] arr) {
        shuffle(arr, ThreadLocalRandom.current());
    }

    public void shuffle(@NonNull float[] arr, @NonNull Random random) {
        for (int i = arr.length - 1; i >= 0; i--) {
            int j = random.nextInt(arr.length);
            float curr = arr[i];
            arr[i] = arr[j];
            arr[j] = curr;
        }
    }

    public void shuffle(@NonNull double[] arr) {
        shuffle(arr, ThreadLocalRandom.current());
    }

    public void shuffle(@NonNull double[] arr, @NonNull Random random) {
        for (int i = arr.length - 1; i >= 0; i--) {
            int j = random.nextInt(arr.length);
            double curr = arr[i];
            arr[i] = arr[j];
            arr[j] = curr;
        }
    }

    public void shuffle(@NonNull Object[] arr) {
        shuffle(arr, ThreadLocalRandom.current());
    }

    public void shuffle(@NonNull Object[] arr, @NonNull Random random) {
        for (int i = arr.length - 1; i >= 0; i--) {
            int j = random.nextInt(arr.length);
            Object curr = arr[i];
            arr[i] = arr[j];
            arr[j] = curr;
        }
    }

    public int[] filled(int size, @NonNull IntSupplier supplier)   {
        int[] arr = new int[size];
        for (int i = arr.length - 1; i >= 0; i--)   {
            arr[i] = supplier.getAsInt();
        }
        return arr;
    }

    public int[] filled(int size, @NonNull IntUnaryOperator supplier)   {
        int[] arr = new int[size];
        for (int i = arr.length - 1; i >= 0; i--)   {
            arr[i] = supplier.applyAsInt(i);
        }
        return arr;
    }

    public long[] filled(int size, @NonNull LongSupplier supplier)   {
        long[] arr = new long[size];
        for (int i = arr.length - 1; i >= 0; i--)   {
            arr[i] = supplier.getAsLong();
        }
        return arr;
    }

    public long[] filled(int size, @NonNull LongUnaryOperator supplier)   {
        long[] arr = new long[size];
        for (int i = arr.length - 1; i >= 0; i--)   {
            arr[i] = supplier.applyAsLong(i);
        }
        return arr;
    }

    public <T> T[] filled(int size, @NonNull IntFunction<T[]> arrayCreator, @NonNull Supplier<T> supplier)   {
        T[] arr = arrayCreator.apply(size);
        for (int i = arr.length - 1; i >= 0; i--)   {
            arr[i] = supplier.get();
        }
        return arr;
    }

    public <T> T[] filled(int size, @NonNull IntFunction<T[]> arrayCreator, @NonNull IntFunction<T> supplier)   {
        T[] arr = arrayCreator.apply(size);
        for (int i = arr.length - 1; i >= 0; i--)   {
            arr[i] = supplier.apply(i);
        }
        return arr;
    }

    public <T> void fill(@NonNull T[] arr, @NonNull Supplier<T> supplier)   {
        for (int i = arr.length - 1; i >= 0; i--)   {
            arr[i] = supplier.get();
        }
    }

    public <T> void fill(@NonNull T[] arr, @NonNull T value)   {
        for (int i = arr.length - 1; i >= 0; i--)   {
            arr[i] = value;
        }
    }

    public <T> Object[] toObjects(@NonNull T[] src) {
        if (src.getClass() == Object[].class)    {
            return src;
        }
        Object[] dst = new Object[src.length];
        System.arraycopy(src, 0, dst, 0, src.length);
        return dst;
    }

    public int indexOf(@NonNull byte[] arr, byte val)    {
        for (int i = arr.length - 1; i >= 0; i--)   {
            if (arr[i] == val)  {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(@NonNull short[] arr, short val)    {
        for (int i = arr.length - 1; i >= 0; i--)   {
            if (arr[i] == val)  {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(@NonNull char[] arr, char val)    {
        for (int i = arr.length - 1; i >= 0; i--)   {
            if (arr[i] == val)  {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(@NonNull int[] arr, int val)    {
        for (int i = arr.length - 1; i >= 0; i--)   {
            if (arr[i] == val)  {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(@NonNull long[] arr, long val)    {
        for (int i = arr.length - 1; i >= 0; i--)   {
            if (arr[i] == val)  {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(@NonNull float[] arr, float val)    {
        for (int i = arr.length - 1; i >= 0; i--)   {
            if (arr[i] == val)  {
                return i;
            }
        }
        return -1;
    }

    public int indexOf(@NonNull double[] arr, double val)    {
        for (int i = arr.length - 1; i >= 0; i--)   {
            if (arr[i] == val)  {
                return i;
            }
        }
        return -1;
    }

    public <T> int indexOf(@NonNull T[] arr, @NonNull T val)    {
        for (int i = arr.length - 1; i >= 0; i--)   {
            if (arr[i] == val)  {
                return i;
            }
        }
        return -1;
    }

    public byte max(@NonNull byte[] arr)    {
        if (arr.length == 0)    {
            throw new IllegalArgumentException("Array may not be empty!");
        }
        byte val = Byte.MIN_VALUE;
        for (int i = arr.length - 1; i >= 0; i--)   {
            if (arr[i] > val)   {
                val = arr[i];
            }
        }
        return val;
    }

    public short max(@NonNull short[] arr)    {
        if (arr.length == 0)    {
            throw new IllegalArgumentException("Array may not be empty!");
        }
        short val = Short.MIN_VALUE;
        for (int i = arr.length - 1; i >= 0; i--)   {
            if (arr[i] > val)   {
                val = arr[i];
            }
        }
        return val;
    }

    public char max(@NonNull char[] arr)    {
        if (arr.length == 0)    {
            throw new IllegalArgumentException("Array may not be empty!");
        }
        char val = Character.MIN_VALUE;
        for (int i = arr.length - 1; i >= 0; i--)   {
            if (arr[i] > val)   {
                val = arr[i];
            }
        }
        return val;
    }

    public int max(@NonNull int[] arr)    {
        if (arr.length == 0)    {
            throw new IllegalArgumentException("Array may not be empty!");
        }
        int val = Integer.MIN_VALUE;
        for (int i = arr.length - 1; i >= 0; i--)   {
            if (arr[i] > val)   {
                val = arr[i];
            }
        }
        return val;
    }

    public long max(@NonNull long[] arr)    {
        if (arr.length == 0)    {
            throw new IllegalArgumentException("Array may not be empty!");
        }
        long val = Long.MIN_VALUE;
        for (int i = arr.length - 1; i >= 0; i--)   {
            if (arr[i] > val)   {
                val = arr[i];
            }
        }
        return val;
    }

    public float max(@NonNull float[] arr)    {
        if (arr.length == 0)    {
            throw new IllegalArgumentException("Array may not be empty!");
        }
        float val = Float.MIN_VALUE;
        for (int i = arr.length - 1; i >= 0; i--)   {
            if (arr[i] > val)   {
                val = arr[i];
            }
        }
        return val;
    }

    public double max(@NonNull double[] arr)    {
        if (arr.length == 0)    {
            throw new IllegalArgumentException("Array may not be empty!");
        }
        double val = Double.MIN_VALUE;
        for (int i = arr.length - 1; i >= 0; i--)   {
            if (arr[i] > val)   {
                val = arr[i];
            }
        }
        return val;
    }

    public byte min(@NonNull byte[] arr)    {
        if (arr.length == 0)    {
            throw new IllegalArgumentException("Array may not be empty!");
        }
        byte val = Byte.MAX_VALUE;
        for (int i = arr.length - 1; i >= 0; i--)   {
            if (arr[i] < val)   {
                val = arr[i];
            }
        }
        return val;
    }

    public short min(@NonNull short[] arr)    {
        if (arr.length == 0)    {
            throw new IllegalArgumentException("Array may not be empty!");
        }
        short val = Short.MAX_VALUE;
        for (int i = arr.length - 1; i >= 0; i--)   {
            if (arr[i] < val)   {
                val = arr[i];
            }
        }
        return val;
    }

    public char min(@NonNull char[] arr)    {
        if (arr.length == 0)    {
            throw new IllegalArgumentException("Array may not be empty!");
        }
        char val = Character.MAX_VALUE;
        for (int i = arr.length - 1; i >= 0; i--)   {
            if (arr[i] < val)   {
                val = arr[i];
            }
        }
        return val;
    }

    public int min(@NonNull int[] arr)    {
        if (arr.length == 0)    {
            throw new IllegalArgumentException("Array may not be empty!");
        }
        int val = Integer.MAX_VALUE;
        for (int i = arr.length - 1; i >= 0; i--)   {
            if (arr[i] < val)   {
                val = arr[i];
            }
        }
        return val;
    }

    public long min(@NonNull long[] arr)    {
        if (arr.length == 0)    {
            throw new IllegalArgumentException("Array may not be empty!");
        }
        long val = Long.MAX_VALUE;
        for (int i = arr.length - 1; i >= 0; i--)   {
            if (arr[i] < val)   {
                val = arr[i];
            }
        }
        return val;
    }

    public float min(@NonNull float[] arr)    {
        if (arr.length == 0)    {
            throw new IllegalArgumentException("Array may not be empty!");
        }
        float val = Float.MAX_VALUE;
        for (int i = arr.length - 1; i >= 0; i--)   {
            if (arr[i] < val)   {
                val = arr[i];
            }
        }
        return val;
    }

    public double min(@NonNull double[] arr)    {
        if (arr.length == 0)    {
            throw new IllegalArgumentException("Array may not be empty!");
        }
        double val = Double.MAX_VALUE;
        for (int i = arr.length - 1; i >= 0; i--)   {
            if (arr[i] < val)   {
                val = arr[i];
            }
        }
        return val;
    }
}
