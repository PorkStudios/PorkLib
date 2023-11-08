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

import lombok.NonNull;
import net.daporkchop.lib.imaging.bitmap.PIcon;
import net.daporkchop.lib.gui.component.state.functional.LabelState;
import net.daporkchop.lib.gui.component.type.functional.Label;
import net.daporkchop.lib.gui.swing.GuiEngineSwing;
import net.daporkchop.lib.gui.swing.SwingTextAlignment;
import net.daporkchop.lib.gui.swing.common.SwingMouseListener;
import net.daporkchop.lib.gui.swing.impl.SwingComponent;
import net.daporkchop.lib.gui.util.HorizontalAlignment;
import net.daporkchop.lib.gui.util.VerticalAlignment;

import javax.swing.*;
import java.awt.*;

/**
 * @author DaPorkchop_
 */
public class SwingLabel extends SwingComponent<Label, JLabel, LabelState> implements Label {
    protected       PIcon   enabledIcon;
    protected       PIcon   disabledIcon;
    protected final boolean originallyOpaque;

    public SwingLabel(String name) {
        super(name, new JLabel());

        this.originallyOpaque = this.swing.isOpaque();

        this.swing.addMouseListener(new SwingMouseListener<>(this));
    }

    protected SwingLabel(String name, JLabel label) {
        super(name, label);

        this.originallyOpaque = this.swing.isOpaque();

        this.swing.addMouseListener(new SwingMouseListener<>(this));
    }

    @Override
    public String getText() {
        return this.swing.getText();
    }

    @Override
    public SwingLabel setText(String text) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            String currentText = this.getText();
            if (currentText != text && (currentText == null || !currentText.equals(text))) {
                this.swing.setText(text);
            }
        } else {
            SwingUtilities.invokeLater(() -> this.setText(text));
        }
        return this;
    }

    @Override
    public VerticalAlignment getTextVAlignment() {
        return SwingTextAlignment.fromSwingVertical(this.swing.getVerticalAlignment());
    }

    @Override
    public SwingLabel setTextVAlignment(@NonNull VerticalAlignment alignment) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.swing.setVerticalAlignment(SwingTextAlignment.toSwingVertical(alignment));
        } else {
            SwingUtilities.invokeLater(() -> this.setTextVAlignment(alignment));
        }
        return this;
    }

    @Override
    public HorizontalAlignment getTextHAlignment() {
        return SwingTextAlignment.fromSwingHorizontal(this.swing.getHorizontalAlignment());
    }

    @Override
    public SwingLabel setTextHAlignment(@NonNull HorizontalAlignment alignment) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.swing.setHorizontalAlignment(SwingTextAlignment.toSwingHorizontal(alignment));
        } else {
            SwingUtilities.invokeLater(() -> this.setTextHAlignment(alignment));
        }
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
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            if (state == null || state == LabelState.ENABLED) {
                if (this.enabledIcon != icon) {
                    this.enabledIcon = icon;
                    this.swing.setIcon(icon == null ? null : icon.asSwingIcon());
                }
            } else if (this.disabledIcon != icon) {
                this.disabledIcon = icon;
                this.swing.setDisabledIcon(icon == null ? null : icon.asSwingIcon());
            }
        } else {
            SwingUtilities.invokeLater(() -> this.setIcon(state, icon));
        }
        return this;
    }

    @Override
    public Label setColor(Color color) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            if (color == null)  {
                if (this.swing.isBackgroundSet())   {
                    this.swing.setBackground(null);
                    if (this.swing.isOpaque() != this.originallyOpaque)  {
                        this.swing.setOpaque(this.originallyOpaque);
                    }
                }
            } else {
                if (!this.swing.isBackgroundSet() || this.swing.getBackground().getRGB() != color.getRGB()) {
                    this.swing.setBackground(color);
                    if (!this.swing.isOpaque()) {
                        this.swing.setOpaque(true);
                    }
                }
            }
        } else {
            SwingUtilities.invokeLater(() -> this.setColor(color));
        }
        return this;
    }

    @Override
    public Label setColor(int argb) {
        return this.setColor(argb == 0 ? null : new Color(argb));
    }

    @Override
    public Label setTextColor(Color color) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            if (color == null)  {
                if (this.swing.isForegroundSet())   {
                    this.swing.setForeground(null);
                }
            } else {
                if (!this.swing.isForegroundSet() || this.swing.getForeground().getRGB() != color.getRGB()) {
                    this.swing.setForeground(color);
                }
            }
        } else {
            SwingUtilities.invokeLater(() -> this.setTextColor(color));
        }
        return this;
    }

    @Override
    public Label setTextColor(int argb) {
        return this.setTextColor(argb == 0 ? null : new Color(argb));
    }
}
