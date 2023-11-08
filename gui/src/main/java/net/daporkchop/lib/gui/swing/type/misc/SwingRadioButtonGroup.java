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

package net.daporkchop.lib.gui.swing.type.misc;

import lombok.NonNull;
import net.daporkchop.lib.gui.component.state.misc.RadioButtonGroupState;
import net.daporkchop.lib.gui.component.type.functional.RadioButton;
import net.daporkchop.lib.gui.component.type.misc.RadioButtonGroup;
import net.daporkchop.lib.gui.swing.GuiEngineSwing;
import net.daporkchop.lib.gui.swing.impl.SwingComponent;
import net.daporkchop.lib.gui.swing.type.functional.SwingRadioButton;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
public class SwingRadioButtonGroup extends SwingComponent<RadioButtonGroup, JComponent, RadioButtonGroupState> implements RadioButtonGroup {
    protected final Map<String, SwingRadioButton> buttons = new HashMap<>();
    protected final ButtonGroup group = new ButtonGroup();

    public SwingRadioButtonGroup(String name) {
        super(name, null);
    }

    @Override
    public RadioButton getSelected() {
        ButtonModel curr = this.group.getSelection();
        return this.buttons.values().stream()
                .filter(b -> b.getSwing().getModel() == curr)
                .findAny().orElse(null);
    }

    @Override
    public Collection<RadioButton> getChildren() {
        return new ArrayList<>(this.buttons.values());
    }

    @Override
    public SwingRadioButtonGroup add(@NonNull RadioButton button) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            SwingRadioButton old = this.buttons.put(button.getQualifiedName(), (SwingRadioButton) button);
            if (old != null) {
                this.group.remove(old.getSwing());
            }
            this.group.add(((SwingRadioButton) button).getSwing());
        } else {
            SwingUtilities.invokeLater(() -> this.add(button));
        }
        return this;
    }

    @Override
    public SwingRadioButtonGroup remove(@NonNull String qualifiedName) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            SwingRadioButton old = this.buttons.remove(qualifiedName);
            if (old != null) {
                this.group.remove(old.getSwing());
            }
        } else {
            SwingUtilities.invokeLater(() -> this.remove(qualifiedName));
        }
        return this;
    }

    @Override
    public String getTooltip() {
        return "";
    }

    @Override
    public RadioButtonGroup setTooltip(String tooltip) {
        return this;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public RadioButtonGroup setEnable(boolean enabled) {
        return this;
    }

    @Override
    public RadioButtonGroup setVisible(boolean state) {
        return this;
    }

    @Override
    public boolean isVisible() {
        return false;
    }
}
