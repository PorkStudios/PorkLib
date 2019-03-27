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
