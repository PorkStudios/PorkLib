/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
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

package net.daporkchop.lib.compat.datafix;

import lombok.NonNull;

/**
 * A completed datafix configuration.
 *
 * @param <D> the type of the encoded data
 * @param <O> the type of the decoded value
 * @param <V> the type of version used to identify the processor chain required to handle data at a given version
 * @author DaPorkchop_
 */
public final class DataFixer<D, O, V extends Comparable<V>> {
    /**
     * Decodes the given data.
     * <p>
     * This will use the next available codec in the chain, keeping the number of converters that must process the data to a minimum.
     *
     * @param data        the data to decode. May be modified
     * @param dataVersion the version that the data was encoded at
     * @return the decoded value
     */
    public O decode(@NonNull D data, @NonNull V dataVersion) {
        return null;
    }

    /**
     * Decodes the given data.
     * <p>
     * This will attempt to decode the data to exactly the target version, regardless of difficulty.
     *
     * @param data          the data to decode. May be modified
     * @param dataVersion   the version that the data was encoded at
     * @param targetVersion the version at which the data should be decoded
     * @return the decoded value
     * @throws IllegalArgumentException if the given data version is newer than the target version
     * @throws IllegalArgumentException if no suitable codec for the given target version could be found
     */
    public O decode(@NonNull D data, @NonNull V dataVersion, @NonNull V targetVersion) {
        return null;
    }

    /**
     * Upgrades the given data to the given target version without decoding it.
     *
     * @param data          the data to upgrade. May be modified
     * @param dataVersion   the version that the data was encoded at
     * @param targetVersion the version to which the data should be upgraded
     * @return the upgraded data
     * @throws IllegalArgumentException if the given data version is newer than the target version
     */
    public D upgrade(@NonNull D data, @NonNull V dataVersion, @NonNull V targetVersion) {
        return data;
    }
}
