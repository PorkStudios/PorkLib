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

package net.daporkchop.lib.gui.swing;

import lombok.NonNull;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.component.type.Button;
import net.daporkchop.lib.gui.impl.AbstractComponent;
import net.daporkchop.lib.gui.swing.type.SwingButton;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
public interface SwingContainer extends Container<SwingComponent> {
    Map<String, SwingComponent> getComponentMap();

    @Override
    default Collection<SwingComponent> getChildren() {
        return this.getComponentMap().values();
    }

    @Override
    @SuppressWarnings("unchecked")
    default <T extends SwingComponent> T getComponent(@NonNull String name) {
        return (T) this.getComponentMap().get(name);
    }

    @Override
    default SwingButton addButton(@NonNull String name) {
        if (this.getComponentMap().containsKey(name))   {
            throw new IllegalStateException(String.format("Already have child named: %s", name));
        }
        SwingButton button = new SwingButton(name);
        this.getComponentMap().put(name, button);
        return button;
    }
}
