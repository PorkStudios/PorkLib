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

package net.daporkchop.lib.minecraft.format.common.block;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.minecraft.block.BlockState;
import net.daporkchop.lib.minecraft.block.Property;
import net.daporkchop.lib.minecraft.block.PropertyMap;
import net.daporkchop.lib.minecraft.format.common.block.legacy.LegacyBlockRegistry;
import net.daporkchop.lib.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of {@link BlockState}, used by {@link AbstractBlockRegistry}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class DefaultBlockState implements BlockState {
    @NonNull
    protected final LegacyBlockRegistry registry;
    @NonNull
    protected final Identifier id;
    protected final int legacyId;
    protected final int meta;
    protected final int runtimeId;

    protected final Map<Property<?>, PropertyMap<?>> otherProperties = new HashMap<>(); //TODO: use a map that's better optimized for low entry counts

    @Override
    public BlockState withMeta(int meta) {
        return null;
    }

    @Override
    public <V> BlockState withProperty(@NonNull Property<V> property, @NonNull V value) {
        return null;
    }

    @Override
    public BlockState withProperty(@NonNull Property.Int property, int value) {
        return null;
    }

    @Override
    public BlockState withProperty(@NonNull Property.Boolean property, boolean value) {
        return null;
    }
}
