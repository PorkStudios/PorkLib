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
import net.daporkchop.lib.gui.component.type.Window;
import net.daporkchop.lib.gui.util.math.BoundingBox;
import net.daporkchop.lib.gui.util.math.ComponentUpdater;
import net.daporkchop.lib.gui.util.math.Constraint;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(chain = true)
public abstract class AbstractWindow<Comp extends Component> extends AbstractComponent<Window<Comp>> implements Window<Comp> {
    protected String title = "";
    protected boolean disposed = false;
    protected boolean resizable = true;
    @Setter
    protected VoidFunction closeHandler = this::dispose;
    @Getter(AccessLevel.PROTECTED)
    protected final Map<String, Comp> componentMap = new HashMap<>();
    protected final GuiSystem system;

    public AbstractWindow(@NonNull GuiSystem<Window<Comp>> system) {
        super("");
        this.system = system;
    }

    @Override
    protected void finalize() throws Throwable {
        if (!this.disposed) {
            this.dispose();
        }
    }

    @Override
    public Window<Comp> setUpdater(@NonNull ComponentUpdater<Window<Comp>> updater) {
        return this.getThis();
    }
}
