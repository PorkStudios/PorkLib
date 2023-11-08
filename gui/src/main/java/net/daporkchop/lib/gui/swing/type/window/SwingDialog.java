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

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.gui.component.type.Window;
import net.daporkchop.lib.gui.swing.GuiEngineSwing;
import net.daporkchop.lib.gui.util.math.BoundingBox;

import javax.swing.*;
import java.awt.*;

/**
 * @author DaPorkchop_
 */
@Getter
public class SwingDialog extends AbstractSwingWindow<SwingDialog, JDialog> {
    protected final AbstractSwingWindow parentWindow;

    public SwingDialog(String name, @NonNull SwingFrame parentWindow) {
        super(name, new JDialog(parentWindow.getSwing()));

        this.parentWindow = parentWindow;

        this.swing.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
    }

    public SwingDialog(String name, @NonNull SwingDialog parentWindow) {
        super(name, new JDialog(parentWindow.getSwing()));

        this.parentWindow = parentWindow;
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
    public Window popup(@NonNull BoundingBox bounds) {
        return new SwingDialog("", this).setBounds(bounds);
    }
}
