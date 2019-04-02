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

package net.daporkchop.lib.gui.form.annotation;

import lombok.Getter;
import lombok.NonNull;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Holder class for annotations for configuring fields in a form
 *
 * @author DaPorkchop_
 */
public abstract class FormType {
    private FormType() {
        throw new IllegalStateException();
    }

    /**
     * Defines a field as containing an int value
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Int {
        int value() default 0;
        int min() default 0;
        int max() default 100;
        int step() default 1;

        Type component() default Type.SPINNER;

        enum Type  {
            SPINNER,
            SLIDER,
            ;
        }
    }

    /**
     * Defines a field as containing a boolean value
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Boolean {
        boolean value() default false;

        Type type() default Type.CHECK_BOX;

        enum Type {
            CHECK_BOX,
            ;
        }
    }

    /**
     * Defines a field as containing a text value
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Text {
        String value() default "";

        Type component() default Type.TEXT_BOX;

        enum Type  {
            TEXT_BOX,
            ;
        }
    }

    /**
     * Defines a field as containing an enum value
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Enum {
        boolean value() default false;

        Type type() default Type.DROPDOWN;

        enum Type {
            DROPDOWN,
            RADIO_BUTTON,
            ;
        }
    }

    /**
     * Defines a field as being an enum member, used by {@link net.daporkchop.lib.gui.form.annotation.FormType.Enum}
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface EnumMember {
    }

    /**
     * Defines a field as being ignored by the GUI form parser
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Ignored  {
    }
}
