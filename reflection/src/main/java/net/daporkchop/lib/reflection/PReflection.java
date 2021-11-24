/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2021 DaPorkchop_
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

package net.daporkchop.lib.reflection;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author DaPorkchop_
 */
@UtilityClass
public class PReflection {
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
     * Gets an annotation on a field
     *
     * @param field           the field
     * @param annotationClass the class of the annotation
     * @param <A>             the type of the annotation
     * @return the annotation instance on the given class, or {@code null} if not found
     */
    public static <A extends Annotation> A getAnnotation(@NonNull Field field, @NonNull Class<A> annotationClass) {
        return getAnnotation(field.getAnnotations(), annotationClass);
    }

    /**
     * Maps a stream of {@link Field}s to a given annotation type, removing all fields from the stream that
     * do not an annotation with the given class.
     *
     * @param stream          the stream
     * @param annotationClass the class of the annotation
     * @param <A>             the type of the annotation
     * @return the annotation instance on the given class, or {@code null} if not found
     */
    public static <A extends Annotation> Stream<A> filterFieldToAnnotation(@NonNull Stream<Field> stream, @NonNull Class<A> annotationClass) {
        return stream.map(field -> getAnnotation(field, annotationClass)).filter(Objects::nonNull);
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

    /**
     * Returns a distinct {@link Stream} containing the given class along with all of its superclasses and superinterfaces.
     *
     * @param clazz the class
     * @return the superclasses and superinterfaces
     */
    public static Stream<Class<?>> getAllSuperclasses(@NonNull Class<?> clazz) {
        return Stream.concat(
                Stream.of(clazz),
                Stream.concat(Stream.of(clazz.getSuperclass()), Stream.of(clazz.getInterfaces()))
                        .filter(Objects::nonNull)
                        .flatMap(PReflection::getAllSuperclasses))
                .distinct();
    }

    public static Method getMethod(@NonNull Class<?> clazz, @NonNull String name, @NonNull Class<?>... params) {
        try {
            Method method = clazz.getDeclaredMethod(name, params);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setStatic(@NonNull Class<?> clazz, @NonNull String fieldName, Object value) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if ((field.getModifiers() & Modifier.STATIC) == 0) {
                throw new IllegalArgumentException(String.format("Not static: %s.%s", clazz.getCanonicalName(), fieldName));
            }
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(null, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
