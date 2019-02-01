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

package net.daporkchop.lib.gui.component.type;

import lombok.NonNull;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.util.math.BoundingBox;
import net.daporkchop.lib.gui.util.math.Constraint;

/**
 * A GUI window
 *
 * @author DaPorkchop_
 */
public interface Window<Impl extends Window, Comp extends Component> extends Component<Impl>, Container<Impl, Comp> {
    Impl setBounds(@NonNull BoundingBox bb);

    default Impl setPos(int x, int y) {
        return this.setConstraints(Constraint.xy(x, y));
    }

    default Impl setSize(int width, int height) {
        return this.setConstraints(Constraint.wh(width, height));
    }

    default Impl setBounds(int x, int y, int width, int height) {
        return this.setBounds(Constraint.bb(x, y, width, height));
    }

    default Impl setConstraints(@NonNull Constraint constraint)  {
        return this.setBounds(this.getBounds().set(constraint));
    }

    Impl update();

    @Override
    default Impl update(Container parent) {
        return this.update();
    }

    Impl setTitle(@NonNull String title);

    Impl setResizable(boolean resizable);

    boolean isResizable();

    void dispose();
}
