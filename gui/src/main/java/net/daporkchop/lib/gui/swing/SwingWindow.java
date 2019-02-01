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

package net.daporkchop.lib.gui.swing;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.gui.component.Window;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.swing.component.SwingComponent;
import net.daporkchop.lib.gui.util.Dimensions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * An implementation of {@link Window} for use with JavaX's Swing
 *
 * @author DaPorkchop_
 */
@Getter
public class SwingWindow extends Window {
    protected JFrame jFrame;
    protected Dimensions oldDimensions = null;

    protected SwingWindow(@NonNull GuiSystemSwing system, @NonNull JFrame jFrame) {
        super(system);
        this.jFrame = jFrame;

        this.jFrame.setLayout(null);
        this.jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.jFrame.addWindowListener(new SwingWindowListener());
        this.jFrame.addComponentListener(new SwingComponentListener());
        this.jFrame.setResizable(true);
    }

    @Override
    public SwingWindow setDimensions(@NonNull Dimensions dimensions) {
        if (this.dimensions == null || !this.dimensions.equals(dimensions)) {
            Insets insets = this.jFrame.getInsets();
            this.jFrame.setBounds(dimensions.getX(), dimensions.getY(), dimensions.getWidth() + insets.left + insets.right, dimensions.getHeight() + insets.top + insets.bottom);
            this.dimensions = dimensions;
        }
        return this.visible ? this.update() : this;
    }

    @Override
    public SwingWindow setTitle(@NonNull String title) {
        if (!this.title.equals(title)) {
            this.jFrame.setTitle(title);
            this.title = title;
        }
        return this;
    }

    @Override
    public SwingWindow setVisible(boolean visible) {
        if (visible != this.visible) {
            if (visible) {
                this.update();
            }
            this.jFrame.setVisible(this.visible = visible);
        }
        return this;
    }

    @Override
    public SwingWindow setResizeable(boolean resizeable) {
        if (resizeable != this.resizeable) {
            this.jFrame.setResizable(this.resizeable = resizeable);
        }
        return this;
    }

    @Override
    public SwingWindow update() {
        if (this.dimensions == null) {
            this.dimensions = new Dimensions(0, 0, 128, 128);
        }
        if (this.oldDimensions == null || !this.oldDimensions.equals(this.dimensions)) {
            this.oldDimensions = this.dimensions;
        }
        this.componentMap.forEach((name, component) -> {
            component.update(this.dimensions);
            Dimensions updated = component.getCurrentDimensions();
            JComponent swing = ((SwingComponent) component).getSwing();
            swing.setBounds(updated.getX(), updated.getY(), updated.getWidth(), updated.getHeight());
        });
        this.jFrame.revalidate();
        return this;
    }

    @Override
    public void dispose() {
        this.jFrame.dispose();
        this.jFrame = null;
    }

    @Override
    public Window addComponent(String name, @NonNull Component component, boolean update) {
        if (!(component instanceof SwingComponent)) {
            throw new IllegalStateException(String.format("Invalid component type: %s", component.getClass().getCanonicalName()));
        }
        this.jFrame.add(((SwingComponent) component).getSwing());
        return super.addComponent(name, component, update);
    }

    protected class SwingWindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            SwingWindow.this.closeHandler.run();
        }
    }

    protected class SwingComponentListener extends ComponentAdapter {
        @Override
        public void componentResized(ComponentEvent e) {
            this.componentMoved(e);
        }

        @Override
        public void componentMoved(ComponentEvent e) {
            JFrame frame = SwingWindow.this.jFrame;
            Insets insets = frame.getInsets();
            SwingWindow.this.dimensions = SwingWindow.this.oldDimensions;
            SwingWindow.this.dimensions = new Dimensions(
                    frame.getX(),
                    frame.getY(),
                    frame.getWidth() - insets.left - insets.right,
                    frame.getHeight() - insets.top - insets.bottom
            );
            SwingWindow.this.update();
        }
    }
}
