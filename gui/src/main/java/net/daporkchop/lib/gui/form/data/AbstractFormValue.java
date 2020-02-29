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

package net.daporkchop.lib.gui.form.data;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.common.function.PFunctions;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.form.annotation.FormComponentName;
import net.daporkchop.lib.gui.form.annotation.FormDisplayName;
import net.daporkchop.lib.gui.form.annotation.FormTooltip;
import net.daporkchop.lib.reflection.PField;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author DaPorkchop_
 */
@Getter
public abstract class AbstractFormValue<A extends Annotation> implements FormValue {
    protected static String[] parseTooltip(@NonNull String[] source) {
        return Arrays.stream(source)
                .filter(Objects::nonNull)
                .filter(PFunctions.not(String::isEmpty))
                .flatMap(s -> s.indexOf('\n') == -1 ? Arrays.stream(s.split("\n")) : Stream.of(s))
                .toArray(String[]::new);
    }

    protected final PField field;
    protected final String componentName;
    protected final String displayName;
    protected final String[] tooltip;
    protected final A annotation;

    public AbstractFormValue(@NonNull PField field, @NonNull Class<A> annotationClass) {
        this(field, field.getAnnotation(annotationClass));
    }

    public AbstractFormValue(@NonNull PField field, A annotationInstance) {
        this.assertCorrectType(field);
        this.field = field;
        {
            FormComponentName annotation = field.getAnnotation(FormComponentName.class);
            if (annotation == null) {
                this.componentName = field.getName();
            } else {
                this.componentName = annotation.value();
            }
        }
        {
            FormDisplayName annotation = field.getAnnotation(FormDisplayName.class);
            if (annotation == null) {
                this.displayName = field.getName() + ": ";
            } else {
                this.displayName = annotation.value() + ": ";
            }
        }
        {
            FormTooltip tooltip = field.getAnnotation(FormTooltip.class);
            if (tooltip == null) {
                this.tooltip = new String[0];
            } else {
                this.tooltip = parseTooltip(tooltip.value());
            }
        }
        this.annotation = annotationInstance == null ? this.defaultAnnotationInstance() : annotationInstance;
    }

    @Override
    public void configure(@NonNull Container container) {
        Component component = container.getChild(this.componentName);
        if (component == null) {
            throw new IllegalStateException(String.format("No component found with name: \"%s\"!", this.componentName));
        }

        if (this.tooltip != null && this.tooltip.length > 0) {
            component.setTooltip(this.tooltip);
        }
        this.doConfigure(component);
    }

    @Override
    public void loadInto(@NonNull Object o, @NonNull Container container) {
        Component component = container.getChild(this.componentName);
        if (component == null) {
            throw new IllegalStateException(String.format("No component found with name: \"%s\"!", this.componentName));
        }
        this.doLoadInto(o, component);
    }

    @Override
    public void loadFrom(@NonNull Object o, @NonNull Container container) {
        Component component = container.getChild(this.componentName);
        if (component == null) {
            throw new IllegalStateException(String.format("No component found with name: \"%s\"!", this.componentName));
        }
        this.doLoadFrom(o, component);
    }

    protected abstract void assertCorrectType(@NonNull PField field);
    protected abstract A defaultAnnotationInstance();
    protected abstract void doConfigure(@NonNull Component component);
    protected abstract void doLoadInto(@NonNull Object o, @NonNull Component component);
    protected abstract void doLoadFrom(@NonNull Object o, @NonNull Component component);
}
