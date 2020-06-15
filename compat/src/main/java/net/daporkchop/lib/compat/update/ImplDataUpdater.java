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
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Default implementation of {@link DataUpdater}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class ImplDataUpdater<D, V extends Comparable<? super V>, P> implements DataUpdater<D, V, P> {
    @NonNull
    protected final NavigableMap<V, BiFunction<D, P, D>> updaters;

    @NonNull
    protected final Function<D, V> versionExtractor;
    @NonNull
    protected final BiFunction<D, V, D> versionReplacer;

    @Override
    public BiFunction<D, P, D> update(@NonNull V from, @NonNull V to) {
        int delta = from.compareTo(to);
        checkState(delta >= 0, "from (%s) may not be greater than to (%s)", from, to);
        if (delta == 0) {
            return (data, param) -> data;
        }
        List<BiFunction<D, P, D>> list = new ArrayList<>(this.updaters.subMap(from, false, to, true).values());
        return (data, param) -> {
            for (BiFunction<D, P, D> updater : list)    {
                data = updater.apply(data, param);
            }
            return this.versionReplacer.apply(data, to);
        };
    }

    @Override
    public BiFunction<D, P, D> update(@NonNull V to) {
        return (data, param) -> {
            V from = this.versionExtractor.apply(data);
            int delta = from.compareTo(to);
            checkState(delta >= 0, "from (%s) may not be greater than to (%s)", from, to);
            if (delta == 0) {
                return data;
            }

            for (BiFunction<D, P, D> updater : this.updaters.subMap(from, false, to, true).values())    {
                data = updater.apply(data, param);
            }
            return this.versionReplacer.apply(data, to);
        };
    }
}
