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

package net.daporkchop.lib.compression.zlib.options;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.compression.option.DeflaterOptions;
import net.daporkchop.lib.compression.zlib.Zlib;
import net.daporkchop.lib.compression.zlib.ZlibMode;
import net.daporkchop.lib.compression.zlib.ZlibProvider;
import net.daporkchop.lib.compression.zlib.ZlibStrategy;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public final class ZlibDeflaterOptions extends ZlibOptions implements DeflaterOptions<ZlibDeflaterOptions, ZlibDeflaterOptions.Builder, ZlibProvider> {
    protected final ZlibStrategy strategy;

    protected final int level;

    protected ZlibDeflaterOptions(ZlibProvider provider, ZlibMode mode, ZlibStrategy strategy, int level) {
        super(provider, mode);

        this.strategy = strategy;
        this.level = level;
    }

    @Override
    public Builder builder() {
        return new Builder(this.provider)
                .mode(this.mode)
                .strategy(this.strategy)
                .level(this.level);
    }

    public static final class Builder extends ZlibOptions.Builder<Builder> implements DeflaterOptions.Builder<Builder, ZlibDeflaterOptions, ZlibProvider> {
        protected ZlibStrategy strategy = ZlibStrategy.DEFAULT;

        protected int level = Zlib.LEVEL_DEFAULT;

        public Builder(ZlibProvider provider) {
            super(provider);
        }

        @Override
        public Builder mode(@NonNull ZlibMode mode) {
            checkArg(mode.compression(), "Zlib mode %s can't be used for compression!", mode);
            return super.mode(mode);
        }

        public Builder strategy(@NonNull ZlibStrategy strategy) {
            this.strategy = strategy;
            return this;
        }

        public Builder level(int level) {
            checkArg(level == Zlib.LEVEL_DEFAULT || (level >= Zlib.LEVEL_NONE && level <= Zlib.LEVEL_BEST), "Invalid Zlib level: %d (expected %d, or value in range %d-%d)", level, Zlib.LEVEL_DEFAULT, Zlib.LEVEL_NONE, Zlib.LEVEL_BEST);
            this.level = level;
            return this;
        }

        @Override
        public ZlibDeflaterOptions build() {
            return new ZlibDeflaterOptions(this.provider, this.mode, this.strategy, this.level);
        }
    }
}
