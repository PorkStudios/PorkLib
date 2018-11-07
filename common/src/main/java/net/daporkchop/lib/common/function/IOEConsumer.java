package net.daporkchop.lib.common.function;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * @author DaPorkchop_
 */
@FunctionalInterface
public interface IOEConsumer<T> extends Consumer<T> {
    @Override
    default void accept(T t) {
        try {
            this.acceptThrowing(t);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void acceptThrowing(T t) throws IOException;
}
