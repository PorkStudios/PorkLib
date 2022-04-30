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

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

/**
 * Base implementation of {@link GenericArrayType} which provides default implementations for the following methods:
 * <br>
 * <ul>
 *     <li>{@link #equals(Object)}</li>
 *     <li>{@link #hashCode()}</li>
 *     <li>{@link #toString()}</li>
 * </ul>
 *
 * @author DaPorkchop_
 */
public abstract class AbstractGenericArrayType implements GenericArrayType {
    @Override
    public abstract @NonNull Type getGenericComponentType();

    //equals() and hashCode() implementations should be functionally identical to sun.reflect.generics.reflectiveObjects.GenericArrayTypeImpl

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof GenericArrayType) {
            GenericArrayType other = (GenericArrayType) obj;

            return this.getGenericComponentType().equals(other.getGenericComponentType());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.getGenericComponentType().hashCode();
    }

    @Override
    public String toString() {
        Type componentType = this.getGenericComponentType();
        return (componentType instanceof Class ? ((Class<?>) componentType).getName() : componentType.toString()) + "[]";
    }

    /**
     * Default implementation of {@link AbstractGenericArrayType}.
     *
     * @author DaPorkchop_
     */
    @RequiredArgsConstructor
    @Getter
    public static final class DefaultGenericArrayType extends AbstractGenericArrayType {
        private final Type genericComponentType;
    }
}
