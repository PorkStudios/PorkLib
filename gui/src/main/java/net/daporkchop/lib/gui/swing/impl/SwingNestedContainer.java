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
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.NestedContainer;
import net.daporkchop.lib.gui.component.state.ElementState;
import net.daporkchop.lib.gui.util.math.BoundingBox;

import javax.swing.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
@Getter
public abstract class SwingNestedContainer<Impl extends NestedContainer, Swing extends JComponent, State extends ElementState<Impl, State>> extends SwingComponent<Impl, Swing, State> implements NestedContainer<Impl, State>, IBasicSwingContainer<Impl, Swing, State> {
    protected final Map<String, Component> children = Collections.synchronizedMap(new HashMap<>());

    public SwingNestedContainer(String name, Swing swing) {
        super(name, swing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Impl update() {
        super.update();
        this.children.forEach((name, element) -> element.update());
        return (Impl) this;
    }
}
