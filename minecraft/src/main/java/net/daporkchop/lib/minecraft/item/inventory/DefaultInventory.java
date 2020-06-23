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

package net.daporkchop.lib.minecraft.item.inventory;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.minecraft.item.ItemStack;
import net.daporkchop.lib.primitive.lambda.IntObjConsumer;
import net.daporkchop.lib.primitive.map.IntObjMap;
import net.daporkchop.lib.primitive.map.open.IntObjOpenHashMap;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * A default implementation of an inventory backed by a simple array.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(onConstructor_ = {@Deprecated})
public class DefaultInventory extends BaseInventory {
    protected final IntObjMap<ItemStack> map;
    protected final int max;

    public DefaultInventory() {
        this.map = new IntObjOpenHashMap<>();
        this.max = -1;
    }

    public DefaultInventory(int slots) {
        this.map = new IntObjOpenHashMap<>(slots);
        this.max = slots;
    }

    @Override
    public int count() {
        return this.map.size();
    }

    @Override
    public ItemStack get(int slot) {
        this.checkSlotIndex(slot);
        return this.map.get(slot);
    }

    @Override
    public Inventory set(int slot, ItemStack stack) {
        this.checkSlotIndex(slot);
        if (stack != null) {
            this.map.put(slot, stack);
        } else {
            this.map.remove(slot);
        }
        return this;
    }

    @Override
    public void forEach(@NonNull IntObjConsumer<ItemStack> action) {
        this.map.forEach(action);
    }

    protected void checkSlotIndex(int slot) {
        checkIndex(slot >= 0 && (this.max < 0 || slot < this.max), slot);
    }
}
