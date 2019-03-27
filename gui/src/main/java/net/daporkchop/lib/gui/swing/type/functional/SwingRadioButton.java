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

package net.daporkchop.lib.gui.swing.type.functional;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.graphics.bitmap.icon.PIcon;
import net.daporkchop.lib.gui.component.state.functional.RadioButtonState;
import net.daporkchop.lib.gui.component.type.functional.CheckBox;
import net.daporkchop.lib.gui.component.type.functional.RadioButton;
import net.daporkchop.lib.gui.component.type.misc.RadioButtonGroup;
import net.daporkchop.lib.gui.swing.impl.SwingComponent;
import net.daporkchop.lib.gui.swing.type.misc.SwingRadioButtonGroup;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author DaPorkchop_
 */
public class SwingRadioButton extends AbstractSwingButton<RadioButton, JRadioButton, RadioButtonState> implements RadioButton {
    @Getter
    protected final SwingRadioButtonGroup group;
    
    public SwingRadioButton(String name, @NonNull SwingRadioButtonGroup group) {
        super(name, new JRadioButton(), RadioButtonState.class);
        
        this.group = group.add(this);

        this.swing.addChangeListener(new SwingRadioButtonChangeListener());
    }

    @Override
    public boolean isSelected() {
        return this.swing.isSelected();
    }

    @Override
    public RadioButton setSelected(boolean selected) {
        this.swing.setSelected(selected);
        return this;
    }

    @Override
    public PIcon getIcon(RadioButtonState state) {
        if (state == null) {
            state = RadioButtonState.ENABLED;
        } else if (state == RadioButtonState.DISABLED_HOVERED) {
            state = RadioButtonState.DISABLED;
        } else if (state == RadioButtonState.DISABLED_HOVERED_SELECTED)    {
            state = RadioButtonState.DISABLED_SELECTED;
        }
        return super.getIcon(state);
    }

    @Override
    public RadioButton setIcon(RadioButtonState state, PIcon icon) {
        if (state == null) {
            state = RadioButtonState.ENABLED;
        } else if (state == RadioButtonState.DISABLED_HOVERED) {
            state = RadioButtonState.DISABLED;
        } else if (state == RadioButtonState.DISABLED_HOVERED_SELECTED)    {
            state = RadioButtonState.DISABLED_SELECTED;
        }
        return super.setIcon(state, icon);
    }

    @Override
    protected RadioButton doSetIcon(@NonNull RadioButtonState state, Icon newIcon) {
        switch (state) {
            case ENABLED:
                this.swing.setIcon(newIcon);
                break;
            case ENABLED_HOVERED:
                this.swing.setRolloverIcon(newIcon);
                break;
            case ENABLED_SELECTED:
                this.swing.setSelectedIcon(newIcon);
                break;
            case ENABLED_HOVERED_SELECTED:
                this.swing.setRolloverSelectedIcon(newIcon);
                break;
            case DISABLED:
                this.swing.setDisabledIcon(newIcon);
                break;
            case DISABLED_SELECTED:
                this.swing.setDisabledSelectedIcon(newIcon);
                break;
            default:
                throw new IllegalStateException(state.name());
        }
        return this;
    }

    protected class SwingRadioButtonChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            SwingRadioButton.this.fireStateChange();
        }
    }
}