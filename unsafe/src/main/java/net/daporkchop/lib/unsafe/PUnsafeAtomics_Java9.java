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

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static net.daporkchop.lib.unsafe.UnsafePlatformInfo.*;

/**
 * Implements the new atomic and memory ordering operations introduced in Java 9 by forwarding calls to the corresponding methods in Unsafe.
 *
 * @author DaPorkchop_
 */
@UtilityClass
class PUnsafeAtomics_Java9 {
    //
    // MEMORY FENCES
    //

    private static final MethodHandle fullFence; // () -> void
    private static final MethodHandle acquireFence; // () -> void
    private static final MethodHandle releaseFence; // () -> void
    private static final MethodHandle loadLoadFence; // () -> void
    private static final MethodHandle storeStoreFence; // () -> void

    static {
        try {
             Class<?> _VarHandle = Class.forName("java.lang.invoke.VarHandle");

            fullFence = MethodHandles.publicLookup().findStatic(_VarHandle, "fullFence", MethodType.methodType(void.class));
            acquireFence = MethodHandles.publicLookup().findStatic(_VarHandle, "acquireFence", MethodType.methodType(void.class));
            releaseFence = MethodHandles.publicLookup().findStatic(_VarHandle, "releaseFence", MethodType.methodType(void.class));
            loadLoadFence = MethodHandles.publicLookup().findStatic(_VarHandle, "loadLoadFence", MethodType.methodType(void.class));
            storeStoreFence = MethodHandles.publicLookup().findStatic(_VarHandle, "storeStoreFence", MethodType.methodType(void.class));
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    @SneakyThrows
    public static void fullFence() {
        fullFence.invokeExact();
    }

    @SneakyThrows
    public static void acquireFence() {
        acquireFence.invokeExact();
    }

    @SneakyThrows
    public static void releaseFence() {
        releaseFence.invokeExact();
    }

    @SneakyThrows
    public static void loadLoadFence() {
        loadLoadFence.invokeExact();
    }

    @SneakyThrows
    public static void storeStoreFence() {
        storeStoreFence.invokeExact();
    }

    //
    // ACQUIRE LOADS
    //

    private static final MethodHandle getBooleanAcquire = PUnsafe.getNewUnsafeMethod("getBooleanAcquire", MethodType.methodType(boolean.class, Object.class, long.class));
    private static final MethodHandle getByteAcquire = PUnsafe.getNewUnsafeMethod("getByteAcquire", MethodType.methodType(byte.class, Object.class, long.class));
    private static final MethodHandle getShortAcquire = PUnsafe.getNewUnsafeMethod("getShortAcquire", MethodType.methodType(short.class, Object.class, long.class));
    private static final MethodHandle getCharAcquire = PUnsafe.getNewUnsafeMethod("getCharAcquire", MethodType.methodType(char.class, Object.class, long.class));
    private static final MethodHandle getIntAcquire = PUnsafe.getNewUnsafeMethod("getIntAcquire", MethodType.methodType(int.class, Object.class, long.class));
    private static final MethodHandle getLongAcquire = PUnsafe.getNewUnsafeMethod("getLongAcquire", MethodType.methodType(long.class, Object.class, long.class));
    private static final MethodHandle getFloatAcquire = PUnsafe.getNewUnsafeMethod("getFloatAcquire", MethodType.methodType(float.class, Object.class, long.class));
    private static final MethodHandle getDoubleAcquire = PUnsafe.getNewUnsafeMethod("getDoubleAcquire", MethodType.methodType(double.class, Object.class, long.class));
    private static final MethodHandle getObjectAcquire = PUnsafe.getNewUnsafeMethod("getObjectAcquire", MethodType.methodType(Object.class, Object.class, long.class));

    @SneakyThrows
    public static boolean getBooleanAcquire(Object base, long offset) {
        return (boolean) getBooleanAcquire.invokeExact(base, offset);
    }

    @SneakyThrows
    public static byte getByteAcquire(Object base, long offset) {
        return (byte) getByteAcquire.invokeExact(base, offset);
    }

    @SneakyThrows
    public static short getShortAcquire(Object base, long offset) {
        return (short) getShortAcquire.invokeExact(base, offset);
    }

    @SneakyThrows
    public static char getCharAcquire(Object base, long offset) {
        return (char) getCharAcquire.invokeExact(base, offset);
    }

    @SneakyThrows
    public static int getIntAcquire(Object base, long offset) {
        return (int) getIntAcquire.invokeExact(base, offset);
    }

    @SneakyThrows
    public static long getLongAcquire(Object base, long offset) {
        return (long) getLongAcquire.invokeExact(base, offset);
    }

    @SneakyThrows
    public static float getFloatAcquire(Object base, long offset) {
        return (float) getFloatAcquire.invokeExact(base, offset);
    }

    @SneakyThrows
    public static double getDoubleAcquire(Object base, long offset) {
        return (double) getDoubleAcquire.invokeExact(base, offset);
    }

    @SneakyThrows
    public static Object getObjectAcquire(Object base, long offset) {
        return (Object) getObjectAcquire.invokeExact(base, offset);
    }

    //
    // RELEASE STORES
    //

    private static final MethodHandle putBooleanRelease = PUnsafe.getNewUnsafeMethod("putBooleanRelease", MethodType.methodType(void.class, Object.class, long.class, boolean.class));
    private static final MethodHandle putByteRelease = PUnsafe.getNewUnsafeMethod("putByteRelease", MethodType.methodType(void.class, Object.class, long.class, byte.class));
    private static final MethodHandle putShortRelease = PUnsafe.getNewUnsafeMethod("putShortRelease", MethodType.methodType(void.class, Object.class, long.class, short.class));
    private static final MethodHandle putCharRelease = PUnsafe.getNewUnsafeMethod("putCharRelease", MethodType.methodType(void.class, Object.class, long.class, char.class));
    private static final MethodHandle putIntRelease = PUnsafe.getNewUnsafeMethod("putIntRelease", MethodType.methodType(void.class, Object.class, long.class, int.class));
    private static final MethodHandle putLongRelease = PUnsafe.getNewUnsafeMethod("putLongRelease", MethodType.methodType(void.class, Object.class, long.class, long.class));
    private static final MethodHandle putFloatRelease = PUnsafe.getNewUnsafeMethod("putFloatRelease", MethodType.methodType(void.class, Object.class, long.class, float.class));
    private static final MethodHandle putDoubleRelease = PUnsafe.getNewUnsafeMethod("putDoubleRelease", MethodType.methodType(void.class, Object.class, long.class, double.class));
    private static final MethodHandle putObjectRelease = PUnsafe.getNewUnsafeMethod("putObjectRelease", MethodType.methodType(void.class, Object.class, long.class, Object.class));

    @SneakyThrows
    public static void putBooleanRelease(Object base, long offset, boolean val) {
        putBooleanRelease.invokeExact(base, offset, val);
    }

    @SneakyThrows
    public static void putByteRelease(Object base, long offset, byte val) {
        putByteRelease.invokeExact(base, offset, val);
    }

    @SneakyThrows
    public static void putShortRelease(Object base, long offset, short val) {
        putShortRelease.invokeExact(base, offset, val);
    }

    @SneakyThrows
    public static void putCharRelease(Object base, long offset, char val) {
        putCharRelease.invokeExact(base, offset, val);
    }

    @SneakyThrows
    public static void putIntRelease(Object base, long offset, int val) {
        putIntRelease.invokeExact(base, offset, val);
    }

    @SneakyThrows
    public static void putLongRelease(Object base, long offset, long val) {
        putLongRelease.invokeExact(base, offset, val);
    }

    @SneakyThrows
    public static void putFloatRelease(Object base, long offset, float val) {
        putFloatRelease.invokeExact(base, offset, val);
    }

    @SneakyThrows
    public static void putDoubleRelease(Object base, long offset, double val) {
        putDoubleRelease.invokeExact(base, offset, val);
    }

    @SneakyThrows
    public static void putObjectRelease(Object base, long offset, Object val) {
        putObjectRelease.invokeExact(base, offset, val);
    }

    //
    // OPAQUE LOADS
    //

    private static final MethodHandle getBooleanOpaque = PUnsafe.getNewUnsafeMethod("getBooleanOpaque", MethodType.methodType(boolean.class, Object.class, long.class));
    private static final MethodHandle getByteOpaque = PUnsafe.getNewUnsafeMethod("getByteOpaque", MethodType.methodType(byte.class, Object.class, long.class));
    private static final MethodHandle getShortOpaque = PUnsafe.getNewUnsafeMethod("getShortOpaque", MethodType.methodType(short.class, Object.class, long.class));
    private static final MethodHandle getCharOpaque = PUnsafe.getNewUnsafeMethod("getCharOpaque", MethodType.methodType(char.class, Object.class, long.class));
    private static final MethodHandle getIntOpaque = PUnsafe.getNewUnsafeMethod("getIntOpaque", MethodType.methodType(int.class, Object.class, long.class));
    private static final MethodHandle getLongOpaque = PUnsafe.getNewUnsafeMethod("getLongOpaque", MethodType.methodType(long.class, Object.class, long.class));
    private static final MethodHandle getFloatOpaque = PUnsafe.getNewUnsafeMethod("getFloatOpaque", MethodType.methodType(float.class, Object.class, long.class));
    private static final MethodHandle getDoubleOpaque = PUnsafe.getNewUnsafeMethod("getDoubleOpaque", MethodType.methodType(double.class, Object.class, long.class));
    private static final MethodHandle getObjectOpaque = PUnsafe.getNewUnsafeMethod("getObjectOpaque", MethodType.methodType(Object.class, Object.class, long.class));

    @SneakyThrows
    public static boolean getBooleanOpaque(Object base, long offset) {
        return (boolean) getBooleanOpaque.invokeExact(base, offset);
    }

    @SneakyThrows
    public static byte getByteOpaque(Object base, long offset) {
        return (byte) getByteOpaque.invokeExact(base, offset);
    }

    @SneakyThrows
    public static short getShortOpaque(Object base, long offset) {
        return (short) getShortOpaque.invokeExact(base, offset);
    }

    @SneakyThrows
    public static char getCharOpaque(Object base, long offset) {
        return (char) getCharOpaque.invokeExact(base, offset);
    }

    @SneakyThrows
    public static int getIntOpaque(Object base, long offset) {
        return (int) getIntOpaque.invokeExact(base, offset);
    }

    @SneakyThrows
    public static long getLongOpaque(Object base, long offset) {
        return (long) getLongOpaque.invokeExact(base, offset);
    }

    @SneakyThrows
    public static float getFloatOpaque(Object base, long offset) {
        return (float) getFloatOpaque.invokeExact(base, offset);
    }

    @SneakyThrows
    public static double getDoubleOpaque(Object base, long offset) {
        return (double) getDoubleOpaque.invokeExact(base, offset);
    }

    @SneakyThrows
    public static Object getObjectOpaque(Object base, long offset) {
        return (Object) getObjectOpaque.invokeExact(base, offset);
    }

    //
    // OPAQUE STORES
    //

    private static final MethodHandle putBooleanOpaque = PUnsafe.getNewUnsafeMethod("putBooleanOpaque", MethodType.methodType(void.class, Object.class, long.class, boolean.class));
    private static final MethodHandle putByteOpaque = PUnsafe.getNewUnsafeMethod("putByteOpaque", MethodType.methodType(void.class, Object.class, long.class, byte.class));
    private static final MethodHandle putShortOpaque = PUnsafe.getNewUnsafeMethod("putShortOpaque", MethodType.methodType(void.class, Object.class, long.class, short.class));
    private static final MethodHandle putCharOpaque = PUnsafe.getNewUnsafeMethod("putCharOpaque", MethodType.methodType(void.class, Object.class, long.class, char.class));
    private static final MethodHandle putIntOpaque = PUnsafe.getNewUnsafeMethod("putIntOpaque", MethodType.methodType(void.class, Object.class, long.class, int.class));
    private static final MethodHandle putLongOpaque = PUnsafe.getNewUnsafeMethod("putLongOpaque", MethodType.methodType(void.class, Object.class, long.class, long.class));
    private static final MethodHandle putFloatOpaque = PUnsafe.getNewUnsafeMethod("putFloatOpaque", MethodType.methodType(void.class, Object.class, long.class, float.class));
    private static final MethodHandle putDoubleOpaque = PUnsafe.getNewUnsafeMethod("putDoubleOpaque", MethodType.methodType(void.class, Object.class, long.class, double.class));
    private static final MethodHandle putObjectOpaque = PUnsafe.getNewUnsafeMethod("putObjectOpaque", MethodType.methodType(void.class, Object.class, long.class, Object.class));

    @SneakyThrows
    public static void putBooleanOpaque(Object base, long offset, boolean val) {
        putBooleanOpaque.invokeExact(base, offset, val);
    }

    @SneakyThrows
    public static void putByteOpaque(Object base, long offset, byte val) {
        putByteOpaque.invokeExact(base, offset, val);
    }

    @SneakyThrows
    public static void putShortOpaque(Object base, long offset, short val) {
        putShortOpaque.invokeExact(base, offset, val);
    }

    @SneakyThrows
    public static void putCharOpaque(Object base, long offset, char val) {
        putCharOpaque.invokeExact(base, offset, val);
    }

    @SneakyThrows
    public static void putIntOpaque(Object base, long offset, int val) {
        putIntOpaque.invokeExact(base, offset, val);
    }

    @SneakyThrows
    public static void putLongOpaque(Object base, long offset, long val) {
        putLongOpaque.invokeExact(base, offset, val);
    }

    @SneakyThrows
    public static void putFloatOpaque(Object base, long offset, float val) {
        putFloatOpaque.invokeExact(base, offset, val);
    }

    @SneakyThrows
    public static void putDoubleOpaque(Object base, long offset, double val) {
        putDoubleOpaque.invokeExact(base, offset, val);
    }

    @SneakyThrows
    public static void putObjectOpaque(Object base, long offset, Object val) {
        putObjectOpaque.invokeExact(base, offset, val);
    }

    //
    // JAVA 9+ ATOMIC UPDATES
    //

    // boolean

    private static final MethodHandle compareAndSetBoolean = PUnsafe.getNewUnsafeMethod("compareAndSetBoolean", MethodType.methodType(boolean.class, Object.class, long.class, boolean.class, boolean.class));
    private static final MethodHandle weakCompareAndSetBoolean = PUnsafe.getNewUnsafeMethod("weakCompareAndSetBoolean", MethodType.methodType(boolean.class, Object.class, long.class, boolean.class, boolean.class));
    private static final MethodHandle weakCompareAndSetBooleanPlain = PUnsafe.getNewUnsafeMethod("weakCompareAndSetBooleanPlain", MethodType.methodType(boolean.class, Object.class, long.class, boolean.class, boolean.class));
    private static final MethodHandle weakCompareAndSetBooleanAcquire = PUnsafe.getNewUnsafeMethod("weakCompareAndSetBooleanAcquire", MethodType.methodType(boolean.class, Object.class, long.class, boolean.class, boolean.class));
    private static final MethodHandle weakCompareAndSetBooleanRelease = PUnsafe.getNewUnsafeMethod("weakCompareAndSetBooleanRelease", MethodType.methodType(boolean.class, Object.class, long.class, boolean.class, boolean.class));
    private static final MethodHandle compareAndExchangeBoolean = PUnsafe.getNewUnsafeMethod("compareAndExchangeBoolean", MethodType.methodType(boolean.class, Object.class, long.class, boolean.class, boolean.class));
    private static final MethodHandle compareAndExchangeBooleanAcquire = PUnsafe.getNewUnsafeMethod("compareAndExchangeBooleanAcquire", MethodType.methodType(boolean.class, Object.class, long.class, boolean.class, boolean.class));
    private static final MethodHandle compareAndExchangeBooleanRelease = PUnsafe.getNewUnsafeMethod("compareAndExchangeBooleanRelease", MethodType.methodType(boolean.class, Object.class, long.class, boolean.class, boolean.class));
    private static final MethodHandle getAndSetBoolean = PUnsafe.getNewUnsafeMethod("getAndSetBoolean", MethodType.methodType(boolean.class, Object.class, long.class, boolean.class));
    private static final MethodHandle getAndSetBooleanAcquire = PUnsafe.getNewUnsafeMethod("getAndSetBooleanAcquire", MethodType.methodType(boolean.class, Object.class, long.class, boolean.class));
    private static final MethodHandle getAndSetBooleanRelease = PUnsafe.getNewUnsafeMethod("getAndSetBooleanRelease", MethodType.methodType(boolean.class, Object.class, long.class, boolean.class));

    @SneakyThrows
    public static boolean compareAndSetBoolean(Object o, long offset, boolean expected, boolean newValue) {
        return (boolean) compareAndSetBoolean.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetBoolean(Object o, long offset, boolean expected, boolean newValue) {
        return (boolean) weakCompareAndSetBoolean.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetBooleanPlain(Object o, long offset, boolean expected, boolean newValue) {
        return (boolean) weakCompareAndSetBooleanPlain.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetBooleanAcquire(Object o, long offset, boolean expected, boolean newValue) {
        return (boolean) weakCompareAndSetBooleanAcquire.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetBooleanRelease(Object o, long offset, boolean expected, boolean newValue) {
        return (boolean) weakCompareAndSetBooleanRelease.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean compareAndExchangeBoolean(Object o, long offset, boolean expected, boolean newValue) {
        return (boolean) compareAndExchangeBoolean.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean compareAndExchangeBooleanAcquire(Object o, long offset, boolean expected, boolean newValue) {
        return (boolean) compareAndExchangeBooleanAcquire.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean compareAndExchangeBooleanRelease(Object o, long offset, boolean expected, boolean newValue) {
        return (boolean) compareAndExchangeBooleanRelease.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean getAndSetBoolean(Object o, long offset, boolean newValue) {
        return (boolean) getAndSetBoolean.invokeExact(o, offset, newValue);
    }

    @SneakyThrows
    public static boolean getAndSetBooleanAcquire(Object o, long offset, boolean newValue) {
        return (boolean) getAndSetBooleanAcquire.invokeExact(o, offset, newValue);
    }

    @SneakyThrows
    public static boolean getAndSetBooleanRelease(Object o, long offset, boolean newValue) {
        return (boolean) getAndSetBooleanRelease.invokeExact(o, offset, newValue);
    }

    // byte

    private static final MethodHandle compareAndSetByte = PUnsafe.getNewUnsafeMethod("compareAndSetByte", MethodType.methodType(boolean.class, Object.class, long.class, byte.class, byte.class));
    private static final MethodHandle weakCompareAndSetByte = PUnsafe.getNewUnsafeMethod("weakCompareAndSetByte", MethodType.methodType(boolean.class, Object.class, long.class, byte.class, byte.class));
    private static final MethodHandle weakCompareAndSetBytePlain = PUnsafe.getNewUnsafeMethod("weakCompareAndSetBytePlain", MethodType.methodType(boolean.class, Object.class, long.class, byte.class, byte.class));
    private static final MethodHandle weakCompareAndSetByteAcquire = PUnsafe.getNewUnsafeMethod("weakCompareAndSetByteAcquire", MethodType.methodType(boolean.class, Object.class, long.class, byte.class, byte.class));
    private static final MethodHandle weakCompareAndSetByteRelease = PUnsafe.getNewUnsafeMethod("weakCompareAndSetByteRelease", MethodType.methodType(boolean.class, Object.class, long.class, byte.class, byte.class));
    private static final MethodHandle compareAndExchangeByte = PUnsafe.getNewUnsafeMethod("compareAndExchangeByte", MethodType.methodType(byte.class, Object.class, long.class, byte.class, byte.class));
    private static final MethodHandle compareAndExchangeByteAcquire = PUnsafe.getNewUnsafeMethod("compareAndExchangeByteAcquire", MethodType.methodType(byte.class, Object.class, long.class, byte.class, byte.class));
    private static final MethodHandle compareAndExchangeByteRelease = PUnsafe.getNewUnsafeMethod("compareAndExchangeByteRelease", MethodType.methodType(byte.class, Object.class, long.class, byte.class, byte.class));
    private static final MethodHandle getAndSetByte = PUnsafe.getNewUnsafeMethod("getAndSetByte", MethodType.methodType(byte.class, Object.class, long.class, byte.class));
    private static final MethodHandle getAndSetByteAcquire = PUnsafe.getNewUnsafeMethod("getAndSetByteAcquire", MethodType.methodType(byte.class, Object.class, long.class, byte.class));
    private static final MethodHandle getAndSetByteRelease = PUnsafe.getNewUnsafeMethod("getAndSetByteRelease", MethodType.methodType(byte.class, Object.class, long.class, byte.class));

    @SneakyThrows
    public static boolean compareAndSetByte(Object o, long offset, byte expected, byte newValue) {
        return (boolean) compareAndSetByte.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetByte(Object o, long offset, byte expected, byte newValue) {
        return (boolean) weakCompareAndSetByte.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetBytePlain(Object o, long offset, byte expected, byte newValue) {
        return (boolean) weakCompareAndSetBytePlain.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetByteAcquire(Object o, long offset, byte expected, byte newValue) {
        return (boolean) weakCompareAndSetByteAcquire.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetByteRelease(Object o, long offset, byte expected, byte newValue) {
        return (boolean) weakCompareAndSetByteRelease.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static byte compareAndExchangeByte(Object o, long offset, byte expected, byte newValue) {
        return (byte) compareAndExchangeByte.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static byte compareAndExchangeByteAcquire(Object o, long offset, byte expected, byte newValue) {
        return (byte) compareAndExchangeByteAcquire.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static byte compareAndExchangeByteRelease(Object o, long offset, byte expected, byte newValue) {
        return (byte) compareAndExchangeByteRelease.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static byte getAndSetByte(Object o, long offset, byte newValue) {
        return (byte) getAndSetByte.invokeExact(o, offset, newValue);
    }

    @SneakyThrows
    public static byte getAndSetByteAcquire(Object o, long offset, byte newValue) {
        return (byte) getAndSetByteAcquire.invokeExact(o, offset, newValue);
    }

    @SneakyThrows
    public static byte getAndSetByteRelease(Object o, long offset, byte newValue) {
        return (byte) getAndSetByteRelease.invokeExact(o, offset, newValue);
    }

    // short

    private static final MethodHandle compareAndSetShort = PUnsafe.getNewUnsafeMethod("compareAndSetShort", MethodType.methodType(boolean.class, Object.class, long.class, short.class, short.class));
    private static final MethodHandle weakCompareAndSetShort = PUnsafe.getNewUnsafeMethod("weakCompareAndSetShort", MethodType.methodType(boolean.class, Object.class, long.class, short.class, short.class));
    private static final MethodHandle weakCompareAndSetShortPlain = PUnsafe.getNewUnsafeMethod("weakCompareAndSetShortPlain", MethodType.methodType(boolean.class, Object.class, long.class, short.class, short.class));
    private static final MethodHandle weakCompareAndSetShortAcquire = PUnsafe.getNewUnsafeMethod("weakCompareAndSetShortAcquire", MethodType.methodType(boolean.class, Object.class, long.class, short.class, short.class));
    private static final MethodHandle weakCompareAndSetShortRelease = PUnsafe.getNewUnsafeMethod("weakCompareAndSetShortRelease", MethodType.methodType(boolean.class, Object.class, long.class, short.class, short.class));
    private static final MethodHandle compareAndExchangeShort = PUnsafe.getNewUnsafeMethod("compareAndExchangeShort", MethodType.methodType(short.class, Object.class, long.class, short.class, short.class));
    private static final MethodHandle compareAndExchangeShortAcquire = PUnsafe.getNewUnsafeMethod("compareAndExchangeShortAcquire", MethodType.methodType(short.class, Object.class, long.class, short.class, short.class));
    private static final MethodHandle compareAndExchangeShortRelease = PUnsafe.getNewUnsafeMethod("compareAndExchangeShortRelease", MethodType.methodType(short.class, Object.class, long.class, short.class, short.class));
    private static final MethodHandle getAndSetShort = PUnsafe.getNewUnsafeMethod("getAndSetShort", MethodType.methodType(short.class, Object.class, long.class, short.class));
    private static final MethodHandle getAndSetShortAcquire = PUnsafe.getNewUnsafeMethod("getAndSetShortAcquire", MethodType.methodType(short.class, Object.class, long.class, short.class));
    private static final MethodHandle getAndSetShortRelease = PUnsafe.getNewUnsafeMethod("getAndSetShortRelease", MethodType.methodType(short.class, Object.class, long.class, short.class));

    @SneakyThrows
    public static boolean compareAndSetShort(Object o, long offset, short expected, short newValue) {
        return (boolean) compareAndSetShort.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetShort(Object o, long offset, short expected, short newValue) {
        return (boolean) weakCompareAndSetShort.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetShortPlain(Object o, long offset, short expected, short newValue) {
        return (boolean) weakCompareAndSetShortPlain.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetShortAcquire(Object o, long offset, short expected, short newValue) {
        return (boolean) weakCompareAndSetShortAcquire.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetShortRelease(Object o, long offset, short expected, short newValue) {
        return (boolean) weakCompareAndSetShortRelease.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static short compareAndExchangeShort(Object o, long offset, short expected, short newValue) {
        return (short) compareAndExchangeShort.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static short compareAndExchangeShortAcquire(Object o, long offset, short expected, short newValue) {
        return (short) compareAndExchangeShortAcquire.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static short compareAndExchangeShortRelease(Object o, long offset, short expected, short newValue) {
        return (short) compareAndExchangeShortRelease.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static short getAndSetShort(Object o, long offset, short newValue) {
        return (short) getAndSetShort.invokeExact(o, offset, newValue);
    }

    @SneakyThrows
    public static short getAndSetShortAcquire(Object o, long offset, short newValue) {
        return (short) getAndSetShortAcquire.invokeExact(o, offset, newValue);
    }

    @SneakyThrows
    public static short getAndSetShortRelease(Object o, long offset, short newValue) {
        return (short) getAndSetShortRelease.invokeExact(o, offset, newValue);
    }

    // char

    private static final MethodHandle compareAndSetChar = PUnsafe.getNewUnsafeMethod("compareAndSetChar", MethodType.methodType(boolean.class, Object.class, long.class, char.class, char.class));
    private static final MethodHandle weakCompareAndSetChar = PUnsafe.getNewUnsafeMethod("weakCompareAndSetChar", MethodType.methodType(boolean.class, Object.class, long.class, char.class, char.class));
    private static final MethodHandle weakCompareAndSetCharPlain = PUnsafe.getNewUnsafeMethod("weakCompareAndSetCharPlain", MethodType.methodType(boolean.class, Object.class, long.class, char.class, char.class));
    private static final MethodHandle weakCompareAndSetCharAcquire = PUnsafe.getNewUnsafeMethod("weakCompareAndSetCharAcquire", MethodType.methodType(boolean.class, Object.class, long.class, char.class, char.class));
    private static final MethodHandle weakCompareAndSetCharRelease = PUnsafe.getNewUnsafeMethod("weakCompareAndSetCharRelease", MethodType.methodType(boolean.class, Object.class, long.class, char.class, char.class));
    private static final MethodHandle compareAndExchangeChar = PUnsafe.getNewUnsafeMethod("compareAndExchangeChar", MethodType.methodType(char.class, Object.class, long.class, char.class, char.class));
    private static final MethodHandle compareAndExchangeCharAcquire = PUnsafe.getNewUnsafeMethod("compareAndExchangeCharAcquire", MethodType.methodType(char.class, Object.class, long.class, char.class, char.class));
    private static final MethodHandle compareAndExchangeCharRelease = PUnsafe.getNewUnsafeMethod("compareAndExchangeCharRelease", MethodType.methodType(char.class, Object.class, long.class, char.class, char.class));
    private static final MethodHandle getAndSetChar = PUnsafe.getNewUnsafeMethod("getAndSetChar", MethodType.methodType(char.class, Object.class, long.class, char.class));
    private static final MethodHandle getAndSetCharAcquire = PUnsafe.getNewUnsafeMethod("getAndSetCharAcquire", MethodType.methodType(char.class, Object.class, long.class, char.class));
    private static final MethodHandle getAndSetCharRelease = PUnsafe.getNewUnsafeMethod("getAndSetCharRelease", MethodType.methodType(char.class, Object.class, long.class, char.class));

    @SneakyThrows
    public static boolean compareAndSetChar(Object o, long offset, char expected, char newValue) {
        return (boolean) compareAndSetChar.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetChar(Object o, long offset, char expected, char newValue) {
        return (boolean) weakCompareAndSetChar.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetCharPlain(Object o, long offset, char expected, char newValue) {
        return (boolean) weakCompareAndSetCharPlain.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetCharAcquire(Object o, long offset, char expected, char newValue) {
        return (boolean) weakCompareAndSetCharAcquire.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetCharRelease(Object o, long offset, char expected, char newValue) {
        return (boolean) weakCompareAndSetCharRelease.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static char compareAndExchangeChar(Object o, long offset, char expected, char newValue) {
        return (char) compareAndExchangeChar.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static char compareAndExchangeCharAcquire(Object o, long offset, char expected, char newValue) {
        return (char) compareAndExchangeCharAcquire.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static char compareAndExchangeCharRelease(Object o, long offset, char expected, char newValue) {
        return (char) compareAndExchangeCharRelease.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static char getAndSetChar(Object o, long offset, char newValue) {
        return (char) getAndSetChar.invokeExact(o, offset, newValue);
    }

    @SneakyThrows
    public static char getAndSetCharAcquire(Object o, long offset, char newValue) {
        return (char) getAndSetCharAcquire.invokeExact(o, offset, newValue);
    }

    @SneakyThrows
    public static char getAndSetCharRelease(Object o, long offset, char newValue) {
        return (char) getAndSetCharRelease.invokeExact(o, offset, newValue);
    }

    // int

    private static final MethodHandle compareAndSetInt = PUnsafe.getNewUnsafeMethod("compareAndSetInt", MethodType.methodType(boolean.class, Object.class, long.class, int.class, int.class));
    private static final MethodHandle weakCompareAndSetInt = PUnsafe.getNewUnsafeMethod("weakCompareAndSetInt", MethodType.methodType(boolean.class, Object.class, long.class, int.class, int.class));
    private static final MethodHandle weakCompareAndSetIntPlain = PUnsafe.getNewUnsafeMethod("weakCompareAndSetIntPlain", MethodType.methodType(boolean.class, Object.class, long.class, int.class, int.class));
    private static final MethodHandle weakCompareAndSetIntAcquire = PUnsafe.getNewUnsafeMethod("weakCompareAndSetIntAcquire", MethodType.methodType(boolean.class, Object.class, long.class, int.class, int.class));
    private static final MethodHandle weakCompareAndSetIntRelease = PUnsafe.getNewUnsafeMethod("weakCompareAndSetIntRelease", MethodType.methodType(boolean.class, Object.class, long.class, int.class, int.class));
    private static final MethodHandle compareAndExchangeInt = PUnsafe.getNewUnsafeMethod("compareAndExchangeInt", MethodType.methodType(int.class, Object.class, long.class, int.class, int.class));
    private static final MethodHandle compareAndExchangeIntAcquire = PUnsafe.getNewUnsafeMethod("compareAndExchangeIntAcquire", MethodType.methodType(int.class, Object.class, long.class, int.class, int.class));
    private static final MethodHandle compareAndExchangeIntRelease = PUnsafe.getNewUnsafeMethod("compareAndExchangeIntRelease", MethodType.methodType(int.class, Object.class, long.class, int.class, int.class));
    private static final MethodHandle getAndSetInt = PUnsafe.getNewUnsafeMethod("getAndSetInt", MethodType.methodType(int.class, Object.class, long.class, int.class));
    private static final MethodHandle getAndSetIntAcquire = PUnsafe.getNewUnsafeMethod("getAndSetIntAcquire", MethodType.methodType(int.class, Object.class, long.class, int.class));
    private static final MethodHandle getAndSetIntRelease = PUnsafe.getNewUnsafeMethod("getAndSetIntRelease", MethodType.methodType(int.class, Object.class, long.class, int.class));

    @SneakyThrows
    public static boolean compareAndSetInt(Object o, long offset, int expected, int newValue) {
        return (boolean) compareAndSetInt.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetInt(Object o, long offset, int expected, int newValue) {
        return (boolean) weakCompareAndSetInt.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetIntPlain(Object o, long offset, int expected, int newValue) {
        return (boolean) weakCompareAndSetIntPlain.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetIntAcquire(Object o, long offset, int expected, int newValue) {
        return (boolean) weakCompareAndSetIntAcquire.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetIntRelease(Object o, long offset, int expected, int newValue) {
        return (boolean) weakCompareAndSetIntRelease.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static int compareAndExchangeInt(Object o, long offset, int expected, int newValue) {
        return (int) compareAndExchangeInt.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static int compareAndExchangeIntAcquire(Object o, long offset, int expected, int newValue) {
        return (int) compareAndExchangeIntAcquire.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static int compareAndExchangeIntRelease(Object o, long offset, int expected, int newValue) {
        return (int) compareAndExchangeIntRelease.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static int getAndSetInt(Object o, long offset, int newValue) {
        return (int) getAndSetInt.invokeExact(o, offset, newValue);
    }

    @SneakyThrows
    public static int getAndSetIntAcquire(Object o, long offset, int newValue) {
        return (int) getAndSetIntAcquire.invokeExact(o, offset, newValue);
    }

    @SneakyThrows
    public static int getAndSetIntRelease(Object o, long offset, int newValue) {
        return (int) getAndSetIntRelease.invokeExact(o, offset, newValue);
    }

    // long

    private static final MethodHandle compareAndSetLong = PUnsafe.getNewUnsafeMethod("compareAndSetLong", MethodType.methodType(boolean.class, Object.class, long.class, long.class, long.class));
    private static final MethodHandle weakCompareAndSetLong = PUnsafe.getNewUnsafeMethod("weakCompareAndSetLong", MethodType.methodType(boolean.class, Object.class, long.class, long.class, long.class));
    private static final MethodHandle weakCompareAndSetLongPlain = PUnsafe.getNewUnsafeMethod("weakCompareAndSetLongPlain", MethodType.methodType(boolean.class, Object.class, long.class, long.class, long.class));
    private static final MethodHandle weakCompareAndSetLongAcquire = PUnsafe.getNewUnsafeMethod("weakCompareAndSetLongAcquire", MethodType.methodType(boolean.class, Object.class, long.class, long.class, long.class));
    private static final MethodHandle weakCompareAndSetLongRelease = PUnsafe.getNewUnsafeMethod("weakCompareAndSetLongRelease", MethodType.methodType(boolean.class, Object.class, long.class, long.class, long.class));
    private static final MethodHandle compareAndExchangeLong = PUnsafe.getNewUnsafeMethod("compareAndExchangeLong", MethodType.methodType(long.class, Object.class, long.class, long.class, long.class));
    private static final MethodHandle compareAndExchangeLongAcquire = PUnsafe.getNewUnsafeMethod("compareAndExchangeLongAcquire", MethodType.methodType(long.class, Object.class, long.class, long.class, long.class));
    private static final MethodHandle compareAndExchangeLongRelease = PUnsafe.getNewUnsafeMethod("compareAndExchangeLongRelease", MethodType.methodType(long.class, Object.class, long.class, long.class, long.class));
    private static final MethodHandle getAndSetLong = PUnsafe.getNewUnsafeMethod("getAndSetLong", MethodType.methodType(long.class, Object.class, long.class, long.class));
    private static final MethodHandle getAndSetLongAcquire = PUnsafe.getNewUnsafeMethod("getAndSetLongAcquire", MethodType.methodType(long.class, Object.class, long.class, long.class));
    private static final MethodHandle getAndSetLongRelease = PUnsafe.getNewUnsafeMethod("getAndSetLongRelease", MethodType.methodType(long.class, Object.class, long.class, long.class));

    @SneakyThrows
    public static boolean compareAndSetLong(Object o, long offset, long expected, long newValue) {
        return (boolean) compareAndSetLong.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetLong(Object o, long offset, long expected, long newValue) {
        return (boolean) weakCompareAndSetLong.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetLongPlain(Object o, long offset, long expected, long newValue) {
        return (boolean) weakCompareAndSetLongPlain.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetLongAcquire(Object o, long offset, long expected, long newValue) {
        return (boolean) weakCompareAndSetLongAcquire.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetLongRelease(Object o, long offset, long expected, long newValue) {
        return (boolean) weakCompareAndSetLongRelease.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static long compareAndExchangeLong(Object o, long offset, long expected, long newValue) {
        return (long) compareAndExchangeLong.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static long compareAndExchangeLongAcquire(Object o, long offset, long expected, long newValue) {
        return (long) compareAndExchangeLongAcquire.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static long compareAndExchangeLongRelease(Object o, long offset, long expected, long newValue) {
        return (long) compareAndExchangeLongRelease.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static long getAndSetLong(Object o, long offset, long newValue) {
        return (long) getAndSetLong.invokeExact(o, offset, newValue);
    }

    @SneakyThrows
    public static long getAndSetLongAcquire(Object o, long offset, long newValue) {
        return (long) getAndSetLongAcquire.invokeExact(o, offset, newValue);
    }

    @SneakyThrows
    public static long getAndSetLongRelease(Object o, long offset, long newValue) {
        return (long) getAndSetLongRelease.invokeExact(o, offset, newValue);
    }

    // float

    private static final MethodHandle compareAndSetFloat = PUnsafe.getNewUnsafeMethod("compareAndSetFloat", MethodType.methodType(boolean.class, Object.class, long.class, float.class, float.class));
    private static final MethodHandle weakCompareAndSetFloat = PUnsafe.getNewUnsafeMethod("weakCompareAndSetFloat", MethodType.methodType(boolean.class, Object.class, long.class, float.class, float.class));
    private static final MethodHandle weakCompareAndSetFloatPlain = PUnsafe.getNewUnsafeMethod("weakCompareAndSetFloatPlain", MethodType.methodType(boolean.class, Object.class, long.class, float.class, float.class));
    private static final MethodHandle weakCompareAndSetFloatAcquire = PUnsafe.getNewUnsafeMethod("weakCompareAndSetFloatAcquire", MethodType.methodType(boolean.class, Object.class, long.class, float.class, float.class));
    private static final MethodHandle weakCompareAndSetFloatRelease = PUnsafe.getNewUnsafeMethod("weakCompareAndSetFloatRelease", MethodType.methodType(boolean.class, Object.class, long.class, float.class, float.class));
    private static final MethodHandle compareAndExchangeFloat = PUnsafe.getNewUnsafeMethod("compareAndExchangeFloat", MethodType.methodType(float.class, Object.class, long.class, float.class, float.class));
    private static final MethodHandle compareAndExchangeFloatAcquire = PUnsafe.getNewUnsafeMethod("compareAndExchangeFloatAcquire", MethodType.methodType(float.class, Object.class, long.class, float.class, float.class));
    private static final MethodHandle compareAndExchangeFloatRelease = PUnsafe.getNewUnsafeMethod("compareAndExchangeFloatRelease", MethodType.methodType(float.class, Object.class, long.class, float.class, float.class));
    private static final MethodHandle getAndSetFloat = PUnsafe.getNewUnsafeMethod("getAndSetFloat", MethodType.methodType(float.class, Object.class, long.class, float.class));
    private static final MethodHandle getAndSetFloatAcquire = PUnsafe.getNewUnsafeMethod("getAndSetFloatAcquire", MethodType.methodType(float.class, Object.class, long.class, float.class));
    private static final MethodHandle getAndSetFloatRelease = PUnsafe.getNewUnsafeMethod("getAndSetFloatRelease", MethodType.methodType(float.class, Object.class, long.class, float.class));

    @SneakyThrows
    public static boolean compareAndSetFloat(Object o, long offset, float expected, float newValue) {
        return (boolean) compareAndSetFloat.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetFloat(Object o, long offset, float expected, float newValue) {
        return (boolean) weakCompareAndSetFloat.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetFloatPlain(Object o, long offset, float expected, float newValue) {
        return (boolean) weakCompareAndSetFloatPlain.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetFloatAcquire(Object o, long offset, float expected, float newValue) {
        return (boolean) weakCompareAndSetFloatAcquire.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetFloatRelease(Object o, long offset, float expected, float newValue) {
        return (boolean) weakCompareAndSetFloatRelease.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static float compareAndExchangeFloat(Object o, long offset, float expected, float newValue) {
        return (float) compareAndExchangeFloat.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static float compareAndExchangeFloatAcquire(Object o, long offset, float expected, float newValue) {
        return (float) compareAndExchangeFloatAcquire.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static float compareAndExchangeFloatRelease(Object o, long offset, float expected, float newValue) {
        return (float) compareAndExchangeFloatRelease.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static float getAndSetFloat(Object o, long offset, float newValue) {
        return (float) getAndSetFloat.invokeExact(o, offset, newValue);
    }

    @SneakyThrows
    public static float getAndSetFloatAcquire(Object o, long offset, float newValue) {
        return (float) getAndSetFloatAcquire.invokeExact(o, offset, newValue);
    }

    @SneakyThrows
    public static float getAndSetFloatRelease(Object o, long offset, float newValue) {
        return (float) getAndSetFloatRelease.invokeExact(o, offset, newValue);
    }

    // double

    private static final MethodHandle compareAndSetDouble = PUnsafe.getNewUnsafeMethod("compareAndSetDouble", MethodType.methodType(boolean.class, Object.class, long.class, double.class, double.class));
    private static final MethodHandle weakCompareAndSetDouble = PUnsafe.getNewUnsafeMethod("weakCompareAndSetDouble", MethodType.methodType(boolean.class, Object.class, long.class, double.class, double.class));
    private static final MethodHandle weakCompareAndSetDoublePlain = PUnsafe.getNewUnsafeMethod("weakCompareAndSetDoublePlain", MethodType.methodType(boolean.class, Object.class, long.class, double.class, double.class));
    private static final MethodHandle weakCompareAndSetDoubleAcquire = PUnsafe.getNewUnsafeMethod("weakCompareAndSetDoubleAcquire", MethodType.methodType(boolean.class, Object.class, long.class, double.class, double.class));
    private static final MethodHandle weakCompareAndSetDoubleRelease = PUnsafe.getNewUnsafeMethod("weakCompareAndSetDoubleRelease", MethodType.methodType(boolean.class, Object.class, long.class, double.class, double.class));
    private static final MethodHandle compareAndExchangeDouble = PUnsafe.getNewUnsafeMethod("compareAndExchangeDouble", MethodType.methodType(double.class, Object.class, long.class, double.class, double.class));
    private static final MethodHandle compareAndExchangeDoubleAcquire = PUnsafe.getNewUnsafeMethod("compareAndExchangeDoubleAcquire", MethodType.methodType(double.class, Object.class, long.class, double.class, double.class));
    private static final MethodHandle compareAndExchangeDoubleRelease = PUnsafe.getNewUnsafeMethod("compareAndExchangeDoubleRelease", MethodType.methodType(double.class, Object.class, long.class, double.class, double.class));
    private static final MethodHandle getAndSetDouble = PUnsafe.getNewUnsafeMethod("getAndSetDouble", MethodType.methodType(double.class, Object.class, long.class, double.class));
    private static final MethodHandle getAndSetDoubleAcquire = PUnsafe.getNewUnsafeMethod("getAndSetDoubleAcquire", MethodType.methodType(double.class, Object.class, long.class, double.class));
    private static final MethodHandle getAndSetDoubleRelease = PUnsafe.getNewUnsafeMethod("getAndSetDoubleRelease", MethodType.methodType(double.class, Object.class, long.class, double.class));

    @SneakyThrows
    public static boolean compareAndSetDouble(Object o, long offset, double expected, double newValue) {
        return (boolean) compareAndSetDouble.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetDouble(Object o, long offset, double expected, double newValue) {
        return (boolean) weakCompareAndSetDouble.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetDoublePlain(Object o, long offset, double expected, double newValue) {
        return (boolean) weakCompareAndSetDoublePlain.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetDoubleAcquire(Object o, long offset, double expected, double newValue) {
        return (boolean) weakCompareAndSetDoubleAcquire.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetDoubleRelease(Object o, long offset, double expected, double newValue) {
        return (boolean) weakCompareAndSetDoubleRelease.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static double compareAndExchangeDouble(Object o, long offset, double expected, double newValue) {
        return (double) compareAndExchangeDouble.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static double compareAndExchangeDoubleAcquire(Object o, long offset, double expected, double newValue) {
        return (double) compareAndExchangeDoubleAcquire.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static double compareAndExchangeDoubleRelease(Object o, long offset, double expected, double newValue) {
        return (double) compareAndExchangeDoubleRelease.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static double getAndSetDouble(Object o, long offset, double newValue) {
        return (double) getAndSetDouble.invokeExact(o, offset, newValue);
    }

    @SneakyThrows
    public static double getAndSetDoubleAcquire(Object o, long offset, double newValue) {
        return (double) getAndSetDoubleAcquire.invokeExact(o, offset, newValue);
    }

    @SneakyThrows
    public static double getAndSetDoubleRelease(Object o, long offset, double newValue) {
        return (double) getAndSetDoubleRelease.invokeExact(o, offset, newValue);
    }

    // Object

    private static final MethodHandle compareAndSetObject = PUnsafe.getNewUnsafeMethod("compareAndSetObject", MethodType.methodType(boolean.class, Object.class, long.class, Object.class, Object.class));
    private static final MethodHandle weakCompareAndSetObject = PUnsafe.getNewUnsafeMethod("weakCompareAndSetObject", MethodType.methodType(boolean.class, Object.class, long.class, Object.class, Object.class));
    private static final MethodHandle weakCompareAndSetObjectPlain = PUnsafe.getNewUnsafeMethod("weakCompareAndSetObjectPlain", MethodType.methodType(boolean.class, Object.class, long.class, Object.class, Object.class));
    private static final MethodHandle weakCompareAndSetObjectAcquire = PUnsafe.getNewUnsafeMethod("weakCompareAndSetObjectAcquire", MethodType.methodType(boolean.class, Object.class, long.class, Object.class, Object.class));
    private static final MethodHandle weakCompareAndSetObjectRelease = PUnsafe.getNewUnsafeMethod("weakCompareAndSetObjectRelease", MethodType.methodType(boolean.class, Object.class, long.class, Object.class, Object.class));
    private static final MethodHandle compareAndExchangeObject = PUnsafe.getNewUnsafeMethod("compareAndExchangeObject", MethodType.methodType(Object.class, Object.class, long.class, Object.class, Object.class));
    private static final MethodHandle compareAndExchangeObjectAcquire = PUnsafe.getNewUnsafeMethod("compareAndExchangeObjectAcquire", MethodType.methodType(Object.class, Object.class, long.class, Object.class, Object.class));
    private static final MethodHandle compareAndExchangeObjectRelease = PUnsafe.getNewUnsafeMethod("compareAndExchangeObjectRelease", MethodType.methodType(Object.class, Object.class, long.class, Object.class, Object.class));
    private static final MethodHandle getAndSetObject = PUnsafe.getNewUnsafeMethod("getAndSetObject", MethodType.methodType(Object.class, Object.class, long.class, Object.class));
    private static final MethodHandle getAndSetObjectAcquire = PUnsafe.getNewUnsafeMethod("getAndSetObjectAcquire", MethodType.methodType(Object.class, Object.class, long.class, Object.class));
    private static final MethodHandle getAndSetObjectRelease = PUnsafe.getNewUnsafeMethod("getAndSetObjectRelease", MethodType.methodType(Object.class, Object.class, long.class, Object.class));

    @SneakyThrows
    public static boolean compareAndSetObject(Object o, long offset, Object expected, Object newValue) {
        return (boolean) compareAndSetObject.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetObject(Object o, long offset, Object expected, Object newValue) {
        return (boolean) weakCompareAndSetObject.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetObjectPlain(Object o, long offset, Object expected, Object newValue) {
        return (boolean) weakCompareAndSetObjectPlain.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetObjectAcquire(Object o, long offset, Object expected, Object newValue) {
        return (boolean) weakCompareAndSetObjectAcquire.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static boolean weakCompareAndSetObjectRelease(Object o, long offset, Object expected, Object newValue) {
        return (boolean) weakCompareAndSetObjectRelease.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static Object compareAndExchangeObject(Object o, long offset, Object expected, Object newValue) {
        return compareAndExchangeObject.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static Object compareAndExchangeObjectAcquire(Object o, long offset, Object expected, Object newValue) {
        return compareAndExchangeObjectAcquire.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static Object compareAndExchangeObjectRelease(Object o, long offset, Object expected, Object newValue) {
        return compareAndExchangeObjectRelease.invokeExact(o, offset, expected, newValue);
    }

    @SneakyThrows
    public static Object getAndSetObject(Object o, long offset, Object newValue) {
        return getAndSetObject.invokeExact(o, offset, newValue);
    }

    @SneakyThrows
    public static Object getAndSetObjectAcquire(Object o, long offset, Object newValue) {
        return getAndSetObjectAcquire.invokeExact(o, offset, newValue);
    }

    @SneakyThrows
    public static Object getAndSetObjectRelease(Object o, long offset, Object newValue) {
        return getAndSetObjectRelease.invokeExact(o, offset, newValue);
    }

    //
    // NUMERIC ATOMIC UPDATES
    //

    // byte
    
    private static final MethodHandle getAndAddByte = PUnsafe.getNewUnsafeMethod("getAndAddByte", MethodType.methodType(byte.class, Object.class, long.class, byte.class));
    private static final MethodHandle getAndAddByteAcquire = PUnsafe.getNewUnsafeMethod("getAndAddByteAcquire", MethodType.methodType(byte.class, Object.class, long.class, byte.class));
    private static final MethodHandle getAndAddByteRelease = PUnsafe.getNewUnsafeMethod("getAndAddByteRelease", MethodType.methodType(byte.class, Object.class, long.class, byte.class));

    @SneakyThrows
    public static byte getAndAddByte(Object o, long offset, byte delta) {
        return (byte) getAndAddByte.invokeExact(o, offset, delta);
    }

    @SneakyThrows
    public static byte getAndAddByteAcquire(Object o, long offset, byte delta) {
        return (byte) getAndAddByteAcquire.invokeExact(o, offset, delta);
    }

    @SneakyThrows
    public static byte getAndAddByteRelease(Object o, long offset, byte delta) {
        return (byte) getAndAddByteRelease.invokeExact(o, offset, delta);
    }

    // short

    private static final MethodHandle getAndAddShort = PUnsafe.getNewUnsafeMethod("getAndAddShort", MethodType.methodType(short.class, Object.class, long.class, short.class));
    private static final MethodHandle getAndAddShortAcquire = PUnsafe.getNewUnsafeMethod("getAndAddShortAcquire", MethodType.methodType(short.class, Object.class, long.class, short.class));
    private static final MethodHandle getAndAddShortRelease = PUnsafe.getNewUnsafeMethod("getAndAddShortRelease", MethodType.methodType(short.class, Object.class, long.class, short.class));

    @SneakyThrows
    public static short getAndAddShort(Object o, long offset, short delta) {
        return (short) getAndAddShort.invokeExact(o, offset, delta);
    }

    @SneakyThrows
    public static short getAndAddShortAcquire(Object o, long offset, short delta) {
        return (short) getAndAddShortAcquire.invokeExact(o, offset, delta);
    }

    @SneakyThrows
    public static short getAndAddShortRelease(Object o, long offset, short delta) {
        return (short) getAndAddShortRelease.invokeExact(o, offset, delta);
    }

    // char

    private static final MethodHandle getAndAddChar = PUnsafe.getNewUnsafeMethod("getAndAddChar", MethodType.methodType(char.class, Object.class, long.class, char.class));
    private static final MethodHandle getAndAddCharAcquire = PUnsafe.getNewUnsafeMethod("getAndAddCharAcquire", MethodType.methodType(char.class, Object.class, long.class, char.class));
    private static final MethodHandle getAndAddCharRelease = PUnsafe.getNewUnsafeMethod("getAndAddCharRelease", MethodType.methodType(char.class, Object.class, long.class, char.class));

    @SneakyThrows
    public static char getAndAddChar(Object o, long offset, char delta) {
        return (char) getAndAddChar.invokeExact(o, offset, delta);
    }

    @SneakyThrows
    public static char getAndAddCharAcquire(Object o, long offset, char delta) {
        return (char) getAndAddCharAcquire.invokeExact(o, offset, delta);
    }

    @SneakyThrows
    public static char getAndAddCharRelease(Object o, long offset, char delta) {
        return (char) getAndAddCharRelease.invokeExact(o, offset, delta);
    }

    // int

    private static final MethodHandle getAndAddInt = PUnsafe.getNewUnsafeMethod("getAndAddInt", MethodType.methodType(int.class, Object.class, long.class, int.class));
    private static final MethodHandle getAndAddIntAcquire = PUnsafe.getNewUnsafeMethod("getAndAddIntAcquire", MethodType.methodType(int.class, Object.class, long.class, int.class));
    private static final MethodHandle getAndAddIntRelease = PUnsafe.getNewUnsafeMethod("getAndAddIntRelease", MethodType.methodType(int.class, Object.class, long.class, int.class));

    @SneakyThrows
    public static int getAndAddInt(Object o, long offset, int delta) {
        return (int) getAndAddInt.invokeExact(o, offset, delta);
    }

    @SneakyThrows
    public static int getAndAddIntAcquire(Object o, long offset, int delta) {
        return (int) getAndAddIntAcquire.invokeExact(o, offset, delta);
    }

    @SneakyThrows
    public static int getAndAddIntRelease(Object o, long offset, int delta) {
        return (int) getAndAddIntRelease.invokeExact(o, offset, delta);
    }

    // long

    private static final MethodHandle getAndAddLong = PUnsafe.getNewUnsafeMethod("getAndAddLong", MethodType.methodType(long.class, Object.class, long.class, long.class));
    private static final MethodHandle getAndAddLongAcquire = PUnsafe.getNewUnsafeMethod("getAndAddLongAcquire", MethodType.methodType(long.class, Object.class, long.class, long.class));
    private static final MethodHandle getAndAddLongRelease = PUnsafe.getNewUnsafeMethod("getAndAddLongRelease", MethodType.methodType(long.class, Object.class, long.class, long.class));

    @SneakyThrows
    public static long getAndAddLong(Object o, long offset, long delta) {
        return (long) getAndAddLong.invokeExact(o, offset, delta);
    }

    @SneakyThrows
    public static long getAndAddLongAcquire(Object o, long offset, long delta) {
        return (long) getAndAddLongAcquire.invokeExact(o, offset, delta);
    }

    @SneakyThrows
    public static long getAndAddLongRelease(Object o, long offset, long delta) {
        return (long) getAndAddLongRelease.invokeExact(o, offset, delta);
    }

    // float

    private static final MethodHandle getAndAddFloat = PUnsafe.getNewUnsafeMethod("getAndAddFloat", MethodType.methodType(float.class, Object.class, long.class, float.class));
    private static final MethodHandle getAndAddFloatAcquire = PUnsafe.getNewUnsafeMethod("getAndAddFloatAcquire", MethodType.methodType(float.class, Object.class, long.class, float.class));
    private static final MethodHandle getAndAddFloatRelease = PUnsafe.getNewUnsafeMethod("getAndAddFloatRelease", MethodType.methodType(float.class, Object.class, long.class, float.class));

    @SneakyThrows
    public static float getAndAddFloat(Object o, long offset, float delta) {
        return (float) getAndAddFloat.invokeExact(o, offset, delta);
    }

    @SneakyThrows
    public static float getAndAddFloatAcquire(Object o, long offset, float delta) {
        return (float) getAndAddFloatAcquire.invokeExact(o, offset, delta);
    }

    @SneakyThrows
    public static float getAndAddFloatRelease(Object o, long offset, float delta) {
        return (float) getAndAddFloatRelease.invokeExact(o, offset, delta);
    }

    // double

    private static final MethodHandle getAndAddDouble = PUnsafe.getNewUnsafeMethod("getAndAddDouble", MethodType.methodType(double.class, Object.class, long.class, double.class));
    private static final MethodHandle getAndAddDoubleAcquire = PUnsafe.getNewUnsafeMethod("getAndAddDoubleAcquire", MethodType.methodType(double.class, Object.class, long.class, double.class));
    private static final MethodHandle getAndAddDoubleRelease = PUnsafe.getNewUnsafeMethod("getAndAddDoubleRelease", MethodType.methodType(double.class, Object.class, long.class, double.class));

    @SneakyThrows
    public static double getAndAddDouble(Object o, long offset, double delta) {
        return (double) getAndAddDouble.invokeExact(o, offset, delta);
    }

    @SneakyThrows
    public static double getAndAddDoubleAcquire(Object o, long offset, double delta) {
        return (double) getAndAddDoubleAcquire.invokeExact(o, offset, delta);
    }

    @SneakyThrows
    public static double getAndAddDoubleRelease(Object o, long offset, double delta) {
        return (double) getAndAddDoubleRelease.invokeExact(o, offset, delta);
    }

    //
    // BITWISE ATOMIC UPDATES
    //

    // boolean

    private static final MethodHandle getAndBitwiseOrBoolean = PUnsafe.getNewUnsafeMethod("getAndBitwiseOrBoolean", MethodType.methodType(boolean.class, Object.class, long.class, boolean.class));
    private static final MethodHandle getAndBitwiseOrBooleanAcquire = PUnsafe.getNewUnsafeMethod("getAndBitwiseOrBooleanAcquire", MethodType.methodType(boolean.class, Object.class, long.class, boolean.class));
    private static final MethodHandle getAndBitwiseOrBooleanRelease = PUnsafe.getNewUnsafeMethod("getAndBitwiseOrBooleanRelease", MethodType.methodType(boolean.class, Object.class, long.class, boolean.class));
    private static final MethodHandle getAndBitwiseAndBoolean = PUnsafe.getNewUnsafeMethod("getAndBitwiseAndBoolean", MethodType.methodType(boolean.class, Object.class, long.class, boolean.class));
    private static final MethodHandle getAndBitwiseAndBooleanAcquire = PUnsafe.getNewUnsafeMethod("getAndBitwiseAndBooleanAcquire", MethodType.methodType(boolean.class, Object.class, long.class, boolean.class));
    private static final MethodHandle getAndBitwiseAndBooleanRelease = PUnsafe.getNewUnsafeMethod("getAndBitwiseAndBooleanRelease", MethodType.methodType(boolean.class, Object.class, long.class, boolean.class));
    private static final MethodHandle getAndBitwiseXorBoolean = PUnsafe.getNewUnsafeMethod("getAndBitwiseXorBoolean", MethodType.methodType(boolean.class, Object.class, long.class, boolean.class));
    private static final MethodHandle getAndBitwiseXorBooleanAcquire = PUnsafe.getNewUnsafeMethod("getAndBitwiseXorBooleanAcquire", MethodType.methodType(boolean.class, Object.class, long.class, boolean.class));
    private static final MethodHandle getAndBitwiseXorBooleanRelease = PUnsafe.getNewUnsafeMethod("getAndBitwiseXorBooleanRelease", MethodType.methodType(boolean.class, Object.class, long.class, boolean.class));

    @SneakyThrows
    public static boolean getAndBitwiseOrBoolean(Object o, long offset, boolean mask) {
        return (boolean) getAndBitwiseOrBoolean.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static boolean getAndBitwiseOrBooleanAcquire(Object o, long offset, boolean mask) {
        return (boolean) getAndBitwiseOrBooleanAcquire.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static boolean getAndBitwiseOrBooleanRelease(Object o, long offset, boolean mask) {
        return (boolean) getAndBitwiseOrBooleanRelease.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static boolean getAndBitwiseAndBoolean(Object o, long offset, boolean mask) {
        return (boolean) getAndBitwiseAndBoolean.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static boolean getAndBitwiseAndBooleanAcquire(Object o, long offset, boolean mask) {
        return (boolean) getAndBitwiseAndBooleanAcquire.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static boolean getAndBitwiseAndBooleanRelease(Object o, long offset, boolean mask) {
        return (boolean) getAndBitwiseAndBooleanRelease.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static boolean getAndBitwiseXorBoolean(Object o, long offset, boolean mask) {
        return (boolean) getAndBitwiseXorBoolean.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static boolean getAndBitwiseXorBooleanAcquire(Object o, long offset, boolean mask) {
        return (boolean) getAndBitwiseXorBooleanAcquire.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static boolean getAndBitwiseXorBooleanRelease(Object o, long offset, boolean mask) {
        return (boolean) getAndBitwiseXorBooleanRelease.invokeExact(o, offset, mask);
    }

    // byte
    
    private static final MethodHandle getAndBitwiseOrByte = PUnsafe.getNewUnsafeMethod("getAndBitwiseOrByte", MethodType.methodType(byte.class, Object.class, long.class, byte.class));
    private static final MethodHandle getAndBitwiseOrByteAcquire = PUnsafe.getNewUnsafeMethod("getAndBitwiseOrByteAcquire", MethodType.methodType(byte.class, Object.class, long.class, byte.class));
    private static final MethodHandle getAndBitwiseOrByteRelease = PUnsafe.getNewUnsafeMethod("getAndBitwiseOrByteRelease", MethodType.methodType(byte.class, Object.class, long.class, byte.class));
    private static final MethodHandle getAndBitwiseAndByte = PUnsafe.getNewUnsafeMethod("getAndBitwiseAndByte", MethodType.methodType(byte.class, Object.class, long.class, byte.class));
    private static final MethodHandle getAndBitwiseAndByteAcquire = PUnsafe.getNewUnsafeMethod("getAndBitwiseAndByteAcquire", MethodType.methodType(byte.class, Object.class, long.class, byte.class));
    private static final MethodHandle getAndBitwiseAndByteRelease = PUnsafe.getNewUnsafeMethod("getAndBitwiseAndByteRelease", MethodType.methodType(byte.class, Object.class, long.class, byte.class));
    private static final MethodHandle getAndBitwiseXorByte = PUnsafe.getNewUnsafeMethod("getAndBitwiseXorByte", MethodType.methodType(byte.class, Object.class, long.class, byte.class));
    private static final MethodHandle getAndBitwiseXorByteAcquire = PUnsafe.getNewUnsafeMethod("getAndBitwiseXorByteAcquire", MethodType.methodType(byte.class, Object.class, long.class, byte.class));
    private static final MethodHandle getAndBitwiseXorByteRelease = PUnsafe.getNewUnsafeMethod("getAndBitwiseXorByteRelease", MethodType.methodType(byte.class, Object.class, long.class, byte.class));

    @SneakyThrows
    public static byte getAndBitwiseOrByte(Object o, long offset, byte mask) {
        return (byte) getAndBitwiseOrByte.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static byte getAndBitwiseOrByteAcquire(Object o, long offset, byte mask) {
        return (byte) getAndBitwiseOrByteAcquire.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static byte getAndBitwiseOrByteRelease(Object o, long offset, byte mask) {
        return (byte) getAndBitwiseOrByteRelease.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static byte getAndBitwiseAndByte(Object o, long offset, byte mask) {
        return (byte) getAndBitwiseAndByte.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static byte getAndBitwiseAndByteAcquire(Object o, long offset, byte mask) {
        return (byte) getAndBitwiseAndByteAcquire.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static byte getAndBitwiseAndByteRelease(Object o, long offset, byte mask) {
        return (byte) getAndBitwiseAndByteRelease.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static byte getAndBitwiseXorByte(Object o, long offset, byte mask) {
        return (byte) getAndBitwiseXorByte.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static byte getAndBitwiseXorByteAcquire(Object o, long offset, byte mask) {
        return (byte) getAndBitwiseXorByteAcquire.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static byte getAndBitwiseXorByteRelease(Object o, long offset, byte mask) {
        return (byte) getAndBitwiseXorByteRelease.invokeExact(o, offset, mask);
    }

    // short

    private static final MethodHandle getAndBitwiseOrShort = PUnsafe.getNewUnsafeMethod("getAndBitwiseOrShort", MethodType.methodType(short.class, Object.class, long.class, short.class));
    private static final MethodHandle getAndBitwiseOrShortAcquire = PUnsafe.getNewUnsafeMethod("getAndBitwiseOrShortAcquire", MethodType.methodType(short.class, Object.class, long.class, short.class));
    private static final MethodHandle getAndBitwiseOrShortRelease = PUnsafe.getNewUnsafeMethod("getAndBitwiseOrShortRelease", MethodType.methodType(short.class, Object.class, long.class, short.class));
    private static final MethodHandle getAndBitwiseAndShort = PUnsafe.getNewUnsafeMethod("getAndBitwiseAndShort", MethodType.methodType(short.class, Object.class, long.class, short.class));
    private static final MethodHandle getAndBitwiseAndShortAcquire = PUnsafe.getNewUnsafeMethod("getAndBitwiseAndShortAcquire", MethodType.methodType(short.class, Object.class, long.class, short.class));
    private static final MethodHandle getAndBitwiseAndShortRelease = PUnsafe.getNewUnsafeMethod("getAndBitwiseAndShortRelease", MethodType.methodType(short.class, Object.class, long.class, short.class));
    private static final MethodHandle getAndBitwiseXorShort = PUnsafe.getNewUnsafeMethod("getAndBitwiseXorShort", MethodType.methodType(short.class, Object.class, long.class, short.class));
    private static final MethodHandle getAndBitwiseXorShortAcquire = PUnsafe.getNewUnsafeMethod("getAndBitwiseXorShortAcquire", MethodType.methodType(short.class, Object.class, long.class, short.class));
    private static final MethodHandle getAndBitwiseXorShortRelease = PUnsafe.getNewUnsafeMethod("getAndBitwiseXorShortRelease", MethodType.methodType(short.class, Object.class, long.class, short.class));

    @SneakyThrows
    public static short getAndBitwiseOrShort(Object o, long offset, short mask) {
        return (short) getAndBitwiseOrShort.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static short getAndBitwiseOrShortAcquire(Object o, long offset, short mask) {
        return (short) getAndBitwiseOrShortAcquire.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static short getAndBitwiseOrShortRelease(Object o, long offset, short mask) {
        return (short) getAndBitwiseOrShortRelease.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static short getAndBitwiseAndShort(Object o, long offset, short mask) {
        return (short) getAndBitwiseAndShort.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static short getAndBitwiseAndShortAcquire(Object o, long offset, short mask) {
        return (short) getAndBitwiseAndShortAcquire.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static short getAndBitwiseAndShortRelease(Object o, long offset, short mask) {
        return (short) getAndBitwiseAndShortRelease.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static short getAndBitwiseXorShort(Object o, long offset, short mask) {
        return (short) getAndBitwiseXorShort.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static short getAndBitwiseXorShortAcquire(Object o, long offset, short mask) {
        return (short) getAndBitwiseXorShortAcquire.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static short getAndBitwiseXorShortRelease(Object o, long offset, short mask) {
        return (short) getAndBitwiseXorShortRelease.invokeExact(o, offset, mask);
    }

    // char

    private static final MethodHandle getAndBitwiseOrChar = PUnsafe.getNewUnsafeMethod("getAndBitwiseOrChar", MethodType.methodType(char.class, Object.class, long.class, char.class));
    private static final MethodHandle getAndBitwiseOrCharAcquire = PUnsafe.getNewUnsafeMethod("getAndBitwiseOrCharAcquire", MethodType.methodType(char.class, Object.class, long.class, char.class));
    private static final MethodHandle getAndBitwiseOrCharRelease = PUnsafe.getNewUnsafeMethod("getAndBitwiseOrCharRelease", MethodType.methodType(char.class, Object.class, long.class, char.class));
    private static final MethodHandle getAndBitwiseAndChar = PUnsafe.getNewUnsafeMethod("getAndBitwiseAndChar", MethodType.methodType(char.class, Object.class, long.class, char.class));
    private static final MethodHandle getAndBitwiseAndCharAcquire = PUnsafe.getNewUnsafeMethod("getAndBitwiseAndCharAcquire", MethodType.methodType(char.class, Object.class, long.class, char.class));
    private static final MethodHandle getAndBitwiseAndCharRelease = PUnsafe.getNewUnsafeMethod("getAndBitwiseAndCharRelease", MethodType.methodType(char.class, Object.class, long.class, char.class));
    private static final MethodHandle getAndBitwiseXorChar = PUnsafe.getNewUnsafeMethod("getAndBitwiseXorChar", MethodType.methodType(char.class, Object.class, long.class, char.class));
    private static final MethodHandle getAndBitwiseXorCharAcquire = PUnsafe.getNewUnsafeMethod("getAndBitwiseXorCharAcquire", MethodType.methodType(char.class, Object.class, long.class, char.class));
    private static final MethodHandle getAndBitwiseXorCharRelease = PUnsafe.getNewUnsafeMethod("getAndBitwiseXorCharRelease", MethodType.methodType(char.class, Object.class, long.class, char.class));

    @SneakyThrows
    public static char getAndBitwiseOrChar(Object o, long offset, char mask) {
        return (char) getAndBitwiseOrChar.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static char getAndBitwiseOrCharAcquire(Object o, long offset, char mask) {
        return (char) getAndBitwiseOrCharAcquire.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static char getAndBitwiseOrCharRelease(Object o, long offset, char mask) {
        return (char) getAndBitwiseOrCharRelease.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static char getAndBitwiseAndChar(Object o, long offset, char mask) {
        return (char) getAndBitwiseAndChar.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static char getAndBitwiseAndCharAcquire(Object o, long offset, char mask) {
        return (char) getAndBitwiseAndCharAcquire.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static char getAndBitwiseAndCharRelease(Object o, long offset, char mask) {
        return (char) getAndBitwiseAndCharRelease.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static char getAndBitwiseXorChar(Object o, long offset, char mask) {
        return (char) getAndBitwiseXorChar.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static char getAndBitwiseXorCharAcquire(Object o, long offset, char mask) {
        return (char) getAndBitwiseXorCharAcquire.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static char getAndBitwiseXorCharRelease(Object o, long offset, char mask) {
        return (char) getAndBitwiseXorCharRelease.invokeExact(o, offset, mask);
    }

    // int

    private static final MethodHandle getAndBitwiseOrInt = PUnsafe.getNewUnsafeMethod("getAndBitwiseOrInt", MethodType.methodType(int.class, Object.class, long.class, int.class));
    private static final MethodHandle getAndBitwiseOrIntAcquire = PUnsafe.getNewUnsafeMethod("getAndBitwiseOrIntAcquire", MethodType.methodType(int.class, Object.class, long.class, int.class));
    private static final MethodHandle getAndBitwiseOrIntRelease = PUnsafe.getNewUnsafeMethod("getAndBitwiseOrIntRelease", MethodType.methodType(int.class, Object.class, long.class, int.class));
    private static final MethodHandle getAndBitwiseAndInt = PUnsafe.getNewUnsafeMethod("getAndBitwiseAndInt", MethodType.methodType(int.class, Object.class, long.class, int.class));
    private static final MethodHandle getAndBitwiseAndIntAcquire = PUnsafe.getNewUnsafeMethod("getAndBitwiseAndIntAcquire", MethodType.methodType(int.class, Object.class, long.class, int.class));
    private static final MethodHandle getAndBitwiseAndIntRelease = PUnsafe.getNewUnsafeMethod("getAndBitwiseAndIntRelease", MethodType.methodType(int.class, Object.class, long.class, int.class));
    private static final MethodHandle getAndBitwiseXorInt = PUnsafe.getNewUnsafeMethod("getAndBitwiseXorInt", MethodType.methodType(int.class, Object.class, long.class, int.class));
    private static final MethodHandle getAndBitwiseXorIntAcquire = PUnsafe.getNewUnsafeMethod("getAndBitwiseXorIntAcquire", MethodType.methodType(int.class, Object.class, long.class, int.class));
    private static final MethodHandle getAndBitwiseXorIntRelease = PUnsafe.getNewUnsafeMethod("getAndBitwiseXorIntRelease", MethodType.methodType(int.class, Object.class, long.class, int.class));

    @SneakyThrows
    public static int getAndBitwiseOrInt(Object o, long offset, int mask) {
        return (int) getAndBitwiseOrInt.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static int getAndBitwiseOrIntAcquire(Object o, long offset, int mask) {
        return (int) getAndBitwiseOrIntAcquire.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static int getAndBitwiseOrIntRelease(Object o, long offset, int mask) {
        return (int) getAndBitwiseOrIntRelease.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static int getAndBitwiseAndInt(Object o, long offset, int mask) {
        return (int) getAndBitwiseAndInt.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static int getAndBitwiseAndIntAcquire(Object o, long offset, int mask) {
        return (int) getAndBitwiseAndIntAcquire.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static int getAndBitwiseAndIntRelease(Object o, long offset, int mask) {
        return (int) getAndBitwiseAndIntRelease.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static int getAndBitwiseXorInt(Object o, long offset, int mask) {
        return (int) getAndBitwiseXorInt.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static int getAndBitwiseXorIntAcquire(Object o, long offset, int mask) {
        return (int) getAndBitwiseXorIntAcquire.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static int getAndBitwiseXorIntRelease(Object o, long offset, int mask) {
        return (int) getAndBitwiseXorIntRelease.invokeExact(o, offset, mask);
    }

    // long

    private static final MethodHandle getAndBitwiseOrLong = PUnsafe.getNewUnsafeMethod("getAndBitwiseOrLong", MethodType.methodType(long.class, Object.class, long.class, long.class));
    private static final MethodHandle getAndBitwiseOrLongAcquire = PUnsafe.getNewUnsafeMethod("getAndBitwiseOrLongAcquire", MethodType.methodType(long.class, Object.class, long.class, long.class));
    private static final MethodHandle getAndBitwiseOrLongRelease = PUnsafe.getNewUnsafeMethod("getAndBitwiseOrLongRelease", MethodType.methodType(long.class, Object.class, long.class, long.class));
    private static final MethodHandle getAndBitwiseAndLong = PUnsafe.getNewUnsafeMethod("getAndBitwiseAndLong", MethodType.methodType(long.class, Object.class, long.class, long.class));
    private static final MethodHandle getAndBitwiseAndLongAcquire = PUnsafe.getNewUnsafeMethod("getAndBitwiseAndLongAcquire", MethodType.methodType(long.class, Object.class, long.class, long.class));
    private static final MethodHandle getAndBitwiseAndLongRelease = PUnsafe.getNewUnsafeMethod("getAndBitwiseAndLongRelease", MethodType.methodType(long.class, Object.class, long.class, long.class));
    private static final MethodHandle getAndBitwiseXorLong = PUnsafe.getNewUnsafeMethod("getAndBitwiseXorLong", MethodType.methodType(long.class, Object.class, long.class, long.class));
    private static final MethodHandle getAndBitwiseXorLongAcquire = PUnsafe.getNewUnsafeMethod("getAndBitwiseXorLongAcquire", MethodType.methodType(long.class, Object.class, long.class, long.class));
    private static final MethodHandle getAndBitwiseXorLongRelease = PUnsafe.getNewUnsafeMethod("getAndBitwiseXorLongRelease", MethodType.methodType(long.class, Object.class, long.class, long.class));

    @SneakyThrows
    public static long getAndBitwiseOrLong(Object o, long offset, long mask) {
        return (long) getAndBitwiseOrLong.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static long getAndBitwiseOrLongAcquire(Object o, long offset, long mask) {
        return (long) getAndBitwiseOrLongAcquire.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static long getAndBitwiseOrLongRelease(Object o, long offset, long mask) {
        return (long) getAndBitwiseOrLongRelease.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static long getAndBitwiseAndLong(Object o, long offset, long mask) {
        return (long) getAndBitwiseAndLong.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static long getAndBitwiseAndLongAcquire(Object o, long offset, long mask) {
        return (long) getAndBitwiseAndLongAcquire.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static long getAndBitwiseAndLongRelease(Object o, long offset, long mask) {
        return (long) getAndBitwiseAndLongRelease.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static long getAndBitwiseXorLong(Object o, long offset, long mask) {
        return (long) getAndBitwiseXorLong.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static long getAndBitwiseXorLongAcquire(Object o, long offset, long mask) {
        return (long) getAndBitwiseXorLongAcquire.invokeExact(o, offset, mask);
    }

    @SneakyThrows
    public static long getAndBitwiseXorLongRelease(Object o, long offset, long mask) {
        return (long) getAndBitwiseXorLongRelease.invokeExact(o, offset, mask);
    }
}
