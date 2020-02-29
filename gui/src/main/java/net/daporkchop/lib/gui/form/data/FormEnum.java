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
import net.daporkchop.lib.common.function.throwing.EFunction;
import net.daporkchop.lib.common.misc.Tuple;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.component.type.functional.Dropdown;
import net.daporkchop.lib.gui.component.type.functional.RadioButton;
import net.daporkchop.lib.gui.component.type.misc.RadioButtonGroup;
import net.daporkchop.lib.gui.form.annotation.FormDefaultDimensions;
import net.daporkchop.lib.gui.form.annotation.FormType;
import net.daporkchop.lib.gui.form.util.exception.FormFieldTypeMismatchException;
import net.daporkchop.lib.reflection.PField;
import net.daporkchop.lib.reflection.PReflection;
import net.daporkchop.lib.reflection.util.Type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author DaPorkchop_
 */
@Getter
public class FormEnum<E extends Enum> extends AbstractFormValue<FormType.Enum> {
    protected static final Map<Class<? extends Enum>, Map<? extends Enum, String[]>> tooltipCache = PorkUtil.newSoftCache();
    protected static final Map<Class<? extends Enum>, Map<? extends Enum, String>> nameCache = PorkUtil.newSoftCache();

    @SuppressWarnings("unchecked")
    protected static <E extends Enum> Map<E, String[]> getTooltips(@NonNull Class<E> clazz) {
        return (Map<E, String[]>) tooltipCache.computeIfAbsent(clazz, c -> Arrays.stream(c.getDeclaredFields())
                .filter(Field::isEnumConstant)
                .map(field -> new Tuple<>(field, PReflection.getAnnotation(field, FormType.EnumMemberTooltip.class)))
                .filter(Tuple::isBNonNull)
                .collect(Collectors.toMap(
                        (EFunction<Tuple<Field, FormType.EnumMemberTooltip>, E>) t -> {
                            t.getA().setAccessible(true);
                            return (E) t.getA().get(null);
                        },
                        t -> parseTooltip(t.getB().value())
                )));
    }

    @SuppressWarnings("unchecked")
    protected static <E extends Enum> Map<E, String> getNames(@NonNull Class<E> clazz, @NonNull FormType.Enum annotation) {
        Map<E, String> names = (Map<E, String>) nameCache.computeIfAbsent(clazz, c -> Arrays.stream(c.getDeclaredFields())
                .filter(Field::isEnumConstant)
                .map(field -> new Tuple<>(field, PReflection.getAnnotation(field, FormType.EnumMemberName.class)))
                .filter(Tuple::isBNonNull)
                .collect(Collectors.toMap(
                        (EFunction<Tuple<Field, FormType.EnumMemberName>, E>) t -> {
                            t.getA().setAccessible(true);
                            return (E) t.getA().get(null);
                        },
                        t -> t.getB().value()
                )));

        String[] externNames = annotation.externNames();
        if (externNames.length != 0)    {
            names = new HashMap<>(names);
            E[] values = clazz.getEnumConstants();
            for (int i = 0; i < externNames.length && i < values.length; i++)    {
                if (!externNames[i].isEmpty())  {
                    names.putIfAbsent(values[i], externNames[i]);
                }
            }
        }

        return names;
    }

    protected final Map<E, String[]> tooltips;
    protected final Map<E, String> names;

    public FormEnum(@NonNull PField<E> field) {
        super(field, FormType.Enum.class);
        if (this.annotation.type() == FormType.Enum.Type.RADIO_BUTTON) {
            this.tooltips = getTooltips(field.getClassType());
        } else {
            this.tooltips = Collections.emptyMap();
        }
        this.names = getNames(field.getClassType(), this.annotation);
    }

    public FormEnum(@NonNull PField<E> field, FormType.Enum annotation) {
        super(field, annotation);
        if (this.annotation.type() == FormType.Enum.Type.RADIO_BUTTON) {
            this.tooltips = getTooltips(field.getClassType());
        } else {
            this.tooltips = Collections.emptyMap();
        }
        this.names = getNames(field.getClassType(), this.annotation);
    }

    @Override
    protected void assertCorrectType(@NonNull PField field) {
        if (!(field.getType() == Type.OBJECT && Enum.class.isAssignableFrom(field.getClassType()))) {
            throw new FormFieldTypeMismatchException("Field %s is not an enum!", field);
        }
    }

    @Override
    protected FormType.Enum defaultAnnotationInstance() {
        return new FormType.Enum() {
            @Override
            public int value() {
                return 0;
            }

            @Override
            public Type type() {
                return Type.DROPDOWN;
            }

            @Override
            public boolean clearDropdownValues() {
                return true;
            }

            @Override
            public String[] externNames() {
                return new String[0];
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return FormType.Enum.class;
            }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doConfigure(@NonNull Component component) {
        switch (this.annotation.type()) {
            case DROPDOWN: {
                if (component instanceof Dropdown) {
                    Dropdown dropdown = (Dropdown) component;
                    if (this.annotation.clearDropdownValues()) {
                        dropdown.clearValues();
                    }
                    E[] values = ((Class<E>) this.field.getClassType()).getEnumConstants();
                    if (this.annotation.value() >= 0 && this.annotation.value() >= values.length) {
                        throw new IllegalArgumentException(String.format("Enum class %s has %d values, but the %dth was requested!", this.field.getClassType().getCanonicalName(), values.length, this.annotation.value()));
                    }
                    dropdown.addValues(values);
                    if (this.annotation.value() >= 0) {
                        dropdown.setSelectedValue(values[this.annotation.value()]);
                    }
                } else {
                    throw new IllegalStateException(String.format("Component \"%s\" is not a dropdown: %s!", this.componentName, component.getClass().getCanonicalName()));
                }
            }
            break;
            case RADIO_BUTTON: {
                if (component instanceof RadioButtonGroup) {
                    RadioButtonGroup group = (RadioButtonGroup) component;
                    E[] values = ((Class<E>) this.field.getClassType()).getEnumConstants();
                    if (this.annotation.value() >= 0 && this.annotation.value() >= values.length) {
                        throw new IllegalArgumentException(String.format("Enum class %s has %d values, but the %dth was requested!", this.field.getClassType().getCanonicalName(), values.length, this.annotation.value()));
                    }
                    for (E value : values) {
                        RadioButton button = group.getButtonByName(value.name());
                        if (button == null) {
                            throw new IllegalStateException(String.format("Could not find radio button with name: \"%s\"!", value.name()));
                        } else {
                            String[] tooltip = this.tooltips.get(value);
                            if (tooltip != null) {
                                button.setTooltip(tooltip);
                            }
                            button.setText(this.names.getOrDefault(value, value.name()));
                        }
                    }
                    if (this.annotation.value() >= 0) {
                        group.getButtonByName(values[this.annotation.value()].name()).setSelected(true);
                    }
                } else {
                    throw new IllegalStateException(String.format("Component \"%s\" is not a radio button group: %s!", this.componentName, component.getClass().getCanonicalName()));
                }
            }
            break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doLoadInto(@NonNull Object o, @NonNull Component component) {
        switch (this.annotation.type()) {
            case DROPDOWN: {
                if (component instanceof Dropdown) {
                    this.field.set(o, ((Dropdown<E>) component).getSelectedValue());
                } else {
                    throw new IllegalStateException(String.format("Component \"%s\" is not a text box: %s!", this.componentName, component.getClass().getCanonicalName()));
                }
            }
            break;
            case RADIO_BUTTON: {
                if (component instanceof RadioButtonGroup) {
                    E[] values = ((Class<E>) this.field.getClassType()).getEnumConstants();
                    String selectedName = ((RadioButtonGroup) component).getSelected().getName();
                    this.field.set(o, Arrays.stream(values)
                            .filter(v -> v.name().equals(selectedName))
                            .findAny().orElseThrow(IllegalStateException::new));
                } else {
                    throw new IllegalStateException(String.format("Component \"%s\" is not a radio button group: %s!", this.componentName, component.getClass().getCanonicalName()));
                }
            }
            break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doLoadFrom(@NonNull Object o, @NonNull Component component) {
        switch (this.annotation.type()) {
            case DROPDOWN: {
                if (component instanceof Dropdown) {
                    ((Dropdown<E>) component).setSelectedValue((E) this.field.get(o));
                } else {
                    throw new IllegalStateException(String.format("Component \"%s\" is not a text box: %s!", this.componentName, component.getClass().getCanonicalName()));
                }
            }
            break;
            case RADIO_BUTTON: {
                //TODO
                throw new UnsupportedOperationException("Radio button");
            }
            //break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public String buildDefault(String prev, @NonNull Container container) {
        Component component;
        switch (this.annotation.type()) {
            case DROPDOWN: {
                this.doConfigure(component = container.<E>dropdown(this.componentName));
                ((Dropdown<E>) component).setRendererText(val -> this.names.getOrDefault(val, val.toString()));
            }
            break;
            case RADIO_BUTTON: {
                //TODO
                throw new UnsupportedOperationException("Radio button");
            }
            //break;
            default:
                throw new IllegalStateException();
        }
        this.configureDefaultDimensions(this.field.getAnnotation(FormDefaultDimensions.class), false, prev, component);
        return this.componentName;
    }
}
