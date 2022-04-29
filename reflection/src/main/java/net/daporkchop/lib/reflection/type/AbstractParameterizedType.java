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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

/**
 * Base implementation of {@link ParameterizedType} which provides default implementations for the following methods:
 * <br>
 * <ul>
 *     <li>{@link #equals(Object)}</li>
 *     <li>{@link #hashCode()}</li>
 *     <li>{@link #toString()}</li>
 * </ul>
 *
 * @author DaPorkchop_
 */
public abstract class AbstractParameterizedType extends AbstractType implements ParameterizedType {
    @Override
    public abstract @NonNull Type @NonNull [] getActualTypeArguments();

    @Override
    public abstract @NonNull Class<?> getRawType();

    @Override
    public abstract Type getOwnerType();

    //equals() and hashCode() implementations should be functionally identical to sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof ParameterizedType) {
            ParameterizedType other = (ParameterizedType) obj;

            return Objects.equals(this.getOwnerType(), other.getOwnerType())
                   && this.getRawType() == other.getRawType()
                   && Arrays.equals(this.getActualTypeArguments(), other.getActualTypeArguments());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.getActualTypeArguments())
               ^ this.getRawType().hashCode()
               ^ Objects.hashCode(this.getOwnerType());
    }

    @Override
    public String toString() {
        Type ownerType = this.getOwnerType();
        Class<?> rawType = this.getRawType();
        Type[] actualTypeArguments = this.getActualTypeArguments();

        //magic number 30 is copied from com.google.gson.internal.\$Gson\$Types$ParameterizedTypeImpl#toString()
        StringBuilder builder = new StringBuilder((actualTypeArguments.length + 1) * 30);

        if (ownerType == null) { //type has no owner, simply append the raw type's name
            builder.append(rawType.getName());
        } else { //type has an owner
            builder.append(ownerType.getTypeName()).append('$').append(rawType.getSimpleName());
        }

        if (actualTypeArguments.length > 0) { //type arguments
            builder.append('<').append(actualTypeArguments[0].getTypeName());
            for (int i = 1; i < actualTypeArguments.length; i++) {
                builder.append(", ").append(actualTypeArguments[i].getTypeName());
            }
            builder.append('>');
        }

        return builder.toString();
    }
}
