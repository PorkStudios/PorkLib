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

package net.daporkchop.lib.compat.update;

import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.misc.Tuple;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Builder for {@link DataUpdater}.
 *
 * @author DaPorkchop_
 */
@Setter
@Accessors(fluent = true, chain = true)
public class DataUpdaterBuilder<D, V extends Comparable<? super V>, P> {
    protected final Map<String, Tuple<V, BiFunction<D, P, D>>> names = new HashMap<>();

    @NonNull
    protected Function<D, V> versionExtractor = data -> {
        throw new UnsupportedOperationException();
    };
    @NonNull
    protected BiFunction<D, V, D> versionReplacer = (data, version) -> {
        throw new UnsupportedOperationException();
    };

    public DataUpdaterBuilder<D, V, P> add(@NonNull String name, @NonNull V version, @NonNull BiFunction<D, P, D> updater) {
        checkState(this.names.putIfAbsent(name, new Tuple<>(version, updater)) == null, "duplicate updater name: %s", name);
        return this;
    }

    public DataUpdaterBuilder<D, V, P> replace(@NonNull String name, @NonNull BiFunction<D, P, D> updater) {
        checkState(this.names.computeIfPresent(name, (n, t) -> new Tuple<>(t.getA(), updater)) != null, "unknown updater name: %s", name);
        return this;
    }

    public DataUpdaterBuilder<D, V, P> remove(@NonNull String name) {
        checkState(this.names.remove(name) != null, "unknown updater name: %s", name);
        return this;
    }

    public DataUpdater<D, V, P> build() {
        NavigableMap<V, BiFunction<D, P, D>> updaters = new TreeMap<>();
        this.names.forEach((name, tuple) -> updaters.put(tuple.getA(), tuple.getB()));
        return new ImplDataUpdater<>(updaters, this.versionExtractor, this.versionReplacer);
    }
}
