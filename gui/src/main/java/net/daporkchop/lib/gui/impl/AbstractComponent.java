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

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.gui.component.Component;
import net.daporkchop.lib.gui.component.Container;
import net.daporkchop.lib.gui.util.math.BoundingBox;
import net.daporkchop.lib.gui.util.math.ComponentUpdater;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public abstract class AbstractComponent<Impl extends Component> implements Component<Impl> {
    protected BoundingBox bounds;
    protected ComponentUpdater<Impl> updater;
    @NonNull
    protected final String name;
    @NonNull
    protected String text = "";
    @NonNull
    protected String tooltip = "";
    protected boolean visible = false;

    @Override
    @SuppressWarnings("unchecked")
    public Impl update(@NonNull Container parent) {
        if (this.updater == null)   {
            throw new IllegalStateException("Dimension calculator is not set!");
        } else {
            this.bounds = this.updater.update(parent.getBounds(), parent, (Impl) this);
        }
        return (Impl) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Impl setUpdater(@NonNull ComponentUpdater<Impl> updater) {
        this.updater = updater;
        return (Impl) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Impl setText(@NonNull String text) {
        this.text = text;
        return (Impl) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Impl setTooltip(@NonNull String tooltip) {
        this.tooltip = tooltip;
        return (Impl) this;
    }
}
