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
import net.daporkchop.lib.gui.form.util.exception.FormFieldIgnoredException;
import net.daporkchop.lib.gui.form.util.exception.FormFieldTypeMismatchException;
import net.daporkchop.lib.reflection.PField;
import net.daporkchop.lib.reflection.util.Type;

/**
 * @author DaPorkchop_
 */
public interface FormValue {
    static FormValue of(@NonNull PField field) {
        if (field.hasAnnotation(FormType.Ignored.class)) {
            throw new FormFieldIgnoredException(field.toString());
        } else if (field.hasAnnotation(FormType.Int.class)) {
            if (field.getType() == Type.INT) {
                return new FormInt(field, field.getAnnotation(FormType.Int.class));
            } else {
                throw new FormFieldTypeMismatchException("Field %s is not an int, but has Int annotation!", field);
            }
        } else if (field.hasAnnotation(FormType.Boolean.class)) {
            if (field.getType() == Type.BOOLEAN)    {
                return new FormBoolean(field, field.getAnnotation(FormType.Boolean.class));
            } else {
                throw new FormFieldTypeMismatchException("Field %s is not a boolean, but has Boolean annotation!", field);
            }
        } else //TODO
    }

    void configure(@NonNull Container container);

    void loadInto(@NonNull Object o, @NonNull Container container);
}
