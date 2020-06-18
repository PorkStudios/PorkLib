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
import net.daporkchop.lib.nbt.tag.IntTag;
import net.daporkchop.lib.nbt.tag.ListTag;
import net.daporkchop.lib.nbt.tag.StringTag;
import net.daporkchop.lib.primitive.map.ObjIntMap;
import net.daporkchop.lib.primitive.map.open.ObjIntOpenHashMap;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author DaPorkchop_
 */
public class ItemDecoder1_8 implements JavaItemDecoder {
    @Override
    public ItemStack decode(@NonNull CompoundTag root, @NonNull JavaVersion version, @NonNull World world) {
        CompoundTag tag = root.getCompound("tag", null);
        ItemStack stack = new ItemStack(this.getId(root, tag), this.getCount(root, tag));
        return stack.meta(this.getMeta(stack, root, tag, version, world));
    }

    protected Identifier getId(@NonNull CompoundTag root, CompoundTag tag) {
        return Identifier.fromString(root.getString("id", "stone"));
    }

    protected int getCount(@NonNull CompoundTag root, CompoundTag tag) {
        return root.getByte("Count", (byte) 1);
    }

    protected ItemMeta getMeta(@NonNull ItemStack stack, @NonNull CompoundTag root, CompoundTag tag, @NonNull JavaVersion version, @NonNull World world) {
        if (this.hasMeta(root, tag)) {
            ItemMeta meta = new ItemMeta();
            if (tag != null) {
                this.getGeneralMeta(stack, tag, meta, version, world);
                this.getBlocksMeta(stack, tag, meta, version, world);
                this.getEnchantmentsMeta(stack, tag, meta, version, world);
                this.getPotionsMeta(stack, tag, meta, version, world);
                this.getCrossbowMeta(stack, tag, meta, version, world);
                this.getDisplayMeta(stack, tag, meta, version, world);
                this.getBookMeta(stack, tag, meta, version, world);
                this.getFireworkMeta(stack, tag, meta, version, world);
                this.getMapMeta(stack, tag, meta, version, world);
                this.getStewMeta(stack, tag, meta, version, world);
            }
            this.getOtherMeta(stack, root, meta, version, world);
            return meta;
        } else {
            return null;
        }
    }

    protected boolean hasMeta(@NonNull CompoundTag root, CompoundTag tag) {
        return root.contains("Damage") || (tag != null && tag.size() > 0);
    }

    protected void getOtherMeta(@NonNull ItemStack stack, @NonNull CompoundTag root, @NonNull ItemMeta meta, @NonNull JavaVersion version, @NonNull World world) {
        int damage = root.getShort("Damage", (short) 0);

        BlockRegistry blockRegistry = world.parent().blockRegistryFor(version);
        if (blockRegistry.containsBlockId(stack.id())) {
            meta.blockState(blockRegistry.getState(stack.id(), damage));
        } else if (stack.id().toString() == "minecraft:potion") { //decode legacy potion damage
            String potionId = LegacyPotionConversion.IDS[damage & 0x7F];
            meta.potion(Identifier.fromString(potionId != null ? potionId : "minecraft:water"));
            if ((damage & 0x4000) != 0) {
                stack.id(Identifier.fromString("minecraft:splash_potion"));
            }
        } else if (stack.id().toString() == "minecraft:filled_map") { //decode legacy map id
            meta.mapId(damage);
        } else {
            meta.damage(damage);
        }
    }

    protected void getGeneralMeta(@NonNull ItemStack stack, @NonNull CompoundTag tag, @NonNull ItemMeta meta, @NonNull JavaVersion version, @NonNull World world) {
        meta.unbreakable(tag.getBoolean("Unbreakable", false));

        ListTag<StringTag> canDestroy = tag.getList("CanDestroy", StringTag.class, null);
        if (canDestroy != null) {
            meta.canDestroy(canDestroy.stream().map(StringTag::value).map(Identifier::fromString).collect(Collectors.toSet()));
        }

        meta.customModelData(tag.getFloat("CustomModelData", Float.NaN)); //this field is actually an int
    }

    protected void getBlocksMeta(@NonNull ItemStack stack, @NonNull CompoundTag tag, @NonNull ItemMeta meta, @NonNull JavaVersion version, @NonNull World world) {
        ListTag<StringTag> canPlaceOn = tag.getList("CanPlaceOn", StringTag.class, null);
        if (canPlaceOn != null) {
            meta.canDestroy(canPlaceOn.stream().map(StringTag::value).map(Identifier::fromString).collect(Collectors.toSet()));
        }

        CompoundTag blockEntityTag = tag.getCompound("BlockEntityTag", null);
        if (blockEntityTag != null) {
            meta.tileEntity(world.parent().options().get(JavaSaveOptions.FIXERS)
                    .tileEntity().ceilingEntry(version).getValue().decode(blockEntityTag, version, world));
        }
    }

    protected void getEnchantmentsMeta(@NonNull ItemStack stack, @NonNull CompoundTag tag, @NonNull ItemMeta meta, @NonNull JavaVersion version, @NonNull World world) {
        ListTag<CompoundTag> enchantments = tag.getList("ench", CompoundTag.class, null);
        if (enchantments != null) {
            ObjIntMap<Identifier> map = new ObjIntOpenHashMap<>();
            Registry enchantmentRegistry = world.parent().registriesFor(version).get(Identifier.fromString("minecraft:enchantment"));
            enchantments.forEach(enchantment -> map.put(enchantmentRegistry.get(enchantment.getShort("id")), enchantment.getShort("lvl")));
            meta.enchantments(map);
        }

        enchantments = tag.getList("StoredEnchantments", CompoundTag.class, null);
        if (enchantments != null) {
            ObjIntMap<Identifier> map = new ObjIntOpenHashMap<>();
            Registry enchantmentRegistry = world.parent().registriesFor(version).get(Identifier.fromString("minecraft:enchantment"));
            enchantments.forEach(enchantment -> map.put(enchantmentRegistry.get(enchantment.getShort("id")), enchantment.getShort("lvl")));
            meta.storedEnchantments(map);
        }

        meta.repairCost(tag.getInt("RepairCost", 0));
    }

    protected void getPotionsMeta(@NonNull ItemStack stack, @NonNull CompoundTag tag, @NonNull ItemMeta meta, @NonNull JavaVersion version, @NonNull World world) {
        ListTag<CompoundTag> customPotionEffects = tag.getList("CustomPotionEffects", CompoundTag.class, null);
        if (customPotionEffects != null) {
            List<PotionEffect> list = new ArrayList<>(customPotionEffects.size());
            Registry potionEffectRegistry = world.parent().registriesFor(version).get(Identifier.fromString("minecraft:mob_effect"));
            customPotionEffects.forEach(potionEffect -> list.add(new PotionEffect(potionEffectRegistry.get(potionEffect.getByte("Id")))
                    .amplifier(potionEffect.getByte("Amplifier", (byte) 1))
                    .duration(potionEffect.getInt("Duration", 1))
                    .ambient(potionEffect.getBoolean("Ambient", false))
                    .showParticles(potionEffect.getBoolean("ShowParticles", true))
                    .showIcon(potionEffect.getBoolean("ShowIcon", true))));
            meta.customEffects(list);
        }

        if (tag.contains("CustomPotionColor")) {
            meta.customPotionColor(new Color(tag.getInt("CustomPotionColor")));
        }
    }

    protected void getCrossbowMeta(@NonNull ItemStack stack, @NonNull CompoundTag tag, @NonNull ItemMeta meta, @NonNull JavaVersion version, @NonNull World world) {
        //no-op
    }

    protected void getDisplayMeta(@NonNull ItemStack stack, @NonNull CompoundTag tag, @NonNull ItemMeta meta, @NonNull JavaVersion version, @NonNull World world) {
        CompoundTag display = tag.getCompound("display", null);
        if (display != null) {
            String name = tag.getString("Name", null);
            if (name != null) {
                meta.name(MCFormatParser.DEFAULT.parse(name));
            }

            ListTag<StringTag> lore = tag.getList("Lore", StringTag.class, null);
            if (lore != null) {
                meta.lore(lore.stream().map(StringTag::value).map(MCFormatParser.DEFAULT::parse).collect(Collectors.toList()));
            }

            if (tag.contains("color")) {
                meta.armorColor(new Color(tag.getInt("color")));
            }
        }

        meta.hideFlags(tag.getInt("HideFlags", 0));
    }

    protected void getBookMeta(@NonNull ItemStack stack, @NonNull CompoundTag tag, @NonNull ItemMeta meta, @NonNull JavaVersion version, @NonNull World world) {
        meta.bookResolved(tag.getBoolean("resolved", false))
                .bookGeneration(tag.getInt("generation", 0))
                .bookAuthor(tag.getString("author", null))
                .bookTitle(tag.getString("title", null));

        ListTag<StringTag> pages = tag.getList("pages", StringTag.class, null);
        if (pages != null) {
            switch (stack.id().toString()) {
                case "minecraft:written_book":
                    meta.bookPages(pages.stream().map(StringTag::value).map(MCFormatParser.DEFAULT::parse).collect(Collectors.toList()));
                    break;
                case "minecraft:writable_book":
                    meta.bookPagesEditable(pages.stream().map(StringTag::value).collect(Collectors.toList()));
                    break;
            }
        }
    }

    protected void getFireworkMeta(@NonNull ItemStack stack, @NonNull CompoundTag tag, @NonNull ItemMeta meta, @NonNull JavaVersion version, @NonNull World world) {
        CompoundTag fireworks = tag.getCompound("Fireworks", null);
        if (fireworks != null) {
            meta.fireworkFlight(fireworks.getByte("Flight", (byte) 0));

            ListTag<CompoundTag> explosions = fireworks.getList("Explosions", CompoundTag.class, null);
            if (explosions != null) {
                meta.fireworkExplosions(explosions.stream()
                        .map(explosion -> {
                            FireworkExplosion e = new FireworkExplosion()
                                    .flicker(explosion.getBoolean("Flicker", false))
                                    .trail(explosion.getBoolean("Trail", false))
                                    .type(explosion.getByte("Type", (byte) 0));

                            ListTag<IntTag> colors = explosion.getList("Colors", IntTag.class, null);
                            if (colors != null) {
                                e.colors(colors.stream().mapToInt(IntTag::value).mapToObj(Color::new).collect(Collectors.toList()));
                            }

                            ListTag<IntTag> fadeColors = explosion.getList("FadeColors", IntTag.class, null);
                            if (fadeColors != null) {
                                e.fadeColors(fadeColors.stream().mapToInt(IntTag::value).mapToObj(Color::new).collect(Collectors.toList()));
                            }
                            return e;
                        })
                        .collect(Collectors.toList()));
            }
        }

        CompoundTag explosion = tag.getCompound("Explosion", null);
        if (explosion != null) {
            FireworkExplosion e = new FireworkExplosion()
                    .flicker(explosion.getBoolean("Flicker", false))
                    .trail(explosion.getBoolean("Trail", false))
                    .type(explosion.getByte("Type", (byte) 0));

            ListTag<IntTag> colors = explosion.getList("Colors", IntTag.class, null);
            if (colors != null) {
                e.colors(colors.stream().mapToInt(IntTag::value).mapToObj(Color::new).collect(Collectors.toList()));
            }

            ListTag<IntTag> fadeColors = explosion.getList("FadeColors", IntTag.class, null);
            if (fadeColors != null) {
                e.fadeColors(fadeColors.stream().mapToInt(IntTag::value).mapToObj(Color::new).collect(Collectors.toList()));
            }
            meta.fireworkExplosion(e);
        }
    }

    protected void getMapMeta(@NonNull ItemStack stack, @NonNull CompoundTag tag, @NonNull ItemMeta meta, @NonNull JavaVersion version, @NonNull World world) {
        ListTag<CompoundTag> decorations = tag.getList("Decorations", CompoundTag.class, null);
        if (decorations != null) {
            meta.mapDecorations(decorations.stream()
                    .map(decoration -> new MapDecoration(decoration.getString("id"))
                            .type(decoration.getByte("type", (byte) 0))
                            .x(decoration.getDouble("x", 0.0d))
                            .z(decoration.getDouble("z", 0.0d))
                            .rotation(decoration.getDouble("rot", 0.0d)))
                    .collect(Collectors.toList()));
        }
    }

    protected void getStewMeta(@NonNull ItemStack stack, @NonNull CompoundTag tag, @NonNull ItemMeta meta, @NonNull JavaVersion version, @NonNull World world) {
        //no-op
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
