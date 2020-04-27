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

import lombok.NonNull;
import net.daporkchop.lib.compression.option.InflaterOptions;
import net.daporkchop.lib.compression.zlib.ZlibMode;
import net.daporkchop.lib.compression.zlib.ZlibProvider;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
public final class ZlibInflaterOptions extends ZlibOptions implements InflaterOptions<ZlibInflaterOptions, ZlibInflaterOptions.Builder, ZlibProvider> {
    protected ZlibInflaterOptions(ZlibProvider provider, ZlibMode mode) {
        super(provider, mode);
    }

    @Override
    public Builder builder() {
        return new Builder(this.provider)
                .mode(this.mode);
    }

    public static final class Builder extends ZlibOptions.Builder<Builder> implements InflaterOptions.Builder<Builder, ZlibInflaterOptions, ZlibProvider> {
        public Builder(ZlibProvider provider) {
            super(provider);
        }

        @Override
        public Builder mode(@NonNull ZlibMode mode) {
            checkArg(mode.compression(), "Zlib mode %s can't be used for decompression!", mode);
            return super.mode(mode);
        }

        @Override
        public ZlibInflaterOptions build() {
            return new ZlibInflaterOptions(this.provider, this.mode);
        }
    }
}
