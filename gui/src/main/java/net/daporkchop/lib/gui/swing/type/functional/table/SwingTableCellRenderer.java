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

package net.daporkchop.lib.gui.swing.type.functional.table;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.gui.component.state.functional.LabelState;
import net.daporkchop.lib.gui.component.type.functional.Label;
import net.daporkchop.lib.gui.component.type.functional.Table;
import net.daporkchop.lib.gui.swing.type.functional.SwingLabel;
import net.daporkchop.lib.gui.util.handler.StateListener;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class SwingTableCellRenderer<V> extends DefaultTableCellRenderer {
    @NonNull
    protected final SwingTable parent;
    @NonNull
    protected final Table.CellRenderer<V> renderer;

    protected final SwingLabel label = new RendererLabel(this);

    protected int row;
    protected int col;

    @Override
    public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        this.row = row;
        this.col = column;

        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void setValue(Object value) {
        this.setToolTipText(null);
        this.setText(null);

        this.renderer.render(
                (V) this.parent.model.getValueAt(this.row, this.col),
                this.label,
                this.parent.rowCache.get(this.row),
                (SwingTableColumn<V>) this.parent.columnCache.get(this.col)
        );
    }

    protected static class RendererLabel extends SwingLabel {
        protected RendererLabel(JLabel label) {
            super("", label);
        }

        @Override
        public Label addStateListener(@NonNull String name, @NonNull StateListener<Label, LabelState> listener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Label removeStateListener(@NonNull String name) {
            throw new UnsupportedOperationException();
        }
    }
}
