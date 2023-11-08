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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.gui.component.type.functional.Table;
import net.daporkchop.lib.gui.swing.GuiEngineSwing;

import javax.swing.SwingUtilities;

/**
 * @author DaPorkchop_
 */
@AllArgsConstructor
@Getter
public class SwingTableRow implements Table.Row {
    @NonNull
    private final SwingTable parent;
    @Accessors(fluent = true)
    protected int      index;

    @Override
    public Table.Row setIndex(int dst) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.parent.model.moveRow(this.index, this.index, dst);
        } else {
            SwingUtilities.invokeLater(() -> this.setIndex(dst));
        }
        return this;
    }

    @Override
    public Table.Row swap(int dst) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.parent.model.moveRow(dst, dst, this.index);
            this.parent.model.moveRow(this.index, this.index, dst);
        } else {
            SwingUtilities.invokeLater(() -> this.swap(dst));
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V getValue(int col) {
        return (V) this.parent.model.getValueAt(this.index, col);
    }

    @Override
    public Table.Row setValue(int col, Object val) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.parent.model.setValueAt(val, this.index, col);
        } else {
            SwingUtilities.invokeLater(() -> this.setValue(col, val));
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> V getValue(@NonNull Table.Column<V> col) {
        return (V) this.parent.model.getValueAt(this.index, col.index());
    }

    @Override
    public <V> Table.Row setValue(@NonNull Table.Column<V> col, V val) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.parent.model.setValueAt(val, this.index, col.index());
        } else {
            SwingUtilities.invokeLater(() -> this.setValue(col, val));
        }
        return this;
    }
}
