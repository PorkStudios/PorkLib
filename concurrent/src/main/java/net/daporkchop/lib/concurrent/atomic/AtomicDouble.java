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

package net.daporkchop.lib.concurrent.atomic;

import lombok.NonNull;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Double.doubleToLongBits;
import static java.lang.Double.longBitsToDouble;
import static net.daporkchop.lib.common.util.PUnsafe.compareAndSwapLong;
import static net.daporkchop.lib.common.util.PUnsafe.getAndAddDouble;
import static net.daporkchop.lib.common.util.PUnsafe.getAndSetLong;
import static net.daporkchop.lib.common.util.PUnsafe.pork_getOffset;
import static net.daporkchop.lib.common.util.PUnsafe.putOrderedLong;

/**
 * @author DaPorkchop_
 * @see AtomicLong
 */
public class AtomicDouble extends Number implements Serializable {
    private static final long valueOffset = pork_getOffset(AtomicDouble.class, "value");

    private volatile double value;

    public AtomicDouble(double initialValue) {
        this.value = initialValue;
    }

    public final double get() {
        return this.value;
    }

    public final void set(double newValue) {
        this.value = newValue;
    }

    public final void lazySet(double newValue) {
        putOrderedLong(this, valueOffset, doubleToLongBits(newValue));
    }

    public final double getAndSet(double newValue) {
        return longBitsToDouble(getAndSetLong(this, valueOffset, doubleToLongBits(newValue)));
    }

    public final boolean compareAndSet(double expect, double update) {
        return compareAndSwapLong(this, valueOffset, doubleToLongBits(expect), doubleToLongBits(update));
    }

    public final boolean weakCompareAndSet(double expect, double update) {
        return compareAndSwapLong(this, valueOffset, doubleToLongBits(expect), doubleToLongBits(update));
    }

    public final double getAndIncrement() {
        return getAndAddDouble(this, valueOffset, 1.0f);
    }

    public final double getAndDecrement() {
        return getAndAddDouble(this, valueOffset, -1.0f);
    }

    public final double getAndAdd(double delta) {
        return getAndAddDouble(this, valueOffset, delta);
    }

    public final double incrementAndGet() {
        return getAndAddDouble(this, valueOffset, 1.0f) + 1.0f;
    }

    public final double decrementAndGet() {
        return getAndAddDouble(this, valueOffset, -1.0f) - 1.0f;
    }

    public final double addAndGet(double delta) {
        return getAndAddDouble(this, valueOffset, delta) + delta;
    }

    public final double getAndUpdate(@NonNull DoubleUnaryOperator updateFunction) {
        double prev, next;
        do {
            prev = this.get();
            next = updateFunction.applyAsDouble(prev);
        } while (!compareAndSet(prev, next));
        return prev;
    }

    public final double updateAndGet(@NonNull DoubleUnaryOperator updateFunction) {
        double prev, next;
        do {
            prev = get();
            next = updateFunction.applyAsDouble(prev);
        } while (!compareAndSet(prev, next));
        return next;
    }

    public final double getAndAccumulate(double x, @NonNull DoubleBinaryOperator accumulatorFunction) {
        double prev, next;
        do {
            prev = get();
            next = accumulatorFunction.applyAsDouble(prev, x);
        } while (!compareAndSet(prev, next));
        return prev;
    }

    public final double accumulateAndGet(double x, @NonNull DoubleBinaryOperator accumulatorFunction) {
        double prev, next;
        do {
            prev = get();
            next = accumulatorFunction.applyAsDouble(prev, x);
        } while (!compareAndSet(prev, next));
        return next;
    }

    public String toString() {
        return String.valueOf(this.get());
    }

    public int intValue() {
        return (int) this.get();
    }

    public long longValue() {
        return (long) this.get();
    }

    @Override
    public float floatValue() {
        return (float) this.get();
    }

    @Override
    public double doubleValue() {
        return this.get();
    }

    @FunctionalInterface
    interface DoubleUnaryOperator {
        double applyAsDouble(double a);
    }

    @FunctionalInterface
    interface DoubleBinaryOperator {
        double applyAsDouble(double a, double b);
    }
}
