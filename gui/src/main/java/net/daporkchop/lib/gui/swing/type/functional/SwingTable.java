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
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.state.functional.TableState;
import net.daporkchop.lib.gui.component.type.functional.Table;
import net.daporkchop.lib.gui.swing.GuiEngineSwing;
import net.daporkchop.lib.gui.swing.common.SwingMouseListener;
import net.daporkchop.lib.gui.swing.impl.SwingComponent;
import net.daporkchop.lib.gui.swing.type.container.SwingScrollPane;
import net.daporkchop.lib.gui.util.ScrollCondition;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.plaf.TableHeaderUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author DaPorkchop_
 */
public class SwingTable extends SwingComponent<Table, JScrollPane, TableState> implements Table {
    @Getter
    protected final SwingScrollPane scrollPane;

    protected final JTable            table;
    protected final JTableHeader      header;
    protected final TableHeaderUI     headerUI;
    protected final DefaultTableModel model;

    protected final List<FakeRow>    rowCache    = new ArrayList<>();
    protected final List<FakeColumn> columnCache = new ArrayList<>();

    protected boolean headersShown = true;

    public SwingTable(String name) {
        this(name, new SwingScrollPane(name));
    }

    protected SwingTable(String name, SwingScrollPane scrollPane) {
        super(name, scrollPane.getSwing());

        this.scrollPane = scrollPane;
        this.scrollPane.setScrolling(ScrollCondition.NEVER);

        this.table = new JTable();
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

        this.swing.addMouseListener(new SwingMouseListener<>(this));
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
            this.table.getColumnModel().removeColumn(this.columnCache.remove(col).delegate);
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
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.model.addColumn(name);
            TableColumn column = this.table.getColumnModel().getColumn(this.table.getColumnModel().getColumnCount() - 1);
            column.setHeaderValue(name);
            FakeColumn<V> fake = new FakeColumn<>(column);
            column.setIdentifier(fake);
            this.columnCache.add(fake);
            return fake;
        } else {
            AtomicReference<Column<V>> ref = new AtomicReference<>();
            try {
                SwingUtilities.invokeAndWait(() -> ref.set(this.addAndGetColumn(name, clazz)));
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
            FakeRow fake = new FakeRow(this.model.getRowCount() - 1);
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
            FakeRow fake = new FakeRow(index);
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
        return (FakeColumn<V>) this.columnCache.get(index);
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

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Accessors(fluent = true)
    protected abstract class FakeTableClass {
        protected int index;
    }

    @RequiredArgsConstructor
    @Getter
    protected class FakeColumn<V> extends FakeTableClass implements Column<V> {
        @NonNull
        protected final TableColumn delegate;

        @SuppressWarnings("unchecked")
        protected Class<V> valueClass = (Class<V>) Object.class;

        @Override
        public Table getParent() {
            return SwingTable.this;
        }

        @Override
        public String getName() {
            return (String) this.delegate.getHeaderValue();
        }

        @Override
        public Column<V> setName(String name) {
            if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
                this.delegate.setHeaderValue(name);
            } else {
                SwingUtilities.invokeLater(() -> this.setName(name));
            }
            return this;
        }

        @Override
        public Column<V> setIndex(int dst) {
            if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
                SwingTable.this.columnCache.remove(this);
                SwingTable.this.columnCache.add(dst, this);
                SwingTable.this.table.getColumnModel().moveColumn(this.index, dst);
            } else {
                SwingUtilities.invokeLater(() -> this.setIndex(dst));
            }
            return this;
        }

        @Override
        public Column<V> swap(int dst) {
            if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
                SwingTable.this.table.getColumnModel().moveColumn(dst, this.index);
                SwingTable.this.table.getColumnModel().moveColumn(this.index, dst); //this should work because this.index will be updated
            } else {
                SwingUtilities.invokeLater(() -> this.swap(dst));
            }
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Class<V> getValueClass() {
            return (Class<V>) SwingTable.this.table.getColumnClass(this.index);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> Column<T> setValueType(@NonNull Class<T> clazz, @NonNull Renderer<T, ? extends Component> renderer) {
            FakeColumn<T> this_ = (FakeColumn<T>) this;
            if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
                this.delegate.setCellRenderer((_table, value, isSelected, hasFocus, row, column1) -> {
                    value = _table.getModel().getValueAt(row, column1);
                    Component component = renderer.update(SwingTable.this.engine(), (T) value, null);
                    return ((SwingComponent) component).getSwing();
                });
                this_.valueClass = clazz;
            } else {
                SwingUtilities.invokeLater(() -> this.setValueType(clazz, renderer));
            }
            return this_;
        }
    }

    @AllArgsConstructor
    @Getter
    protected class FakeRow extends FakeTableClass implements Row {
        protected int index;

        @Override
        public Table getParent() {
            return SwingTable.this;
        }

        @Override
        public Row setIndex(int dst) {
            if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
                SwingTable.this.model.moveRow(this.index, this.index, dst);
            } else {
                SwingUtilities.invokeLater(() -> this.setIndex(dst));
            }
            return this;
        }

        @Override
        public Row swap(int dst) {
            if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
                SwingTable.this.model.moveRow(dst, dst, this.index);
                SwingTable.this.model.moveRow(this.index, this.index, dst);
            } else {
                SwingUtilities.invokeLater(() -> this.swap(dst));
            }
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <V> V getValue(int col) {
            return (V) SwingTable.this.model.getValueAt(this.index, col);
        }

        @Override
        public Row setValue(int col, Object val) {
            if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
                SwingTable.this.model.setValueAt(val, this.index, col);
            } else {
                SwingUtilities.invokeLater(() -> this.setValue(col, val));
            }
            return this;
        }
    }
}
