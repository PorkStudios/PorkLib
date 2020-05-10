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

package net.daporkchop.lib.minecraft.registry;

import lombok.NonNull;
import net.daporkchop.lib.minecraft.util.Identifier;

/**
 * {@link Registry} implementation for blocks.
 *
 * @author DaPorkchop_
 */
public final class BlockRegistry extends AbstractRegistry {
    public static Builder builder() {
        return new Builder();
    }

    private BlockRegistry(@NonNull AbstractRegistry.Builder builder) {
        super(builder);
    }

    public static final class Builder extends AbstractRegistry.Builder {
        private Builder() {
            super(Identifier.fromString("minecraft:blocks"));
        }

        @Override
        public Builder register(@NonNull Identifier identifier, int id) {
            super.register(identifier, id);
            return this;
        }

        @Override
        public BlockRegistry build() {
            return new BlockRegistry(this);
        }
    }
}
