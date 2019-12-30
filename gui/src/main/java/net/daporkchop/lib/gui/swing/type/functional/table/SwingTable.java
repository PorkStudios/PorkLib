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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.gui.component.state.functional.LabelState;
import net.daporkchop.lib.gui.component.state.functional.TableState;
import net.daporkchop.lib.gui.component.type.functional.Label;
import net.daporkchop.lib.gui.component.type.functional.Table;
import net.daporkchop.lib.gui.swing.GuiEngineSwing;
import net.daporkchop.lib.gui.swing.common.SwingMouseListener;
import net.daporkchop.lib.gui.swing.impl.SwingComponent;
import net.daporkchop.lib.gui.swing.type.container.SwingScrollPane;
import net.daporkchop.lib.gui.swing.type.functional.SwingLabel;
import net.daporkchop.lib.gui.util.ScrollCondition;
import net.daporkchop.lib.gui.util.handler.StateListener;
import net.daporkchop.lib.gui.util.handler.TableClickHandler;
import net.daporkchop.lib.unsafe.PUnsafe;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.plaf.TableHeaderUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author DaPorkchop_
 */
public class SwingTable extends SwingComponent<Table, JScrollPane, TableState> implements Table {
    protected static final long DEFAULTEDITORSBYCOLUMNCLASS_OFFSET = PUnsafe.pork_getOffset(JTable.class, "defaultEditorsByColumnClass");

    @Getter
    protected final SwingScrollPane scrollPane;

    protected final JTable            table;
    protected final JTableHeader      header;
    protected final TableHeaderUI     headerUI;
    protected final DefaultTableModel model;

    protected final List<SwingTableRow> rowCache    = new ArrayList<>();
    protected final List<SwingTableColumn>    columnCache = new ArrayList<>();

    protected boolean headersShown = true;

    public SwingTable(String name) {
        this(name, new SwingScrollPane(name));
    }

    protected SwingTable(String name, SwingScrollPane scrollPane) {
        super(name, scrollPane.getSwing());

        this.scrollPane = scrollPane;
        this.scrollPane.setScrolling(ScrollCondition.NEVER);

        this.table = new JTable();
        this.table.setAutoCreateColumnsFromModel(false);
        this.table.setEnabled(true);

        PUnsafe.<Hashtable>getObject(this.table, DEFAULTEDITORSBYCOLUMNCLASS_OFFSET).clear();

        this.swing.setViewportView(this.table);
        this.header = this.table.getTableHeader();
        this.headerUI = this.header.getUI();
        this.model = (DefaultTableModel) this.table.getModel();
        this.model.addTableModelListener(event -> {
            switch (event.getType()) {
                case TableModelEvent.INSERT:
                case TableModelEvent.DELETE: {
                    for (int i = this.rowCache.size() - 1; i >= 0; i--) {
                        this.rowCache.get(i).index = i;
                    }
                    for (int i = this.columnCache.size() - 1; i >= 0; i--) {
                        this.columnCache.get(i).index = i;
                    }
                }
            }
        });

        this.table.addMouseListener(new SwingTableMouseListener(this));
    }

    @Override
    public int getRows() {
        return this.table.getRowCount();
    }

    @Override
    public int getColumns() {
        return this.table.getColumnCount();
    }

    @Override
    public Table removeColumn(int col) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.table.removeColumn(this.columnCache.remove(col).delegate);
        } else {
            SwingUtilities.invokeLater(() -> this.removeColumn(col));
        }
        return this;
    }

    @Override
    public Table removeRow(int row) {
        this.model.removeRow(row);
        return this;
    }

    @Override
    public Table removeColumn(@NonNull Column column) {
        if (column.getParent() != this) {
            throw new IllegalArgumentException("Column does not belong to this table!");
        }
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.removeColumn(column.index());
        } else {
            SwingUtilities.invokeLater(() -> this.removeColumn(column));
        }
        return this;
    }

    @Override
    public Table removeRow(@NonNull Row row) {
        if (row.getParent() != this) {
            throw new IllegalArgumentException("Row does not belong to this table!");
        }
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.removeRow(row.index());
        } else {
            SwingUtilities.invokeLater(() -> this.removeRow(row));
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> Column<V> addAndGetColumn(String name, @NonNull Class<V> clazz) {
        return this.addAndGetColumn(name, clazz, Table.defaultTextRenderer());
    }

    @Override
    public <V> Column<V> addAndGetColumn(String name, @NonNull Class<V> clazz, @NonNull CellRenderer<V> renderer) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.model.addColumn(name);
            TableColumn column = new TableColumn(this.table.getColumnModel().getColumnCount());
            column.setHeaderValue(name);
            column.setCellRenderer(new SwingTableCellRenderer<>(this, renderer));
            this.table.addColumn(column);
            SwingTableColumn<V> fake = new SwingTableColumn<>(this, column, clazz);
            this.columnCache.add(fake);
            return fake;
        } else {
            AtomicReference<Column<V>> ref = new AtomicReference<>();
            try {
                SwingUtilities.invokeAndWait(() -> ref.set(this.addAndGetColumn(name, clazz, renderer)));
            } catch (InvocationTargetException e) {
                throw e.getCause() instanceof RuntimeException ? (RuntimeException) e.getCause() : new RuntimeException(e.getCause());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return ref.get();
        }
    }

    @Override
    public Row addAndGetRow() {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.model.addRow(new Object[this.model.getColumnCount()]);
            SwingTableRow fake = new SwingTableRow(this, this.model.getRowCount() - 1);
            this.rowCache.add(fake);
            return fake;
        } else {
            AtomicReference<Row> ref = new AtomicReference<>();
            try {
                SwingUtilities.invokeAndWait(() -> ref.set(this.addAndGetRow()));
            } catch (InvocationTargetException e) {
                throw e.getCause() instanceof RuntimeException ? (RuntimeException) e.getCause() : new RuntimeException(e.getCause());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return ref.get();
        }
    }

    @Override
    public Row insertAndGetRow(int index) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.model.insertRow(index, new Object[this.model.getColumnCount()]);
            SwingTableRow fake = new SwingTableRow(this, index);
            this.rowCache.add(fake);
            return fake;
        } else {
            AtomicReference<Row> ref = new AtomicReference<>();
            try {
                SwingUtilities.invokeAndWait(() -> ref.set(this.insertAndGetRow(index)));
            } catch (InvocationTargetException e) {
                throw e.getCause() instanceof RuntimeException ? (RuntimeException) e.getCause() : new RuntimeException(e.getCause());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return ref.get();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> Column<V> getColumn(int index) {
        return (SwingTableColumn<V>) this.columnCache.get(index);
    }

    @Override
    public Row getRow(int index) {
        return this.rowCache.get(index);
    }

    @Override
    public String getColumnName(int index) {
        return this.table.getColumnName(index);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V getValue(int row, int col) {
        return this.getValue(this.rowCache.get(row), (Column<V>) this.columnCache.get(col));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Table setValue(int row, int col, Object val) {
        return this.setValue(this.rowCache.get(row), this.columnCache.get(col), val);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V getValue(@NonNull Row row, @NonNull Column<V> col) {
        if (row.getParent() != this) {
            throw new IllegalArgumentException("Row does not belong to this table!");
        } else if (col.getParent() != this) {
            throw new IllegalArgumentException("Column does not belong to this table!");
        }
        return (V) this.model.getValueAt(row.index(), col.index());
    }

    @Override
    public <V> Table setValue(@NonNull Row row, @NonNull Column<V> col, V val) {
        if (row.getParent() != this) {
            throw new IllegalArgumentException("Row does not belong to this table!");
        } else if (col.getParent() != this) {
            throw new IllegalArgumentException("Column does not belong to this table!");
        }
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.model.setValueAt(val, row.index(), col.index());
        } else {
            SwingUtilities.invokeLater(() -> this.setValue(row, col, val));
        }
        return this;
    }

    @Override
    public boolean areHeadersShown() {
        return this.headersShown;
    }

    @Override
    public Table setHeadersShown(boolean headersShown) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            if (this.headersShown != headersShown) {
                this.headersShown = headersShown;
                this.header.setUI(headersShown ? this.headerUI : null);
                //this.table.setTableHeader(headersShown ? this.header : null);
            }
        } else {
            SwingUtilities.invokeLater(() -> this.setHeadersShown(headersShown));
        }
        return this;
    }

    protected static class SwingTableMouseListener extends SwingMouseListener<SwingTable> {
        public SwingTableMouseListener(SwingTable delegate) {
            super(delegate);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void mouseClicked(MouseEvent e) {
            if (this.delegate.isEnabled()) {
                int rowNumber = this.delegate.table.rowAtPoint(e.getPoint());
                int columnNumber = this.delegate.table.columnAtPoint(e.getPoint());

                if (rowNumber >= 0 && columnNumber >= 0)   {
                    TableClickHandler handler = this.delegate.columnCache.get(columnNumber).clickHandler;
                    if (handler != null) {
                        handler.onClick(e.getButton(), this.delegate.rowCache.get(columnNumber), this.delegate.model.getValueAt(rowNumber, columnNumber));
                    }
                }
            }

            super.mouseClicked(e);
        }
    }
}
