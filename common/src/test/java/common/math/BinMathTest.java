/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2024 DaPorkchop_
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

package common.math;

import net.daporkchop.lib.common.math.BinMath;
import org.junit.Test;

import java.util.function.Supplier;

import static org.junit.Assert.*;

/**
 * @author DaPorkchop_
 */
public class BinMathTest {
    private static void assertFails(Class<? extends Throwable> expectedThrowable, Runnable action) {
        Throwable cause = null;
        try {
            action.run();
        } catch (Throwable e) {
            cause = e;
        }

        if (!expectedThrowable.isInstance(cause)) {
            throw new AssertionError("expected an " + expectedThrowable.getName() + " to be thrown", cause);
        }
    }

    private static void assertFails(Class<? extends Throwable> expectedThrowable, Supplier<?> action) {
        Throwable cause = null;
        try {
            action.get();
        } catch (Throwable e) {
            cause = e;
        }

        if (!expectedThrowable.isInstance(cause)) {
            throw new AssertionError("expected an " + expectedThrowable.getName() + " to be thrown", cause);
        }
    }

    @Test
    public void testUnsignedBitCeil_Int() {
        assertEquals(1, BinMath.unsignedBitCeil(0));
        assertEquals(1, BinMath.unsignedBitCeil(1));
        assertEquals(2, BinMath.unsignedBitCeil(2));
        assertEquals(4, BinMath.unsignedBitCeil(3));
        assertEquals(4, BinMath.unsignedBitCeil(4));
        assertEquals(8, BinMath.unsignedBitCeil(5));
        assertEquals(8, BinMath.unsignedBitCeil(6));
        assertEquals(8, BinMath.unsignedBitCeil(7));
        assertEquals(8, BinMath.unsignedBitCeil(8));
        assertEquals(16, BinMath.unsignedBitCeil(9));
        assertEquals(Integer.MIN_VALUE, BinMath.unsignedBitCeil(Integer.MAX_VALUE));
        assertEquals(Integer.MIN_VALUE, BinMath.unsignedBitCeil(Integer.MIN_VALUE));

        //these should all fail because assertions are enabled
        assertFails(AssertionError.class, () -> BinMath.unsignedBitCeil(Integer.MIN_VALUE + 1));
        assertFails(AssertionError.class, () -> BinMath.unsignedBitCeil(Integer.MIN_VALUE + 2));
        assertFails(AssertionError.class, () -> BinMath.unsignedBitCeil(-1));
        assertFails(AssertionError.class, () -> BinMath.unsignedBitCeil(-2));
    }

    @Test
    public void testUnsignedBitCeil_Long() {
        assertEquals(1L, BinMath.unsignedBitCeil(0L));
        assertEquals(1L, BinMath.unsignedBitCeil(1L));
        assertEquals(2L, BinMath.unsignedBitCeil(2L));
        assertEquals(4L, BinMath.unsignedBitCeil(3L));
        assertEquals(4L, BinMath.unsignedBitCeil(4L));
        assertEquals(8L, BinMath.unsignedBitCeil(5L));
        assertEquals(8L, BinMath.unsignedBitCeil(6L));
        assertEquals(8L, BinMath.unsignedBitCeil(7L));
        assertEquals(8L, BinMath.unsignedBitCeil(8L));
        assertEquals(16L, BinMath.unsignedBitCeil(9L));
        assertEquals(Long.MIN_VALUE, BinMath.unsignedBitCeil(Long.MAX_VALUE));
        assertEquals(Long.MIN_VALUE, BinMath.unsignedBitCeil(Long.MIN_VALUE));

        //these should all fail because assertions are enabled
        assertFails(AssertionError.class, () -> BinMath.unsignedBitCeil(Long.MIN_VALUE + 1L));
        assertFails(AssertionError.class, () -> BinMath.unsignedBitCeil(Long.MIN_VALUE + 2L));
        assertFails(AssertionError.class, () -> BinMath.unsignedBitCeil(-1L));
        assertFails(AssertionError.class, () -> BinMath.unsignedBitCeil(-2L));
    }
}
