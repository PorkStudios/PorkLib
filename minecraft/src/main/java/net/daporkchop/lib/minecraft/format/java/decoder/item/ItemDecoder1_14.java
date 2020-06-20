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
import net.daporkchop.lib.minecraft.block.BlockState;
import net.daporkchop.lib.nbt.tag.CompoundTag;
import net.daporkchop.lib.nbt.tag.StringTag;
import net.daporkchop.lib.nbt.tag.Tag;

import java.util.Map;
import java.util.stream.Collectors;

import static net.daporkchop.lib.minecraft.item.ItemMeta.*;

/**
 * @author DaPorkchop_
 */
public class ItemDecoder1_14 extends ItemDecoder1_13 {
    public ItemDecoder1_14() {
        this(new ItemDecoder1_13());
    }

    public ItemDecoder1_14(@NonNull ItemDecoder1_13 parent) {
        super(parent);

        this.map.put("BlockStateTag", (AdvancedItemMetaDecoder) (meta, tag, stack, version, world) -> {
            BlockState state = world.parent().blockRegistryFor(version).getDefaultState(stack.id());
            for (Map.Entry<String, Tag> entry : tag.getCompound("BlockStateTag")) {
                state = state.withProperty(entry.getKey(), ((StringTag) entry.getValue()).value());
            }
            meta.put(BLOCK_STATE, state);
        });

        this.map.put("ChargedProjectiles", (AdvancedItemMetaDecoder) (meta, tag, stack, version, world) -> meta.put(
                CROSSBOW_CHARGED_PROJECTILES,
                tag.getList("ChargedProjectiles", CompoundTag.class).stream()
                        .map(projectile -> this.decode(projectile, version, world)).collect(Collectors.toList())));
        this.map.put("Charged", (meta, tag) -> meta.put(CROSSBOW_CHARGED, tag.getBoolean("Charged")));
    }
}
