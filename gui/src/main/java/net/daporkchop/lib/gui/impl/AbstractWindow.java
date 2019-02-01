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

package net.daporkchop.lib.gui.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.function.VoidFunction;
import net.daporkchop.lib.gui.GuiSystem;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.Window;
import net.daporkchop.lib.gui.util.Dimensions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(chain = true)
public abstract class AbstractWindow<Impl extends AbstractWindow> extends AbstractComponent<Impl> implements Window<Impl> {
    protected Dimensions dimensions;
    protected String title = "";
    protected boolean visible = false;
    protected boolean disposed = false;
    protected boolean resizeable = true;
    @Setter
    protected VoidFunction closeHandler = this::dispose;
    @Getter(AccessLevel.PROTECTED)
    protected final Map<String, Component> componentMap = new HashMap<>();
    protected final GuiSystem system;
    public AbstractWindow(@NonNull GuiSystem<Impl> system) {
        super("");
        this.system = system;
    }

    public Window show() {
        return this.setVisible(true);
    }

    public Window hide() {
        return this.setVisible(false);
    }

    @Override
    protected void finalize() throws Throwable {
        if (!this.disposed) {
            this.dispose();
        }
    }

    public Window addComponent(@NonNull String name, @NonNull Component component) {
        return this.addComponent(name, component, true);
    }

    public Window addComponent(@NonNull String name, @NonNull Component component, boolean update) {
        this.componentMap.put(name, component);
        return update ? this.update() : this;
    }

    public Collection<Component> getComponents() {
        return this.componentMap.values();
    }

    @SuppressWarnings("unchecked")
    public <T extends Component<T>> T getComponentByName(@NonNull String name) {
        return (T) this.componentMap.get(name);
    }
}
