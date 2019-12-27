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

package net.daporkchop.lib.gui.swing.type.functional;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.gui.component.state.functional.TextBoxState;
import net.daporkchop.lib.gui.component.type.functional.TextBox;
import net.daporkchop.lib.gui.swing.GuiEngineSwing;
import net.daporkchop.lib.gui.swing.common.SwingMouseListener;
import net.daporkchop.lib.gui.swing.impl.SwingComponent;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
@Accessors(chain = true)
public class SwingTextBox extends SwingComponent<TextBox, JTextField, TextBoxState> implements TextBox {
    protected String text = "";
    protected final Map<String, Consumer<String>> handlers = new HashMap<>();

    public SwingTextBox(String name) {
        this(name, new SwingTextBoxImpl());
    }

    protected SwingTextBox(String name, @NonNull JTextField jTextField) {
        super(name, jTextField);

        if (this.getClass() == SwingTextBox.class)  {
            //this.swing.setUI(new SwingTextBoxUI());
        }
        this.swing.setText("");
        this.swing.getDocument().addDocumentListener(new SwingTextBoxDocumentListener());
        this.swing.addMouseListener(new SwingMouseListener<>(this));
    }

    @Override
    public String getText() {
        return this.swing.getText();
    }

    @Override
    public TextBox setText(@NonNull String text) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            if (!this.text.equals(text)) {
                this.swing.setText(this.text = text);
            }
        } else {
            SwingUtilities.invokeLater(() -> this.setText(text));
        }
        return this;
    }

    @Override
    public String getHint() {
        return ((SwingTextBoxImpl) this.swing).hint;
    }

    @Override
    public TextBox setHint(@NonNull String hint) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            ((SwingTextBoxImpl) this.swing).setHint(hint);
        } else {
            SwingUtilities.invokeLater(() -> this.setHint(hint));
        }
        return this;
    }

    @Override
    public int getHintColor() {
        return ((SwingTextBoxImpl) this.swing).hintColor.getRGB();
    }

    @Override
    public TextBox setHintColor(int argb) {
        return this.setHintColor(new Color(argb));
    }

    @Override
    public TextBox setHintColor(@NonNull Color color) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            ((SwingTextBoxImpl) this.swing).setHintColor(color);
        } else {
            SwingUtilities.invokeLater(() -> this.setHintColor(color));
        }
        return this;
    }

    @Override
    public boolean isPassword() {
        return false;
    }

    @Override
    public TextBox addTextChangedListener(@NonNull String name, @NonNull Consumer<String> callback) {
        this.handlers.put(name, callback);
        return this;
    }

    @Override
    public TextBox removeTextChangedListener(@NonNull String name) {
        this.handlers.remove(name);
        return this;
    }

    protected class SwingTextBoxDocumentListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            this.changedUpdate(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            this.changedUpdate(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            String text = SwingTextBox.this.swing.getText();
            if (!SwingTextBox.this.text.equals(text)) {
                SwingTextBox.this.text = text;
                SwingTextBox.this.handlers.forEach((key, handler) -> handler.accept(text));
            }
        }
    }

    @Getter
    protected static class SwingTextBoxImpl extends JTextField implements FocusListener {
        protected String hint;
        protected Color hintColor = Color.LIGHT_GRAY;

        public void setHint(String hint) {
            this.hint = hint;
            this.repaint();
        }

        public void setHintColor(@NonNull Color hintColor) {
            this.hintColor = hintColor;
            this.repaint();
        }

        {
            this.addFocusListener(this);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (this.ui != null) {
                Graphics scratchGraphics = (g == null) ? null : g.create();
                try {
                    this.ui.update(scratchGraphics, this);
                    this.drawHint(scratchGraphics);
                } finally {
                    scratchGraphics.dispose();
                }
            }
        }

        protected void drawHint(Graphics g)   {
            if (this.hint != null && this.getText().isEmpty() && !this.hasFocus()) {
                g.setColor(this.hintColor);
                int padding = (this.getHeight() - this.getFont().getSize()) / 2;
                g.drawString(this.hint, 4, this.getHeight() - padding - 1);
            }
        }

        @Override
        public void focusGained(FocusEvent e) {
            this.repaint();
        }

        @Override
        public void focusLost(FocusEvent e) {
            this.repaint();
        }
    }
}
