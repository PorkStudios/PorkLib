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
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.Element;
import net.daporkchop.lib.gui.component.capability.IconHolder;
import net.daporkchop.lib.gui.component.capability.TextHolder;
import net.daporkchop.lib.gui.component.state.ElementState;
import net.daporkchop.lib.gui.component.state.functional.ButtonState;
import net.daporkchop.lib.gui.component.type.functional.Button;
import net.daporkchop.lib.gui.swing.GuiEngineSwing;
import net.daporkchop.lib.gui.swing.SwingTextAlignment;
import net.daporkchop.lib.gui.swing.impl.SwingComponent;
import net.daporkchop.lib.gui.swing.type.window.AbstractSwingWindow;
import net.daporkchop.lib.gui.util.HorizontalAlignment;
import net.daporkchop.lib.gui.util.VerticalAlignment;
import net.daporkchop.lib.gui.util.math.BoundingBox;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
public abstract class AbstractSwingButton<Impl extends Component<Impl, State> & IconHolder<Impl, State> & TextHolder<Impl>, Swing extends AbstractButton, State extends Enum<State> & ElementState<? extends Element, State>> extends SwingComponent<Impl, Swing, State> implements IconHolder<Impl, State>, TextHolder<Impl> {
    protected final Map<State, PIcon> icons;

    public AbstractSwingButton(String name, Swing swing, @NonNull Class<State> stateClass) {
        super(name, swing);

        this.icons = new EnumMap<>(stateClass);
    }

    @Override
    public String getText() {
        return this.swing.getText();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Impl setText(String text) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            if (!this.getText().equals(text)) {
                this.swing.setText(text);
            }
        } else {
            SwingUtilities.invokeLater(() -> this.setText(text));
        }
        return (Impl) this;
    }

    @Override
    public VerticalAlignment getTextVAlignment() {
        return SwingTextAlignment.fromSwingVertical(this.swing.getVerticalAlignment());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Impl setTextVAlignment(@NonNull VerticalAlignment alignment) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.swing.setVerticalAlignment(SwingTextAlignment.toSwingVertical(alignment));
        } else {
            SwingUtilities.invokeLater(() -> this.setTextVAlignment(alignment));
        }
        return (Impl) this;
    }

    @Override
    public HorizontalAlignment getTextHAlignment() {
        return SwingTextAlignment.fromSwingHorizontal(this.swing.getHorizontalAlignment());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Impl setTextHAlignment(@NonNull HorizontalAlignment alignment) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.swing.setHorizontalAlignment(SwingTextAlignment.toSwingHorizontal(alignment));
        } else {
            SwingUtilities.invokeLater(() -> this.setTextHAlignment(alignment));
        }
        return (Impl) this;
    }

    @Override
    public PIcon getIcon(@NonNull State state) {
        return this.icons.get(state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Impl setIcon(@NonNull State state, PIcon icon) {
        Icon newIcon;
        if (icon == null && this.icons.containsKey(state)) {
            this.icons.remove(state);
            newIcon = null;
        } else if (icon != null && this.icons.get(state) != icon) {
            this.icons.put(state, icon);
            newIcon = icon.getAsSwingIcon();
        } else {
            return (Impl) this;
        }
        return this.doSetIcon(state, newIcon);
    }

    protected abstract Impl doSetIcon(@NonNull State state, Icon newIcon);

    @Override
    @SuppressWarnings("unchecked")
    public Impl setTextColor(Color color) {
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
        return (Impl) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Impl setTextColor(int argb) {
        return this.setTextColor(argb == 0 ? null : new Color(argb));
    }
}
