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

package net.daporkchop.lib.gui.swing.type.functional;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.imaging.bitmap.PIcon;
import net.daporkchop.lib.gui.component.state.functional.ButtonState;
import net.daporkchop.lib.gui.component.type.functional.Button;
import net.daporkchop.lib.gui.swing.GuiEngineSwing;
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
    protected ClickHandler clickHandler = null;

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
            if (this.delegate.isEnabled() && this.delegate.clickHandler != null) {
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
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
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
        } else {
            SwingUtilities.invokeLater(() -> this.doSetIcon(state, newIcon));
        }
        return this;
    }
}
