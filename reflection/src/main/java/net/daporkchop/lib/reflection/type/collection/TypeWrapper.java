/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
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

package net.daporkchop.lib.reflection.type.collection;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.reflection.type.PTypes;

import java.lang.reflect.Type;

/**
 * Wrapper object for a {@link Type} which uses {@link PTypes#equals(Type, Type)}, {@link PTypes#hashCode(Type)} and {@link PTypes#toString(Type)} for {@link #equals(Object)},
 * {@link #hashCode()} and {@link #toString()}, respectively.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public final class TypeWrapper {
    @NonNull
    private final Type delegate;

    /**
     * @return the original, unwrapped {@link Type} instance
     */
    public Type unwrap() {
        return this.delegate;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj
               || (obj instanceof TypeWrapper && PTypes.equals(this.delegate, ((TypeWrapper) obj).unwrap()))
               || (obj instanceof Type && PTypes.equals(this.delegate, (Type) obj));
    }

    @Override
    public int hashCode() {
        return PTypes.hashCode(this.delegate);
    }

    @Override
    public String toString() {
        return PTypes.toString(this.delegate);
    }
}
