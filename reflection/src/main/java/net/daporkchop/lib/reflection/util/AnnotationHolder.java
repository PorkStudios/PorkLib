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
}
