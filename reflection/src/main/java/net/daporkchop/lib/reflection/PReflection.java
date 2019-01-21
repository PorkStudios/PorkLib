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
import net.daporkchop.lib.common.util.PConstants;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author DaPorkchop_
 */
public abstract class PReflection {
    /**
     * Gets an annotation on a class
     *
     * @param clazz           the class
     * @param annotationClass the class of the annotation
     * @param <A>             the type of the annotation
     * @return the annotation instance on the given class, or {@code null} if not found
     */
    public static <A extends Annotation> A getAnnotation(@NonNull Class<?> clazz, @NonNull Class<A> annotationClass) {
        return getAnnotation(clazz.getAnnotations(), annotationClass);
    }

    /**
     * Gets an annotation on a class
     *
     * @param annotations an array of annotations
     * @param clazz       the class of the annotation
     * @param <A>         the type of the annotation
     * @return the annotation instance in the given array, or {@code null} if not found
     */
    @SuppressWarnings("unchecked")
    public static <A extends Annotation> A getAnnotation(@NonNull Annotation[] annotations, @NonNull Class<A> clazz) {
        for (Annotation a : annotations) {
            if (a.annotationType() == clazz) {
                return (A) a;
            }
        }
        return null;
    }

    public static void setStatic(@NonNull Class<?> clazz, @NonNull String fieldName, Object value)  {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if ((field.getModifiers() & Modifier.STATIC) == 0)  {
                throw new IllegalArgumentException(String.format("Not static: %s.%s", clazz.getCanonicalName(), fieldName));
            }
            if (!field.isAccessible())  {
                field.setAccessible(true);
            }
            field.set(null, value);
        } catch (NoSuchFieldException
                | IllegalAccessException e) {
            throw PConstants.p_exception(e);
        }
    }
}
