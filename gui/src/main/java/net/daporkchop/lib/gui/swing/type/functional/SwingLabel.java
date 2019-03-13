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

import lombok.NonNull;
import net.daporkchop.lib.graphics.bitmap.icon.PIcon;
import net.daporkchop.lib.gui.component.state.ElementState;
import net.daporkchop.lib.gui.component.state.functional.LabelState;
import net.daporkchop.lib.gui.component.type.functional.Label;
import net.daporkchop.lib.gui.swing.SwingTextAlignment;
import net.daporkchop.lib.gui.swing.impl.SwingComponent;
import net.daporkchop.lib.gui.util.HorizontalAlignment;
import net.daporkchop.lib.gui.util.VerticalAlignment;

import javax.swing.*;

/**
 * @author DaPorkchop_
 */
public class SwingLabel extends SwingComponent<Label, JLabel, LabelState> implements Label {
    protected PIcon enabledIcon;
    protected PIcon disabledIcon;

    public SwingLabel(String name) {
        super(name, new JLabel(), LabelState.ENABLED);
    }

    @Override
    public String getText() {
        return this.swing.getText();
    }

    @Override
    public SwingLabel setText(String text) {
        if (!this.getText().equals(text)) {
            this.swing.setText(text);
        }
        return this;
    }

    @Override
    public VerticalAlignment getTextVAlignment() {
        return SwingTextAlignment.fromSwingVertical(this.swing.getVerticalAlignment());
    }

    @Override
    public SwingLabel setTextVAlignment(@NonNull VerticalAlignment alignment) {
        this.swing.setVerticalAlignment(SwingTextAlignment.toSwingVertical(alignment));
        return this;
    }

    @Override
    public HorizontalAlignment getTextHAlignment() {
        return SwingTextAlignment.fromSwingHorizontal(this.swing.getHorizontalAlignment());
    }

    @Override
    public SwingLabel setTextHAlignment(@NonNull HorizontalAlignment alignment) {
        this.swing.setHorizontalAlignment(SwingTextAlignment.toSwingHorizontal(alignment));
        return this;
    }

    @Override
    public PIcon getIcon(LabelState state) {
        if (state == null || state == LabelState.ENABLED)   {
            return this.enabledIcon;
        } else {
            return this.disabledIcon;
        }
    }

    @Override
    public Label setIcon(LabelState state, PIcon icon) {
        if (state == null || state == LabelState.ENABLED)   {
            if (this.enabledIcon != icon)   {
                this.enabledIcon = icon;
                this.swing.setIcon(icon == null ? null : icon.getAsSwingIcon());
            }
        } else if (this.disabledIcon != icon) {
            this.disabledIcon = icon;
            this.swing.setDisabledIcon(icon == null ? null : icon.getAsSwingIcon());
        }
        return this;
    }
}
