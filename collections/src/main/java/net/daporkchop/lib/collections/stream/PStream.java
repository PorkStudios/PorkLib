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

package net.daporkchop.lib.collections.stream;

import lombok.NonNull;
import net.daporkchop.lib.collections.util.BaseCollection;
import net.daporkchop.lib.common.function.io.IOConsumer;

import java.util.function.Consumer;

/**
 * A simplification of {@link java.util.stream.Stream}.
 *
 * @param <V> the type to be used as a value
 * @author DaPorkchop_
 */
//TODO: more methods here, and add a few primitive stream types!
public interface PStream<V> extends BaseCollection {
    /**
     * Returns (an estimation of) the size required this stream.
     *
     * @return the number of elements in this stream
     */
    @Override
    long size();

    /**
     * Returns a stream over the same data that supports concurrent operations.
     * <p>
     * If this stream is already concurrent, it will return itself.
     *
     * @return a stream over the same data that supports concurrent operations
     */
    PStream<V> concurrent();

    /**
     * Iterates over all values in the stream, passing them as parameters to the given function.
     *
     * @param consumer the function to run
     */
    void forEach(@NonNull Consumer<V> consumer);

    /**
     * Iterates over all values in the stream, passing them as parameters to the given function.
     *
     * @param consumer the function to run
     * @return this stream
     */
    default PStream<V> forEachAndContinue(@NonNull Consumer<V> consumer) {
        this.forEach(consumer);
        return this;
    }

    /**
     * Convenience method to allow passing lambdas that throw an IOException without explicitly casting to {@link IOConsumer}
     *
     * @see #forEach(Consumer)
     */
    default void forEachIO(@NonNull IOConsumer<V> consumer) {
        this.forEach(consumer);
    }

    /**
     * Convenience method to allow passing lambdas that throw an IOException without explicitly casting to {@link IOConsumer}
     *
     * @see #forEachAndContinue(Consumer)
     */
    default PStream<V> forEachIOAndContinue(@NonNull IOConsumer<V> consumer) {
        this.forEach(consumer);
        return this;
    }
}
