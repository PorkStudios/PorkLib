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
