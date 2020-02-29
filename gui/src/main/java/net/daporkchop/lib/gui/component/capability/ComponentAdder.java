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

package net.daporkchop.lib.gui.component.capability;

import lombok.NonNull;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.component.type.container.Panel;
import net.daporkchop.lib.gui.component.type.container.ScrollPane;
import net.daporkchop.lib.gui.component.type.functional.Button;
import net.daporkchop.lib.gui.component.type.functional.CheckBox;
import net.daporkchop.lib.gui.component.type.functional.Dropdown;
import net.daporkchop.lib.gui.component.type.functional.Label;
import net.daporkchop.lib.gui.component.type.functional.ProgressBar;
import net.daporkchop.lib.gui.component.type.functional.RadioButton;
import net.daporkchop.lib.gui.component.type.functional.Slider;
import net.daporkchop.lib.gui.component.type.functional.Spinner;
import net.daporkchop.lib.gui.component.type.functional.Table;
import net.daporkchop.lib.gui.component.type.functional.TextBox;
import net.daporkchop.lib.gui.component.type.misc.RadioButtonGroup;
import net.daporkchop.lib.gui.form.PForm;

import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
@SuppressWarnings("unchecked")
public interface ComponentAdder<Impl> extends BlankComponentAdder<Impl> {
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

    //slider
    Slider slider(@NonNull String name);

    default Slider slider(@NonNull String name, int val, int min, int max)  {
        return this.slider(name).setValAndLimits(val, min, max);
    }

    default Impl slider(@NonNull String name, @NonNull Consumer<Slider> initializer)    {
        Slider slider = this.slider(name);
        initializer.accept(slider);
        return (Impl) this;
    }

    default Impl slider(@NonNull String name, int val, int min, int max, @NonNull Consumer<Slider> initializer)    {
        Slider slider = this.slider(name).setValAndLimits(val, min, max);
        initializer.accept(slider);
        return (Impl) this;
    }

    //spinner
    Spinner spinner(@NonNull String name);

    default Spinner spinner(@NonNull String name, int val, int min, int max, int step)  {
        return this.spinner(name).setValAndLimits(val, min, max).setStep(step);
    }

    default Spinner spinner(@NonNull String name, double val, double min, double max, double step)  {
        return this.spinner(name).setValAndLimitsD(val, min, max).setStepD(step);
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

    default Impl spinner(@NonNull String name, double val, double min, double max, double step, @NonNull Consumer<Spinner> initializer)  {
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

    //table
    Table table(@NonNull String name);

    default Impl table(@NonNull String name, @NonNull Consumer<Table> initializer)  {
        Table table = this.table(name);
        initializer.accept(table);
        return (Impl) this;
    }

    //
    //
    // misc. components
    //
    //

    //radio button group
    RadioButtonGroup radioGroup(@NonNull String name);

    @Override
    default RadioButtonGroup radioGroup() {
        throw new UnsupportedOperationException(); //TODO: make radio button groups not be actual components
    }

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

    //
    //
    // form stuff
    //
    //
    default <T> PForm<T> form(@NonNull Class<T> clazz)  {
        PForm<T> form = new PForm<>(clazz, (Container) this);
        form.buildDefault().prepare();
        return form;
    }

    default <T> Impl form(@NonNull Class<T> clazz, @NonNull Consumer<PForm<T>> initializer)  {
        PForm<T> form = new PForm<>(clazz, (Container) this);
        initializer.accept(form);
        form.buildDefault().prepare();
        return (Impl) this;
    }

    //
    //
    // stuff from blankcomponentadder shouldn't really be here, but i added it anyway
    // and just deprecated it all because reasons
    //
    //
    @Override
    @Deprecated
    default Panel panel() {
        return this.panel("");
    }

    @Override
    @Deprecated
    default ScrollPane scrollPane() {
        return this.scrollPane("");
    }

    @Override
    @Deprecated
    default Button button() {
        return this.button("");
    }

    @Override
    default CheckBox checkBox() {
        return this.checkBox("");
    }

    @Override
    @Deprecated
    default <V> Dropdown<V> dropdown() {
        return this.dropdown("");
    }

    @Override
    @Deprecated
    default Label label() {
        return this.label("");
    }

    @Override
    @Deprecated
    default ProgressBar progressBar() {
        return this.progressBar("");
    }

    @Override
    @Deprecated
    default RadioButton radioButton(@NonNull RadioButtonGroup group) {
        return this.radioButton("", group);
    }

    @Override
    @Deprecated
    default Slider slider() {
        return this.slider("");
    }

    @Override
    @Deprecated
    default Spinner spinner() {
        return this.spinner("");
    }

    @Override
    @Deprecated
    default Table table() {
        return null; //TODO
    }

    @Override
    @Deprecated
    default TextBox textBox() {
        return this.textBox("");
    }

    @Override
    @Deprecated
    default TextBox passwordBox() {
        return this.passwordBox("");
    }
}
