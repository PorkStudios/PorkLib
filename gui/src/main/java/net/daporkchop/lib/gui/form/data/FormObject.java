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
import net.daporkchop.lib.common.util.PUnsafe;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.component.Element;
import net.daporkchop.lib.gui.form.annotation.FormComponentName;
import net.daporkchop.lib.gui.form.annotation.FormType;
import net.daporkchop.lib.reflection.PField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.StringJoiner;

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
            if (element instanceof Container)   {
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
            if (element instanceof Container)   {
                this.fields.forEach(value -> value.loadInto(us, (Container) element));
            } else {
                throw new IllegalStateException(String.format("Not a container: %s (%s)", this.componentName, element == null ? "null" : element.getClass().getCanonicalName()));
            }
            this.field.set(o, us);
        } else {
            this.fields.forEach(value -> value.loadInto(o, container));
        }
    }
}
