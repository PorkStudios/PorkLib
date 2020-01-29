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

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.orientation.advanced.Axis;
import net.daporkchop.lib.gui.component.state.container.ScrollPaneState;
import net.daporkchop.lib.gui.component.type.container.ScrollPane;
import net.daporkchop.lib.gui.swing.GuiEngineSwing;
import net.daporkchop.lib.gui.swing.common.SwingMouseListener;
import net.daporkchop.lib.gui.swing.impl.SwingComponent;
import net.daporkchop.lib.gui.swing.impl.SwingNestedContainer;
import net.daporkchop.lib.gui.util.ScrollCondition;
import net.daporkchop.lib.gui.util.ScrollDir;
import net.daporkchop.lib.gui.util.math.BoundingBox;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;

/**
 * @author DaPorkchop_
 */
@Accessors(chain = true)
public class SwingScrollPane extends SwingNestedContainer<ScrollPane, JScrollPane, ScrollPaneState> implements ScrollPane {
    protected final HackyPanelWrapper panel = new HackyPanelWrapper();

    @NonNull
    @Getter
    @Setter
    protected ScrollSpeedCalculator scrollSpeed;

    public SwingScrollPane(String name) {
        super(name, new JScrollPane());
        this.swing.setViewportView(this.panel);
        this.swing.getHorizontalScrollBar().setMinimum(0);
        this.swing.getVerticalScrollBar().setMinimum(0);

        this.swing.addMouseListener(new SwingMouseListener<>(this));
    }

    @Override
    public ScrollPane addChild(@NonNull Component child, boolean update) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            if (!(child instanceof SwingComponent)) {
                throw new IllegalArgumentException(String.format("Invalid child type! Expected %s but found %s!", SwingComponent.class.getCanonicalName(), child.getClass().getCanonicalName()));
            } else if (this.getChildren().containsKey(child.getName())) {
                throw new IllegalArgumentException(String.format("Child with name %s exists!", child.getName()));
            }
            SwingComponent swing = (SwingComponent) child;
            this.children.put(child.getName(), swing.setParent(this));
            if (swing.hasSwing()) {
                this.panel.add(swing.getSwing());
                return update ? this.update() : this;
            } else {
                return this;
            }
        } else {
            try {
                SwingUtilities.invokeAndWait(() -> this.addChild(child, update));
                return this;
            } catch (InvocationTargetException e) {
                throw e.getCause() instanceof RuntimeException ? (RuntimeException) e.getCause() : new RuntimeException(e.getCause());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public ScrollPane removeChild(@NonNull String name, boolean update) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            SwingComponent removed = (SwingComponent) this.getChildren().remove(name);
            if (removed == null) {
                throw new IllegalArgumentException(String.format("No such child: %s", name));
            } else {
                this.panel.remove(removed.getSwing());
                return update ? this.update() : this;
            }
        } else {
            try {
                SwingUtilities.invokeAndWait(() -> this.removeChild(name, update));
                return this;
            } catch (InvocationTargetException e) {
                throw e.getCause() instanceof RuntimeException ? (RuntimeException) e.getCause() : new RuntimeException(e.getCause());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public ScrollPane update() {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            super.update();
            if (this.children.isEmpty()) {
                this.swing.getHorizontalScrollBar().setMaximum(this.bounds.getWidth());
                this.swing.getVerticalScrollBar().setMaximum(this.bounds.getHeight());
            } else {
                int minX = Integer.MAX_VALUE;
                int maxX = Integer.MIN_VALUE;
                int minY = Integer.MAX_VALUE;
                int maxY = Integer.MIN_VALUE;
                for (Component component : this.children.values()) {
                    if (!((SwingComponent) component).hasSwing()) {
                        throw new IllegalStateException();
                    }
                    BoundingBox bb = component.getBounds();

                    int i = Axis.LEFT.getFrom(bb, component, null);
                    if (i < minX) {
                        minX = i;
                    }
                    i = Axis.RIGHT.getFrom(bb, component, null);
                    if (i > maxX) {
                        maxX = i;
                    }
                    i = Axis.ABOVE.getFrom(bb, component, null);
                    if (i < minY) {
                        minY = i;
                    }
                    i = Axis.BELOW.getFrom(bb, component, null);
                    if (i > maxY) {
                        maxY = i;
                    }
                }
                if (minX != 0 || minY != 0) {
                    for (Component component : this.children.values()) { //offset components to 0
                        BoundingBox bounds = component.getBounds();
                        ((SwingComponent) component).getSwing().setBounds(bounds.getX() - minX, bounds.getY() - minY, bounds.getWidth(), bounds.getHeight());
                    }
                }
                this.panel.setMinX(minX).setMaxX(maxX).setMinY(minY).setMaxY(maxY);
                this.swing.getHorizontalScrollBar().setMaximum(maxX - minX);
                this.swing.getVerticalScrollBar().setMaximum(maxY - minY);
            }
        } else {
            SwingUtilities.invokeLater(this::update);
        }
        return this;
    }

    @Override
    public ScrollPane setScrolling(@NonNull ScrollDir dir, @NonNull ScrollCondition condition) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
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
        } else {
            SwingUtilities.invokeLater(() -> this.setScrolling(dir, condition));
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
        throw new IllegalArgumentException();
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    protected class HackyPanelWrapper extends JPanel implements Scrollable {
        protected int minX;
        protected int maxX;
        protected int minY;
        protected int maxY;

        public HackyPanelWrapper() {
            super(null);
        }

        @Override
        public Dimension getPreferredSize() {
            return this.getPreferredScrollableViewportSize();
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return new Dimension(
                    this.maxX - this.minX,
                    this.maxY - this.minY
            );
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            int speed = -1;
            if (SwingScrollPane.this.scrollSpeed != null) {
                speed = SwingScrollPane.this.scrollSpeed.getScrollSpeed(direction == SwingConstants.HORIZONTAL ? ScrollDir.HORIZONTAL : ScrollDir.VERTICAL, SwingScrollPane.this.bounds);
            }
            return speed == -1 ? DEFAULT_SCROLL_SPEED : speed;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return this.getScrollableUnitIncrement(visibleRect, orientation, direction);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return false;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
