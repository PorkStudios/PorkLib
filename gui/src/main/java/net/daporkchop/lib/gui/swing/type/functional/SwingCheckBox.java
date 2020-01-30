/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

import lombok.NonNull;
import net.daporkchop.lib.graphics.bitmap.PIcon;
import net.daporkchop.lib.gui.component.state.functional.CheckBoxState;
import net.daporkchop.lib.gui.component.type.functional.CheckBox;
import net.daporkchop.lib.gui.swing.GuiEngineSwing;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author DaPorkchop_
 */
public class SwingCheckBox extends AbstractSwingButton<CheckBox, JCheckBox, CheckBoxState> implements CheckBox {
    public SwingCheckBox(String name) {
        super(name, new JCheckBox(), CheckBoxState.class);

        this.swing.addChangeListener(new SwingCheckBoxChangeListener());
    }

    @Override
    public boolean isSelected() {
        return this.swing.isSelected();
    }

    @Override
    public CheckBox setSelected(boolean selected) {
        this.swing.setSelected(selected);
        return this;
    }

    @Override
    public PIcon getIcon(CheckBoxState state) {
        if (state == null) {
            state = CheckBoxState.ENABLED;
        } else if (state == CheckBoxState.DISABLED_HOVERED) {
            state = CheckBoxState.DISABLED;
        } else if (state == CheckBoxState.DISABLED_HOVERED_SELECTED)    {
            state = CheckBoxState.DISABLED_SELECTED;
        }
        return super.getIcon(state);
    }

    @Override
    public CheckBox setIcon(CheckBoxState state, PIcon icon) {
        if (state == null) {
            state = CheckBoxState.ENABLED;
        } else if (state == CheckBoxState.DISABLED_HOVERED) {
            state = CheckBoxState.DISABLED;
        } else if (state == CheckBoxState.DISABLED_HOVERED_SELECTED)    {
            state = CheckBoxState.DISABLED_SELECTED;
        }
        return super.setIcon(state, icon);
    }

    @Override
    protected CheckBox doSetIcon(@NonNull CheckBoxState state, Icon newIcon) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
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
        } else {
            SwingUtilities.invokeLater(() -> this.doSetIcon(state, newIcon));
        }
        return this;
    }

    protected class SwingCheckBoxChangeListener implements ChangeListener   {
        @Override
        public void stateChanged(ChangeEvent e) {
            SwingCheckBox.this.fireStateChange();
        }
    }
}
