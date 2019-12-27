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

import lombok.NonNull;
import net.daporkchop.lib.gui.component.type.Window;
import net.daporkchop.lib.gui.swing.GuiEngineSwing;
import net.daporkchop.lib.gui.util.math.BoundingBox;

import javax.swing.*;

/**
 * @author DaPorkchop_
 */
public class SwingFrame extends AbstractSwingWindow<SwingFrame, JFrame> {
    public SwingFrame(String name) {
        super(name, new JFrame());
    }

    @Override
    public String getTitle() {
        return this.swing.getTitle();
    }

    @Override
    public AbstractSwingWindow setTitle(@NonNull String title) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            if (!title.equals(this.getTitle())) {
                this.swing.setTitle(title);
            }
        } else {
            SwingUtilities.invokeLater(() -> this.setTitle(title));
        }
        return this;
    }

    @Override
    public boolean isResizable() {
        return this.swing.isResizable();
    }

    @Override
    public AbstractSwingWindow setResizable(boolean resizable) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            if (this.isResizable() != resizable) {
                this.swing.setResizable(resizable);
            }
        } else {
            SwingUtilities.invokeLater(() -> this.setResizable(resizable));
        }
        return this;
    }

    @Override
    public Window getParentWindow() {
        return null;
    }

    @Override
    public Window popup(@NonNull BoundingBox bounds) {
        return new SwingDialog("", this).setBounds(bounds);
    }
}
