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
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.ProtectionDomain;

/**
 * Wrapper class around {@link Unsafe}.
 * <p>
 * Really serves very little purpose except to avoid the otherwise unavoidable "Internal API" warnings at compile-time
 * caused by referencing anything in {@link sun}
 *
 * @author DaPorkchop_
 */
public interface PUnsafe {
    /**
     * A constant reference to {@link Unsafe}
     */
    Unsafe UNSAFE = PorkUtil.unsafe;

    //some constants

    /**
     * The value of {@code arrayBaseOffset(boolean[].class)}
     */
    int ARRAY_BOOLEAN_BASE_OFFSET = arrayBaseOffset(boolean[].class);

    /**
     * The value of {@code arrayBaseOffset(byte[].class)}
     */
    int ARRAY_BYTE_BASE_OFFSET = arrayBaseOffset(byte[].class);

    /**
     * The value of {@code arrayBaseOffset(short[].class)}
     */
    int ARRAY_SHORT_BASE_OFFSET = arrayBaseOffset(short[].class);

    /**
     * The value of {@code arrayBaseOffset(char[].class)}
     */
    int ARRAY_CHAR_BASE_OFFSET = arrayBaseOffset(char[].class);

    /**
     * The value of {@code arrayBaseOffset(int[].class)}
     */
    int ARRAY_INT_BASE_OFFSET = arrayBaseOffset(int[].class);

    /**
     * The value of {@code arrayBaseOffset(long[].class)}
     */
    int ARRAY_LONG_BASE_OFFSET = arrayBaseOffset(long[].class);

    /**
     * The value of {@code arrayBaseOffset(float[].class)}
     */
    int ARRAY_FLOAT_BASE_OFFSET = arrayBaseOffset(float[].class);

    /**
     * The value of {@code arrayBaseOffset(double[].class)}
     */
    int ARRAY_DOUBLE_BASE_OFFSET = arrayBaseOffset(double[].class);

    /**
     * The value of {@code arrayBaseOffset(Object[].class)}
     */
    int ARRAY_OBJECT_BASE_OFFSET = arrayBaseOffset(Object[].class);

    /**
     * The value of {@code arrayIndexScale(boolean[].class)}
     */
    int ARRAY_BOOLEAN_INDEX_SCALE = arrayIndexScale(boolean[].class);

    /**
     * The value of {@code arrayIndexScale(byte[].class)}
     */
    int ARRAY_BYTE_INDEX_SCALE = arrayIndexScale(byte[].class);

    /**
     * The value of {@code arrayIndexScale(short[].class)}
     */
    int ARRAY_SHORT_INDEX_SCALE = arrayIndexScale(short[].class);

    /**
     * The value of {@code arrayIndexScale(char[].class)}
     */
    int ARRAY_CHAR_INDEX_SCALE = arrayIndexScale(char[].class);

    /**
     * The value of {@code arrayIndexScale(int[].class)}
     */
    int ARRAY_INT_INDEX_SCALE = arrayIndexScale(int[].class);

    /**
     * The value of {@code arrayIndexScale(long[].class)}
     */
    int ARRAY_LONG_INDEX_SCALE = arrayIndexScale(long[].class);

    /**
     * The value of {@code arrayIndexScale(float[].class)}
     */
    int ARRAY_FLOAT_INDEX_SCALE = arrayIndexScale(float[].class);

    /**
     * The value of {@code arrayIndexScale(double[].class)}
     */
    int ARRAY_DOUBLE_INDEX_SCALE = arrayIndexScale(double[].class);

    /**
     * The value of {@code arrayIndexScale(Object[].class)}
     */
    int ARRAY_OBJECT_INDEX_SCALE = arrayIndexScale(Object[].class);

    //methods

    static int getInt(Object o, long pos) {
        return UNSAFE.getInt(o, pos);
    }

    static void putInt(Object o, long pos, int val) {
        UNSAFE.putInt(o, pos, val);
    }

    @SuppressWarnings("unchecked")
    static <T> T getObject(Object o, long pos) {
        return (T) UNSAFE.getObject(o, pos);
    }

    static void putObject(Object o, long pos, Object val) {
        UNSAFE.putObject(o, pos, val);
    }

    static boolean getBoolean(Object o, long pos) {
        return UNSAFE.getBoolean(o, pos);
    }

    static void putBoolean(Object o, long pos, boolean val) {
        UNSAFE.putBoolean(o, pos, val);
    }

    static byte getByte(Object o, long pos) {
        return UNSAFE.getByte(o, pos);
    }

    static void putByte(Object o, long pos, byte val) {
        UNSAFE.putByte(o, pos, val);
    }

    static short getShort(Object o, long pos) {
        return UNSAFE.getShort(o, pos);
    }

    static void putShort(Object o, long pos, short val) {
        UNSAFE.putShort(o, pos, val);
    }

    static char getChar(Object o, long pos) {
        return UNSAFE.getChar(o, pos);
    }

    static void putChar(Object o, long pos, char val) {
        UNSAFE.putChar(o, pos, val);
    }

    static long getLong(Object o, long pos) {
        return UNSAFE.getLong(o, pos);
    }

    static void putLong(Object o, long pos, long val) {
        UNSAFE.putLong(o, pos, val);
    }

    static float getFloat(Object o, long pos) {
        return UNSAFE.getFloat(o, pos);
    }

    static void putFloat(Object o, long pos, float val) {
        UNSAFE.putFloat(o, pos, val);
    }

    static double getDouble(Object o, long pos) {
        return UNSAFE.getDouble(o, pos);
    }

    static void putDouble(Object o, long pos, double val) {
        UNSAFE.putDouble(o, pos, val);
    }

    static int getInt(long pos) {
        return UNSAFE.getInt(pos);
    }

    static void putInt(long pos, int val) {
        UNSAFE.putInt(pos, val);
    }

    static byte getByte(long pos) {
        return UNSAFE.getByte(pos);
    }

    static void putByte(long pos, byte val) {
        UNSAFE.putByte(pos, val);
    }

    static short getShort(long pos) {
        return UNSAFE.getShort(pos);
    }

    static void putShort(long pos, short val) {
        UNSAFE.putShort(pos, val);
    }

    static char getChar(long pos) {
        return UNSAFE.getChar(pos);
    }

    static void putChar(long pos, char val) {
        UNSAFE.putChar(pos, val);
    }

    static long getLong(long pos) {
        return UNSAFE.getLong(pos);
    }

    static void putLong(long pos, long val) {
        UNSAFE.putLong(pos, val);
    }

    static float getFloat(long pos) {
        return UNSAFE.getFloat(pos);
    }

    static void putFloat(long pos, float val) {
        UNSAFE.putFloat(pos, val);
    }

    static double getDouble(long pos) {
        return UNSAFE.getDouble(pos);
    }

    static void putDouble(long pos, double val) {
        UNSAFE.putDouble(pos, val);
    }

    static long getAddress(long pos) {
        return UNSAFE.getAddress(pos);
    }

    static void putAddress(long pos, long val) {
        UNSAFE.putAddress(pos, val);
    }

    static long allocateMemory(long size) {
        return UNSAFE.allocateMemory(size);
    }

    static long reallocateMemory(long oldAddress, long size) {
        return UNSAFE.reallocateMemory(oldAddress, size);
    }

    static void setMemory(Object o, long pos, long length, byte val) {
        UNSAFE.setMemory(o, pos, length, val);
    }

    static void setMemory(long pos, long length, byte val) {
        UNSAFE.setMemory(null, pos, length, val);
    }

    static void copyMemory(Object src, long srcOffset, Object dst, long dstOffset, long length) {
        UNSAFE.copyMemory(src, srcOffset, dst, dstOffset, length);
    }

    static void copyMemory(long src, long dst, long length) {
        UNSAFE.copyMemory(null, src, null, dst, length);
    }

    static void freeMemory(long address) {
        UNSAFE.freeMemory(address);
    }

    static long staticFieldOffset(Field field) {
        return UNSAFE.staticFieldOffset(field);
    }

    static long objectFieldOffset(Field field) {
        return UNSAFE.objectFieldOffset(field);
    }

    static Object staticFieldBase(Field field) {
        return UNSAFE.staticFieldBase(field);
    }

    static boolean shouldBeInitialized(Class<?> clazz) {
        return UNSAFE.shouldBeInitialized(clazz);
    }

    static void ensureClassInitialized(Class<?> clazz) {
        UNSAFE.ensureClassInitialized(clazz);
    }

    static int arrayBaseOffset(Class<?> clazz) {
        return UNSAFE.arrayBaseOffset(clazz);
    }

    static int arrayIndexScale(Class<?> clazz) {
        return UNSAFE.arrayIndexScale(clazz);
    }

    static int addressSize() {
        return UNSAFE.addressSize();
    }

    static int pageSize() {
        return UNSAFE.pageSize();
    }

    static Class<?> defineClass(String name, byte[] var2, int var3, int var4, ClassLoader var5, ProtectionDomain var6) {
        return UNSAFE.defineClass(name, var2, var3, var4, var5, var6);
    }

    static Class<?> defineAnonymousClass(Class<?> var1, byte[] var2, Object[] var3) {
        return UNSAFE.defineAnonymousClass(var1, var2, var3);
    }

    static Object allocateInstance(Class<?> clazz) {
        try {
            return UNSAFE.allocateInstance(clazz);
        } catch (InstantiationException e) {
            throw PConstants.p_exception(e);
        }
    }

    static void throwException(Throwable t) {
        UNSAFE.throwException(t);
    }

    static boolean compareAndSwapObject(Object o, long pos, Object var4, Object var5) {
        return UNSAFE.compareAndSwapObject(o, pos, var4, var5);
    }

    static boolean compareAndSwapInt(Object var1, long var2, int var4, int var5) {
        return UNSAFE.compareAndSwapInt(var1, var2, var4, var5);
    }

    static boolean compareAndSwapLong(Object var1, long var2, long var4, long var6) {
        return UNSAFE.compareAndSwapLong(var1, var2, var4, var6);
    }

    @SuppressWarnings("unchecked")
    static <T> T getObjectVolatile(Object var1, long var2) {
        return (T) UNSAFE.getObjectVolatile(var1, var2);
    }

    static void putObjectVolatile(Object var1, long var2, Object var4) {
        UNSAFE.putObjectVolatile(var1, var2, var4);
    }

    static int getIntVolatile(Object var1, long var2) {
        return UNSAFE.getIntVolatile(var1, var2);
    }

    static void putIntVolatile(Object var1, long var2, int var4) {
        UNSAFE.putIntVolatile(var1, var2, var4);
    }

    static boolean getBooleanVolatile(Object var1, long var2) {
        return UNSAFE.getBooleanVolatile(var1, var2);
    }

    static void putBooleanVolatile(Object var1, long var2, boolean var4) {
        UNSAFE.putBooleanVolatile(var1, var2, var4);
    }

    static byte getByteVolatile(Object var1, long var2) {
        return UNSAFE.getByteVolatile(var1, var2);
    }

    static void putByteVolatile(Object var1, long var2, byte var4) {
        UNSAFE.putByteVolatile(var1, var2, var4);
    }

    static short getShortVolatile(Object var1, long var2) {
        return UNSAFE.getShortVolatile(var1, var2);
    }

    static void putShortVolatile(Object var1, long var2, short var4) {
        UNSAFE.putShortVolatile(var1, var2, var4);
    }

    static char getCharVolatile(Object var1, long var2) {
        return UNSAFE.getCharVolatile(var1, var2);
    }

    static void putCharVolatile(Object var1, long var2, char var4) {
        UNSAFE.putCharVolatile(var1, var2, var4);
    }

    static long getLongVolatile(Object var1, long var2) {
        return UNSAFE.getLongVolatile(var1, var2);
    }

    static void putLongVolatile(Object var1, long var2, long var4) {
        UNSAFE.putLongVolatile(var1, var2, var4);
    }

    static float getFloatVolatile(Object var1, long var2) {
        return UNSAFE.getFloatVolatile(var1, var2);
    }

    static void putFloatVolatile(Object var1, long var2, float var4) {
        UNSAFE.putFloatVolatile(var1, var2, var4);
    }

    static double getDoubleVolatile(Object var1, long var2) {
        return UNSAFE.getDoubleVolatile(var1, var2);
    }

    static void putDoubleVolatile(Object var1, long var2, double var4) {
        UNSAFE.putDoubleVolatile(var1, var2, var4);
    }

    static void putOrderedObject(Object var1, long var2, Object var4) {
        UNSAFE.putOrderedObject(var1, var2, var4);
    }

    static void putOrderedInt(Object var1, long var2, int var4) {
        UNSAFE.putOrderedInt(var1, var2, var4);
    }

    static void putOrderedLong(Object var1, long var2, long var4) {
        UNSAFE.putOrderedLong(var1, var2, var4);
    }

    static void unpark(Object var1) {
        UNSAFE.unpark(var1);
    }

    static void park(boolean var1, long var2) {
        UNSAFE.park(var1, var2);
    }

    static int getLoadAverage(double[] var1, int var2) {
        return UNSAFE.getLoadAverage(var1, var2);
    }

    static int getAndAddInt(Object var1, long var2, int var4) {
        return UNSAFE.getAndAddInt(var1, var2, var4);
    }

    static long getAndAddLong(Object var1, long var2, long var4) {
        return UNSAFE.getAndAddLong(var1, var2, var4);
    }

    static int getAndSetInt(Object var1, long var2, int var4) {
        return UNSAFE.getAndSetInt(var1, var2, var4);
    }

    static long getAndSetLong(Object var1, long var2, long var4) {
        return UNSAFE.getAndSetLong(var1, var2, var4);
    }

    @SuppressWarnings("unchecked")
    static <T> T getAndSetObject(Object var1, long var2, Object var4) {
        return (T) UNSAFE.getAndSetObject(var1, var2, var4);
    }

    static void loadFence() {
        UNSAFE.loadFence();
    }

    static void storeFence() {
        UNSAFE.storeFence();
    }

    static void fullFence() {
        UNSAFE.fullFence();
    }

    //custom methods
    static long pork_getOffset(@NonNull Class clazz, @NonNull String fieldName) {
        try {
            return UNSAFE.objectFieldOffset(clazz.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            throw PConstants.p_exception(e);
        }
    }

    static float getAndAddFloat(Object var1, long var2, float var4) {
        int var5;
        do {
            var5 = UNSAFE.getIntVolatile(var1, var2);
        } while (!UNSAFE.compareAndSwapInt(var1, var2, var5, Float.floatToIntBits(Float.intBitsToFloat(var5) + var4)));

        return var5;
    }

    static double getAndAddDouble(Object var1, long var2, double var4) {
        long var6;
        do {
            var6 = UNSAFE.getLongVolatile(var1, var2);
        }
        while (!UNSAFE.compareAndSwapLong(var1, var2, var6, Double.doubleToLongBits(Double.longBitsToDouble(var6) + var4)));

        return var6;
    }
}
