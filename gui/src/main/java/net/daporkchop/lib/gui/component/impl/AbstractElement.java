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

package net.daporkchop.lib.gui.component.impl;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.gui.component.Element;
import net.daporkchop.lib.gui.component.state.ElementState;
import net.daporkchop.lib.gui.util.handler.StateListener;
import net.daporkchop.lib.gui.util.math.BoundingBox;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@SuppressWarnings("unchecked")
public abstract class AbstractElement<Impl extends Element, State extends ElementState<? extends Element, State>> implements Element<Impl, State> {
    @NonNull
    protected final String name;

    protected BoundingBox bounds;

    private State prevState;
    protected final Map<String, StateListener<Impl, State>> stateListeners = new LinkedHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public Impl update() {
        if (this.prevState == null) {
            this.fireStateChange();
        }
        return (Impl) this;
    }

    public boolean fireStateChange()    {
        State state = this.getState();
        if (state != this.prevState)  {
            this.prevState = state;
            this.stateListeners.forEach((name, listener) -> listener.onStateChange(state));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Impl addStateListener(@NonNull String name, @NonNull StateListener<Impl, State> listener) {
        if (this.stateListeners.putIfAbsent(name, listener) != null)    {
            throw new IllegalArgumentException(String.format("Listener name \"%s\" is already occupied!", name));
        }
        return (Impl) this;
    }

    @Override
    public Impl removeStateListener(@NonNull String name) {
        this.stateListeners.remove(name);
        return (Impl) this;
    }
}
