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
import net.daporkchop.lib.minecraft.item.ItemStack;
import net.daporkchop.lib.minecraft.item.inventory.Inventory;
import net.daporkchop.lib.minecraft.text.MCTextType;
import net.daporkchop.lib.minecraft.text.component.MCTextRoot;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.util.property.BooleanKey;
import net.daporkchop.lib.minecraft.util.property.ObjectKey;
import net.daporkchop.lib.minecraft.util.property.PositiveOrZeroIntKey;
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

    /**
     * Whether or not the tile entity should be kept as an unprocessed NBT tag in memory.
     * <p>
     * This library does not respect this value, but it does store it so that it can be written again when saving the tile entity.
     */
    PropertyKey<Boolean> KEEP_PACKED = new BooleanKey("keep_packed");

    //containers

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
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    PropertyKey<String> LOCK = new PropertyKey<String>("lock", null) {
        @Override
        public boolean isSet(String value) {
            return value != null && !value.isEmpty();
        }
    };

    //furnace

    /**
     * The number of ticks that the current furnace recipe has been cooking for.
     * <p>
     * Negative values will be treated as {@code 0}.
     */
    PropertyKey<Integer> FURNACE_COOK_TIME = new PositiveOrZeroIntKey("furnace_cook_time");

    /**
     * The number of ticks that the current furnace recipe takes in total.
     * <p>
     * Negative values will be treated as {@code 0}.
     */
    PropertyKey<Integer> FURNACE_TOTAL_TIME = new PositiveOrZeroIntKey("furnace_cook_time_total");

    /**
     * The number of ticks that the current furnace fuel has left to burn.
     * <p>
     * Negative values will be treated as {@code 0}.
     */
    PropertyKey<Integer> FURNACE_BURN_TIME = new PositiveOrZeroIntKey("furnace_burn_time");

    //various

    /**
     * An array containing 4 text components with the text on a sign.
     * <p>
     * Arrays with more than 4 elements will be truncated, less than 4 values will be expanded with empty lines.
     * <p>
     * {@code null} values will be treated as unset.
     */
    PropertyKey<TextComponent[]> SIGN_TEXT = new PropertyKey<TextComponent[]>("sign_text", null) {
        @Override
        public boolean isSet(TextComponent[] value) {
            return value != null && value.length == 4;
        }

        @Override
        public TextComponent[] process(TextComponent[] value) {
            if (value == null || value.length != 4) {
                TextComponent[] arr = new TextComponent[4];
                int i = 0;
                for (int len = value == null ? 0 : value.length; i < len; i++) {
                    arr[i] = value[i];
                }
                for (; i < 4; i++) {
                    arr[i] = new MCTextRoot(MCTextType.JSON, "\"\"");
                }
                return arr;
            }
            return value;
        }

        @Override
        public void append(@NonNull StringBuilder builder, @NonNull TextComponent[] value) {
            builder.append(this.name).append('=').append('[')
                    .append(value[0]).append(',').append(' ')
                    .append(value[1]).append(',').append(' ')
                    .append(value[2]).append(',').append(' ')
                    .append(value[3]).append(']')
                    .append(',').append(' ');
        }
    };

    /**
     * The number of ticks the potions have to brew.
     * <p>
     * Negative values will be treated as {@code 0}.
     */
    PropertyKey<Integer> BREW_TIME = new PositiveOrZeroIntKey("brew_time");

    /**
     * The currently playing record.
     */
    PropertyKey<ItemStack> JUKEBOX_RECORD = new ObjectKey<>("jukebox_record");

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
