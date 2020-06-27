/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
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

package net.daporkchop.lib.collections.spliterator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.unsafe.util.AbstractReleasable;

import java.util.Spliterator;
import java.util.function.Consumer;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Base class for implementations of a {@link Spliterator} which apply a flat-mapping operation to values returned by the parent {@link Spliterator}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public abstract class FlatMappingSpliterator<I, O> extends AbstractReleasable implements Spliterator<O>, Consumer<I> {
    @NonNull
    protected final Spliterator<I> parent;
    protected Spliterator<O> current;

    @Override
    public void accept(I val) {
        checkState(this.current == null, "current spliterator wasn't finished!");
        checkState((this.current = this.flatMap(val)) != null, "flatMap returned null!");
    }

    @Override
    public boolean tryAdvance(@NonNull Consumer<? super O> action) {
        while (true) {
            //if flat mapped spliterator is unset, attempt to move forward one input and create it
            if (this.current == null && !this.parent.tryAdvance(this)) {
                this.release();
                return false;
            }

            if (this.current.tryAdvance(action)) {
                return true; //successful, nothing needs to be done
            } else {
                this.current = null; //current spliterator is empty
            }
        }
    }

    @Override
    public void forEachRemaining(@NonNull Consumer<? super O> action) {
        if (this.current != null) { //current spliterator is non-nul, drain it
            this.current.forEachRemaining(action);
            this.current = null;
        }

        //iterate over EVERYTHING
        this.parent.forEachRemaining(val -> this.flatMap(val).forEachRemaining(action));
        this.release();
    }

    @Override
    public Spliterator<O> trySplit() {
        Spliterator<I> other = this.parent.trySplit();
        return other != null ? this.doSplit(other) : null;
    }

    @Override
    public long estimateSize() {
        long parentSize = this.parent.estimateSize();
        long currentSize = this.current.estimateSize();
        return parentSize == Long.MAX_VALUE || currentSize == Long.MAX_VALUE ? Long.MAX_VALUE : this.estimateSizeFromIs(parentSize) + currentSize;
    }

    @Override
    public int characteristics() {
        return this.parent.characteristics() & ~(SIZED | SUBSIZED);
    }

    protected abstract Spliterator<O> flatMap(I value);

    protected abstract long estimateSizeFromIs(long iCount);

    protected abstract Spliterator<O> doSplit(@NonNull Spliterator<I> parent);
}
