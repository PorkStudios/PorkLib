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
import net.daporkchop.lib.gui.component.state.functional.TextBoxState;
import net.daporkchop.lib.gui.component.type.functional.TextBox;
import net.daporkchop.lib.gui.swing.impl.SwingComponent;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
public class SwingTextBox extends SwingComponent<TextBox, JTextField, TextBoxState> implements TextBox {
    protected String text = "";
    protected final Map<String, Consumer<String>> handlers = new HashMap<>();

    public SwingTextBox(String name) {
        this(name, new JTextField());
    }

    protected SwingTextBox(String name, JTextField jTextField) {
        super(name, jTextField);

        this.swing.setUI(new SwingTextBoxUI());
        this.swing.setText("");
        this.swing.getDocument().addDocumentListener(new SwingTextBoxDocumentListener());
    }

    @Override
    public String getText() {
        return this.swing.getText();
    }

    @Override
    public TextBox setText(@NonNull String text) {
        if (!this.text.equals(text)) {
            this.swing.setText(this.text = text);
        }
        return this;
    }

    @Override
    public String getHint() {
        return ((SwingTextBoxUI) this.swing.getUI()).hint;
    }

    @Override
    public TextBox setHint(@NonNull String hint) {
        ((SwingTextBoxUI) this.swing.getUI()).setHint(hint);
        return this;
    }

    @Override
    public int getHintColor() {
        return ((SwingTextBoxUI) this.swing.getUI()).color.getRGB();
    }

    @Override
    public TextBox setHintColor(int argb) {
        return this.setHintColor(new Color(argb));
    }

    @Override
    public TextBox setHintColor(@NonNull Color color) {
        ((SwingTextBoxUI) this.swing.getUI()).setColor(color);
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
    public class SwingTextBoxUI extends BasicTextFieldUI implements FocusListener {
        private String hint;
        private Color color = Color.LIGHT_GRAY;

        public void setColor(@NonNull Color color) {
            this.color = color;
            this.repaint();
        }

        private void repaint() {
            if (this.getComponent() != null) {
                this.getComponent().repaint();
            }
        }

        public void setHint(String hint) {
            this.hint = hint;
            this.repaint();
        }

        @Override
        protected void paintSafely(Graphics g) {
            super.paintSafely(g);
            JTextComponent comp = getComponent();
            if (this.hint != null && comp.getText().isEmpty() && !comp.hasFocus()) {
                g.setColor(this.color);
                int padding = (comp.getHeight() - comp.getFont().getSize()) / 2;
                g.drawString(this.hint, 3, comp.getHeight() - padding - 1);
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

        @Override
        protected void installListeners() {
            super.installListeners();
            this.getComponent().addFocusListener(this);
        }

        @Override
        protected void uninstallListeners() {
            super.uninstallListeners();
            this.getComponent().removeFocusListener(this);
        }
    }
}
