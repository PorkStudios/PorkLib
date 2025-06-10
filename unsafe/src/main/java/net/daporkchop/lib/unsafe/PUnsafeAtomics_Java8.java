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

import lombok.experimental.UtilityClass;

import static net.daporkchop.lib.unsafe.UnsafePlatformInfo.*;

/**
 * Emulates the new memory ordering and atomic operations added in Java 9 using only intrinsics available in Java 8.
 *
 * @author DaPorkchop_
 */
@SuppressWarnings("unused")
@UtilityClass
class PUnsafeAtomics_Java8 {
    //
    // ACQUIRE LOADS
    //

    //for emulating this on Java 8:
    //  acquire is equivalent to a regular load followed by Unsafe#loadFence(): https://cr.openjdk.org/~shade/8132332/8132332.jdk.patch

    public static boolean getBooleanAcquire(Object base, long offset) {
        boolean value = PUnsafe.getBoolean(base, offset);
        PUnsafe.loadFence();
        return value;
    }

    public static byte getByteAcquire(Object base, long offset) {
        byte value = PUnsafe.getByte(base, offset);
        PUnsafe.loadFence();
        return value;
    }

    public static short getShortAcquire(Object base, long offset) {
        short value = PUnsafe.getShort(base, offset);
        PUnsafe.loadFence();
        return value;
    }

    public static char getCharAcquire(Object base, long offset) {
        char value = PUnsafe.getChar(base, offset);
        PUnsafe.loadFence();
        return value;
    }

    public static int getIntAcquire(Object base, long offset) {
        int value = PUnsafe.getInt(base, offset);
        PUnsafe.loadFence();
        return value;
    }

    public static long getLongAcquire(Object base, long offset) {
        long value = PUnsafe.getLong(base, offset);
        PUnsafe.loadFence();
        return value;
    }

    public static float getFloatAcquire(Object base, long offset) {
        float value = PUnsafe.getFloat(base, offset);
        PUnsafe.loadFence();
        return value;
    }

    public static double getDoubleAcquire(Object base, long offset) {
        double value = PUnsafe.getDouble(base, offset);
        PUnsafe.loadFence();
        return value;
    }

    public static Object getObjectAcquire(Object base, long offset) {
        Object value = PUnsafe.getObject(base, offset);
        PUnsafe.loadFence();
        return value;
    }

    //
    // RELEASE STORES
    //

    //for emulating this on Java 8:
    //  Unsafe.putOrdered*() has release semantics (https://github.com/openjdk/jdk9/blob/acf474a972939c89853947a59d03704a9f26e3c0/jdk/src/jdk.unsupported/share/classes/sun/misc/Unsafe.java#L1031-L1055),
    //  so we should use it where possible, otherwise fall back to volatile stores

    public static void putBooleanRelease(Object base, long offset, boolean val) {
        PUnsafe.putBooleanVolatile(base, offset, val);
    }

    public static void putByteRelease(Object base, long offset, byte val) {
        PUnsafe.putByteVolatile(base, offset, val);
    }

    public static void putShortRelease(Object base, long offset, short val) {
        PUnsafe.putShortVolatile(base, offset, val);
    }

    public static void putCharRelease(Object base, long offset, char val) {
        PUnsafe.putCharVolatile(base, offset, val);
    }

    public static void putIntRelease(Object base, long offset, int val) {
        PUnsafe.sun_misc_Unsafe.putOrderedInt(base, offset, val);
    }

    public static void putLongRelease(Object base, long offset, long val) {
        PUnsafe.sun_misc_Unsafe.putOrderedLong(base, offset, val);
    }

    public static void putFloatRelease(Object base, long offset, float val) {
        PUnsafe.sun_misc_Unsafe.putOrderedInt(base, offset, Float.floatToRawIntBits(val));
    }

    public static void putDoubleRelease(Object base, long offset, double val) {
        PUnsafe.sun_misc_Unsafe.putOrderedLong(base, offset, Double.doubleToRawLongBits(val));
    }

    public static void putObjectRelease(Object base, long offset, Object val) {
        PUnsafe.sun_misc_Unsafe.putOrderedObject(base, offset, val);
    }

    //
    // OPAQUE LOADS
    //

    public static boolean getBooleanOpaque(Object base, long offset) {
        return PUnsafe.getBooleanVolatile(base, offset);
    }

    public static byte getByteOpaque(Object base, long offset) {
        return PUnsafe.getByteVolatile(base, offset);
    }

    public static short getShortOpaque(Object base, long offset) {
        return PUnsafe.getShortVolatile(base, offset);
    }

    public static char getCharOpaque(Object base, long offset) {
        return PUnsafe.getCharVolatile(base, offset);
    }

    public static int getIntOpaque(Object base, long offset) {
        return PUnsafe.getIntVolatile(base, offset);
    }

    public static long getLongOpaque(Object base, long offset) {
        return PUnsafe.getLongVolatile(base, offset);
    }

    public static float getFloatOpaque(Object base, long offset) {
        return PUnsafe.getFloatVolatile(base, offset);
    }

    public static double getDoubleOpaque(Object base, long offset) {
        return PUnsafe.getDoubleVolatile(base, offset);
    }

    public static Object getObjectOpaque(Object base, long offset) {
        return PUnsafe.getObjectVolatile(base, offset);
    }

    //
    // OPAQUE STORES
    //

    public static void putBooleanOpaque(Object base, long offset, boolean val) {
        PUnsafe.putBooleanVolatile(base, offset, val);
    }

    public static void putByteOpaque(Object base, long offset, byte val) {
        PUnsafe.putByteVolatile(base, offset, val);
    }

    public static void putShortOpaque(Object base, long offset, short val) {
        PUnsafe.putShortVolatile(base, offset, val);
    }

    public static void putCharOpaque(Object base, long offset, char val) {
        PUnsafe.putCharVolatile(base, offset, val);
    }

    public static void putIntOpaque(Object base, long offset, int val) {
        PUnsafe.putIntVolatile(base, offset, val);
    }

    public static void putLongOpaque(Object base, long offset, long val) {
        PUnsafe.putLongVolatile(base, offset, val);
    }

    public static void putFloatOpaque(Object base, long offset, float val) {
        PUnsafe.putFloatVolatile(base, offset, val);
    }

    public static void putDoubleOpaque(Object base, long offset, double val) {
        PUnsafe.putDoubleVolatile(base, offset, val);
    }

    public static void putObjectOpaque(Object base, long offset, Object val) {
        PUnsafe.putObjectVolatile(base, offset, val);
    }

    //
    // JAVA 9+ ATOMIC UPDATES
    //

    //TODO: some of these have more efficient default implementations (in particular getAndSet*), are they worth adding?

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
            fullWord = PUnsafe.getIntVolatile(o, wordOffset);
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
            curr = PUnsafe.getByteVolatile(o, offset);
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
            fullWord = PUnsafe.getIntVolatile(o, wordOffset);
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
            curr = PUnsafe.getShortVolatile(o, offset);
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
        return PUnsafe.sun_misc_Unsafe.compareAndSwapInt(o, offset, expected, newValue);
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
            curr = PUnsafe.getIntVolatile(o, offset);
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
        return PUnsafe.sun_misc_Unsafe.getAndSetInt(o, offset, newValue);
    }

    public static int getAndSetIntAcquire(Object o, long offset, int newValue) {
        return getAndSetInt(o, offset, newValue);
    }

    public static int getAndSetIntRelease(Object o, long offset, int newValue) {
        return getAndSetInt(o, offset, newValue);
    }

    // long

    public static boolean compareAndSetLong(Object o, long offset, long expected, long newValue) {
        return PUnsafe.sun_misc_Unsafe.compareAndSwapLong(o, offset, expected, newValue);
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
            curr = PUnsafe.getLongVolatile(o, offset);
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
        return PUnsafe.sun_misc_Unsafe.getAndSetLong(o, offset, newValue);
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
        return PUnsafe.sun_misc_Unsafe.compareAndSwapObject(o, offset, expected, newValue);
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

    public static Object compareAndExchangeObject(Object o, long offset, Object expected, Object newValue) {
        Object curr;
        do {
            curr = PUnsafe.getObjectVolatile(o, offset);
        } while (curr == expected && !weakCompareAndSetObject(o, offset, expected, newValue));
        return  curr; // either curr != expected (so compare failed and we return curr), or curr == expected and CAS succeeded
    }

    public static Object compareAndExchangeObjectAcquire(Object o, long offset, Object expected, Object newValue) {
        return compareAndExchangeObject(o, offset, expected, newValue);
    }

    public static Object compareAndExchangeObjectRelease(Object o, long offset, Object expected, Object newValue) {
        return compareAndExchangeObject(o, offset, expected, newValue);
    }

    public static Object getAndSetObject(Object o, long offset, Object newValue) {
        return PUnsafe.sun_misc_Unsafe.getAndSetObject(o, offset, newValue);
    }

    public static Object getAndSetObjectAcquire(Object o, long offset, Object newValue) {
        return getAndSetObject(o, offset, newValue);
    }

    public static Object getAndSetObjectRelease(Object o, long offset, Object newValue) {
        return getAndSetObject(o, offset, newValue);
    }

    //
    // NUMERIC ATOMIC UPDATES
    //

    // byte

    public static byte getAndAddByte(Object o, long offset, byte delta) {
        byte curr;
        do {
            curr = PUnsafe.getByteVolatile(o, offset);
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
            curr = PUnsafe.getShortVolatile(o, offset);
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
            curr = PUnsafe.getCharVolatile(o, offset);
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
        return PUnsafe.sun_misc_Unsafe.getAndAddInt(o, offset, delta);
    }

    public static int getAndAddIntAcquire(Object o, long offset, int delta) {
        return getAndAddInt(o, offset, delta);
    }

    public static int getAndAddIntRelease(Object o, long offset, int delta) {
        return getAndAddInt(o, offset, delta);
    }

    // long

    public static long getAndAddLong(Object o, long offset, long delta) {
        return PUnsafe.sun_misc_Unsafe.getAndAddLong(o, offset, delta);
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
            currBits = PUnsafe.getIntVolatile(o, offset);
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
            currBits = PUnsafe.getLongVolatile(o, offset);
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
            curr = PUnsafe.getByteVolatile(o, offset);
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
            curr = PUnsafe.getByteVolatile(o, offset);
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
            curr = PUnsafe.getByteVolatile(o, offset);
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
            curr = PUnsafe.getShortVolatile(o, offset);
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
            curr = PUnsafe.getShortVolatile(o, offset);
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
            curr = PUnsafe.getShortVolatile(o, offset);
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
            curr = PUnsafe.getIntVolatile(o, offset);
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
            curr = PUnsafe.getIntVolatile(o, offset);
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
            curr = PUnsafe.getIntVolatile(o, offset);
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
            curr = PUnsafe.getLongVolatile(o, offset);
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
            curr = PUnsafe.getLongVolatile(o, offset);
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
            curr = PUnsafe.getLongVolatile(o, offset);
        } while (!weakCompareAndSetLong(o, offset, curr, curr ^ mask));
        return curr;
    }

    public static long getAndBitwiseXorLongAcquire(Object o, long offset, long mask) {
        return getAndBitwiseXorLong(o, offset, mask);
    }

    public static long getAndBitwiseXorLongRelease(Object o, long offset, long mask) {
        return getAndBitwiseXorLong(o, offset, mask);
    }
}
