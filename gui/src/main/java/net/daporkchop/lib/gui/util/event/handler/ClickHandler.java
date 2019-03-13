package net.daporkchop.lib.gui.util.event.handler;

/**
 * @author DaPorkchop_
 */
@FunctionalInterface
public interface ClickHandler {
    void onClick(int mouseButton, int x, int y);
}
