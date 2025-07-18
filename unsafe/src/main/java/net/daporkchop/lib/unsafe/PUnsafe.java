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
@SuppressWarnings({ "PointlessArithmeticExpression", "unchecked", "unused", "UnusedReturnValue" })
@UtilityClass
public class PUnsafe {
    /**
     * A reference to {@link Unsafe}.
     */
    final Unsafe sun_misc_Unsafe = AccessController.doPrivileged((PrivilegedAction<Unsafe>) () -> {
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
            throw new AssertionError("Unable to obtain instance of jdk.internal.misc.Unsafe! Try adding '--add-opens java.base/jdk.internal.misc=ALL-UNNAMED' to your JVM arguments.", t);
        }
    });

    @SneakyThrows
    static MethodHandle getNewUnsafeMethod(String name, MethodType type) {
        return MethodHandles.lookup().bind(jdk_internal_misc_Unsafe, name, type);
    }

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
    public final long ARRAY_BOOLEAN_BASE_OFFSET = sun_misc_Unsafe.arrayBaseOffset(boolean[].class);

    /**
     * @deprecated use {@link #arrayByteBaseOffset()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_BYTE_BASE_OFFSET = sun_misc_Unsafe.arrayBaseOffset(byte[].class);

    /**
     * @deprecated use {@link #arrayShortBaseOffset()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_SHORT_BASE_OFFSET = sun_misc_Unsafe.arrayBaseOffset(short[].class);

    /**
     * @deprecated use {@link #arrayCharBaseOffset()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_CHAR_BASE_OFFSET = sun_misc_Unsafe.arrayBaseOffset(char[].class);

    /**
     * @deprecated use {@link #arrayIntBaseOffset()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_INT_BASE_OFFSET = sun_misc_Unsafe.arrayBaseOffset(int[].class);

    /**
     * @deprecated use {@link #arrayLongBaseOffset()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_LONG_BASE_OFFSET = sun_misc_Unsafe.arrayBaseOffset(long[].class);

    /**
     * @deprecated use {@link #arrayFloatBaseOffset()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_FLOAT_BASE_OFFSET = sun_misc_Unsafe.arrayBaseOffset(float[].class);

    /**
     * @deprecated use {@link #arrayDoubleBaseOffset()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_DOUBLE_BASE_OFFSET = sun_misc_Unsafe.arrayBaseOffset(double[].class);

    /**
     * @deprecated use {@link #arrayObjectBaseOffset()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_OBJECT_BASE_OFFSET = sun_misc_Unsafe.arrayBaseOffset(Object[].class);

    /**
     * @deprecated use {@link #arrayBooleanIndexScale()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_BOOLEAN_INDEX_SCALE = sun_misc_Unsafe.arrayIndexScale(boolean[].class);

    /**
     * @deprecated use {@link #arrayByteIndexScale()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_BYTE_INDEX_SCALE = sun_misc_Unsafe.arrayIndexScale(byte[].class);

    /**
     * @deprecated use {@link #arrayShortIndexScale()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_SHORT_INDEX_SCALE = sun_misc_Unsafe.arrayIndexScale(short[].class);

    /**
     * @deprecated use {@link #arrayCharIndexScale()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_CHAR_INDEX_SCALE = sun_misc_Unsafe.arrayIndexScale(char[].class);

    /**
     * @deprecated use {@link #arrayIntIndexScale()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_INT_INDEX_SCALE = sun_misc_Unsafe.arrayIndexScale(int[].class);

    /**
     * @deprecated use {@link #arrayLongIndexScale()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_LONG_INDEX_SCALE = sun_misc_Unsafe.arrayIndexScale(long[].class);

    /**
     * @deprecated use {@link #arrayFloatIndexScale()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_FLOAT_INDEX_SCALE = sun_misc_Unsafe.arrayIndexScale(float[].class);

    /**
     * @deprecated use {@link #arrayDoubleIndexScale()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_DOUBLE_INDEX_SCALE = sun_misc_Unsafe.arrayIndexScale(double[].class);

    /**
     * @deprecated use {@link #arrayObjectIndexScale()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final long ARRAY_OBJECT_INDEX_SCALE = sun_misc_Unsafe.arrayIndexScale(Object[].class);

    /**
     * @deprecated use {@link #addressSize()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final int ADDRESS_SIZE = sun_misc_Unsafe.addressSize();

    /**
     * @deprecated use {@link #pageSize()}
     */
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public final int PAGE_SIZE = sun_misc_Unsafe.pageSize();

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
        return sun_misc_Unsafe.getLoadAverage(loadavg, nelems);
    }

    //
    // FIELD OFFSET ACCESSORS
    //

    public static long objectFieldOffset(Field field) {
        return sun_misc_Unsafe.objectFieldOffset(field);
    }

    //TODO: it seems this isn't supported when running in a graalvm native image
    public static Object staticFieldBase(Field field) {
        return sun_misc_Unsafe.staticFieldBase(field);
    }

    public static long staticFieldOffset(Field field) {
        return sun_misc_Unsafe.staticFieldOffset(field);
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
    // CHECKED ARRAY OFFSET ACCESSORS
    //

    public static long arrayBooleanElementOffsetChecked(boolean[] array, int index) {
        int arrayLength = array.length;
        if (index < 0 || index >= arrayLength) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return index * ARRAY_BOOLEAN_INDEX_SCALE + ARRAY_BOOLEAN_BASE_OFFSET;
    }

    public static long arrayByteElementOffsetChecked(byte[] array, int index) {
        int arrayLength = array.length;
        if (index < 0 || index >= arrayLength) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return index * ARRAY_BYTE_INDEX_SCALE + ARRAY_BYTE_BASE_OFFSET;
    }

    public static long arrayShortElementOffsetChecked(short[] array, int index) {
        int arrayLength = array.length;
        if (index < 0 || index >= arrayLength) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return index * ARRAY_SHORT_INDEX_SCALE + ARRAY_SHORT_BASE_OFFSET;
    }

    public static long arrayCharElementOffsetChecked(char[] array, int index) {
        int arrayLength = array.length;
        if (index < 0 || index >= arrayLength) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return index * ARRAY_CHAR_INDEX_SCALE + ARRAY_CHAR_BASE_OFFSET;
    }

    public static long arrayIntElementOffsetChecked(int[] array, int index) {
        int arrayLength = array.length;
        if (index < 0 || index >= arrayLength) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return index * ARRAY_INT_INDEX_SCALE + ARRAY_INT_BASE_OFFSET;
    }

    public static long arrayLongElementOffsetChecked(long[] array, int index) {
        int arrayLength = array.length;
        if (index < 0 || index >= arrayLength) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return index * ARRAY_LONG_INDEX_SCALE + ARRAY_LONG_BASE_OFFSET;
    }

    public static long arrayFloatElementOffsetChecked(float[] array, int index) {
        int arrayLength = array.length;
        if (index < 0 || index >= arrayLength) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return index * ARRAY_FLOAT_INDEX_SCALE + ARRAY_FLOAT_BASE_OFFSET;
    }

    public static long arrayDoubleElementOffsetChecked(double[] array, int index) {
        int arrayLength = array.length;
        if (index < 0 || index >= arrayLength) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return index * ARRAY_DOUBLE_INDEX_SCALE + ARRAY_DOUBLE_BASE_OFFSET;
    }

    public static long arrayObjectElementOffsetChecked(Object[] array, int index) {
        int arrayLength = array.length;
        if (index < 0 || index >= arrayLength) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return index * ARRAY_OBJECT_INDEX_SCALE + ARRAY_OBJECT_BASE_OFFSET;
    }

    //
    // REGULAR LOADS
    //

    public static boolean getBoolean(Object base, long offset) {
        return sun_misc_Unsafe.getBoolean(base, offset);
    }

    public static byte getByte(Object base, long offset) {
        return sun_misc_Unsafe.getByte(base, offset);
    }

    public static short getShort(Object base, long offset) {
        return sun_misc_Unsafe.getShort(base, offset);
    }

    public static char getChar(Object base, long offset) {
        return sun_misc_Unsafe.getChar(base, offset);
    }

    public static int getInt(Object base, long offset) {
        return sun_misc_Unsafe.getInt(base, offset);
    }

    public static long getLong(Object base, long offset) {
        return sun_misc_Unsafe.getLong(base, offset);
    }

    public static float getFloat(Object base, long offset) {
        return sun_misc_Unsafe.getFloat(base, offset);
    }

    public static double getDouble(Object base, long offset) {
        return sun_misc_Unsafe.getDouble(base, offset);
    }

    public static <T> T getObject(Object base, long offset) {
        return (T) sun_misc_Unsafe.getObject(base, offset);
    }

    public static boolean getBoolean(long address) {
        return sun_misc_Unsafe.getBoolean(null, address);
    }

    public static byte getByte(long address) {
        return sun_misc_Unsafe.getByte(null, address);
    }

    public static short getShort(long address) {
        return sun_misc_Unsafe.getShort(null, address);
    }

    public static char getChar(long address) {
        return sun_misc_Unsafe.getChar(null, address);
    }

    public static int getInt(long address) {
        return sun_misc_Unsafe.getInt(null, address);
    }

    public static long getLong(long address) {
        return sun_misc_Unsafe.getLong(null, address);
    }

    public static float getFloat(long address) {
        return sun_misc_Unsafe.getFloat(null, address);
    }

    public static double getDouble(long address) {
        return sun_misc_Unsafe.getDouble(null, address);
    }
    
    //
    // REGULAR STORES
    //

    public static void putBoolean(Object base, long offset, boolean val) {
        sun_misc_Unsafe.putBoolean(base, offset, val);
    }

    public static void putByte(Object base, long offset, byte val) {
        sun_misc_Unsafe.putByte(base, offset, val);
    }

    public static void putShort(Object base, long offset, short val) {
        sun_misc_Unsafe.putShort(base, offset, val);
    }

    public static void putChar(Object base, long offset, char val) {
        sun_misc_Unsafe.putChar(base, offset, val);
    }

    public static void putInt(Object base, long offset, int val) {
        sun_misc_Unsafe.putInt(base, offset, val);
    }

    public static void putLong(Object base, long offset, long val) {
        sun_misc_Unsafe.putLong(base, offset, val);
    }

    public static void putFloat(Object base, long offset, float val) {
        sun_misc_Unsafe.putFloat(base, offset, val);
    }

    public static void putDouble(Object base, long offset, double val) {
        sun_misc_Unsafe.putDouble(base, offset, val);
    }

    public static void putObject(Object base, long offset, Object val) {
        sun_misc_Unsafe.putObject(base, offset, val);
    }

    public static void putBoolean(long address, boolean val) {
        sun_misc_Unsafe.putBoolean(null, address, val);
    }
    
    public static void putByte(long address, byte val) {
        sun_misc_Unsafe.putByte(null, address, val);
    }
    
    public static void putShort(long address, short val) {
        sun_misc_Unsafe.putShort(null, address, val);
    }
    
    public static void putChar(long address, char val) {
        sun_misc_Unsafe.putChar(null, address, val);
    }
    
    public static void putInt(long address, int val) {
        sun_misc_Unsafe.putInt(null, address, val);
    }
    
    public static void putLong(long address, long val) {
        sun_misc_Unsafe.putLong(null, address, val);
    }
    
    public static void putFloat(long address, float val) {
        sun_misc_Unsafe.putFloat(null, address, val);
    }
    
    public static void putDouble(long address, double val) {
        sun_misc_Unsafe.putDouble(null, address, val);
    }

    //
    // VOLATILE LOADS
    //

    public static boolean getBooleanVolatile(Object base, long offset) {
        return sun_misc_Unsafe.getBooleanVolatile(base, offset);
    }

    public static byte getByteVolatile(Object base, long offset) {
        return sun_misc_Unsafe.getByteVolatile(base, offset);
    }

    public static short getShortVolatile(Object base, long offset) {
        return sun_misc_Unsafe.getShortVolatile(base, offset);
    }

    public static char getCharVolatile(Object base, long offset) {
        return sun_misc_Unsafe.getCharVolatile(base, offset);
    }

    public static int getIntVolatile(Object base, long offset) {
        return sun_misc_Unsafe.getIntVolatile(base, offset);
    }

    public static long getLongVolatile(Object base, long offset) {
        return sun_misc_Unsafe.getLongVolatile(base, offset);
    }

    public static float getFloatVolatile(Object base, long offset) {
        return sun_misc_Unsafe.getFloatVolatile(base, offset);
    }

    public static double getDoubleVolatile(Object base, long offset) {
        return sun_misc_Unsafe.getDoubleVolatile(base, offset);
    }

    public static <T> T getObjectVolatile(Object base, long offset) {
        return (T) sun_misc_Unsafe.getObjectVolatile(base, offset);
    }
    
    //
    // VOLATILE STORES
    //

    public static void putBooleanVolatile(Object base, long offset, boolean val) {
        sun_misc_Unsafe.putBooleanVolatile(base, offset, val);
    }
    
    public static void putByteVolatile(Object base, long offset, byte val) {
        sun_misc_Unsafe.putByteVolatile(base, offset, val);
    }
    
    public static void putShortVolatile(Object base, long offset, short val) {
        sun_misc_Unsafe.putShortVolatile(base, offset, val);
    }
    
    public static void putCharVolatile(Object base, long offset, char val) {
        sun_misc_Unsafe.putCharVolatile(base, offset, val);
    }
    
    public static void putIntVolatile(Object base, long offset, int val) {
        sun_misc_Unsafe.putIntVolatile(base, offset, val);
    }

    public static void putLongVolatile(Object base, long offset, long val) {
        sun_misc_Unsafe.putLongVolatile(base, offset, val);
    }
    
    public static void putFloatVolatile(Object base, long offset, float val) {
        sun_misc_Unsafe.putFloatVolatile(base, offset, val);
    }
    
    public static void putDoubleVolatile(Object base, long offset, double val) {
        sun_misc_Unsafe.putDoubleVolatile(base, offset, val);
    }
    
    public static void putObjectVolatile(Object base, long offset, Object val) {
        sun_misc_Unsafe.putObjectVolatile(base, offset, val);
    }

    //
    // ACQUIRE LOADS
    //

    public static boolean getBooleanAcquire(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getBooleanAcquire(base, offset);
        } else {
            return PUnsafeAtomics_Java8.getBooleanAcquire(base, offset);
        }
    }

    public static byte getByteAcquire(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getByteAcquire(base, offset);
        } else {
            return PUnsafeAtomics_Java8.getByteAcquire(base, offset);
        }
    }

    public static short getShortAcquire(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getShortAcquire(base, offset);
        } else {
            return PUnsafeAtomics_Java8.getShortAcquire(base, offset);
        }
    }

    public static char getCharAcquire(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getCharAcquire(base, offset);
        } else {
            return PUnsafeAtomics_Java8.getCharAcquire(base, offset);
        }
    }

    public static int getIntAcquire(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getIntAcquire(base, offset);
        } else {
            return PUnsafeAtomics_Java8.getIntAcquire(base, offset);
        }
    }

    public static long getLongAcquire(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getLongAcquire(base, offset);
        } else {
            return PUnsafeAtomics_Java8.getLongAcquire(base, offset);
        }
    }

    public static float getFloatAcquire(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getFloatAcquire(base, offset);
        } else {
            return PUnsafeAtomics_Java8.getFloatAcquire(base, offset);
        }
    }

    public static double getDoubleAcquire(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getDoubleAcquire(base, offset);
        } else {
            return PUnsafeAtomics_Java8.getDoubleAcquire(base, offset);
        }
    }

    public static <T> T getObjectAcquire(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return (T) PUnsafeAtomics_Java9.getObjectAcquire(base, offset);
        } else {
            return (T) PUnsafeAtomics_Java8.getObjectAcquire(base, offset);
        }
    }

    //
    // RELEASE STORES
    //

    public static void putBooleanRelease(Object base, long offset, boolean val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PUnsafeAtomics_Java9.putBooleanRelease(base, offset, val);
        } else {
            PUnsafeAtomics_Java8.putBooleanRelease(base, offset, val);
        }
    }

    public static void putByteRelease(Object base, long offset, byte val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PUnsafeAtomics_Java9.putByteRelease(base, offset, val);
        } else {
            PUnsafeAtomics_Java8.putByteRelease(base, offset, val);
        }
    }

    public static void putShortRelease(Object base, long offset, short val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PUnsafeAtomics_Java9.putShortRelease(base, offset, val);
        } else {
            PUnsafeAtomics_Java8.putShortRelease(base, offset, val);
        }
    }

    public static void putCharRelease(Object base, long offset, char val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PUnsafeAtomics_Java9.putCharRelease(base, offset, val);
        } else {
            PUnsafeAtomics_Java8.putCharRelease(base, offset, val);
        }
    }

    public static void putIntRelease(Object base, long offset, int val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PUnsafeAtomics_Java9.putIntRelease(base, offset, val);
        } else {
            PUnsafeAtomics_Java8.putIntRelease(base, offset, val);
        }
    }

    public static void putLongRelease(Object base, long offset, long val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PUnsafeAtomics_Java9.putLongRelease(base, offset, val);
        } else {
            PUnsafeAtomics_Java8.putLongRelease(base, offset, val);
        }
    }

    public static void putFloatRelease(Object base, long offset, float val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PUnsafeAtomics_Java9.putFloatRelease(base, offset, val);
        } else {
            PUnsafeAtomics_Java8.putFloatRelease(base, offset, val);
        }
    }

    public static void putDoubleRelease(Object base, long offset, double val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PUnsafeAtomics_Java9.putDoubleRelease(base, offset, val);
        } else {
            PUnsafeAtomics_Java8.putDoubleRelease(base, offset, val);
        }
    }

    public static void putObjectRelease(Object base, long offset, Object val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PUnsafeAtomics_Java9.putObjectRelease(base, offset, val);
        } else {
            PUnsafeAtomics_Java8.putObjectRelease(base, offset, val);
        }
    }

    //
    // OPAQUE LOADS
    //

    public static boolean getBooleanOpaque(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getBooleanOpaque(base, offset);
        } else {
            return PUnsafeAtomics_Java8.getBooleanOpaque(base, offset);
        }
    }

    public static byte getByteOpaque(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getByteOpaque(base, offset);
        } else {
            return PUnsafeAtomics_Java8.getByteOpaque(base, offset);
        }
    }

    public static short getShortOpaque(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getShortOpaque(base, offset);
        } else {
            return PUnsafeAtomics_Java8.getShortOpaque(base, offset);
        }
    }

    public static char getCharOpaque(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getCharOpaque(base, offset);
        } else {
            return PUnsafeAtomics_Java8.getCharOpaque(base, offset);
        }
    }

    public static int getIntOpaque(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getIntOpaque(base, offset);
        } else {
            return PUnsafeAtomics_Java8.getIntOpaque(base, offset);
        }
    }

    public static long getLongOpaque(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getLongOpaque(base, offset);
        } else {
            return PUnsafeAtomics_Java8.getLongOpaque(base, offset);
        }
    }

    public static float getFloatOpaque(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getFloatOpaque(base, offset);
        } else {
            return PUnsafeAtomics_Java8.getFloatOpaque(base, offset);
        }
    }

    public static double getDoubleOpaque(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getDoubleOpaque(base, offset);
        } else {
            return PUnsafeAtomics_Java8.getDoubleOpaque(base, offset);
        }
    }

    public static <T> T getObjectOpaque(Object base, long offset) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return (T) PUnsafeAtomics_Java9.getObjectOpaque(base, offset);
        } else {
            return (T) PUnsafeAtomics_Java8.getObjectOpaque(base, offset);
        }
    }
    
    //
    // OPAQUE STORES
    //

    public static void putBooleanOpaque(Object base, long offset, boolean val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PUnsafeAtomics_Java9.putBooleanOpaque(base, offset, val);
        } else {
            PUnsafeAtomics_Java8.putBooleanOpaque(base, offset, val);
        }
    }

    public static void putByteOpaque(Object base, long offset, byte val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PUnsafeAtomics_Java9.putByteOpaque(base, offset, val);
        } else {
            PUnsafeAtomics_Java8.putByteOpaque(base, offset, val);
        }
    }

    public static void putShortOpaque(Object base, long offset, short val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PUnsafeAtomics_Java9.putShortOpaque(base, offset, val);
        } else {
            PUnsafeAtomics_Java8.putShortOpaque(base, offset, val);
        }
    }

    public static void putCharOpaque(Object base, long offset, char val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PUnsafeAtomics_Java9.putCharOpaque(base, offset, val);
        } else {
            PUnsafeAtomics_Java8.putCharOpaque(base, offset, val);
        }
    }

    public static void putIntOpaque(Object base, long offset, int val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PUnsafeAtomics_Java9.putIntOpaque(base, offset, val);
        } else {
            PUnsafeAtomics_Java8.putIntOpaque(base, offset, val);
        }
    }

    public static void putLongOpaque(Object base, long offset, long val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PUnsafeAtomics_Java9.putLongOpaque(base, offset, val);
        } else {
            PUnsafeAtomics_Java8.putLongOpaque(base, offset, val);
        }
    }

    public static void putFloatOpaque(Object base, long offset, float val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PUnsafeAtomics_Java9.putFloatOpaque(base, offset, val);
        } else {
            PUnsafeAtomics_Java8.putFloatOpaque(base, offset, val);
        }
    }

    public static void putDoubleOpaque(Object base, long offset, double val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PUnsafeAtomics_Java9.putDoubleOpaque(base, offset, val);
        } else {
            PUnsafeAtomics_Java8.putDoubleOpaque(base, offset, val);
        }
    }

    public static void putObjectOpaque(Object base, long offset, Object val) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            PUnsafeAtomics_Java9.putObjectOpaque(base, offset, val);
        } else {
            PUnsafeAtomics_Java8.putObjectOpaque(base, offset, val);
        }
    }

    //
    // OFF-HEAP MEMORY MANAGEMENT
    //

    public static long allocateMemory(long size) {
        return sun_misc_Unsafe.allocateMemory(size);
    }

    public static long reallocateMemory(long oldAddress, long size) {
        return sun_misc_Unsafe.reallocateMemory(oldAddress, size);
    }

    public static void freeMemory(long address) {
        sun_misc_Unsafe.freeMemory(address);
    }

    //
    // MEMORY RANGE OPERATIONS
    //

    public static void setMemory(Object base, long offset, long size, byte val) {
        sun_misc_Unsafe.setMemory(base, offset, size, val);
    }

    public static void setMemory(long addr, long size, byte val) {
        sun_misc_Unsafe.setMemory(addr, size, val);
    }

    public static void copyMemory(Object srcBase, long srcOffset, Object dstBase, long dstOffset, long size) {
        sun_misc_Unsafe.copyMemory(srcBase, srcOffset, dstBase, dstOffset, size);
    }

    public static void copyMemory(long srcAddr, long dstAddr, long length) {
        sun_misc_Unsafe.copyMemory(null, srcAddr, null, dstAddr, length);
    }

    //
    // CLASS MANAGEMENT
    //

    public static boolean shouldBeInitialized(Class<?> clazz) {
        return sun_misc_Unsafe.shouldBeInitialized(clazz);
    }

    public static void ensureClassInitialized(Class<?> clazz) {
        sun_misc_Unsafe.ensureClassInitialized(clazz);
    }

    /**
     * @deprecated this will no longer work on Java 17+
     */
    @Deprecated
    public static Class<?> defineClass(String name, byte[] classBytes, int off, int len, ClassLoader srcLoader, ProtectionDomain domain) {
        return sun_misc_Unsafe.defineClass(name, classBytes, off, len, srcLoader, domain);
    }

    private static final class DefineClass_Java9 {
        static final MethodHandle MethodHandles$Lookup_defineClass; // (MethodHandles.Lookup, byte[]) -> Class

        static {
            try {
                MethodHandles$Lookup_defineClass = MethodHandles.lookup()
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
        return sun_misc_Unsafe.defineAnonymousClass(hostClass, data, constantPoolPatches);
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

    @SneakyThrows(InstantiationException.class)
    public static <T> T allocateInstance(Class<T> clazz) {
        return (T) sun_misc_Unsafe.allocateInstance(clazz);
    }

    //
    // UNINITIALIZED ARRAY ALLOCATION
    //

    private static final MethodHandle allocateUninitializedArray; //(Class<?>, int) -> Object

    static {
        if (JAVA_VERSION >= 9) {
            try {
                allocateUninitializedArray = getNewUnsafeMethod("allocateUninitializedArray", MethodType.methodType(Object.class, Class.class, int.class));
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

    //we're intentionally using MethodHandle#invoke() instead of invokeExact() for these functions, since we're actually casting the return type to something else

    @SneakyThrows
    public static boolean[] allocateUninitializedBooleanArray(int length) {
        if (allocateUninitializedArray != null) { //use Java 9 intrinsic if possible
            return (boolean[]) allocateUninitializedArray.invoke(boolean.class, length);
        } else { //fallback to creating a zeroed array
            return new boolean[length];
        }
    }

    @SneakyThrows
    public static byte[] allocateUninitializedByteArray(int length) {
        if (allocateUninitializedArray != null) { //use Java 9 intrinsic if possible
            return (byte[]) allocateUninitializedArray.invoke(byte.class, length);
        } else { //fallback to creating a zeroed array
            return new byte[length];
        }
    }

    @SneakyThrows
    public static short[] allocateUninitializedShortArray(int length) {
        if (allocateUninitializedArray != null) { //use Java 9 intrinsic if possible
            return (short[]) allocateUninitializedArray.invoke(short.class, length);
        } else { //fallback to creating a zeroed array
            return new short[length];
        }
    }

    @SneakyThrows
    public static char[] allocateUninitializedCharArray(int length) {
        if (allocateUninitializedArray != null) { //use Java 9 intrinsic if possible
            return (char[]) allocateUninitializedArray.invoke(char.class, length);
        } else { //fallback to creating a zeroed array
            return new char[length];
        }
    }

    @SneakyThrows
    public static int[] allocateUninitializedIntArray(int length) {
        if (allocateUninitializedArray != null) { //use Java 9 intrinsic if possible
            return (int[]) allocateUninitializedArray.invoke(int.class, length);
        } else { //fallback to creating a zeroed array
            return new int[length];
        }
    }

    @SneakyThrows
    public static long[] allocateUninitializedLongArray(int length) {
        if (allocateUninitializedArray != null) { //use Java 9 intrinsic if possible
            return (long[]) allocateUninitializedArray.invoke(long.class, length);
        } else { //fallback to creating a zeroed array
            return new long[length];
        }
    }

    @SneakyThrows
    public static float[] allocateUninitializedFloatArray(int length) {
        if (allocateUninitializedArray != null) { //use Java 9 intrinsic if possible
            return (float[]) allocateUninitializedArray.invoke(float.class, length);
        } else { //fallback to creating a zeroed array
            return new float[length];
        }
    }

    @SneakyThrows
    public static double[] allocateUninitializedDoubleArray(int length) {
        if (allocateUninitializedArray != null) { //use Java 9 intrinsic if possible
            return (double[]) allocateUninitializedArray.invoke(double.class, length);
        } else { //fallback to creating a zeroed array
            return new double[length];
        }
    }

    //
    // OBJECT MONITOR WITHOUT synchronized
    // TODO: these are removed in Java 9+, what do?
    //

    @Deprecated
    public static void monitorEnter(Object o) {
        sun_misc_Unsafe.monitorEnter(o);
    }

    @Deprecated
    public static void monitorExit(Object o) {
        sun_misc_Unsafe.monitorExit(o);
    }

    @Deprecated
    public static boolean tryMonitorEnter(Object o) {
        return sun_misc_Unsafe.tryMonitorEnter(o);
    }

    //
    // THROW EXCEPTIONS
    //

    public static Error throwException(Throwable t) {
        sun_misc_Unsafe.throwException(t);
        throw new AssertionError("impossible", t); //this code can never be reached
    }

    //
    // THREAD PARKING
    //

    public static void park(boolean absolute, long time) {
        sun_misc_Unsafe.park(absolute, time);
    }

    public static void unpark(Thread thread) {
        sun_misc_Unsafe.unpark(thread);
    }

    //
    // ORDERED MEMORY WRITES
    //
 
    /**
     * @deprecated use {@link #putIntRelease}
     */
    @Deprecated
    public static void putOrderedInt(Object o, long pos, int val) {
        sun_misc_Unsafe.putOrderedInt(o, pos, val);
    }

    /**
     * @deprecated use {@link #putLongRelease}
     */
    @Deprecated
    public static void putOrderedLong(Object o, long pos, long val) {
        sun_misc_Unsafe.putOrderedLong(o, pos, val);
    }

    /**
     * @deprecated use {@link #putObjectRelease}
     */
    @Deprecated
    public static void putOrderedObject(Object o, long pos, Object val) {
        sun_misc_Unsafe.putOrderedObject(o, pos, val);
    }

    //
    // FENCES
    //

    /**
     * Ensures lack of reordering of loads before the fence with loads or stores after the fence.
     *
     * @see Unsafe#loadFence()
     * @deprecated prefer using the fence methods in {@link PVarHandle}
     */
    @Deprecated
    public static void loadFence() {
        sun_misc_Unsafe.loadFence();
    }

    /**
     * Ensures lack of reordering of stores before the fence with loads or stores after the fence.
     *
     * @see Unsafe#storeFence()
     * @deprecated prefer using the fence methods in {@link PVarHandle}
     */
    @Deprecated
    public static void storeFence() {
        sun_misc_Unsafe.storeFence();
    }

    /**
     * Ensures lack of reordering of loads or stores before the fence with loads or stores after the fence.
     *
     * @see Unsafe#fullFence()
     * @deprecated prefer using the fence methods in {@link PVarHandle}
     */
    @Deprecated
    public static void fullFence() {
        sun_misc_Unsafe.fullFence();
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

    // boolean

    public static boolean compareAndSetBoolean(Object o, long offset, boolean expected, boolean newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndSetBoolean(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndSetBoolean(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetBoolean(Object o, long offset, boolean expected, boolean newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetBoolean(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetBoolean(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetBooleanPlain(Object o, long offset, boolean expected, boolean newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetBooleanPlain(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetBooleanPlain(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetBooleanAcquire(Object o, long offset, boolean expected, boolean newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetBooleanAcquire(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetBooleanAcquire(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetBooleanRelease(Object o, long offset, boolean expected, boolean newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetBooleanRelease(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetBooleanRelease(o, offset, expected, newValue);
        }
    }

    public static boolean compareAndExchangeBoolean(Object o, long offset, boolean expected, boolean newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndExchangeBoolean(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndExchangeBoolean(o, offset, expected, newValue);
        }
    }

    public static boolean compareAndExchangeBooleanAcquire(Object o, long offset, boolean expected, boolean newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndExchangeBooleanAcquire(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndExchangeBooleanAcquire(o, offset, expected, newValue);
        }
    }

    public static boolean compareAndExchangeBooleanRelease(Object o, long offset, boolean expected, boolean newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndExchangeBooleanRelease(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndExchangeBooleanRelease(o, offset, expected, newValue);
        }
    }

    public static boolean getAndSetBoolean(Object o, long offset, boolean newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndSetBoolean(o, offset, newValue);
        } else {
            return PUnsafeAtomics_Java8.getAndSetBoolean(o, offset, newValue);
        }
    }

    public static boolean getAndSetBooleanRelease(Object o, long offset, boolean newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndSetBooleanRelease(o, offset, newValue);
        } else {
            return PUnsafeAtomics_Java8.getAndSetBooleanRelease(o, offset, newValue);
        }
    }

    public static boolean getAndSetBooleanAcquire(Object o, long offset, boolean newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndSetBooleanAcquire(o, offset, newValue);
        } else {
            return PUnsafeAtomics_Java8.getAndSetBooleanAcquire(o, offset, newValue);
        }
    }

    // byte

    public static boolean compareAndSetByte(Object o, long offset, byte expected, byte newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndSetByte(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndSetByte(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetByte(Object o, long offset, byte expected, byte newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetByte(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetByte(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetBytePlain(Object o, long offset, byte expected, byte newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetBytePlain(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetBytePlain(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetByteAcquire(Object o, long offset, byte expected, byte newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetByteAcquire(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetByteAcquire(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetByteRelease(Object o, long offset, byte expected, byte newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetByteRelease(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetByteRelease(o, offset, expected, newValue);
        }
    }

    public static byte compareAndExchangeByte(Object o, long offset, byte expected, byte newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndExchangeByte(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndExchangeByte(o, offset, expected, newValue);
        }
    }

    public static byte compareAndExchangeByteAcquire(Object o, long offset, byte expected, byte newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndExchangeByteAcquire(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndExchangeByteAcquire(o, offset, expected, newValue);
        }
    }

    public static byte compareAndExchangeByteRelease(Object o, long offset, byte expected, byte newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndExchangeByteRelease(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndExchangeByteRelease(o, offset, expected, newValue);
        }
    }

    public static byte getAndSetByte(Object o, long offset, byte newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndSetByte(o, offset, newValue);
        } else {
            return PUnsafeAtomics_Java8.getAndSetByte(o, offset, newValue);
        }
    }

    public static byte getAndSetByteAcquire(Object o, long offset, byte newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndSetByteAcquire(o, offset, newValue);
        } else {
            return PUnsafeAtomics_Java8.getAndSetByteAcquire(o, offset, newValue);
        }
    }

    public static byte getAndSetByteRelease(Object o, long offset, byte newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndSetByteRelease(o, offset, newValue);
        } else {
            return PUnsafeAtomics_Java8.getAndSetByteRelease(o, offset, newValue);
        }
    }

    // short

    public static boolean compareAndSetShort(Object o, long offset, short expected, short newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndSetShort(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndSetShort(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetShort(Object o, long offset, short expected, short newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetShort(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetShort(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetShortPlain(Object o, long offset, short expected, short newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetShortPlain(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetShortPlain(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetShortAcquire(Object o, long offset, short expected, short newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetShortAcquire(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetShortAcquire(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetShortRelease(Object o, long offset, short expected, short newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetShortRelease(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetShortRelease(o, offset, expected, newValue);
        }
    }

    public static short compareAndExchangeShort(Object o, long offset, short expected, short newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndExchangeShort(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndExchangeShort(o, offset, expected, newValue);
        }
    }

    public static short compareAndExchangeShortAcquire(Object o, long offset, short expected, short newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndExchangeShortAcquire(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndExchangeShortAcquire(o, offset, expected, newValue);
        }
    }

    public static short compareAndExchangeShortRelease(Object o, long offset, short expected, short newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndExchangeShortRelease(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndExchangeShortRelease(o, offset, expected, newValue);
        }
    }

    public static short getAndSetShort(Object o, long offset, short newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndSetShort(o, offset, newValue);
        } else {
            return PUnsafeAtomics_Java8.getAndSetShort(o, offset, newValue);
        }
    }

    public static short getAndSetShortAcquire(Object o, long offset, short newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndSetShortAcquire(o, offset, newValue);
        } else {
            return PUnsafeAtomics_Java8.getAndSetShortAcquire(o, offset, newValue);
        }
    }

    public static short getAndSetShortRelease(Object o, long offset, short newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndSetShortRelease(o, offset, newValue);
        } else {
            return PUnsafeAtomics_Java8.getAndSetShortRelease(o, offset, newValue);
        }
    }
    
    // char

    public static boolean compareAndSetChar(Object o, long offset, char expected, char newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndSetChar(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndSetChar(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetChar(Object o, long offset, char expected, char newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetChar(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetChar(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetCharPlain(Object o, long offset, char expected, char newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetCharPlain(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetCharPlain(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetCharAcquire(Object o, long offset, char expected, char newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetCharAcquire(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetCharAcquire(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetCharRelease(Object o, long offset, char expected, char newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetCharRelease(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetCharRelease(o, offset, expected, newValue);
        }
    }

    public static char compareAndExchangeChar(Object o, long offset, char expected, char newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndExchangeChar(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndExchangeChar(o, offset, expected, newValue);
        }
    }

    public static char compareAndExchangeCharAcquire(Object o, long offset, char expected, char newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndExchangeCharAcquire(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndExchangeCharAcquire(o, offset, expected, newValue);
        }
    }

    public static char compareAndExchangeCharRelease(Object o, long offset, char expected, char newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndExchangeCharRelease(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndExchangeCharRelease(o, offset, expected, newValue);
        }
    }

    public static char getAndSetChar(Object o, long offset, char newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndSetChar(o, offset, newValue);
        } else {
            return PUnsafeAtomics_Java8.getAndSetChar(o, offset, newValue);
        }
    }

    public static char getAndSetCharRelease(Object o, long offset, char newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndSetCharRelease(o, offset, newValue);
        } else {
            return PUnsafeAtomics_Java8.getAndSetCharRelease(o, offset, newValue);
        }
    }

    public static char getAndSetCharAcquire(Object o, long offset, char newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndSetCharAcquire(o, offset, newValue);
        } else {
            return PUnsafeAtomics_Java8.getAndSetCharAcquire(o, offset, newValue);
        }
    }

    // int

    public static boolean compareAndSetInt(Object o, long offset, int expected, int newValue) {
        return sun_misc_Unsafe.compareAndSwapInt(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetInt(Object o, long offset, int expected, int newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetInt(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetInt(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetIntPlain(Object o, long offset, int expected, int newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetIntPlain(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetIntPlain(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetIntAcquire(Object o, long offset, int expected, int newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetIntAcquire(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetIntAcquire(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetIntRelease(Object o, long offset, int expected, int newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetIntRelease(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetIntRelease(o, offset, expected, newValue);
        }
    }

    public static int compareAndExchangeInt(Object o, long offset, int expected, int newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndExchangeInt(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndExchangeInt(o, offset, expected, newValue);
        }
    }

    public static int compareAndExchangeIntAcquire(Object o, long offset, int expected, int newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndExchangeIntAcquire(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndExchangeIntAcquire(o, offset, expected, newValue);
        }
    }

    public static int compareAndExchangeIntRelease(Object o, long offset, int expected, int newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndExchangeIntRelease(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndExchangeIntRelease(o, offset, expected, newValue);
        }
    }

    public static int getAndSetInt(Object o, long offset, int newValue) {
        return sun_misc_Unsafe.getAndSetInt(o, offset, newValue);
    }

    public static int getAndSetIntAcquire(Object o, long offset, int newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndSetIntAcquire(o, offset, newValue);
        } else {
            return PUnsafeAtomics_Java8.getAndSetIntAcquire(o, offset, newValue);
        }
    }

    public static int getAndSetIntRelease(Object o, long offset, int newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndSetIntRelease(o, offset, newValue);
        } else {
            return PUnsafeAtomics_Java8.getAndSetIntRelease(o, offset, newValue);
        }
    }

    // long

    public static boolean compareAndSetLong(Object o, long offset, long expected, long newValue) {
        return sun_misc_Unsafe.compareAndSwapLong(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetLong(Object o, long offset, long expected, long newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetLong(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetLong(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetLongPlain(Object o, long offset, long expected, long newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetLongPlain(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetLongPlain(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetLongAcquire(Object o, long offset, long expected, long newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetLongAcquire(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetLongAcquire(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetLongRelease(Object o, long offset, long expected, long newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetLongRelease(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetLongRelease(o, offset, expected, newValue);
        }
    }

    public static long compareAndExchangeLong(Object o, long offset, long expected, long newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndExchangeLong(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndExchangeLong(o, offset, expected, newValue);
        }
    }

    public static long compareAndExchangeLongAcquire(Object o, long offset, long expected, long newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndExchangeLongAcquire(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndExchangeLongAcquire(o, offset, expected, newValue);
        }
    }

    public static long compareAndExchangeLongRelease(Object o, long offset, long expected, long newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndExchangeLongRelease(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndExchangeLongRelease(o, offset, expected, newValue);
        }
    }

    public static long getAndSetLong(Object o, long offset, long newValue) {
        return sun_misc_Unsafe.getAndSetLong(o, offset, newValue);
    }

    public static long getAndSetLongAcquire(Object o, long offset, long newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndSetLongAcquire(o, offset, newValue);
        } else {
            return PUnsafeAtomics_Java8.getAndSetLongAcquire(o, offset, newValue);
        }
    }

    public static long getAndSetLongRelease(Object o, long offset, long newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndSetLongRelease(o, offset, newValue);
        } else {
            return PUnsafeAtomics_Java8.getAndSetLongRelease(o, offset, newValue);
        }
    }

    // float

    public static boolean compareAndSetFloat(Object o, long offset, float expected, float newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndSetFloat(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndSetFloat(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetFloat(Object o, long offset, float expected, float newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetFloat(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetFloat(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetFloatPlain(Object o, long offset, float expected, float newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetFloatPlain(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetFloatPlain(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetFloatAcquire(Object o, long offset, float expected, float newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetFloatAcquire(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetFloatAcquire(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetFloatRelease(Object o, long offset, float expected, float newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetFloatRelease(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetFloatRelease(o, offset, expected, newValue);
        }
    }

    public static float compareAndExchangeFloat(Object o, long offset, float expected, float newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndExchangeFloat(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndExchangeFloat(o, offset, expected, newValue);
        }
    }

    public static float compareAndExchangeFloatAcquire(Object o, long offset, float expected, float newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndExchangeFloatAcquire(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndExchangeFloatAcquire(o, offset, expected, newValue);
        }
    }

    public static float compareAndExchangeFloatRelease(Object o, long offset, float expected, float newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndExchangeFloatRelease(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndExchangeFloatRelease(o, offset, expected, newValue);
        }
    }

    public static float getAndSetFloat(Object o, long offset, float newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndSetFloat(o, offset, newValue);
        } else {
            return PUnsafeAtomics_Java8.getAndSetFloat(o, offset, newValue);
        }
    }

    public static float getAndSetFloatRelease(Object o, long offset, float newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndSetFloatRelease(o, offset, newValue);
        } else {
            return PUnsafeAtomics_Java8.getAndSetFloatRelease(o, offset, newValue);
        }
    }

    public static float getAndSetFloatAcquire(Object o, long offset, float newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndSetFloatAcquire(o, offset, newValue);
        } else {
            return PUnsafeAtomics_Java8.getAndSetFloatAcquire(o, offset, newValue);
        }
    }

    // double

    public static boolean compareAndSetDouble(Object o, long offset, double expected, double newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndSetDouble(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndSetDouble(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetDouble(Object o, long offset, double expected, double newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetDouble(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetDouble(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetDoublePlain(Object o, long offset, double expected, double newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetDoublePlain(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetDoublePlain(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetDoubleAcquire(Object o, long offset, double expected, double newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetDoubleAcquire(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetDoubleAcquire(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetDoubleRelease(Object o, long offset, double expected, double newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetDoubleRelease(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetDoubleRelease(o, offset, expected, newValue);
        }
    }

    public static double compareAndExchangeDouble(Object o, long offset, double expected, double newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndExchangeDouble(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndExchangeDouble(o, offset, expected, newValue);
        }
    }

    public static double compareAndExchangeDoubleAcquire(Object o, long offset, double expected, double newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndExchangeDoubleAcquire(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndExchangeDoubleAcquire(o, offset, expected, newValue);
        }
    }

    public static double compareAndExchangeDoubleRelease(Object o, long offset, double expected, double newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.compareAndExchangeDoubleRelease(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.compareAndExchangeDoubleRelease(o, offset, expected, newValue);
        }
    }

    public static double getAndSetDouble(Object o, long offset, double newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndSetDouble(o, offset, newValue);
        } else {
            return PUnsafeAtomics_Java8.getAndSetDouble(o, offset, newValue);
        }
    }

    public static double getAndSetDoubleRelease(Object o, long offset, double newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndSetDoubleRelease(o, offset, newValue);
        } else {
            return PUnsafeAtomics_Java8.getAndSetDoubleRelease(o, offset, newValue);
        }
    }

    public static double getAndSetDoubleAcquire(Object o, long offset, double newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndSetDoubleAcquire(o, offset, newValue);
        } else {
            return PUnsafeAtomics_Java8.getAndSetDoubleAcquire(o, offset, newValue);
        }
    }

    // Object

    public static boolean compareAndSetObject(Object o, long offset, Object expected, Object newValue) {
        return sun_misc_Unsafe.compareAndSwapObject(o, offset, expected, newValue);
    }

    public static boolean weakCompareAndSetObject(Object o, long offset, Object expected, Object newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetObject(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetObject(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetObjectPlain(Object o, long offset, Object expected, Object newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetObjectPlain(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetObjectPlain(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetObjectAcquire(Object o, long offset, Object expected, Object newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetObjectAcquire(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetObjectAcquire(o, offset, expected, newValue);
        }
    }

    public static boolean weakCompareAndSetObjectRelease(Object o, long offset, Object expected, Object newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.weakCompareAndSetObjectRelease(o, offset, expected, newValue);
        } else {
            return PUnsafeAtomics_Java8.weakCompareAndSetObjectRelease(o, offset, expected, newValue);
        }
    }

    public static <T> T compareAndExchangeObject(Object o, long offset, Object expected, Object newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return (T) PUnsafeAtomics_Java9.compareAndExchangeObject(o, offset, expected, newValue);
        } else {
            return (T) PUnsafeAtomics_Java8.compareAndExchangeObject(o, offset, expected, newValue);
        }
    }

    public static <T> T compareAndExchangeObjectAcquire(Object o, long offset, Object expected, Object newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return (T) PUnsafeAtomics_Java9.compareAndExchangeObjectAcquire(o, offset, expected, newValue);
        } else {
            return (T) PUnsafeAtomics_Java8.compareAndExchangeObjectAcquire(o, offset, expected, newValue);
        }
    }

    public static <T> T compareAndExchangeObjectRelease(Object o, long offset, Object expected, Object newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return (T) PUnsafeAtomics_Java9.compareAndExchangeObjectRelease(o, offset, expected, newValue);
        } else {
            return (T) PUnsafeAtomics_Java8.compareAndExchangeObjectRelease(o, offset, expected, newValue);
        }
    }

    public static <T> T getAndSetObject(Object o, long offset, Object newValue) {
        return (T) sun_misc_Unsafe.getAndSetObject(o, offset, newValue);
    }

    public static <T> T getAndSetObjectAcquire(Object o, long offset, Object newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return (T) PUnsafeAtomics_Java9.getAndSetObjectAcquire(o, offset, newValue);
        } else {
            return (T) PUnsafeAtomics_Java8.getAndSetObjectAcquire(o, offset, newValue);
        }
    }

    public static <T> T getAndSetObjectRelease(Object o, long offset, Object newValue) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return (T) PUnsafeAtomics_Java9.getAndSetObjectRelease(o, offset, newValue);
        } else {
            return (T) PUnsafeAtomics_Java8.getAndSetObjectRelease(o, offset, newValue);
        }
    }

    //
    // NUMERIC ATOMIC UPDATES
    //
    
    // byte

    public static byte getAndAddByte(Object o, long offset, byte delta) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndAddByte(o, offset, delta);
        } else {
            return PUnsafeAtomics_Java8.getAndAddByte(o, offset, delta);
        }
    }

    public static byte getAndAddByteAcquire(Object o, long offset, byte delta) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndAddByteAcquire(o, offset, delta);
        } else {
            return PUnsafeAtomics_Java8.getAndAddByteAcquire(o, offset, delta);
        }
    }

    public static byte getAndAddByteRelease(Object o, long offset, byte delta) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndAddByteRelease(o, offset, delta);
        } else {
            return PUnsafeAtomics_Java8.getAndAddByteRelease(o, offset, delta);
        }
    }
    
    // short

    public static short getAndAddShort(Object o, long offset, short delta) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndAddShort(o, offset, delta);
        } else {
            return PUnsafeAtomics_Java8.getAndAddShort(o, offset, delta);
        }
    }

    public static short getAndAddShortAcquire(Object o, long offset, short delta) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndAddShortAcquire(o, offset, delta);
        } else {
            return PUnsafeAtomics_Java8.getAndAddShortAcquire(o, offset, delta);
        }
    }

    public static short getAndAddShortRelease(Object o, long offset, short delta) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndAddShortRelease(o, offset, delta);
        } else {
            return PUnsafeAtomics_Java8.getAndAddShortRelease(o, offset, delta);
        }
    }
    
    // char

    public static char getAndAddChar(Object o, long offset, char delta) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndAddChar(o, offset, delta);
        } else {
            return PUnsafeAtomics_Java8.getAndAddChar(o, offset, delta);
        }
    }

    public static char getAndAddCharAcquire(Object o, long offset, char delta) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndAddCharAcquire(o, offset, delta);
        } else {
            return PUnsafeAtomics_Java8.getAndAddCharAcquire(o, offset, delta);
        }
    }

    public static char getAndAddCharRelease(Object o, long offset, char delta) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndAddCharRelease(o, offset, delta);
        } else {
            return PUnsafeAtomics_Java8.getAndAddCharRelease(o, offset, delta);
        }
    }
    
    // int

    public static int getAndAddInt(Object o, long offset, int delta) {
        return sun_misc_Unsafe.getAndAddInt(o, offset, delta);
    }

    public static int getAndAddIntAcquire(Object o, long offset, int delta) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndAddIntAcquire(o, offset, delta);
        } else {
            return PUnsafeAtomics_Java8.getAndAddIntAcquire(o, offset, delta);
        }
    }

    public static int getAndAddIntRelease(Object o, long offset, int delta) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndAddIntRelease(o, offset, delta);
        } else {
            return PUnsafeAtomics_Java8.getAndAddIntRelease(o, offset, delta);
        }
    }
    
    // long

    public static long getAndAddLong(Object o, long offset, long delta) {
        return sun_misc_Unsafe.getAndAddLong(o, offset, delta);
    }

    public static long getAndAddLongAcquire(Object o, long offset, long delta) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndAddLongAcquire(o, offset, delta);
        } else {
            return PUnsafeAtomics_Java8.getAndAddLongAcquire(o, offset, delta);
        }
    }

    public static long getAndAddLongRelease(Object o, long offset, long delta) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndAddLongRelease(o, offset, delta);
        } else {
            return PUnsafeAtomics_Java8.getAndAddLongRelease(o, offset, delta);
        }
    }
    
    // float

    public static float getAndAddFloat(Object o, long offset, float delta) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndAddFloat(o, offset, delta);
        } else {
            return PUnsafeAtomics_Java8.getAndAddFloat(o, offset, delta);
        }
    }

    public static float getAndAddFloatAcquire(Object o, long offset, float delta) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndAddFloatAcquire(o, offset, delta);
        } else {
            return PUnsafeAtomics_Java8.getAndAddFloatAcquire(o, offset, delta);
        }
    }

    public static float getAndAddFloatRelease(Object o, long offset, float delta) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndAddFloatRelease(o, offset, delta);
        } else {
            return PUnsafeAtomics_Java8.getAndAddFloatRelease(o, offset, delta);
        }
    }
    
    // double

    public static double getAndAddDouble(Object o, long offset, double delta) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndAddDouble(o, offset, delta);
        } else {
            return PUnsafeAtomics_Java8.getAndAddDouble(o, offset, delta);
        }
    }

    public static double getAndAddDoubleAcquire(Object o, long offset, double delta) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndAddDoubleAcquire(o, offset, delta);
        } else {
            return PUnsafeAtomics_Java8.getAndAddDoubleAcquire(o, offset, delta);
        }
    }

    public static double getAndAddDoubleRelease(Object o, long offset, double delta) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndAddDoubleRelease(o, offset, delta);
        } else {
            return PUnsafeAtomics_Java8.getAndAddDoubleRelease(o, offset, delta);
        }
    }

    //
    // BITWISE ATOMIC UPDATES
    //
      
    // boolean

    public static boolean getAndBitwiseOrBoolean(Object o, long offset, boolean mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseOrBoolean(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseOrBoolean(o, offset, mask);
        }
    }

    public static boolean getAndBitwiseOrBooleanAcquire(Object o, long offset, boolean mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseOrBooleanAcquire(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseOrBooleanAcquire(o, offset, mask);
        }
    }

    public static boolean getAndBitwiseOrBooleanRelease(Object o, long offset, boolean mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseOrBooleanRelease(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseOrBooleanRelease(o, offset, mask);
        }
    }

    public static boolean getAndBitwiseAndBoolean(Object o, long offset, boolean mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseAndBoolean(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseAndBoolean(o, offset, mask);
        }
    }

    public static boolean getAndBitwiseAndBooleanAcquire(Object o, long offset, boolean mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseAndBooleanAcquire(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseAndBooleanAcquire(o, offset, mask);
        }
    }

    public static boolean getAndBitwiseAndBooleanRelease(Object o, long offset, boolean mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseAndBooleanRelease(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseAndBooleanRelease(o, offset, mask);
        }
    }

    public static boolean getAndBitwiseXorBoolean(Object o, long offset, boolean mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseXorBoolean(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseXorBoolean(o, offset, mask);
        }
    }

    public static boolean getAndBitwiseXorBooleanAcquire(Object o, long offset, boolean mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseXorBooleanAcquire(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseXorBooleanAcquire(o, offset, mask);
        }
    }

    public static boolean getAndBitwiseXorBooleanRelease(Object o, long offset, boolean mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseXorBooleanRelease(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseXorBooleanRelease(o, offset, mask);
        }
    }
      
    // byte

    public static byte getAndBitwiseOrByte(Object o, long offset, byte mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseOrByte(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseOrByte(o, offset, mask);
        }
    }

    public static byte getAndBitwiseOrByteAcquire(Object o, long offset, byte mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseOrByteAcquire(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseOrByteAcquire(o, offset, mask);
        }
    }

    public static byte getAndBitwiseOrByteRelease(Object o, long offset, byte mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseOrByteRelease(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseOrByteRelease(o, offset, mask);
        }
    }

    public static byte getAndBitwiseAndByte(Object o, long offset, byte mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseAndByte(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseAndByte(o, offset, mask);
        }
    }

    public static byte getAndBitwiseAndByteAcquire(Object o, long offset, byte mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseAndByteAcquire(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseAndByteAcquire(o, offset, mask);
        }
    }

    public static byte getAndBitwiseAndByteRelease(Object o, long offset, byte mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseAndByteRelease(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseAndByteRelease(o, offset, mask);
        }
    }

    public static byte getAndBitwiseXorByte(Object o, long offset, byte mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseXorByte(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseXorByte(o, offset, mask);
        }
    }

    public static byte getAndBitwiseXorByteAcquire(Object o, long offset, byte mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseXorByteAcquire(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseXorByteAcquire(o, offset, mask);
        }
    }

    public static byte getAndBitwiseXorByteRelease(Object o, long offset, byte mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseXorByteRelease(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseXorByteRelease(o, offset, mask);
        }
    }

    // short

    public static short getAndBitwiseOrShort(Object o, long offset, short mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseOrShort(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseOrShort(o, offset, mask);
        }
    }

    public static short getAndBitwiseOrShortAcquire(Object o, long offset, short mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseOrShortAcquire(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseOrShortAcquire(o, offset, mask);
        }
    }

    public static short getAndBitwiseOrShortRelease(Object o, long offset, short mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseOrShortRelease(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseOrShortRelease(o, offset, mask);
        }
    }

    public static short getAndBitwiseAndShort(Object o, long offset, short mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseAndShort(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseAndShort(o, offset, mask);
        }
    }

    public static short getAndBitwiseAndShortAcquire(Object o, long offset, short mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseAndShortAcquire(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseAndShortAcquire(o, offset, mask);
        }
    }

    public static short getAndBitwiseAndShortRelease(Object o, long offset, short mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseAndShortRelease(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseAndShortRelease(o, offset, mask);
        }
    }

    public static short getAndBitwiseXorShort(Object o, long offset, short mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseXorShort(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseXorShort(o, offset, mask);
        }
    }

    public static short getAndBitwiseXorShortAcquire(Object o, long offset, short mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseXorShortAcquire(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseXorShortAcquire(o, offset, mask);
        }
    }

    public static short getAndBitwiseXorShortRelease(Object o, long offset, short mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseXorShortRelease(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseXorShortRelease(o, offset, mask);
        }
    }
      
    // char

    public static char getAndBitwiseOrChar(Object o, long offset, char mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseOrChar(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseOrChar(o, offset, mask);
        }
    }

    public static char getAndBitwiseOrCharAcquire(Object o, long offset, char mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseOrCharAcquire(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseOrCharAcquire(o, offset, mask);
        }
    }

    public static char getAndBitwiseOrCharRelease(Object o, long offset, char mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseOrCharRelease(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseOrCharRelease(o, offset, mask);
        }
    }

    public static char getAndBitwiseAndChar(Object o, long offset, char mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseAndChar(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseAndChar(o, offset, mask);
        }
    }

    public static char getAndBitwiseAndCharAcquire(Object o, long offset, char mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseAndCharAcquire(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseAndCharAcquire(o, offset, mask);
        }
    }

    public static char getAndBitwiseAndCharRelease(Object o, long offset, char mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseAndCharRelease(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseAndCharRelease(o, offset, mask);
        }
    }

    public static char getAndBitwiseXorChar(Object o, long offset, char mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseXorChar(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseXorChar(o, offset, mask);
        }
    }

    public static char getAndBitwiseXorCharAcquire(Object o, long offset, char mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseXorCharAcquire(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseXorCharAcquire(o, offset, mask);
        }
    }

    public static char getAndBitwiseXorCharRelease(Object o, long offset, char mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseXorCharRelease(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseXorCharRelease(o, offset, mask);
        }
    }

    // int

    public static int getAndBitwiseOrInt(Object o, long offset, int mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseOrInt(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseOrInt(o, offset, mask);
        }
    }

    public static int getAndBitwiseOrIntAcquire(Object o, long offset, int mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseOrIntAcquire(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseOrIntAcquire(o, offset, mask);
        }
    }

    public static int getAndBitwiseOrIntRelease(Object o, long offset, int mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseOrIntRelease(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseOrIntRelease(o, offset, mask);
        }
    }

    public static int getAndBitwiseAndInt(Object o, long offset, int mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseAndInt(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseAndInt(o, offset, mask);
        }
    }

    public static int getAndBitwiseAndIntAcquire(Object o, long offset, int mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseAndIntAcquire(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseAndIntAcquire(o, offset, mask);
        }
    }

    public static int getAndBitwiseAndIntRelease(Object o, long offset, int mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseAndIntRelease(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseAndIntRelease(o, offset, mask);
        }
    }

    public static int getAndBitwiseXorInt(Object o, long offset, int mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseXorInt(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseXorInt(o, offset, mask);
        }
    }

    public static int getAndBitwiseXorIntAcquire(Object o, long offset, int mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseXorIntAcquire(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseXorIntAcquire(o, offset, mask);
        }
    }

    public static int getAndBitwiseXorIntRelease(Object o, long offset, int mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseXorIntRelease(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseXorIntRelease(o, offset, mask);
        }
    }
    
    // long

    public static long getAndBitwiseOrLong(Object o, long offset, long mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseOrLong(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseOrLong(o, offset, mask);
        }
    }

    public static long getAndBitwiseOrLongAcquire(Object o, long offset, long mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseOrLongAcquire(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseOrLongAcquire(o, offset, mask);
        }
    }

    public static long getAndBitwiseOrLongRelease(Object o, long offset, long mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseOrLongRelease(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseOrLongRelease(o, offset, mask);
        }
    }

    public static long getAndBitwiseAndLong(Object o, long offset, long mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseAndLong(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseAndLong(o, offset, mask);
        }
    }

    public static long getAndBitwiseAndLongAcquire(Object o, long offset, long mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseAndLongAcquire(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseAndLongAcquire(o, offset, mask);
        }
    }

    public static long getAndBitwiseAndLongRelease(Object o, long offset, long mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseAndLongRelease(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseAndLongRelease(o, offset, mask);
        }
    }

    public static long getAndBitwiseXorLong(Object o, long offset, long mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseXorLong(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseXorLong(o, offset, mask);
        }
    }

    public static long getAndBitwiseXorLongAcquire(Object o, long offset, long mask) {
        if (JAVA_VERSION >= 9) { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseXorLongAcquire(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseXorLongAcquire(o, offset, mask);
        }
    }

    public static long getAndBitwiseXorLongRelease(Object o, long offset, long mask) {
        if (JAVA_VERSION >= 9 )  { //use Java 9 intrinsic if possible
            return PUnsafeAtomics_Java9.getAndBitwiseXorLongRelease(o, offset, mask);
        } else {
            return PUnsafeAtomics_Java8.getAndBitwiseXorLongRelease(o, offset, mask);
        }
    }

    //
    //
    // custom methods
    //
    //

    public long pork_getOffset(@NonNull Class clazz, @NonNull String fieldName) {
        try {
            return sun_misc_Unsafe.objectFieldOffset(clazz.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            while ((clazz = clazz.getSuperclass()) != null) {
                try {
                    return sun_misc_Unsafe.objectFieldOffset(clazz.getDeclaredField(fieldName));
                } catch (NoSuchFieldException e1) {
                    //ignore
                }
            }
            sun_misc_Unsafe.throwException(e);
            throw new RuntimeException(e);
        }
    }

    public UnsafeStaticField pork_getStaticField(@NonNull Class clazz, @NonNull String fieldName) {
        try {
            return new UnsafeStaticField(clazz, fieldName);
        } catch (NoSuchFieldException e) {
            sun_misc_Unsafe.throwException(e);
            throw new RuntimeException(e);
        }
    }

    public <V> V pork_swapObject(Object o, long pos, Object newValue) {
        Object v;
        do {
            v = sun_misc_Unsafe.getObjectVolatile(o, pos);
        } while (!sun_misc_Unsafe.compareAndSwapObject(o, pos, v, newValue));
        return (V) v;
    }

    public boolean pork_checkSwapIfNonNull(Object o, long pos, Object newValue) {
        Object v;
        do {
            if ((v = sun_misc_Unsafe.getObjectVolatile(o, pos)) == null) {
                return false;
            }
        } while (!sun_misc_Unsafe.compareAndSwapObject(o, pos, v, newValue));
        return true;
    }

    public <V> V pork_swapIfNonNull(Object o, long pos, Object newValue) {
        Object v;
        do {
            if ((v = sun_misc_Unsafe.getObjectVolatile(o, pos)) == null) {
                return null;
            }
        } while (!sun_misc_Unsafe.compareAndSwapObject(o, pos, v, newValue));
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
        long offset = sun_misc_Unsafe.allocateMemory(size);
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
