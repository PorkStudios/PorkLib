package net.daporkchop.lib.common.function;

import java.io.IOException;
import java.util.function.Function;

/**
 * @author DaPorkchop_
 */
@FunctionalInterface
public interface IOEFunction<T, R> extends Function<T, R> {
    @Override
    default R apply(T t) {
        try {
            return this.applyThrowing(t);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    R applyThrowing(T t) throws IOException;
}
