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

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.gui.component.SubElement;
import net.daporkchop.lib.gui.component.orientation.Orientation;
import net.daporkchop.lib.gui.util.math.BoundingBox;

import javax.swing.*;

/**
 * @author DaPorkchop_
 */
@Getter
@Setter
@Accessors(chain = true)
@SuppressWarnings("unchecked")
public abstract class SwingSubElement<Impl extends SubElement, Swing extends JComponent> extends SwingElement<Impl, Swing> implements SubElement<Impl> {
    protected Orientation<Impl> orientation;
    @NonNull
    protected SwingContainer parent;

    public SwingSubElement(String name, Swing swing) {
        super(name, swing);
        this.swing.setToolTipText("");
    }

    @Override
    public Impl setOrientation(@NonNull Orientation<Impl> orientation) {
        this.orientation = orientation;
        return this.considerUpdate();
    }

    @Override
    public Impl update() {
        this.bounds = this.orientation == null ? new BoundingBox(0, 0, 0, 0) : this.orientation.update(this.parent.getBounds(), this.parent, (Impl) this);
        return (Impl) this;
    }

    @Override
    public String getTooltip() {
        return this.swing.getToolTipText();
    }

    @Override
    public Impl setTooltip(String tooltip) {
        if (this.getTooltip() == null || !this.getTooltip().equals(tooltip)) {
            this.swing.setToolTipText(tooltip);
        }
        return (Impl) this;
    }

    @Override
    public void release() {
    }
}
