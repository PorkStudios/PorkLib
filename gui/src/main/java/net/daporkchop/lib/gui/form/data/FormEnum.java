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
import net.daporkchop.lib.common.function.throwing.EFunction;
import net.daporkchop.lib.common.misc.Tuple;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.form.annotation.FormType;
import net.daporkchop.lib.gui.form.util.exception.FormFieldTypeMismatchException;
import net.daporkchop.lib.reflection.PField;
import net.daporkchop.lib.reflection.PReflection;
import net.daporkchop.lib.reflection.util.Type;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author DaPorkchop_
 */
@Getter
public class FormEnum<E extends Enum> extends AbstractFormValue<FormType.Enum> {
    protected static final Map<Class<? extends Enum>, Map<? extends Enum, String[]>> tooltipCache = PorkUtil.newSoftCache();

    @SuppressWarnings("unchecked")
    protected static <E extends Enum> Map<E, String[]> getTooltips(@NonNull Class<E> clazz) {
        return (Map<E, String[]>) tooltipCache.computeIfAbsent(clazz, c -> Arrays.stream(c.getDeclaredFields())
                .filter(Field::isEnumConstant)
                .map(field -> new Tuple<>(field, PReflection.getAnnotation(field, FormType.EnumMemberTooltip.class)))
                .filter(Tuple::isBNonNull)
                .collect(Collectors.toMap(
                        (EFunction<Tuple<Field, FormType.EnumMemberTooltip>, E>) t -> (E) t.getA().get(null),
                        t -> parseTooltip(t.getB().value())
                )));
    }

    protected final Map<E, String[]> tooltips;

    public FormEnum(@NonNull PField<E> field) {
        super(field, FormType.Enum.class);
        this.tooltips = getTooltips(field.getClassType());
    }

    public FormEnum(@NonNull PField<E> field, FormType.Enum annotation) {
        super(field, annotation);
        this.tooltips = getTooltips(field.getClassType());
    }

    @Override
    protected void assertCorrectType(@NonNull PField field) {
        if (!(field.getType() == Type.OBJECT && Enum.class.isAssignableFrom(field.getClassType()))) {
            throw new FormFieldTypeMismatchException("Field %s is not an enum!", field);
        }
    }

    @Override
    protected void doConfigure(@NonNull Component component) {
        //TODO
    }

    @Override
    protected void doLoadInto(@NonNull Object o, @NonNull Component component) {

    }
}
