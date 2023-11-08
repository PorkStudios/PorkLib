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

package net.daporkchop.lib.gui.swing.type.window;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.imaging.bitmap.PIcon;
import net.daporkchop.lib.gui.GuiEngine;
import net.daporkchop.lib.gui.component.Element;
import net.daporkchop.lib.gui.component.state.WindowState;
import net.daporkchop.lib.gui.component.type.Window;
import net.daporkchop.lib.gui.swing.GuiEngineSwing;
import net.daporkchop.lib.gui.swing.impl.SwingContainer;
import net.daporkchop.lib.gui.util.math.BoundingBox;

import javax.swing.SwingUtilities;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author DaPorkchop_
 */
@Getter
@Setter(AccessLevel.PROTECTED)
@Accessors(chain = true)
public abstract class AbstractSwingWindow<Impl extends AbstractSwingWindow<Impl, Swing>, Swing extends java.awt.Window> extends SwingContainer<Window, Swing, WindowState> implements Window {
    protected BoundingBox oldDimensions;
    protected PIcon       icon;

    protected boolean built = false;
    protected boolean minimized = false;
    protected boolean active = false;
    protected boolean closing = false;
    protected boolean closed = false;

    public AbstractSwingWindow(String name, Swing swing) {
        super(name, swing);

        this.swing.setLayout(null);
        this.swing.addWindowListener(new SwingWindowListener());
        this.swing.addComponentListener(new SwingWindowComponentListener());
    }

    @Override
    public WindowState getState() {
        if (!this.built) {
            return WindowState.CONSTRUCTION;
        } else if (this.closed) {
            return WindowState.RELEASED;
        } else if (this.closing) {
            return WindowState.CLOSED;
        } else if (this.isVisible()) {
            if (this.minimized) {
                return WindowState.VISIBLE_MINIMIZED;
            } else if (this.active) {
                return WindowState.VISIBLE;
            } else {
                return WindowState.VISIBLE_INACTIVE;
            }
        } else {
            return WindowState.HIDDEN;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Impl setIcon(@NonNull PIcon icon) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            if (icon != this.icon) {
                this.icon = icon;
                this.swing.setIconImage(icon.asBufferedImage());
            }
        } else {
            SwingUtilities.invokeLater(() -> this.setIcon(icon));
        }
        return (Impl) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Impl setIcon(@NonNull PIcon... icons) {
        if (icons.length == 0) {
            throw new IllegalArgumentException("Arguments may not be empty!");
        }
        int max = Integer.MIN_VALUE;
        PIcon maxI = null;
        for (int i = icons.length - 1; i >= 0; i--) {
            if (icons[i] == null) {
                throw new NullPointerException();
            }
            PIcon icon = icons[i];
            if (icon.width() == 0 || icon.height() == 0 || icon.width() != icon.height()) {
                throw new IllegalArgumentException("Icon must be square!");
            } else if (icon.width() > max) {
                max = icon.width();
                maxI = icon;
            }
        }
        this.icon = maxI;
        List<Image> images = Stream.of(icons).map(PIcon::asBufferedImage).collect(Collectors.toList());
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.swing.setIconImages(images);
        } else {
            SwingUtilities.invokeLater(() -> this.swing.setIconImages(images));
        }
        return (Impl) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Impl setBounds(@NonNull BoundingBox bounds) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            if (this.bounds == null || !this.bounds.equals(bounds)) {
                this.bounds = bounds;
                Insets insets = this.swing.getInsets();
                this.swing.setBounds(
                        bounds.getX(),
                        bounds.getY(),
                        bounds.getWidth() + insets.left + insets.right,
                        bounds.getHeight() + insets.top + insets.bottom
                );
                this.update();
            }
        } else {
            SwingUtilities.invokeLater(() -> this.setBounds(bounds));
        }
        return (Impl) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Impl update() {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            if (this.bounds == null) {
                this.bounds = new BoundingBox(0, 0, 128, 128);
            }
            if (this.oldDimensions == null || !this.oldDimensions.equals(this.bounds)) {
                this.oldDimensions = this.bounds;
            }
            this.children.values().forEach(Element::update);
            this.swing.revalidate();
        } else {
            SwingUtilities.invokeLater(this::update);
        }
        return (Impl) this;
    }

    @Override
    public boolean isMinDimensionsAreValueSize() {
        //TODO
        return false;
    }

    @Override
    public Window getWindow() {
        return this;
    }

    @Override
    public GuiEngine engine() {
        return GuiEngine.swing();
    }

    @Override
    public void release() {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.swing.dispose();
        } else {
            SwingUtilities.invokeLater(this::release);
        }
    }

    protected class SwingWindowListener implements WindowListener {
        @Override
        public void windowOpened(WindowEvent e) {
            AbstractSwingWindow.this.setBuilt(true).fireStateChange();
        }

        @Override
        public void windowClosing(WindowEvent e) {
            AbstractSwingWindow.this.setClosing(true).fireStateChange();
            AbstractSwingWindow.this.release();
        }

        @Override
        public void windowClosed(WindowEvent e) {
            AbstractSwingWindow.this.setClosed(true).fireStateChange();
        }

        @Override
        public void windowIconified(WindowEvent e) {
            AbstractSwingWindow.this.setMinimized(true).fireStateChange();
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
            AbstractSwingWindow.this.setMinimized(false).fireStateChange();
        }

        @Override
        public void windowActivated(WindowEvent e) {
            AbstractSwingWindow.this.setActive(true).fireStateChange();
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
            AbstractSwingWindow.this.setActive(false).fireStateChange();
        }
    }

    protected class SwingWindowComponentListener implements ComponentListener {
        @Override
        public void componentResized(ComponentEvent e) {
            this.componentMoved(e);
        }

        @Override
        public void componentMoved(ComponentEvent e) {
            java.awt.Window window = AbstractSwingWindow.this.swing;
            Insets insets = window.getInsets();
            //AbstractSwingWindow.this.bounds = AbstractSwingWindow.this.oldDimensions;
            AbstractSwingWindow.this.bounds = new BoundingBox(
                    window.getX(),
                    window.getY(),
                    window.getWidth() - insets.left - insets.right,
                    window.getHeight() - insets.top - insets.bottom
            );
            AbstractSwingWindow.this.update();
        }

        @Override
        public void componentShown(ComponentEvent e) {
            AbstractSwingWindow.this.fireStateChange();
        }

        @Override
        public void componentHidden(ComponentEvent e) {
            AbstractSwingWindow.this.fireStateChange();
        }
    }
}
