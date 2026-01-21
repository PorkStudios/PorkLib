/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2025 DaPorkchop_
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
 */

package net.daporkchop.lib.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that floating-point operations in annotated code may be optimized in ways which violate Java semantics.
 * <p>
 * If applied to a method/constructor, fast-math semantics apply to all code in the method/constructor. If applied to a
 * class, fast-math semantics apply to all code in all methods/constructors in the class.
 * <p>
 * In any case, the fast-math semantics also apply recursively to all method calls in annotated code. Note that this does
 * not mean that the called methods' semantics are changed: the fast-math semantics only apply to the particular call site
 * within fast-math code, i.e. if the target method is inlined.
 *
 * @author DaPorkchop_
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface FastMath {
    /**
     * The set of enabled fast-math options.
     * <p>
     * By default, all flags are enabled.
     */
    Option[] value() default {
            Option.UNSAFE_MATH_OPTIMIZATIONS,
            Option.ASSOCIATIVE_MATH,
            Option.RECIPROCAL_MATH,
            Option.FINITE_MATH,
            Option.NO_SIGNED_ZERO,
            Option.CONTRACT,
            Option.FAST_EXCESS_PRECISION,
    };

    /**
     * @author DaPorkchop_
     */
    enum Option {
        /**
         * Enables more aggressive unsafe optimization of floating-point math, including calls to library functions.
         */
        UNSAFE_MATH_OPTIMIZATIONS,

        /**
         * If {@code true}, floating-point operations will be considered associative and may be re-ordered for performance.
         * <p>
         * This requires {@link #NO_SIGNED_ZERO}.
         */
        ASSOCIATIVE_MATH,

        /**
         * If {@code true}, division by a value may be replaced with multiplication by its reciprocal.
         */
        RECIPROCAL_MATH,

        /**
         * If {@code true}, assumes that floating-point values are always finite (i.e. they can never be +Inf, -Inf or NaN).
         */
        FINITE_MATH,

        /**
         * If {@code true}, allows optimizations on floating-point operations to ignore the sign of zero.
         */
        NO_SIGNED_ZERO,

        /**
         * If {@code true}, a floating-point multiplication followed by an addition may be merged into a single FMA operation.
         * <p>
         * This requires {@link #FAST_EXCESS_PRECISION}.
         */
        CONTRACT,

        /**
         * If {@code true}, floating-point operations may be performed at higher precision than specified.
         */
        FAST_EXCESS_PRECISION,
    }
}
