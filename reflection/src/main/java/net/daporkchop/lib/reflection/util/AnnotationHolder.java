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

import lombok.NonNull;
import net.daporkchop.lib.reflection.PReflection;

import java.lang.annotation.Annotation;

/**
 * @author DaPorkchop_
 */
public interface AnnotationHolder {

    /**
     * Gets all the annotations on this member
     *
     * @return all the annotations on this member
     */
    Annotation[] getAnnotations();

    /**
     * Gets an annotation on this member
     *
     * @param clazz the class of the annotation
     * @param <A>   the type of the annotation
     * @return the annotation instance on this member, or {@code null} if not found
     */
    default <A extends Annotation> A getAnnotation(@NonNull Class<A> clazz) {
        return PReflection.getAnnotation(this.getAnnotations(), clazz);
    }

    /**
     * Checks if a given annotation class is present on this member
     *
     * @param clazz the annotation class to check for
     * @return whether or not an annotation of the given annotation class is present on this member
     */
    default boolean hasAnnotation(@NonNull Class<? extends Annotation> clazz) {
        return PReflection.getAnnotation(this.getAnnotations(), clazz) != null;
    }
}
