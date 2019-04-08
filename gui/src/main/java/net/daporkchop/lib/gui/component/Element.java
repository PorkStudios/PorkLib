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
import net.daporkchop.lib.gui.component.state.ElementState;
import net.daporkchop.lib.gui.util.handler.StateListener;
import net.daporkchop.lib.gui.util.math.BoundingBox;
import net.daporkchop.lib.gui.util.math.Constraint;

import java.util.StringJoiner;
import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
public interface Element<Impl extends Element, State extends ElementState<? extends Element, State>> {
    String getName();

    default String getQualifiedName() {
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

    @SuppressWarnings("unchecked")
    default Impl considerUpdate()   {
        this.getParent().considerUpdate();
        return (Impl) this;
    }

    //visual things
    boolean isVisible();
    Impl setVisible(boolean state);

    default Impl show() {
        return this.setVisible(true);
    }

    default Impl hide() {
        return this.setVisible(false);
    }

    //state things
    State getState();
    Impl addStateListener(@NonNull String name, @NonNull StateListener<Impl, State> listener);
    default Impl addStateListener(@NonNull StateListener<Impl, State> listener) {
        return this.addStateListener(String.format("%s@%d", listener.getClass().getCanonicalName(), System.identityHashCode(listener)), listener);
    }
    default Impl addStateListener(@NonNull String name, @NonNull State state, @NonNull Runnable callback)   {
        return this.addStateListener(name, s -> {
            if (state == s) {
                callback.run();
            }
        });
    }
    default Impl addStateListener(@NonNull State state, @NonNull Runnable callback)   {
        return this.addStateListener(String.format("%s@%d", callback.getClass().getCanonicalName(), System.identityHashCode(callback)), s -> {
            if (state == s) {
                callback.run();
            }
        });
    }
    @SuppressWarnings("unchecked")
    default Impl addVisibilityListener(@NonNull Consumer<Boolean> callback)  {
        return this.addStateListener(String.format("%s@%d", callback.getClass().getCanonicalName(), System.identityHashCode(callback)), new StateListener<Impl, State>() {
            protected boolean visible = Element.this.isVisible();

            @Override
            public void onStateChange(@NonNull State state) {
                if (state.isVisible() != this.visible) {
                    callback.accept(this.visible = state.isVisible());
                }
            }
        });
    }
    default Impl addVisibleListener(@NonNull Runnable callback)  {
        return this.addVisibilityListener(visible -> {
            if (visible)    {
                callback.run();
            }
        });
    }
    default Impl addInvisibleListener(@NonNull Runnable callback)  {
        return this.addVisibilityListener(visible -> {
            if (!visible)   {
                callback.run();
            }
        });
    }
    Impl removeStateListener(@NonNull String name);

    //position things
    default Impl setConstraint(@NonNull Constraint constraint)  {
        return this.setBounds(this.getBounds().set(constraint));
    }

    Impl setBounds(@NonNull BoundingBox bounds);

    //other
    /**
     * Releases all resources associated with this element
     */
    void release();
}
