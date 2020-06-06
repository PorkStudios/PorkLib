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

package net.daporkchop.lib.minecraft.block;

import net.daporkchop.lib.minecraft.util.Identifier;

/**
 * Represents a block state: the block's {@link Identifier} combined with a metadata value.
 *
 * @author DaPorkchop_
 * @see BlockRegistry for an explanation of what all the values mean
 */
public interface BlockState {
    /**
     * @return the block's {@link Identifier}
     */
    Identifier id();

    /**
     * @return the block's legacy ID
     */
    int legacyId();

    /**
     * @return the block state's metadata value
     */
    int meta();

    /**
     * @return the block state's runtime ID
     */
    int runtimeId();

    /**
     * Gets a {@link BlockState} with the same block {@link Identifier} and the given metadata value.
     *
     * @param meta the new metadata value
     * @return a {@link BlockState} with the given metadata value
     * @throws IllegalArgumentException if a state with the given metadata value was not registered for the block
     */
    BlockState withMeta(int meta);

    /**
     * @return the {@link BlockRegistry} that this block state belongs to
     */
    BlockRegistry registry();
}
