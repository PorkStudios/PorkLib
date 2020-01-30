/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.graphics.bitmap.PIcon;
import net.daporkchop.lib.gui.component.state.functional.DropdownState;
import net.daporkchop.lib.gui.component.type.functional.Dropdown;
import net.daporkchop.lib.gui.swing.GuiEngineSwing;
import net.daporkchop.lib.gui.swing.common.SwingMouseListener;
import net.daporkchop.lib.gui.swing.impl.SwingComponent;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author DaPorkchop_
 */
@Accessors(chain = true)
public class SwingDropdown<V> extends SwingComponent<Dropdown<V>, JComboBox<V>, DropdownState> implements Dropdown<V> {
    protected final Map<String, Consumer<V>> valueChangeListeners = Collections.synchronizedMap(new LinkedHashMap<>());

    protected Function<V, String> textRenderer = Objects::toString;
    protected Function<V, PIcon> iconRenderer;
    protected boolean shouldRenderAsText = true;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    protected boolean down;

    @SuppressWarnings("unchecked")
    public SwingDropdown(String name) {
        super(name, new JComboBox<>());

        this.swing.setRenderer(new SwingDropdownRenderer());

        this.swing.addMouseListener(new SwingMouseListener<>(this));
        this.swing.addPopupMenuListener(new SwingDropdownPopupListener<>(this));
        this.swing.addItemListener(new SwingDropdownItemListener<>(this));
    }

    @Override
    public Dropdown<V> clearValues() {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.swing.removeAllItems();
        } else {
            SwingUtilities.invokeLater(this::clearValues);
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V getSelectedValue() {
        return (V) this.swing.getSelectedItem();
    }

    @Override
    public Dropdown<V> setSelectedValue(V value) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.swing.setSelectedItem(value);
        } else {
            SwingUtilities.invokeLater(() -> this.setSelectedValue(value));
        }
        return this;
    }

    @Override
    public Dropdown<V> addValue(@NonNull V value) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.swing.addItem(value);
        } else {
            SwingUtilities.invokeLater(() -> this.addValue(value));
        }
        return this;
    }

    @Override
    public Dropdown<V> removeValue(@NonNull V value) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.swing.removeItem(value);
        } else {
            SwingUtilities.invokeLater(() -> this.removeValue(value));
        }
        return this;
    }

    @Override
    public Dropdown<V> addValueSelectedListener(@NonNull String name, @NonNull Consumer<V> callback) {
        this.valueChangeListeners.put(name, callback);
        return null;
    }

    @Override
    public Dropdown<V> removeValueSelectedListener(@NonNull String name) {
        this.valueChangeListeners.remove(name);
        return this;
    }

    @Override
    public Dropdown<V> setRendererText(@NonNull Function<V, String> renderer) {
        this.textRenderer = renderer;
        this.shouldRenderAsText = true;
        this.iconRenderer = null;
        return this;
    }

    @Override
    public Dropdown<V> setRendererIcon(@NonNull Function<V, PIcon> renderer) {
        this.iconRenderer = renderer;
        this.shouldRenderAsText = false;
        this.textRenderer = null;
        return this;
    }

    @RequiredArgsConstructor
    @Getter
    protected static class SwingDropdownPopupListener<V> implements PopupMenuListener   {
        @NonNull
        protected final SwingDropdown<V> delegate;

        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            this.delegate.setDown(true).fireStateChange();
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            this.delegate.setDown(false).fireStateChange();
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
            this.delegate.setDown(false).fireStateChange();
        }
    }

    @RequiredArgsConstructor
    @Getter
    protected static class SwingDropdownItemListener<V> implements ItemListener {
        @NonNull
        protected final SwingDropdown<V> delegate;
        protected V previouslySelected;

        @Override
        public void itemStateChanged(ItemEvent e) {
            @SuppressWarnings("unchecked")
            V value = (V) e.getItem();
            if (value != this.previouslySelected)   {
                this.previouslySelected = value;
                this.delegate.valueChangeListeners.forEach((name, listener) -> listener.accept(value));
            }
        }
    }

    protected class SwingDropdownRenderer extends BasicComboBoxRenderer.UIResource  {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (isSelected) {
                this.setBackground(list.getSelectionBackground());
                this.setForeground(list.getSelectionForeground());
            } else {
                this.setBackground(list.getBackground());
                this.setForeground(list.getForeground());
            }
            this.setFont(list.getFont());

            @SuppressWarnings("unchecked")
            V val = (V) value;

            if (SwingDropdown.this.shouldRenderAsText)  {
                if (this.getIcon() != null) {
                    this.setIcon(null);
                }
                this.setText(SwingDropdown.this.textRenderer.apply(val));
            } else {
                if (this.getText() != null) {
                    this.setText(null);
                }
                this.setIcon(SwingDropdown.this.iconRenderer.apply(val).asSwingIcon());
            }
            return this;
        }
    }
}
