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
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Types that can be held by a field
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public enum Type {
    OBJECT(true),
    ARRAY_OBJECT(true),
    ARRAY_BOOLEAN(true),
    ARRAY_BYTE(true),
    ARRAY_SHORT(true),
    ARRAY_INT(true),
    ARRAY_LONG(true),
    ARRAY_FLOAT(true),
    ARRAY_DOUBLE(true),
    ARRAY_CHAR(true),
    BOOLEAN(false),
    BYTE(false),
    SHORT(false),
    INT(false),
    LONG(false),
    FLOAT(false),
    DOUBLE(false),
    CHAR(false),
    STRING(true), //not used internally
    BIGINTEGER(true), //not used internally
    BIGDECIMAL(true), //not used internally
    ;

    /**
     * Gets the type of a field
     *
     * @param field the field
     * @return the type of value stored in the field
     */
    public static Type getType(@NonNull Field field) {
        Class<?> clazz = field.getType();
        if (clazz == boolean.class) {
            return BOOLEAN;
        } else if (clazz == byte.class) {
            return BYTE;
        } else if (clazz == short.class) {
            return SHORT;
        } else if (clazz == int.class) {
            return INT;
        } else if (clazz == long.class) {
            return LONG;
        } else if (clazz == float.class) {
            return FLOAT;
        } else if (clazz == double.class) {
            return DOUBLE;
        } else if (clazz == char.class) {
            return CHAR;
        } else if (clazz.isArray()) {
            if (clazz == boolean[].class) {
                return ARRAY_BOOLEAN;
            } else if (clazz == byte[].class) {
                return ARRAY_BYTE;
            } else if (clazz == short[].class) {
                return ARRAY_SHORT;
            } else if (clazz == int[].class) {
                return ARRAY_INT;
            } else if (clazz == long[].class) {
                return ARRAY_LONG;
            } else if (clazz == float[].class) {
                return ARRAY_FLOAT;
            } else if (clazz == double[].class) {
                return ARRAY_DOUBLE;
            } else if (clazz == char[].class) {
                return ARRAY_CHAR;
            } else {
                return ARRAY_OBJECT;
            }
        } else {
            return OBJECT;
        }
    }

    public static Type getMoreAccurateType(@NonNull Class<?> clazz, boolean mustBeNumber, boolean allowArrays) {
        if (clazz == byte.class || clazz == Byte.class) {
            return BYTE;
        } else if (clazz == short.class || clazz == Short.class) {
            return SHORT;
        } else if (clazz == int.class || clazz == Integer.class) {
            return INT;
        } else if (clazz == long.class || clazz == Long.class) {
            return LONG;
        } else if (clazz == float.class || clazz == Float.class) {
            return FLOAT;
        } else if (clazz == double.class || clazz == Double.class) {
            return DOUBLE;
        } else if (clazz == char.class || clazz == Character.class) {
            return CHAR;
        } else if (clazz.isArray()) {
            if (allowArrays)    {
                if (clazz == boolean[].class) {
                    return ARRAY_BOOLEAN;
                } else if (clazz == byte[].class) {
                    return ARRAY_BYTE;
                } else if (clazz == short[].class) {
                    return ARRAY_SHORT;
                } else if (clazz == int[].class) {
                    return ARRAY_INT;
                } else if (clazz == long[].class) {
                    return ARRAY_LONG;
                } else if (clazz == float[].class) {
                    return ARRAY_FLOAT;
                } else if (clazz == double[].class) {
                    return ARRAY_DOUBLE;
                } else if (clazz == char[].class) {
                    return ARRAY_CHAR;
                } else {
                    return ARRAY_OBJECT;
                }
            } else {
                throw new IllegalArgumentException("arrays not allowed!");
            }
        } else if (clazz == String.class) {
            return STRING;
        } else if (clazz == BigInteger.class) {
            return BIGINTEGER;
        } else if (clazz == BigDecimal.class) {
            return BIGDECIMAL;
        } else {
            if (mustBeNumber) {
                throw new IllegalArgumentException(String.format("Not a number: %s", clazz.getCanonicalName()));
            } else if (clazz == boolean.class || clazz == Boolean.class) {
                return BOOLEAN;
            } else {
                return OBJECT;
            }
        }
    }

    private final boolean object;
}
