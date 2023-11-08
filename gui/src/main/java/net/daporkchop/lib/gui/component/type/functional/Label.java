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

package net.daporkchop.lib.gui.component.type.functional;

import lombok.NonNull;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.capability.IconHolder;
import net.daporkchop.lib.gui.component.capability.TextHolder;
import net.daporkchop.lib.gui.component.state.functional.LabelState;

import java.awt.Color;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;

/**
 * The simplest possible GUI component. A label can display text, nothing more.
 *
 * @author DaPorkchop_
 */
public interface Label extends Component<Label, LabelState>, IconHolder<Label, LabelState>, TextHolder<Label> {
    @Override
    default LabelState getState() {
        return this.isVisible() ?
                this.isEnabled() ?
                        this.isHovered() ? LabelState.ENABLED_HOVERED : LabelState.ENABLED
                        : this.isHovered() ? LabelState.DISABLED_HOVERED : LabelState.DISABLED
                : LabelState.HIDDEN;
    }

    Label setColor(Color color);
    Label setColor(int argb);

    default Label trackSpinner(@NonNull Spinner spinner, @NonNull DoubleFunction<String> formatter)    {
        DoubleConsumer callback = val -> this.setText(formatter.apply(val));
        spinner.addChangeListenerD(callback);
        callback.accept(spinner.getValue());
        return this;
    }
    default Label trackSpinner(@NonNull Spinner spinner, @NonNull String format)    {
        return this.trackSpinner(spinner, val -> String.format(format, val));
    }
    default Label trackSpinner(@NonNull Spinner spinner)    {
        return this.trackSpinner(spinner, String::valueOf);
    }
    default Label trackSpinner(@NonNull String qualifiedName, @NonNull DoubleFunction<String> formatter)   {
        return this.trackSpinner(this.getWindow().<Spinner>getChild(qualifiedName), formatter);
    }
    default Label trackSpinner(@NonNull String qualifiedName, @NonNull String format)   {
        return this.trackSpinner(qualifiedName, val -> String.format(format, val));
    }
    default Label trackSpinner(@NonNull String qualifiedName)   {
        return this.trackSpinner(qualifiedName, String::valueOf);
    }

    default Label trackSlider(@NonNull Slider slider, @NonNull IntFunction<String> formatter)   {
        IntConsumer callback = val -> this.setText(formatter.apply(val));
        slider.addChangeListener(callback);
        callback.accept(slider.getValue());
        return this;
    }
    default Label trackSlider(@NonNull Slider slider, @NonNull String format)   {
        return this.trackSlider(slider, val -> String.format(format, val));
    }
    default Label trackSlider(@NonNull Slider slider)   {
        return this.trackSlider(slider, String::valueOf);
    }
    default Label trackSlider(@NonNull String qualifiedName, @NonNull IntFunction<String> formatter)   {
        return this.trackSlider(this.getWindow().<Slider>getChild(qualifiedName), formatter);
    }
    default Label trackSlider(@NonNull String qualifiedName, @NonNull String format)   {
        return this.trackSlider(qualifiedName, val -> String.format(format, val));
    }
    default Label trackSlider(@NonNull String qualifiedName)   {
        return this.trackSlider(qualifiedName, String::valueOf);
    }
}
