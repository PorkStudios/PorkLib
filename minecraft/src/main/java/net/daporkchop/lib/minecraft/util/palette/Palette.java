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

package net.daporkchop.lib.minecraft.util.palette;

import lombok.NonNull;
import net.daporkchop.lib.minecraft.block.BlockState;

/**
 * A mapping of {@link BlockState}s to numeric IDs.
 * <p>
 * The IDs are only used internally for chunk sections that use a palette.
 *
 * @author DaPorkchop_
 */
public interface Palette {
    /**
     * Checks whether or not this palette contains the given {@link BlockState}.
     *
     * @param state the {@link BlockState} to check for
     * @return whether or not this palette contains the given {@link BlockState}
     */
    boolean contains(@NonNull BlockState state);

    /**
     * Gets the ID mapped to the given {@link BlockState}.
     * <p>
     * If the given {@link BlockState} is not known, it will be added and a new one computed.
     *
     * @param state the {@link BlockState} to get the ID for
     * @return the {@link BlockState}'s ID
     */
    int get(@NonNull BlockState state);

    /**
     * Gets the {@link BlockState} mapped to the given ID.
     *
     * @param id the ID to get the {@link BlockState} for
     * @return the ID's {@link BlockState}, or {@code null} if it is not known
     */
    BlockState get(int id);
}
