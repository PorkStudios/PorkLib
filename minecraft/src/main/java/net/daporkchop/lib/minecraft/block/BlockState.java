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

package net.daporkchop.lib.minecraft.block;

import lombok.NonNull;
import net.daporkchop.lib.minecraft.util.Identifier;

import java.util.Collection;

import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * Represents a block state: the storage's {@link Identifier} combined with a metadata value.
 *
 * @author DaPorkchop_
 * @see BlockRegistry for an explanation of what all the values mean
 */
public interface BlockState {
    /**
     * @return the {@link BlockRegistry} that this block state belongs to
     */
    BlockRegistry registry();

    /**
     * @return the block's {@link Identifier}
     */
    Identifier id();

    /**
     * @return whether or not the block has a legacy ID
     * @see #legacyId()
     */
    boolean hasLegacyId();

    /**
     * @return the block's legacy ID, or {@code -1} if the block does not have a legacy ID
     * @see #hasLegacyId()
     */
    int legacyId();

    /**
     * @return the block state's metadata value
     */
    int meta();

    /**
     * @return the block state's runtime ID
     */
    int runtimeId();

    /**
     * Gets a {@link BlockState} with the same block {@link Identifier} and the given metadata value.
     *
     * @param meta the new metadata value
     * @return a {@link BlockState} with the given metadata value
     * @throws IllegalArgumentException if a state with the given metadata value was not registered for the block
     */
    BlockState withMeta(int meta);

    /**
     * Gets a {@link BlockState} with the same block {@link Identifier} and the given {@link Property} set to the given value.
     *
     * @param property the {@link Property} key to change
     * @param value    the new property value
     * @param <V>      the property's value type
     * @return a {@link BlockState} with the given {@link Property} set to the given value
     * @throws IllegalArgumentException if the given {@link Property} was not registered for the block
     * @throws IllegalArgumentException if the given {@link Property} cannot store the given value
     * @see #withProperty(Property.Int, int)
     * @see #withProperty(Property.Boolean, boolean)
     */
    <V> BlockState withProperty(@NonNull Property<V> property, @NonNull V value);

    /**
     * Gets a {@link BlockState} with the same block {@link Identifier} and the given {@link Property.Int} set to the given value.
     *
     * @param property the {@link Property.Int} key to change
     * @param value    the new property value
     * @return a {@link BlockState} with the given {@link Property.Int} set to the given value
     * @throws IllegalArgumentException if the given {@link Property.Int} was not registered for the block
     * @throws IllegalArgumentException if the given {@link Property.Int} cannot store the given value
     */
    BlockState withProperty(@NonNull Property.Int property, int value);

    /**
     * Gets a {@link BlockState} with the same block {@link Identifier} and the given {@link Property.Boolean} set to the given value.
     *
     * @param property the {@link Property.Boolean} key to change
     * @param value    the new property value
     * @return a {@link BlockState} with the given {@link Property.Boolean} set to the given value
     * @throws IllegalArgumentException if the given {@link Property.Boolean} was not registered for the block
     */
    BlockState withProperty(@NonNull Property.Boolean property, boolean value);

    /**
     * Convenience method, gets a {@link BlockState} with the same block {@link Identifier} and the property with the given name set to the given value.
     *
     * @param property the property name
     * @param value    the {@link String} representation of the new property value
     * @return a {@link BlockState} with the same block {@link Identifier} and the property with the given name set to the given value
     * @throws IllegalArgumentException if the a property with the given name was not registered for the block
     * @throws IllegalArgumentException if the property with the given name cannot parse or store the given value
     */
    default BlockState withProperty(@NonNull String property, @NonNull String value) {
        Property<?> prop = this.property(property);
        return this.withProperty(prop, uncheckedCast(prop.decodeValue(value)));
    }

    /**
     * @return all properties supported by the block
     */
    Collection<Property<?>> properties();

    /**
     * Gets the {@link Property} with the given name.
     *
     * @param name the name of the property to get
     * @param <T>  the property's value type
     * @return the {@link Property} with the given name
     * @throws IllegalArgumentException if the block does not have any properties with the given name
     */
    <T> Property<T> property(@NonNull String name);

    /**
     * Gets the {@link Property.Int} with the given name.
     *
     * @param name the name of the property to get
     * @return the {@link Property.Int} with the given name
     * @throws IllegalArgumentException if the block does not have any properties with the given name
     * @throws ClassCastException       if the property is not a {@link Property.Int}
     */
    default Property.Int propertyInt(@NonNull String name) {
        return uncheckedCast(this.property(name));
    }

    /**
     * Gets the {@link Property.Boolean} with the given name.
     *
     * @param name the name of the property to get
     * @return the {@link Property.Boolean} with the given name
     * @throws IllegalArgumentException if the block does not have any properties with the given name
     * @throws ClassCastException       if the property is not a {@link Property.Boolean}
     */
    default Property.Boolean propertyBoolean(@NonNull String name) {
        return uncheckedCast(this.property(name));
    }
}
