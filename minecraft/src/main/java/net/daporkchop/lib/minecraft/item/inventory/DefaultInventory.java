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
import lombok.ToString;
import net.daporkchop.lib.minecraft.item.ItemStack;

/**
 * A default implementation of an inventory backed by a simple array.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(onConstructor_ = {@Deprecated})
@ToString
public class DefaultInventory implements Inventory {
    @NonNull
    protected final ItemStack[] slots;

    public DefaultInventory(int slots) {
        this.slots = new ItemStack[slots];
    }

    @Override
    public int size() {
        return this.slots.length;
    }

    @Override
    public ItemStack get(int slot) {
        return this.slots[slot];
    }

    @Override
    public Inventory set(int slot, @NonNull ItemStack stack) {
        this.slots[slot] = stack;
        return this;
    }
}
