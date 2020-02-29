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

package net.daporkchop.lib.gui.swing.common;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.gui.swing.impl.SwingComponent;
import net.daporkchop.lib.gui.swing.impl.SwingElement;
import net.daporkchop.lib.gui.swing.type.functional.SwingButton;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class SwingMouseListener<Impl extends SwingComponent> implements MouseListener {
    @NonNull
    protected final Impl delegate;

    @Override
    public void mouseClicked(MouseEvent e) {
        this.delegate.setMouseDown(false).fireStateChange();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.delegate.setMouseDown(true).fireStateChange();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.delegate.setMouseDown(false).fireStateChange();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        this.delegate.setHovered(true).fireStateChange();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.delegate.setHovered(false).fireStateChange();
    }
}
