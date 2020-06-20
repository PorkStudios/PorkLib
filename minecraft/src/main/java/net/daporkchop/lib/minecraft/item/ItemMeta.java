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

package net.daporkchop.lib.minecraft.item;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.misc.Cloneable;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.logging.format.component.TextComponent;
import net.daporkchop.lib.minecraft.block.BlockState;
import net.daporkchop.lib.minecraft.tileentity.TileEntity;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.util.property.BooleanKey;
import net.daporkchop.lib.minecraft.util.property.CollectionKey;
import net.daporkchop.lib.minecraft.util.property.ObjectKey;
import net.daporkchop.lib.minecraft.util.property.PositiveIntKey;
import net.daporkchop.lib.minecraft.util.property.PositiveOrZeroIntKey;
import net.daporkchop.lib.minecraft.util.property.Properties;
import net.daporkchop.lib.minecraft.util.property.PropertyKey;
import net.daporkchop.lib.nbt.tag.CompoundTag;
import net.daporkchop.lib.primitive.map.IntIntMap;
import net.daporkchop.lib.primitive.map.ObjIntMap;

import java.awt.Color;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * Optional additional information used to describe an item.
 * <p>
 * See <a href="https://minecraft.gamepedia.com/Player.dat_format#Item_structure">Item Structure on the Minecraft Wiki</a>.
 * <p>
 * This class is split into sections. Different item types may only respect properties from certain sections.
 *
 * @author DaPorkchop_
 */
@EqualsAndHashCode
@Getter
@Setter
@Accessors(fluent = true, chain = true)
public final class ItemMeta implements Properties<ItemMeta>, Cloneable<ItemMeta> {
    /**
     * Any additional NBT tags attached to an item that are not known by this library.
     */
    public static final PropertyKey<CompoundTag> UNKNOWN_NBT = new ObjectKey<>("nbt_unknown");

    // general

    /**
     * The damage value of this item.
     * <p>
     * Negative values will be treated as {@code 0}.
     */
    public static final PropertyKey<Integer> DAMAGE = new PositiveIntKey("damage");

    /**
     * If {@code true}, the item will not lose durability when used in survival mode.
     */
    public static final PropertyKey<Boolean> UNBREAKABLE = new BooleanKey("unbreakable");

    /**
     * A {@link Set} of the block IDs that may be destroyed by this item in adventure mode.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    public static final PropertyKey<Set<Identifier>> CAN_DESTROY = new CollectionKey<>("can_destroy", Collections.emptySet());

    /**
     * A value used in the {@code custom_model_data} item tag in the overrides of item models.
     */
    public static final PropertyKey<Integer> CUSTOM_MODEL_DATA = new ObjectKey<>("custom_model_data", 0);

    // block

    /**
     * A {@link Set} of the block IDs that this block may be placed against in adventure mode.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    public static final PropertyKey<Set<Identifier>> CAN_PLACE_ON = new CollectionKey<>("can_place_on", Collections.emptySet());

    /**
     * Contains the {@link TileEntity} data attached to this item, for example banner data or blocks with tile entities that were Ctrl+picked in
     * creative mode.
     */
    public static final PropertyKey<TileEntity> TILE_ENTITY = new ObjectKey<>("tile_entity");

    /**
     * A custom {@link BlockState} which should be used in favor of the default state for this item's ID.
     * <p>
     * Default or {@code null} values will be treated as unset.
     * <p>
     * Values that do not correspond to this item's ID will be silently serialized, and may cause unexpected behavior when loaded.
     */
    public static final PropertyKey<BlockState> BLOCK_STATE = new ObjectKey<>("block_state");

    // enchantment

    /**
     * A map of enchantment IDs to their corresponding levels.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    public static final PropertyKey<ObjIntMap<Identifier>> ENCHANTMENTS = new ObjectKey<>("enchantments");

    /**
     * Exactly the same as {@link #ENCHANTMENTS}, but used for enchantments stored in an enchanted book.
     */
    public static final PropertyKey<ObjIntMap<Identifier>> STORED_ENCHANTMENTS = new ObjectKey<>("enchantments_stored");

    /**
     * The number of additional XP levels required to process this item in an anvil.
     * <p>
     * Negative values will be treated as {@code 0}.
     */
    public static final PropertyKey<Integer> REPAIR_COST = new PositiveIntKey("repair_cost");

    // potion

    /**
     * The {@link Identifier} of the default potion to apply.
     * <p>
     * {@code null} values will be treated as unset.
     */
    public static final PropertyKey<Identifier> POTION = new ObjectKey<>("potion");

    /**
     * Additional, custom potion effects applied by this item.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    public static final PropertyKey<List<PotionEffect>> POTION_CUSTOM_EFFECTS = new CollectionKey<>("potion_custom_effects", Collections.emptyList());

    /**
     * A custom {@link Color} to be used by the potion.
     * <p>
     * {@code null} values will be treated as unset.
     * <p>
     * The color's alpha channel will be ignored.
     */
    public static final PropertyKey<Color> POTION_CUSTOM_COLOR = new ObjectKey<>("potion_custom_color");

    // crossbow

    /**
     * A list of items that the crossbow has charged.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    public static final PropertyKey<List<ItemStack>> CROSSBOW_CHARGED_PROJECTILES = new CollectionKey<>("crossbow_charged_projectiles", Collections.emptyList());

    /**
     * Whether or not this crossbow is charged.
     */
    public static final PropertyKey<Boolean> CROSSBOW_CHARGED = new BooleanKey("crossbow_charged");

    //display

    /**
     * A custom text component shown in place of this item's name.
     * <p>
     * {@code null} values will be treated as unset.
     */
    public static final PropertyKey<TextComponent> DISPLAY_NAME = new ObjectKey<>("display_name");

    /**
     * The lines of text to display as this item's lore.
     * <p>
     * {@code null} values will be treated as unset.
     */
    public static final PropertyKey<List<TextComponent>> DISPLAY_LORE = new CollectionKey<>("display_lore", Collections.emptyList());

    /**
     * The color that the leather armor was dyed with.
     * <p>
     * {@code null} values will be treated as unset.
     * <p>
     * The color's alpha channel will be ignored.
     */
    public static final PropertyKey<Color> DISPLAY_ARMOR_COLOR = new ObjectKey<>("display_armor_color");

    /**
     * A bitmask indicating which parts of an item tooltip should not be rendered.
     * <p>
     * See the Minecraft Wiki page for more information on this field.
     */
    public static final PropertyKey<Integer> DISPLAY_HIDE_FLAGS = new ObjectKey<>("display_hide_flags", 0);

    // book

    /**
     * Whether or not the book has been opened.
     */
    public static final PropertyKey<Boolean> BOOK_RESOLVED = new BooleanKey("book_resolved");

    /**
     * The copy tier of this book.
     * <p>
     * {@code 0} values will be treated as unset.
     */
    public static final PropertyKey<Integer> BOOK_GENERATION = new ObjectKey<>("book_generation", 0);

    /**
     * The name of the author of this book.
     * <p>
     * {@code null} values will be treated as unset.
     */
    public static final PropertyKey<String> BOOK_AUTHOR = new ObjectKey<>("book_author");

    /**
     * The title of this book.
     * <p>
     * {@code null} values will be treated as unset.
     */
    public static final PropertyKey<String> BOOK_TITLE = new ObjectKey<>("book_title");

    /**
     * The pages in the book. Each {@link TextComponent} represents a single page.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    public static final PropertyKey<List<TextComponent>> BOOK_PAGES = new CollectionKey<>("book_pages", Collections.emptyList());

    /**
     * The pages in the book. Each {@link String} represents a single page.
     * <p>
     * Published books do not use this field, it is only used by the Book and Quill item.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    public static final PropertyKey<List<String>> BOOK_PAGES_EDITABLE = new CollectionKey<>("book_pages_editable", Collections.emptyList());

    // firework

    /**
     * The firework explosion type. Only used by a firework star.
     * <p>
     * {@code null} values will be treated as unset.
     */
    public static final PropertyKey<FireworkExplosion> FIREWORK_STAR_EXPLOSION = new ObjectKey<>("firework_star_explosion");

    /**
     * The number of gunpowder used to craft this firework rocket.
     */
    public static final PropertyKey<Integer> FIREWORK_FLIGHT = new ObjectKey<>("firework_flight", 0);

    /**
     * All of the firework explosions that belong to this firework rocket.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    public static final PropertyKey<List<FireworkExplosion>> FIREWORK_EXPLOSIONS = new CollectionKey<>("firework_explosions", Collections.emptyList());

    // map

    /**
     * The map number.
     * <p>
     * Negative values will be treated as unset.
     */
    public static final PropertyKey<Integer> MAP_ID = new PositiveOrZeroIntKey("map_id");

    /**
     * All of the decorations to display on this map.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    public static final PropertyKey<List<MapDecoration>> MAP_DECORATIONS = new CollectionKey<>("map_decorations", Collections.emptyList());

    /**
     * The map color.
     * <p>
     * Negative values will be treated as unset.
     */
    public static final PropertyKey<Integer> MAP_COLOR = new ObjectKey<>("map_color", 0);

    // suspicious stew

    /**
     * The status effects that this suspicious stew has.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    public static final PropertyKey<IntIntMap> STEW_EFFECTS = new ObjectKey<>("stew_effects");

    //TODO: attribute modifiers

    //TODO: skulls

    //TODO: entities, required for armor stands and spawn eggs

    //TODO: compasses track the position of their lodestone block

    protected final Map<PropertyKey<?>, ?> map;

    public ItemMeta() {
        this.map = new IdentityHashMap<>();
    }

    private ItemMeta(@NonNull Map<PropertyKey<?>, ?> map) {
        this.map = new IdentityHashMap<>(map);
    }

    /**
     * @return whether or not this {@link ItemMeta} is empty (contains no explicitly set values)
     */
    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean has(@NonNull PropertyKey<?> key) {
        return this.map.containsKey(key);
    }

    @Override
    public <T> T get(@NonNull PropertyKey<T> key) {
        return uncheckedCast(this.map.getOrDefault(key, uncheckedCast(key.defaultValue())));
    }

    @Override
    public ItemMeta remove(@NonNull PropertyKey<?> key) {
        this.map.remove(key);
        return this;
    }

    @Override
    public <T> ItemMeta put(@NonNull PropertyKey<T> key, T value) {
        if ((value = key.process(value)) != null) {
            this.map.put(key, uncheckedCast(value));
        } else {
            this.map.remove(key);
        }
        return this;
    }

    @Override
    public ItemMeta clone() {
        return new ItemMeta(this.map);
    }

    @Override
    public String toString() {
        try (Handle<StringBuilder> handle = PorkUtil.STRINGBUILDER_POOL.get()) {
            StringBuilder builder = handle.get();
            builder.setLength(0);
            builder.append('{');
            this.map.forEach((key, value) -> {
                if (key.isSet(uncheckedCast(value))) {
                    key.append(builder, uncheckedCast(value));
                }
            });
            if (builder.length() > 2) {
                builder.setLength(builder.length() - 2);
            }
            return builder.append('}').toString();
        }
    }
}
