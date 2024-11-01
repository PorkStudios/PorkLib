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

import net.daporkchop.lib.common.math.PMath;
import org.junit.Test;

import java.util.function.Supplier;

import static org.junit.Assert.*;

/**
 * @author DaPorkchop_
 */
public class PMathTest {
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
    public void testGrowCapacity1_Int() {
        assertEquals(1, PMath.growCapacity1(0));
        assertEquals(2, PMath.growCapacity1(1));
        assertEquals(4, PMath.growCapacity1(2));
        assertEquals(4, PMath.growCapacity1(3));
        assertEquals(8, PMath.growCapacity1(4));
        assertEquals(8, PMath.growCapacity1(5));
        assertEquals(Integer.MAX_VALUE, PMath.growCapacity1(Integer.MAX_VALUE - 1));

        assertFails(IllegalArgumentException.class, () -> PMath.growCapacity1(-1));
        assertFails(IllegalArgumentException.class, () -> PMath.growCapacity1(Integer.MIN_VALUE));
        assertFails(IllegalArgumentException.class, () -> PMath.growCapacity1(Integer.MAX_VALUE));
    }

    @Test
    public void testGrowCapacity1_Long() {
        assertEquals(1L, PMath.growCapacity1(0L));
        assertEquals(2L, PMath.growCapacity1(1L));
        assertEquals(4L, PMath.growCapacity1(2L));
        assertEquals(4L, PMath.growCapacity1(3L));
        assertEquals(8L, PMath.growCapacity1(4L));
        assertEquals(8L, PMath.growCapacity1(5L));
        assertEquals(Long.MAX_VALUE, PMath.growCapacity1(Long.MAX_VALUE - 1L));

        assertFails(IllegalArgumentException.class, () -> PMath.growCapacity1(-1L));
        assertFails(IllegalArgumentException.class, () -> PMath.growCapacity1(Long.MIN_VALUE));
        assertFails(IllegalArgumentException.class, () -> PMath.growCapacity1(Long.MAX_VALUE));
    }

    @Test
    public void testGrowCapacityBy_Int() {
        assertEquals(1, PMath.growCapacityBy(0, 0));
        
        assertEquals(1, PMath.growCapacityBy(0, 1));
        assertEquals(2, PMath.growCapacityBy(1, 1));
        assertEquals(4, PMath.growCapacityBy(2, 1));
        assertEquals(4, PMath.growCapacityBy(3, 1));
        assertEquals(8, PMath.growCapacityBy(4, 1));
        assertEquals(8, PMath.growCapacityBy(5, 1));

        assertEquals(16, PMath.growCapacityBy(0, 10));
        assertEquals(16, PMath.growCapacityBy(1, 10));
        assertEquals(16, PMath.growCapacityBy(5, 10));
        assertEquals(16, PMath.growCapacityBy(6, 10));
        assertEquals(32, PMath.growCapacityBy(7, 10));

        assertEquals(Integer.MAX_VALUE, PMath.growCapacityBy(Integer.MAX_VALUE, 0));
        assertEquals(Integer.MAX_VALUE, PMath.growCapacityBy(Integer.MAX_VALUE - 1, 0));
        assertEquals(Integer.MAX_VALUE, PMath.growCapacityBy(Integer.MAX_VALUE - 1, 1));
        assertEquals(Integer.MAX_VALUE, PMath.growCapacityBy(Integer.MAX_VALUE - 1000, 1));
        assertEquals(Integer.MAX_VALUE, PMath.growCapacityBy(0, Integer.MAX_VALUE));
        assertEquals(Integer.MAX_VALUE, PMath.growCapacityBy(1, Integer.MAX_VALUE - 1));
        assertEquals(Integer.MAX_VALUE, PMath.growCapacityBy(1, Integer.MAX_VALUE - 1000));

        assertFails(IllegalArgumentException.class, () -> PMath.growCapacityBy(-1, 0));
        assertFails(IllegalArgumentException.class, () -> PMath.growCapacityBy(0, -1));
        assertFails(IllegalArgumentException.class, () -> PMath.growCapacityBy(Integer.MAX_VALUE, 1));
        assertFails(IllegalArgumentException.class, () -> PMath.growCapacityBy(1, Integer.MAX_VALUE));
        assertFails(IllegalArgumentException.class, () -> PMath.growCapacityBy(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

    @Test
    public void testGrowCapacityBy_Long() {
        assertEquals(1L, PMath.growCapacityBy(0L, 0L));
        
        assertEquals(1L, PMath.growCapacityBy(0L, 1L));
        assertEquals(2L, PMath.growCapacityBy(1L, 1L));
        assertEquals(4L, PMath.growCapacityBy(2L, 1L));
        assertEquals(4L, PMath.growCapacityBy(3L, 1L));
        assertEquals(8L, PMath.growCapacityBy(4L, 1L));
        assertEquals(8L, PMath.growCapacityBy(5L, 1L));

        assertEquals(16L, PMath.growCapacityBy(0L, 10L));
        assertEquals(16L, PMath.growCapacityBy(1L, 10L));
        assertEquals(16L, PMath.growCapacityBy(5L, 10L));
        assertEquals(16L, PMath.growCapacityBy(6L, 10L));
        assertEquals(32L, PMath.growCapacityBy(7L, 10L));

        assertEquals(Long.MAX_VALUE, PMath.growCapacityBy(Long.MAX_VALUE, 0L));
        assertEquals(Long.MAX_VALUE, PMath.growCapacityBy(Long.MAX_VALUE - 1L, 0L));
        assertEquals(Long.MAX_VALUE, PMath.growCapacityBy(Long.MAX_VALUE - 1L, 1L));
        assertEquals(Long.MAX_VALUE, PMath.growCapacityBy(Long.MAX_VALUE - 1000L, 1L));
        assertEquals(Long.MAX_VALUE, PMath.growCapacityBy(0L, Long.MAX_VALUE));
        assertEquals(Long.MAX_VALUE, PMath.growCapacityBy(1L, Long.MAX_VALUE - 1L));
        assertEquals(Long.MAX_VALUE, PMath.growCapacityBy(1L, Long.MAX_VALUE - 1000L));

        assertFails(IllegalArgumentException.class, () -> PMath.growCapacityBy(-1L, 0L));
        assertFails(IllegalArgumentException.class, () -> PMath.growCapacityBy(0L, -1L));
        assertFails(IllegalArgumentException.class, () -> PMath.growCapacityBy(Long.MAX_VALUE, 1L));
        assertFails(IllegalArgumentException.class, () -> PMath.growCapacityBy(1L, Long.MAX_VALUE));
        assertFails(IllegalArgumentException.class, () -> PMath.growCapacityBy(Long.MAX_VALUE, Long.MAX_VALUE));
    }
}
