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
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.component.Element;
import net.daporkchop.lib.gui.component.NestedContainer;
import net.daporkchop.lib.gui.form.annotation.FormComponentName;
import net.daporkchop.lib.gui.form.annotation.FormDefaultDimensions;
import net.daporkchop.lib.gui.form.annotation.FormType;
import net.daporkchop.lib.reflection.PField;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author DaPorkchop_
 */
@Getter
public class FormObject implements FormValue {
    protected final Class<?> clazz;
    protected final String componentName;
    protected final PField field;
    protected final Collection<FormValue> fields = new ArrayList<>();

    public FormObject(@NonNull Class<?> clazz, PField aField) {
        this.clazz = clazz;
        this.field = aField;

        if (aField != null) {
            FormComponentName annotation = aField.getAnnotation(FormComponentName.class);
            if (annotation == null) {
                this.componentName = aField.getName();
            } else {
                this.componentName = annotation.value();
            }
        } else {
            this.componentName = null;
        }

        Arrays.stream(clazz.getDeclaredFields())
                .map(PField::of)
                .filter(PFunctions.invert(PField::isStatic))
                .filter(PFunctions.invert(field -> field.hasAnnotation(FormType.Ignored.class)))
                .map(FormValue::of)
                .forEach(this.fields::add);
    }

    @Override
    public void configure(@NonNull Container container) {
        if (this.field == null) {
            this.fields.forEach(value -> value.configure(container));
        } else {
            Element element = container.getChild(this.componentName);
            if (element instanceof Container) {
                this.fields.forEach(value -> value.configure((Container) element));
            } else {
                throw new IllegalStateException(String.format("Not a container: %s (%s)", this.componentName, element == null ? "null" : element.getClass().getCanonicalName()));
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loadInto(@NonNull Object o, @NonNull Container container) {
        if (this.field != null) {
            Object us = PUnsafe.allocateInstance(this.clazz);
            Element element = container.getChild(this.componentName);
            if (element instanceof Container) {
                this.fields.forEach(value -> value.loadInto(us, (Container) element));
            } else {
                throw new IllegalStateException(String.format("Not a container: %s (%s)", this.componentName, element == null ? "null" : element.getClass().getCanonicalName()));
            }
            this.field.set(o, us);
        } else {
            this.fields.forEach(value -> value.loadInto(o, container));
        }
    }

    @Override
    public String buildDefault(String prev, @NonNull Container container) {
        if (this.field != null) {
            Objects.requireNonNull(prev);
            FormType.Object annotation = this.field.getAnnotation(FormType.Object.class);
            if (annotation == null) {
                annotation = new FormType.Object() {
                    @Override
                    public Type type() {
                        return Type.SCROLL_PANE;
                    }

                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return FormType.Object.class;
                    }
                };
            }
            switch (annotation.type()) {
                case PANEL:
                    container = container.panel(this.field.getName());
                    break;
                case SCROLL_PANE:
                    container = container.scrollPane(this.field.getName());
                    break;
                default:
                    throw new IllegalStateException();
            }
            this.configureDefaultDimensions(this.field.getAnnotation(FormDefaultDimensions.class), true, prev, (NestedContainer<?, ?>) container, annotation.type() != FormType.Object.Type.SCROLL_PANE);
        }
        AtomicReference<String> ref = new AtomicReference<>(null);
        Container theContainer = container;
        this.fields.forEach(value -> ref.set(value.buildDefault(ref.get(), theContainer)));
        return this.field == null ? ref.get() : this.componentName;
    }
}
