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

package net.daporkchop.lib.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a class as the root class for a configuration
 *
 * @author DaPorkchop_
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {
    /**
     * Whether or not this configuration should be stored in a single static field, or simply
     * returned as a local object.
     * <p>
     * To set the name of the static field, use {@link #staticName()}
     */
    boolean staticInstance() default false;

    /**
     * The name of the static field that the configuration will be stored in.
     * <p>
     * If no field could be found with the matching name, or the field is of the wrong type, an
     * exception will be thrown when attempting to load the config.
     */
    String staticName() default "INSTANCE";

    /**
     * Allows setting a custom name for a config category or value.
     */
    @Target({ElementType.TYPE, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Name {
        /**
         * The name of this element relative to the current namespace
         */
        String value();
    }

    /**
     * Allows adding a comment to a config entry.
     * <p>
     * Note that comments will only be added on encoding systems that support comments.
     */
    @Target({ElementType.TYPE, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Comment {
        /**
         * The comment to set. The comment may be given as an array of {@link String}s, where each element
         * in the array corresponds to a single comment line, as a single {@link String} with newlines, or
         * as a combination of both. If the array is empty or all elements required the array are empty, the
         * comment will not be added.
         */
        String[] value();
    }

    /**
     * Allows specifying the class to be used for initializing a value.
     * <p>
     * Note that no constructors will be invoked on the object during initialization.
     * <p>
     * If not added, this will simply create an instance of whatever the type required the field is.
     */
    @Target({ElementType.TYPE, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Implementation {
        /**
         * The class to use for this value.
         */
        Class<?> value();
    }

    /**
     * Marks a given field as having a default value.
     *
     * This prevents fields from throwing an exception when they can't be initialized from config.
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Default {
        boolean booleanValue() default false;

        byte byteValue() default 0;

        short shortValue() default 0;

        int intValue() default 0;

        long longValue() default 0L;

        float floatValue() default 0.0f;

        double doubleValue() default 0.0d;

        char charValue() default '\u0000';

        /**
         * The full name of a no-args static function that will return the default value for this field
         */
        String objectValue() default "net.daporkchop.lib.common.util.PorkUtil#getNull";
    }
}
