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

package net.daporkchop.lib.unsafe;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

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
    private final Unsafe UNSAFE = AccessController.doPrivileged((PrivilegedAction<Unsafe>) () -> {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new AssertionError("Unable to obtain instance of sun.misc.Unsafe", e);
        }
    });

    //
    // INTERNAL
    //

    private final long DIRECT_BUFFER_ADDRESS_OFFSET = AccessController.doPrivileged((PrivilegedAction<Long>) () -> {
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
        boolean unaligned;
        try {
            Class<?> bitsClass = Class.forName("java.nio.Bits", false, ClassLoader.getSystemClassLoader());
            if (UnsafePlatformInfo.JAVA_VERSION >= 9) {
                try {
                    Field field = bitsClass.getDeclaredField(UnsafePlatformInfo.JAVA_VERSION >= 11 ? "UNALIGNED" : "unaligned");
                    if (field.getType() == boolean.class) {
                        return new UnsafeStaticField(field).getBoolean();
                    }
                } catch (NoSuchFieldException e) {
                    //silently ignore exception and continue
                }
            }

            Method unalignedMethod = bitsClass.getDeclaredMethod("unaligned");
            unalignedMethod.setAccessible(true);
            unaligned = (boolean) unalignedMethod.invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | SecurityException e) {
            unaligned = false;
        }

        if (!unaligned) { //unaligned memory access isn't available, check to see if we're on x86
            try {
                //noinspection DynamicRegexReplaceableByCompiledPattern
                unaligned = System.getProperty("os.arch", "").matches("^(i[3-6]86|x86(_64)?|x64|amd64)$");
            } catch (SecurityException e) {
                //silently ignore and continue
            }
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
    public void requireTightlyPackedPrimitiveArrays() throws AssertionError {
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
    public void requireTightlyPackedBooleanArrays() throws AssertionError {
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
    public void requireTightlyPackedByteArrays() throws AssertionError {
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
    public void requireTightlyPackedShortArrays() throws AssertionError {
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
    public void requireTightlyPackedCharArrays() throws AssertionError {
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
    public void requireTightlyPackedIntArrays() throws AssertionError {
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
    public void requireTightlyPackedLongArrays() throws AssertionError {
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
    public void requireTightlyPackedFloatArrays() throws AssertionError {
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
    public void requireTightlyPackedDoubleArrays() throws AssertionError {
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
    public long arrayBooleanBaseOffset() {
        return ARRAY_BOOLEAN_BASE_OFFSET;
    }

    /**
     * @return the value of {@code arrayBaseOffset(byte[].class)}
     */
    public long arrayByteBaseOffset() {
        return ARRAY_BYTE_BASE_OFFSET;
    }

    /**
     * @return the value of {@code arrayBaseOffset(short[].class)}
     */
    public long arrayShortBaseOffset() {
        return ARRAY_SHORT_BASE_OFFSET;
    }

    /**
     * @return the value of {@code arrayBaseOffset(char[].class)}
     */
    public long arrayCharBaseOffset() {
        return ARRAY_CHAR_BASE_OFFSET;
    }

    /**
     * @return the value of {@code arrayBaseOffset(int[].class)}
     */
    public long arrayIntBaseOffset() {
        return ARRAY_INT_BASE_OFFSET;
    }

    /**
     * @return the value of {@code arrayBaseOffset(long[].class)}
     */
    public long arrayLongBaseOffset() {
        return ARRAY_LONG_BASE_OFFSET;
    }

    /**
     * @return the value of {@code arrayBaseOffset(float[].class)}
     */
    public long arrayFloatBaseOffset() {
        return ARRAY_FLOAT_BASE_OFFSET;
    }

    /**
     * @return the value of {@code arrayBaseOffset(double[].class)}
     */
    public long arrayDoubleBaseOffset() {
        return ARRAY_DOUBLE_BASE_OFFSET;
    }

    /**
     * @return the value of {@code arrayBaseOffset(Object[].class)}
     */
    public long arrayObjectBaseOffset() {
        return ARRAY_OBJECT_BASE_OFFSET;
    }

    //
    // ARRAY INDEX SCALES
    //

    /**
     * @return the value of {@code arrayIndexScale(boolean[].class)}
     */
    public long arrayBooleanIndexScale() {
        return ARRAY_BOOLEAN_INDEX_SCALE;
    }

    /**
     * @return the value of {@code arrayIndexScale(byte[].class)}
     */
    public long arrayByteIndexScale() {
        return ARRAY_BYTE_INDEX_SCALE;
    }

    /**
     * @return the value of {@code arrayIndexScale(short[].class)}
     */
    public long arrayShortIndexScale() {
        return ARRAY_SHORT_INDEX_SCALE;
    }

    /**
     * @return the value of {@code arrayIndexScale(char[].class)}
     */
    public long arrayCharIndexScale() {
        return ARRAY_CHAR_INDEX_SCALE;
    }

    /**
     * @return the value of {@code arrayIndexScale(int[].class)}
     */
    public long arrayIntIndexScale() {
        return ARRAY_INT_INDEX_SCALE;
    }

    /**
     * @return the value of {@code arrayIndexScale(long[].class)}
     */
    public long arrayLongIndexScale() {
        return ARRAY_LONG_INDEX_SCALE;
    }

    /**
     * @return the value of {@code arrayIndexScale(float[].class)}
     */
    public long arrayFloatIndexScale() {
        return ARRAY_FLOAT_INDEX_SCALE;
    }

    /**
     * @return the value of {@code arrayIndexScale(double[].class)}
     */
    public long arrayDoubleIndexScale() {
        return ARRAY_DOUBLE_INDEX_SCALE;
    }

    /**
     * @return the value of {@code arrayIndexScale(Object[].class)}
     */
    public long arrayObjectIndexScale() {
        return ARRAY_OBJECT_INDEX_SCALE;
    }

    //
    // ARCHITECTURE INFORMATION
    //

    /**
     * @return the value of {@link Unsafe#addressSize()}.
     */
    public int addressSize() {
        return ADDRESS_SIZE;
    }

    /**
     * @return the value of {@link Unsafe#pageSize()}.
     */
    public int pageSize() {
        return PAGE_SIZE;
    }

    /**
     * @return whether the current system supports unaligned memory access
     */
    public boolean isUnalignedAccessSupported() {
        return UNALIGNED;
    }

    //
    // SYSTEM INFORMATION
    //

    public int getLoadAverage(double[] loadavg, int nelems) {
        return UNSAFE.getLoadAverage(loadavg, nelems);
    }

    //
    // FIELD OFFSET ACCESSORS
    //

    public long objectFieldOffset(Field field) {
        return UNSAFE.objectFieldOffset(field);
    }

    //TODO: it seems this isn't supported when running in a graalvm native image
    public Object staticFieldBase(Field field) {
        return UNSAFE.staticFieldBase(field);
    }

    public long staticFieldOffset(Field field) {
        return UNSAFE.staticFieldOffset(field);
    }

    //
    // ARRAY OFFSET ACCESSORS
    //

    public long arrayBooleanElementOffset(int index) {
        return index * ARRAY_BOOLEAN_INDEX_SCALE + ARRAY_BOOLEAN_BASE_OFFSET;
    }

    public long arrayByteElementOffset(int index) {
        return index * ARRAY_BYTE_INDEX_SCALE + ARRAY_BYTE_BASE_OFFSET;
    }

    public long arrayShortElementOffset(int index) {
        return index * ARRAY_SHORT_INDEX_SCALE + ARRAY_SHORT_BASE_OFFSET;
    }

    public long arrayCharElementOffset(int index) {
        return index * ARRAY_CHAR_INDEX_SCALE + ARRAY_CHAR_BASE_OFFSET;
    }

    public long arrayIntElementOffset(int index) {
        return index * ARRAY_INT_INDEX_SCALE + ARRAY_INT_BASE_OFFSET;
    }

    public long arrayLongElementOffset(int index) {
        return index * ARRAY_LONG_INDEX_SCALE + ARRAY_LONG_BASE_OFFSET;
    }

    public long arrayFloatElementOffset(int index) {
        return index * ARRAY_FLOAT_INDEX_SCALE + ARRAY_FLOAT_BASE_OFFSET;
    }

    public long arrayDoubleElementOffset(int index) {
        return index * ARRAY_DOUBLE_INDEX_SCALE + ARRAY_DOUBLE_BASE_OFFSET;
    }

    public long arrayObjectElementOffset(int index) {
        return index * ARRAY_OBJECT_INDEX_SCALE + ARRAY_OBJECT_BASE_OFFSET;
    }

    //
    // NON-VOLATILE HEAP ACCESSORS
    //

    public boolean getBoolean(Object base, long offset) {
        return UNSAFE.getBoolean(base, offset);
    }

    public void putBoolean(Object base, long offset, boolean val) {
        UNSAFE.putBoolean(base, offset, val);
    }

    public byte getByte(Object base, long offset) {
        return UNSAFE.getByte(base, offset);
    }

    public void putByte(Object base, long offset, byte val) {
        UNSAFE.putByte(base, offset, val);
    }

    public short getShort(Object base, long offset) {
        return UNSAFE.getShort(base, offset);
    }

    public void putShort(Object base, long offset, short val) {
        UNSAFE.putShort(base, offset, val);
    }

    public char getChar(Object base, long offset) {
        return UNSAFE.getChar(base, offset);
    }

    public void putChar(Object base, long offset, char val) {
        UNSAFE.putChar(base, offset, val);
    }

    public int getInt(Object base, long offset) {
        return UNSAFE.getInt(base, offset);
    }

    public void putInt(Object base, long offset, int val) {
        UNSAFE.putInt(base, offset, val);
    }

    public long getLong(Object base, long offset) {
        return UNSAFE.getLong(base, offset);
    }

    public void putLong(Object base, long offset, long val) {
        UNSAFE.putLong(base, offset, val);
    }

    public float getFloat(Object base, long offset) {
        return UNSAFE.getFloat(base, offset);
    }

    public void putFloat(Object base, long offset, float val) {
        UNSAFE.putFloat(base, offset, val);
    }

    public double getDouble(Object base, long offset) {
        return UNSAFE.getDouble(base, offset);
    }

    public void putDouble(Object base, long offset, double val) {
        UNSAFE.putDouble(base, offset, val);
    }

    public <T> T getObject(Object base, long offset) {
        @SuppressWarnings("unchecked")
        T value = (T) UNSAFE.getObject(base, offset);
        return value;
    }

    public void putObject(Object base, long offset, Object val) {
        UNSAFE.putObject(base, offset, val);
    }

    //
    // NON-VOLATILE OFF-HEAP ACCESSORS
    //

    //for some reason the ordinary Unsafe class doesn't have a boolean variant without an object base...
    public boolean getBoolean(long addr) {
        return UNSAFE.getBoolean(null, addr);
    }

    public void putBoolean(long addr, boolean val) {
        UNSAFE.putBoolean(null, addr, val);
    }

    public byte getByte(long addr) {
        return UNSAFE.getByte(addr);
    }

    public void putByte(long addr, byte val) {
        UNSAFE.putByte(addr, val);
    }

    public short getShort(long addr) {
        return UNSAFE.getShort(addr);
    }

    public void putShort(long addr, short val) {
        UNSAFE.putShort(addr, val);
    }

    public char getChar(long addr) {
        return UNSAFE.getChar(addr);
    }

    public void putChar(long addr, char val) {
        UNSAFE.putChar(addr, val);
    }

    public int getInt(long addr) {
        return UNSAFE.getInt(addr);
    }

    public void putInt(long addr, int val) {
        UNSAFE.putInt(addr, val);
    }

    public long getLong(long addr) {
        return UNSAFE.getLong(addr);
    }

    public void putLong(long addr, long val) {
        UNSAFE.putLong(addr, val);
    }

    public float getFloat(long addr) {
        return UNSAFE.getFloat(addr);
    }

    public void putFloat(long addr, float val) {
        UNSAFE.putFloat(addr, val);
    }

    public double getDouble(long addr) {
        return UNSAFE.getDouble(addr);
    }

    public void putDouble(long addr, double val) {
        UNSAFE.putDouble(addr, val);
    }

    public long getAddress(long addr) {
        return UNSAFE.getAddress(addr);
    }

    public void putAddress(long addr, long val) {
        UNSAFE.putAddress(addr, val);
    }

    //
    // VOLATILE HEAP ACCESSORS
    //

    public boolean getBooleanVolatile(Object base, long offset) {
        return UNSAFE.getBooleanVolatile(base, offset);
    }

    public void putBooleanVolatile(Object base, long offset, boolean val) {
        UNSAFE.putBooleanVolatile(base, offset, val);
    }

    public byte getByteVolatile(Object base, long offset) {
        return UNSAFE.getByteVolatile(base, offset);
    }

    public void putByteVolatile(Object base, long offset, byte val) {
        UNSAFE.putByteVolatile(base, offset, val);
    }

    public short getShortVolatile(Object base, long offset) {
        return UNSAFE.getShortVolatile(base, offset);
    }

    public void putShortVolatile(Object base, long offset, short val) {
        UNSAFE.putShortVolatile(base, offset, val);
    }

    public char getCharVolatile(Object base, long offset) {
        return UNSAFE.getCharVolatile(base, offset);
    }

    public void putCharVolatile(Object base, long offset, char val) {
        UNSAFE.putCharVolatile(base, offset, val);
    }

    public int getIntVolatile(Object base, long offset) {
        return UNSAFE.getIntVolatile(base, offset);
    }

    public void putIntVolatile(Object base, long offset, int val) {
        UNSAFE.putIntVolatile(base, offset, val);
    }

    public long getLongVolatile(Object base, long offset) {
        return UNSAFE.getLongVolatile(base, offset);
    }

    public void putLongVolatile(Object base, long offset, long val) {
        UNSAFE.putLongVolatile(base, offset, val);
    }

    public float getFloatVolatile(Object base, long offset) {
        return UNSAFE.getFloatVolatile(base, offset);
    }

    public void putFloatVolatile(Object base, long offset, float val) {
        UNSAFE.putFloatVolatile(base, offset, val);
    }

    public double getDoubleVolatile(Object base, long offset) {
        return UNSAFE.getDoubleVolatile(base, offset);
    }

    public void putDoubleVolatile(Object base, long offset, double val) {
        UNSAFE.putDoubleVolatile(base, offset, val);
    }

    public <T> T getObjectVolatile(Object base, long offset) {
        @SuppressWarnings("unchecked")
        T val = (T) UNSAFE.getObjectVolatile(base, offset);
        return val;
    }

    public void putObjectVolatile(Object base, long offset, Object val) {
        UNSAFE.putObjectVolatile(base, offset, val);
    }

    //
    // VOLATILE OFF-HEAP ACCESSORS
    //

    public boolean getBooleanVolatile(long addr) {
        return UNSAFE.getBooleanVolatile(null, addr);
    }

    public void putBooleanVolatile(long addr, boolean val) {
        UNSAFE.putBooleanVolatile(null, addr, val);
    }

    public byte getByteVolatile(long addr) {
        return UNSAFE.getByteVolatile(null, addr);
    }

    public void putByteVolatile(long addr, byte val) {
        UNSAFE.putByteVolatile(null, addr, val);
    }

    public short getShortVolatile(long addr) {
        return UNSAFE.getShortVolatile(null, addr);
    }

    public void putShortVolatile(long addr, short val) {
        UNSAFE.putShortVolatile(null, addr, val);
    }

    public char getCharVolatile(long addr) {
        return UNSAFE.getCharVolatile(null, addr);
    }

    public void putCharVolatile(long addr, char val) {
        UNSAFE.putCharVolatile(null, addr, val);
    }

    public int getIntVolatile(long addr) {
        return UNSAFE.getIntVolatile(null, addr);
    }

    public void putIntVolatile(long addr, int val) {
        UNSAFE.putIntVolatile(null, addr, val);
    }

    public long getLongVolatile(long addr) {
        return UNSAFE.getLongVolatile(null, addr);
    }

    public void putLongVolatile(long addr, long val) {
        UNSAFE.putLongVolatile(null, addr, val);
    }

    public float getFloatVolatile(long addr) {
        return UNSAFE.getFloatVolatile(null, addr);
    }

    public void putFloatVolatile(long addr, float val) {
        UNSAFE.putFloatVolatile(null, addr, val);
    }

    public double getDoubleVolatile(long addr) {
        return UNSAFE.getDoubleVolatile(null, addr);
    }

    public void putDoubleVolatile(long addr, double val) {
        UNSAFE.putDoubleVolatile(null, addr, val);
    }

    //
    // OFF-HEAP MEMORY MANAGEMENT
    //

    public long allocateMemory(long size) {
        return UNSAFE.allocateMemory(size);
    }

    public long reallocateMemory(long oldAddress, long size) {
        return UNSAFE.reallocateMemory(oldAddress, size);
    }

    public void freeMemory(long address) {
        UNSAFE.freeMemory(address);
    }

    //
    // MEMORY RANGE OPERATIONS
    //

    public void setMemory(Object base, long offset, long size, byte val) {
        UNSAFE.setMemory(base, offset, size, val);
    }

    public void setMemory(long addr, long size, byte val) {
        UNSAFE.setMemory(addr, size, val);
    }

    public void copyMemory(Object srcBase, long srcOffset, Object dstBase, long dstOffset, long size) {
        UNSAFE.copyMemory(srcBase, srcOffset, dstBase, dstOffset, size);
    }

    public void copyMemory(long srcAddr, long dstAddr, long length) {
        UNSAFE.copyMemory(null, srcAddr, null, dstAddr, length);
    }

    //
    // CLASS MANAGEMENT
    //

    public boolean shouldBeInitialized(Class<?> clazz) {
        return UNSAFE.shouldBeInitialized(clazz);
    }

    public void ensureClassInitialized(Class<?> clazz) {
        UNSAFE.ensureClassInitialized(clazz);
    }

    public Class<?> defineClass(String name, byte[] classBytes, int off, int len, ClassLoader srcLoader, ProtectionDomain domain) {
        return UNSAFE.defineClass(name, classBytes, off, len, srcLoader, domain);
    }

    public Class<?> defineAnonymousClass(Class<?> hostClass, byte[] data, Object[] constantPoolPatches) {
        return UNSAFE.defineAnonymousClass(hostClass, data, constantPoolPatches);
    }

    //
    // UNINITIALIZED CLASS ALLOCATION
    //

    public <T> T allocateInstance(Class<T> clazz) {
        try {
            @SuppressWarnings("unchecked")
            T val = (T) UNSAFE.allocateInstance(clazz);
            return val;
        } catch (InstantiationException e) {
            UNSAFE.throwException(e);
            throw new AssertionError("impossible", e);
        }
    }

    //
    // UNINITIALIZED ARRAY ALLOCATION
    // TODO: implement these (they're only supported under Java 9+)
    //

    public boolean[] allocateUninitializedBooleanArray(int length) {
        return new boolean[length];
    }

    public byte[] allocateUninitializedByteArray(int length) {
        return new byte[length];
    }

    public short[] allocateUninitializedShortArray(int length) {
        return new short[length];
    }

    public char[] allocateUninitializedCharArray(int length) {
        return new char[length];
    }

    public int[] allocateUninitializedIntArray(int length) {
        return new int[length];
    }

    public long[] allocateUninitializedLongArray(int length) {
        return new long[length];
    }

    public float[] allocateUninitializedFloatArray(int length) {
        return new float[length];
    }

    public double[] allocateUninitializedDoubleArray(int length) {
        return new double[length];
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

    public void loadFence() {
        UNSAFE.loadFence();
    }

    public void storeFence() {
        UNSAFE.storeFence();
    }

    public void fullFence() {
        UNSAFE.fullFence();
    }

    //
    // ATOMIC CAS
    //

    public boolean compareAndSwapInt(Object o, long pos, int expected, int newValue) {
        return UNSAFE.compareAndSwapInt(o, pos, expected, newValue);
    }

    public boolean compareAndSwapLong(Object o, long pos, long expected, long newValue) {
        return UNSAFE.compareAndSwapLong(o, pos, expected, newValue);
    }

    public boolean compareAndSwapObject(Object o, long pos, Object expected, Object newValue) {
        return UNSAFE.compareAndSwapObject(o, pos, expected, newValue);
    }

    //
    // GENERAL-PURPOSE ATOMIC OPERATIONS
    //

    public int getAndAddInt(Object o, long pos, int val) {
        return UNSAFE.getAndAddInt(o, pos, val);
    }

    public long getAndAddLong(Object o, long pos, long val) {
        return UNSAFE.getAndAddLong(o, pos, val);
    }

    public int getAndSetInt(Object o, long pos, int val) {
        return UNSAFE.getAndSetInt(o, pos, val);
    }

    public long getAndSetLong(Object o, long pos, long val) {
        return UNSAFE.getAndSetLong(o, pos, val);
    }

    public <T> T getAndSetObject(Object o, long pos, Object val) {
        @SuppressWarnings("unchecked")
        T oldVal = (T) UNSAFE.getAndSetObject(o, pos, val);
        return oldVal;
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
