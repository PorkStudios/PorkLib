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

package net.daporkchop.lib.gui.swing.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.Element;
import net.daporkchop.lib.gui.component.orientation.Orientation;
import net.daporkchop.lib.gui.component.state.ElementState;
import net.daporkchop.lib.gui.component.type.functional.Label;
import net.daporkchop.lib.gui.swing.GuiEngineSwing;
import net.daporkchop.lib.gui.swing.type.window.AbstractSwingWindow;
import net.daporkchop.lib.gui.util.Side;
import net.daporkchop.lib.gui.util.math.BoundingBox;
import net.daporkchop.lib.gui.util.math.Size;

import javax.swing.*;
import java.awt.*;
import java.util.StringJoiner;

/**
 * @author DaPorkchop_
 */
@Getter
@Setter
@Accessors(chain = true)
@SuppressWarnings("unchecked")
public abstract class SwingComponent<Impl extends Component, Swing extends JComponent, State extends ElementState<? extends Element, State>> extends SwingElement<Impl, Swing, State> implements Component<Impl, State> {
    protected Orientation<Impl> orientation;
    protected IBasicSwingContainer parent;
    @Setter(AccessLevel.PRIVATE)
    protected AbstractSwingWindow window;

    protected boolean hovered;
    protected boolean mouseDown;

    @Getter
    protected boolean minDimensionsAreValueSize;

    protected final int[] paddings = new int[4];

    public SwingComponent(String name, Swing swing) {
        super(name, swing);
    }

    public Impl setParent(@NonNull IBasicSwingContainer parent)   {
        if (this.parent != null)    {
            throw new IllegalStateException("Parent already set!");
        } else {
            this.parent = parent;
            while (parent.getParent() != null)  {
                parent = (IBasicSwingContainer) parent.getParent();
            }
            this.window = (AbstractSwingWindow) parent;
            return (Impl) this;
        }
    }

    @Override
    public Impl setOrientation(@NonNull Orientation<Impl> orientation) {
        this.orientation = orientation;
        return this.considerUpdate();
    }

    @Override
    public Impl update() {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD)  {
            if (this.swing != null) {
                super.update();
                this.bounds = this.calculateBounds();
                this.swing.setBounds(this.bounds.getX(), this.bounds.getY(), this.bounds.getWidth(), this.bounds.getHeight());
            }
        } else {
            SwingUtilities.invokeLater(this::update);
        }
        return (Impl) this;
    }

    protected BoundingBox calculateBounds() {
        BoundingBox bounds = this.bounds = this.orientation == null ? new BoundingBox(0, 0, 0, 0) : this.orientation.update(this.parent.getBounds(), this.parent, (Impl) this);
        if (this.swing != null && this.minDimensionsAreValueSize) {
            Dimension preferred = this.swing.getPreferredSize();
            if (preferred != null)  {
                bounds = new BoundingBox(bounds.getX(), bounds.getY(), Math.max(preferred.width, bounds.getWidth()), Math.max(preferred.height, bounds.getHeight()));
            }
        }
        return bounds;
    }

    @Override
    public Impl minDimensionsAreValueSize() {
        if (this.minDimensionsAreValueSize) {
            return (Impl) this;
        } else {
            this.minDimensionsAreValueSize = true;
            return this.considerUpdate();
        }
    }

    @Override
    public String getTooltip() {
        return this.swing.getToolTipText();
    }

    @Override
    public Impl setTooltip(String tooltip) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            if (this.swing != null) {
                String currentTooltip = this.getTooltip();
                if (currentTooltip != tooltip && (currentTooltip == null || !currentTooltip.equals(tooltip))) {
                    this.swing.setToolTipText(tooltip);
                }
            }
        } else {
            SwingUtilities.invokeLater(() -> this.setTooltip(tooltip));
        }
        return (Impl) this;
    }

    @Override
    public Impl setTooltip(String[] tooltip) {
        if (tooltip == null) {
            return this.setTooltip((String) null);
        } else {
            StringJoiner builder = new StringJoiner("<br>", "<html>", "</html>");
            for (String line : tooltip) {
                if (line.indexOf('\n') == -1) {
                    builder.add(line);
                } else {
                    for (String subLine : line.split("\n")) {
                        builder.add(subLine);
                    }
                }
            }
            return this.setTooltip(builder.toString());
        }
    }

    @Override
    public boolean isEnabled() {
        return this.swing.isEnabled();
    }

    @Override
    public Impl setEnable(boolean enabled) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.swing.setEnabled(enabled);
            this.fireStateChange();
        } else {
            SwingUtilities.invokeLater(() -> this.setEnable(enabled));
        }
        return (Impl) this;
    }

    @Override
    public void release() {
    }

    @Override
    public Impl setPadding(@NonNull Side side, int padding) {
        if (side.getDelegates().length == 1)    {
            this.paddings[side.getDelegates()[0].ordinal()] = padding;
        } else {
            for (Side delegate : side.getDelegates())    {
                this.paddings[delegate.ordinal()] = padding;
            }
        }
        return (Impl) this;
    }

    @Override
    public int getPadding(@NonNull Side side) {
        if (side.getDelegates().length == 1) {
            return this.paddings[side.getDelegates()[0].ordinal()];
        } else {
            int i = 0;
            for (Side delegate : side.getDelegates())    {
                i += this.paddings[delegate.ordinal()];
            }
            return i;
        }
    }

    public boolean hasSwing()   {
        return this.swing != null;
    }
}
