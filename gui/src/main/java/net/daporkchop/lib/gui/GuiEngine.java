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
