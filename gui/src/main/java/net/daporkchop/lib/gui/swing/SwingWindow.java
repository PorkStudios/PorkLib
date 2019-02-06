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
import net.daporkchop.lib.gui.component.type.Window;
import net.daporkchop.lib.gui.impl.AbstractWindow;
import net.daporkchop.lib.gui.util.math.BoundingBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of {@link Window} for use with JavaX's Swing
 *
 * @author DaPorkchop_
 */
@Getterg
public class SwingWindow extends AbstractWindow<SwingComponent> implements SwingContainer<SwingWindow> {
    protected JFrame jFrame;
    protected BoundingBox oldDimensions = null;
    protected final Map<String, SwingComponent> componentMap = Collections.synchronizedMap(new HashMap<>());

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
    public SwingWindow setBounds(@NonNull BoundingBox bb) {
        if (this.bounds == null || !this.bounds.equals(bb)) {
            Insets insets = this.jFrame.getInsets();
            this.jFrame.setBounds(bb.getX(), bb.getY(), bb.getWidth() + insets.left + insets.right, bb.getHeight() + insets.top + insets.bottom);
            this.bounds = bb;
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
    public SwingWindow setResizable(boolean resizeable) {
        if (resizeable != this.resizable) {
            this.jFrame.setResizable(this.resizable = resizeable);
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
        this.componentMap.forEach((name, component) -> {
            component.update(this);
            BoundingBox updated = this.getBounds();
            JComponent swing = component.getSwing();
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
            SwingWindow.this.bounds = SwingWindow.this.oldDimensions;
            SwingWindow.this.bounds = new BoundingBox(
                    frame.getX(),
                    frame.getY(),
                    frame.getWidth() - insets.left - insets.right,
                    frame.getHeight() - insets.top - insets.bottom
            );
            SwingWindow.this.update();
        }
    }
}
