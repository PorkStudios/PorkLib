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
         * as a combination of both. If the array is empty or all elements of the array are empty, the
         * comment will not be added.
         */
        String[] value();
    }

    /**
     * Allows specifying the class to be used for initializing a value.
     * <p>
     * Note that no constructors will be invoked on the object during initialization.
     * <p>
     * If not added, this will simply create an instance of whatever the type of the field is.
     */
    @Target({ElementType.TYPE, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Implementation {
        /**
         * The class to use for this value.
         */
        Class<?> value();

        /**
         * Whether or not all fields in the object must be set. If {@code true}, an exception will be thrown
         * if a field in the class could not be obtained from the config.
         */
        boolean requiredAll() default true;
    }
}
