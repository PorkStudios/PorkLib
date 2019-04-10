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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.state.functional.TableState;
import net.daporkchop.lib.gui.component.type.functional.Table;
import net.daporkchop.lib.gui.swing.common.SwingMouseListener;
import net.daporkchop.lib.gui.swing.impl.SwingComponent;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.util.Vector;

/**
 * @author DaPorkchop_
 */
public class SwingTable extends SwingComponent<Table, JTable, TableState> implements Table {
    protected final DefaultTableModel model;

    public SwingTable(String name) {
        super(name, new JTable());

        this.model = (DefaultTableModel) this.swing.getModel();

        this.swing.addMouseListener(new SwingMouseListener<>(this));
    }

    @Override
    public int getRows() {
        return this.swing.getRowCount();
    }

    @Override
    public int getColumns() {
        return this.swing.getColumnCount();
    }

    @Override
    public Table removeColumn(int col) {
        this.swing.getColumnModel().removeColumn(this.swing.getColumnModel().getColumn(col));
        return this;
    }

    @Override
    public Table removeRow(int row) {
        this.model.removeRow(row);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> Column<V> addAndGetColumn(String name, @NonNull Class<V> clazz) {
        this.model.addColumn(name);
        TableColumn column = this.swing.getColumnModel().getColumn(this.swing.getColumnModel().getColumnCount() - 1);
        column.setHeaderValue(name);
        FakeColumn<V> fake = new FakeColumn<>(column);
        column.setIdentifier(fake);
        return fake;
    }

    @Override
    public Row addAndGetRow() {
        this.model.addRow(new Object[this.model.getColumnCount()]);
        return new FakeRow(this.model.getRowCount() - 1);
    }

    @Override
    public Row insertAndGetRow(int index) {
        //this.model.insertRow(index, );
        //return new FakeRow(index);
        return this.addAndGetRow();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> Column<V> getColumn(int index) {
        return (FakeColumn<V>) this.swing.getColumnModel().getColumn(index).getIdentifier();
    }

    @Override
    public Row getRow(int index) {
        return new FakeRow(index);
    }

    @Override
    public String getColumnName(int index) {
        return this.swing.getColumnName(index);
    }

    @RequiredArgsConstructor
    @Getter
    protected class FakeColumn<V> implements Column<V>  {
        @NonNull
        protected final TableColumn column;

        @SuppressWarnings("unchecked")
        protected Class<V> valueClass = (Class<V>) Object.class;

        @Override
        public Table getParent() {
            return SwingTable.this;
        }

        @Override
        public String getName() {
            return (String) this.column.getHeaderValue();
        }

        @Override
        public Column<V> setName(String name) {
            this.column.setHeaderValue(name);
            return this;
        }

        @Override
        public int index() {
            return this.column.getModelIndex();
        }

        @Override
        public Column<V> setIndex(int dst) {
            return this;
        }

        @Override
        public Column<V> swap(int dst) {
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Class<V> getValueClass() {
            return (Class<V>) SwingTable.this.swing.getColumnClass(this.index());
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> Column<T> setValueType(@NonNull Class<T> clazz, @NonNull Renderer<T, ? extends Component> renderer) {
            FakeColumn<T> this_ = (FakeColumn<T>) this;
            this.column.setCellRenderer((table, value, isSelected, hasFocus, row, column1) -> {
                Component component = renderer.update(SwingTable.this.engine(), (T) value, null);
                return ((SwingComponent) component).getSwing();
            });
            this_.valueClass = clazz;
            return this_;
        }
    }

    @AllArgsConstructor
    @Getter
    protected class FakeRow implements Row  {
        protected int index;

        @Override
        public Table getParent() {
            return SwingTable.this;
        }

        @Override
        public int index() {
            return this.index;
        }

        @Override
        public Row setIndex(int dst) {
            return this;
        }

        @Override
        public Row swap(int dst) {
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <V> V getValue(int col) {
            return (V) SwingTable.this.model.getValueAt(this.index, col);
        }

        @Override
        public Row setValue(int col, Object val) {
            SwingTable.this.model.setValueAt(val, this.index, col);
            return this;
        }
    }
}
