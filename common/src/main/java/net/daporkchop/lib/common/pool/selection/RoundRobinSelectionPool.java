/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.common.pool.selection;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * An implementation of {@link SelectionPool} which selects from a fixed number of values given at construction time using a
 * round-robin method based on an incrementing counter.
 * <p>
 * Should not be created directly, instead use the helper methods in {@link SelectionPool}.
 *
 * @author DaPorkchop_
 */
public final class RoundRobinSelectionPool<V> implements SelectionPool<V> {
    protected static final long STEP_OFFSET = PUnsafe.pork_getOffset(RoundRobinSelectionPool.class, "step");

    protected final Object[] values;
    protected final int valueCount;
    protected volatile int step;

    protected RoundRobinSelectionPool(@NonNull Object[] values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("Must have at least 1 value!");
        }
        this.values = values;
        this.valueCount = values.length;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V any() {
        int index;
        int nextIndex;
        do {
            index = PUnsafe.getIntVolatile(this, STEP_OFFSET);
            if ((nextIndex = index + 1) >= this.valueCount) {
                nextIndex = 0;
            }
        } while (!PUnsafe.compareAndSwapInt(this, STEP_OFFSET, index, nextIndex));

        return (V) this.values[nextIndex];
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<V> matching(@NonNull Predicate<V> condition) {
        return Arrays.stream((V[]) this.values).filter(condition).collect(Collectors.toList());
    }
}
