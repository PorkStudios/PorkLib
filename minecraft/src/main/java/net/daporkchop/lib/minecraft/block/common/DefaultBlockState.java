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

package net.daporkchop.lib.minecraft.block.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.minecraft.block.BlockRegistry;
import net.daporkchop.lib.minecraft.block.BlockState;
import net.daporkchop.lib.minecraft.block.Property;
import net.daporkchop.lib.minecraft.block.PropertyMap;
import net.daporkchop.lib.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.common.util.PorkUtil.*;

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
    protected final BlockRegistry registry;
    @NonNull
    protected final Identifier id;
    protected final int legacyId;
    protected final int meta;
    protected final int runtimeId;

    @Getter(AccessLevel.PROTECTED)
    protected BlockState[] otherMeta;
    @Getter(AccessLevel.PROTECTED)
    protected final Map<Property<?>, PropertyMap<?>> otherProperties = new IdentityHashMap<>(); //TODO: use a map that's better optimized for low entry counts

    @Getter(AccessLevel.PROTECTED)
    protected Map<String, Property<?>> propertiesByName;

    @Override
    public boolean hasLegacyId() {
        return this.legacyId >= 0;
    }

    @Override
    public BlockState withMeta(int meta) {
        BlockState[] otherMeta = this.otherMeta;
        BlockState state = null;
        checkArg(meta >= 0 && meta < otherMeta.length && (state = otherMeta[meta]) != null, meta);
        return state;
    }

    @Override
    public <V> BlockState withProperty(@NonNull Property<V> property, @NonNull V value) {
        PropertyMap<V> map = uncheckedCast(this.otherProperties.get(property));
        checkArg(map != null, "Invalid property %s for block %s!", property, this.id);
        return map.getState(value);
    }

    @Override
    public BlockState withProperty(@NonNull Property.Int property, int value) {
        PropertyMap.Int map = uncheckedCast(this.otherProperties.get(property));
        checkArg(map != null, "Invalid property %s for block %s!", property, this.id);
        return map.getState(value);
    }

    @Override
    public BlockState withProperty(@NonNull Property.Boolean property, boolean value) {
        PropertyMap.Boolean map = uncheckedCast(this.otherProperties.get(property));
        checkArg(map != null, "Invalid property %s for block %s!", property, this.id);
        return map.getState(value);
    }

    @Override
    public <T> Property<T> property(@NonNull String name) {
        Property<T> property = uncheckedCast(this.propertiesByName.get(name));
        checkArg(property != null, "Invalid property %s for block %s!", name, this.id);
        return property;
    }

    @Override
    public Collection<Property<?>> properties() {
        return uncheckedCast(Collections.unmodifiableSet(this.propertiesByName.keySet()));
    }

    @Override
    public String toString() {
        return this.id.toString() + '#' + this.meta;
    }
}
