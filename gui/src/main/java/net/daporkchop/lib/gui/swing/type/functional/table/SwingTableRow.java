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
