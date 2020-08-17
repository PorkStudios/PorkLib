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

package net.daporkchop.lib.unsafe;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.security.ProtectionDomain;

/**
 * Wrapper class around {@link Unsafe}.
 * <p>
 * Really serves very little purpose except to avoid the otherwise unavoidable "Internal API" warnings at compile-time
 * caused by referencing anything in {@link sun}.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class PUnsafe {
    static {
        Unsafe unsafe = null;
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (NoSuchFieldException
                | IllegalAccessException e) {
            throw new AssertionError("Unable to obtain instance of sun.misc.Unsafe", e);
        } finally {
            if ((UNSAFE = unsafe) == null)  {
                throw new AssertionError("Unable to obtain instance of sun.misc.Unsafe");
            }
        }
    }

    /**
     * A constant reference to {@link Unsafe}
     */
    public static final Unsafe UNSAFE;

    /**
     * The value of {@code arrayBaseOffset(boolean[].class)}
     */
    public static final long ARRAY_BOOLEAN_BASE_OFFSET = UNSAFE.arrayBaseOffset(boolean[].class);

    //some constants
    /**
     * The value of {@code arrayBaseOffset(byte[].class)}
     */
    public static final long ARRAY_BYTE_BASE_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);

    /**
     * The value of {@code arrayBaseOffset(short[].class)}
     */
    public static final long ARRAY_SHORT_BASE_OFFSET = UNSAFE.arrayBaseOffset(short[].class);

    /**
     * The value of {@code arrayBaseOffset(char[].class)}
     */
    public static final long ARRAY_CHAR_BASE_OFFSET = UNSAFE.arrayBaseOffset(char[].class);

    /**
     * The value of {@code arrayBaseOffset(int[].class)}
     */
    public static final long ARRAY_INT_BASE_OFFSET = UNSAFE.arrayBaseOffset(int[].class);

    /**
     * The value of {@code arrayBaseOffset(long[].class)}
     */
    public static final long ARRAY_LONG_BASE_OFFSET = UNSAFE.arrayBaseOffset(long[].class);

    /**
     * The value of {@code arrayBaseOffset(float[].class)}
     */
    public static final long ARRAY_FLOAT_BASE_OFFSET = UNSAFE.arrayBaseOffset(float[].class);

    /**
     * The value of {@code arrayBaseOffset(double[].class)}
     */
    public static final long ARRAY_DOUBLE_BASE_OFFSET = UNSAFE.arrayBaseOffset(double[].class);

    /**
     * The value of {@code arrayBaseOffset(Object[].class)}
     */
    public static final long ARRAY_OBJECT_BASE_OFFSET = UNSAFE.arrayBaseOffset(Object[].class);

    /**
     * The value of {@code arrayIndexScale(boolean[].class)}
     */
    public static final long ARRAY_BOOLEAN_INDEX_SCALE = UNSAFE.arrayIndexScale(boolean[].class);

    /**
     * The value of {@code arrayIndexScale(byte[].class)}
     */
    public static final long ARRAY_BYTE_INDEX_SCALE = UNSAFE.arrayIndexScale(byte[].class);

    /**
     * The value of {@code arrayIndexScale(short[].class)}
     */
    public static final long ARRAY_SHORT_INDEX_SCALE = UNSAFE.arrayIndexScale(short[].class);

    /**
     * The value of {@code arrayIndexScale(char[].class)}
     */
    public static final long ARRAY_CHAR_INDEX_SCALE = UNSAFE.arrayIndexScale(char[].class);

    /**
     * The value of {@code arrayIndexScale(int[].class)}
     */
    public static final long ARRAY_INT_INDEX_SCALE = UNSAFE.arrayIndexScale(int[].class);

    /**
     * The value of {@code arrayIndexScale(long[].class)}
     */
    public static final long ARRAY_LONG_INDEX_SCALE = UNSAFE.arrayIndexScale(long[].class);

    /**
     * The value of {@code arrayIndexScale(float[].class)}
     */
    public static final long ARRAY_FLOAT_INDEX_SCALE = UNSAFE.arrayIndexScale(float[].class);

    /**
     * The value of {@code arrayIndexScale(double[].class)}
     */
    public static final long ARRAY_DOUBLE_INDEX_SCALE = UNSAFE.arrayIndexScale(double[].class);

    /**
     * The value of {@code arrayIndexScale(Object[].class)}
     */
    public static final long ARRAY_OBJECT_INDEX_SCALE = UNSAFE.arrayIndexScale(Object[].class);

    /**
     * The value of {@link Unsafe#addressSize()}.
     */
    public static final int ADDRESS_SIZE = UNSAFE.addressSize();

    /**
     * The value of {@link Unsafe#pageSize()}.
     */
    public static final int PAGE_SIZE = UNSAFE.pageSize();

    //methods
    public static <T> T getObject(Object o, long pos) {
        @SuppressWarnings("unchecked")
        T value = (T) UNSAFE.getObject(o, pos);
        return value;
    }

    public static void putObject(Object o, long pos, Object val) {
        UNSAFE.putObject(o, pos, val);
    }

    public static boolean getBoolean(Object o, long pos) {
        return UNSAFE.getBoolean(o, pos);
    }

    public static void putBoolean(Object o, long pos, boolean val) {
        UNSAFE.putBoolean(o, pos, val);
    }

    public static byte getByte(Object o, long pos) {
        return UNSAFE.getByte(o, pos);
    }

    public static void putByte(Object o, long pos, byte val) {
        UNSAFE.putByte(o, pos, val);
    }

    public static short getShort(Object o, long pos) {
        return UNSAFE.getShort(o, pos);
    }

    public static void putShort(Object o, long pos, short val) {
        UNSAFE.putShort(o, pos, val);
    }

    public static char getChar(Object o, long pos) {
        return UNSAFE.getChar(o, pos);
    }

    public static void putChar(Object o, long pos, char val) {
        UNSAFE.putChar(o, pos, val);
    }

    public static int getInt(Object o, long pos) {
        return UNSAFE.getInt(o, pos);
    }

    public static void putInt(Object o, long pos, int val) {
        UNSAFE.putInt(o, pos, val);
    }

    public static long getLong(Object o, long pos) {
        return UNSAFE.getLong(o, pos);
    }

    public static void putLong(Object o, long pos, long val) {
        UNSAFE.putLong(o, pos, val);
    }

    public static float getFloat(Object o, long pos) {
        return UNSAFE.getFloat(o, pos);
    }

    public static void putFloat(Object o, long pos, float val) {
        UNSAFE.putFloat(o, pos, val);
    }

    public static double getDouble(Object o, long pos) {
        return UNSAFE.getDouble(o, pos);
    }

    public static void putDouble(Object o, long pos, double val) {
        UNSAFE.putDouble(o, pos, val);
    }

    public static int getInt(long pos) {
        return UNSAFE.getInt(pos);
    }

    public static void putInt(long pos, int val) {
        UNSAFE.putInt(pos, val);
    }

    public static byte getByte(long pos) {
        return UNSAFE.getByte(pos);
    }

    public static void putByte(long pos, byte val) {
        UNSAFE.putByte(pos, val);
    }

    public static short getShort(long pos) {
        return UNSAFE.getShort(pos);
    }

    public static void putShort(long pos, short val) {
        UNSAFE.putShort(pos, val);
    }

    public static char getChar(long pos) {
        return UNSAFE.getChar(pos);
    }

    public static void putChar(long pos, char val) {
        UNSAFE.putChar(pos, val);
    }

    public static long getLong(long pos) {
        return UNSAFE.getLong(pos);
    }

    public static void putLong(long pos, long val) {
        UNSAFE.putLong(pos, val);
    }

    public static float getFloat(long pos) {
        return UNSAFE.getFloat(pos);
    }

    public static void putFloat(long pos, float val) {
        UNSAFE.putFloat(pos, val);
    }

    public static double getDouble(long pos) {
        return UNSAFE.getDouble(pos);
    }

    public static void putDouble(long pos, double val) {
        UNSAFE.putDouble(pos, val);
    }

    public static long getAddress(long pos) {
        return UNSAFE.getAddress(pos);
    }

    public static void putAddress(long pos, long val) {
        UNSAFE.putAddress(pos, val);
    }

    public static long allocateMemory(long size) {
        return UNSAFE.allocateMemory(size);
    }

    public static long allocateMemory(@NonNull Object cleanerTarget, long size) {
        long offset = UNSAFE.allocateMemory(size);
        PCleaner.cleaner(cleanerTarget, offset);
        return offset;
    }

    public static long reallocateMemory(long oldAddress, long size) {
        return UNSAFE.reallocateMemory(oldAddress, size);
    }

    public static void setMemory(Object o, long pos, long length, byte val) {
        if (o == null)  {
            //get a speed boost by not using any objects as references when the memory is off-heap
            UNSAFE.setMemory(pos, length, val);
        } else {
            UNSAFE.setMemory(o, pos, length, val);
        }
    }

    public static void setMemory(long pos, long length, byte val) {
        UNSAFE.setMemory(null, pos, length, val);
    }

    public static void copyMemory(Object src, long srcOffset, Object dst, long dstOffset, long length) {
        if (src == null && dst == null) {
            //get a speed boost by not using any objects as references when both src and dst are off-heap
            UNSAFE.copyMemory(srcOffset, dstOffset, length);
        } else {
            UNSAFE.copyMemory(src, srcOffset, dst, dstOffset, length);
        }
    }

    public static void copyMemory(long src, long dst, long length) {
        UNSAFE.copyMemory(null, src, null, dst, length);
    }

    public static void freeMemory(long address) {
        UNSAFE.freeMemory(address);
    }

    public static long staticFieldOffset(Field field) {
        return UNSAFE.staticFieldOffset(field);
    }

    public static long objectFieldOffset(Field field) {
        return UNSAFE.objectFieldOffset(field);
    }

    public static Object staticFieldBase(Field field) {
        return UNSAFE.staticFieldBase(field);
    }

    public static boolean shouldBeInitialized(Class<?> clazz) {
        return UNSAFE.shouldBeInitialized(clazz);
    }

    public static void ensureClassInitialized(Class<?> clazz) {
        UNSAFE.ensureClassInitialized(clazz);
    }

    public static int arrayBaseOffset(Class<?> clazz) {
        return UNSAFE.arrayBaseOffset(clazz);
    }

    public static int arrayIndexScale(Class<?> clazz) {
        return UNSAFE.arrayIndexScale(clazz);
    }

    public static int addressSize() {
        return UNSAFE.addressSize();
    }

    public static int pageSize() {
        return UNSAFE.pageSize();
    }

    public static Class<?> defineClass(String name, byte[] classBytes, int off, int len, ClassLoader srcLoader, ProtectionDomain domain) {
        return UNSAFE.defineClass(name, classBytes, off, len, srcLoader, domain);
    }

    public static Class<?> defineAnonymousClass(Class<?> hostClass, byte[] data, Object[] cpPatches) {
        return UNSAFE.defineAnonymousClass(hostClass, data, cpPatches);
    }

    @SuppressWarnings("unchecked")
    public static <T> T allocateInstance(Class<T> clazz) {
        try {
            return (T) UNSAFE.allocateInstance(clazz);
        } catch (InstantiationException e) {
            UNSAFE.throwException(e);
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("deprecation")
    public static void monitorEnter(Object o)   {
        UNSAFE.monitorEnter(o);
    }

    @SuppressWarnings("deprecation")
    public static void monitorExit(Object o)    {
        UNSAFE.monitorExit(o);
    }

    @SuppressWarnings("deprecation")
    public static boolean tryMonitorEnter(Object o) {
        return UNSAFE.tryMonitorEnter(o);
    }

    public static void throwException(Throwable t) {
        UNSAFE.throwException(t);
    }

    public static boolean compareAndSwapObject(Object o, long pos, Object expected, Object newValue) {
        return UNSAFE.compareAndSwapObject(o, pos, expected, newValue);
    }

    public static boolean compareAndSwapInt(Object o, long pos, int expected, int newValue) {
        return UNSAFE.compareAndSwapInt(o, pos, expected, newValue);
    }

    public static boolean compareAndSwapLong(Object o, long pos, long expected, long newValue) {
        return UNSAFE.compareAndSwapLong(o, pos, expected, newValue);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getObjectVolatile(Object o, long pos) {
        return (T) UNSAFE.getObjectVolatile(o, pos);
    }

    public static void putObjectVolatile(Object o, long pos, Object val) {
        UNSAFE.putObjectVolatile(o, pos, val);
    }

    public static int getIntVolatile(Object o, long pos) {
        return UNSAFE.getIntVolatile(o, pos);
    }

    public static void putIntVolatile(Object o, long pos, int val) {
        UNSAFE.putIntVolatile(o, pos, val);
    }

    public static boolean getBooleanVolatile(Object o, long pos) {
        return UNSAFE.getBooleanVolatile(o, pos);
    }

    public static void putBooleanVolatile(Object o, long pos, boolean val) {
        UNSAFE.putBooleanVolatile(o, pos, val);
    }

    public static byte getByteVolatile(Object o, long pos) {
        return UNSAFE.getByteVolatile(o, pos);
    }

    public static void putByteVolatile(Object o, long pos, byte val) {
        UNSAFE.putByteVolatile(o, pos, val);
    }

    public static short getShortVolatile(Object o, long pos) {
        return UNSAFE.getShortVolatile(o, pos);
    }

    public static void putShortVolatile(Object o, long pos, short val) {
        UNSAFE.putShortVolatile(o, pos, val);
    }

    public static char getCharVolatile(Object o, long pos) {
        return UNSAFE.getCharVolatile(o, pos);
    }

    public static void putCharVolatile(Object o, long pos, char val) {
        UNSAFE.putCharVolatile(o, pos, val);
    }

    public static long getLongVolatile(Object o, long pos) {
        return UNSAFE.getLongVolatile(o, pos);
    }

    public static void putLongVolatile(Object o, long pos, long val) {
        UNSAFE.putLongVolatile(o, pos, val);
    }

    public static float getFloatVolatile(Object o, long pos) {
        return UNSAFE.getFloatVolatile(o, pos);
    }

    public static void putFloatVolatile(Object o, long pos, float val) {
        UNSAFE.putFloatVolatile(o, pos, val);
    }

    public static double getDoubleVolatile(Object o, long pos) {
        return UNSAFE.getDoubleVolatile(o, pos);
    }

    public static void putDoubleVolatile(Object o, long pos, double val) {
        UNSAFE.putDoubleVolatile(o, pos, val);
    }

    public static void putOrderedObject(Object o, long pos, Object val) {
        UNSAFE.putOrderedObject(o, pos, val);
    }

    public static void putOrderedInt(Object o, long pos, int val) {
        UNSAFE.putOrderedInt(o, pos, val);
    }

    public static void putOrderedLong(Object o, long pos, long val) {
        UNSAFE.putOrderedLong(o, pos, val);
    }

    public static void unpark(Object thread) {
        UNSAFE.unpark(thread);
    }

    public static void park(boolean absolute, long time) {
        UNSAFE.park(absolute, time);
    }

    public static int getLoadAverage(double[] loadavg, int nelems) {
        return UNSAFE.getLoadAverage(loadavg, nelems);
    }

    public static int getAndAddInt(Object o, long pos, int val) {
        return UNSAFE.getAndAddInt(o, pos, val);
    }

    public static long getAndAddLong(Object o, long pos, long val) {
        return UNSAFE.getAndAddLong(o, pos, val);
    }

    public static int getAndSetInt(Object o, long pos, int val) {
        return UNSAFE.getAndSetInt(o, pos, val);
    }

    public static long getAndSetLong(Object o, long pos, long val) {
        return UNSAFE.getAndSetLong(o, pos, val);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getAndSetObject(Object o, long pos, Object val) {
        return (T) UNSAFE.getAndSetObject(o, pos, val);
    }

    public static void loadFence() {
        UNSAFE.loadFence();
    }

    public static void storeFence() {
        UNSAFE.storeFence();
    }

    public static void fullFence() {
        UNSAFE.fullFence();
    }

    //
    //
    // custom methods
    //
    //
    
    public static long pork_getOffset(@NonNull Class clazz, @NonNull String fieldName) {
        try {
            return UNSAFE.objectFieldOffset(clazz.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            while ((clazz = clazz.getSuperclass()) != null)   {
                try {
                    return UNSAFE.objectFieldOffset(clazz.getDeclaredField(fieldName));
                } catch (NoSuchFieldException e1)   {
                    //ignore
                }
            }
            UNSAFE.throwException(e);
            throw new RuntimeException(e);
        }
    }

    public static UnsafeStaticField pork_getStaticField(@NonNull Class clazz, @NonNull String fieldName) {
        try {
            return new UnsafeStaticField(clazz, fieldName);
        } catch (NoSuchFieldException e) {
            UNSAFE.throwException(e);
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <V> V pork_swapObject(Object o, long pos, Object newValue) {
        Object v;
        do {
            v = UNSAFE.getObjectVolatile(o, pos);
        } while (!UNSAFE.compareAndSwapObject(o, pos, v, newValue));
        return (V) v;
    }

    public static boolean pork_checkSwapIfNonNull(Object o, long pos, Object newValue) {
        Object v;
        do {
            if ((v = UNSAFE.getObjectVolatile(o, pos)) == null) {
                return false;
            }
        } while (!UNSAFE.compareAndSwapObject(o, pos, v, newValue));
        return true;
    }

    @SuppressWarnings("unchecked")
    public static <V> V pork_swapIfNonNull(Object o, long pos, Object newValue) {
        Object v;
        do {
            if ((v = UNSAFE.getObjectVolatile(o, pos)) == null) {
                return null;
            }
        } while (!UNSAFE.compareAndSwapObject(o, pos, v, newValue));
        return (V) v;
    }

    public static long pork_directBufferAddress(Buffer buffer)   {
        return ((DirectBuffer) buffer).address();
    }

    public static Object pork_directBufferAttachment(Buffer buffer)   {
        return ((DirectBuffer) buffer).attachment();
    }

    public static Cleaner pork_directBufferCleaner(Buffer buffer)   {
        return ((DirectBuffer) buffer).cleaner();
    }

    public static void pork_releaseBuffer(Buffer buffer) {
        if (buffer instanceof DirectBuffer) {
            Cleaner cleaner = pork_directBufferCleaner(buffer);
            if (cleaner != null)    {
                cleaner.clean();
            }
        }
    }
    
    //
    //
    // custom array methods
    //
    //
    
    public static boolean getArrayVolatile(boolean[] a, int index)  {
        return getBooleanVolatile(a, ARRAY_BOOLEAN_BASE_OFFSET + index * ARRAY_BOOLEAN_INDEX_SCALE);
    }

    public static byte getArrayVolatile(byte[] a, int index)  {
        return getByteVolatile(a, ARRAY_BYTE_BASE_OFFSET + index * ARRAY_BYTE_INDEX_SCALE);
    }

    public static short getArrayVolatile(short[] a, int index)  {
        return getShortVolatile(a, ARRAY_SHORT_BASE_OFFSET + index * ARRAY_SHORT_INDEX_SCALE);
    }

    public static char getArrayVolatile(char[] a, int index)  {
        return getCharVolatile(a, ARRAY_CHAR_BASE_OFFSET + index * ARRAY_CHAR_INDEX_SCALE);
    }

    public static int getArrayVolatile(int[] a, int index)  {
        return getIntVolatile(a, ARRAY_INT_BASE_OFFSET + index * ARRAY_INT_INDEX_SCALE);
    }

    public static long getArrayVolatile(long[] a, int index)  {
        return getLongVolatile(a, ARRAY_LONG_BASE_OFFSET + index * ARRAY_LONG_INDEX_SCALE);
    }

    public static float getArrayVolatile(float[] a, int index)  {
        return getFloatVolatile(a, ARRAY_FLOAT_BASE_OFFSET + index * ARRAY_FLOAT_INDEX_SCALE);
    }

    public static double getArrayVolatile(double[] a, int index)  {
        return getDoubleVolatile(a, ARRAY_DOUBLE_BASE_OFFSET + index * ARRAY_DOUBLE_INDEX_SCALE);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getArrayVolatile(Object[] a, int index)  {
        return (T) getObjectVolatile(a, ARRAY_OBJECT_BASE_OFFSET + index * ARRAY_OBJECT_INDEX_SCALE);
    }

    public static void putArrayVolatile(boolean[] a, int index, boolean value)  {
        putBooleanVolatile(a, ARRAY_BOOLEAN_BASE_OFFSET + index * ARRAY_BOOLEAN_INDEX_SCALE, value);
    }

    public static void putArrayVolatile(byte[] a, int index, byte value)  {
        putByteVolatile(a, ARRAY_BYTE_BASE_OFFSET + index * ARRAY_BYTE_INDEX_SCALE, value);
    }

    public static void putArrayVolatile(short[] a, int index, short value)  {
        putShortVolatile(a, ARRAY_SHORT_BASE_OFFSET + index * ARRAY_SHORT_INDEX_SCALE, value);
    }

    public static void putArrayVolatile(char[] a, int index, char value)  {
        putCharVolatile(a, ARRAY_CHAR_BASE_OFFSET + index * ARRAY_CHAR_INDEX_SCALE, value);
    }

    public static void putArrayVolatile(int[] a, int index, int value)  {
        putIntVolatile(a, ARRAY_INT_BASE_OFFSET + index * ARRAY_INT_INDEX_SCALE, value);
    }

    public static void putArrayVolatile(long[] a, int index, long value)  {
        putLongVolatile(a, ARRAY_LONG_BASE_OFFSET + index * ARRAY_LONG_INDEX_SCALE, value);
    }

    public static void putArrayVolatile(float[] a, int index, float value)  {
        putFloatVolatile(a, ARRAY_FLOAT_BASE_OFFSET + index * ARRAY_FLOAT_INDEX_SCALE, value);
    }

    public static void putArrayVolatile(double[] a, int index, double value)  {
        putDoubleVolatile(a, ARRAY_DOUBLE_BASE_OFFSET + index * ARRAY_DOUBLE_INDEX_SCALE, value);
    }

    public static void putArrayVolatile(Object[] a, int index, Object value)  {
        putObjectVolatile(a, ARRAY_OBJECT_BASE_OFFSET + index * ARRAY_OBJECT_INDEX_SCALE, value);
    }

    public static boolean compareAndSwapArray(int[] a, int index, int expect, int value)    {
        return compareAndSwapInt(a, ARRAY_INT_BASE_OFFSET + index * ARRAY_INT_INDEX_SCALE, expect, value);
    }

    public static boolean compareAndSwapArray(long[] a, int index, long expect, long value)    {
        return compareAndSwapLong(a, ARRAY_LONG_BASE_OFFSET + index * ARRAY_LONG_INDEX_SCALE, expect, value);
    }

    public static boolean compareAndSwapArray(Object[] a, int index, Object expect, Object value)    {
        return compareAndSwapObject(a, ARRAY_OBJECT_BASE_OFFSET + index * ARRAY_OBJECT_INDEX_SCALE, expect, value);
    }
}
