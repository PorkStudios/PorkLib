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

package net.daporkchop.lib.minecraft.tile;

import lombok.NonNull;
import net.daporkchop.lib.minecraft.text.ChatFormat;
import net.daporkchop.lib.minecraft.util.Identifier;

/**
 * Representation of a {@code "minecraft:sign"} tile entity.
 *
 * @author DaPorkchop_
 */
public interface TileEntitySign extends TileEntity {
    Identifier ID = Identifier.fromString("minecraft:sign");

    @Override
    default Identifier id() {
        return ID;
    }

    /**
     * @return an array containing the 4 lines of text displayed on the sign
     */
    String[] lines();

    /**
     * Sets the line of text at the given index.
     *
     * @param index the index of the line to set
     * @param text  the new text to set
     * @return this instance
     * @throws IndexOutOfBoundsException if the given index is outside of the range {@code [0-4)}
     */
    TileEntitySign line(int index, @NonNull String text);

    /**
     * @return the color that has been used to dye the sign. The default value is {@link ChatFormat#BLACK}
     */
    ChatFormat color();

    /**
     * Sets the color that has been used to dye the sign.
     *
     * @param color the new color
     * @return this instance
     * @throws IllegalArgumentException if the given {@link ChatFormat} is not a color
     */
    TileEntitySign color(@NonNull ChatFormat color);
}
