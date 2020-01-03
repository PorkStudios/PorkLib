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
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.component.type.functional.Slider;
import net.daporkchop.lib.gui.component.type.functional.Spinner;
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
public class FormInt extends AbstractFormValue<FormType.Int> {
    public FormInt(@NonNull PField field)   {
        super(field, FormType.Int.class);
    }

    public FormInt(@NonNull PField field, FormType.Int annotation)   {
        super(field, annotation);
    }

    @Override
    protected void assertCorrectType(@NonNull PField field) {
        if (field.getType() != Type.INT)    {
            throw new FormFieldTypeMismatchException("Field %s is not an int!", field);
        }
    }

    @Override
    protected FormType.Int defaultAnnotationInstance() {
        return new FormType.Int()   {
            @Override
            public int value() {
                return 0;
            }

            @Override
            public int min() {
                return 0;
            }

            @Override
            public int max() {
                return 100;
            }

            @Override
            public int step() {
                return 1;
            }

            @Override
            public Type type() {
                return Type.SPINNER;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return FormType.Int.class;
            }
        };
    }

    @Override
    protected void doConfigure(@NonNull Component component) {
        switch (this.annotation.type())    {
            case SPINNER: {
                if (component instanceof Spinner) {
                    ((Spinner) component)
                            .setValAndLimits(this.annotation.value(), this.annotation.min(), this.annotation.max())
                            .setStep(this.annotation.step());
                } else {
                    throw new IllegalStateException(String.format("Component \"%s\" is not a spinner: %s!", this.componentName, component.getClass().getCanonicalName()));
                }
            }
            break;
            case SLIDER: {
                if (component instanceof Slider) {
                    ((Slider) component)
                            .setValAndLimits(this.annotation.value(), this.annotation.min(), this.annotation.max())
                            .setStep(this.annotation.step());
                } else {
                    throw new IllegalStateException(String.format("Component \"%s\" is not a slider: %s!", this.componentName, component.getClass().getCanonicalName()));
                }
            }
            break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    protected void doLoadInto(@NonNull Object o, @NonNull Component component) {
        switch (this.annotation.type())    {
            case SPINNER: {
                if (component instanceof Spinner) {
                    this.field.setInt(o, ((Spinner) component).getValue());
                } else {
                    throw new IllegalStateException(String.format("Component \"%s\" is not a spinner: %s!", this.componentName, component.getClass().getCanonicalName()));
                }
            }
            break;
            case SLIDER: {
                if (component instanceof Slider) {
                    this.field.setInt(o, ((Slider) component).getValue());
                } else {
                    throw new IllegalStateException(String.format("Component \"%s\" is not a slider: %s!", this.componentName, component.getClass().getCanonicalName()));
                }
            }
            break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    protected void doLoadFrom(@NonNull Object o, @NonNull Component component) {
        switch (this.annotation.type())    {
            case SPINNER: {
                if (component instanceof Spinner) {
                    ((Spinner) component).setValue(this.field.getInt(o));
                } else {
                    throw new IllegalStateException(String.format("Component \"%s\" is not a spinner: %s!", this.componentName, component.getClass().getCanonicalName()));
                }
            }
            break;
            case SLIDER: {
                if (component instanceof Slider) {
                    ((Slider) component).setValue(this.field.getInt(o));
                } else {
                    throw new IllegalStateException(String.format("Component \"%s\" is not a slider: %s!", this.componentName, component.getClass().getCanonicalName()));
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
            case SPINNER: {
                component = container.spinner(this.componentName)
                        .setValAndLimits(this.annotation.value(), this.annotation.min(), this.annotation.max())
                        .setStep(this.annotation.step());
            }
            break;
            case SLIDER: {
                component = container.slider(this.componentName)
                        .setValAndLimits(this.annotation.value(), this.annotation.min(), this.annotation.max())
                        .setStep(this.annotation.step());
            }
            break;
            default:
                throw new IllegalStateException();
        }
        this.configureDefaultDimensions(this.field.getAnnotation(FormDefaultDimensions.class), false, prev, component);
        return this.componentName;
    }
}
