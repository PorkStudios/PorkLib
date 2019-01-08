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
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Float.floatToIntBits;
import static java.lang.Float.intBitsToFloat;
import static net.daporkchop.lib.common.util.PUnsafe.*;

/**
 * @author DaPorkchop_
 * @see AtomicInteger
 */
public class AtomicFloat extends Number implements Serializable {
    private static final long valueOffset = pork_getOffset(AtomicFloat.class, "value");

    private volatile float value;

    public AtomicFloat(float initialValue) {
        this.value = initialValue;
    }

    public final float get() {
        return this.value;
    }

    public final void set(float newValue) {
        this.value = newValue;
    }

    public final void lazySet(float newValue) {
        putOrderedInt(this, valueOffset, floatToIntBits(newValue));
    }

    public final float getAndSet(float newValue) {
        return intBitsToFloat(getAndSetInt(this, valueOffset, floatToIntBits(newValue)));
    }

    public final boolean compareAndSet(float expect, float update) {
        return compareAndSwapInt(this, valueOffset, floatToIntBits(expect), floatToIntBits(update));
    }

    public final boolean weakCompareAndSet(float expect, float update) {
        return compareAndSwapInt(this, valueOffset, floatToIntBits(expect), floatToIntBits(update));
    }

    public final float getAndIncrement() {
        return getAndAddFloat(this, valueOffset, 1.0f);
    }

    public final float getAndDecrement() {
        return getAndAddFloat(this, valueOffset, -1.0f);
    }

    public final float getAndAdd(float delta) {
        return getAndAddFloat(this, valueOffset, delta);
    }

    public final float incrementAndGet() {
        return getAndAddFloat(this, valueOffset, 1.0f) + 1.0f;
    }

    public final float decrementAndGet() {
        return getAndAddFloat(this, valueOffset, -1.0f) - 1.0f;
    }

    public final float addAndGet(float delta) {
        return getAndAddFloat(this, valueOffset, delta) + delta;
    }

    public final float getAndUpdate(@NonNull FloatUnaryOperator updateFunction) {
        float prev, next;
        do {
            prev = this.get();
            next = updateFunction.applyAsFloat(prev);
        } while (!compareAndSet(prev, next));
        return prev;
    }

    public final float updateAndGet(@NonNull FloatUnaryOperator updateFunction) {
        float prev, next;
        do {
            prev = get();
            next = updateFunction.applyAsFloat(prev);
        } while (!compareAndSet(prev, next));
        return next;
    }

    public final float getAndAccumulate(float x, @NonNull FloatBinaryOperator accumulatorFunction) {
        float prev, next;
        do {
            prev = get();
            next = accumulatorFunction.applyAsFloat(prev, x);
        } while (!compareAndSet(prev, next));
        return prev;
    }

    public final float accumulateAndGet(float x, @NonNull FloatBinaryOperator accumulatorFunction) {
        float prev, next;
        do {
            prev = get();
            next = accumulatorFunction.applyAsFloat(prev, x);
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

    public float floatValue() {
        return this.get();
    }

    public double doubleValue() {
        return this.get();
    }

    @FunctionalInterface
    interface FloatUnaryOperator {
        float applyAsFloat(float a);
    }

    @FunctionalInterface
    interface FloatBinaryOperator {
        float applyAsFloat(float a, float b);
    }
}
