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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.gui.Window;
import net.daporkchop.lib.gui.util.Dimensions;

import javax.swing.*;
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

    protected SwingWindow(@NonNull GuiSystemSwing system, @NonNull JFrame jFrame)    {
        super(system);
        this.jFrame = jFrame;

        this.jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.jFrame.addWindowListener(new SwingWindowListener());
        this.jFrame.setResizable(true);
    }

    @Override
    public SwingWindow setDimensions(@NonNull Dimensions dimensions) {
        if (this.dimensions == null || !this.dimensions.equals(dimensions)) {
            this.jFrame.setBounds(dimensions.getX(), dimensions.getY(), dimensions.getWidth(), dimensions.getHeight());
            this.dimensions = dimensions;
        }
        return this.update();
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
        if (visible != this.visible)    {
            this.jFrame.setVisible(this.visible = visible);
        }
        return this;
    }

    @Override
    public SwingWindow setResizeable(boolean resizeable) {
        if (resizeable != this.resizeable)    {
            this.jFrame.setResizable(this.resizeable = resizeable);
        }
        return this;
    }

    @Override
    public SwingWindow update() {
        if (this.dimensions == null)    {
            this.dimensions = new Dimensions(0, 0, 128, 128);
        }
        if (this.oldDimensions == null || !this.oldDimensions.equals(this.dimensions))  {
            this.oldDimensions = this.dimensions;
        }
        this.componentMap.forEach((name, component) -> component.update(this.dimensions));
        return this;
    }

    @Override
    public void dispose() {
        this.jFrame.dispose();
        this.jFrame = null;
    }

    protected class SwingWindowListener extends WindowAdapter   {
        @Override
        public void windowClosing(WindowEvent e) {
            SwingWindow.this.closeHandler.run();
        }
    }
}
