package net.daporkchop.lib.gui.component.capability;

/**
 * @author DaPorkchop_
 */
public interface Enableable<Impl> {
    boolean isEnabled();
    Impl setEnable(boolean enabled);

    default Impl enable()   {
        return this.setEnable(true);
    }

    default Impl disable()  {
        return this.setEnable(false);
    }
}
