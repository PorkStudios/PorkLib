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

package net.daporkchop.lib.gui.swing.type.window;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.graphics.bitmap.icon.PIcon;
import net.daporkchop.lib.gui.component.state.WindowState;
import net.daporkchop.lib.gui.component.type.Window;
import net.daporkchop.lib.gui.swing.impl.SwingContainer;
import net.daporkchop.lib.gui.util.math.BoundingBox;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
    protected PIcon icon;

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
        if (!this.built)    {
            return WindowState.CONSTRUCTION;
        } else if (this.isVisible())    {
            if (this.minimized) {
                return WindowState.VISIBLE_MINIMIZED;
            } else if (this.active) {
                return WindowState.VISIBLE;
            } else {
                return WindowState.VISIBLE_INACTIVE;
            }
        } else if (this.closed) {
            return WindowState.CLOSED;
        } else if (this.closing)    {
            return WindowState.CLOSING;
        } else {
            return WindowState.HIDDEN;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Impl setIcon(@NonNull PIcon icon) {
        if (icon != this.icon)  {
            this.icon = icon;
            this.swing.setIconImage(icon.getAsBufferedImage());
        }
        return (Impl) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Impl setIcon(@NonNull PIcon... icons) {
        if (icons.length == 0)  {
            throw new IllegalArgumentException("Arguments may not be empty!");
        }
        int max = Integer.MIN_VALUE;
        PIcon maxI = null;
        for (int i = icons.length - 1; i >= 0; i--) {
            if (icons[i] == null)   {
                throw new NullPointerException();
            }
            PIcon icon = icons[i];
            if (icon.isEmpty() || icon.getWidth() != icon.getHeight())  {
                throw new IllegalArgumentException("Icon must be square!");
            } else if (icon.getWidth() > max)   {
                max = icon.getWidth();
                maxI = icon;
            }
        }
        this.icon = maxI;
        this.swing.setIconImages(Stream.of(icons).map(PIcon::getAsBufferedImage).collect(Collectors.toList()));
        return (Impl) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Impl setBounds(@NonNull BoundingBox bounds) {
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
        return (Impl) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Impl update() {
        if (this.bounds == null) {
            this.bounds = new BoundingBox(0, 0, 128, 128);
        }
        if (this.oldDimensions == null || !this.oldDimensions.equals(this.bounds)) {
            this.oldDimensions = this.bounds;
        }
        this.children.forEach((name, element) -> element.update());
        this.swing.revalidate();
        return (Impl) this;
    }

    @Override
    public void release() {
        this.swing.dispose();
    }

    protected class SwingWindowListener implements WindowListener  {
        @Override
        public void windowOpened(WindowEvent e) {
            AbstractSwingWindow.this.setBuilt(true).fireStateChange();
        }

        @Override
        public void windowClosing(WindowEvent e) {
            AbstractSwingWindow.this.setClosing(true).fireStateChange();
            AbstractSwingWindow.this.release(); //TODO: custom handling
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
            AbstractSwingWindow.this.bounds = AbstractSwingWindow.this.oldDimensions;
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
        }

        @Override
        public void componentHidden(ComponentEvent e) {
        }
    }
}
