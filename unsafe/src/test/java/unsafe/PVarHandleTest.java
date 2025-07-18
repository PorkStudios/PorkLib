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

package unsafe;

import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.unsafe.PVarHandle;
import org.junit.Test;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.Assert.*;

/**
 * @author DaPorkchop_
 */
public class PVarHandleTest {
    public static boolean booleanPlainStatic;
    public static byte bytePlainStatic;
    public static short shortPlainStatic;
    public static char charPlainStatic;
    public static int intPlainStatic;
    public static long longPlainStatic;
    public static float floatPlainStatic;
    public static double doublePlainStatic;
    public static String stringPlainStatic;

    public boolean booleanPlain;
    public byte bytePlain;
    public short shortPlain;
    public char charPlain;
    public int intPlain;
    public long longPlain;
    public float floatPlain;
    public double doublePlain;
    public String stringPlain;

    //although there is a lot of nearly identical code here, i'm not abstracting it out in all cases because i want to use invokeExact()
    //  to verify that the MethodHandle types are correct at the same time

    @Test
    public synchronized void testInstanceAccessValidInt() throws Throwable {
        PVarHandle handle = PVarHandle.forField(MethodHandles.lookup(), PVarHandleTest.class, "intPlain", int.class);
        int expected = 0;
        this.intPlain = expected;

        for (MethodHandle setter : Arrays.asList(handle.setPlainInvoker(), handle.setOpaqueInvoker(), handle.setReleaseInvoker(), handle.setVolatileInvoker())) {
            setter.invokeExact(this, ++expected);
            assertEquals(expected, this.intPlain);
        }

        for (MethodHandle getter : Arrays.asList(handle.getPlainInvoker(), handle.getOpaqueInvoker(), handle.getAcquireInvoker(), handle.getVolatileInvoker())) {
            this.intPlain = ++expected;
            assertEquals(expected, (int) getter.invokeExact(this));
        }

        assertTrue((boolean) handle.compareAndSetInvoker().invokeExact(this, expected, ++expected));
        assertEquals(expected, this.intPlain);

        for (MethodHandle weakCAS : Arrays.asList(handle.weakCompareAndSetInvoker(), handle.weakCompareAndSetPlainInvoker(), handle.weakCompareAndSetAcquireInvoker(), handle.weakCompareAndSetReleaseInvoker())) {
            while (!(boolean) weakCAS.invokeExact(this, expected, expected + 1)) ;
            assertEquals(++expected, this.intPlain);
        }

        for (MethodHandle compareExchange : Arrays.asList(handle.compareAndExchangeInvoker(), handle.compareAndExchangeAcquireInvoker(), handle.compareAndExchangeReleaseInvoker())) {
            assertEquals(expected, (int) compareExchange.invokeExact(this, expected, ++expected));
            assertEquals(expected, this.intPlain);
        }

        for (MethodHandle getAndSet : Arrays.asList(handle.getAndSetInvoker(), handle.getAndSetAcquireInvoker(), handle.getAndSetReleaseInvoker())) {
            assertEquals(expected, (int) getAndSet.invokeExact(this, ++expected));
            assertEquals(expected, this.intPlain);
        }

        for (MethodHandle getAndAdd : Arrays.asList(handle.getAndAddInvoker(), handle.getAndAddAcquireInvoker(), handle.getAndAddReleaseInvoker())) {
            assertEquals(expected, (int) getAndAdd.invokeExact(this, 1));
            assertEquals(++expected, this.intPlain);
        }

        for (MethodHandle getAndBitwiseOr : Arrays.asList(handle.getAndBitwiseOrInvoker(), handle.getAndBitwiseOrAcquireInvoker(), handle.getAndBitwiseOrReleaseInvoker())) {
            this.intPlain = 0b0011;
            assertEquals(0b0011, (int) getAndBitwiseOr.invokeExact(this, 0b0101));
            assertEquals(0b0111, this.intPlain);
        }

        for (MethodHandle getAndBitwiseAnd : Arrays.asList(handle.getAndBitwiseAndInvoker(), handle.getAndBitwiseAndAcquireInvoker(), handle.getAndBitwiseAndReleaseInvoker())) {
            this.intPlain = 0b0011;
            assertEquals(0b0011, (int) getAndBitwiseAnd.invokeExact(this, 0b0101));
            assertEquals(0b0001, this.intPlain);
        }

        for (MethodHandle getAndBitwiseXor : Arrays.asList(handle.getAndBitwiseXorInvoker(), handle.getAndBitwiseXorAcquireInvoker(), handle.getAndBitwiseXorReleaseInvoker())) {
            this.intPlain = 0b0011;
            assertEquals(0b0011, (int) getAndBitwiseXor.invokeExact(this, 0b0101));
            assertEquals(0b0110, this.intPlain);
        }
    }

    @Test
    public synchronized void testStaticAccessValidInt() throws Throwable {
        PVarHandle handle = PVarHandle.forStaticField(MethodHandles.lookup(), PVarHandleTest.class, "intPlainStatic", int.class);
        int expected = 0;
        intPlainStatic = expected;

        for (MethodHandle setter : Arrays.asList(handle.setPlainInvoker(), handle.setOpaqueInvoker(), handle.setReleaseInvoker(), handle.setVolatileInvoker())) {
            setter.invokeExact(++expected);
            assertEquals(expected, intPlainStatic);
        }

        for (MethodHandle getter : Arrays.asList(handle.getPlainInvoker(), handle.getOpaqueInvoker(), handle.getAcquireInvoker(), handle.getVolatileInvoker())) {
            intPlainStatic = ++expected;
            assertEquals(expected, (int) getter.invokeExact());
        }

        assertTrue((boolean) handle.compareAndSetInvoker().invokeExact(expected, ++expected));
        assertEquals(expected, intPlainStatic);

        for (MethodHandle weakCAS : Arrays.asList(handle.weakCompareAndSetInvoker(), handle.weakCompareAndSetPlainInvoker(), handle.weakCompareAndSetAcquireInvoker(), handle.weakCompareAndSetReleaseInvoker())) {
            while (!(boolean) weakCAS.invokeExact(expected, expected + 1)) ;
            assertEquals(++expected, intPlainStatic);
        }

        for (MethodHandle compareExchange : Arrays.asList(handle.compareAndExchangeInvoker(), handle.compareAndExchangeAcquireInvoker(), handle.compareAndExchangeReleaseInvoker())) {
            assertEquals(expected, (int) compareExchange.invokeExact(expected, ++expected));
            assertEquals(expected, intPlainStatic);
        }

        for (MethodHandle getAndSet : Arrays.asList(handle.getAndSetInvoker(), handle.getAndSetAcquireInvoker(), handle.getAndSetReleaseInvoker())) {
            assertEquals(expected, (int) getAndSet.invokeExact(++expected));
            assertEquals(expected, intPlainStatic);
        }

        for (MethodHandle getAndAdd : Arrays.asList(handle.getAndAddInvoker(), handle.getAndAddAcquireInvoker(), handle.getAndAddReleaseInvoker())) {
            assertEquals(expected, (int) getAndAdd.invokeExact(1));
            assertEquals(++expected, intPlainStatic);
        }

        for (MethodHandle getAndBitwiseOr : Arrays.asList(handle.getAndBitwiseOrInvoker(), handle.getAndBitwiseOrAcquireInvoker(), handle.getAndBitwiseOrReleaseInvoker())) {
            intPlainStatic = 0b0011;
            assertEquals(0b0011, (int) getAndBitwiseOr.invokeExact(0b0101));
            assertEquals(0b0111, intPlainStatic);
        }

        for (MethodHandle getAndBitwiseAnd : Arrays.asList(handle.getAndBitwiseAndInvoker(), handle.getAndBitwiseAndAcquireInvoker(), handle.getAndBitwiseAndReleaseInvoker())) {
            intPlainStatic = 0b0011;
            assertEquals(0b0011, (int) getAndBitwiseAnd.invokeExact(0b0101));
            assertEquals(0b0001, intPlainStatic);
        }

        for (MethodHandle getAndBitwiseXor : Arrays.asList(handle.getAndBitwiseXorInvoker(), handle.getAndBitwiseXorAcquireInvoker(), handle.getAndBitwiseXorReleaseInvoker())) {
            intPlainStatic = 0b0011;
            assertEquals(0b0011, (int) getAndBitwiseXor.invokeExact(0b0101));
            assertEquals(0b0110, intPlainStatic);
        }
    }

    @Test
    public void testArrayAccessValidInt() throws Throwable {
        PVarHandle handle = PVarHandle.forArrayElement(MethodHandles.lookup(), int[].class);
        int[] arr = new int[1];
        int expected = 0;

        assertEquals(expected, arr[0]);

        for (MethodHandle setter : Arrays.asList(handle.setPlainInvoker(), handle.setOpaqueInvoker(), handle.setReleaseInvoker(), handle.setVolatileInvoker())) {
            setter.invokeExact(arr, 0, ++expected);
            assertEquals(expected, arr[0]);
        }

        for (MethodHandle getter : Arrays.asList(handle.getPlainInvoker(), handle.getOpaqueInvoker(), handle.getAcquireInvoker(), handle.getVolatileInvoker())) {
            arr[0] = ++expected;
            assertEquals(expected, (int) getter.invokeExact(arr, 0));
        }

        assertTrue((boolean) handle.compareAndSetInvoker().invokeExact(arr, 0, expected, ++expected));
        assertEquals(expected, arr[0]);

        for (MethodHandle weakCAS : Arrays.asList(handle.weakCompareAndSetInvoker(), handle.weakCompareAndSetPlainInvoker(), handle.weakCompareAndSetAcquireInvoker(), handle.weakCompareAndSetReleaseInvoker())) {
            while (!(boolean) weakCAS.invokeExact(arr, 0, expected, expected + 1)) ;
            assertEquals(++expected, arr[0]);
        }

        for (MethodHandle compareExchange : Arrays.asList(handle.compareAndExchangeInvoker(), handle.compareAndExchangeAcquireInvoker(), handle.compareAndExchangeReleaseInvoker())) {
            assertEquals(expected, (int) compareExchange.invokeExact(arr, 0, expected, ++expected));
            assertEquals(expected, arr[0]);
        }

        for (MethodHandle getAndSet : Arrays.asList(handle.getAndSetInvoker(), handle.getAndSetAcquireInvoker(), handle.getAndSetReleaseInvoker())) {
            assertEquals(expected, (int) getAndSet.invokeExact(arr, 0, ++expected));
            assertEquals(expected, arr[0]);
        }

        for (MethodHandle getAndAdd : Arrays.asList(handle.getAndAddInvoker(), handle.getAndAddAcquireInvoker(), handle.getAndAddReleaseInvoker())) {
            assertEquals(expected, (int) getAndAdd.invokeExact(arr, 0, 1));
            assertEquals(++expected, arr[0]);
        }

        for (MethodHandle getAndBitwiseOr : Arrays.asList(handle.getAndBitwiseOrInvoker(), handle.getAndBitwiseOrAcquireInvoker(), handle.getAndBitwiseOrReleaseInvoker())) {
            arr[0] = 0b0011;
            assertEquals(0b0011, (int) getAndBitwiseOr.invokeExact(arr, 0, 0b0101));
            assertEquals(0b0111, arr[0]);
        }

        for (MethodHandle getAndBitwiseAnd : Arrays.asList(handle.getAndBitwiseAndInvoker(), handle.getAndBitwiseAndAcquireInvoker(), handle.getAndBitwiseAndReleaseInvoker())) {
            arr[0] = 0b0011;
            assertEquals(0b0011, (int) getAndBitwiseAnd.invokeExact(arr, 0, 0b0101));
            assertEquals(0b0001, arr[0]);
        }

        for (MethodHandle getAndBitwiseXor : Arrays.asList(handle.getAndBitwiseXorInvoker(), handle.getAndBitwiseXorAcquireInvoker(), handle.getAndBitwiseXorReleaseInvoker())) {
            arr[0] = 0b0011;
            assertEquals(0b0011, (int) getAndBitwiseXor.invokeExact(arr, 0, 0b0101));
            assertEquals(0b0110, arr[0]);
        }
    }

    //test with byte to ensure that the emulated atomic operations on java 8 work

    @Test
    public void testArrayAccessValidByte() throws Throwable {
        PVarHandle handle = PVarHandle.forArrayElement(MethodHandles.lookup(), byte[].class);
        byte[] arr = new byte[1];
        byte expected = 0;

        assertEquals(expected, arr[0]);

        for (MethodHandle setter : Arrays.asList(handle.setPlainInvoker(), handle.setOpaqueInvoker(), handle.setReleaseInvoker(), handle.setVolatileInvoker())) {
            setter.invokeExact(arr, 0, ++expected);
            assertEquals(expected, arr[0]);
        }

        for (MethodHandle getter : Arrays.asList(handle.getPlainInvoker(), handle.getOpaqueInvoker(), handle.getAcquireInvoker(), handle.getVolatileInvoker())) {
            arr[0] = ++expected;
            assertEquals(expected, (byte) getter.invokeExact(arr, 0));
        }

        assertTrue((boolean) handle.compareAndSetInvoker().invokeExact(arr, 0, expected, ++expected));
        assertEquals(expected, arr[0]);

        for (MethodHandle weakCAS : Arrays.asList(handle.weakCompareAndSetInvoker(), handle.weakCompareAndSetPlainInvoker(), handle.weakCompareAndSetAcquireInvoker(), handle.weakCompareAndSetReleaseInvoker())) {
            while (!(boolean) weakCAS.invokeExact(arr, 0, expected, (byte) (expected + 1))) ;
            assertEquals(++expected, arr[0]);
        }

        for (MethodHandle compareExchange : Arrays.asList(handle.compareAndExchangeInvoker(), handle.compareAndExchangeAcquireInvoker(), handle.compareAndExchangeReleaseInvoker())) {
            assertEquals(expected, (byte) compareExchange.invokeExact(arr, 0, expected, ++expected));
            assertEquals(expected, arr[0]);
        }

        for (MethodHandle getAndSet : Arrays.asList(handle.getAndSetInvoker(), handle.getAndSetAcquireInvoker(), handle.getAndSetReleaseInvoker())) {
            assertEquals(expected, (byte) getAndSet.invokeExact(arr, 0, ++expected));
            assertEquals(expected, arr[0]);
        }

        for (MethodHandle getAndAdd : Arrays.asList(handle.getAndAddInvoker(), handle.getAndAddAcquireInvoker(), handle.getAndAddReleaseInvoker())) {
            assertEquals(expected, (byte) getAndAdd.invokeExact(arr, 0, (byte) 1));
            assertEquals(++expected, arr[0]);
        }

        for (MethodHandle getAndBitwiseOr : Arrays.asList(handle.getAndBitwiseOrInvoker(), handle.getAndBitwiseOrAcquireInvoker(), handle.getAndBitwiseOrReleaseInvoker())) {
            arr[0] = 0b0011;
            assertEquals(0b0011, (byte) getAndBitwiseOr.invokeExact(arr, 0, (byte) 0b0101));
            assertEquals(0b0111, arr[0]);
        }

        for (MethodHandle getAndBitwiseAnd : Arrays.asList(handle.getAndBitwiseAndInvoker(), handle.getAndBitwiseAndAcquireInvoker(), handle.getAndBitwiseAndReleaseInvoker())) {
            arr[0] = 0b0011;
            assertEquals(0b0011, (byte) getAndBitwiseAnd.invokeExact(arr, 0, (byte) 0b0101));
            assertEquals(0b0001, arr[0]);
        }

        for (MethodHandle getAndBitwiseXor : Arrays.asList(handle.getAndBitwiseXorInvoker(), handle.getAndBitwiseXorAcquireInvoker(), handle.getAndBitwiseXorReleaseInvoker())) {
            arr[0] = 0b0011;
            assertEquals(0b0011, (byte) getAndBitwiseXor.invokeExact(arr, 0, (byte) 0b0101));
            assertEquals(0b0110, arr[0]);
        }
    }

    //test with double to ensure that the emulated atomic operations on java 8 work

    @Test
    public void testArrayAccessValidDouble() throws Throwable {
        PVarHandle handle = PVarHandle.forArrayElement(MethodHandles.lookup(), double[].class);
        double[] arr = new double[1];
        double expected = 0;

        assertEquals(expected, arr[0], Double.MIN_VALUE);

        for (MethodHandle setter : Arrays.asList(handle.setPlainInvoker(), handle.setOpaqueInvoker(), handle.setReleaseInvoker(), handle.setVolatileInvoker())) {
            setter.invokeExact(arr, 0, ++expected);
            assertEquals(expected, arr[0], Double.MIN_VALUE);
        }

        for (MethodHandle getter : Arrays.asList(handle.getPlainInvoker(), handle.getOpaqueInvoker(), handle.getAcquireInvoker(), handle.getVolatileInvoker())) {
            arr[0] = ++expected;
            assertEquals(expected, (double) getter.invokeExact(arr, 0), Double.MIN_VALUE);
        }

        assertTrue((boolean) handle.compareAndSetInvoker().invokeExact(arr, 0, expected, ++expected));
        assertEquals(expected, arr[0], Double.MIN_VALUE);

        for (MethodHandle weakCAS : Arrays.asList(handle.weakCompareAndSetInvoker(), handle.weakCompareAndSetPlainInvoker(), handle.weakCompareAndSetAcquireInvoker(), handle.weakCompareAndSetReleaseInvoker())) {
            while (!(boolean) weakCAS.invokeExact(arr, 0, expected, expected + 1)) ;
            assertEquals(++expected, arr[0], Double.MIN_VALUE);
        }

        for (MethodHandle compareExchange : Arrays.asList(handle.compareAndExchangeInvoker(), handle.compareAndExchangeAcquireInvoker(), handle.compareAndExchangeReleaseInvoker())) {
            assertEquals(expected, (double) compareExchange.invokeExact(arr, 0, expected, ++expected), Double.MIN_VALUE);
            assertEquals(expected, arr[0], Double.MIN_VALUE);
        }

        for (MethodHandle getAndSet : Arrays.asList(handle.getAndSetInvoker(), handle.getAndSetAcquireInvoker(), handle.getAndSetReleaseInvoker())) {
            assertEquals(expected, (double) getAndSet.invokeExact(arr, 0, ++expected), Double.MIN_VALUE);
            assertEquals(expected, arr[0], Double.MIN_VALUE);
        }

        for (MethodHandle getAndAdd : Arrays.asList(handle.getAndAddInvoker(), handle.getAndAddAcquireInvoker(), handle.getAndAddReleaseInvoker())) {
            assertEquals(expected, (double) getAndAdd.invokeExact(arr, 0, 1.0d), Double.MIN_VALUE);
            assertEquals(++expected, arr[0], Double.MIN_VALUE);
        }
    }

    //test with double to ensure that the method signatures aren't using raw types anywhere

    @Test
    public void testArrayAccessValidObject() throws Throwable {
        PVarHandle handle = PVarHandle.forArrayElement(MethodHandles.lookup(), String[].class);
        String[] arr = { "0" };
        int expected = 0;

        assertEquals(String.valueOf(expected), arr[0]);

        for (MethodHandle setter : Arrays.asList(handle.setPlainInvoker(), handle.setOpaqueInvoker(), handle.setReleaseInvoker(), handle.setVolatileInvoker())) {
            setter.invokeExact(arr, 0, String.valueOf(++expected));
            assertEquals(String.valueOf(expected), arr[0]);
        }

        for (MethodHandle getter : Arrays.asList(handle.getPlainInvoker(), handle.getOpaqueInvoker(), handle.getAcquireInvoker(), handle.getVolatileInvoker())) {
            arr[0] = String.valueOf(++expected);
            assertEquals(String.valueOf(expected), (String) getter.invokeExact(arr, 0));
        }

        arr[0] = arr[0].intern();
        assertTrue((boolean) handle.compareAndSetInvoker().invokeExact(arr, 0, String.valueOf(expected).intern(), String.valueOf(++expected).intern()));
        assertEquals(String.valueOf(expected), arr[0]);

        for (MethodHandle weakCAS : Arrays.asList(handle.weakCompareAndSetInvoker(), handle.weakCompareAndSetPlainInvoker(), handle.weakCompareAndSetAcquireInvoker(), handle.weakCompareAndSetReleaseInvoker())) {
            while (!(boolean) weakCAS.invokeExact(arr, 0, String.valueOf(expected).intern(), String.valueOf(expected + 1).intern())) ;
            assertEquals(String.valueOf(++expected), arr[0]);
        }

        for (MethodHandle compareExchange : Arrays.asList(handle.compareAndExchangeInvoker(), handle.compareAndExchangeAcquireInvoker(), handle.compareAndExchangeReleaseInvoker())) {
            assertEquals(String.valueOf(expected), (String) compareExchange.invokeExact(arr, 0, String.valueOf(expected).intern(), String.valueOf(++expected).intern()));
            assertEquals(String.valueOf(expected), arr[0]);
        }

        for (MethodHandle getAndSet : Arrays.asList(handle.getAndSetInvoker(), handle.getAndSetAcquireInvoker(), handle.getAndSetReleaseInvoker())) {
            assertEquals(String.valueOf(expected), (String) getAndSet.invokeExact(arr, 0, String.valueOf(++expected)));
            assertEquals(String.valueOf(expected), arr[0]);
        }
    }

    @RequiredArgsConstructor
    private static abstract class AbstractState<T> {
        public abstract T getCurrent();

        public abstract void setCurrent(T value);
    }

    @RequiredArgsConstructor
    private static abstract class ExpectedState<T> {
        public final T one;

        public abstract T getExpected();

        public abstract T incrementAndGetExpected();

        public static final ExpectedState<Byte> BYTE = new ExpectedState<Byte>((byte) 1) {
            private byte expected;
            
            @Override
            public Byte getExpected() {
                return this.expected;
            }

            @Override
            public Byte incrementAndGetExpected() {
                return ++this.expected;
            }
        };

        public static final ExpectedState<Short> SHORT = new ExpectedState<Short>((short) 1) {
            private short expected;
            
            @Override
            public Short getExpected() {
                return this.expected;
            }

            @Override
            public Short incrementAndGetExpected() {
                return ++this.expected;
            }
        };

        public static final ExpectedState<Character> CHAR = new ExpectedState<Character>((char) 1) {
            private char expected;
            
            @Override
            public Character getExpected() {
                return this.expected;
            }

            @Override
            public Character incrementAndGetExpected() {
                return ++this.expected;
            }
        };

        public static final ExpectedState<Integer> INT = new ExpectedState<Integer>((int) 1) {
            private int expected;
            
            @Override
            public Integer getExpected() {
                return this.expected;
            }

            @Override
            public Integer incrementAndGetExpected() {
                return ++this.expected;
            }
        };

        public static final ExpectedState<Long> LONG = new ExpectedState<Long>((long) 1) {
            private long expected;
            
            @Override
            public Long getExpected() {
                return this.expected;
            }

            @Override
            public Long incrementAndGetExpected() {
                return ++this.expected;
            }
        };

        public static final ExpectedState<Float> FLOAT = new ExpectedState<Float>((float) 1) {
            private float expected;
            
            @Override
            public Float getExpected() {
                return this.expected;
            }

            @Override
            public Float incrementAndGetExpected() {
                return ++this.expected;
            }
        };

        public static final ExpectedState<Double> DOUBLE = new ExpectedState<Double>((double) 1) {
            private double expected;
            
            @Override
            public Double getExpected() {
                return this.expected;
            }

            @Override
            public Double incrementAndGetExpected() {
                return ++this.expected;
            }
        };

        public static final ExpectedState<String> STRING = new ExpectedState<String>(null) {
            private int expected;
            
            @Override
            public String getExpected() {
                return String.valueOf(this.expected).intern();
            }

            @Override
            public String incrementAndGetExpected() {
                return String.valueOf(++this.expected).intern();
            }
        };
    }

    @SafeVarargs
    private static <T> T[] concat(T[] a, T... b) {
        T[] result = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    private static <T> void testValidAbstract(PVarHandle handle, Object[] initialArgs, ExpectedState<T> expected, Supplier<T> getCurrent, Consumer<T> setCurrent, boolean numeric, boolean bitwise) throws Throwable {
        setCurrent.accept(expected.getExpected());
        assertEquals(expected.getExpected(), getCurrent.get());

        for (MethodHandle setter : Arrays.asList(handle.setPlainInvoker(), handle.setOpaqueInvoker(), handle.setReleaseInvoker(), handle.setVolatileInvoker())) {
            setter.invokeWithArguments(concat(initialArgs, expected.incrementAndGetExpected()));
            assertEquals(expected.getExpected(), getCurrent.get());
        }

        for (MethodHandle getter : Arrays.asList(handle.getPlainInvoker(), handle.getOpaqueInvoker(), handle.getAcquireInvoker(), handle.getVolatileInvoker())) {
            setCurrent.accept(expected.incrementAndGetExpected());
            assertEquals(expected.getExpected(), getter.invokeWithArguments(initialArgs));
        }

        assertTrue((boolean) handle.compareAndSetInvoker().invokeWithArguments(concat(initialArgs, expected.getExpected(), expected.incrementAndGetExpected())));
        assertEquals(expected.getExpected(), getCurrent.get());

        for (MethodHandle weakCAS : Arrays.asList(handle.weakCompareAndSetInvoker(), handle.weakCompareAndSetPlainInvoker(), handle.weakCompareAndSetAcquireInvoker(), handle.weakCompareAndSetReleaseInvoker())) {
            T prev = expected.getExpected(), next = expected.incrementAndGetExpected();
            while (!(boolean) weakCAS.invokeWithArguments(concat(initialArgs, prev, next))) ;
            assertEquals(expected.getExpected(), getCurrent.get());
        }

        for (MethodHandle compareExchange : Arrays.asList(handle.compareAndExchangeInvoker(), handle.compareAndExchangeAcquireInvoker(), handle.compareAndExchangeReleaseInvoker())) {
            assertEquals(expected.getExpected(), compareExchange.invokeWithArguments(concat(initialArgs, expected.getExpected(), expected.incrementAndGetExpected())));
            assertEquals(expected.getExpected(), getCurrent.get());
        }

        for (MethodHandle getAndSet : Arrays.asList(handle.getAndSetInvoker(), handle.getAndSetAcquireInvoker(), handle.getAndSetReleaseInvoker())) {
            assertEquals(expected.getExpected(), getAndSet.invokeWithArguments(concat(initialArgs, expected.incrementAndGetExpected())));
            assertEquals(expected.getExpected(), getCurrent.get());
        }

        if (numeric) {
            for (MethodHandle getAndAdd : Arrays.asList(handle.getAndAddInvoker(), handle.getAndAddAcquireInvoker(), handle.getAndAddReleaseInvoker())) {
                assertEquals(expected.getExpected(), getAndAdd.invokeWithArguments(concat(initialArgs, expected.one)));
                assertEquals(expected.incrementAndGetExpected(), getCurrent.get());
            }
        }

        if (bitwise) {
            //TODO: bitwise operations?
        }
    }

    @Test
    public void testInstanceValidAbstract() throws Throwable {
        testValidAbstract(
                PVarHandle.forField(MethodHandles.lookup(), PVarHandleTest.class, "bytePlain", byte.class),
                new Object[]{ this }, ExpectedState.BYTE,
                () -> this.bytePlain, value -> this.bytePlain = value,
                true, true);
        
        testValidAbstract(
                PVarHandle.forField(MethodHandles.lookup(), PVarHandleTest.class, "shortPlain", short.class),
                new Object[]{ this }, ExpectedState.SHORT,
                () -> this.shortPlain, value -> this.shortPlain = value,
                true, true);
        
        testValidAbstract(
                PVarHandle.forField(MethodHandles.lookup(), PVarHandleTest.class, "charPlain", char.class),
                new Object[]{ this }, ExpectedState.CHAR,
                () -> this.charPlain, value -> this.charPlain = value,
                true, true);
        
        testValidAbstract(
                PVarHandle.forField(MethodHandles.lookup(), PVarHandleTest.class, "intPlain", int.class),
                new Object[]{ this }, ExpectedState.INT,
                () -> this.intPlain, value -> this.intPlain = value,
                true, true);
        
        testValidAbstract(
                PVarHandle.forField(MethodHandles.lookup(), PVarHandleTest.class, "longPlain", long.class),
                new Object[]{ this }, ExpectedState.LONG,
                () -> this.longPlain, value -> this.longPlain = value,
                true, true);
        
        testValidAbstract(
                PVarHandle.forField(MethodHandles.lookup(), PVarHandleTest.class, "floatPlain", float.class),
                new Object[]{ this }, ExpectedState.FLOAT,
                () -> this.floatPlain, value -> this.floatPlain = value,
                true, false);
        
        testValidAbstract(
                PVarHandle.forField(MethodHandles.lookup(), PVarHandleTest.class, "doublePlain", double.class),
                new Object[]{ this }, ExpectedState.DOUBLE,
                () -> this.doublePlain, value -> this.doublePlain = value,
                true, false);
        
        testValidAbstract(
                PVarHandle.forField(MethodHandles.lookup(), PVarHandleTest.class, "stringPlain", String.class),
                new Object[]{ this }, ExpectedState.STRING,
                () -> this.stringPlain, value -> this.stringPlain = value,
                false, false);
    }

    @Test
    public void testStaticValidAbstract() throws Throwable {
        testValidAbstract(
                PVarHandle.forStaticField(MethodHandles.lookup(), PVarHandleTest.class, "bytePlainStatic", byte.class),
                new Object[]{}, ExpectedState.BYTE,
                () -> bytePlainStatic, value -> bytePlainStatic = value,
                true, true);

        testValidAbstract(
                PVarHandle.forStaticField(MethodHandles.lookup(), PVarHandleTest.class, "shortPlainStatic", short.class),
                new Object[]{}, ExpectedState.SHORT,
                () -> shortPlainStatic, value -> shortPlainStatic = value,
                true, true);

        testValidAbstract(
                PVarHandle.forStaticField(MethodHandles.lookup(), PVarHandleTest.class, "charPlainStatic", char.class),
                new Object[]{}, ExpectedState.CHAR,
                () -> charPlainStatic, value -> charPlainStatic = value,
                true, true);

        testValidAbstract(
                PVarHandle.forStaticField(MethodHandles.lookup(), PVarHandleTest.class, "intPlainStatic", int.class),
                new Object[]{}, ExpectedState.INT,
                () -> intPlainStatic, value -> intPlainStatic = value,
                true, true);

        testValidAbstract(
                PVarHandle.forStaticField(MethodHandles.lookup(), PVarHandleTest.class, "longPlainStatic", long.class),
                new Object[]{}, ExpectedState.LONG,
                () -> longPlainStatic, value -> longPlainStatic = value,
                true, true);

        testValidAbstract(
                PVarHandle.forStaticField(MethodHandles.lookup(), PVarHandleTest.class, "floatPlainStatic", float.class),
                new Object[]{}, ExpectedState.FLOAT,
                () -> floatPlainStatic, value -> floatPlainStatic = value,
                true, false);

        testValidAbstract(
                PVarHandle.forStaticField(MethodHandles.lookup(), PVarHandleTest.class, "doublePlainStatic", double.class),
                new Object[]{}, ExpectedState.DOUBLE,
                () -> doublePlainStatic, value -> doublePlainStatic = value,
                true, false);

        testValidAbstract(
                PVarHandle.forStaticField(MethodHandles.lookup(), PVarHandleTest.class, "stringPlainStatic", String.class),
                new Object[]{}, ExpectedState.STRING,
                () -> stringPlainStatic, value -> stringPlainStatic = value,
                false, false);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testArrayIndexTooHigh() throws Throwable {
        PVarHandle.forArrayElement(MethodHandles.lookup(), int[].class).setPlainInvoker().invokeExact(new int[2], 3, 0);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testArrayIndexNegative() throws Throwable {
        PVarHandle.forArrayElement(MethodHandles.lookup(), int[].class).setPlainInvoker().invokeExact(new int[2], -1, 0);
    }

    @Test(expected = NullPointerException.class)
    public void testArrayNull() throws Throwable {
        PVarHandle.forArrayElement(MethodHandles.lookup(), int[].class).setPlainInvoker().invokeExact((int[]) null, 0, 0);
    }

    @Test(expected = IllegalAccessException.class)
    public void testNotActuallyInstance() throws Throwable {
        PVarHandle.forField(MethodHandles.lookup(), PVarHandleTest.class, "intPlainStatic", int.class);
    }

    @Test(expected = IllegalAccessException.class)
    public void testNotActuallyStatic() throws Throwable {
        PVarHandle.forStaticField(MethodHandles.lookup(), PVarHandleTest.class, "intPlain", int.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotActuallyArray() {
        PVarHandle.forArrayElement(MethodHandles.lookup(), int.class);
    }
}
