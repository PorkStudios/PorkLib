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

package net.daporkchop.lib.reflection.util;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Access levels for class (members)
 * <p>
 * Sorted by order of how good they are
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public enum Access {
    PUBLIC(Modifier.PUBLIC),
    PROTECTED(Modifier.PROTECTED),
    PRIVATE(Modifier.PRIVATE),
    PACKAGE_PRIVATE(-1);

    private final int mod;

    public boolean matches(int modifier)    {
        if (this.mod == -1) {
            return (modifier & (Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED)) != 0;
        } else {
            return (modifier & this.mod) != 0;
        }
    }

    public static Access getAccess(@NonNull Field field)   {
        return getAccess(field.getModifiers());
    }

    public static Access getAccess(int modifiers)   {
        if ((modifiers & Modifier.PUBLIC) != 0) {
            return PUBLIC;
        } else if ((modifiers & Modifier.PRIVATE) != 0) {
            return PRIVATE;
        } else if ((modifiers & Modifier.PROTECTED) != 0)   {
            return PROTECTED;
        } else {
            return PACKAGE_PRIVATE;
        }
    }
}
