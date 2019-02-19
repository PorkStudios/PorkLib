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

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.gui.component.type.Button;
import net.daporkchop.lib.gui.swing.SwingTextAlignment;
import net.daporkchop.lib.gui.swing.impl.SwingComponent;
import net.daporkchop.lib.gui.util.HorizontalAlignment;
import net.daporkchop.lib.gui.util.VerticalAlignment;
import net.daporkchop.lib.gui.util.event.handler.ClickHandler;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author DaPorkchop_
 */
@Getter
@Setter
@Accessors(chain = true)
public class SwingButton extends SwingComponent<Button, JButton> implements Button {
    @NonNull
    protected ClickHandler clickHandler = (button, x, y) -> {
    };

    public SwingButton(String name) {
        super(name, new JButton());

        this.swing.addMouseListener(new SwingButtonMouseListener());
    }

    @Override
    public String getText() {
        return this.swing.getText();
    }

    @Override
    public SwingButton setText(@NonNull String text) {
        if (!this.getText().equals(text)) {
            this.swing.setText(text);
        }
        return this;
    }

    @Override
    public VerticalAlignment getVerticalTextAlignment() {
        return SwingTextAlignment.fromSwingVertical(this.swing.getVerticalAlignment());
    }

    @Override
    public SwingButton setVerticalTextAlignment(@NonNull VerticalAlignment alignment) {
        this.swing.setVerticalAlignment(SwingTextAlignment.toSwingVertical(alignment));
        return this;
    }

    @Override
    public HorizontalAlignment getHorizontalTextAlignment() {
        return SwingTextAlignment.fromSwingHorizontal(this.swing.getHorizontalAlignment());
    }

    @Override
    public SwingButton setHorizontalTextAlignment(@NonNull HorizontalAlignment alignment) {
        this.swing.setHorizontalAlignment(SwingTextAlignment.toSwingHorizontal(alignment));
        return this;
    }

    protected class SwingButtonMouseListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            SwingButton.this.clickHandler.onClick(e.getButton(), e.getX(), e.getY());
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }
}
