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
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.misc.Cloneable;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.logging.format.component.TextComponent;
import net.daporkchop.lib.minecraft.block.BlockState;
import net.daporkchop.lib.minecraft.tileentity.TileEntity;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.primitive.map.IntIntMap;
import net.daporkchop.lib.primitive.map.ObjIntMap;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import static net.daporkchop.lib.common.util.PValidation.*;
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
public final class ItemMeta implements Cloneable<ItemMeta> {
    private static final Map<String, Key<?>> KEY_LOOKUP = new ConcurrentHashMap<>();

    public static <T> Key<T> key(@NonNull Key<T> key) {
        String name = key.name();
        checkState(KEY_LOOKUP.putIfAbsent(name, key) == null, "duplicate key name: %s", name);
        return key;
    }

    // general

    /**
     * The damage value of this item.
     * <p>
     * Negative values will be treated as {@code 0}.
     */
    public static final Key<Integer> DAMAGE = key(new PositiveIntKey("damage"));

    /**
     * If {@code true}, the item will not lose durability when used in survival mode.
     */
    public static final Key<Boolean> UNBREAKABLE = key(new BooleanKey("unbreakable"));

    /**
     * A {@link Set} of the block IDs that may be destroyed by this item in adventure mode.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    public static final Key<Set<Identifier>> CAN_DESTROY = key(new CollectionKey<>("can_destroy", Collections.emptySet()));

    /**
     * A value used in the {@code custom_model_data} item tag in the overrides of item models.
     */
    public static final Key<Integer> CUSTOM_MODEL_DATA = key(new AnyIntKey("custom_model_data"));

    // block

    /**
     * A {@link Set} of the block IDs that this block may be placed against in adventure mode.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    public static final Key<Set<Identifier>> CAN_PLACE_ON = key(new CollectionKey<>("can_place_on", Collections.emptySet()));

    /**
     * Contains the {@link TileEntity} data attached to this item, for example banner data or blocks with tile entities that were Ctrl+picked in
     * creative mode.
     */
    public static final Key<TileEntity> TILE_ENTITY = key(new ObjectKey<>("tile_entity"));

    /**
     * A custom {@link BlockState} which should be used in favor of the default state for this item's ID.
     * <p>
     * Default or {@code null} values will be treated as unset.
     * <p>
     * Values that do not correspond to this item's ID will be silently serialized, and may cause unexpected behavior when loaded.
     */
    public static final Key<BlockState> BLOCK_STATE = key(new ObjectKey<>("block_state"));

    // enchantment

    /**
     * A map of enchantment IDs to their corresponding levels.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    public static final Key<ObjIntMap<Identifier>> ENCHANTMENTS = key(new ObjectKey<>("enchantments"));

    /**
     * Exactly the same as {@link #ENCHANTMENTS}, but used for enchantments stored in an enchanted book.
     */
    public static final Key<ObjIntMap<Identifier>> STORED_ENCHANTMENTS = key(new ObjectKey<>("enchantments_stored"));

    /**
     * The number of additional XP levels required to process this item in an anvil.
     * <p>
     * Negative values will be treated as {@code 0}.
     */
    public static final Key<Integer> REPAIR_COST = key(new PositiveIntKey("repair_cost"));

    // potion

    /**
     * The {@link Identifier} of the default potion to apply.
     * <p>
     * {@code null} values will be treated as unset.
     */
    public static final Key<Identifier> POTION = key(new ObjectKey<>("potion"));

    /**
     * Additional, custom potion effects applied by this item.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    public static final Key<List<PotionEffect>> POTION_CUSTOM_EFFECTS = key(new CollectionKey<>("potion_custom_effects", Collections.emptyList()));

    /**
     * A custom {@link Color} to be used by the potion.
     * <p>
     * {@code null} values will be treated as unset.
     * <p>
     * The color's alpha channel will be ignored.
     */
    public static final Key<Color> POTION_CUSTOM_COLOR = key(new ObjectKey<>("potion_custom_color"));

    // crossbow

    /**
     * A list of items that the crossbow has charged.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    public static final Key<List<ItemStack>> CROSSBOW_CHARGED_PROJECTILES = key(new CollectionKey<>("crossbow_charged_projectiles", Collections.emptyList()));

    /**
     * Whether or not this crossbow is charged.
     */
    public static final Key<Boolean> CROSSBOW_CHARGED = key(new BooleanKey("crossbow_charged"));

    //display

    /**
     * A custom text component shown in place of this item's name.
     * <p>
     * {@code null} values will be treated as unset.
     */
    public static final Key<TextComponent> DISPLAY_NAME = key(new ObjectKey<>("display_name"));

    /**
     * The lines of text to display as this item's lore.
     * <p>
     * {@code null} values will be treated as unset.
     */
    public static final Key<List<TextComponent>> DISPLAY_LORE = key(new CollectionKey<>("display_lore", Collections.emptyList()));

    /**
     * The color that the leather armor was dyed with.
     * <p>
     * {@code null} values will be treated as unset.
     * <p>
     * The color's alpha channel will be ignored.
     */
    public static final Key<Color> DISPLAY_ARMOR_COLOR = key(new ObjectKey<>("display_armor_color"));

    /**
     * A bitmask indicating which parts of an item tooltip should not be rendered.
     * <p>
     * See the Minecraft Wiki page for more information on this field.
     */
    public static final Key<Integer> DISPLAY_HIDE_FLAGS = key(new AnyIntKey("display_hide_flags"));

    // book

    /**
     * Whether or not the book has been opened.
     */
    public static final Key<Boolean> BOOK_RESOLVED = key(new BooleanKey("book_resolved"));

    /**
     * The copy tier of this book.
     * <p>
     * {@code 0} values will be treated as unset.
     */
    public static final Key<Integer> BOOK_GENERATION = key(new AnyIntKey("book_generation"));

    /**
     * The name of the author of this book.
     * <p>
     * {@code null} values will be treated as unset.
     */
    public static final Key<String> BOOK_AUTHOR = key(new ObjectKey<>("book_author"));

    /**
     * The title of this book.
     * <p>
     * {@code null} values will be treated as unset.
     */
    public static final Key<String> BOOK_TITLE = key(new ObjectKey<>("book_title"));

    /**
     * The pages in the book. Each {@link TextComponent} represents a single page.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    public static final Key<List<TextComponent>> BOOK_PAGES = key(new CollectionKey<>("book_pages", Collections.emptyList()));

    /**
     * The pages in the book. Each {@link String} represents a single page.
     * <p>
     * Published books do not use this field, it is only used by the Book and Quill item.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    public static final Key<List<String>> BOOK_PAGES_EDITABLE = key(new CollectionKey<>("book_pages_editable", Collections.emptyList()));

    // firework

    /**
     * The firework explosion type. Only used by a firework star.
     * <p>
     * {@code null} values will be treated as unset.
     */
    public static final Key<FireworkExplosion> FIREWORK_STAR_EXPLOSION = key(new ObjectKey<>("firework_star_explosion"));

    /**
     * The number of gunpowder used to craft this firework rocket.
     */
    public static final Key<Integer> FIREWORK_FLIGHT = key(new AnyIntKey("firework_flight"));

    /**
     * All of the firework explosions that belong to this firework rocket.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    public static final Key<List<FireworkExplosion>> FIREWORK_EXPLOSIONS = key(new CollectionKey<>("firework_explosions", Collections.emptyList()));

    // map

    /**
     * The map number.
     * <p>
     * Negative values will be treated as unset.
     */
    public static final Key<Integer> MAP_ID = key(new PositiveOrZeroIntKey("map_id"));

    /**
     * All of the decorations to display on this map.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    public static final Key<List<MapDecoration>> MAP_DECORATIONS = key(new CollectionKey<>("map_decorations", Collections.emptyList()));

    /**
     * The map color.
     * <p>
     * Negative values will be treated as unset.
     */
    public static final Key<Integer> MAP_COLOR = key(new AnyIntKey("map_color"));

    // suspicious stew

    /**
     * The status effects that this suspicious stew has.
     * <p>
     * Empty or {@code null} values will be treated as unset.
     */
    public static final Key<IntIntMap> STEW_EFFECTS = key(new ObjectKey<>("stew_effects"));

    //TODO: attribute modifiers

    //TODO: skulls

    //TODO: entities, required for armor stands and spawn eggs

    //TODO: compasses track the position of their lodestone block

    protected final Map<Key<?>, ?> map;

    public ItemMeta() {
        this.map = new IdentityHashMap<>();
    }

    private ItemMeta(@NonNull Map<Key<?>, ?> map) {
        this.map = new IdentityHashMap<>(map);
    }

    /**
     * @return whether or not this {@link ItemMeta} is empty (contains no explicitly set values)
     */
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public <T> ItemMeta put(@NonNull Key<T> key, T value)   {
        if ((value = key.process(value)) != null)   {
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

    /**
     * A key used to identify an {@link ItemMeta} value.
     *
     * @param <T> the type of value referenced by this key
     * @author DaPorkchop_
     */
    @RequiredArgsConstructor
    @Getter
    @Accessors(fluent = true)
    public static abstract class Key<T> implements Comparable<Key<?>> {
        @NonNull
        protected final String name;
        protected final T defaultValue;
        protected final int hashCode = ThreadLocalRandom.current().nextInt();

        @Override
        public int compareTo(Key<?> o) {
            return this.name.compareTo(o.name);
        }

        /**
         * Checks whether or not the given value is considered to be unset.
         *
         * @param value the value to check
         * @return whether or not the given value is considered to be unset
         */
        protected abstract boolean isSet(T value);

        /**
         * Pre-processes a value that is about to be set.
         * <p>
         * This allows a key to e.g. clamp a value to a specified range.
         *
         * @param value the value to process
         * @return the processed value. If {@code null}, the key will be removed
         */
        protected T process(T value) {
            return this.isSet(value) ? value : null;
        }

        /**
         * Appends the given value as an entry to the given {@link StringBuilder}.
         * <p>
         * The value is guaranteed to be considered "set" as defined by {@link #isSet(Object)}.
         *
         * @param builder the {@link StringBuilder} to append to
         * @param value   the value to append
         */
        protected void append(@NonNull StringBuilder builder, @NonNull T value) {
            builder.append(this.name).append('=').append(value).append(',').append(' ');
        }
    }

    /**
     * A {@link Key} which stores an {@code int}. No values are considered unset.
     *
     * @author DaPorkchop_
     */
    public static final class AnyIntKey extends Key<Integer> {
        public AnyIntKey(String name) {
            super(name, 0);
        }

        public AnyIntKey(String name, Integer defaultValue) {
            super(name, defaultValue);
        }

        @Override
        protected boolean isSet(Integer value) {
            return value != null;
        }

        @Override
        protected void append(@NonNull StringBuilder builder, @NonNull Integer value) {
            builder.append(this.name).append('=').append(value.intValue()).append(',').append(' ');
        }
    }

    /**
     * A {@link Key} which stores a positive {@code int}. All non-positive values are considered unset and removed, defaulting to {@code 0}.
     *
     * @author DaPorkchop_
     */
    public static final class PositiveIntKey extends Key<Integer> {
        public PositiveIntKey(String name) {
            super(name, 0);
        }

        public PositiveIntKey(String name, Integer defaultValue) {
            super(name, defaultValue);
        }

        @Override
        protected boolean isSet(Integer value) {
            return value != null && value > 0;
        }

        @Override
        protected void append(@NonNull StringBuilder builder, @NonNull Integer value) {
            builder.append(this.name).append('=').append(value.intValue()).append(',').append(' ');
        }
    }

    /**
     * A {@link Key} which stores a positive {@code int}. All negative values are considered unset and removed, defaulting to {@code 0}.
     *
     * @author DaPorkchop_
     */
    public static final class PositiveOrZeroIntKey extends Key<Integer> {
        public PositiveOrZeroIntKey(String name) {
            super(name, 0);
        }

        public PositiveOrZeroIntKey(String name, Integer defaultValue) {
            super(name, defaultValue);
        }

        @Override
        protected boolean isSet(Integer value) {
            return value != null && value >= 0;
        }

        @Override
        protected void append(@NonNull StringBuilder builder, @NonNull Integer value) {
            builder.append(this.name).append('=').append(value.intValue()).append(',').append(' ');
        }
    }

    /**
     * A {@link Key} which stores a {@code boolean}. Default values are considered unset and removed.
     *
     * @author DaPorkchop_
     */
    public static final class BooleanKey extends Key<Boolean> {
        public BooleanKey(String name) {
            super(name, Boolean.FALSE);
        }

        public BooleanKey(String name, Boolean defaultValue) {
            super(name, defaultValue);
        }

        @Override
        protected boolean isSet(Boolean value) {
            return value != null && value != this.defaultValue;
        }

        @Override
        protected void append(@NonNull StringBuilder builder, @NonNull Boolean value) {
            builder.append(this.name).append('=').append(value.booleanValue()).append(',').append(' ');
        }
    }

    /**
     * A {@link Key} which stores a {@link Collection}. All empty values are considered unset and removed.
     *
     * @author DaPorkchop_
     */
    public static final class CollectionKey<C extends Collection> extends Key<C> {
        public CollectionKey(String name, C defaultValue) {
            super(name, defaultValue);
        }

        @Override
        protected boolean isSet(C value) {
            return value != null && !value.isEmpty();
        }
    }

    /**
     * A {@link Key} which stores an arbitrary {@link Object}. All {@code null} values are considered unset and removed.
     *
     * @author DaPorkchop_
     */
    public static final class ObjectKey<V> extends Key<V> {
        public ObjectKey(String name) {
            super(name, null);
        }

        public ObjectKey(String name, V defaultValue) {
            super(name, defaultValue);
        }

        @Override
        protected boolean isSet(V value) {
            return value != null;
        }
    }
}
