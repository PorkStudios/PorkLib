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
import net.daporkchop.lib.gui.component.state.ElementState;
import net.daporkchop.lib.gui.component.state.functional.TableState;
import net.daporkchop.lib.gui.component.type.functional.Table;
import net.daporkchop.lib.gui.swing.impl.SwingComponent;

import javax.swing.*;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Function;

/**
 * @author DaPorkchop_
 */
public class SwingTable extends SwingComponent<Table, SwingTable.JTableHack, TableState> implements Table {
    protected final SwingTableModel model;

    public SwingTable(String name) {
        super(name, new JTableHack());

        this.model = this.swing.getModel();
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
    public String getColumnName(int index) {
        return this.model.getColumnName(index);
    }

    @Override
    public Table setColumnName(int index, @NonNull String name) {
        return this;
    }

    @Override
    public Table removeColumn(int col) {
        this.swing.getColumnModel().removeColumn(this.swing.getColumnModel().getColumn(col));
        return this;
    }

    @Override
    public Table removeRow(int row) {
        //TODO
        return this;
    }

    @Override
    public Table addColumn(String name) {
        this.swing.addColumn(new TableColumn());
        this.swing.getCellRenderer()
        return this;
    }

    @RequiredArgsConstructor
    @Getter
    protected static class SwingTableModel implements TableModel    {
        @NonNull
        protected final JTableHack table;

        protected int columnCount = 0;
        protected int rowCount = 0;

        protected SwingTableColumnModel columnModel;

        public SwingTableColumnModel getColumnModel()   {
            SwingTableColumnModel columnModel = this.columnModel;
            if (columnModel == null)   {
                columnModel = this.columnModel = this.table.getColumnModel();
            }
            return columnModel;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return (String) this.getColumnModel().getColumn(columnIndex).getHeaderValue();
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return this.getColumnModel().getColumn(columnIndex);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return null;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
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
    protected static class SwingTableColumnModel implements TableColumnModel    {
        @NonNull
        protected final JTableHack table;

        @Override
        public void addColumn(TableColumn aColumn) {
        }

        @Override
        public void removeColumn(TableColumn column) {
        }

        @Override
        public void moveColumn(int columnIndex, int newIndex) {
        }

        @Override
        public void setColumnMargin(int newMargin) {
        }

        @Override
        public int getColumnCount() {
            return 0;
        }

        @Override
        public Enumeration<TableColumn> getColumns() {
            return null;
        }

        @Override
        public int getColumnIndex(Object columnIdentifier) {
            return 0;
        }

        @Override
        public TableColumn getColumn(int columnIndex) {
            return null;
        }

        @Override
        public int getColumnMargin() {
            return 0;
        }

        @Override
        public int getColumnIndexAtX(int xPosition) {
            return 0;
        }

        @Override
        public int getTotalColumnWidth() {
            return 0;
        }

        @Override
        public void setColumnSelectionAllowed(boolean flag) {
        }

        @Override
        public boolean getColumnSelectionAllowed() {
            return false;
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
        public void setSelectionModel(ListSelectionModel newModel) {
        }

        @Override
        public ListSelectionModel getSelectionModel() {
            return null;
        }

        @Override
        public void addColumnModelListener(TableColumnModelListener x) {
        }

        @Override
        public void removeColumnModelListener(TableColumnModelListener x) {
        }
    }

    @RequiredArgsConstructor
    @Getter
    @Setter
    @Accessors(chain = true)
    protected static class ColumnData<T, C extends Component<C, ? extends ElementState>> {
        @NonNull
        protected final TableColumn delegate;

        @SuppressWarnings("unchecked")
        protected Class<T> valueClass = (Class<T>) Object.class;
        protected Function<T, C> componentCreator =
    }

    protected static class JTableHack extends JTable  {
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
