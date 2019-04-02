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

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private boolean minDimensionsAreValueSize;

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
        if (this.swing != null) {
            super.update();
            this.bounds = this.calculateBounds();
            this.minBounds = this.calculateBounds();
            this.swing.setBounds(this.bounds.getX(), this.bounds.getY(), this.bounds.getWidth(), this.bounds.getHeight());
        }
        return (Impl) this;
    }

    protected BoundingBox calculateBounds() {
        BoundingBox bounds = this.orientation == null ? new BoundingBox(0, 0, 0, 0) : this.orientation.update(this.parent.getBounds(), this.parent, (Impl) this);
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
    public BoundingBox computeMinBounds() {
        Dimension preferred = this.swing.getPreferredSize();
        if (preferred != null)  {
            BoundingBox bounds = this.orientation == null ? null : this.orientation.getMin(this.parent.getBounds(), this.parent, (Impl) this);
            if (bounds == null) {
                return this.bounds.set(Size.of(preferred.width, preferred.height));
            } else {
                return bounds.set(Size.of(Math.max(preferred.width, bounds.getWidth()), Math.max(preferred.height, bounds.getHeight())));
            }
        }
        return null;
    }

    @Override
    public String getTooltip() {
        return this.swing.getToolTipText();
    }

    @Override
    public Impl setTooltip(String tooltip) {
        if (this.swing != null && (this.getTooltip() == null || !this.getTooltip().equals(tooltip))) {
            this.swing.setToolTipText(tooltip);
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
        this.swing.setEnabled(enabled);
        this.fireStateChange();
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
