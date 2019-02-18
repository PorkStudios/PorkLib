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

package net.daporkchop.lib.gui.component;

import lombok.NonNull;
import net.daporkchop.lib.gui.util.math.BoundingBox;

import java.util.StringJoiner;

/**
 * @author DaPorkchop_
 */
public interface Element<Impl extends Element<Impl>> {
    String getName();

    default String getFullName() {
        StringJoiner joiner = new StringJoiner(".");
        joiner.add(this.getName());
        Container next = this.getParent();
        while (next != null) {
            joiner.add(next.getName());
            next = next.getParent();
        }
        return joiner.toString();
    }

    BoundingBox getBounds();

    /**
     * Gets this element's parent
     *
     * @return this element's parent, or {@code null} if (and only if) this element is a {@link net.daporkchop.lib.gui.component.type.Window}
     */
    Container getParent();

    /**
     * Updates this element.
     * <p>
     * If this element is a {@link Container}, this will also recursively update all child elements.
     */
    Impl update();

    //visual things
    String getTooltip();

    Impl setTooltip(@NonNull String tooltip);

    boolean isVisible();

    Impl setVisible(boolean state);

    default Impl show() {
        return this.setVisible(true);
    }

    default Impl hide() {
        return this.setVisible(false);
    }

    /**
     * Releases all resources associated with this element
     */
    void release();
}
