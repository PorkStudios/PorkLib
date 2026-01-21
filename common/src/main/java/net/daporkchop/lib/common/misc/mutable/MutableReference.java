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
 *
 */

package net.daporkchop.lib.common.misc.mutable;

import lombok.AllArgsConstructor;

import java.io.Serializable;

/**
 * A container holding a mutable object reference.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
public final class MutableReference<T> implements Cloneable, Serializable {
    private static final long serialVersionUID = 1L;

    private T value;

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    @Override
    public MutableReference<T> clone() {
        return new MutableReference<>(this.value);
    }

    /**
     * @return the current value
     */
    public T get() {
        return this.value;
    }

    /**
     * Sets the current value to the given value.
     *
     * @param value the new value
     */
    public void set(T value) {
        this.value = value;
    }

    /**
     * Sets the current value to the given value and returns the old value.
     *
     * @param value the new value
     * @return the old value
     */
    public T exchange(T value) {
        T old = this.value;
        this.value = value;
        return old;
    }
}
