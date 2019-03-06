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
import net.daporkchop.lib.gui.component.NestedContainer;
import net.daporkchop.lib.gui.component.capability.IconHolder;
import net.daporkchop.lib.gui.component.capability.Resizable;
import net.daporkchop.lib.gui.component.capability.TextHolder;
import net.daporkchop.lib.gui.util.event.EventManager;

import java.util.StringJoiner;

/**
 * The root element of any GUI. A window contains every component in the gui, and is also the only valid
 * implementation of {@link net.daporkchop.lib.gui.component.Element} that doesn't inherit from
 * {@link net.daporkchop.lib.gui.component.Component}.
 *
 * @author DaPorkchop_
 */
public interface Window extends Container<Window>, Resizable<Window> {
    String getTitle();

    Window setTitle(@NonNull String title);

    EventManager getEventManager();

    @Override
    default Container getParent() {
        return null;
    }

    @Override
    default Window considerUpdate() {
        return this.isVisible() ? this.update() : this;
    }

    //convenience methods
    @SuppressWarnings("unchecked")
    default <T extends Component> T getComponent(@NonNull String qualifiedName) {
        if (qualifiedName.contains(".")) {
            String[] split = qualifiedName.split("\\.");
            Component currentChild = null;
            for (int i = 0; i < split.length; i++) {
                if (currentChild == null && i > 0) {
                    StringJoiner joiner = new StringJoiner(".");
                    for (int j = 0; j <= i; j++) {
                        joiner.add(split[j]);
                    }
                    throw new IllegalArgumentException(String.format("Unable to locate element with name: %s", joiner.toString()));
                }
                if (i == 0) {
                    currentChild = this.getComponent(split[i]);
                } else if (!(currentChild instanceof NestedContainer)) {
                    StringJoiner joiner = new StringJoiner(".");
                    for (int j = 0; j <= i; j++) {
                        joiner.add(split[j]);
                    }
                    throw new IllegalStateException(String.format("Invalid element type at %s: expected %s but found %s!", joiner.toString(), NestedContainer.class.getCanonicalName(), currentChild.getClass().getCanonicalName()));
                } else {
                    currentChild = ((NestedContainer) currentChild).getChild(split[i]);
                }
            }
            return (T) currentChild;
        } else {
            return this.getChild(qualifiedName);
        }
    }

    default Window removeComponent(@NonNull String qualifiedName) {
        return this.removeComponent(qualifiedName, true);
    }

    default Window removeComponent(@NonNull String qualifiedName, boolean update) {
        if (qualifiedName.isEmpty()) {
            throw new IllegalArgumentException("Cannot remove window!");
        }
        Component component = this.getComponent(qualifiedName);
        component.getParent().removeChild(component.getName(), update);
        return this;
    }

    default Window setText(@NonNull String qualifiedName, String text) {
        Component component = this.getComponent(qualifiedName);
        if (component == null) {
            throw new IllegalArgumentException(String.format("Unknown component name: %s", qualifiedName));
        } else if (!(component instanceof TextHolder)) {
            throw new IllegalStateException(String.format("Component %s doesn't have text!", qualifiedName));
        } else {
            ((TextHolder) component).setText(text);
        }
        return this;
    }

    default Window setTooltip(@NonNull String qualifiedName, String tooltip) {
        Component component = this.getComponent(qualifiedName);
        if (component == null) {
            throw new IllegalArgumentException(String.format("Unknown component name: %s", qualifiedName));
        } else {
            component.setTooltip(tooltip);
        }
        return this;
    }

    //other
}
