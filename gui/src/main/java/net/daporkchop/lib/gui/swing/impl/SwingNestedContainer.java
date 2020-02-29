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
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.Element;
import net.daporkchop.lib.gui.component.NestedContainer;
import net.daporkchop.lib.gui.component.orientation.advanced.Axis;
import net.daporkchop.lib.gui.component.state.ElementState;
import net.daporkchop.lib.gui.swing.GuiEngineSwing;
import net.daporkchop.lib.gui.util.math.BoundingBox;
import net.daporkchop.lib.gui.util.math.Size;

import javax.swing.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
@Getter
public abstract class SwingNestedContainer<Impl extends NestedContainer, Swing extends JComponent, State extends ElementState<? extends Element, State>> extends SwingComponent<Impl, Swing, State> implements NestedContainer<Impl, State>, IBasicSwingContainer<Impl, Swing, State> {
    protected final Map<String, Component> children = Collections.synchronizedMap(new HashMap<>());

    public SwingNestedContainer(String name, Swing swing) {
        super(name, swing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Impl update() {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            super.update();
            this.children.forEach((_name, element) -> element.update());
            if (this.minDimensionsAreValueSize) {
                int w = 0;
                int h = 0;
                for (Component component : this.children.values()) {
                    BoundingBox bb = component.getBounds();
                    int i = Axis.RIGHT.getFrom(bb, component, null);
                    if (i > w) {
                        w = i;
                    }
                    i = Axis.BELOW.getFrom(bb, component, null);
                    if (i > h) {
                        h = i;
                    }
                }
                if (w > this.bounds.getWidth() || h > this.bounds.getHeight()) {
                    this.bounds = this.bounds.set(Size.of(
                            Math.max(w, this.bounds.getWidth()),
                            Math.max(h, this.bounds.getHeight())
                    ));
                    this.swing.setSize(this.bounds.getWidth(), this.bounds.getHeight());
                }
            }
        } else {
            SwingUtilities.invokeLater(this::update);
        }
        return (Impl) this;
    }
}
