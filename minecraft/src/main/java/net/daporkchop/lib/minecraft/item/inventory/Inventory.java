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
import net.daporkchop.lib.minecraft.item.ItemStack;
import net.daporkchop.lib.primitive.lambda.IntObjConsumer;

/**
 * Base representation of an inventory.
 *
 * @author DaPorkchop_
 */
public interface Inventory {
    /**
     * @return the number of non-empty slots in this inventory
     */
    int count();

    /**
     * Gets the {@link ItemStack} at the given slot index.
     *
     * @param slot the slot index
     * @return the {@link ItemStack} at the given slot index
     * @throws IndexOutOfBoundsException if the given slot index is out of bounds
     */
    ItemStack get(int slot);

    /**
     * Sets the {@link ItemStack} at the given slot index.
     *
     * @param slot  the slot index
     * @param stack the new {@link ItemStack}
     * @return this inventory
     */
    Inventory set(int slot, ItemStack stack);

    /**
     * Executes the given function for every non-empty slot in this inventory.
     * <p>
     * The first parameter is the slot index, the second one is the item at the slot.
     *
     * @param action the function to run
     */
    void forEach(@NonNull IntObjConsumer<ItemStack> action);
}
