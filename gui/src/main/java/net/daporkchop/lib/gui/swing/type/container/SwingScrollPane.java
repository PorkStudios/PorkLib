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

package net.daporkchop.lib.gui.swing.type.container;

import lombok.NonNull;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.state.container.ScrollPaneState;
import net.daporkchop.lib.gui.component.type.container.ScrollPane;
import net.daporkchop.lib.gui.swing.common.SwingMouseListener;
import net.daporkchop.lib.gui.swing.impl.SwingComponent;
import net.daporkchop.lib.gui.swing.impl.SwingNestedContainer;
import net.daporkchop.lib.gui.util.ScrollCondition;
import net.daporkchop.lib.gui.util.ScrollDir;
import net.daporkchop.lib.gui.util.math.BoundingBox;

import javax.swing.*;
import java.awt.*;

/**
 * @author DaPorkchop_
 */
public class SwingScrollPane extends SwingNestedContainer<ScrollPane, JScrollPane, ScrollPaneState> implements ScrollPane {
    protected final JPanel panel = new JPanel();

    public SwingScrollPane(String name) {
        super(name, new JScrollPane());

        this.swing.setLayout(null);
        this.panel.setLayout(null);
        this.swing.setViewportView(this.panel);

        this.swing.addMouseListener(new SwingMouseListener<>(this));
    }

    @Override
    public ScrollPane addChild(@NonNull Component child, boolean update) {
        if (!(child instanceof SwingComponent))    {
            throw new IllegalArgumentException(String.format("Invalid child type! Expected %s but found %s!", SwingComponent.class.getCanonicalName(), child.getClass().getCanonicalName()));
        } else if (this.getChildren().containsKey(child.getName()))  {
            throw new IllegalArgumentException(String.format("Child with name %s exists!", child.getName()));
        }
        SwingComponent swing = (SwingComponent) child;
        this.children.put(child.getName(), swing.setParent(this));
        this.panel.add(swing.getSwing());
        return update ? this.update() : this;
    }

    @Override
    public ScrollPane removeChild(@NonNull String name, boolean update) {
        SwingComponent removed = (SwingComponent) this.getChildren().remove(name);
        if (removed == null) {
            throw new IllegalArgumentException(String.format("No such child: %s", name));
        } else {
            this.panel.remove(removed.getSwing());
            return update ? this.update() : this;
        }
    }

    @Override
    public ScrollPane update() {
        super.update();
        this.swing.setPreferredSize(new Dimension(this.bounds.getWidth(), this.bounds.getHeight()));
        if (true) {
            return this;
        }
        if (this.children.isEmpty()) {
            this.swing.getHorizontalScrollBar().setMinimum(0);
            this.swing.getHorizontalScrollBar().setMaximum(this.bounds.getWidth());
            this.swing.getVerticalScrollBar().setMinimum(0);
            this.swing.getVerticalScrollBar().setMaximum(this.bounds.getHeight());
        } else {
            int minX = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxY = Integer.MIN_VALUE;
            for (Component component : this.children.values()) {
                BoundingBox bounds = component.getBounds();
                int x = bounds.getX();
                int y = bounds.getY();
                if (x < minX) {
                    minX = x;
                }
                if (y < minY) {
                    minY = y;
                }
                x += bounds.getWidth();
                y += bounds.getHeight();
                if (x > maxX) {
                    maxX = x;
                }
                if (y > maxY) {
                    maxY = y;
                }
            }
            this.swing.getHorizontalScrollBar().setMinimum(minX);
            this.swing.getHorizontalScrollBar().setMaximum(maxX);
            this.swing.getVerticalScrollBar().setMinimum(minY);
            this.swing.getVerticalScrollBar().setMaximum(maxY);
        }
        return this;
    }

    @Override
    public ScrollPane setScrolling(@NonNull ScrollDir dir, @NonNull ScrollCondition condition) {
        if (dir == ScrollDir.HORIZONTAL) {
            switch (condition) {
                case ALWAYS:
                    this.swing.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
                    break;
                case NEVER:
                    this.swing.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                    break;
                case AUTO:
                    this.swing.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                    break;
            }
        } else {
            switch (condition) {
                case ALWAYS:
                    this.swing.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                    break;
                case NEVER:
                    this.swing.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
                    break;
                case AUTO:
                    this.swing.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                    break;
            }
        }
        return this;
    }

    @Override
    public ScrollCondition getScrolling(@NonNull ScrollDir dir) {
        if (dir == ScrollDir.HORIZONTAL) {
            switch (this.swing.getHorizontalScrollBarPolicy()) {
                case ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS:
                    return ScrollCondition.ALWAYS;
                case ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER:
                    return ScrollCondition.NEVER;
                case ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED:
                    return ScrollCondition.AUTO;
            }
        } else {
            switch (this.swing.getVerticalScrollBarPolicy()) {
                case ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS:
                    return ScrollCondition.ALWAYS;
                case ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER:
                    return ScrollCondition.NEVER;
                case ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED:
                    return ScrollCondition.AUTO;
            }
        }
        throw new IllegalStateException();
    }
}
