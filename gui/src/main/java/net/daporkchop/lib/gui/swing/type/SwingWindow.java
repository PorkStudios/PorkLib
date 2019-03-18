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

package net.daporkchop.lib.gui.swing.type;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.graphics.bitmap.icon.PIcon;
import net.daporkchop.lib.gui.component.state.WindowState;
import net.daporkchop.lib.gui.component.type.Window;
import net.daporkchop.lib.gui.swing.impl.SwingContainer;
import net.daporkchop.lib.gui.util.event.EventManager;
import net.daporkchop.lib.gui.util.math.BoundingBox;

import javax.swing.*;
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
public class SwingWindow extends SwingContainer<Window, JFrame, WindowState> implements Window {
    protected final EventManager eventManager = new EventManager();

    protected BoundingBox oldDimensions;
    protected PIcon icon;

    protected boolean built = false;
    protected boolean minimized = false;
    protected boolean active = false;
    protected boolean closing = false;
    protected boolean closed = false;

    public SwingWindow(String name) {
        super(name, new JFrame());

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
    public String getTitle() {
        return this.swing.getTitle();
    }

    @Override
    public SwingWindow setTitle(@NonNull String title) {
        if (!title.equals(this.getTitle())) {
            this.swing.setTitle(title);
        }
        return this;
    }

    @Override
    public boolean isResizable() {
        return this.swing.isResizable();
    }

    @Override
    public SwingWindow setResizable(boolean resizable) {
        if (this.isResizable() != resizable)    {
            this.swing.setResizable(resizable);
        }
        return this;
    }

    @Override
    public Window setIcon(@NonNull PIcon icon) {
        if (icon != this.icon)  {
            this.icon = icon;
            this.swing.setIconImage(icon.getAsBufferedImage());
        }
        return this;
    }

    @Override
    public Window setIcon(@NonNull PIcon... icons) {
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
        return this;
    }

    @Override
    public SwingWindow setBounds(@NonNull BoundingBox bounds) {
        if (this.bounds == null || !this.bounds.equals(bounds)) {
            this.bounds = bounds;
            if (false) {
                //this.swing.setExtendedState(JFrame.MAXIMIZED_BOTH);
                this.swing.setUndecorated(true);
            }
            Insets insets = this.swing.getInsets();
            this.swing.setBounds(
                    bounds.getX(),
                    bounds.getY(),
                    bounds.getWidth() + insets.left + insets.right,
                    bounds.getHeight() + insets.top + insets.bottom
            );
            this.update();
        }
        return this;
    }

    @Override
    public SwingWindow update() {
        if (this.bounds == null) {
            this.bounds = new BoundingBox(0, 0, 128, 128);
        }
        if (this.oldDimensions == null || !this.oldDimensions.equals(this.bounds)) {
            this.oldDimensions = this.bounds;
        }
        this.children.forEach((name, element) -> element.update());
        this.swing.revalidate();
        return this;
    }

    @Override
    public void release() {
        this.swing.dispose();
    }

    protected class SwingWindowListener implements WindowListener  {
        @Override
        public void windowOpened(WindowEvent e) {
            SwingWindow.this.setBuilt(true).fireStateChange();
        }

        @Override
        public void windowClosing(WindowEvent e) {
            SwingWindow.this.setClosing(true).fireStateChange();
            SwingWindow.this.release(); //TODO: custom handling
        }

        @Override
        public void windowClosed(WindowEvent e) {
            SwingWindow.this.setClosed(true).fireStateChange();
        }

        @Override
        public void windowIconified(WindowEvent e) {
            SwingWindow.this.setMinimized(true).fireStateChange();
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
            SwingWindow.this.setMinimized(false).fireStateChange();
        }

        @Override
        public void windowActivated(WindowEvent e) {
            SwingWindow.this.setActive(true).fireStateChange();
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
            SwingWindow.this.setActive(false).fireStateChange();
        }
    }

    protected class SwingWindowComponentListener implements ComponentListener {
        @Override
        public void componentResized(ComponentEvent e) {
            this.componentMoved(e);
        }

        @Override
        public void componentMoved(ComponentEvent e) {
            JFrame frame = SwingWindow.this.swing;
            Insets insets = frame.getInsets();
            SwingWindow.this.bounds = SwingWindow.this.oldDimensions;
            SwingWindow.this.bounds = new BoundingBox(
                    frame.getX(),
                    frame.getY(),
                    frame.getWidth() - insets.left - insets.right,
                    frame.getHeight() - insets.top - insets.bottom
            );
            SwingWindow.this.update();
        }

        @Override
        public void componentShown(ComponentEvent e) {
        }

        @Override
        public void componentHidden(ComponentEvent e) {
        }
    }
}
