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
import lombok.RequiredArgsConstructor;

import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * A function which is able to convert encoded data from an older version to a newer one.
 * <p>
 * Converters may modify the data instance which is passed to them.
 *
 * @author DaPorkchop_
 * @see DataFixer
 */
@FunctionalInterface
public interface DataConverter<D> {
    /**
     * Converts the given data to a new version.
     *
     * @param data the data to convert
     * @return the converted data. Implementations may modify and return the input data
     */
    D convert(@NonNull D data);

    default DataConverter<D> andThen(@NonNull DataConverter<D> other) {
        return new Multi<>(uncheckedCast(new DataConverter[]{
                this,
                other
        }));
    }

    /**
     * Executes a series of other {@link DataConverter}s.
     *
     * @author DaPorkchop_
     */
    @RequiredArgsConstructor
    final class Multi<D> implements DataConverter<D> {
        @NonNull
        protected final DataConverter<D>[] converters;

        @Override
        public D convert(@NonNull D data) {
            for (DataConverter<D> converter : this.converters) {
                data = converter.convert(data);
            }
            return data;
        }

        @Override
        public DataConverter<D> andThen(@NonNull DataConverter<D> other) {
            DataConverter<D>[] converters = uncheckedCast(new DataConverter[this.converters.length + 1]);
            System.arraycopy(this.converters, 0, converters, 0, this.converters.length);
            converters[this.converters.length] = other;
            return new Multi<>(converters);
        }
    }
}
