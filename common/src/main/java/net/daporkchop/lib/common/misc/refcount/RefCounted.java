/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.common.misc.refcount;

import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * A type that has a reference count which can be atomically incremented and decremented, and will be released
 * once the reference count reaches 0.
 * <p>
 * The reference count of a newly created instance is always 1.
 * <p>
 * Invoking {@link AutoCloseable#close()} on a {@link RefCounted} has the same effect as {@link #release()}.
 *
 * @author DaPorkchop_
 */
public interface RefCounted extends AutoCloseable {
    /**
     * @return the current reference count
     */
    int refCnt();

    /**
     * Retains this instance by incrementing the reference count.
     *
     * @throws AlreadyReleasedException if this instance's reference count has already reached 0
     */
    void retain() throws AlreadyReleasedException;

    /**
     * Releases this instance by decrementing the reference count.
     *
     * @return whether or not the reference count reached 0 and instance was released
     * @throws AlreadyReleasedException if this instance's reference count has already reached 0
     */
    boolean release() throws AlreadyReleasedException;

    @Override
    default void close() {
        this.release();
    }
}
