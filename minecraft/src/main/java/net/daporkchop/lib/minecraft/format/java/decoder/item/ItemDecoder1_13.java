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
import net.daporkchop.lib.minecraft.item.ItemMeta;
import net.daporkchop.lib.minecraft.item.ItemStack;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.version.java.JavaVersion;
import net.daporkchop.lib.minecraft.world.World;
import net.daporkchop.lib.nbt.tag.CompoundTag;
import net.daporkchop.lib.primitive.map.ObjIntMap;
import net.daporkchop.lib.primitive.map.open.ObjIntOpenHashMap;

import static net.daporkchop.lib.minecraft.item.ItemMeta.*;

/**
 * @author DaPorkchop_
 */
public class ItemDecoder1_13 extends ItemDecoder1_9 {
    public ItemDecoder1_13() {
        this(new ItemDecoder1_9());
    }

    public ItemDecoder1_13(@NonNull ItemDecoder1_9 parent) {
        super(parent);

        this.map.remove("ench"); //1.13 uses a different format for enchantments
        this.map.put("Enchantments", (meta, tag) -> {
            ObjIntMap<Identifier> map = new ObjIntOpenHashMap<>();
            tag.getList("Enchantments", CompoundTag.class)
                    .forEach(enchantment -> map.put(Identifier.fromString(enchantment.getString("id")), enchantment.getInt("lvl")));
            meta.put(ENCHANTMENTS, map);
        });
        this.map.put("StoredEnchantments", (meta, tag) -> {
            ObjIntMap<Identifier> map = new ObjIntOpenHashMap<>();
            tag.getList("StoredEnchantments", CompoundTag.class)
                    .forEach(enchantment -> map.put(Identifier.fromString(enchantment.getString("id")), enchantment.getInt("lvl")));
            meta.put(ENCHANTMENTS, map);
        });
    }

    @Override
    protected void initialDecode(@NonNull ItemStack stack, @NonNull Cache cache, @NonNull CompoundTag root, CompoundTag tag, @NonNull JavaVersion version, @NonNull World world) {
        if (tag != null) {
            cache.meta.put(ItemMeta.DAMAGE, tag.getInt("Damage", 0));
        }
    }
}
