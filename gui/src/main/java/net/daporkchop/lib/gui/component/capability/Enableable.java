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

package net.daporkchop.lib.gui.component.capability;

import lombok.NonNull;
import net.daporkchop.lib.gui.component.Element;
import net.daporkchop.lib.gui.component.state.ElementState;
import net.daporkchop.lib.gui.util.handler.StateListener;

import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
public interface Enableable<Impl extends Element> {
    boolean isEnabled();
    Impl setEnable(boolean enabled);

    default Impl enable() {
        return this.setEnable(true);
    }

    default Impl disable() {
        return this.setEnable(false);
    }

    default Impl toggle() {
        return this.setEnable(!this.isEnabled());
    }

    @SuppressWarnings("unchecked")
    default Impl addEnableListener(@NonNull Consumer<Boolean> callback)  {
        return (Impl) ((Impl) this).addStateListener(String.format("%s@%d", callback.getClass().getCanonicalName(), System.identityHashCode(callback)), new StateListener() {
            protected boolean enabled = Enableable.this.isEnabled();

            @Override
            public void onStateChange(@NonNull ElementState state) {
                if (state.isEnabled() != this.enabled) {
                    callback.accept(this.enabled = state.isEnabled());
                }
            }
        });
    }
    default Impl addEnableListener(@NonNull Runnable callback)  {
        return this.addEnableListener(enabled -> {
            if (enabled)    {
                callback.run();
            }
        });
    }
    default Impl addDisableListener(@NonNull Runnable callback)  {
        return this.addEnableListener(enabled -> {
            if (!enabled)   {
                callback.run();
            }
        });
    }
}
