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

package net.daporkchop.lib.reflection.type;

import lombok.NonNull;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * Base implementation of {@link TypeVariable} which provides default implementations for the following methods:
 * <br>
 * <ul>
 *     <li>{@link #equals(Object)}</li>
 *     <li>{@link #hashCode()}</li>
 *     <li>{@link #toString()}</li>
 * </ul>
 *
 * @author DaPorkchop_
 */
public abstract class AbstractTypeVariable<D extends GenericDeclaration> extends AbstractType implements TypeVariable<D> {
    @Override
    public abstract @NonNull Type @NonNull [] getBounds();

    @Override
    public abstract @NonNull D getGenericDeclaration();

    @Override
    public abstract @NonNull String getName();

    @Override
    public abstract @NonNull AnnotatedType @NonNull [] getAnnotatedBounds();

    //equals() and hashCode() implementations should be functionally identical to sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof TypeVariable) {
            TypeVariable<?> other = (TypeVariable<?>) obj;

            return this.getGenericDeclaration().equals(other.getGenericDeclaration())
                   && this.getName().equals(other.getName());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.getGenericDeclaration().hashCode() ^ this.getName().hashCode();
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
