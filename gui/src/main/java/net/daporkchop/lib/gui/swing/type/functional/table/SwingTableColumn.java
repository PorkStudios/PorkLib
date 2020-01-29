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

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.gui.component.type.functional.Table;
import net.daporkchop.lib.gui.swing.GuiEngineSwing;
import net.daporkchop.lib.gui.util.handler.TableClickHandler;

import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class SwingTableColumn<V> implements Table.Column<V> {
    @NonNull
    protected final SwingTable parent;
    @NonNull
    protected final TableColumn delegate;
    @NonNull
    protected final Class<V>    valueClass;

    protected TableClickHandler<V> clickHandler;

    @Accessors(fluent = true)
    protected int index;

    @Override
    public String getName() {
        return (String) this.delegate.getHeaderValue();
    }

    @Override
    public Table.Column<V> setName(String name) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.delegate.setHeaderValue(name);
        } else {
            SwingUtilities.invokeLater(() -> this.setName(name));
        }
        return this;
    }

    @Override
    public Table.Column<V> setIndex(int dst) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.parent.columnCache.remove(this);
            this.parent.columnCache.add(dst, this);
            this.parent.table.getColumnModel().moveColumn(this.index, dst);
        } else {
            SwingUtilities.invokeLater(() -> this.setIndex(dst));
        }
        return this;
    }

    @Override
    public Table.Column<V> swap(int dst) {
        if (Thread.currentThread().getClass() == GuiEngineSwing.EVENT_DISPATCH_THREAD) {
            this.parent.table.getColumnModel().moveColumn(dst, this.index);
            this.parent.table.getColumnModel().moveColumn(this.index, dst); //this should work because this.index will be updated
        } else {
            SwingUtilities.invokeLater(() -> this.swap(dst));
        }
        return this;
    }
}
