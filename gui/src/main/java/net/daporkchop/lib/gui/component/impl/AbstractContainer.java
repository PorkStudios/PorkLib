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

package net.daporkchop.lib.gui.component.impl;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.Element;
import net.daporkchop.lib.gui.component.state.ElementState;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
@Getter
@SuppressWarnings("unchecked")
public abstract class AbstractContainer<Impl extends AbstractContainer, State extends ElementState<? extends Element, State>> extends AbstractElement<Impl, State> implements Container<Impl, State> {
    protected final Map<String, Component> children = Collections.synchronizedMap(new HashMap<>());

    public AbstractContainer(String name) {
        super(name);
    }

    @Override
    public Impl addChild(@NonNull Component child, boolean update) {
        this.children.put(child.getName(), child);
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
}
