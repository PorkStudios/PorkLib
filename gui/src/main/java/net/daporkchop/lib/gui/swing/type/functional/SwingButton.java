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
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.graphics.bitmap.icon.PIcon;
import net.daporkchop.lib.gui.component.state.functional.ButtonState;
import net.daporkchop.lib.gui.component.type.functional.Button;
import net.daporkchop.lib.gui.swing.common.SwingMouseListener;
import net.daporkchop.lib.gui.util.handler.ClickHandler;

import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 * @author DaPorkchop_
 */
@Getter
@Setter
@Accessors(chain = true)
public class SwingButton extends AbstractSwingButton<Button, JButton, ButtonState> implements Button {
    @NonNull
    protected ClickHandler clickHandler = (button, x, y) -> {
    };

    public SwingButton(String name) {
        super(name, new JButton(), ButtonState.class);

        this.swing.addMouseListener(new SwingButtonMouseListener(this));
    }

    protected static class SwingButtonMouseListener extends SwingMouseListener<SwingButton> {
        public SwingButtonMouseListener(SwingButton delegate) {
            super(delegate);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (this.delegate.isEnabled()) {
                this.delegate.clickHandler.onClick(e.getButton(), e.getX(), e.getY());
            }
            super.mouseClicked(e);
        }
    }

    @Override
    public PIcon getIcon(ButtonState state) {
        if (state == null) {
            state = ButtonState.ENABLED;
        } else if (state == ButtonState.DISABLED_HOVERED) {
            state = ButtonState.DISABLED;
        }
        return super.getIcon(state);
    }

    @Override
    public Button setIcon(ButtonState state, PIcon icon) {
        if (state == null) {
            state = ButtonState.ENABLED;
        } else if (state == ButtonState.DISABLED_HOVERED) {
            state = ButtonState.DISABLED;
        }
        return super.setIcon(state, icon);
    }

    @Override
    protected Button doSetIcon(@NonNull ButtonState state, Icon newIcon) {
        switch (state) {
            case ENABLED:
                this.swing.setIcon(newIcon);
                break;
            case ENABLED_HOVERED:
                this.swing.setRolloverIcon(newIcon);
                break;
            case ENABLED_CLICKED:
                this.swing.setPressedIcon(newIcon);
                break;
            case DISABLED:
                this.swing.setDisabledIcon(newIcon);
                break;
            default:
                throw new IllegalStateException(state.name());
        }
        return this;
    }
}
