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
import net.daporkchop.lib.gui.component.Element;
import net.daporkchop.lib.gui.component.type.functional.Slider;
import net.daporkchop.lib.gui.component.type.functional.Spinner;
import net.daporkchop.lib.gui.form.annotation.FormType;
import net.daporkchop.lib.gui.form.util.exception.FormFieldTypeMismatchException;
import net.daporkchop.lib.reflection.PField;
import net.daporkchop.lib.reflection.util.Type;

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
    public void configure(@NonNull Container container) {
        Component component = container.getChild(this.componentName);
        if (component == null)    {
            throw new IllegalStateException(String.format("No component found with name: \"%s\"!", this.componentName));
        }

        if (this.tooltip != null)   {
            component.setTooltip(this.tooltip);
        }
        switch (this.annotation.component())    {
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
    public void loadInto(@NonNull Object o, @NonNull Container container) {
        Component component = container.getChild(this.componentName);
        if (component == null)    {
            throw new IllegalStateException(String.format("No component found with name: \"%s\"!", this.componentName));
        }

        switch (this.annotation.component())    {
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
}
