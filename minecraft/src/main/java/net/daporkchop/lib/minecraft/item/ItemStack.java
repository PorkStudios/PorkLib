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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.misc.Cloneable;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.nbt.tag.CompoundTag;

/**
 * Representation of an item stack.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
@Getter
@Setter
@Accessors(fluent = true, chain = true)
public class ItemStack implements Cloneable<ItemStack> {
    @NonNull
    protected Identifier id;

    protected int size;

    protected ItemMeta meta;

    public ItemStack(@NonNull Identifier id)    {
        this(id, 0, null);
    }

    public ItemStack(@NonNull Identifier id, int size)    {
        this(id, size, null);
    }

    @Override
    public ItemStack clone() {
        return new ItemStack(this.id, this.size, this.meta == null ? null : this.meta.clone());
    }

    @Override
    public String toString() {
        try (Handle<StringBuilder> handle = PorkUtil.STRINGBUILDER_POOL.get()) {
            StringBuilder builder = handle.get();
            builder.setLength(0);

            builder.append(this.id);
            if (this.size != 1) {
                builder.append('*').append(this.size);
            }
            if (this.meta != null)  {
                builder.append(" with meta ").append(this.meta);
            }
            return builder.toString();
        }
    }
}
