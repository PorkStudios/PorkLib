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

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.component.Element;
import net.daporkchop.lib.gui.component.SubElement;
import net.daporkchop.lib.gui.component.type.Button;
import net.daporkchop.lib.gui.swing.type.SwingButton;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
@Getter
@SuppressWarnings("unchecked")
public abstract class SwingContainer<Impl extends Container, Swing extends java.awt.Container> extends SwingElement<Impl, Swing> implements Container<Impl> {
    protected final Map<String, SubElement> children = Collections.synchronizedMap(new HashMap<>());

    public SwingContainer(String name, Swing swing) {
        super(name, swing);
    }

    @Override
    public Impl addChild(@NonNull SubElement child, boolean update) {
        if (!(child instanceof SwingSubElement))    {
            throw new IllegalArgumentException(String.format("Invalid child type! Expected %s but found %s!", SwingSubElement.class.getCanonicalName(), child.getClass().getCanonicalName()));
        }else if (this.children.containsKey(child.getName()))  {
            throw new IllegalArgumentException(String.format("Child with name %s exists!", child.getName()));
        }
        SwingSubElement swing = (SwingSubElement) child;
        this.children.put(child.getName(), swing.setParent(this));
        this.swing.add(swing.swing);
        return update ? this.update() : (Impl) this;
    }

    @Override
    public Impl removeChild(@NonNull String name, boolean update) {
        if (this.children.remove(name) == null) {
            throw new IllegalArgumentException(String.format("No such child: %s", name));
        } else {
            return update ? this.update() : (Impl) this;
        }
    }

    @Override
    public Button button(@NonNull String name) {
        SwingButton button = new SwingButton(name);
        this.addChild(button);
        return button;
    }
}
