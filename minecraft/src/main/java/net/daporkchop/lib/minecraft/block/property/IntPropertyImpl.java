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
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.minecraft.block.BlockState;
import net.daporkchop.lib.minecraft.block.Property;
import net.daporkchop.lib.minecraft.block.PropertyMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * Default implementation of {@link Property.Int}.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public class IntPropertyImpl implements Property.Int {
    @Getter
    protected final String name;
    protected final int min;
    protected final int max;

    public IntPropertyImpl(@NonNull String name, int min, int max) {
        this.name = name.intern();
        this.min = notNegative(min, "min");
        checkArg(min < max, "min (%d) must be less than max (%d)", min, max);
        this.max = max;
    }

    @Override
    public IntStream intValues() {
        return IntStream.range(this.min, this.max);
    }

    @Override
    public PropertyMap<Integer> propertyMap(@NonNull Function<Integer, BlockState> mappingFunction) {
        BlockState[] states = new BlockState[this.max - this.min];
        for (int i = this.min; i < this.max; i++)  {
            checkState((states[i - this.min] = mappingFunction.apply(i)) != null, "state may not be null!");
        }
        return new PropertyMapImpl(states, this.min);
    }

    @Override
    public String encodeValue(@NonNull Integer value) {
        return String.valueOf(value);
    }

    @Override
    public Integer decodeValue(@NonNull String encoded) {
        return Integer.parseUnsignedInt(encoded);
    }

    @Override
    public String toString() {
        return this.name;
    }

    @RequiredArgsConstructor
    protected static class PropertyMapImpl implements PropertyMap.Int {
        @NonNull
        protected final BlockState[] states;
        protected final int offset;

        @Override
        public BlockState getState(int value) {
            value -= this.offset;
            checkArg(value >= 0 && value < this.states.length, value);
            return this.states[value];
        }
    }
}
