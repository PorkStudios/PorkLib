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

package net.daporkchop.lib.gui.component;

import lombok.NonNull;
import net.daporkchop.lib.gui.GuiEngine;
import net.daporkchop.lib.gui.component.capability.Enableable;
import net.daporkchop.lib.gui.component.capability.SizedValueHolder;
import net.daporkchop.lib.gui.component.orientation.Orientation;
import net.daporkchop.lib.gui.component.orientation.SimpleDynamicOrientation;
import net.daporkchop.lib.gui.component.orientation.StaticOrientation;
import net.daporkchop.lib.gui.component.orientation.advanced.AdvancedOrientation;
import net.daporkchop.lib.gui.component.state.ElementState;
import net.daporkchop.lib.gui.component.type.Window;
import net.daporkchop.lib.gui.util.Side;
import net.daporkchop.lib.gui.util.handler.StateListener;
import net.daporkchop.lib.gui.util.math.BoundingBox;
import net.daporkchop.lib.gui.util.math.Constraint;

import java.util.function.Consumer;

/**
 * An actual element in a GUI. Components may not be standalone, they must be held inside of
 * a {@link Container}. Containers may also be nested inside each other by using {@link NestedContainer},
 * which is also a component.
 * <p>
 * Components provide the actual content of the GUI, such as text, buttons, tick boxes, images, etc.
 *
 * @author DaPorkchop_
 */
@SuppressWarnings("unchecked")
public interface Component<Impl extends Component, State extends ElementState<? extends Element, State>> extends Element<Impl, State>, Enableable<Impl>, SizedValueHolder<Impl> {
    @Override
    default Impl setBounds(@NonNull BoundingBox bounds) {
        return this.setOrientation(bounds);
    }

    Orientation<Impl> getOrientation();

    default Impl setOrientation(@NonNull BoundingBox bounds) {
        return (Impl) this.setOrientation(new StaticOrientation<>(bounds));
    }

    Impl setOrientation(@NonNull Orientation<Impl> orientation);

    Window getWindow();

    String getTooltip();

    Impl setTooltip(String tooltip);
    Impl setTooltip(String[] tooltip);

    //state methods
    boolean isHovered();
    boolean isMouseDown();

    //padding stuff
    Impl setPadding(@NonNull Side side, int padding);
    int getPadding(@NonNull Side side);

    default Impl setPadding(int padding)    {
        return this.setPadding(Side.ALL, padding);
    }

    default Impl pad(int padding)    {
        return this.setPadding(Side.ALL, padding);
    }

    default Impl padTop(int padding)    {
        return this.setPadding(Side.TOP, padding);
    }

    default Impl padBottom(int padding)    {
        return this.setPadding(Side.BOTTOM, padding);
    }

    default Impl padLeft(int padding)    {
        return this.setPadding(Side.LEFT, padding);
    }

    default Impl padRight(int padding)    {
        return this.setPadding(Side.RIGHT, padding);
    }

    default Impl padVertical(int padding)    {
        return this.setPadding(Side.TOP_BOTTOM, padding);
    }

    default Impl padHorizontal(int padding)    {
        return this.setPadding(Side.LEFT_RIGHT, padding);
    }

    default Impl padSides(int padding)    {
        return this.setPadding(Side.LEFT_RIGHT, padding);
    }

    //convenience methods
    @SuppressWarnings("unchecked")
    default Impl addHoveringListener(@NonNull Consumer<Boolean> callback)  {
        return this.addStateListener(String.format("%s@%d", callback.getClass().getCanonicalName(), System.identityHashCode(callback)), new StateListener<Impl, State>() {
            protected boolean hovered = Component.this.isHovered();

            @Override
            public void onStateChange(@NonNull State state) {
                if (state.isHovered() != this.hovered) {
                    callback.accept(this.hovered = state.isHovered());
                }
            }
        });
    }
    default Impl addHoveredListener(@NonNull Runnable callback)  {
        return this.addHoveringListener(hovered -> {
            if (hovered)    {
                callback.run();
            }
        });
    }
    default Impl addNotHoveredListener(@NonNull Runnable callback)  {
        return this.addHoveringListener(hovered -> {
            if (!hovered)   {
                callback.run();
            }
        });
    }

    default Impl orientRelative(@NonNull Number x, @NonNull Number y, @NonNull Number width, @NonNull Number height) {
        return (Impl) this.setOrientation(SimpleDynamicOrientation.of(x, y, width, height));
    }

    default Impl setOrientation(@NonNull Constraint constraint) {
        return this.setOrientation(this.getBounds().set(constraint));
    }

    default Impl orientAdvanced(@NonNull Consumer<AdvancedOrientation<Impl>> initializer)   {
        AdvancedOrientation<Impl> orientation = new AdvancedOrientation<>();
        initializer.accept(orientation);
        return this.setOrientation(orientation);
    }

    @Override
    default GuiEngine engine() {
        return this.getWindow().engine();
    }
}
