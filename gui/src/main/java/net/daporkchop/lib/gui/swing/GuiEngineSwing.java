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

package net.daporkchop.lib.gui.swing;

import lombok.NonNull;
import net.daporkchop.lib.common.pool.StaticPool;
import net.daporkchop.lib.gui.GuiEngine;
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
import net.daporkchop.lib.gui.swing.type.container.SwingPanel;
import net.daporkchop.lib.gui.swing.type.container.SwingScrollPane;
import net.daporkchop.lib.gui.swing.type.functional.SwingButton;
import net.daporkchop.lib.gui.swing.type.functional.SwingCheckBox;
import net.daporkchop.lib.gui.swing.type.functional.SwingDropdown;
import net.daporkchop.lib.gui.swing.type.functional.SwingLabel;
import net.daporkchop.lib.gui.swing.type.functional.SwingPasswordBox;
import net.daporkchop.lib.gui.swing.type.functional.SwingProgressBar;
import net.daporkchop.lib.gui.swing.type.functional.SwingRadioButton;
import net.daporkchop.lib.gui.swing.type.functional.SwingSlider;
import net.daporkchop.lib.gui.swing.type.functional.SwingSpinner;
import net.daporkchop.lib.gui.swing.type.functional.SwingTable;
import net.daporkchop.lib.gui.swing.type.misc.SwingRadioButtonGroup;
import net.daporkchop.lib.gui.swing.type.window.SwingFrame;
import net.daporkchop.lib.gui.util.math.BoundingBox;

import javax.swing.*;

/**
 * @author DaPorkchop_
 */
public class GuiEngineSwing implements GuiEngine {
    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException
                | InstantiationException
                | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    public static GuiEngineSwing getInstance() {
        return StaticPool.getInstance(GuiEngineSwing.class);
    }

    protected final BlankComponentAdder blankComponents = new SwingBlankComponentAdder();

    @Override
    public String getName() {
        return "Swing";
    }

    @Override
    public Window newWindow(@NonNull BoundingBox bounds) {
        return new SwingFrame("").setBounds(bounds);
    }

    @Override
    public BlankComponentAdder blankComponents() {
        return this.blankComponents;
    }

    protected static class SwingBlankComponentAdder implements BlankComponentAdder<SwingBlankComponentAdder>    {
        @Override
        public Panel panel() {
            return new SwingPanel("");
        }

        @Override
        public ScrollPane scrollPane() {
            return new SwingScrollPane("");
        }

        @Override
        public Button button() {
            return new SwingButton("");
        }

        @Override
        public CheckBox checkBox() {
            return new SwingCheckBox("");
        }

        @Override
        public <V> Dropdown<V> dropdown() {
            return new SwingDropdown<>("");
        }

        @Override
        public Label label() {
            return new SwingLabel("");
        }

        @Override
        public ProgressBar progressBar() {
            return new SwingProgressBar("");
        }

        @Override
        public RadioButton radioButton(@NonNull RadioButtonGroup group) {
            return new SwingRadioButton("", (SwingRadioButtonGroup) group);
        }

        @Override
        public Slider slider() {
            return new SwingSlider("");
        }

        @Override
        public Spinner spinner() {
            return new SwingSpinner("");
        }

        @Override
        public Table table() {
            return new SwingTable("");
        }

        @Override
        public TextBox textBox() {
            return null;
        }

        @Override
        public TextBox passwordBox() {
            return new SwingPasswordBox("");
        }

        @Override
        public RadioButtonGroup radioGroup() {
            return new SwingRadioButtonGroup("");
        }
    }
}
