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

package net.daporkchop.lib.minecraft.format.java.decoder.item;

import lombok.NonNull;
import net.daporkchop.lib.minecraft.block.BlockRegistry;
import net.daporkchop.lib.minecraft.format.java.JavaSaveOptions;
import net.daporkchop.lib.minecraft.format.java.decoder.JavaItemDecoder;
import net.daporkchop.lib.minecraft.item.FireworkExplosion;
import net.daporkchop.lib.minecraft.item.ItemMeta;
import net.daporkchop.lib.minecraft.item.ItemStack;
import net.daporkchop.lib.minecraft.item.MapDecoration;
import net.daporkchop.lib.minecraft.item.PotionEffect;
import net.daporkchop.lib.minecraft.registry.Registry;
import net.daporkchop.lib.minecraft.text.parser.MCFormatParser;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.version.java.JavaVersion;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.nbt.tag.CompoundTag;
import net.daporkchop.lib.nbt.tag.IntArrayTag;
import net.daporkchop.lib.nbt.tag.IntTag;
import net.daporkchop.lib.nbt.tag.ListTag;
import net.daporkchop.lib.nbt.tag.StringTag;
import net.daporkchop.lib.nbt.tag.Tag;
import net.daporkchop.lib.primitive.map.ObjIntMap;
import net.daporkchop.lib.primitive.map.open.ObjIntOpenHashMap;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static net.daporkchop.lib.minecraft.item.ItemMeta.*;

/**
 * @author DaPorkchop_
 */
public class ItemDecoder1_8 implements JavaItemDecoder {
    protected final Map<String, ItemMetaDecoder> map = new HashMap<>();
    protected final ThreadLocal<Deque<Cache>> cache = ThreadLocal.withInitial(ArrayDeque::new);

    public ItemDecoder1_8() {
        this.map.put("Unbreakable", (meta, tag) -> meta.put(UNBREAKABLE, tag.getBoolean("Unbreakable")));
        this.map.put("CustomModelData", (meta, tag) -> meta.put(CUSTOM_MODEL_DATA, tag.getInt("CustomModelData")));

        this.map.put("CanDestroy", (meta, tag) -> meta.put(CAN_DESTROY,
                tag.getList("CanDestroy", StringTag.class).stream().map(StringTag::value).map(Identifier::fromString).collect(Collectors.toSet())));
        this.map.put("CanPlaceOn", (meta, tag) -> meta.put(CAN_PLACE_ON,
                tag.getList("CanPlaceOn", StringTag.class).stream().map(StringTag::value).map(Identifier::fromString).collect(Collectors.toSet())));

        this.map.put("BlockEntityTag", (AdvancedItemMetaDecoder) (meta, tag, stack, version, world) -> meta.put(TILE_ENTITY,
                world.parent().options().get(JavaSaveOptions.FIXERS).tileEntity().ceilingEntry(version).getValue()
                        .decode(tag.getCompound("BlockEntityTag"), version, world)));

        this.map.put("ench", (AdvancedItemMetaDecoder) (meta, tag, stack, version, world) -> {
            ObjIntMap<Identifier> map = new ObjIntOpenHashMap<>();
            Registry enchantmentRegistry = world.parent().registriesFor(version).get(Identifier.fromString("minecraft:enchantment"));
            tag.getList("ench", CompoundTag.class).forEach(enchantment -> map.put(enchantmentRegistry.get(enchantment.getShort("id")), enchantment.getShort("lvl")));
            meta.put(ENCHANTMENTS, map);
        });
        this.map.put("StoredEnchantments", (AdvancedItemMetaDecoder) (meta, tag, stack, version, world) -> {
            ObjIntMap<Identifier> map = new ObjIntOpenHashMap<>();
            Registry enchantmentRegistry = world.parent().registriesFor(version).get(Identifier.fromString("minecraft:enchantment"));
            tag.getList("StoredEnchantments", CompoundTag.class).forEach(enchantment -> map.put(enchantmentRegistry.get(enchantment.getShort("id")), enchantment.getShort("lvl")));
            meta.put(STORED_ENCHANTMENTS, map);
        });
        this.map.put("RepairCost", (meta, tag) -> meta.put(REPAIR_COST, tag.getInt("RepairCost")));

        this.map.put("CustomPotionEffects", (AdvancedItemMetaDecoder) (meta, tag, stack, version, world) -> {
            Registry potionEffectRegistry = world.parent().registriesFor(version).get(Identifier.fromString("minecraft:mob_effect"));
            meta.put(POTION_CUSTOM_EFFECTS, tag.getList("CustomPotionEffects", CompoundTag.class).stream()
                    .map(potionEffect -> new PotionEffect(potionEffectRegistry.get(potionEffect.getByte("Id")))
                            .amplifier(potionEffect.getByte("Amplifier", (byte) 1))
                            .duration(potionEffect.getInt("Duration", 1))
                            .ambient(potionEffect.getBoolean("Ambient", false))
                            .showParticles(potionEffect.getBoolean("ShowParticles", true))
                            .showIcon(potionEffect.getBoolean("ShowIcon", true)))
                    .collect(Collectors.toList()));
        });
        this.map.put("CustomPotionColor", (meta, tag) -> meta.put(POTION_CUSTOM_COLOR, new Color(tag.getInt("CustomPotionColor"))));

        this.map.put("display", (meta, tag) -> {
            CompoundTag display = tag.getCompound("display");
            String name = display.getString("Name", null);
            if (name != null) {
                meta.put(DISPLAY_NAME, MCFormatParser.DEFAULT.parse(name));
            }

            ListTag<StringTag> lore = display.getList("Lore", StringTag.class, null);
            if (lore != null) {
                meta.put(DISPLAY_LORE, lore.stream().map(StringTag::value).map(MCFormatParser.DEFAULT::parse).collect(Collectors.toList()));
            }

            if (display.contains("color")) {
                meta.put(DISPLAY_ARMOR_COLOR, new Color(display.getInt("color")));
            }
        });
        this.map.put("HideFlags", (meta, tag) -> meta.put(DISPLAY_HIDE_FLAGS, tag.getInt("HideFlags")));

        this.map.put("resolved", (meta, tag) -> meta.put(BOOK_RESOLVED, tag.getBoolean("resolved")));
        this.map.put("generation", (meta, tag) -> meta.put(BOOK_GENERATION, tag.getInt("generation")));
        this.map.put("author", (meta, tag) -> meta.put(BOOK_AUTHOR, tag.getString("author")));
        this.map.put("title", (meta, tag) -> meta.put(BOOK_TITLE, tag.getString("title")));
        this.map.put("pages", (AdvancedItemMetaDecoder) (meta, tag, stack, version, world) -> {
            if (stack.id().toString() == "minecraft:written_book") {
                meta.put(BOOK_PAGES, tag.getList("pages", StringTag.class).stream().map(StringTag::value).map(MCFormatParser.DEFAULT::parse).collect(Collectors.toList()));
            } else/* if (stack.id().toString() == "minecraft:writable_book")*/ { //actually just do this for all items
                meta.put(BOOK_PAGES_EDITABLE, tag.getList("pages", StringTag.class).stream().map(StringTag::value).collect(Collectors.toList()));
            }
        });

        this.map.put("Fireworks", (meta, tag) -> {
            CompoundTag fireworks = tag.getCompound("Fireworks");

            if (fireworks.contains("Flight")) {
                meta.put(FIREWORK_FLIGHT, (int) fireworks.getByte("Flight"));
            }

            ListTag<CompoundTag> explosions = fireworks.getList("Explosions", CompoundTag.class, null);
            if (explosions != null) {
                meta.put(FIREWORK_EXPLOSIONS, explosions.stream()
                        .map(explosion -> {
                            FireworkExplosion e = new FireworkExplosion()
                                    .flicker(explosion.getBoolean("Flicker", false))
                                    .trail(explosion.getBoolean("Trail", false))
                                    .type(explosion.getByte("Type", (byte) 0));

                            int[] colors = explosion.getIntArray("Colors", null);
                            if (colors != null) {
                                e.colors(Arrays.stream(colors).mapToObj(Color::new).collect(Collectors.toList()));
                            }

                            int[] fadeColors = explosion.getIntArray("FadeColors", null);
                            if (fadeColors != null) {
                                e.fadeColors(Arrays.stream(fadeColors).mapToObj(Color::new).collect(Collectors.toList()));
                            }
                            return e;
                        })
                        .collect(Collectors.toList()));
            }
        });
        this.map.put("Explosion", (meta, tag) -> {
            CompoundTag explosion = tag.getCompound("Explosion");
            FireworkExplosion e = new FireworkExplosion()
                    .flicker(explosion.getBoolean("Flicker", false))
                    .trail(explosion.getBoolean("Trail", false))
                    .type(explosion.getByte("Type", (byte) 0));

            int[] colors = explosion.getIntArray("Colors", null);
            if (colors != null) {
                e.colors(Arrays.stream(colors).mapToObj(Color::new).collect(Collectors.toList()));
            }

            int[] fadeColors = explosion.getIntArray("FadeColors", null);
            if (fadeColors != null) {
                e.fadeColors(Arrays.stream(fadeColors).mapToObj(Color::new).collect(Collectors.toList()));
            }
            meta.put(FIREWORK_STAR_EXPLOSION, e);
        });

        this.map.put("Decorations", (meta, tag) -> meta.put(MAP_DECORATIONS, tag.getList("Decorations", CompoundTag.class).stream()
                .map(decoration -> new MapDecoration(decoration.getString("id"))
                        .type(decoration.getByte("type", (byte) 0))
                        .x(decoration.getDouble("x", 0.0d))
                        .z(decoration.getDouble("z", 0.0d))
                        .rotation(decoration.getDouble("rot", 0.0d)))
                .collect(Collectors.toList())));
    }

    protected ItemDecoder1_8(@NonNull ItemDecoder1_8 parent) {
        this.map.putAll(parent.map);
    }

    @Override
    public ItemStack decode(@NonNull CompoundTag root, @NonNull JavaVersion version, @NonNull World world) {
        CompoundTag tag = root.getCompound("tag", null);
        ItemStack stack = new ItemStack(Identifier.fromString(root.getString("id", "stone")), root.getByte("Count", (byte) 1));
        Deque<Cache> cacheQueue = this.cache.get();
        Cache cache = cacheQueue.isEmpty() ? new Cache() : cacheQueue.pop();

        this.initialDecode(stack, cache, root, tag, version, world);
        this.mainDecode(stack, cache, root, tag, version, world);

        cacheQueue.push(cache); //return cache to stack so it can be re-used

        if (!cache.compound.isEmpty()) { //unknown NBT tags were found, store them
            cache.meta.put(ItemMeta.UNKNOWN_NBT, cache.compound);
            cache.compound = new CompoundTag();
        }
        if (!cache.meta.isEmpty()) { //ItemMeta instance was modified
            stack.meta(cache.meta);
            cache.meta = new ItemMeta();
        }
        return stack;
    }

    protected void initialDecode(@NonNull ItemStack stack, @NonNull Cache cache, @NonNull CompoundTag root, CompoundTag tag, @NonNull JavaVersion version, @NonNull World world) {
        int damage = root.getShort("Damage", (short) 0);

        BlockRegistry blockRegistry = world.parent().blockRegistryFor(version);
        if (blockRegistry.containsBlockId(stack.id())) {
            cache.meta.put(BLOCK_STATE, blockRegistry.getState(stack.id(), damage));
        } else if (stack.id().toString() == "minecraft:potion") { //decode legacy potion damage
            String potionId = tag != null && tag.contains("CustomPotionEffects") ? "minecraft:water" : LegacyPotionConversion.IDS[damage & 0x7F];
            cache.meta.put(POTION, Identifier.fromString(potionId != null ? potionId : "minecraft:water"));
            if ((damage & 0x4000) != 0) {
                stack.id(Identifier.fromString("minecraft:splash_potion"));
            }
        } else if (stack.id().toString() == "minecraft:filled_map") { //decode legacy map id
            cache.meta.put(MAP_ID, damage);
        } else {
            cache.meta.put(DAMAGE, damage);
        }
    }

    protected void mainDecode(@NonNull ItemStack stack, @NonNull Cache cache, @NonNull CompoundTag root, CompoundTag tag, @NonNull JavaVersion version, @NonNull World world) {
        if (tag != null) {
            tag.forEach((key, value) -> {
                ItemMetaDecoder decoder = this.map.get(key);
                if (decoder != null) {
                    decoder.decode(cache.meta, tag, stack, version, world);
                } else {
                    cache.compound.putTag(key, ((Tag) value).clone());
                }
            });
        }
    }

    @FunctionalInterface
    public interface ItemMetaDecoder {
        default void decode(@NonNull ItemMeta meta, @NonNull CompoundTag tag, ItemStack stack, @NonNull JavaVersion version, @NonNull World world) {
            this.decode(meta, tag);
        }

        void decode(@NonNull ItemMeta meta, @NonNull CompoundTag tag);
    }

    @FunctionalInterface
    public interface AdvancedItemMetaDecoder extends ItemMetaDecoder {
        @Override
        void decode(@NonNull ItemMeta meta, @NonNull CompoundTag tag, ItemStack stack, @NonNull JavaVersion version, @NonNull World world);

        @Override
        default void decode(@NonNull ItemMeta meta, @NonNull CompoundTag tag) {
            throw new UnsupportedOperationException();
        }
    }

    protected static class Cache {
        public ItemMeta meta = new ItemMeta();
        public CompoundTag compound = new CompoundTag();
    }

    protected static final class LegacyPotionConversion {
        protected static final String[] IDS = {
                "minecraft:water",
                "minecraft:regeneration",
                "minecraft:swiftness",
                "minecraft:fire_resistance",
                "minecraft:poison",
                "minecraft:healing",
                "minecraft:night_vision",
                null,
                "minecraft:weakness",
                "minecraft:strength",
                "minecraft:slowness",
                "minecraft:leaping",
                "minecraft:harming",
                "minecraft:water_breathing",
                "minecraft:invisibility",
                null,
                "minecraft:awkward",
                "minecraft:regeneration",
                "minecraft:swiftness",
                "minecraft:fire_resistance",
                "minecraft:poison",
                "minecraft:healing",
                "minecraft:night_vision",
                null,
                "minecraft:weakness",
                "minecraft:strength",
                "minecraft:slowness",
                "minecraft:leaping",
                "minecraft:harming",
                "minecraft:water_breathing",
                "minecraft:invisibility",
                null,
                "minecraft:thick",
                "minecraft:strong_regeneration",
                "minecraft:strong_swiftness",
                "minecraft:fire_resistance",
                "minecraft:strong_poison",
                "minecraft:strong_healing",
                "minecraft:night_vision",
                null,
                "minecraft:weakness",
                "minecraft:strong_strength",
                "minecraft:slowness",
                "minecraft:strong_leaping",
                "minecraft:strong_harming",
                "minecraft:water_breathing",
                "minecraft:invisibility",
                null,
                null,
                "minecraft:strong_regeneration",
                "minecraft:strong_swiftness",
                "minecraft:fire_resistance",
                "minecraft:strong_poison",
                "minecraft:strong_healing",
                "minecraft:night_vision",
                null,
                "minecraft:weakness",
                "minecraft:strong_strength",
                "minecraft:slowness",
                "minecraft:strong_leaping",
                "minecraft:strong_harming",
                "minecraft:water_breathing",
                "minecraft:invisibility",
                null,
                "minecraft:mundane",
                "minecraft:long_regeneration",
                "minecraft:long_swiftness",
                "minecraft:long_fire_resistance",
                "minecraft:long_poison",
                "minecraft:healing",
                "minecraft:long_night_vision",
                null,
                "minecraft:long_weakness",
                "minecraft:long_strength",
                "minecraft:long_slowness",
                "minecraft:long_leaping",
                "minecraft:harming",
                "minecraft:long_water_breathing",
                "minecraft:long_invisibility",
                null,
                "minecraft:awkward",
                "minecraft:long_regeneration",
                "minecraft:long_swiftness",
                "minecraft:long_fire_resistance",
                "minecraft:long_poison",
                "minecraft:healing",
                "minecraft:long_night_vision",
                null,
                "minecraft:long_weakness",
                "minecraft:long_strength",
                "minecraft:long_slowness",
                "minecraft:long_leaping",
                "minecraft:harming",
                "minecraft:long_water_breathing",
                "minecraft:long_invisibility",
                null,
                "minecraft:thick",
                "minecraft:regeneration",
                "minecraft:swiftness",
                "minecraft:long_fire_resistance",
                "minecraft:poison",
                "minecraft:strong_healing",
                "minecraft:long_night_vision",
                null,
                "minecraft:long_weakness",
                "minecraft:strength",
                "minecraft:long_slowness",
                "minecraft:leaping",
                "minecraft:strong_harming",
                "minecraft:long_water_breathing",
                "minecraft:long_invisibility",
                null,
                null,
                "minecraft:regeneration",
                "minecraft:swiftness",
                "minecraft:long_fire_resistance",
                "minecraft:poison",
                "minecraft:strong_healing",
                "minecraft:long_night_vision",
                null,
                "minecraft:long_weakness",
                "minecraft:strength",
                "minecraft:long_slowness",
                "minecraft:leaping",
                "minecraft:strong_harming",
                "minecraft:long_water_breathing",
                "minecraft:long_invisibility",
                null
        };
    }
}
