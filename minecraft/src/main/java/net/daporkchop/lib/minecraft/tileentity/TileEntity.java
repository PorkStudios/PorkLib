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

package net.daporkchop.lib.minecraft.tileentity;

import lombok.NonNull;
import net.daporkchop.lib.logging.format.component.TextComponent;
import net.daporkchop.lib.minecraft.item.ItemMeta;
import net.daporkchop.lib.minecraft.item.inventory.Inventory;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.util.property.ObjectKey;
import net.daporkchop.lib.minecraft.util.property.Properties;
import net.daporkchop.lib.minecraft.util.property.PropertyKey;
import net.daporkchop.lib.nbt.tag.CompoundTag;

/**
 * Base representation of a tile entity.
 *
 * @author DaPorkchop_
 */
public interface TileEntity extends Properties<TileEntity> {
    Identifier ID_CHEST = Identifier.fromString("minecraft:chest");
    Identifier ID_COMMAND_BLOCK = Identifier.fromString("minecraft:command_block");
    Identifier ID_FURNACE = Identifier.fromString("minecraft:furnace");
    Identifier ID_JUKEBOX = Identifier.fromString("minecraft:jukebox");
    Identifier ID_SIGN = Identifier.fromString("minecraft:sign");

    /**
     * Any additional NBT tags attached to a tile entity that are not known by this library.
     */
    PropertyKey<CompoundTag> UNKNOWN_NBT = new ObjectKey<>("nbt_unknown");

    //general

    /**
     * The custom name of the tile entity.
     */
    PropertyKey<TextComponent> CUSTOM_NAME = new ObjectKey<>("custom_name");

    /**
     * The inventory stored by the tile entity.
     */
    PropertyKey<Inventory> INVENTORY = new ObjectKey<>("inventory");

    /**
     * Prevents the container from being opened unless the opener is holding an item whose name matches this string.
     *
     * Empty or {@code null} values will be treated as unset.
     */
    PropertyKey<String> LOCK = new PropertyKey<String>("lock", null) {
        @Override
        public boolean isSet(String value) {
            return value != null && !value.isEmpty();
        }
    };

    //actual methods

    /**
     * @return this entity's ID (e.g. {@code "minecraft:ender_chest"})
     */
    Identifier id();

    @Override
    boolean isEmpty();

    @Override
    boolean has(@NonNull PropertyKey<?> key);

    @Override
    <T> T get(@NonNull PropertyKey<T> key);

    @Override
    TileEntity remove(@NonNull PropertyKey<?> key);

    @Override
    <T> TileEntity put(@NonNull PropertyKey<T> key, T value);
}
