/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
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

package net.daporkchop.lib.common.misc.release;

/**
 * A variant of {@link DirectMemoryHolder} which allows external access to the underlying memory
 * block owned by this instance.
 *
 * @author DaPorkchop_
 */
@Deprecated
public interface AccessibleDirectMemoryHolder extends DirectMemoryHolder {
    /**
     * @return an object (possibly {@code null}) that is used as a relative reference
     */
    Object memoryRef();

    /**
     * @return the offset of the direct memory relative to {@link #memoryRef()}
     */
    long memoryOff();

    /**
     * Gets the total size (in bytes) of the memory block addressed by this instance.
     * <p>
     * The base address of the memory block in question may be accessed by {@link #memoryOff()}.
     * <p>
     * This method may be invoked safely (without throwing an exception) even if the memory has been
     * released, however, the results are undefined.
     *
     * @return the size of the memory block
     */
    long memorySize();
}
