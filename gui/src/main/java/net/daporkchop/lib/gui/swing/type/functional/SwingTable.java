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
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.state.functional.TableState;
import net.daporkchop.lib.gui.component.type.functional.Table;
import net.daporkchop.lib.gui.swing.common.SwingMouseListener;
import net.daporkchop.lib.gui.swing.impl.SwingComponent;

import javax.swing.*;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * @author DaPorkchop_
 */
public class SwingTable extends SwingComponent<Table, SwingTable.JTableHack, TableState> implements Table {
    protected final SwingTableModel model;

    public SwingTable(String name) {
        super(name, new JTableHack());

        this.swing.wrapper = this;
        this.model = this.swing.getModel();

        this.swing.addMouseListener(new SwingMouseListener<>(this));
    }

    @Override
    public int getRows() {
        return this.model.getRowCount();
    }

    @Override
    public int getColumns() {
        return this.model.getColumnCount();
    }

    @Override
    public Table removeColumn(int col) {
        this.swing.getColumnModel().removeColumn(this.swing.getColumnModel().getColumn(col));
        return this;
    }

    @Override
    public Table removeRow(int row) {
        this.swing.getModel().rows.remove(row);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> Column<V> addAndGetColumn(String name, @NonNull Class<V> clazz) {
        TableColumn column = new TableColumn();
        column.setHeaderValue(name);
        this.swing.addColumn(column);
        return (Column<V>) this.swing.getColumnModel().columns.stream()
                .filter(data -> data.delegate == column)
                .findAny()
                .orElseThrow(IllegalStateException::new)
                .setValueClass(clazz);
    }

    @Override
    public Row addAndGetRow() {
        SwingRow row = new SwingRow(this.swing);
        for (int i = this.getColumns() - 1; i >= 0; i--)    {
            row.values.add(null);
        }
        this.swing.getModel().rows.add(row);
        return row;
    }

    @Override
    public Row insertAndGetRow(int index) {
        SwingRow row = new SwingRow(this.swing);
        for (int i = this.getColumns() - 1; i >= 0; i--)    {
            row.values.add(null);
        }
        this.swing.getModel().rows.add(index, row);
        return row;
    }

    @Override
    public Column getColumn(int index) {
        return this.swing.getColumnModel().columns.get(index);
    }

    @Override
    public Row getRow(int index) {
        return this.swing.getModel().rows.get(index);
    }

    @Override
    public String getColumnName(int index) {
        return this.model.getColumnName(index);
    }

    @RequiredArgsConstructor
    @Getter
    protected static class SwingTableModel implements TableModel {
        @NonNull
        protected final JTableHack table;

        protected SwingTableColumnModel columnModel;
        protected final List<SwingRow> rows = new ArrayList<>(); //TODO: a linked list might be better here...

        public SwingTableColumnModel getColumnModel() {
            SwingTableColumnModel columnModel = this.columnModel;
            if (columnModel == null) {
                columnModel = this.columnModel = this.table.getColumnModel();
            }
            return columnModel;
        }

        @Override
        public int getRowCount() {
            return this.rows.size();
        }

        @Override
        public int getColumnCount() {
            return this.getColumnModel().columns.size();
        }

        @Override
        public String getColumnName(int columnIndex) {
            String s = (String) this.getColumnModel().getColumn(columnIndex).getHeaderValue();
            return s == null ? "" : s;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return this.getColumnModel().getColumnData(columnIndex).valueClass;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return this.rows.get(rowIndex).getValue(columnIndex);
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            this.rows.get(rowIndex).setValue(columnIndex, aValue);
        }

        @Override
        public void addTableModelListener(TableModelListener l) {
        }

        @Override
        public void removeTableModelListener(TableModelListener l) {
        }
    }

    @RequiredArgsConstructor
    @Getter
    protected static class SwingTableColumnModel implements TableColumnModel {
        @NonNull
        protected final JTableHack table;

        protected final List<ColumnData> columns = new ArrayList<>();

        @Override
        public void addColumn(TableColumn aColumn) {
            this.columns.add(new ColumnData(this.table, aColumn));
            for (SwingRow row : this.table.getModel().rows) {
                row.values.add(null);
            }
            this.setAllColumnIndices();
        }

        @Override
        public void removeColumn(TableColumn column) {
            int i = 0;
            for (Iterator<ColumnData> iter = this.columns.iterator(); iter.hasNext(); i++) {
                if (iter.next().delegate.equals(column)) {
                    iter.remove();
                    for (SwingRow row : this.table.getModel().rows) {
                        row.values.remove(i);
                    }
                    this.setAllColumnIndices();
                    return;
                }
            }
        }

        @Override
        public void moveColumn(int columnIndex, int newIndex) {
            this.columns.add(newIndex, this.columns.remove(columnIndex));
            for (SwingRow row : this.table.getModel().rows) {
                row.values.add(newIndex, row.values.remove(columnIndex));
            }
            this.setAllColumnIndices();
        }

        public void setAllColumnIndices()   {
            for (int i = 0; i < this.columns.size(); i++)   {
                this.columns.get(i).delegate.setModelIndex(i);
            }
        }

        @Override
        public int getColumnCount() {
            return this.columns.size();
        }

        @Override
        public Enumeration<TableColumn> getColumns() {
            return new Enumeration<TableColumn>() {
                protected final Iterator<ColumnData> iter = SwingTableColumnModel.this.columns.iterator();

                @Override
                public boolean hasMoreElements() {
                    return this.iter.hasNext();
                }

                @Override
                public TableColumn nextElement() {
                    return this.iter.next().delegate;
                }
            };
        }

        @Override
        public int getColumnIndex(@NonNull Object columnIdentifier) {
            for (int i = 0; i < this.columns.size(); i++) {
                if (columnIdentifier.equals(this.columns.get(i).delegate.getIdentifier())) {
                    return i;
                }
            }
            throw new IllegalArgumentException("Identifier not found!");
        }

        public ColumnData getColumnData(int columnIndex) {
            return this.columns.get(columnIndex);
        }

        @Override
        public TableColumn getColumn(int columnIndex) {
            return this.columns.get(columnIndex).delegate;
        }

        @Override
        public int getColumnMargin() {
            return 0;
        }

        @Override
        public void setColumnMargin(int newMargin) {
        }

        @Override
        public int getColumnIndexAtX(int xPosition) {
            if (xPosition < 0) {
                return -1;
            }
            for (int i = 0, w = 0; i < this.columns.size(); w += this.columns.get(i++).delegate.getWidth()) {
                if (xPosition < w) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public int getTotalColumnWidth() {
            return this.columns.stream().mapToInt(col -> col.delegate.getWidth()).sum();
        }

        @Override
        public boolean getColumnSelectionAllowed() {
            return false;
        }

        @Override
        public void setColumnSelectionAllowed(boolean flag) {
        }

        @Override
        public int[] getSelectedColumns() {
            return new int[0];
        }

        @Override
        public int getSelectedColumnCount() {
            return 0;
        }

        @Override
        public ListSelectionModel getSelectionModel() {
            return new DefaultListSelectionModel();
        }

        @Override
        public void setSelectionModel(ListSelectionModel newModel) {
        }

        @Override
        public void addColumnModelListener(TableColumnModelListener x) {
        }

        @Override
        public void removeColumnModelListener(TableColumnModelListener x) {
        }
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    protected static class ColumnData<V> implements Column<V> {
        @NonNull
        protected final JTableHack parent;
        @NonNull
        protected final TableColumn delegate;

        @SuppressWarnings("unchecked")
        protected Class<V> valueClass = (Class<V>) Object.class;
        @SuppressWarnings("unchecked")
        protected Renderer<V, ? extends Component> valueRenderer = (Renderer<V, ? extends Component>) Table.defaultTextRenderer();

        @SuppressWarnings("unchecked")
        public ColumnData(@NonNull JTableHack parent, @NonNull TableColumn delegate) {
            this.parent = parent;
            this.delegate = delegate;

            this.delegate.setCellRenderer((table, value, isSelected, hasFocus, row, column) -> ((SwingComponent) this.valueRenderer.update(this.parent.wrapper.engine(), (V) value, null)).getSwing());
        }

        @Override
        public Table getParent() {
            return this.parent.wrapper;
        }

        @Override
        public String getName() {
            return (String) this.delegate.getHeaderValue();
        }

        @Override
        public Column<V> setName(String name) {
            this.delegate.setHeaderValue(name);
            return this;
        }

        @Override
        public int index() {
            return this.parent.getColumnModel().columns.indexOf(this);
        }

        @Override
        public Column<V> setIndex(int dst) {
            this.parent.getColumnModel().moveColumn(this.index(), dst);
            return this;
        }

        @Override
        public Column<V> swap(int dst) {
            List<ColumnData> columnsList = this.parent.getColumnModel().columns;
            int thisIndex = columnsList.indexOf(this);
            columnsList.set(thisIndex, columnsList.set(dst, this));
            for (SwingRow row : this.parent.getModel().rows) {
                row.values.set(thisIndex, row.values.set(dst, row.values.get(thisIndex)));
            }
            this.parent.getColumnModel().setAllColumnIndices();
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> Column<T> setValueType(@NonNull Class<T> clazz, @NonNull Renderer<T, ? extends Component> renderer) {
            ColumnData<T> this_ = (ColumnData<T>) this;
            this_.valueClass = clazz;
            this_.valueRenderer = renderer;
            return this_;
        }
    }

    @RequiredArgsConstructor
    @Getter
    @Setter
    @Accessors(chain = true)
    protected static class SwingRow implements Row  {
        @NonNull
        protected final JTableHack parent;

        protected final List<Object> values = new ArrayList<>();

        @Override
        public Table getParent()    {
            return this.parent.wrapper;
        }

        @Override
        public int index() {
            return this.parent.getModel().rows.indexOf(this);
        }

        @Override
        public Row setIndex(int dst) {
            List<SwingRow> rows = this.parent.getModel().rows;
            rows.remove(this);
            rows.add(dst, this);
            return this;
        }

        @Override
        public Row swap(int dst) {
            List<SwingRow> rows = this.parent.getModel().rows;
            rows.set(rows.indexOf(this), rows.set(dst, this));
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <V> V getValue(int col) {
            return (V) this.values.get(col);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Row setValue(int col, Object val) {
            if (val == null || this.parent.getColumnModel().columns.get(col).valueClass.isAssignableFrom(val.getClass())) {
                this.values.set(col, val);
                return this;
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    protected static class JTableHack extends JTable {
        @NonNull
        protected SwingTable wrapper = null;

        @Override
        protected TableModel createDefaultDataModel() {
            return new SwingTableModel(this);
        }

        @Override
        protected TableColumnModel createDefaultColumnModel() {
            return new SwingTableColumnModel(this);
        }

        @Override
        public SwingTableModel getModel() {
            return (SwingTableModel) super.getModel();
        }

        @Override
        public SwingTableColumnModel getColumnModel() {
            return (SwingTableColumnModel) super.getColumnModel();
        }
    }
}
