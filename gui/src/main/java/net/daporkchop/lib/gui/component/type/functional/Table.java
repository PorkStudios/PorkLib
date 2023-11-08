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

package net.daporkchop.lib.gui.component.type.functional;

import lombok.NonNull;
import net.daporkchop.lib.gui.GuiEngine;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.state.functional.TableState;
import net.daporkchop.lib.gui.component.type.container.ScrollPane;
import net.daporkchop.lib.gui.util.handler.TableClickHandler;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * A 2-dimensional grid of values, arranged into rows and columns. Each column may only have one value type,
 * and has a specific renderer used for it.
 *
 * @author DaPorkchop_
 */
public interface Table extends Component<Table, TableState> {
    static <V> CellRenderer<V> defaultTextRenderer()  {
        return (value, label, row, col) -> label.setText(Objects.toString(value));
    }

    @Override
    default TableState getState() {
        return this.isVisible() ?
                this.isEnabled() ?
                        this.isHovered() ? TableState.ENABLED_HOVERED : TableState.ENABLED
                        : this.isHovered() ? TableState.DISABLED_HOVERED : TableState.DISABLED
                : TableState.HIDDEN;
    }

    ScrollPane getScrollPane();

    int getColumns();
    int getRows();

    @Deprecated
    Table removeColumn(int col);
    @Deprecated
    Table removeRow(int row);

    Table removeColumn(@NonNull Column column);
    Table removeRow(@NonNull Row row);

    <V> Column<V> addAndGetColumn(String name, @NonNull Class<V> clazz);
    <V> Column<V> addAndGetColumn(String name, @NonNull Class<V> clazz, @NonNull CellRenderer<V> renderer);
    Row addAndGetRow();
    Row insertAndGetRow(int index);
    default Table addColumn(String name, @NonNull Class<?> clazz)    {
        this.addAndGetColumn(name, clazz);
        return this;
    }
    default Table addRow()  {
        this.addAndGetRow();
        return this;
    }
    default Table insertRow(int index)  {
        this.insertAndGetRow(index);
        return this;
    }

    <V> Column<V> getColumn(int index);
    Row getRow(int index);

    default String getColumnName(int index)   {
        return this.getColumn(index).getName();
    }
    default Table setColumnName(int index, String name)    {
        this.getColumn(index).setName(name);
        return this;
    }

    @Deprecated
    <V> V getValue(int row, int col);
    @Deprecated
    Table setValue(int row, int col, Object val);

    <V> V getValue(@NonNull Row row, @NonNull Column<V> col);
    <V> Table setValue(@NonNull Row row, @NonNull Column<V> col, V val);

    boolean areHeadersShown();
    Table setHeadersShown(boolean headersShown);
    default Table showHeaders() {
        return this.setHeadersShown(true);
    }
    default Table hideHeaders() {
        return this.setHeadersShown(false);
    }

    boolean areRowsSelectable();
    Table setRowsSelectable(boolean rowsSelectable);
    default Table enableRowSelection() {
        return this.setRowsSelectable(true);
    }
    default Table disableRowSelection() {
        return this.setRowsSelectable(false);
    }

    interface Column<V>    {
        Table getParent();

        String getName();
        Column<V> setName(String name);

        int index();
        Column<V> setIndex(int dst);
        Column<V> swap(int dst);

        Class<V> getValueClass();

        TableClickHandler<V> getClickHandler();
        Column<V> setClickHandler(TableClickHandler<V> handler);
    }

    interface Row   {
        Table getParent();

        int index();
        Row setIndex(int dst);
        Row swap(int dst);

        @Deprecated
        <V> V getValue(int col);
        @Deprecated
        Row setValue(int col, Object val);

        <V> V getValue(@NonNull Column<V> col);
        <V> Row setValue(@NonNull Column<V> col, V val);
    }

    @FunctionalInterface
    interface CellRenderer<V>  {
        void render(V value, @NonNull Label label, @NonNull Row row, @NonNull Column<V> col);
    }
}
