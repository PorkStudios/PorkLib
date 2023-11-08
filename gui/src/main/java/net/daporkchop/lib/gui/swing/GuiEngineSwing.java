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

package net.daporkchop.lib.gui.swing;

import lombok.NonNull;
import net.daporkchop.lib.common.misc.InstancePool;
import net.daporkchop.lib.common.util.PorkUtil;
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
import net.daporkchop.lib.gui.swing.type.functional.table.SwingTable;
import net.daporkchop.lib.gui.swing.type.misc.SwingRadioButtonGroup;
import net.daporkchop.lib.gui.swing.type.window.SwingFrame;
import net.daporkchop.lib.gui.util.math.BoundingBox;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * An implementation of {@link GuiEngine} backed by Java Swing.
 * <p>
 * Be aware that due to limitations of Swing itself, many methods that affect the state of the GUI will be queued to be executed asynchronously
 * rather than immediately, meaning that the changes will not be immediately visible to accessors. This can cause bugs that are very difficult
 * to track down, and should therefore only be used for simple GUIs. Use at your own risk.
 *
 * @author DaPorkchop_
 */
//TODO: make a custom GUI engine from scratch i guess
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

    public static final Class<?> EVENT_DISPATCH_THREAD = PorkUtil.classForName("java.awt.EventDispatchThread");

    public static GuiEngineSwing getInstance() {
        return InstancePool.getInstance(GuiEngineSwing.class);
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

    protected static class SwingBlankComponentAdder implements BlankComponentAdder<SwingBlankComponentAdder> {
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
