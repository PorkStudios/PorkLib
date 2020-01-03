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
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.component.type.functional.CheckBox;
import net.daporkchop.lib.gui.form.annotation.FormDefaultDimensions;
import net.daporkchop.lib.gui.form.annotation.FormType;
import net.daporkchop.lib.gui.form.util.exception.FormFieldTypeMismatchException;
import net.daporkchop.lib.reflection.PField;
import net.daporkchop.lib.reflection.util.Type;

import java.lang.annotation.Annotation;

/**
 * @author DaPorkchop_
 */
@Getter
public class FormBoolean extends AbstractFormValue<FormType.Boolean> {
    public FormBoolean(@NonNull PField field)   {
        super(field, FormType.Boolean.class);
    }

    public FormBoolean(@NonNull PField field, FormType.Boolean annotation)   {
        super(field, annotation);
    }

    @Override
    protected void assertCorrectType(@NonNull PField field) {
        if (field.getType() != Type.BOOLEAN)    {
            throw new FormFieldTypeMismatchException("Field %s is not a boolean!", field);
        }
    }

    @Override
    protected FormType.Boolean defaultAnnotationInstance() {
        return new FormType.Boolean()   {
            @Override
            public boolean value() {
                return false;
            }

            @Override
            public Type type() {
                return Type.CHECK_BOX;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return FormType.Boolean.class;
            }
        };
    }

    @Override
    protected void doConfigure(@NonNull Component component) {
        switch (this.annotation.type()) {
            case CHECK_BOX: {
                if (component instanceof CheckBox) {
                    ((CheckBox) component).setSelected(this.annotation.value());
                } else {
                    throw new IllegalStateException(String.format("Component \"%s\" is not a check box: %s!", this.componentName, component.getClass().getCanonicalName()));
                }
            }
            break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    protected void doLoadInto(@NonNull Object o, @NonNull Component component) {
        switch (this.annotation.type()) {
            case CHECK_BOX: {
                if (component instanceof CheckBox) {
                    this.field.setBoolean(o, ((CheckBox) component).isSelected());
                } else {
                    throw new IllegalStateException(String.format("Component \"%s\" is not a check box: %s!", this.componentName, component.getClass().getCanonicalName()));
                }
            }
            break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    protected void doLoadFrom(@NonNull Object o, @NonNull Component component) {
        switch (this.annotation.type()) {
            case CHECK_BOX: {
                if (component instanceof CheckBox) {
                    ((CheckBox) component).setSelected(this.field.getBoolean(o));
                } else {
                    throw new IllegalStateException(String.format("Component \"%s\" is not a check box: %s!", this.componentName, component.getClass().getCanonicalName()));
                }
            }
            break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public String buildDefault(String prev, @NonNull Container container) {
        Component component;
        switch (this.annotation.type()) {
            case CHECK_BOX: {
                component = container.checkBox(this.componentName).setSelected(this.annotation.value());
            }
            break;
            default:
                throw new IllegalStateException();
        }
        this.configureDefaultDimensions(this.field.getAnnotation(FormDefaultDimensions.class), false, prev, component);
        return this.componentName;
    }
}
