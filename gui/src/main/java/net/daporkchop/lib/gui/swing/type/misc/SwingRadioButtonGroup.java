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

package net.daporkchop.lib.gui.swing.type.misc;

import lombok.NonNull;
import net.daporkchop.lib.gui.component.state.misc.RadioButtonGroupState;
import net.daporkchop.lib.gui.component.type.functional.RadioButton;
import net.daporkchop.lib.gui.component.type.misc.RadioButtonGroup;
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
        SwingRadioButton old = this.buttons.put(button.getQualifiedName(), (SwingRadioButton) button);
        if (old != null)   {
            this.group.remove(old.getSwing());
        }
        this.group.add(((SwingRadioButton) button).getSwing());
        return this;
    }

    @Override
    public SwingRadioButtonGroup remove(@NonNull String qualifiedName) {
        SwingRadioButton old = this.buttons.remove(qualifiedName);
        if (old != null)    {
            this.group.remove(old.getSwing());
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
