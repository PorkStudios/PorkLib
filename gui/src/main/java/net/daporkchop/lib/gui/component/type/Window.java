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

package net.daporkchop.lib.gui.component.type;

import lombok.NonNull;
import net.daporkchop.lib.imaging.bitmap.PIcon;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.component.NestedContainer;
import net.daporkchop.lib.gui.component.capability.SimpleIconHolder;
import net.daporkchop.lib.gui.component.capability.Resizable;
import net.daporkchop.lib.gui.component.capability.TextHolder;
import net.daporkchop.lib.gui.component.state.WindowState;
import net.daporkchop.lib.gui.util.math.BoundingBox;

import java.util.StringJoiner;

/**
 * The root element of any GUI. A window contains every component in the gui, and is also the only valid
 * implementation of {@link net.daporkchop.lib.gui.component.Element} that doesn't inherit from
 * {@link net.daporkchop.lib.gui.component.Component}.
 *
 * @author DaPorkchop_
 */
public interface Window extends Container<Window, WindowState>, Resizable<Window>, SimpleIconHolder<Window, WindowState> {
    String getTitle();
    Window setTitle(@NonNull String title);

    @Override
    default Container getParent() {
        return null;
    }

    @Override
    default Window considerUpdate() {
        return this.isVisible() ? this.update() : this;
    }

    default Window setIcon(@NonNull PIcon... icons)    {
        if (icons.length == 0)  {
            throw new IllegalArgumentException("Arguments may not be empty!");
        }
        int max = Integer.MIN_VALUE;
        PIcon maxI = null;
        for (int i = icons.length - 1; i >= 0; i--) {
            if (icons[i] == null)   {
                throw new NullPointerException();
            }
            PIcon icon = icons[i];
            if (icon.width() == 0 || icon.height() == 0 || icon.width() != icon.height())  {
                throw new IllegalArgumentException("Icon must be square!");
            } else if (icon.width() > max)   {
                max = icon.width();
                maxI = icon;
            }
        }
        return this.setIcon(maxI);
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

    //hierarchy stuff
    Window getParentWindow();
    default Window popup(int width, int height) {
        return this.popup(new BoundingBox(0, 0, width, height));
    }
    default Window popup(int x, int y, int width, int height) {
        return this.popup(new BoundingBox(x, y, width, height));
    }
    Window popup(@NonNull BoundingBox bounds);

    //other
}
