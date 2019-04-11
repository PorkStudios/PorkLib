package net.daporkchop.lib.unsafe.capability;

import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * A variant of {@link DirectMemoryHolder} which allows external access to the underlying memory
 * block owned by this instance.
 *
 * @author DaPorkchop_
 */
public interface AccessibleDirectMemoryHolder extends DirectMemoryHolder {
    /**
     * Gets the memory address of the memory block owned by this instance.
     *
     * @return the memory address
     * @throws AlreadyReleasedException if the memory was already released
     */
    long getMemoryAddress() throws AlreadyReleasedException;

    /**
     * Gets the total size (in bytes) of the memory block addressed by this instance.
     * <p>
     * The base address of the memory block in question may be accessed by {@link #getMemoryAddress()}.
     * <p>
     * This method may be invoked safely (without throwing an exception) even if the memory has been
     * released, however, the results are undefined.
     *
     * @return the size of the memory block
     */
    long getMemorySize();
}
