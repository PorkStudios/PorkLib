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
import java.nio.Buffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
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
    /**
     * A reference to {@link Unsafe}.
     */
    public final Unsafe UNSAFE = AccessController.doPrivileged((PrivilegedAction<Unsafe>) () -> {
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

    //
    // ARRAY BASE OFFSETS
    //

    /**
     * The value of {@code arrayBaseOffset(boolean[].class)}.
     */
    public final long ARRAY_BOOLEAN_BASE_OFFSET = UNSAFE.arrayBaseOffset(boolean[].class);

    /**
     * The value of {@code arrayBaseOffset(byte[].class)}.
     */
    public final long ARRAY_BYTE_BASE_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);

    /**
     * The value of {@code arrayBaseOffset(short[].class)}.
     */
    public final long ARRAY_SHORT_BASE_OFFSET = UNSAFE.arrayBaseOffset(short[].class);

    /**
     * The value of {@code arrayBaseOffset(char[].class)}.
     */
    public final long ARRAY_CHAR_BASE_OFFSET = UNSAFE.arrayBaseOffset(char[].class);

    /**
     * The value of {@code arrayBaseOffset(int[].class)}.
     */
    public final long ARRAY_INT_BASE_OFFSET = UNSAFE.arrayBaseOffset(int[].class);

    /**
     * The value of {@code arrayBaseOffset(long[].class)}.
     */
    public final long ARRAY_LONG_BASE_OFFSET = UNSAFE.arrayBaseOffset(long[].class);

    /**
     * The value of {@code arrayBaseOffset(float[].class)}.
     */
    public final long ARRAY_FLOAT_BASE_OFFSET = UNSAFE.arrayBaseOffset(float[].class);

    /**
     * The value of {@code arrayBaseOffset(double[].class)}.
     */
    public final long ARRAY_DOUBLE_BASE_OFFSET = UNSAFE.arrayBaseOffset(double[].class);

    /**
     * The value of {@code arrayBaseOffset(Object[].class)}.
     */
    public final long ARRAY_OBJECT_BASE_OFFSET = UNSAFE.arrayBaseOffset(Object[].class);

    //
    // ARRAY INDEX SCALES
    //

    /**
     * The value of {@code arrayIndexScale(boolean[].class)}.
     */
    public final long ARRAY_BOOLEAN_INDEX_SCALE = UNSAFE.arrayIndexScale(boolean[].class);

    /**
     * The value of {@code arrayIndexScale(byte[].class)}.
     */
    public final long ARRAY_BYTE_INDEX_SCALE = UNSAFE.arrayIndexScale(byte[].class);

    /**
     * The value of {@code arrayIndexScale(short[].class)}.
     */
    public final long ARRAY_SHORT_INDEX_SCALE = UNSAFE.arrayIndexScale(short[].class);

    /**
     * The value of {@code arrayIndexScale(char[].class)}.
     */
    public final long ARRAY_CHAR_INDEX_SCALE = UNSAFE.arrayIndexScale(char[].class);

    /**
     * The value of {@code arrayIndexScale(int[].class)}.
     */
    public final long ARRAY_INT_INDEX_SCALE = UNSAFE.arrayIndexScale(int[].class);

    /**
     * The value of {@code arrayIndexScale(long[].class)}.
     */
    public final long ARRAY_LONG_INDEX_SCALE = UNSAFE.arrayIndexScale(long[].class);

    /**
     * The value of {@code arrayIndexScale(float[].class)}.
     */
    public final long ARRAY_FLOAT_INDEX_SCALE = UNSAFE.arrayIndexScale(float[].class);

    /**
     * The value of {@code arrayIndexScale(double[].class)}.
     */
    public final long ARRAY_DOUBLE_INDEX_SCALE = UNSAFE.arrayIndexScale(double[].class);

    /**
     * The value of {@code arrayIndexScale(Object[].class)}.
     */
    public final long ARRAY_OBJECT_INDEX_SCALE = UNSAFE.arrayIndexScale(Object[].class);

    //
    // ARCHITECTURE INFORMATION
    //

    /**
     * The value of {@link Unsafe#addressSize()}.
     */
    public final int ADDRESS_SIZE = UNSAFE.addressSize();

    /**
     * The value of {@link Unsafe#pageSize()}.
     */
    public final int PAGE_SIZE = UNSAFE.pageSize();

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

    public Object staticFieldBase(Field field) {
        return UNSAFE.staticFieldBase(field);
    }

    public long staticFieldOffset(Field field) {
        return UNSAFE.staticFieldOffset(field);
    }

    //
    // ARRAY OFFSET ACCESSORS
    //

    //the computations here are in an unintuitive order to allow JIT to optimize them into FMA instructions where possible
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

    public int getInt(long addr) {
        return UNSAFE.getInt(addr);
    }

    public void putInt(long addr, int val) {
        UNSAFE.putInt(addr, val);
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

    public long pork_directBufferAddress(Buffer buffer) {
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
    //
    // custom array methods
    //
    //
}
