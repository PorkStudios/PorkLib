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

package net.daporkchop.lib.gui.form.annotation;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

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
@UtilityClass
public class FormType {
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

        Type type() default Type.SPINNER;

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

        String hint() default "";

        Type type() default Type.TEXT_BOX;

        enum Type  {
            TEXT_BOX,
            PASSWORD,
            ;
        }
    }

    /**
     * Defines a field as containing an object value
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Object {
        Type type() default Type.PANEL;

        enum Type  {
            PANEL,
            SCROLL_PANE,
            ;
        }
    }

    /**
     * Defines a field as containing an enum value
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Enum {
        int value() default -1;

        Type type() default Type.DROPDOWN;

        boolean clearDropdownValues() default true;

        String[] externNames() default {};

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
    public @interface EnumMemberTooltip {
        String[] value();
    }

    /**
     * Defines a field as being an enum member, used by {@link net.daporkchop.lib.gui.form.annotation.FormType.Enum}
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface EnumMemberName {
        String value();
    }

    /**
     * Defines a field as being ignored by the GUI form parser
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Ignored  {
    }
}
