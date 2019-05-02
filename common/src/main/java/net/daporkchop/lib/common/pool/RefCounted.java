/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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

package net.daporkchop.lib.common.pool;

/**
 * A type that allows reference counting to check if it should be released.
 *
 * @author DaPorkchop_
 */
public interface RefCounted<Impl extends RefCounted> {
    /**
     * Gets the current reference count on this instance.
     *
     * @return this instance's current reference count
     */
    int refCount();

    /**
     * Retains this instance by incrementing its reference count by 1.
     *
     * @return this instance
     */
    Impl retain();

    /**
     * Retains this instance multiple times be incrementing its reference count by the given amount.
     *
     * @param amount the amount to increment the reference count by
     * @return this instance
     * @throws IllegalArgumentException if the amount is less than 0
     */
    Impl retain(int amount);

    /**
     * Releases this instance by decrementing its reference count by 1.
     * <p>
     * Once the reference count reaches 0, this instance will be released and using any of its methods
     * or fields should be considered unsafe.
     *
     * @return this instance
     */
    Impl release();

    /**
     * Releases this instance multiple times be decrementing its reference count by the given amount.
     *
     * @param amount the amount to decrement the reference count by
     * @return this instance
     * @throws IllegalArgumentException if the amount is less than 0
     */
    Impl release(int amount);

    /**
     * Releases this instance immediately, regardless of current reference count.
     * <p>
     * This method should only be used internally unless you know EXACTLY what you're doing.
     */
    @Deprecated
    void immediatelyRelease();
}
