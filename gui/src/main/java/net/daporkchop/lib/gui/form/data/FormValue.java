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

import lombok.NonNull;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.form.annotation.FormType;
import net.daporkchop.lib.gui.form.util.exception.FormException;
import net.daporkchop.lib.gui.form.util.exception.FormFieldIgnoredException;
import net.daporkchop.lib.reflection.PField;

/**
 * @author DaPorkchop_
 */
public interface FormValue {
    @SuppressWarnings("unchecked")
    static FormValue of(@NonNull PField field) {
        if (field.hasAnnotation(FormType.Ignored.class)) {
            throw new FormFieldIgnoredException(field.toString());
        } else {
            try {
                return new FormInt(field);
            } catch (FormException e0) {
                try {
                    return new FormBoolean(field);
                } catch (FormException e1) {
                    try {
                        return new FormString(field);
                    } catch (FormException e2) {
                        try {
                            return new FormEnum<>(field);
                        } catch (FormException e3) {
                            try {
                                return new FormObject(field.getClassType(), field);
                            } catch (FormException e4) {
                                throw new IllegalArgumentException(String.format("Unknown type for field %s!", field));
                            }
                        }
                    }
                }
            }
        }
    }

    void configure(@NonNull Container container);

    void loadInto(@NonNull Object o, @NonNull Container container);
}
