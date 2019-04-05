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

package net.daporkchop.lib.gui.component.orientation.advanced;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.Element;
import net.daporkchop.lib.gui.util.Side;
import net.daporkchop.lib.gui.util.math.BoundingBox;

import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public enum Axis {
    X((this_, bb, target, dst) -> bb.getX()),
    Y((this_, bb, target, dst) -> bb.getY()),
    WIDTH((this_, bb, target, dst) -> bb.getWidth() - (dst == null ? 0 : dst.getPadding(Side.LEFT_RIGHT))),
    HEIGHT((this_, bb, target, dst) -> bb.getHeight() - (dst == null ? 0 : dst.getPadding(Side.TOP_BOTTOM))),
    BELOW((this_, bb, target, dst) -> bb.getY() + bb.getHeight() + (target == null ? 0 : target.getPadding(Side.BOTTOM) + (dst == null ? 0 : dst.getPadding(Side.TOP)))),
    RIGHT((this_, bb, target, dst) -> bb.getX() + bb.getWidth() + (target == null ? 0 : target.getPadding(Side.RIGHT) + (dst == null ? 0 : dst.getPadding(Side.LEFT)))),
    ;

    @NonNull
    protected final AxisGetter getter;

    public int getFrom(@NonNull BoundingBox bb) {
        return this.getter.get(this, bb, null, null);
    }

    public int getFrom(@NonNull BoundingBox bb, Component target, Component dst) {
        return this.getter.get(this, bb, target, dst);
    }

    @FunctionalInterface
    protected interface AxisGetter {
        int get(@NonNull Axis this_, @NonNull BoundingBox bb, Component target, Component dst);
    }
}
