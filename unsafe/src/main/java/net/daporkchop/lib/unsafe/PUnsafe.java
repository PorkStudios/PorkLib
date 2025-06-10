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

package net.daporkchop.lib.unsafe;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;

import static net.daporkchop.lib.unsafe.UnsafePlatformInfo.*;

/**
 * Wrapper class around {@link Unsafe}.
 * <p>
 * Really serves very little purpose except to avoid the otherwise unavoidable "Internal API" warnings at compile-time
 * caused by referencing anything in {@link sun}.
 *
 * @author DaPorkchop_
 */
@SuppressWarnings({ "PointlessArithmeticExpression", "unused", "UnusedReturnValue" })
@UtilityClass
public class PUnsafe {
    /**
     * A reference to {@link Unsafe}.
     */
    final Unsafe UNSAFE = AccessController.doPrivileged((PrivilegedAction<Unsafe>) () -> {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new AssertionError("Unable to obtain instance of sun.misc.Unsafe", e);
        }
    });

    static final Object jdk_internal_misc_Unsafe = AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
        try {
            if (JAVA_VERSION >= 9) {
                //acquire an instance of jdk.internal.misc.Unsafe if it's available
                Class<?> jdk_internal_misc_Unsafe = Class.forName("jdk.internal.misc.Unsafe");
                return MethodHandles.lookup()
                        .findStatic(jdk_internal_misc_Unsafe, "getUnsafe", MethodType.methodType(jdk_internal_misc_Unsafe))
                        .invoke();
            } else {
                return null;
            }
        } catch (Throwable t) {
            throw new AssertionError("Unable to obtain instance of jdk.internal.misc.Unsafe", t);
        }
    });

    //
    // INTERNAL
    //

    static final long DIRECT_BUFFER_ADDRESS_OFFSET = AccessController.doPrivileged((PrivilegedAction<Long>) () -> {
        try {
            return objectFieldOffset(Buffer.class.getDeclaredField("address"));
        } catch (NoSuchFieldException e) {
            throw new AssertionError("Unable to resolve direct buffer address offset!");
        }
    });

    //the following deprecated fields will be marked as private eventually

    /**
     * @deprecated use {@link #arrayBooleanBaseOffset()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_BOOLEAN_BASE_OFFSET = UNSAFE.arrayBaseOffset(boolean[].class);

    /**
     * @deprecated use {@link #arrayByteBaseOffset()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_BYTE_BASE_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);

    /**
     * @deprecated use {@link #arrayShortBaseOffset()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_SHORT_BASE_OFFSET = UNSAFE.arrayBaseOffset(short[].class);

    /**
     * @deprecated use {@link #arrayCharBaseOffset()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_CHAR_BASE_OFFSET = UNSAFE.arrayBaseOffset(char[].class);

    /**
     * @deprecated use {@link #arrayIntBaseOffset()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_INT_BASE_OFFSET = UNSAFE.arrayBaseOffset(int[].class);

    /**
     * @deprecated use {@link #arrayLongBaseOffset()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_LONG_BASE_OFFSET = UNSAFE.arrayBaseOffset(long[].class);

    /**
     * @deprecated use {@link #arrayFloatBaseOffset()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_FLOAT_BASE_OFFSET = UNSAFE.arrayBaseOffset(float[].class);

    /**
     * @deprecated use {@link #arrayDoubleBaseOffset()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_DOUBLE_BASE_OFFSET = UNSAFE.arrayBaseOffset(double[].class);

    /**
     * @deprecated use {@link #arrayObjectBaseOffset()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_OBJECT_BASE_OFFSET = UNSAFE.arrayBaseOffset(Object[].class);

    /**
     * @deprecated use {@link #arrayBooleanIndexScale()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_BOOLEAN_INDEX_SCALE = UNSAFE.arrayIndexScale(boolean[].class);

    /**
     * @deprecated use {@link #arrayByteIndexScale()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_BYTE_INDEX_SCALE = UNSAFE.arrayIndexScale(byte[].class);

    /**
     * @deprecated use {@link #arrayShortIndexScale()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_SHORT_INDEX_SCALE = UNSAFE.arrayIndexScale(short[].class);

    /**
     * @deprecated use {@link #arrayCharIndexScale()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_CHAR_INDEX_SCALE = UNSAFE.arrayIndexScale(char[].class);

    /**
     * @deprecated use {@link #arrayIntIndexScale()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_INT_INDEX_SCALE = UNSAFE.arrayIndexScale(int[].class);

    /**
     * @deprecated use {@link #arrayLongIndexScale()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_LONG_INDEX_SCALE = UNSAFE.arrayIndexScale(long[].class);

    /**
     * @deprecated use {@link #arrayFloatIndexScale()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_FLOAT_INDEX_SCALE = UNSAFE.arrayIndexScale(float[].class);

    /**
     * @deprecated use {@link #arrayDoubleIndexScale()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_DOUBLE_INDEX_SCALE = UNSAFE.arrayIndexScale(double[].class);

    /**
     * @deprecated use {@link #arrayObjectIndexScale()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_OBJECT_INDEX_SCALE = UNSAFE.arrayIndexScale(Object[].class);

    /**
     * @deprecated use {@link #addressSize()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final int ADDRESS_SIZE = UNSAFE.addressSize();

    /**
     * @deprecated use {@link #pageSize()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final int PAGE_SIZE = UNSAFE.pageSize();

    private final boolean UNALIGNED = AccessController.doPrivileged((PrivilegedAction<Boolean>) () -> {
        boolean unaligned = false;
        try {
            Class<?> bitsClass = Class.forName("java.nio.Bits", false, ClassLoader.getSystemClassLoader());
            if (UnsafePlatformInfo.JAVA_VERSION >= 9) {
                try {
                    Field field = bitsClass.getDeclaredField(UnsafePlatformInfo.JAVA_VERSION >= 11 ? "UNALIGNED" : "unaligned");
                    if (field.getType() == boolean.class) {
                        unaligned = new UnsafeStaticField(field).getBoolean();
                    }
                } catch (NoSuchFieldException e) {
                    //silently ignore exception and continue
                }
            }

            if (!unaligned) {
                Method unalignedMethod = bitsClass.getDeclaredMethod("unaligned");
                unalignedMethod.setAccessible(true);
                unaligned = (boolean) unalignedMethod.invoke(null);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | SecurityException e) {
            //silently ignore exception and continue
        }

        if (!unaligned) { //unaligned memory access isn't available, check to see if we're on x86
            try {
                //noinspection DynamicRegexReplaceableByCompiledPattern
                unaligned = System.getProperty("os.arch", "").matches("^(i[3-6]86|x86(_64)?|x64|amd64)$");
            } catch (SecurityException e) {
                //silently ignore and continue
            }
        }

        if (Boolean.getBoolean("porklib.unsafe.forceAlignedAccess")) {
            unaligned = false;
        }

        return unaligned;
    });

    //
    // ASSERTIONS
    //
    // these are intended to be invoked once in the static initializer of classes which rely on certain JVM/architecture implementation details in order to work correctly
    //

    /**
     * Ensures that unaligned memory accesses are supported.
     * <p>
     * This assertion is not necessary for any of the {@code getUnaligned*} or {@code putUnaligned*} methods, as they will operate correctly in either case.
     *
     * @throws AssertionError if unaligned memory accesses are not supported
     */
    public void requireUnalignedAccess() throws AssertionError {
        if (!UNALIGNED) {
            throw new AssertionError("unaligned memory accesses not supported!");
        }
    }

    /**
     * Ensures that primitive array elements are tightly packed in memory.
     *
     * @throws AssertionError if any primitive array types are not stored tightly packed in memory
     */
    public static void requireTightlyPackedPrimitiveArrays() throws AssertionError {
        requireTightlyPackedBooleanArrays();
        requireTightlyPackedByteArrays();
        requireTightlyPackedShortArrays();
        requireTightlyPackedCharArrays();
        requireTightlyPackedIntArrays();
        requireTightlyPackedLongArrays();
        requireTightlyPackedFloatArrays();
        requireTightlyPackedDoubleArrays();
    }

    /**
     * Ensures that {@code boolean[]} elements are tightly packed in memory.
     *
     * @throws AssertionError if {@code boolean[]} elements are not tightly packed in memory
     *                        ** @see #requireTightlyPackedPrimitiveArrays()
     */
    public static void requireTightlyPackedBooleanArrays() throws AssertionError {
        if (arrayBooleanIndexScale() != Byte.BYTES) { //we assume boolean[] elements always occupy a full byte in memory
            throw new AssertionError("boolean[] is not tightly packed!");
        }
    }

    /**
     * Ensures that {@code byte[]} elements are tightly packed in memory.
     *
     * @throws AssertionError if {@code byte[]} elements are not tightly packed in memory
     * @see #requireTightlyPackedPrimitiveArrays()
     */
    public static void requireTightlyPackedByteArrays() throws AssertionError {
        if (arrayByteIndexScale() != Byte.BYTES) {
            throw new AssertionError("byte[] is not tightly packed!");
        }
    }

    /**
     * Ensures that {@code short[]} elements are tightly packed in memory.
     *
     * @throws AssertionError if {@code short[]} elements are not tightly packed in memory
     * @see #requireTightlyPackedPrimitiveArrays()
     */
    public static void requireTightlyPackedShortArrays() throws AssertionError {
        if (arrayShortIndexScale() != Short.BYTES) {
            throw new AssertionError("short[] is not tightly packed!");
        }
    }

    /**
     * Ensures that {@code char[]} elements are tightly packed in memory.
     *
     * @throws AssertionError if {@code char[]} elements are not tightly packed in memory
     * @see #requireTightlyPackedPrimitiveArrays()
     */
    public static void requireTightlyPackedCharArrays() throws AssertionError {
        if (arrayCharIndexScale() != Character.BYTES) {
            throw new AssertionError("char[] is not tightly packed!");
        }
    }

    /**
     * Ensures that {@code int[]} elements are tightly packed in memory.
     *
     * @throws AssertionError if {@code int[]} elements are not tightly packed in memory
     * @see #requireTightlyPackedPrimitiveArrays()
     */
    public static void requireTightlyPackedIntArrays() throws AssertionError {
        if (arrayIntIndexScale() != Integer.BYTES) {
            throw new AssertionError("int[] is not tightly packed!");
        }
    }

    /**
     * Ensures that {@code long[]} elements are tightly packed in memory.
     *
     * @throws AssertionError if {@code long[]} elements are not tightly packed in memory
     * @see #requireTightlyPackedPrimitiveArrays()
     */
    public static void requireTightlyPackedLongArrays() throws AssertionError {
        if (arrayLongIndexScale() != Long.BYTES) {
            throw new AssertionError("long[] is not tightly packed!");
        }
    }

    /**
     * Ensures that {@code float[]} elements are tightly packed in memory.
     *
     * @throws AssertionError if {@code float[]} elements are not tightly packed in memory
     * @see #requireTightlyPackedPrimitiveArrays()
     */
    public static void requireTightlyPackedFloatArrays() throws AssertionError {
        if (arrayFloatIndexScale() != Float.BYTES) {
            throw new AssertionError("float[] is not tightly packed!");
        }
    }

    /**
     * Ensures that {@code double[]} elements are tightly packed in memory.
     *
     * @throws AssertionError if {@code double[]} elements are not tightly packed in memory
     * @see #requireTightlyPackedPrimitiveArrays()
     */
    public static void requireTightlyPackedDoubleArrays() throws AssertionError {
        if (arrayDoubleIndexScale() != Double.BYTES) {
            throw new AssertionError("double[] is not tightly packed!");
        }
    }

    //
    // ARRAY BASE OFFSETS
    //

    /**
     * @return the value of {@code arrayBaseOffset(boolean[].class)}
     */
    public static long arrayBooleanBaseOffset() {
        return ARRAY_BOOLEAN_BASE_OFFSET;
    }

    /**
     * @return the value of {@code arrayBaseOffset(byte[].class)}
     */
    public static long arrayByteBaseOffset() {
        return ARRAY_BYTE_BASE_OFFSET;
    }

    /**
     * @return the value of {@code arrayBaseOffset(short[].class)}
     */
    public static long arrayShortBaseOffset() {
        return ARRAY_SHORT_BASE_OFFSET;
    }

    /**
     * @return the value of {@code arrayBaseOffset(char[].class)}
     */
    public static long arrayCharBaseOffset() {
        return ARRAY_CHAR_BASE_OFFSET;
    }

    /**
     * @return the value of {@code arrayBaseOffset(int[].class)}
     */
    public static long arrayIntBaseOffset() {
        return ARRAY_INT_BASE_OFFSET;
    }

    /**
     * @return the value of {@code arrayBaseOffset(long[].class)}
     */
    public static long arrayLongBaseOffset() {
        return ARRAY_LONG_BASE_OFFSET;
    }

    /**
     * @return the value of {@code arrayBaseOffset(float[].class)}
     */
    public static long arrayFloatBaseOffset() {
        return ARRAY_FLOAT_BASE_OFFSET;
    }

    /**
     * @return the value of {@code arrayBaseOffset(double[].class)}
     */
    public static long arrayDoubleBaseOffset() {
        return ARRAY_DOUBLE_BASE_OFFSET;
    }

    /**
     * @return the value of {@code arrayBaseOffset(Object[].class)}
     */
    public static long arrayObjectBaseOffset() {
        return ARRAY_OBJECT_BASE_OFFSET;
    }

    //
    // ARRAY INDEX SCALES
    //

    /**
     * @return the value of {@code arrayIndexScale(boolean[].class)}
     */
    public static long arrayBooleanIndexScale() {
        return ARRAY_BOOLEAN_INDEX_SCALE;
    }

    /**
     * @return the value of {@code arrayIndexScale(byte[].class)}
     */
    public static long arrayByteIndexScale() {
        return ARRAY_BYTE_INDEX_SCALE;
    }

    /**
     * @return the value of {@code arrayIndexScale(short[].class)}
     */
    public static long arrayShortIndexScale() {
        return ARRAY_SHORT_INDEX_SCALE;
    }

    /**
     * @return the value of {@code arrayIndexScale(char[].class)}
     */
    public static long arrayCharIndexScale() {
        return ARRAY_CHAR_INDEX_SCALE;
    }

    /**
     * @return the value of {@code arrayIndexScale(int[].class)}
     */
    public static long arrayIntIndexScale() {
        return ARRAY_INT_INDEX_SCALE;
    }

    /**
     * @return the value of {@code arrayIndexScale(long[].class)}
     */
    public static long arrayLongIndexScale() {
        return ARRAY_LONG_INDEX_SCALE;
    }

    /**
     * @return the value of {@code arrayIndexScale(float[].class)}
     */
    public static long arrayFloatIndexScale() {
        return ARRAY_FLOAT_INDEX_SCALE;
    }

    /**
     * @return the value of {@code arrayIndexScale(double[].class)}
     */
    public static long arrayDoubleIndexScale() {
        return ARRAY_DOUBLE_INDEX_SCALE;
    }

    /**
     * @return the value of {@code arrayIndexScale(Object[].class)}
     */
    public static long arrayObjectIndexScale() {
        return ARRAY_OBJECT_INDEX_SCALE;
    }

    //
    // ARCHITECTURE INFORMATION
    //

    /**
     * @return the value of {@link Unsafe#addressSize()}.
     */
    public static int addressSize() {
        return ADDRESS_SIZE;
    }

    /**
     * @return the value of {@link Unsafe#pageSize()}.
     */
    public static int pageSize() {
        return PAGE_SIZE;
    }

    /**
     * @return whether the current system supports unaligned memory access
     */
    public static boolean isUnalignedAccessSupported() {
        return UNALIGNED;
    }

    //
    // SYSTEM INFORMATION
    //

    public static int getLoadAverage(double[] loadavg, int nelems) {
        return UNSAFE.getLoadAverage(loadavg, nelems);
    }

    //
    // FIELD OFFSET ACCESSORS
    //

    public static long objectFieldOffset(Field field) {
        return UNSAFE.objectFieldOffset(field);
    }

    //TODO: it seems this isn't supported when running in a graalvm native image
    public static Object staticFieldBase(Field field) {
        return UNSAFE.staticFieldBase(field);
    }

    public static long staticFieldOffset(Field field) {
        return UNSAFE.staticFieldOffset(field);
    }

    //
    // ARRAY OFFSET ACCESSORS
    //

    public static long arrayBooleanElementOffset(int index) {
        return index * ARRAY_BOOLEAN_INDEX_SCALE + ARRAY_BOOLEAN_BASE_OFFSET;
    }

    public static long arrayByteElementOffset(int index) {
        return index * ARRAY_BYTE_INDEX_SCALE + ARRAY_BYTE_BASE_OFFSET;
    }

    public static long arrayShortElementOffset(int index) {
        return index * ARRAY_SHORT_INDEX_SCALE + ARRAY_SHORT_BASE_OFFSET;
    }

    public static long arrayCharElementOffset(int index) {
        return index * ARRAY_CHAR_INDEX_SCALE + ARRAY_CHAR_BASE_OFFSET;
    }

    public static long arrayIntElementOffset(int index) {
        return index * ARRAY_INT_INDEX_SCALE + ARRAY_INT_BASE_OFFSET;
    }

    public static long arrayLongElementOffset(int index) {
        return index * ARRAY_LONG_INDEX_SCALE + ARRAY_LONG_BASE_OFFSET;
    }

    public static long arrayFloatElementOffset(int index) {
        return index * ARRAY_FLOAT_INDEX_SCALE + ARRAY_FLOAT_BASE_OFFSET;
    }

    public static long arrayDoubleElementOffset(int index) {
        return index * ARRAY_DOUBLE_INDEX_SCALE + ARRAY_DOUBLE_BASE_OFFSET;
    }

    public static long arrayObjectElementOffset(int index) {
        return index * ARRAY_OBJECT_INDEX_SCALE + ARRAY_OBJECT_BASE_OFFSET;
    }

    //
    // REGULAR LOADS
    //

    public static boolean getBoolean(Object base, long offset) {
        return UNSAFE.getBoolean(base, offset);
    }

    public static byte getByte(Object base, long offset) {
        return UNSAFE.getByte(base, offset);
    }

    public static short getShort(Object base, long offset) {
        return UNSAFE.getShort(base, offset);
    }

    public static char getChar(Object base, long offset) {
        return UNSAFE.getChar(base, offset);
    }

    public static int getInt(Object base, long offset) {
        return UNSAFE.getInt(base, offset);
    }

    public static long getLong(Object base, long offset) {
        return UNSAFE.getLong(base, offset);
    }

    public static float getFloat(Object base, long offset) {
        return UNSAFE.getFloat(base, offset);
    }

    public static double getDouble(Object base, long offset) {
        return UNSAFE.getDouble(base, offset);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getObject(Object base, long offset) {
        return (T) UNSAFE.getObject(base, offset);
    }

    public static boolean getBoolean(long address) {
        return UNSAFE.getBoolean(null, address);
    }

    public static byte getByte(long address) {
        return UNSAFE.getByte(null, address);
    }

    public static short getShort(long address) {
        return UNSAFE.getShort(null, address);
    }

    public static char getChar(long address) {
        return UNSAFE.getChar(null, address);
    }

    public static int getInt(long address) {
        return UNSAFE.getInt(null, address);
    }

    public static long getLong(long address) {
        return UNSAFE.getLong(null, address);
    }

    public static float getFloat(long address) {
        return UNSAFE.getFloat(null, address);
    }

    public static double getDouble(long address) {
        return UNSAFE.getDouble(null, address);
    }
    
    //
    // REGULAR STORES
    //

    public static void putBoolean(Object base, long offset, boolean val) {
        UNSAFE.putBoolean(base, offset, val);
    }

    public static void putByte(Object base, long offset, byte val) {
        UNSAFE.putByte(base, offset, val);
    }

    public static void putShort(Object base, long offset, short val) {
        UNSAFE.putShort(base, offset, val);
    }

    public static void putChar(Object base, long offset, char val) {
        UNSAFE.putChar(base, offset, val);
    }

    public static void putInt(Object base, long offset, int val) {
        UNSAFE.putInt(base, offset, val);
    }

    public static void putLong(Object base, long offset, long val) {
        UNSAFE.putLong(base, offset, val);
    }

    public static void putFloat(Object base, long offset, float val) {
        UNSAFE.putFloat(base, offset, val);
    }

    public static void putDouble(Object base, long offset, double val) {
        UNSAFE.putDouble(base, offset, val);
    }

    public static void putObject(Object base, long offset, Object val) {
        UNSAFE.putObject(base, offset, val);
    }

    public static void putBoolean(long address, boolean val) {
        UNSAFE.putBoolean(null, address, val);
    }
    
    public static void putByte(long address, byte val) {
        UNSAFE.putByte(null, address, val);
    }
    
    public static void putShort(long address, short val) {
        UNSAFE.putShort(null, address, val);
    }
    
    public static void putChar(long address, char val) {
        UNSAFE.putChar(null, address, val);
    }
    
    public static void putInt(long address, int val) {
        UNSAFE.putInt(null, address, val);
    }
    
    public static void putLong(long address, long val) {
        UNSAFE.putLong(null, address, val);
    }
    
    public static void putFloat(long address, float val) {
        UNSAFE.putFloat(null, address, val);
    }
    
    public static void putDouble(long address, double val) {
        UNSAFE.putDouble(null, address, val);
    }

    //
    // VOLATILE LOADS
    //

    public static boolean getBooleanVolatile(Object base, long offset) {
        return UNSAFE.getBooleanVolatile(base, offset);
    }

    public static byte getByteVolatile(Object base, long offset) {
        return UNSAFE.getByteVolatile(base, offset);
    }

    public static short getShortVolatile(Object base, long offset) {
        return UNSAFE.getShortVolatile(base, offset);
    }

    public static char getCharVolatile(Object base, long offset) {
        return UNSAFE.getCharVolatile(base, offset);
    }

    public static int getIntVolatile(Object base, long offset) {
        return UNSAFE.getIntVolatile(base, offset);
    }

    public static long getLongVolatile(Object base, long offset) {
        return UNSAFE.getLongVolatile(base, offset);
    }

    public static float getFloatVolatile(Object base, long offset) {
        return UNSAFE.getFloatVolatile(base, offset);
    }

    public static double getDoubleVolatile(Object base, long offset) {
        return UNSAFE.getDoubleVolatile(base, offset);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getObjectVolatile(Object base, long offset) {
        return (T) UNSAFE.getObjectVolatile(base, offset);
    }
    
    //
    // VOLATILE STORES
    //

    public static void putBooleanVolatile(Object base, long offset, boolean val) {
        UNSAFE.putBooleanVolatile(base, offset, val);
    }
    
    public static void putByteVolatile(Object base, long offset, byte val) {
        UNSAFE.putByteVolatile(base, offset, val);
    }
    
    public static void putShortVolatile(Object base, long offset, short val) {
        UNSAFE.putShortVolatile(base, offset, val);
    }
    
    public static void putCharVolatile(Object base, long offset, char val) {
        UNSAFE.putCharVolatile(base, offset, val);
    }
    
    public static void putIntVolatile(Object base, long offset, int val) {
        UNSAFE.putIntVolatile(base, offset, val);
    }
    
    public static void putLongVolatile(Object base, long offset, long val) {
        UNSAFE.putLongVolatile(base, offset, val);
    }
    
    public static void putFloatVolatile(Object base, long offset, float val) {
        UNSAFE.putFloatVolatile(base, offset, val);
    }
    
    public static void putDoubleVolatile(Object base, long offset, double val) {
        UNSAFE.putDoubleVolatile(base, offset, val);
    }
    
    public static void putObjectVolatile(Object base, long offset, Object val) {
        UNSAFE.putObjectVolatile(base, offset, val);
    }

    //
    // ACQUIRE LOADS
    //

    //for emulating this on Java 8:
    //  acquire is equivalent to a regular load followed by Unsafe#loadFence(): https://cr.openjdk.org/~shade/8132332/8132332.jdk.patch

    private static final class GetAcquire_Java9 {
        static final MethodHandle getBooleanAcquire; // (Object, long) -> boolean
        static final MethodHandle getByteAcquire; // (Object, long) -> byte
        static final MethodHandle getShortAcquire; // (Object, long) -> short
        static final MethodHandle getCharAcquire; // (Object, long) -> char
        static final MethodHandle getIntAcquire; // (Object, long) -> int
        static final MethodHandle getLongAcquire; // (Object, long) -> long
        static final MethodHandle getFloatAcquire; // (Object, long) -> float
        static final MethodHandle getDoubleAcquire; // (Object, long) -> double
        static final MethodHandle getObjectAcquire; // (Object, long) -> Object

        static {
            try {
                getBooleanAcquire = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "getBooleanAcquire", MethodType.methodType(boolean.class, Object.class, long.class)).bindTo(jdk_internal_misc_Unsafe);
                getByteAcquire = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "getByteAcquire", MethodType.methodType(byte.class, Object.class, long.class)).bindTo(jdk_internal_misc_Unsafe);
                getShortAcquire = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "getShortAcquire", MethodType.methodType(short.class, Object.class, long.class)).bindTo(jdk_internal_misc_Unsafe);
                getCharAcquire = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "getCharAcquire", MethodType.methodType(char.class, Object.class, long.class)).bindTo(jdk_internal_misc_Unsafe);
                getIntAcquire = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "getIntAcquire", MethodType.methodType(int.class, Object.class, long.class)).bindTo(jdk_internal_misc_Unsafe);
                getLongAcquire = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "getLongAcquire", MethodType.methodType(long.class, Object.class, long.class)).bindTo(jdk_internal_misc_Unsafe);
                getFloatAcquire = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "getFloatAcquire", MethodType.methodType(float.class, Object.class, long.class)).bindTo(jdk_internal_misc_Unsafe);
                getDoubleAcquire = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "getDoubleAcquire", MethodType.methodType(double.class, Object.class, long.class)).bindTo(jdk_internal_misc_Unsafe);
                getObjectAcquire = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "getObjectAcquire", MethodType.methodType(Object.class, Object.class, long.class)).bindTo(jdk_internal_misc_Unsafe);
            } catch (Throwable t) {
                throw new AssertionError(t);
            }
        }
    }

    @SneakyThrows
    public static boolean getBooleanAcquire(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return (boolean) GetAcquire_Java9.getBooleanAcquire.invokeExact(base, offset);
        } else { //fall back to emulating it using Java 8 intrinsics
            boolean value = UNSAFE.getBoolean(base, offset);
            UNSAFE.loadFence();
            return value;
        }
    }

    @SneakyThrows
    public static byte getByteAcquire(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return (byte) GetAcquire_Java9.getByteAcquire.invokeExact(base, offset);
        } else { //fall back to emulating it using Java 8 intrinsics
            byte value = UNSAFE.getByte(base, offset);
            UNSAFE.loadFence();
            return value;
        }
    }

    @SneakyThrows
    public static short getShortAcquire(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return (short) GetAcquire_Java9.getShortAcquire.invokeExact(base, offset);
        } else { //fall back to emulating it using Java 8 intrinsics
            short value = UNSAFE.getShort(base, offset);
            UNSAFE.loadFence();
            return value;
        }
    }

    @SneakyThrows
    public static char getCharAcquire(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return (char) GetAcquire_Java9.getCharAcquire.invokeExact(base, offset);
        } else { //fall back to emulating it using Java 8 intrinsics
            char value = UNSAFE.getChar(base, offset);
            UNSAFE.loadFence();
            return value;
        }
    }

    @SneakyThrows
    public static int getIntAcquire(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return (int) GetAcquire_Java9.getIntAcquire.invokeExact(base, offset);
        } else { //fall back to emulating it using Java 8 intrinsics
            int value = UNSAFE.getInt(base, offset);
            UNSAFE.loadFence();
            return value;
        }
    }

    @SneakyThrows
    public static long getLongAcquire(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return (long) GetAcquire_Java9.getLongAcquire.invokeExact(base, offset);
        } else { //fall back to emulating it using Java 8 intrinsics
            long value = UNSAFE.getLong(base, offset);
            UNSAFE.loadFence();
            return value;
        }
    }

    @SneakyThrows
    public static float getFloatAcquire(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return (float) GetAcquire_Java9.getFloatAcquire.invokeExact(base, offset);
        } else { //fall back to emulating it using Java 8 intrinsics
            float value = UNSAFE.getFloat(base, offset);
            UNSAFE.loadFence();
            return value;
        }
    }

    @SneakyThrows
    public static double getDoubleAcquire(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return (double) GetAcquire_Java9.getDoubleAcquire.invokeExact(base, offset);
        } else { //fall back to emulating it using Java 8 intrinsics
            double value = UNSAFE.getDouble(base, offset);
            UNSAFE.loadFence();
            return value;
        }
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> T getObjectAcquire(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return (T) (Object) GetAcquire_Java9.getObjectAcquire.invokeExact(base, offset);
        } else { //fall back to emulating it using Java 8 intrinsics
            T value = (T) UNSAFE.getObject(base, offset);
            UNSAFE.loadFence();
            return value;
        }
    }

    //
    // RELEASE STORES
    //

    //for emulating this on Java 8:
    //  Unsafe.putOrdered*() has release semantics [citation needed], so we should use it where possible, otherwise fall back to volatile stores

    private static final class PutRelease_Java9 {
        static final MethodHandle putBooleanRelease; // (Object, long, boolean) -> void
        static final MethodHandle putByteRelease; // (Object, long, byte) -> void
        static final MethodHandle putShortRelease; // (Object, long, short) -> void
        static final MethodHandle putCharRelease; // (Object, long, char) -> void
        static final MethodHandle putIntRelease; // (Object, long, int) -> void
        static final MethodHandle putLongRelease; // (Object, long, long) -> void
        static final MethodHandle putFloatRelease; // (Object, long, float) -> void
        static final MethodHandle putDoubleRelease; // (Object, long, double) -> void
        static final MethodHandle putObjectRelease; // (Object, long, Object) -> void

        static {
            try {
                putBooleanRelease = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "putBooleanRelease", MethodType.methodType(void.class, Object.class, long.class, boolean.class)).bindTo(jdk_internal_misc_Unsafe);
                putByteRelease = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "putByteRelease", MethodType.methodType(void.class, Object.class, long.class, byte.class)).bindTo(jdk_internal_misc_Unsafe);
                putShortRelease = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "putShortRelease", MethodType.methodType(void.class, Object.class, long.class, short.class)).bindTo(jdk_internal_misc_Unsafe);
                putCharRelease = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "putCharRelease", MethodType.methodType(void.class, Object.class, long.class, char.class)).bindTo(jdk_internal_misc_Unsafe);
                putIntRelease = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "putIntRelease", MethodType.methodType(void.class, Object.class, long.class, int.class)).bindTo(jdk_internal_misc_Unsafe);
                putLongRelease = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "putLongRelease", MethodType.methodType(void.class, Object.class, long.class, long.class)).bindTo(jdk_internal_misc_Unsafe);
                putFloatRelease = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "putFloatRelease", MethodType.methodType(void.class, Object.class, long.class, float.class)).bindTo(jdk_internal_misc_Unsafe);
                putDoubleRelease = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "putDoubleRelease", MethodType.methodType(void.class, Object.class, long.class, double.class)).bindTo(jdk_internal_misc_Unsafe);
                putObjectRelease = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "putObjectRelease", MethodType.methodType(void.class, Object.class, long.class, Object.class)).bindTo(jdk_internal_misc_Unsafe);
            } catch (Throwable t) {
                throw new AssertionError(t);
            }
        }
    }

    @SneakyThrows
    public static void putBooleanRelease(Object base, long offset, boolean val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PutRelease_Java9.putBooleanRelease.invokeExact(base, offset, val);
        } else { //fall back to volatile store
            putBooleanVolatile(base, offset, val);
        }
    }

    @SneakyThrows
    public static void putByteRelease(Object base, long offset, byte val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PutRelease_Java9.putByteRelease.invokeExact(base, offset, val);
        } else { //fall back to volatile store
            putByteVolatile(base, offset, val);
        }
    }

    @SneakyThrows
    public static void putShortRelease(Object base, long offset, short val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PutRelease_Java9.putShortRelease.invokeExact(base, offset, val);
        } else { //fall back to volatile store
            putShortVolatile(base, offset, val);
        }
    }

    @SneakyThrows
    public static void putCharRelease(Object base, long offset, char val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PutRelease_Java9.putCharRelease.invokeExact(base, offset, val);
        } else { //fall back to volatile store
            putCharVolatile(base, offset, val);
        }
    }

    @SneakyThrows
    public static void putIntRelease(Object base, long offset, int val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PutRelease_Java9.putIntRelease.invokeExact(base, offset, val);
        } else { //fall back to ordered store
            UNSAFE.putOrderedInt(base, offset, val);
        }
    }

    @SneakyThrows
    public static void putLongRelease(Object base, long offset, long val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PutRelease_Java9.putLongRelease.invokeExact(base, offset, val);
        } else { //fall back to ordered store
            UNSAFE.putOrderedLong(base, offset, val);
        }
    }

    @SneakyThrows
    public static void putFloatRelease(Object base, long offset, float val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PutRelease_Java9.putFloatRelease.invokeExact(base, offset, val);
        } else { //fall back to ordered store
            UNSAFE.putOrderedInt(base, offset, Float.floatToRawIntBits(val));
        }
    }

    @SneakyThrows
    public static void putDoubleRelease(Object base, long offset, double val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PutRelease_Java9.putDoubleRelease.invokeExact(base, offset, val);
        } else { //fall back to ordered store
            UNSAFE.putOrderedLong(base, offset, Double.doubleToRawLongBits(val));
        }
    }

    @SneakyThrows
    public static void putObjectRelease(Object base, long offset, Object val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PutRelease_Java9.putObjectRelease.invokeExact(base, offset, val);
        } else { //fall back to ordered store
            UNSAFE.putOrderedObject(base, offset, val);
        }
    }

    //
    // OPAQUE LOADS
    //

    private static final class GetOpaque_Java9 {
        static final MethodHandle getBooleanOpaque; // (Object, long) -> boolean
        static final MethodHandle getByteOpaque; // (Object, long) -> byte
        static final MethodHandle getShortOpaque; // (Object, long) -> short
        static final MethodHandle getCharOpaque; // (Object, long) -> char
        static final MethodHandle getIntOpaque; // (Object, long) -> int
        static final MethodHandle getLongOpaque; // (Object, long) -> long
        static final MethodHandle getFloatOpaque; // (Object, long) -> float
        static final MethodHandle getDoubleOpaque; // (Object, long) -> double
        static final MethodHandle getObjectOpaque; // (Object, long) -> Object

        static {
            try {
                getBooleanOpaque = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "getBooleanOpaque", MethodType.methodType(boolean.class, Object.class, long.class)).bindTo(jdk_internal_misc_Unsafe);
                getByteOpaque = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "getByteOpaque", MethodType.methodType(byte.class, Object.class, long.class)).bindTo(jdk_internal_misc_Unsafe);
                getShortOpaque = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "getShortOpaque", MethodType.methodType(short.class, Object.class, long.class)).bindTo(jdk_internal_misc_Unsafe);
                getCharOpaque = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "getCharOpaque", MethodType.methodType(char.class, Object.class, long.class)).bindTo(jdk_internal_misc_Unsafe);
                getIntOpaque = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "getIntOpaque", MethodType.methodType(int.class, Object.class, long.class)).bindTo(jdk_internal_misc_Unsafe);
                getLongOpaque = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "getLongOpaque", MethodType.methodType(long.class, Object.class, long.class)).bindTo(jdk_internal_misc_Unsafe);
                getFloatOpaque = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "getFloatOpaque", MethodType.methodType(float.class, Object.class, long.class)).bindTo(jdk_internal_misc_Unsafe);
                getDoubleOpaque = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "getDoubleOpaque", MethodType.methodType(double.class, Object.class, long.class)).bindTo(jdk_internal_misc_Unsafe);
                getObjectOpaque = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "getObjectOpaque", MethodType.methodType(Object.class, Object.class, long.class)).bindTo(jdk_internal_misc_Unsafe);
            } catch (Throwable t) {
                throw new AssertionError(t);
            }
        }
    }

    @SneakyThrows
    public static boolean getBooleanOpaque(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return (boolean) GetOpaque_Java9.getBooleanOpaque.invokeExact(base, offset);
        } else { //fall back to volatile load
            return getBooleanVolatile(base, offset);
        }
    }

    @SneakyThrows
    public static byte getByteOpaque(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return (byte) GetOpaque_Java9.getByteOpaque.invokeExact(base, offset);
        } else { //fall back to volatile load
            return getByteVolatile(base, offset);
        }
    }

    @SneakyThrows
    public static short getShortOpaque(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return (short) GetOpaque_Java9.getShortOpaque.invokeExact(base, offset);
        } else { //fall back to volatile load
            return getShortVolatile(base, offset);
        }
    }

    @SneakyThrows
    public static char getCharOpaque(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return (char) GetOpaque_Java9.getCharOpaque.invokeExact(base, offset);
        } else { //fall back to volatile load
            return getCharVolatile(base, offset);
        }
    }

    @SneakyThrows
    public static int getIntOpaque(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return (int) GetOpaque_Java9.getIntOpaque.invokeExact(base, offset);
        } else { //fall back to volatile load
            return getIntVolatile(base, offset);
        }
    }

    @SneakyThrows
    public static long getLongOpaque(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return (long) GetOpaque_Java9.getLongOpaque.invokeExact(base, offset);
        } else { //fall back to volatile load
            return getLongVolatile(base, offset);
        }
    }

    @SneakyThrows
    public static float getFloatOpaque(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return (float) GetOpaque_Java9.getFloatOpaque.invokeExact(base, offset);
        } else { //fall back to volatile load
            return getFloatVolatile(base, offset);
        }
    }

    @SneakyThrows
    public static double getDoubleOpaque(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return (double) GetOpaque_Java9.getDoubleOpaque.invokeExact(base, offset);
        } else { //fall back to volatile load
            return getDoubleVolatile(base, offset);
        }
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> T getObjectOpaque(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return (T) (Object) GetOpaque_Java9.getObjectOpaque.invokeExact(base, offset);
        } else { //fall back to volatile load
            return getObjectVolatile(base, offset);
        }
    }
    
    //
    // OPAQUE STORES
    //

    private static final class PutOpaque_Java9 {
        static final MethodHandle putBooleanOpaque; // (Object, long, boolean) -> void
        static final MethodHandle putByteOpaque; // (Object, long, byte) -> void
        static final MethodHandle putShortOpaque; // (Object, long, short) -> void
        static final MethodHandle putCharOpaque; // (Object, long, char) -> void
        static final MethodHandle putIntOpaque; // (Object, long, int) -> void
        static final MethodHandle putLongOpaque; // (Object, long, long) -> void
        static final MethodHandle putFloatOpaque; // (Object, long, float) -> void
        static final MethodHandle putDoubleOpaque; // (Object, long, double) -> void
        static final MethodHandle putObjectOpaque; // (Object, long, Object) -> void

        static {
            try {
                putBooleanOpaque = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "putBooleanOpaque", MethodType.methodType(void.class, Object.class, long.class, boolean.class)).bindTo(jdk_internal_misc_Unsafe);
                putByteOpaque = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "putByteOpaque", MethodType.methodType(void.class, Object.class, long.class, byte.class)).bindTo(jdk_internal_misc_Unsafe);
                putShortOpaque = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "putShortOpaque", MethodType.methodType(void.class, Object.class, long.class, short.class)).bindTo(jdk_internal_misc_Unsafe);
                putCharOpaque = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "putCharOpaque", MethodType.methodType(void.class, Object.class, long.class, char.class)).bindTo(jdk_internal_misc_Unsafe);
                putIntOpaque = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "putIntOpaque", MethodType.methodType(void.class, Object.class, long.class, int.class)).bindTo(jdk_internal_misc_Unsafe);
                putLongOpaque = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "putLongOpaque", MethodType.methodType(void.class, Object.class, long.class, long.class)).bindTo(jdk_internal_misc_Unsafe);
                putFloatOpaque = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "putFloatOpaque", MethodType.methodType(void.class, Object.class, long.class, float.class)).bindTo(jdk_internal_misc_Unsafe);
                putDoubleOpaque = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "putDoubleOpaque", MethodType.methodType(void.class, Object.class, long.class, double.class)).bindTo(jdk_internal_misc_Unsafe);
                putObjectOpaque = MethodHandles.publicLookup().findVirtual(jdk_internal_misc_Unsafe.getClass(), "putObjectOpaque", MethodType.methodType(void.class, Object.class, long.class, Object.class)).bindTo(jdk_internal_misc_Unsafe);
            } catch (Throwable t) {
                throw new AssertionError(t);
            }
        }
    }

    @SneakyThrows
    public static void putBooleanOpaque(Object base, long offset, boolean val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PutOpaque_Java9.putBooleanOpaque.invokeExact(base, offset, val);
        } else { //fall back to volatile store
            putBooleanVolatile(base, offset, val);
        }
    }
    
    @SneakyThrows
    public static void putByteOpaque(Object base, long offset, byte val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PutOpaque_Java9.putByteOpaque.invokeExact(base, offset, val);
        } else { //fall back to volatile store
            putByteVolatile(base, offset, val);
        }
    }
    
    @SneakyThrows
    public static void putShortOpaque(Object base, long offset, short val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PutOpaque_Java9.putShortOpaque.invokeExact(base, offset, val);
        } else { //fall back to volatile store
            putShortVolatile(base, offset, val);
        }
    }
    
    @SneakyThrows
    public static void putCharOpaque(Object base, long offset, char val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PutOpaque_Java9.putCharOpaque.invokeExact(base, offset, val);
        } else { //fall back to volatile store
            putCharVolatile(base, offset, val);
        }
    }
    
    @SneakyThrows
    public static void putIntOpaque(Object base, long offset, int val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PutOpaque_Java9.putIntOpaque.invokeExact(base, offset, val);
        } else { //fall back to volatile store
            putIntVolatile(base, offset, val);
        }
    }
    
    @SneakyThrows
    public static void putLongOpaque(Object base, long offset, long val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PutOpaque_Java9.putLongOpaque.invokeExact(base, offset, val);
        } else { //fall back to volatile store
            putLongVolatile(base, offset, val);
        }
    }
    
    @SneakyThrows
    public static void putFloatOpaque(Object base, long offset, float val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PutOpaque_Java9.putFloatOpaque.invokeExact(base, offset, val);
        } else { //fall back to volatile store
            putFloatVolatile(base, offset, val);
        }
    }
    
    @SneakyThrows
    public static void putDoubleOpaque(Object base, long offset, double val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PutOpaque_Java9.putDoubleOpaque.invokeExact(base, offset, val);
        } else { //fall back to volatile store
            putDoubleVolatile(base, offset, val);
        }
    }
    
    @SneakyThrows
    public static void putObjectOpaque(Object base, long offset, Object val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PutOpaque_Java9.putObjectOpaque.invokeExact(base, offset, val);
        } else { //fall back to volatile store
            putObjectVolatile(base, offset, val);
        }
    }

    //
    // OFF-HEAP MEMORY MANAGEMENT
    //

    public static long allocateMemory(long size) {
        return UNSAFE.allocateMemory(size);
    }

    public static long reallocateMemory(long oldAddress, long size) {
        return UNSAFE.reallocateMemory(oldAddress, size);
    }

    public static void freeMemory(long address) {
        UNSAFE.freeMemory(address);
    }

    //
    // MEMORY RANGE OPERATIONS
    //

    public static void setMemory(Object base, long offset, long size, byte val) {
        UNSAFE.setMemory(base, offset, size, val);
    }

    public static void setMemory(long addr, long size, byte val) {
        UNSAFE.setMemory(addr, size, val);
    }

    public static void copyMemory(Object srcBase, long srcOffset, Object dstBase, long dstOffset, long size) {
        UNSAFE.copyMemory(srcBase, srcOffset, dstBase, dstOffset, size);
    }

    public static void copyMemory(long srcAddr, long dstAddr, long length) {
        UNSAFE.copyMemory(null, srcAddr, null, dstAddr, length);
    }

    //
    // CLASS MANAGEMENT
    //

    public static boolean shouldBeInitialized(Class<?> clazz) {
        return UNSAFE.shouldBeInitialized(clazz);
    }

    public static void ensureClassInitialized(Class<?> clazz) {
        UNSAFE.ensureClassInitialized(clazz);
    }

    /**
     * @deprecated this will no longer work on Java 17+
     */
    @Deprecated
    public static Class<?> defineClass(String name, byte[] classBytes, int off, int len, ClassLoader srcLoader, ProtectionDomain domain) {
        return UNSAFE.defineClass(name, classBytes, off, len, srcLoader, domain);
    }

    private static final class DefineClass_Java9 {
        static final MethodHandle MethodHandles$Lookup_defineClass; // (MethodHandles.Lookup, byte[]) -> Class

        static {
            try {
                MethodHandles$Lookup_defineClass = MethodHandles.publicLookup()
                        .findVirtual(MethodHandles.Lookup.class, "defineClass", MethodType.methodType(Class.class, byte[].class));
            } catch (Throwable t) {
                throw new AssertionError("Unable to find java.lang.invoke.MethodHandles$Lookup#defineClass", t);
            }
        }
    }

    /**
     * Defines a class in the same {@link ClassLoader} and with the same {@link ProtectionDomain} as the given host class.
     * <p>
     * Note that the given class data must contain a class whose name must be in the same package as the host class.
     *
     * @param hostLookup a {@link MethodHandles.Lookup} whose {@link MethodHandles.Lookup#lookupClass()} is the host class. Must have {@link MethodHandles.Lookup#PACKAGE package} access!
     * @param name       the class' binary name, e.g. {@code "java.lang.String"}
     * @param data       a {@code byte[]} containing the class data
     * @return the defined {@link Class}
     */
    @SneakyThrows
    public static Class<?> defineClass(MethodHandles.Lookup hostLookup, String name, byte[] data) {
        if (JAVA_VERSION >= 9) {
            //invoke MethodHandles.Lookup#defineClass() directly
            return (Class<?>) DefineClass_Java9.MethodHandles$Lookup_defineClass.invokeExact(hostLookup, data);
        } else {
            //fall back to Unsafe#defineClass()
            if ((hostLookup.lookupModes() & MethodHandles.Lookup.PACKAGE) == 0) {
                throw new IllegalArgumentException("Lookup must have PACKAGE access!");
            }

            return defineClass(name, data, 0, data.length, hostLookup.lookupClass().getClassLoader(), hostLookup.lookupClass().getProtectionDomain());
        }
    }

    /**
     * @deprecated this will no longer work on Java 17+
     */
    @Deprecated
    public static Class<?> defineAnonymousClass(Class<?> hostClass, byte[] data, Object[] constantPoolPatches) {
        return UNSAFE.defineAnonymousClass(hostClass, data, constantPoolPatches);
    }

    private static final class DefineHiddenClass_Java15 {
        static final MethodHandle MethodHandles$Lookup_defineHiddenClass; // (MethodHandles.Lookup, byte[], boolean, MethodHandles.Lookup.ClassOption...) -> MethodHandles.Lookup

        static {
            try {
                Class<?> MethodHandles$Lookup$ClassOption_class = Class.forName("java.lang.invoke.MethodHandles$Lookup$ClassOption");
                Class<?> MethodHandles$Lookup$ClassOption_arrayClass = Array.newInstance(MethodHandles$Lookup$ClassOption_class, 0).getClass();

                MethodHandles$Lookup_defineHiddenClass = MethodHandles.publicLookup()
                        .findVirtual(MethodHandles.Lookup.class, "defineHiddenClass", MethodType.methodType(MethodHandles.Lookup.class, byte[].class, boolean.class, MethodHandles$Lookup$ClassOption_arrayClass));
            } catch (Throwable t) {
                throw new AssertionError("Unable to find java.lang.invoke.MethodHandles$Lookup#defineHiddenClass", t);
            }
        }
    }

    /**
     * Defines a hidden class in the context of the given host class.
     * <p>
     * Hidden classes are defined in the same {@link ClassLoader} as the host class, but cannot be referred to by name. They are eligible for garbage collection once no longer
     * referenced, even if their host {@link ClassLoader} is still referenced. This makes them a more performant alternative to separate {@link ClassLoader} when defining
     * generated classes at runtime.
     * <p>
     * Note that the given class data must contain a class whose name must be in the same package as the host class.
     * <p>
     * Note that for compatibility reasons with older Java versions, and unlike the Java 15 API, the returned {@link MethodHandles.Lookup} is <strong>not</strong> guaranteed
     * to have full privilege or original access. Only public and package-private class members of the hidden class are guaranteed to be accessible with the returned lookup.
     *
     * @param hostLookup a {@link MethodHandles.Lookup} whose {@link MethodHandles.Lookup#lookupClass()} is the host class. Must have full privilege access!
     * @param initialize if {@code true} the class will be initialized
     * @param data       a {@code byte[]} containing the class data
     * @return a {@link MethodHandles.Lookup} whose {@link MethodHandles.Lookup#lookupClass()} is the newly defined class
     */
    @SneakyThrows
    public static MethodHandles.Lookup defineHiddenClass(MethodHandles.Lookup hostLookup, boolean initialize, byte[] data) {
        if (JAVA_VERSION >= 15) {
            //invoke MethodHandles.Lookup#defineHiddenClass() directly
            return (MethodHandles.Lookup) DefineHiddenClass_Java15.MethodHandles$Lookup_defineHiddenClass.invoke(hostLookup, data, initialize);
        } else {
            //fall back to Unsafe#defineAnonymousClass()
            Class<?> clazz = defineAnonymousClass(hostLookup.lookupClass(), data, null);
            if (initialize) {
                ensureClassInitialized(clazz);
            }
            return hostLookup.in(clazz); //this unfortunately won't return a Lookup with original (or even private) access, but that's probably fine for most users
        }
    }

    //
    // UNINITIALIZED CLASS ALLOCATION
    //

    @SuppressWarnings("unchecked")
    @SneakyThrows(InstantiationException.class)
    public static <T> T allocateInstance(Class<T> clazz) {
        return (T) UNSAFE.allocateInstance(clazz);
    }

    //
    // UNINITIALIZED ARRAY ALLOCATION
    //

    private static final MethodHandle allocateUninitializedArray; //(Class<?>, int) -> Object

    static {
        if (JAVA_VERSION >= 9) {
            try {
                allocateUninitializedArray = MethodHandles.lookup()
                        .findVirtual(jdk_internal_misc_Unsafe.getClass(), "allocateUninitializedArray", MethodType.methodType(Object.class, Class.class, int.class))
                        .bindTo(jdk_internal_misc_Unsafe);
            } catch (Throwable t) {
                throw new AssertionError("Unable to find jdk.internal.misc.Unsafe#allocateUninitializedArray", t);
            }
        } else {
            allocateUninitializedArray = null;
        }
    }

    @SneakyThrows
    public static Object allocateUninitializedArray(Class<?> componentType, int length) {
        if (allocateUninitializedArray != null) { //use Java 9 intrinsic if possible
            return allocateUninitializedArray.invokeExact(componentType, length);
        } else { //fallback to creating a zeroed array
            return allocateUninitializedArray0(componentType, length);
        }
    }

    private static Object allocateUninitializedArray0(Class<?> componentType, int length) {
        if (componentType == null) {
            throw new IllegalArgumentException("Component type is null");
        } else if (!componentType.isPrimitive()) {
            throw new IllegalArgumentException("Component type is not primitive");
        } else if (length < 0) {
            throw new IllegalArgumentException("Negative length");
        }

        if (componentType == boolean.class) return new boolean[length];
        if (componentType == byte.class) return new byte[length];
        if (componentType == short.class) return new short[length];
        if (componentType == char.class) return new char[length];
        if (componentType == int.class) return new int[length];
        if (componentType == long.class) return new long[length];
        if (componentType == float.class) return new float[length];
        if (componentType == double.class) return new double[length];
        throw new IllegalArgumentException();
    }

    @SneakyThrows
    public static boolean[] allocateUninitializedBooleanArray(int length) {
        if (allocateUninitializedArray != null) { //use Java 9 intrinsic if possible
            return (boolean[]) allocateUninitializedArray.invokeExact(boolean.class, length);
        } else { //fallback to creating a zeroed array
            return new boolean[length];
        }
    }

    @SneakyThrows
    public static byte[] allocateUninitializedByteArray(int length) {
        if (allocateUninitializedArray != null) { //use Java 9 intrinsic if possible
            return (byte[]) allocateUninitializedArray.invokeExact(byte.class, length);
        } else { //fallback to creating a zeroed array
            return new byte[length];
        }
    }

    @SneakyThrows
    public static short[] allocateUninitializedShortArray(int length) {
        if (allocateUninitializedArray != null) { //use Java 9 intrinsic if possible
            return (short[]) allocateUninitializedArray.invokeExact(short.class, length);
        } else { //fallback to creating a zeroed array
            return new short[length];
        }
    }

    @SneakyThrows
    public static char[] allocateUninitializedCharArray(int length) {
        if (allocateUninitializedArray != null) { //use Java 9 intrinsic if possible
            return (char[]) allocateUninitializedArray.invokeExact(char.class, length);
        } else { //fallback to creating a zeroed array
            return new char[length];
        }
    }

    @SneakyThrows
    public static int[] allocateUninitializedIntArray(int length) {
        if (allocateUninitializedArray != null) { //use Java 9 intrinsic if possible
            return (int[]) allocateUninitializedArray.invokeExact(int.class, length);
        } else { //fallback to creating a zeroed array
            return new int[length];
        }
    }

    @SneakyThrows
    public static long[] allocateUninitializedLongArray(int length) {
        if (allocateUninitializedArray != null) { //use Java 9 intrinsic if possible
            return (long[]) allocateUninitializedArray.invokeExact(long.class, length);
        } else { //fallback to creating a zeroed array
            return new long[length];
        }
    }

    @SneakyThrows
    public static float[] allocateUninitializedFloatArray(int length) {
        if (allocateUninitializedArray != null) { //use Java 9 intrinsic if possible
            return (float[]) allocateUninitializedArray.invokeExact(float.class, length);
        } else { //fallback to creating a zeroed array
            return new float[length];
        }
    }

    @SneakyThrows
    public static double[] allocateUninitializedDoubleArray(int length) {
        if (allocateUninitializedArray != null) { //use Java 9 intrinsic if possible
            return (double[]) allocateUninitializedArray.invokeExact(double.class, length);
        } else { //fallback to creating a zeroed array
            return new double[length];
        }
    }

    //
    // OBJECT MONITOR WITHOUT synchronized
    // TODO: these are removed in Java 9+, what do?
    //

    @Deprecated
    public void monitorEnter(Object o) {
        UNSAFE.monitorEnter(o);
    }

    @Deprecated
    public void monitorExit(Object o) {
        UNSAFE.monitorExit(o);
    }

    @Deprecated
    public boolean tryMonitorEnter(Object o) {
        return UNSAFE.tryMonitorEnter(o);
    }

    //
    // THROW EXCEPTIONS
    //

    public Error throwException(Throwable t) {
        UNSAFE.throwException(t);
        throw new AssertionError("impossible", t); //this code can never be reached
    }

    //
    // THREAD PARKING
    //

    public void park(boolean absolute, long time) {
        UNSAFE.park(absolute, time);
    }

    public void unpark(Thread thread) {
        UNSAFE.unpark(thread);
    }

    //
    // ORDERED MEMORY WRITES
    //

    public void putOrderedInt(Object o, long pos, int val) {
        UNSAFE.putOrderedInt(o, pos, val);
    }

    public void putOrderedLong(Object o, long pos, long val) {
        UNSAFE.putOrderedLong(o, pos, val);
    }

    public void putOrderedObject(Object o, long pos, Object val) {
        UNSAFE.putOrderedObject(o, pos, val);
    }

    //
    // FENCES
    //

    /**
     * Ensures lack of reordering of loads before the fence with loads or stores after the fence.
     *
     * @see Unsafe#loadFence()
     */
    public static void loadFence() {
        UNSAFE.loadFence();
    }

    /**
     * Ensures lack of reordering of stores before the fence with loads or stores after the fence.
     *
     * @see Unsafe#storeFence()
     */
    public static void storeFence() {
        UNSAFE.storeFence();
    }

    /**
     * Ensures lack of reordering of loads or stores before the fence with loads or stores after the fence.
     *
     * @see Unsafe#fullFence()
     */
    public static void fullFence() {
        UNSAFE.fullFence();
    }

    //
    // ATOMIC CAS
    //

    @Deprecated
    public static boolean compareAndSwapInt(Object o, long offset, int expected, int newValue) {
        return compareAndSetInt(o, offset, expected, newValue);
    }

    @Deprecated
    public static boolean compareAndSwapLong(Object o, long offset, long expected, long newValue) {
        return compareAndSetLong(o, offset, expected, newValue);
    }

    @Deprecated
    public static boolean compareAndSwapObject(Object o, long offset, Object expected, Object newValue) {
        return compareAndSetObject(o, offset, expected, newValue);
    }

    //
    // JAVA 9+ ATOMIC UPDATES
    //

    //TODO: some of these have more efficient default implementations (in particular getAndSet*), are they worth adding?
    //TODO: all of these should forward to the new unsafe intrinsics on java 9+
     
    private static boolean byte2bool(byte b) {
        return b != 0;
    }

    private static byte bool2byte(boolean b) {
        return b ? (byte) 1 : (byte) 0;
    }

    // boolean
    // (emulated using byte)

    public static boolean compareAndSetBoolean(Object o, long offset, boolean expected, boolean newValue) {
        return compareAndSetByte(o, offset, bool2byte(expected), bool2byte(newValue));
    }

    public static boolean weakCompareAndSetBoolean(Object o, long offset, boolean expected, boolean newValue) {
        return compareAndSetBoolean(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetBooleanPlain(Object o, long offset, boolean expected, boolean newValue) {
        return weakCompareAndSetBoolean(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetBooleanAcquire(Object o, long offset, boolean expected, boolean newValue) {
        return weakCompareAndSetBoolean(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetBooleanRelease(Object o, long offset, boolean expected, boolean newValue) {
        return weakCompareAndSetBoolean(o, offset, expected, newValue);
    }

    public static boolean compareAndExchangeBoolean(Object o, long offset, boolean expected, boolean newValue) {
        return byte2bool(compareAndExchangeByte(o, offset, bool2byte(expected), bool2byte(newValue)));
    }

    public static boolean compareAndExchangeBooleanAcquire(Object o, long offset, boolean expected, boolean newValue) {
        return compareAndExchangeBoolean(o, offset, expected, newValue);
    }

    public static boolean compareAndExchangeBooleanRelease(Object o, long offset, boolean expected, boolean newValue) {
        return compareAndExchangeBoolean(o, offset, expected, newValue);
    }

    public static boolean getAndSetBoolean(Object o, long offset, boolean newValue) {
        return byte2bool(getAndSetByte(o, offset, bool2byte(newValue)));
    }

    public static boolean getAndSetBooleanRelease(Object o, long offset, boolean newValue) {
        return getAndSetBoolean(o, offset, newValue);
    }

    public static boolean getAndSetBooleanAcquire(Object o, long offset, boolean newValue) {
        return getAndSetBoolean(o, offset, newValue);
    }

    // byte

    public static boolean compareAndSetByte(Object o, long offset, byte expected, byte newValue) {
        return compareAndExchangeByte(o, offset, expected, newValue) == expected;
    }

    public static boolean weakCompareAndSetByte(Object o, long offset, byte expected, byte newValue) {
        return compareAndSetByte(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetBytePlain(Object o, long offset, byte expected, byte newValue) {
        return weakCompareAndSetByte(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetByteAcquire(Object o, long offset, byte expected, byte newValue) {
        return weakCompareAndSetByte(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetByteRelease(Object o, long offset, byte expected, byte newValue) {
        return weakCompareAndSetByte(o, offset, expected, newValue);
    }

    public static byte compareAndExchangeByte(Object o, long offset, byte expected, byte newValue) {
        //emulated CAS using ints
        long wordOffset = offset & ~3;
        int shift = (int) (offset & 3) << 3;
        if (IS_BIG_ENDIAN) {
            shift = 24 - shift;
        }
        int mask = 0xFF << shift;
        int maskedExpected = (expected & 0xFF) << shift;
        int maskedNewValue = (newValue & 0xFF) << shift;
        int fullWord;
        do {
            fullWord = getIntVolatile(o, wordOffset);
            if ((fullWord & mask) != maskedExpected) {
                return (byte) ((fullWord & mask) >> shift);
            }
        } while (!weakCompareAndSetInt(o, wordOffset, fullWord, (fullWord & ~mask) | maskedNewValue));
        return expected;
    }

    public static byte compareAndExchangeByteAcquire(Object o, long offset, byte expected, byte newValue) {
        return compareAndExchangeByte(o, offset, expected, newValue);
    }

    public static byte compareAndExchangeByteRelease(Object o, long offset, byte expected, byte newValue) {
        return compareAndExchangeByte(o, offset, expected, newValue);
    }

    public static byte getAndSetByte(Object o, long offset, byte newValue) {
        byte curr;
        do {
            curr = getByteVolatile(o, offset);
        } while (!weakCompareAndSetByte(o, offset, curr, newValue));
        return curr;
    }

    public static byte getAndSetByteAcquire(Object o, long offset, byte newValue) {
        return getAndSetByte(o, offset, newValue);
    }

    public static byte getAndSetByteRelease(Object o, long offset, byte newValue) {
        return getAndSetByte(o, offset, newValue);
    }

    // short

    public static boolean compareAndSetShort(Object o, long offset, short expected, short newValue) {
        return compareAndExchangeShort(o, offset, expected, newValue) == expected;
    }

    public static boolean weakCompareAndSetShort(Object o, long offset, short expected, short newValue) {
        return compareAndSetShort(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetShortPlain(Object o, long offset, short expected, short newValue) {
        return weakCompareAndSetShort(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetShortAcquire(Object o, long offset, short expected, short newValue) {
        return weakCompareAndSetShort(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetShortRelease(Object o, long offset, short expected, short newValue) {
        return weakCompareAndSetShort(o, offset, expected, newValue);
    }

    public static short compareAndExchangeShort(Object o, long offset, short expected, short newValue) {
        //emulated CAS using ints
        if ((offset & 3) == 3) {
            throw new IllegalArgumentException("Update spans the word, not supported");
        }
        long wordOffset = offset & ~3;
        int shift = (int) (offset & 3) << 3;
        if (IS_BIG_ENDIAN) {
            shift = 16 - shift;
        }
        int mask = 0xFFFF << shift;
        int maskedExpected = (expected & 0xFFFF) << shift;
        int maskedNewValue = (expected & 0xFFFF) << shift;
        int fullWord;
        do {
            fullWord = getIntVolatile(o, wordOffset);
            if ((fullWord & mask) != maskedExpected) {
                return (short) ((fullWord & mask) >> shift);
            }
        } while (!weakCompareAndSetInt(o, wordOffset, fullWord, (fullWord & ~mask) | maskedNewValue));
        return expected;
    }

    public static short compareAndExchangeShortAcquire(Object o, long offset, short expected, short newValue) {
        return compareAndExchangeShort(o, offset, expected, newValue);
    }

    public static short compareAndExchangeShortRelease(Object o, long offset, short expected, short newValue) {
        return compareAndExchangeShort(o, offset, expected, newValue);
    }

    public static short getAndSetShort(Object o, long offset, short newValue) {
        short curr;
        do {
            curr = getShortVolatile(o, offset);
        } while (!weakCompareAndSetShort(o, offset, curr, newValue));
        return curr;
    }

    public static short getAndSetShortAcquire(Object o, long offset, short newValue) {
        return getAndSetShort(o, offset, newValue);
    }

    public static short getAndSetShortRelease(Object o, long offset, short newValue) {
        return getAndSetShort(o, offset, newValue);
    }
    
    // char
    // (emulated using short)

    public static boolean compareAndSetChar(Object o, long offset, char expected, char newValue) {
        return compareAndSetShort(o, offset, (short) expected, (short) newValue);
    }

    public static boolean weakCompareAndSetChar(Object o, long offset, char expected, char newValue) {
        return compareAndSetChar(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetCharPlain(Object o, long offset, char expected, char newValue) {
        return weakCompareAndSetChar(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetCharAcquire(Object o, long offset, char expected, char newValue) {
        return weakCompareAndSetChar(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetCharRelease(Object o, long offset, char expected, char newValue) {
        return weakCompareAndSetChar(o, offset, expected, newValue);
    }

    public static char compareAndExchangeChar(Object o, long offset, char expected, char newValue) {
        return (char) compareAndExchangeShort(o, offset, (short) expected, (short) newValue);
    }

    public static char compareAndExchangeCharAcquire(Object o, long offset, char expected, char newValue) {
        return compareAndExchangeChar(o, offset, expected, newValue);
    }

    public static char compareAndExchangeCharRelease(Object o, long offset, char expected, char newValue) {
        return compareAndExchangeChar(o, offset, expected, newValue);
    }

    public static char getAndSetChar(Object o, long offset, char newValue) {
        return (char) getAndSetShort(o, offset, (short) newValue);
    }

    public static char getAndSetCharRelease(Object o, long offset, char newValue) {
        return getAndSetChar(o, offset, newValue);
    }

    public static char getAndSetCharAcquire(Object o, long offset, char newValue) {
        return getAndSetChar(o, offset, newValue);
    }

    // int

    public static boolean compareAndSetInt(Object o, long offset, int expected, int newValue) {
        return UNSAFE.compareAndSwapInt(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetInt(Object o, long offset, int expected, int newValue) {
        return compareAndSetInt(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetIntPlain(Object o, long offset, int expected, int newValue) {
        return compareAndSetInt(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetIntAcquire(Object o, long offset, int expected, int newValue) {
        return compareAndSetInt(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetIntRelease(Object o, long offset, int expected, int newValue) {
        return compareAndSetInt(o, offset, expected, newValue);
    }

    public static int compareAndExchangeInt(Object o, long offset, int expected, int newValue) {
        int curr;
        do {
            curr = getIntVolatile(o, offset);
        } while (curr == expected && !weakCompareAndSetInt(o, offset, expected, newValue));
        return curr; // either curr != expected (so compare failed and we return curr), or curr == expected and CAS succeeded
    }

    public static int compareAndExchangeIntAcquire(Object o, long offset, int expected, int newValue) {
        return compareAndExchangeInt(o, offset, expected, newValue);
    }

    public static int compareAndExchangeIntRelease(Object o, long offset, int expected, int newValue) {
        return compareAndExchangeInt(o, offset, expected, newValue);
    }

    public static int getAndSetInt(Object o, long offset, int newValue) {
        return UNSAFE.getAndSetInt(o, offset, newValue);
    }

    public static int getAndSetIntAcquire(Object o, long offset, int newValue) {
        return getAndSetInt(o, offset, newValue);
    }

    public static int getAndSetIntRelease(Object o, long offset, int newValue) {
        return getAndSetInt(o, offset, newValue);
    }

    // long

    public static boolean compareAndSetLong(Object o, long offset, long expected, long newValue) {
        return UNSAFE.compareAndSwapLong(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetLong(Object o, long offset, long expected, long newValue) {
        return compareAndSetLong(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetLongPlain(Object o, long offset, long expected, long newValue) {
        return compareAndSetLong(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetLongAcquire(Object o, long offset, long expected, long newValue) {
        return compareAndSetLong(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetLongRelease(Object o, long offset, long expected, long newValue) {
        return compareAndSetLong(o, offset, expected, newValue);
    }

    public static long compareAndExchangeLong(Object o, long offset, long expected, long newValue) {
        long curr;
        do {
            curr = getLongVolatile(o, offset);
        } while (curr == expected && !weakCompareAndSetLong(o, offset, expected, newValue));
        return curr; // either curr != expected (so compare failed and we return curr), or curr == expected and CAS succeeded
    }

    public static long compareAndExchangeLongAcquire(Object o, long offset, long expected, long newValue) {
        return compareAndExchangeLong(o, offset, expected, newValue);
    }

    public static long compareAndExchangeLongRelease(Object o, long offset, long expected, long newValue) {
        return compareAndExchangeLong(o, offset, expected, newValue);
    }

    public static long getAndSetLong(Object o, long offset, long newValue) {
        return UNSAFE.getAndSetLong(o, offset, newValue);
    }

    public static long getAndSetLongAcquire(Object o, long offset, long newValue) {
        return getAndSetLong(o, offset, newValue);
    }

    public static long getAndSetLongRelease(Object o, long offset, long newValue) {
        return getAndSetLong(o, offset, newValue);
    }

    // float
    // (emulated using int)

    public static boolean compareAndSetFloat(Object o, long offset, float expected, float newValue) {
        return compareAndSetInt(o, offset, Float.floatToRawIntBits(expected), Float.floatToRawIntBits(newValue));
    }

    public static boolean weakCompareAndSetFloat(Object o, long offset, float expected, float newValue) {
        return compareAndSetFloat(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetFloatPlain(Object o, long offset, float expected, float newValue) {
        return weakCompareAndSetFloat(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetFloatAcquire(Object o, long offset, float expected, float newValue) {
        return weakCompareAndSetFloat(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetFloatRelease(Object o, long offset, float expected, float newValue) {
        return weakCompareAndSetFloat(o, offset, expected, newValue);
    }

    public static float compareAndExchangeFloat(Object o, long offset, float expected, float newValue) {
        return Float.intBitsToFloat(compareAndExchangeInt(o, offset, Float.floatToRawIntBits(expected), Float.floatToRawIntBits(newValue)));
    }

    public static float compareAndExchangeFloatAcquire(Object o, long offset, float expected, float newValue) {
        return compareAndExchangeFloat(o, offset, expected, newValue);
    }

    public static float compareAndExchangeFloatRelease(Object o, long offset, float expected, float newValue) {
        return compareAndExchangeFloat(o, offset, expected, newValue);
    }

    public static float getAndSetFloat(Object o, long offset, float newValue) {
        return Float.intBitsToFloat(getAndSetInt(o, offset, Float.floatToRawIntBits(newValue)));
    }

    public static float getAndSetFloatRelease(Object o, long offset, float newValue) {
        return getAndSetFloat(o, offset, newValue);
    }

    public static float getAndSetFloatAcquire(Object o, long offset, float newValue) {
        return getAndSetFloat(o, offset, newValue);
    }

    // double
    // (emulated using long)

    public static boolean compareAndSetDouble(Object o, long offset, double expected, double newValue) {
        return compareAndSetLong(o, offset, Double.doubleToRawLongBits(expected), Double.doubleToRawLongBits(newValue));
    }

    public static boolean weakCompareAndSetDouble(Object o, long offset, double expected, double newValue) {
        return compareAndSetDouble(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetDoublePlain(Object o, long offset, double expected, double newValue) {
        return weakCompareAndSetDouble(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetDoubleAcquire(Object o, long offset, double expected, double newValue) {
        return weakCompareAndSetDouble(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetDoubleRelease(Object o, long offset, double expected, double newValue) {
        return weakCompareAndSetDouble(o, offset, expected, newValue);
    }

    public static double compareAndExchangeDouble(Object o, long offset, double expected, double newValue) {
        return Double.longBitsToDouble(compareAndExchangeLong(o, offset, Double.doubleToRawLongBits(expected), Double.doubleToRawLongBits(newValue)));
    }

    public static double compareAndExchangeDoubleAcquire(Object o, long offset, double expected, double newValue) {
        return compareAndExchangeDouble(o, offset, expected, newValue);
    }

    public static double compareAndExchangeDoubleRelease(Object o, long offset, double expected, double newValue) {
        return compareAndExchangeDouble(o, offset, expected, newValue);
    }

    public static double getAndSetDouble(Object o, long offset, double newValue) {
        return Double.longBitsToDouble(getAndSetLong(o, offset, Double.doubleToRawLongBits(newValue)));
    }

    public static double getAndSetDoubleRelease(Object o, long offset, double newValue) {
        return getAndSetDouble(o, offset, newValue);
    }

    public static double getAndSetDoubleAcquire(Object o, long offset, double newValue) {
        return getAndSetDouble(o, offset, newValue);
    }

    // Object

    public static boolean compareAndSetObject(Object o, long offset, Object expected, Object newValue) {
        return UNSAFE.compareAndSwapObject(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetObject(Object o, long offset, Object expected, Object newValue) {
        return compareAndSetObject(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetObjectPlain(Object o, long offset, Object expected, Object newValue) {
        return compareAndSetObject(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetObjectAcquire(Object o, long offset, Object expected, Object newValue) {
        return compareAndSetObject(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetObjectRelease(Object o, long offset, Object expected, Object newValue) {
        return compareAndSetObject(o, offset, expected, newValue);
    }

    @SuppressWarnings("unchecked")
    public static <T> T compareAndExchangeObject(Object o, long offset, Object expected, Object newValue) {
        Object curr;
        do {
            curr = getObjectVolatile(o, offset);
        } while (curr == expected && !weakCompareAndSetObject(o, offset, expected, newValue));
        return (T) curr; // either curr != expected (so compare failed and we return curr), or curr == expected and CAS succeeded
    }

    public static <T> T compareAndExchangeObjectAcquire(Object o, long offset, Object expected, Object newValue) {
        return compareAndExchangeObject(o, offset, expected, newValue);
    }

    public static <T> T compareAndExchangeObjectRelease(Object o, long offset, Object expected, Object newValue) {
        return compareAndExchangeObject(o, offset, expected, newValue);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getAndSetObject(Object o, long offset, Object newValue) {
        return (T) UNSAFE.getAndSetObject(o, offset, newValue);
    }

    public static <T> T getAndSetObjectAcquire(Object o, long offset, Object newValue) {
        return getAndSetObject(o, offset, newValue);
    }

    public static <T> T getAndSetObjectRelease(Object o, long offset, Object newValue) {
        return getAndSetObject(o, offset, newValue);
    }

    //
    // NUMERIC ATOMIC UPDATES
    //
    
    // byte

    public static byte getAndAddByte(Object o, long offset, byte delta) {
        byte curr;
        do {
            curr = getByteVolatile(o, offset);
        } while (!weakCompareAndSetByte(o, offset, curr, (byte) (curr + delta)));
        return curr;
    }

    public static byte getAndAddByteAcquire(Object o, long offset, byte delta) {
        return getAndAddByte(o, offset, delta);
    }

    public static byte getAndAddByteRelease(Object o, long offset, byte delta) {
        return getAndAddByte(o, offset, delta);
    }
    
    // short

    public static short getAndAddShort(Object o, long offset, short delta) {
        short curr;
        do {
            curr = getShortVolatile(o, offset);
        } while (!weakCompareAndSetShort(o, offset, curr, (short) (curr + delta)));
        return curr;
    }

    public static short getAndAddShortAcquire(Object o, long offset, short delta) {
        return getAndAddShort(o, offset, delta);
    }

    public static short getAndAddShortRelease(Object o, long offset, short delta) {
        return getAndAddShort(o, offset, delta);
    }
    
    // char

    public static char getAndAddChar(Object o, long offset, char delta) {
        char curr;
        do {
            curr = getCharVolatile(o, offset);
        } while (!weakCompareAndSetChar(o, offset, curr, (char) (curr + delta)));
        return curr;
    }

    public static char getAndAddCharAcquire(Object o, long offset, char delta) {
        return getAndAddChar(o, offset, delta);
    }

    public static char getAndAddCharRelease(Object o, long offset, char delta) {
        return getAndAddChar(o, offset, delta);
    }
    
    // int

    public static int getAndAddInt(Object o, long offset, int delta) {
        return UNSAFE.getAndAddInt(o, offset, delta);
    }

    public static int getAndAddIntAcquire(Object o, long offset, int delta) {
        return getAndAddInt(o, offset, delta);
    }

    public static int getAndAddIntRelease(Object o, long offset, int delta) {
        return getAndAddInt(o, offset, delta);
    }
    
    // long

    public static long getAndAddLong(Object o, long offset, long delta) {
        return UNSAFE.getAndAddLong(o, offset, delta);
    }

    public static long getAndAddLongAcquire(Object o, long offset, long delta) {
        return getAndAddLong(o, offset, delta);
    }

    public static long getAndAddLongRelease(Object o, long offset, long delta) {
        return getAndAddLong(o, offset, delta);
    }
    
    // float

    public static float getAndAddFloat(Object o, long offset, float delta) {
        //this is a bit funky because conversion between sNaN and qNaN values could cause an infinite loop
        //  unless we use the raw original bits as reference for the CAS
        int currBits;
        float curr;
        do {
            currBits = getIntVolatile(o, offset);
            curr = Float.intBitsToFloat(currBits);
        } while (!weakCompareAndSetInt(o, offset, currBits, Float.floatToRawIntBits(curr + delta)));
        return curr;
    }

    public static float getAndAddFloatAcquire(Object o, long offset, float delta) {
        return getAndAddFloat(o, offset, delta);
    }

    public static float getAndAddFloatRelease(Object o, long offset, float delta) {
        return getAndAddFloat(o, offset, delta);
    }
    
    // double

    public static double getAndAddDouble(Object o, long offset, double delta) {
        //this is a bit funky because conversion between sNaN and qNaN values could cause an infinite loop
        //  unless we use the raw original bits as reference for the CAS
        long currBits;
        double curr;
        do {
            currBits = getLongVolatile(o, offset);
            curr = Double.longBitsToDouble(currBits);
        } while (!weakCompareAndSetLong(o, offset, currBits, Double.doubleToRawLongBits(curr + delta)));
        return curr;
    }

    public static double getAndAddDoubleAcquire(Object o, long offset, double delta) {
        return getAndAddDouble(o, offset, delta);
    }

    public static double getAndAddDoubleRelease(Object o, long offset, double delta) {
        return getAndAddDouble(o, offset, delta);
    }

    //
    // BITWISE ATOMIC UPDATES
    //
      
    // boolean
    // (emulated using byte)

    public static boolean getAndBitwiseOrBoolean(Object o, long offset, boolean mask) {
        return byte2bool(getAndBitwiseOrByte(o, offset, bool2byte(mask)));
    }

    public static boolean getAndBitwiseOrBooleanAcquire(Object o, long offset, boolean mask) {
        return getAndBitwiseOrBoolean(o, offset, mask);
    }

    public static boolean getAndBitwiseOrBooleanRelease(Object o, long offset, boolean mask) {
        return getAndBitwiseOrBoolean(o, offset, mask);
    }

    public static boolean getAndBitwiseAndBoolean(Object o, long offset, boolean mask) {
        return byte2bool(getAndBitwiseAndByte(o, offset, bool2byte(mask)));
    }

    public static boolean getAndBitwiseAndBooleanAcquire(Object o, long offset, boolean mask) {
        return getAndBitwiseAndBoolean(o, offset, mask);
    }

    public static boolean getAndBitwiseAndBooleanRelease(Object o, long offset, boolean mask) {
        return getAndBitwiseAndBoolean(o, offset, mask);
    }

    public static boolean getAndBitwiseXorBoolean(Object o, long offset, boolean mask) {
        return byte2bool(getAndBitwiseXorByte(o, offset, bool2byte(mask)));
    }

    public static boolean getAndBitwiseXorBooleanAcquire(Object o, long offset, boolean mask) {
        return getAndBitwiseXorBoolean(o, offset, mask);
    }

    public static boolean getAndBitwiseXorBooleanRelease(Object o, long offset, boolean mask) {
        return getAndBitwiseXorBoolean(o, offset, mask);
    }
      
    // byte

    public static byte getAndBitwiseOrByte(Object o, long offset, byte mask) {
        byte curr;
        do {
            curr = getByteVolatile(o, offset);
        } while (!weakCompareAndSetByte(o, offset, curr, (byte) (curr | mask)));
        return curr;
    }

    public static byte getAndBitwiseOrByteAcquire(Object o, long offset, byte mask) {
        return getAndBitwiseOrByte(o, offset, mask);
    }

    public static byte getAndBitwiseOrByteRelease(Object o, long offset, byte mask) {
        return getAndBitwiseOrByte(o, offset, mask);
    }

    public static byte getAndBitwiseAndByte(Object o, long offset, byte mask) {
        byte curr;
        do {
            curr = getByteVolatile(o, offset);
        } while (!weakCompareAndSetByte(o, offset, curr, (byte) (curr & mask)));
        return curr;
    }

    public static byte getAndBitwiseAndByteAcquire(Object o, long offset, byte mask) {
        return getAndBitwiseAndByte(o, offset, mask);
    }

    public static byte getAndBitwiseAndByteRelease(Object o, long offset, byte mask) {
        return getAndBitwiseAndByte(o, offset, mask);
    }

    public static byte getAndBitwiseXorByte(Object o, long offset, byte mask) {
        byte curr;
        do {
            curr = getByteVolatile(o, offset);
        } while (!weakCompareAndSetByte(o, offset, curr, (byte) (curr ^ mask)));
        return curr;
    }

    public static byte getAndBitwiseXorByteAcquire(Object o, long offset, byte mask) {
        return getAndBitwiseXorByte(o, offset, mask);
    }

    public static byte getAndBitwiseXorByteRelease(Object o, long offset, byte mask) {
        return getAndBitwiseXorByte(o, offset, mask);
    }

    // short

    public static short getAndBitwiseOrShort(Object o, long offset, short mask) {
        short curr;
        do {
            curr = getShortVolatile(o, offset);
        } while (!weakCompareAndSetShort(o, offset, curr, (short) (curr | mask)));
        return curr;
    }

    public static short getAndBitwiseOrShortAcquire(Object o, long offset, short mask) {
        return getAndBitwiseOrShort(o, offset, mask);
    }

    public static short getAndBitwiseOrShortRelease(Object o, long offset, short mask) {
        return getAndBitwiseOrShort(o, offset, mask);
    }

    public static short getAndBitwiseAndShort(Object o, long offset, short mask) {
        short curr;
        do {
            curr = getShortVolatile(o, offset);
        } while (!weakCompareAndSetShort(o, offset, curr, (short) (curr & mask)));
        return curr;
    }

    public static short getAndBitwiseAndShortAcquire(Object o, long offset, short mask) {
        return getAndBitwiseAndShort(o, offset, mask);
    }

    public static short getAndBitwiseAndShortRelease(Object o, long offset, short mask) {
        return getAndBitwiseAndShort(o, offset, mask);
    }

    public static short getAndBitwiseXorShort(Object o, long offset, short mask) {
        short curr;
        do {
            curr = getShortVolatile(o, offset);
        } while (!weakCompareAndSetShort(o, offset, curr, (short) (curr ^ mask)));
        return curr;
    }

    public static short getAndBitwiseXorShortAcquire(Object o, long offset, short mask) {
        return getAndBitwiseXorShort(o, offset, mask);
    }

    public static short getAndBitwiseXorShortRelease(Object o, long offset, short mask) {
        return getAndBitwiseXorShort(o, offset, mask);
    }
      
    // char
    // (emulated using short)

    public static char getAndBitwiseOrChar(Object o, long offset, char mask) {
        return (char) getAndBitwiseOrShort(o, offset, (short) mask);
    }

    public static char getAndBitwiseOrCharAcquire(Object o, long offset, char mask) {
        return getAndBitwiseOrChar(o, offset, mask);
    }

    public static char getAndBitwiseOrCharRelease(Object o, long offset, char mask) {
        return getAndBitwiseOrChar(o, offset, mask);
    }

    public static char getAndBitwiseAndChar(Object o, long offset, char mask) {
        return (char) getAndBitwiseAndShort(o, offset, (short) mask);
    }

    public static char getAndBitwiseAndCharAcquire(Object o, long offset, char mask) {
        return getAndBitwiseAndChar(o, offset, mask);
    }

    public static char getAndBitwiseAndCharRelease(Object o, long offset, char mask) {
        return getAndBitwiseAndChar(o, offset, mask);
    }

    public static char getAndBitwiseXorChar(Object o, long offset, char mask) {
        return (char) getAndBitwiseXorShort(o, offset, (short) mask);
    }

    public static char getAndBitwiseXorCharAcquire(Object o, long offset, char mask) {
        return getAndBitwiseXorChar(o, offset, mask);
    }

    public static char getAndBitwiseXorCharRelease(Object o, long offset, char mask) {
        return getAndBitwiseXorChar(o, offset, mask);
    }

    // int

    public static int getAndBitwiseOrInt(Object o, long offset, int mask) {
        int curr;
        do {
            curr = getIntVolatile(o, offset);
        } while (!weakCompareAndSetInt(o, offset, curr, curr | mask));
        return curr;
    }

    public static int getAndBitwiseOrIntAcquire(Object o, long offset, int mask) {
        return getAndBitwiseOrInt(o, offset, mask);
    }

    public static int getAndBitwiseOrIntRelease(Object o, long offset, int mask) {
        return getAndBitwiseOrInt(o, offset, mask);
    }

    public static int getAndBitwiseAndInt(Object o, long offset, int mask) {
        int curr;
        do {
            curr = getIntVolatile(o, offset);
        } while (!weakCompareAndSetInt(o, offset, curr, curr & mask));
        return curr;
    }

    public static int getAndBitwiseAndIntAcquire(Object o, long offset, int mask) {
        return getAndBitwiseAndInt(o, offset, mask);
    }

    public static int getAndBitwiseAndIntRelease(Object o, long offset, int mask) {
        return getAndBitwiseAndInt(o, offset, mask);
    }

    public static int getAndBitwiseXorInt(Object o, long offset, int mask) {
        int curr;
        do {
            curr = getIntVolatile(o, offset);
        } while (!weakCompareAndSetInt(o, offset, curr, curr ^ mask));
        return curr;
    }

    public static int getAndBitwiseXorIntAcquire(Object o, long offset, int mask) {
        return getAndBitwiseXorInt(o, offset, mask);
    }

    public static int getAndBitwiseXorIntRelease(Object o, long offset, int mask) {
        return getAndBitwiseXorInt(o, offset, mask);
    }
    
    // long

    public static long getAndBitwiseOrLong(Object o, long offset, long mask) {
        long curr;
        do {
            curr = getLongVolatile(o, offset);
        } while (!weakCompareAndSetLong(o, offset, curr, curr | mask));
        return curr;
    }

    public static long getAndBitwiseOrLongAcquire(Object o, long offset, long mask) {
        return getAndBitwiseOrLong(o, offset, mask);
    }

    public static long getAndBitwiseOrLongRelease(Object o, long offset, long mask) {
        return getAndBitwiseOrLong(o, offset, mask);
    }

    public static long getAndBitwiseAndLong(Object o, long offset, long mask) {
        long curr;
        do {
            curr = getLongVolatile(o, offset);
        } while (!weakCompareAndSetLong(o, offset, curr, curr & mask));
        return curr;
    }

    public static long getAndBitwiseAndLongAcquire(Object o, long offset, long mask) {
        return getAndBitwiseAndLong(o, offset, mask);
    }

    public static long getAndBitwiseAndLongRelease(Object o, long offset, long mask) {
        return getAndBitwiseAndLong(o, offset, mask);
    }

    public static long getAndBitwiseXorLong(Object o, long offset, long mask) {
        long curr;
        do {
            curr = getLongVolatile(o, offset);
        } while (!weakCompareAndSetLong(o, offset, curr, curr ^ mask));
        return curr;
    }

    public static long getAndBitwiseXorLongAcquire(Object o, long offset, long mask) {
        return getAndBitwiseXorLong(o, offset, mask);
    }

    public static long getAndBitwiseXorLongRelease(Object o, long offset, long mask) {
        return getAndBitwiseXorLong(o, offset, mask);
    }

    //
    //
    // custom methods
    //
    //

    public long pork_getOffset(@NonNull Class clazz, @NonNull String fieldName) {
        try {
            return UNSAFE.objectFieldOffset(clazz.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            while ((clazz = clazz.getSuperclass()) != null) {
                try {
                    return UNSAFE.objectFieldOffset(clazz.getDeclaredField(fieldName));
                } catch (NoSuchFieldException e1) {
                    //ignore
                }
            }
            UNSAFE.throwException(e);
            throw new RuntimeException(e);
        }
    }

    public UnsafeStaticField pork_getStaticField(@NonNull Class clazz, @NonNull String fieldName) {
        try {
            return new UnsafeStaticField(clazz, fieldName);
        } catch (NoSuchFieldException e) {
            UNSAFE.throwException(e);
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <V> V pork_swapObject(Object o, long pos, Object newValue) {
        Object v;
        do {
            v = UNSAFE.getObjectVolatile(o, pos);
        } while (!UNSAFE.compareAndSwapObject(o, pos, v, newValue));
        return (V) v;
    }

    public boolean pork_checkSwapIfNonNull(Object o, long pos, Object newValue) {
        Object v;
        do {
            if ((v = UNSAFE.getObjectVolatile(o, pos)) == null) {
                return false;
            }
        } while (!UNSAFE.compareAndSwapObject(o, pos, v, newValue));
        return true;
    }

    @SuppressWarnings("unchecked")
    public <V> V pork_swapIfNonNull(Object o, long pos, Object newValue) {
        Object v;
        do {
            if ((v = UNSAFE.getObjectVolatile(o, pos)) == null) {
                return null;
            }
        } while (!UNSAFE.compareAndSwapObject(o, pos, v, newValue));
        return (V) v;
    }

    /**
     * Gets the memory address of the given direct {@link Buffer}'s contents.
     * <p>
     * If the given {@link Buffer} is not direct, the behavior is undefined.
     *
     * @param buffer the {@link Buffer}
     * @return the {@link Buffer}'s contents' memory address
     */
    public long pork_directBufferAddress(Buffer buffer) {
        assert buffer.isDirect() : "not a direct buffer: " + buffer;

        return PUnsafe.getLong(buffer, DIRECT_BUFFER_ADDRESS_OFFSET);
    }

    //TODO: these methods won't work on Java 9+
    public Object pork_directBufferAttachment(Buffer buffer) {
        return ((DirectBuffer) buffer).attachment();
    }

    public Cleaner pork_directBufferCleaner(Buffer buffer) {
        return ((DirectBuffer) buffer).cleaner();
    }

    public void pork_releaseBuffer(Buffer buffer) {
        if (buffer instanceof DirectBuffer) {
            Cleaner cleaner = pork_directBufferCleaner(buffer);
            if (cleaner != null) {
                cleaner.clean();
            }
        }
    }

    public long pork_allocateMemory(@NonNull Object cleanerTarget, long size) {
        long offset = UNSAFE.allocateMemory(size);
        PCleaner.cleaner(cleanerTarget, offset);
        return offset;
    }

    //
    // UNALIGNED MEMORY ACCESSORS
    //
    // unlike the ordinary get/set methods, which always operate using the native byte order and may or may not support unaligned accesses, these methods will always
    // support unaligned accesses in exchange for potentially being slower. they're intended to be used for (de)serialization, where byte order is important and
    // alignments are likely to differ from what the platform expects.
    //

    // short

    public short getUnalignedShort(long addr) {
        if (UNALIGNED) {
            return getShort(addr);
        } else {
            return IS_BIG_ENDIAN ? getUnalignedShortBE(addr) : getUnalignedShortLE(addr);
        }
    }

    public short getUnalignedShort(Object base, long offset) {
        if (UNALIGNED) {
            return getShort(base, offset);
        } else {
            return IS_BIG_ENDIAN ? getUnalignedShortBE(base, offset) : getUnalignedShortLE(base, offset);
        }
    }

    public void putUnalignedShort(long addr, short val) {
        if (UNALIGNED) {
            putShort(addr, val);
        } else {
            if (IS_BIG_ENDIAN) {
                putUnalignedShortBE(addr, val);
            } else {
                putUnalignedShortLE(addr, val);
            }
        }
    }

    public void putUnalignedShort(Object base, long offset, short val) {
        if (UNALIGNED) {
            putShort(base, offset, val);
        } else {
            if (IS_BIG_ENDIAN) {
                putUnalignedShortBE(base, offset, val);
            } else {
                putUnalignedShortLE(base, offset, val);
            }
        }
    }

    public short getUnalignedShortBE(long addr) {
        if (UNALIGNED) {
            short value = getShort(addr);
            return IS_BIG_ENDIAN ? value : Short.reverseBytes(value);
        } else {
            return (short) ((getByte(addr + 0L) << 8) | (getByte(addr + 1L) & 0xFF));
        }
    }

    public short getUnalignedShortBE(Object base, long offset) {
        if (UNALIGNED) {
            short value = getShort(base, offset);
            return IS_BIG_ENDIAN ? value : Short.reverseBytes(value);
        } else {
            return (short) ((getByte(base, offset + 0L) << 8) | (getByte(base, offset + 1L) & 0xFF));
        }
    }

    public void putUnalignedShortBE(long addr, short val) {
        if (UNALIGNED) {
            putShort(addr, IS_BIG_ENDIAN ? val : Short.reverseBytes(val));
        } else {
            putByte(addr + 0L, (byte) (val >>> 8));
            putByte(addr + 1L, (byte) val);
        }
    }

    public void putUnalignedShortBE(Object base, long offset, short val) {
        if (UNALIGNED) {
            putShort(base, offset, IS_BIG_ENDIAN ? val : Short.reverseBytes(val));
        } else {
            putByte(base, offset + 0L, (byte) (val >>> 8));
            putByte(base, offset + 1L, (byte) val);
        }
    }

    public short getUnalignedShortLE(long addr) {
        if (UNALIGNED) {
            short value = getShort(addr);
            return IS_LITTLE_ENDIAN ? value : Short.reverseBytes(value);
        } else {
            return (short) ((getByte(addr + 0L) & 0xFF) | (getByte(addr + 1L) << 8));
        }
    }

    public short getUnalignedShortLE(Object base, long offset) {
        if (UNALIGNED) {
            short value = getShort(base, offset);
            return IS_LITTLE_ENDIAN ? value : Short.reverseBytes(value);
        } else {
            return (short) ((getByte(base, offset + 0L) & 0xFF) | (getByte(base, offset + 1L) << 8));
        }
    }

    public void putUnalignedShortLE(long addr, short val) {
        if (UNALIGNED) {
            putShort(addr, IS_LITTLE_ENDIAN ? val : Short.reverseBytes(val));
        } else {
            putByte(addr + 0L, (byte) val);
            putByte(addr + 1L, (byte) (val >>> 8));
        }
    }

    public void putUnalignedShortLE(Object base, long offset, short val) {
        if (UNALIGNED) {
            putShort(base, offset, IS_LITTLE_ENDIAN ? val : Short.reverseBytes(val));
        } else {
            putByte(base, offset + 0L, (byte) val);
            putByte(base, offset + 1L, (byte) (val >>> 8));
        }
    }

    // char

    public char getUnalignedChar(long addr) {
        if (UNALIGNED) {
            return getChar(addr);
        } else {
            return IS_BIG_ENDIAN ? getUnalignedCharBE(addr) : getUnalignedCharLE(addr);
        }
    }

    public char getUnalignedChar(Object base, long offset) {
        if (UNALIGNED) {
            return getChar(base, offset);
        } else {
            return IS_BIG_ENDIAN ? getUnalignedCharBE(base, offset) : getUnalignedCharLE(base, offset);
        }
    }

    public void putUnalignedChar(long addr, char val) {
        if (UNALIGNED) {
            putChar(addr, val);
        } else {
            if (IS_BIG_ENDIAN) {
                putUnalignedCharBE(addr, val);
            } else {
                putUnalignedCharLE(addr, val);
            }
        }
    }

    public void putUnalignedChar(Object base, long offset, char val) {
        if (UNALIGNED) {
            putChar(base, offset, val);
        } else {
            if (IS_BIG_ENDIAN) {
                putUnalignedCharBE(base, offset, val);
            } else {
                putUnalignedCharLE(base, offset, val);
            }
        }
    }

    public char getUnalignedCharBE(long addr) {
        if (UNALIGNED) {
            char value = getChar(addr);
            return IS_BIG_ENDIAN ? value : Character.reverseBytes(value);
        } else {
            return (char) ((getByte(addr + 0L) << 8) | (getByte(addr + 1L) & 0xFF));
        }
    }

    public char getUnalignedCharBE(Object base, long offset) {
        if (UNALIGNED) {
            char value = getChar(base, offset);
            return IS_BIG_ENDIAN ? value : Character.reverseBytes(value);
        } else {
            return (char) ((getByte(base, offset + 0L) << 8) | (getByte(base, offset + 1L) & 0xFF));
        }
    }

    public void putUnalignedCharBE(long addr, char val) {
        if (UNALIGNED) {
            putChar(addr, IS_BIG_ENDIAN ? val : Character.reverseBytes(val));
        } else {
            putByte(addr + 0L, (byte) (val >>> 8));
            putByte(addr + 1L, (byte) val);
        }
    }

    public void putUnalignedCharBE(Object base, long offset, char val) {
        if (UNALIGNED) {
            putChar(base, offset, IS_BIG_ENDIAN ? val : Character.reverseBytes(val));
        } else {
            putByte(base, offset + 0L, (byte) (val >>> 8));
            putByte(base, offset + 1L, (byte) val);
        }
    }

    public char getUnalignedCharLE(long addr) {
        if (UNALIGNED) {
            char value = getChar(addr);
            return IS_LITTLE_ENDIAN ? value : Character.reverseBytes(value);
        } else {
            return (char) ((getByte(addr + 0L) & 0xFF) | (getByte(addr + 1L) << 8));
        }
    }

    public char getUnalignedCharLE(Object base, long offset) {
        if (UNALIGNED) {
            char value = getChar(base, offset);
            return IS_LITTLE_ENDIAN ? value : Character.reverseBytes(value);
        } else {
            return (char) ((getByte(base, offset + 0L) & 0xFF) | (getByte(base, offset + 1L) << 8));
        }
    }

    public void putUnalignedCharLE(long addr, char val) {
        if (UNALIGNED) {
            putChar(addr, IS_LITTLE_ENDIAN ? val : Character.reverseBytes(val));
        } else {
            putByte(addr + 0L, (byte) val);
            putByte(addr + 1L, (byte) (val >>> 8));
        }
    }

    public void putUnalignedCharLE(Object base, long offset, char val) {
        if (UNALIGNED) {
            putChar(base, offset, IS_LITTLE_ENDIAN ? val : Character.reverseBytes(val));
        } else {
            putByte(base, offset + 0L, (byte) val);
            putByte(base, offset + 1L, (byte) (val >>> 8));
        }
    }

    // int

    public int getUnalignedInt(long addr) {
        if (UNALIGNED) {
            return getInt(addr);
        } else {
            return IS_BIG_ENDIAN ? getUnalignedIntBE(addr) : getUnalignedIntLE(addr);
        }
    }

    public int getUnalignedInt(Object base, long offset) {
        if (UNALIGNED) {
            return getInt(base, offset);
        } else {
            return IS_BIG_ENDIAN ? getUnalignedIntBE(base, offset) : getUnalignedIntLE(base, offset);
        }
    }

    public void putUnalignedInt(long addr, int val) {
        if (UNALIGNED) {
            putInt(addr, val);
        } else {
            if (IS_BIG_ENDIAN) {
                putUnalignedIntBE(addr, val);
            } else {
                putUnalignedIntLE(addr, val);
            }
        }
    }

    public void putUnalignedInt(Object base, long offset, int val) {
        if (UNALIGNED) {
            putInt(base, offset, val);
        } else {
            if (IS_BIG_ENDIAN) {
                putUnalignedIntBE(base, offset, val);
            } else {
                putUnalignedIntLE(base, offset, val);
            }
        }
    }

    public int getUnalignedIntBE(long addr) {
        if (UNALIGNED) {
            int value = getInt(addr);
            return IS_BIG_ENDIAN ? value : Integer.reverseBytes(value);
        } else {
            return (getByte(addr + 0L) << 24)
                    | ((getByte(addr + 1L) & 0xFF) << 16)
                    | ((getByte(addr + 2L) & 0xFF) << 8)
                    | (getByte(addr + 3L) & 0xFF);
        }
    }

    public int getUnalignedIntBE(Object base, long offset) {
        if (UNALIGNED) {
            int value = getInt(base, offset);
            return IS_BIG_ENDIAN ? value : Integer.reverseBytes(value);
        } else {
            return (getByte(base, offset + 0L) << 24)
                    | ((getByte(base, offset + 1L) & 0xFF) << 16)
                    | ((getByte(base, offset + 2L) & 0xFF) << 8)
                    | (getByte(base, offset + 3L) & 0xFF);
        }
    }

    public void putUnalignedIntBE(long addr, int val) {
        if (UNALIGNED) {
            putInt(addr, IS_BIG_ENDIAN ? val : Integer.reverseBytes(val));
        } else {
            putByte(addr + 0L, (byte) (val >>> 24));
            putByte(addr + 1L, (byte) (val >>> 16));
            putByte(addr + 2L, (byte) (val >>> 8));
            putByte(addr + 3L, (byte) val);
        }
    }

    public void putUnalignedIntBE(Object base, long offset, int val) {
        if (UNALIGNED) {
            putInt(base, offset, IS_BIG_ENDIAN ? val : Integer.reverseBytes(val));
        } else {
            putByte(base, offset + 0L, (byte) (val >>> 24));
            putByte(base, offset + 1L, (byte) (val >>> 16));
            putByte(base, offset + 2L, (byte) (val >>> 8));
            putByte(base, offset + 3L, (byte) val);
        }
    }

    public int getUnalignedIntLE(long addr) {
        if (UNALIGNED) {
            int value = getInt(addr);
            return IS_LITTLE_ENDIAN ? value : Integer.reverseBytes(value);
        } else {
            return (getByte(addr + 0L) & 0xFF)
                    | ((getByte(addr + 1L) & 0xFF) << 8)
                    | ((getByte(addr + 2L) & 0xFF) << 16)
                    | (getByte(addr + 3L) << 24);
        }
    }

    public int getUnalignedIntLE(Object base, long offset) {
        if (UNALIGNED) {
            int value = getInt(base, offset);
            return IS_LITTLE_ENDIAN ? value : Integer.reverseBytes(value);
        } else {
            return (getByte(base, offset + 0L) & 0xFF)
                    | ((getByte(base, offset + 1L) & 0xFF) << 8)
                    | ((getByte(base, offset + 2L) & 0xFF) << 16)
                    | (getByte(base, offset + 3L) << 24);
        }
    }

    public void putUnalignedIntLE(long addr, int val) {
        if (UNALIGNED) {
            putInt(addr, IS_LITTLE_ENDIAN ? val : Integer.reverseBytes(val));
        } else {
            putByte(addr + 0L, (byte) val);
            putByte(addr + 1L, (byte) (val >>> 8));
            putByte(addr + 2L, (byte) (val >>> 16));
            putByte(addr + 3L, (byte) (val >>> 24));
        }
    }

    public void putUnalignedIntLE(Object base, long offset, int val) {
        if (UNALIGNED) {
            putInt(base, offset, IS_LITTLE_ENDIAN ? val : Integer.reverseBytes(val));
        } else {
            putByte(base, offset + 0L, (byte) val);
            putByte(base, offset + 1L, (byte) (val >>> 8));
            putByte(base, offset + 2L, (byte) (val >>> 16));
            putByte(base, offset + 3L, (byte) (val >>> 24));
        }
    }

    // long

    public long getUnalignedLong(long addr) {
        if (UNALIGNED) {
            return getLong(addr);
        } else {
            return IS_BIG_ENDIAN ? getUnalignedLongBE(addr) : getUnalignedLongLE(addr);
        }
    }

    public long getUnalignedLong(Object base, long offset) {
        if (UNALIGNED) {
            return getLong(base, offset);
        } else {
            return IS_BIG_ENDIAN ? getUnalignedLongBE(base, offset) : getUnalignedLongLE(base, offset);
        }
    }

    public void putUnalignedLong(long addr, long val) {
        if (UNALIGNED) {
            putLong(addr, val);
        } else {
            if (IS_BIG_ENDIAN) {
                putUnalignedLongBE(addr, val);
            } else {
                putUnalignedLongLE(addr, val);
            }
        }
    }

    public void putUnalignedLong(Object base, long offset, long val) {
        if (UNALIGNED) {
            putLong(base, offset, val);
        } else {
            if (IS_BIG_ENDIAN) {
                putUnalignedLongBE(base, offset, val);
            } else {
                putUnalignedLongLE(base, offset, val);
            }
        }
    }

    public long getUnalignedLongBE(long addr) {
        if (UNALIGNED) {
            long value = getLong(addr);
            return IS_BIG_ENDIAN ? value : Long.reverseBytes(value);
        } else {
            return ((long) getByte(addr + 0L) << 56L)
                    | ((getByte(addr + 1L) & 0xFFL) << 48L)
                    | ((getByte(addr + 2L) & 0xFFL) << 40L)
                    | ((getByte(addr + 3L) & 0xFFL) << 32L)
                    | ((getByte(addr + 4L) & 0xFFL) << 24L)
                    | ((getByte(addr + 5L) & 0xFFL) << 16L)
                    | ((getByte(addr + 6L) & 0xFFL) << 8L)
                    | (getByte(addr + 7L) & 0xFFL);
        }
    }

    public long getUnalignedLongBE(Object base, long offset) {
        if (UNALIGNED) {
            long value = getLong(base, offset);
            return IS_BIG_ENDIAN ? value : Long.reverseBytes(value);
        } else {
            return ((long) getByte(base, offset + 0L) << 56L)
                    | ((getByte(base, offset + 1L) & 0xFFL) << 48L)
                    | ((getByte(base, offset + 2L) & 0xFFL) << 40L)
                    | ((getByte(base, offset + 3L) & 0xFFL) << 32L)
                    | ((getByte(base, offset + 4L) & 0xFFL) << 24L)
                    | ((getByte(base, offset + 5L) & 0xFFL) << 16L)
                    | ((getByte(base, offset + 6L) & 0xFFL) << 8L)
                    | (getByte(base, offset + 7L) & 0xFFL);
        }
    }

    public void putUnalignedLongBE(long addr, long val) {
        if (UNALIGNED) {
            putLong(addr, IS_BIG_ENDIAN ? val : Long.reverseBytes(val));
        } else {
            putByte(addr + 0L, (byte) (val >>> 56L));
            putByte(addr + 1L, (byte) (val >>> 48L));
            putByte(addr + 2L, (byte) (val >>> 40L));
            putByte(addr + 3L, (byte) (val >>> 32L));
            putByte(addr + 4L, (byte) (val >>> 24L));
            putByte(addr + 5L, (byte) (val >>> 16L));
            putByte(addr + 6L, (byte) (val >>> 8L));
            putByte(addr + 7L, (byte) val);
        }
    }

    public void putUnalignedLongBE(Object base, long offset, long val) {
        if (UNALIGNED) {
            putLong(base, offset, IS_BIG_ENDIAN ? val : Long.reverseBytes(val));
        } else {
            putByte(base, offset + 0L, (byte) (val >>> 56L));
            putByte(base, offset + 1L, (byte) (val >>> 48L));
            putByte(base, offset + 2L, (byte) (val >>> 40L));
            putByte(base, offset + 3L, (byte) (val >>> 32L));
            putByte(base, offset + 4L, (byte) (val >>> 24L));
            putByte(base, offset + 5L, (byte) (val >>> 16L));
            putByte(base, offset + 6L, (byte) (val >>> 8L));
            putByte(base, offset + 7L, (byte) val);
        }
    }

    public long getUnalignedLongLE(long addr) {
        if (UNALIGNED) {
            long value = getLong(addr);
            return IS_LITTLE_ENDIAN ? value : Long.reverseBytes(value);
        } else {
            return (getByte(addr + 0L) & 0xFFL)
                    | ((getByte(addr + 1L) & 0xFFL) << 8L)
                    | ((getByte(addr + 2L) & 0xFFL) << 16L)
                    | ((getByte(addr + 3L) & 0xFFL) << 24L)
                    | ((getByte(addr + 4L) & 0xFFL) << 32L)
                    | ((getByte(addr + 5L) & 0xFFL) << 40L)
                    | ((getByte(addr + 6L) & 0xFFL) << 48L)
                    | ((long) getByte(addr + 7L) << 56L);
        }
    }

    public long getUnalignedLongLE(Object base, long offset) {
        if (UNALIGNED) {
            long value = getLong(base, offset);
            return IS_LITTLE_ENDIAN ? value : Long.reverseBytes(value);
        } else {
            return (getByte(base, offset + 0L) & 0xFFL)
                    | ((getByte(base, offset + 1L) & 0xFFL) << 8L)
                    | ((getByte(base, offset + 2L) & 0xFFL) << 16L)
                    | ((getByte(base, offset + 3L) & 0xFFL) << 24L)
                    | ((getByte(base, offset + 4L) & 0xFFL) << 32L)
                    | ((getByte(base, offset + 5L) & 0xFFL) << 40L)
                    | ((getByte(base, offset + 6L) & 0xFFL) << 48L)
                    | ((long) getByte(base, offset + 7L) << 56L);
        }
    }

    public void putUnalignedLongLE(long addr, long val) {
        if (UNALIGNED) {
            putLong(addr, IS_LITTLE_ENDIAN ? val : Long.reverseBytes(val));
        } else {
            putByte(addr + 0L, (byte) val);
            putByte(addr + 1L, (byte) (val >>> 8L));
            putByte(addr + 2L, (byte) (val >>> 16L));
            putByte(addr + 3L, (byte) (val >>> 24L));
            putByte(addr + 4L, (byte) (val >>> 32L));
            putByte(addr + 5L, (byte) (val >>> 40L));
            putByte(addr + 6L, (byte) (val >>> 48L));
            putByte(addr + 7L, (byte) (val >>> 56L));
        }
    }

    public void putUnalignedLongLE(Object base, long offset, long val) {
        if (UNALIGNED) {
            putLong(base, offset, IS_LITTLE_ENDIAN ? val : Long.reverseBytes(val));
        } else {
            putByte(base, offset + 0L, (byte) val);
            putByte(base, offset + 1L, (byte) (val >>> 8L));
            putByte(base, offset + 2L, (byte) (val >>> 16L));
            putByte(base, offset + 3L, (byte) (val >>> 24L));
            putByte(base, offset + 4L, (byte) (val >>> 32L));
            putByte(base, offset + 5L, (byte) (val >>> 40L));
            putByte(base, offset + 6L, (byte) (val >>> 48L));
            putByte(base, offset + 7L, (byte) (val >>> 56L));
        }
    }

    // float

    public float getUnalignedFloat(long addr) {
        return Float.intBitsToFloat(getUnalignedInt(addr));
    }

    public float getUnalignedFloat(Object base, long offset) {
        return Float.intBitsToFloat(getUnalignedInt(base, offset));
    }

    public void putUnalignedFloat(long addr, float val) {
        putUnalignedInt(addr, Float.floatToRawIntBits(val));
    }

    public void putUnalignedFloat(Object base, long offset, float val) {
        putUnalignedInt(base, offset, Float.floatToRawIntBits(val));
    }

    public float getUnalignedFloatBE(long addr) {
        return Float.intBitsToFloat(getUnalignedIntBE(addr));
    }

    public float getUnalignedFloatBE(Object base, long offset) {
        return Float.intBitsToFloat(getUnalignedIntBE(base, offset));
    }

    public void putUnalignedFloatBE(long addr, float val) {
        putUnalignedIntBE(addr, Float.floatToRawIntBits(val));
    }

    public void putUnalignedFloatBE(Object base, long offset, float val) {
        putUnalignedIntBE(base, offset, Float.floatToRawIntBits(val));
    }

    public float getUnalignedFloatLE(long addr) {
        return Float.intBitsToFloat(getUnalignedIntLE(addr));
    }

    public float getUnalignedFloatLE(Object base, long offset) {
        return Float.intBitsToFloat(getUnalignedIntLE(base, offset));
    }

    public void putUnalignedFloatLE(long addr, float val) {
        putUnalignedIntLE(addr, Float.floatToRawIntBits(val));
    }

    public void putUnalignedFloatLE(Object base, long offset, float val) {
        putUnalignedIntLE(base, offset, Float.floatToRawIntBits(val));
    }

    // double

    public double getUnalignedDouble(long addr) {
        return Double.longBitsToDouble(getUnalignedLong(addr));
    }

    public double getUnalignedDouble(Object base, long offset) {
        return Double.longBitsToDouble(getUnalignedLong(base, offset));
    }

    public void putUnalignedDouble(long addr, double val) {
        putUnalignedLong(addr, Double.doubleToRawLongBits(val));
    }

    public void putUnalignedDouble(Object base, long offset, double val) {
        putUnalignedLong(base, offset, Double.doubleToRawLongBits(val));
    }

    public double getUnalignedDoubleBE(long addr) {
        return Double.longBitsToDouble(getUnalignedLongBE(addr));
    }

    public double getUnalignedDoubleBE(Object base, long offset) {
        return Double.longBitsToDouble(getUnalignedLongBE(base, offset));
    }

    public void putUnalignedDoubleBE(long addr, double val) {
        putUnalignedLongBE(addr, Double.doubleToRawLongBits(val));
    }

    public void putUnalignedDoubleBE(Object base, long offset, double val) {
        putUnalignedLongBE(base, offset, Double.doubleToRawLongBits(val));
    }

    public double getUnalignedDoubleLE(long addr) {
        return Double.longBitsToDouble(getUnalignedLongLE(addr));
    }

    public double getUnalignedDoubleLE(Object base, long offset) {
        return Double.longBitsToDouble(getUnalignedLongLE(base, offset));
    }

    public void putUnalignedDoubleLE(long addr, double val) {
        putUnalignedLongLE(addr, Double.doubleToRawLongBits(val));
    }

    public void putUnalignedDoubleLE(Object base, long offset, double val) {
        putUnalignedLongLE(base, offset, Double.doubleToRawLongBits(val));
    }
}
