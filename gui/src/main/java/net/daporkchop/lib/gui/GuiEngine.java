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

package net.daporkchop.lib.gui;

import lombok.NonNull;
import net.daporkchop.lib.gui.component.capability.BlankComponentAdder;
import net.daporkchop.lib.gui.component.type.Window;
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
import net.daporkchop.lib.gui.swing.GuiEngineSwing;
import net.daporkchop.lib.gui.util.math.BoundingBox;

/**
 * A system for displaying Guis
 *
 * @author DaPorkchop_
 */
public interface GuiEngine extends BlankComponentAdder<GuiEngine> {
    static GuiEngineSwing swing() {
        return GuiEngineSwing.getInstance();
    }

    String getName();

    default Window newWindow(int width, int height) {
        return this.newWindow(new BoundingBox(0, 0, width, height));
    }

    default Window newWindow(int x, int y, int width, int height) {
        return this.newWindow(new BoundingBox(x, y, width, height));
    }

    /**
     * Creates a new window with the given bounds
     *
     * @param bounds the bounds of the new window to be created
     * @return the newly created window
     */
    Window newWindow(@NonNull BoundingBox bounds);

    BlankComponentAdder<? extends BlankComponentAdder> blankComponents();

    //component stuff

    @Override
    default Panel panel() {
        return this.blankComponents().panel();
    }

    @Override
    default ScrollPane scrollPane() {
        return this.blankComponents().scrollPane();
    }

    @Override
    default Button button() {
        return this.blankComponents().button();
    }

    @Override
    default CheckBox checkBox() {
        return this.blankComponents().checkBox();
    }

    @Override
    default <V> Dropdown<V> dropdown() {
        return this.blankComponents().dropdown();
    }

    @Override
    default Label label() {
        return this.blankComponents().label();
    }

    @Override
    default ProgressBar progressBar() {
        return this.blankComponents().progressBar();
    }

    @Override
    default RadioButton radioButton(@NonNull RadioButtonGroup group) {
        return this.blankComponents().radioButton(group);
    }

    @Override
    default Slider slider() {
        return this.blankComponents().slider();
    }

    @Override
    default Spinner spinner() {
        return this.blankComponents().spinner();
    }

    @Override
    default Table table() {
        return this.blankComponents().table();
    }

    @Override
    default TextBox textBox() {
        return this.blankComponents().textBox();
    }

    @Override
    default TextBox passwordBox() {
        return this.blankComponents().passwordBox();
    }

    @Override
    default RadioButtonGroup radioGroup() {
        return this.blankComponents().radioGroup();
    }
}
