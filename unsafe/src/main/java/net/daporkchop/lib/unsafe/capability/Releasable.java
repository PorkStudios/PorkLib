package net.daporkchop.lib.unsafe.capability;

import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * A type that contains resources that may be manually released
 *
 * @author DaPorkchop_
 */
public interface Releasable extends AutoCloseable {
    /**
     * Releases all resources used by this instance.
     * <p>
     * After invoking this method, this instance should be treated as invalid and one should assume that
     * using any fields/methods defined by superclasses will result in undefined behavior, unless the
     * superclass implementations specifically state otherwise.
     *
     * @throws AlreadyReleasedException if the resources used by this instance have already been released
     */
    void release() throws AlreadyReleasedException;

    @Override
    default void close() {
        this.release();
    }
}
