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

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

/**
 * Base implementation of {@link WildcardType} which provides default implementations for the following methods:
 * <br>
 * <ul>
 *     <li>{@link #equals(Object)}</li>
 *     <li>{@link #hashCode()}</li>
 *     <li>{@link #toString()}</li>
 * </ul>
 *
 * @author DaPorkchop_
 */
public abstract class AbstractWildcardType extends AbstractType implements WildcardType {

    @Override
    public abstract @NonNull Type @NonNull [] getUpperBounds();

    @Override
    public abstract @NonNull Type @NonNull [] getLowerBounds();

    //equals() and hashCode() implementations should be functionally identical to sun.reflect.generics.reflectiveObjects.WildcardTypeImpl

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof WildcardType) {
            WildcardType other = (WildcardType) obj;

            return Arrays.equals(this.getUpperBounds(), other.getUpperBounds())
                   && Arrays.equals(this.getLowerBounds(), other.getLowerBounds());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.getUpperBounds()) ^ Arrays.hashCode(this.getLowerBounds());
    }

    @Override
    public String toString() {
        Type[] upperBounds = this.getUpperBounds();
        Type[] lowerBounds = this.getLowerBounds();

        //determine whether to print lower or upper bounds

        String prefix;
        Type[] bounds;
        if (PTypes.isWildcardSuper(upperBounds, lowerBounds)) { //use lower bounds
            prefix = "? super ";
            bounds = lowerBounds;
        } else if (PTypes.isWildcardUnbounded(upperBounds, lowerBounds)) { //there are effectively no bounds
            return "?";
        } else { //use upper bounds
            prefix = "? extends ";
            bounds = upperBounds;
        }

        //magic number 30 is copied from com.google.gson.internal.\$Gson\$Types$ParameterizedTypeImpl#toString()
        StringBuilder builder = new StringBuilder((upperBounds.length + lowerBounds.length) * 30);

        //append bound types
        builder.append(prefix).append(bounds[0].getTypeName());
        for (int i = 1; i < bounds.length; i++) {
            builder.append(" & ").append(bounds[i].getTypeName());
        }

        return builder.toString();
    }
}
