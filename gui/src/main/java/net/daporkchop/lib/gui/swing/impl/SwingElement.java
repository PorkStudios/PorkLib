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

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.gui.component.Element;
import net.daporkchop.lib.gui.component.impl.AbstractElement;
import net.daporkchop.lib.gui.component.state.ElementState;
import net.daporkchop.lib.gui.swing.GuiEngineSwing;
import net.daporkchop.lib.gui.util.math.BoundingBox;
import net.daporkchop.lib.gui.util.math.Size;

import javax.swing.*;

/**
 * @author DaPorkchop_
 */
@Getter
public abstract class SwingElement<Impl extends Element, Swing extends java.awt.Component, State extends ElementState<? extends Element, State>> extends AbstractElement<Impl, State> {
    protected final Swing swing;

    public SwingElement(String name, Swing swing) {
        super(name);
        this.swing = swing;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Impl setVisible(boolean state) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            if (state != this.swing.isVisible()) {
                this.swing.setVisible(state);
                this.fireStateChange();
            }
        } else {
            SwingUtilities.invokeLater(() -> this.setVisible(state));
        }
        return (Impl) this;
    }

    @Override
    public boolean isVisible() {
        return this.swing.isVisible();
    }
}
