package net.daporkchop.lib.logging.console;

import lombok.NonNull;

/**
 * @author DaPorkchop_
 */
public interface Console {
    void setTitle(@NonNull String title);

    void setTextColor(int color);
}
