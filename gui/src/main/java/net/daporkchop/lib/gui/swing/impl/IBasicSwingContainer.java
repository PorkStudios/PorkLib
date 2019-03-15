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

package net.daporkchop.lib.gui.swing.impl;

import lombok.NonNull;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.component.capability.ComponentAdder;
import net.daporkchop.lib.gui.component.state.ElementState;
import net.daporkchop.lib.gui.component.type.container.Panel;
import net.daporkchop.lib.gui.component.type.functional.Button;
import net.daporkchop.lib.gui.component.type.functional.Label;
import net.daporkchop.lib.gui.swing.type.container.SwingPanel;
import net.daporkchop.lib.gui.swing.type.functional.SwingButton;
import net.daporkchop.lib.gui.swing.type.functional.SwingLabel;

import javax.swing.*;

/**
 * @author DaPorkchop_
 */
@SuppressWarnings("unchecked")
public interface IBasicSwingContainer<Impl extends Container, Swing extends java.awt.Container, State extends ElementState<Impl, State>> extends Container<Impl, State> {
    //componentadder methods
    @Override
    default Button button(@NonNull String name) {
        SwingButton button = new SwingButton(name);
        this.addChild(button);
        return button;
    }

    @Override
    default Label label(@NonNull String name) {
        SwingLabel label = new SwingLabel(name);
        this.addChild(label);
        return label;
    }

    @Override
    default Panel panel(@NonNull String name) {
        SwingPanel panel = new SwingPanel(name);
        this.addChild(panel);
        return panel;
    }

    //container methods
    @Override
    default Impl addChild(@NonNull Component child, boolean update) {
        if (!(child instanceof SwingComponent))    {
            throw new IllegalArgumentException(String.format("Invalid child type! Expected %s but found %s!", SwingComponent.class.getCanonicalName(), child.getClass().getCanonicalName()));
        } else if (this.getChildren().containsKey(child.getName()))  {
            throw new IllegalArgumentException(String.format("Child with name %s exists!", child.getName()));
        }
        SwingComponent swing = (SwingComponent) child;
        this.getChildren().put(child.getName(), swing.setParent(this));
        this.getSwing().add(swing.swing);
        return update ? this.update() : (Impl) this;
    }

    @Override
    default Impl removeChild(@NonNull String name, boolean update) {
        SwingComponent removed = (SwingComponent) this.getChildren().remove(name);
        if (removed == null) {
            throw new IllegalArgumentException(String.format("No such child: %s", name));
        } else {
            this.getSwing().remove(removed.swing);
            return update ? this.update() : (Impl) this;
        }
    }

    //other
    Swing getSwing();
}