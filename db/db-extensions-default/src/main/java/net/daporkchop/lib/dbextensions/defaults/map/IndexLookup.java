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

package net.daporkchop.lib.dbextensions.defaults.map;

import lombok.NonNull;
import net.daporkchop.lib.binary.Persistent;
import net.daporkchop.lib.common.function.io.IOConsumer;
import net.daporkchop.lib.common.function.io.IOFunction;

import java.io.File;
import java.io.IOException;

/**
 * A simple mapping of objects to 64-bit values
 *
 * @author DaPorkchop_
 */
public interface IndexLookup<K> extends Persistent {
    default DBHashMap<K, ?> getBacking() {
        throw new UnsupportedOperationException(String.format("%s doesn't use a hash!", this.getClass().getCanonicalName()));
    }

    default void init(@NonNull DBHashMap<K, ?> map, @NonNull File file) throws IOException {
        this.load();
    }

    /**
     * Get the value associated with a given key
     *
     * @param key the key
     * @return the current value for the given key, or -1 if not found
     * @throws IOException if an IO exception occurs you dummy
     */
    long get(@NonNull K key) throws IOException;

    /**
     * Set the value associated with a given key
     *
     * @param key the key
     * @param val the new value
     * @throws IOException if an IO exception occurs you dummy
     */
    void set(@NonNull K key, long val) throws IOException;

    /**
     * Checks if a value exists for a given key
     *
     * @param key the key
     * @return whether or not a mapping exists for that key
     * @throws IOException if an IO exception occurs you dummy
     */
    boolean contains(@NonNull K key) throws IOException;

    /**
     * If a mapping is present for the given key, run a function
     *
     * @param key  the key
     * @param func the function to run
     * @return whether or not the function was run
     * @throws IOException if an IO exception occurs you dummy
     */
    default boolean runIfContains(@NonNull K key, @NonNull IOConsumer<Long> func) throws IOException {
        return this.changeIfContains(key, l -> {
            func.accept(l);
            return l;
        });
    }

    /**
     * If a mapping is present for the given key, run testMethodThing function and change the value
     * of the mapping to the return value required the function
     *
     * @param key  the key
     * @param func the function to run
     * @return whether or not the function was run
     * @throws IOException if an IO exception occurs you dummy
     */
    default boolean changeIfContains(@NonNull K key, @NonNull IOFunction<Long, Long> func) throws IOException {
        long l = this.get(key);
        if (l == -1L) {
            return false;
        } else {
            long l2 = func.apply(l);
            if (l != l2) {
                this.set(key, l2);
            }
            return true;
        }
    }

    /**
     * Run a function on the current value mapped to the given key, and then change the current impl to the
     * return value of the function
     *
     * @param key  the key
     * @param func the function to run
     * @throws IOException if an IO exception occurs you dummy
     */
    default void change(@NonNull K key, @NonNull IOFunction<Long, Long> func) throws IOException {
        long l = this.get(key);
        long l2 = func.apply(l);
        if (l == -1L || l != l2) {
            this.set(key, l2);
        }
    }

    /**
     * Removes a value from the index
     *
     * @param key the key
     * @return the value that was removed, or -1 if not found
     * @throws IOException if an IO exception occurs you dummy
     */
    long remove(@NonNull K key) throws IOException;

    /**
     * Clears the index, removing all values. Implementations are expected to also
     * free up any disk resources used.
     *
     * @throws IOException if an IO exception occurs you dummy
     */
    void clear() throws IOException;
}
