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

package net.daporkchop.lib.minecraft.block.property;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.minecraft.block.BlockState;
import net.daporkchop.lib.minecraft.block.Property;
import net.daporkchop.lib.minecraft.block.PropertyMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Default implementation of {@link Property}.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public class EnumPropertyImpl implements Property<String> {
    @Getter
    protected final String name;
    protected final String[] values;

    public EnumPropertyImpl(@NonNull String name, @NonNull List<String> values) {
        this.name = name.intern();
        this.values = values.stream()
                .peek(Objects::requireNonNull)
                .map(String::intern)
                .toArray(String[]::new);
    }

    @Override
    public Stream<String> values() {
        return Arrays.stream(this.values);
    }

    @Override
    public PropertyMap<String> propertyMap(@NonNull Function<String, BlockState> mappingFunction) {
        PropertyMapImpl map = new PropertyMapImpl();
        for (String value : this.values) {
            BlockState state = mappingFunction.apply(value);
            checkState(state != null, "state may not be null!");
            map.put(value, state);
        }
        return map;
    }

    @Override
    public String encodeValue(@NonNull String value) {
        return value;
    }

    @Override
    public String decodeValue(@NonNull String encoded) {
        return encoded.intern();
    }

    @Override
    public String toString() {
        return this.name;
    }

    protected static class PropertyMapImpl extends HashMap<String, BlockState> implements PropertyMap<String> {
        @Override
        public BlockState getState(@NonNull String value) {
            BlockState state = this.get(value);
            checkArg(state != null, value);
            return state;
        }
    }
}
