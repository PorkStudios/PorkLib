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

package net.daporkchop.lib.gui.swing.component;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.function.VoidFunction;
import net.daporkchop.lib.gui.component.type.Button;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author DaPorkchop_
 */
@Getter
@Setter
@Accessors(chain = true)
public class SwingButton extends SwingComponent<SwingButton, JButton> implements Button<SwingButton> {
    @NonNull
    protected VoidFunction clickHandler;

    public SwingButton(@NonNull String name) {
        super(name, new JButton());
        this.swing.addMouseListener(new SwingButtonMouseHandler());
        this.swing.setText("");
        this.swing.setToolTipText("");
    }

    @Override
    public SwingButton setText(String text) {
        super.setText(text);
        this.swing.setText(text);
        return this;
    }

    @Override
    public SwingButton setTooltip(String tooltip) {
        super.setTooltip(tooltip);
        this.swing.setToolTipText(tooltip);
        return this;
    }

    protected class SwingButtonMouseHandler extends MouseAdapter    {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (SwingButton.this.clickHandler == null)  {
                throw new IllegalStateException("Click handler not set!");
            } else {
                SwingButton.this.clickHandler.run();
            }
        }
    }
}
