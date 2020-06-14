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
import net.daporkchop.lib.compat.datafix.decode.ParameterizedDecoder;
import net.daporkchop.lib.compat.datafix.encode.ParameterizedEncoder;

import java.util.Map;
import java.util.TreeMap;

/**
 * Used to configure and construct instances of {@link DataFixer}.
 *
 * @author DaPorkchop_
 */
public class DataFixerBuilder<O, D, V extends Comparable<? super V>> {
    protected final Map<V, DataConverter<D>> converters = new TreeMap<>();
    protected final Map<V, ParameterizedDecoder<O, D, V, ?>> decoders = new TreeMap<>();
    protected final Map<V, ParameterizedEncoder<O, D, V, ?>> encoders = new TreeMap<>();

    /**
     * Adds a new {@link DataConverter} which is required to convert data to the given version.
     *
     * @param version   the version which the converter is required to convert data to
     * @param converter the {@link DataConverter} to add
     * @return this builder
     */
    public synchronized DataFixerBuilder<O, D, V> addConverter(@NonNull V version, @NonNull DataConverter<D> converter) {
        this.converters.merge(version, converter, DataConverter::andThen);
        return this;
    }

    /**
     * Sets the {@link DataCodec} to use for the given version.
     * <p>
     * If a different {@link DataCodec} is already registered for the given version, it will be silently replaced.
     *
     * @param version the version to use the codec for
     * @param codec   the {@link DataCodec} to use
     * @return this builder
     */
    public synchronized DataFixerBuilder<O, D, V> addDecoder(@NonNull V version, @NonNull ParameterizedDecoder<O, D, V, ?> codec) {
        this.decoders.put(version, codec);
        return this;
    }

    /**
     * Sets the {@link DataCodec} to use for the given version.
     * <p>
     * If a different {@link DataCodec} is already registered for the given version, it will be silently replaced.
     *
     * @param version the version to use the codec for
     * @param codec   the {@link DataCodec} to use
     * @return this builder
     */
    public synchronized DataFixerBuilder<O, D, V> addEncoder(@NonNull V version, @NonNull ParameterizedEncoder<O, D, V, ?> codec) {
        this.encoders.put(version, codec);
        return this;
    }

    public synchronized DataFixer<O, D, V> build() {
        return new DataFixer<>(this.converters, this.decoders, this.encoders);
    }
}
