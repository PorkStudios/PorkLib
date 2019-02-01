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

package net.daporkchop.lib.gui;

import lombok.NonNull;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.type.Button;
import net.daporkchop.lib.gui.component.type.Window;
import net.daporkchop.lib.gui.swing.GuiSystemSwing;
import net.daporkchop.lib.gui.util.math.BoundingBox;

/**
 * A system for displaying Guis
 *
 * @author DaPorkchop_
 */
public interface GuiSystem<T extends Window<T, ? extends Component>> {
    static GuiSystemSwing swing() {
        return GuiSystemSwing.getInstance();
    }

    String getName();

    default T newWindow(int x, int y, int width, int height) {
        return this.newWindow(new BoundingBox(x, y, width, height));
    }

    /**
     * Creates a new window with the given dimensions
     *
     * @param dimensions the dimensions of the new window to be created
     * @return the newly created window
     */
    T newWindow(@NonNull BoundingBox dimensions);
}
