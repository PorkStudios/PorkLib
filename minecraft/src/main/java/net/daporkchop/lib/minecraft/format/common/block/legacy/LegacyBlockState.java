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

package net.daporkchop.lib.minecraft.format.common.block.legacy;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.minecraft.util.Identifier;
import net.daporkchop.lib.minecraft.block.BlockState;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Implementation of {@link BlockState} used by {@link LegacyBlockRegistry}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Accessors(fluent = true)
final class LegacyBlockState implements BlockState {
    @NonNull
    protected final Identifier id;
    protected final int legacyId;
    protected final int meta;
    protected final int runtimeId;
    @NonNull
    protected final LegacyBlockState[] states;
    @NonNull
    protected final LegacyBlockRegistry registry;

    @Override
    public BlockState withMeta(int meta) {
        checkArg(meta >= 0 && meta < 16, "meta (%d) must be in range 0-15!", meta);
        return this.states[meta];
    }

    @Override
    public String toString() {
        return this.id.toString() + '#' + this.meta;
    }
}
