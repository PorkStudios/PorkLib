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
public final class ZlibDeflaterOptions extends ZlibOptions<ZlibDeflaterOptions> implements DeflaterOptions<ZlibDeflaterOptions, ZlibProvider> {
    protected final ZlibStrategy strategy;

    protected final int level;

    public ZlibDeflaterOptions(@NonNull ZlibProvider provider) {
        super(provider, ZlibMode.ZLIB);

        this.strategy = ZlibStrategy.DEFAULT;
        this.level = Zlib.LEVEL_DEFAULT;
    }

    private ZlibDeflaterOptions(ZlibProvider provider, ZlibMode mode, ZlibStrategy strategy, int level) {
        super(provider, mode);

        this.strategy = strategy;
        this.level = level;
    }

    @Override
    public ZlibDeflaterOptions withMode(@NonNull ZlibMode mode) {
        checkArg(mode.compression(), "Zlib mode %s can't be used for compression!", mode);
        if (mode == this.mode) {
            return this;
        }
        return new ZlibDeflaterOptions(this.provider, mode, this.strategy, this.level);
    }

    public ZlibDeflaterOptions withStrategy(@NonNull ZlibStrategy strategy) {
        if (strategy == this.strategy) {
            return this;
        }
        return new ZlibDeflaterOptions(this.provider, this.mode, strategy, this.level);
    }

    public ZlibDeflaterOptions withLevel(int level) {
        checkArg(level == Zlib.LEVEL_DEFAULT || (level >= Zlib.LEVEL_NONE && level <= Zlib.LEVEL_BEST), "Invalid Zlib level: %d (expected %d, or value in range %d-%d)", level, Zlib.LEVEL_DEFAULT, Zlib.LEVEL_NONE, Zlib.LEVEL_BEST);
        if (level == this.level) {
            return this;
        }
        return new ZlibDeflaterOptions(this.provider, this.mode, this.strategy, level);
    }
}
