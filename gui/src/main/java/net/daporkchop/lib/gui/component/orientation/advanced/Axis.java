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
    ABOVE((this_, bb, target, dst) -> bb.getY() + (target == null ? 0 : target.getPadding(Side.TOP) + (dst == null ? 0 : dst.getPadding(Side.BOTTOM)))),
    BELOW((this_, bb, target, dst) -> bb.getY() + bb.getHeight() + (target == null ? 0 : target.getPadding(Side.BOTTOM) + (dst == null ? 0 : dst.getPadding(Side.TOP)))),
    LEFT((this_, bb, target, dst) -> bb.getX() + (target == null ? 0 : target.getPadding(Side.LEFT) + (dst == null ? 0 : dst.getPadding(Side.RIGHT)))),
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
