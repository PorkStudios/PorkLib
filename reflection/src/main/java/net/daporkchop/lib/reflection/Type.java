/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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

package net.daporkchop.lib.reflection;

import lombok.NonNull;

import java.lang.reflect.Field;

/**
 * Types that can be held by a field
 *
 * @author DaPorkchop_
 */
public enum Type {
    OBJECT,
    ARRAY,
    BOOLEAN,
    BYTE,
    SHORT,
    INT,
    LONG,
    FLOAT,
    DOUBLE,
    CHAR;

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
            return ARRAY;
        } else {
            return OBJECT;
        }
    }
}
