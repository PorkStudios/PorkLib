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
import net.daporkchop.lib.gui.component.capability.Enableable;
import net.daporkchop.lib.gui.component.capability.SizedValueHolder;
import net.daporkchop.lib.gui.component.orientation.Orientation;
import net.daporkchop.lib.gui.component.orientation.SimpleDynamicOrientation;
import net.daporkchop.lib.gui.component.orientation.StaticOrientation;
import net.daporkchop.lib.gui.component.orientation.advanced.AdvancedOrientation;
import net.daporkchop.lib.gui.component.state.ElementState;
import net.daporkchop.lib.gui.component.type.Window;
import net.daporkchop.lib.gui.util.HorizontalAlignment;
import net.daporkchop.lib.gui.util.VerticalAlignment;
import net.daporkchop.lib.gui.util.event.handler.StateListener;
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

    //state methods
    boolean isHovered();
    boolean isMouseDown();

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
}
