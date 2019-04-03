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

package net.daporkchop.lib.gui.form.data;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.common.function.PFunctions;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.form.annotation.FormComponentName;
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
                .filter(PFunctions.invert(String::isEmpty))
                .flatMap(s -> s.indexOf('\n') == -1 ? Arrays.stream(s.split("\n")) : Stream.of(s))
                .toArray(String[]::new);
    }

    protected final PField field;
    protected final String componentName;
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

    protected abstract void assertCorrectType(@NonNull PField field);
    protected abstract A defaultAnnotationInstance();
    protected abstract void doConfigure(@NonNull Component component);
    protected abstract void doLoadInto(@NonNull Object o, @NonNull Component component);
}
