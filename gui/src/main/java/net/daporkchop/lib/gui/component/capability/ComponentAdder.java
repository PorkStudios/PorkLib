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

package net.daporkchop.lib.gui.component.capability;

import lombok.NonNull;
import net.daporkchop.lib.gui.component.type.container.Panel;
import net.daporkchop.lib.gui.component.type.container.ScrollPane;
import net.daporkchop.lib.gui.component.type.functional.Button;
import net.daporkchop.lib.gui.component.type.functional.CheckBox;
import net.daporkchop.lib.gui.component.type.functional.Dropdown;
import net.daporkchop.lib.gui.component.type.functional.Label;
import net.daporkchop.lib.gui.component.type.functional.ProgressBar;
import net.daporkchop.lib.gui.component.type.functional.RadioButton;
import net.daporkchop.lib.gui.component.type.functional.Spinner;
import net.daporkchop.lib.gui.component.type.functional.TextBox;
import net.daporkchop.lib.gui.component.type.misc.RadioButtonGroup;

import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
@SuppressWarnings("unchecked")
public interface ComponentAdder<Impl> {
    //
    //
    //nested containers
    //
    //

    //panel
    Panel panel(@NonNull String name);

    default Impl panel(@NonNull String name, @NonNull Consumer<Panel> initializer) {
        Panel panel = this.panel(name);
        initializer.accept(panel);
        return (Impl) this;
    }

    //scrollpane
    ScrollPane scrollPane(@NonNull String name);

    default Impl scrollPane(@NonNull String name, @NonNull Consumer<ScrollPane> initializer) {
        ScrollPane scrollPane = this.scrollPane(name);
        initializer.accept(scrollPane);
        return (Impl) this;
    }

    //
    //
    //functional components
    //
    //

    //button
    Button button(@NonNull String name);

    default Impl button(@NonNull String name, @NonNull Consumer<Button> initializer) {
        Button button = this.button(name);
        initializer.accept(button);
        return (Impl) this;
    }

    //checkbox
    CheckBox checkBox(@NonNull String name);

    default Impl checkBox(@NonNull String name, @NonNull Consumer<CheckBox> initializer)    {
        CheckBox checkBox = this.checkBox(name);
        initializer.accept(checkBox);
        return (Impl) this;
    }

    //dropdown
    <V> Dropdown<V> dropdown(@NonNull String name);

    default <V> Impl dropdown(@NonNull String name, @NonNull Consumer<Dropdown<V>> initializer) {
        Dropdown<V> dropdown = this.dropdown(name);
        initializer.accept(dropdown);
        return (Impl) this;
    }

    default <V extends Enum<V>> Impl dropdown(@NonNull String name, @NonNull Class<V> enumClazz, @NonNull Consumer<Dropdown<V>> initializer) {
        Dropdown<V> dropdown = this.<V>dropdown(name).addValues(enumClazz.getEnumConstants()).setSelectedValue(null);
        if (Defaulted.class.isAssignableFrom(enumClazz)) {
            for (Defaulted value : (Defaulted[]) enumClazz.getEnumConstants()) {
                if (value.isDefaultValue()) {
                    dropdown.setSelectedValue((V) value);
                    break;
                }
            }
        }
        initializer.accept(dropdown);
        return (Impl) this;
    }

    //label
    Label label(@NonNull String name);

    default Impl label(@NonNull String name, @NonNull Consumer<Label> initializer) {
        Label label = this.label(name);
        initializer.accept(label);
        return (Impl) this;
    }

    default Label label(@NonNull String name, @NonNull String text) {
        return this.label(name).setText(text);
    }

    default Impl label(@NonNull String name, @NonNull String text, @NonNull Consumer<Label> initializer) {
        Label label = this.label(name).setText(text);
        initializer.accept(label);
        return (Impl) this;
    }

    //progress bar
    ProgressBar progressBar(@NonNull String name);

    default ProgressBar progressBar(@NonNull String name, int end)  {
        return this.progressBar(name).setEnd(end);
    }

    default ProgressBar infiniteProgressBar(@NonNull String name)  {
        return this.progressBar(name).setInfinite(true);
    }

    default Impl progressBar(@NonNull String name, @NonNull Consumer<ProgressBar> initializer)  {
        ProgressBar progressBar = this.progressBar(name);
        initializer.accept(progressBar);
        return (Impl) this;
    }

    default Impl progressBar(@NonNull String name, int end, @NonNull Consumer<ProgressBar> initializer)  {
        ProgressBar progressBar = this.progressBar(name, end);
        initializer.accept(progressBar);
        return (Impl) this;
    }

    default Impl infiniteProgressBar(@NonNull String name, @NonNull Consumer<ProgressBar> initializer)  {
        ProgressBar progressBar = this.infiniteProgressBar(name);
        initializer.accept(progressBar);
        return (Impl) this;
    }

    //radio button
    RadioButton radioButton(@NonNull String name, @NonNull RadioButtonGroup group);
    RadioButton radioButton(@NonNull String name, @NonNull String groupName);

    default Impl radioButton(@NonNull String name, @NonNull RadioButtonGroup group, @NonNull Consumer<RadioButton> initializer) {
        RadioButton radioButton = this.radioButton(name, group);
        initializer.accept(radioButton);
        return (Impl) this;
    }

    default Impl radioButton(@NonNull String name, @NonNull String groupName, @NonNull Consumer<RadioButton> initializer) {
        RadioButton radioButton = this.radioButton(name, groupName);
        initializer.accept(radioButton);
        return (Impl) this;
    }

    //spinner
    Spinner spinner(@NonNull String name);

    default Spinner spinner(@NonNull String name, int val, int min, int max, int step)  {
        return this.spinner(name).setValAndLimits(val, min, max).setStep(step);
    }

    default Impl spinner(@NonNull String name, @NonNull Consumer<Spinner> initializer)  {
        Spinner spinner = this.spinner(name);
        initializer.accept(spinner);
        return (Impl) this;
    }

    default Impl spinner(@NonNull String name, int val, int min, int max, int step, @NonNull Consumer<Spinner> initializer)  {
        Spinner spinner = this.spinner(name, val, min, max, step);
        initializer.accept(spinner);
        return (Impl) this;
    }

    //text box
    TextBox textBox(@NonNull String name);

    default TextBox textBox(@NonNull String name, @NonNull String defaultText) {
        return this.textBox(name).setText(defaultText);
    }

    default Impl textBox(@NonNull String name, @NonNull Consumer<TextBox> initializer)  {
        TextBox textBox = this.textBox(name);
        initializer.accept(textBox);
        return (Impl) this;
    }

    default Impl textBox(@NonNull String name, @NonNull String defaultText, @NonNull Consumer<TextBox> initializer)  {
        TextBox textBox = this.textBox(name).setText(defaultText);
        initializer.accept(textBox);
        return (Impl) this;
    }

    TextBox passwordBox(@NonNull String name);

    default TextBox passwordBox(@NonNull String name, @NonNull String defaultText) {
        return this.passwordBox(name).setText(defaultText);
    }

    default Impl passwordBox(@NonNull String name, @NonNull Consumer<TextBox> initializer)  {
        TextBox passwordBox = this.passwordBox(name);
        initializer.accept(passwordBox);
        return (Impl) this;
    }

    default Impl passwordBox(@NonNull String name, @NonNull String defaultText, @NonNull Consumer<TextBox> initializer)  {
        TextBox passwordBox = this.passwordBox(name).setText(defaultText);
        initializer.accept(passwordBox);
        return (Impl) this;
    }

    //
    //
    // misc. components
    //
    //

    //radio button group
    RadioButtonGroup radioGroup(@NonNull String name);

    default RadioButtonGroup radioButtonGroup(@NonNull String name)   {
        return this.radioGroup(name);
    }

    default Impl radioGroup(@NonNull String name, @NonNull Consumer<RadioButtonGroup> initializer)   {
        RadioButtonGroup radioButtonGroup = this.radioGroup(name);
        initializer.accept(radioButtonGroup);
        return (Impl) this;
    }

    default Impl radioButtonGroup(@NonNull String name, @NonNull Consumer<RadioButtonGroup> initializer)   {
        return this.radioGroup(name, initializer);
    }

    default Impl radioGroupFast(@NonNull String name)   {
        this.radioGroup(name);
        return (Impl) this;
    }
}
