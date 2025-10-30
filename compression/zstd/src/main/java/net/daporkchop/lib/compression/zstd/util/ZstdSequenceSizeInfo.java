/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2025 DaPorkchop_
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
 */

package net.daporkchop.lib.compression.zstd.util;

import lombok.Value;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.annotation.ValueBased;
import net.daporkchop.lib.common.annotation.param.NotNegative;

import java.util.OptionalLong;

/**
 * @author DaPorkchop_
 */
@Value
@ValueBased
@Accessors(fluent = true)
public final class ZstdSequenceSizeInfo {
    /**
     * The number of frames in this sequence.
     */
    private final @NotNegative long numFrames;

    /**
     * A lower bound on the decompressed size of all the frames in this sequence.
     */
    private final @NotNegative long decompressedSizeLowerBound;

    /**
     * An upper bound on the decompressed size of all the frames in this sequence.
     */
    private final @NotNegative long decompressedSizeUpperBound;

    /**
     * @return the exact decompressed size of all the frames in this sequence, or an empty optional if the exact size is unknown
     */
    public @NotNegative OptionalLong decompressedSizeExact() {
        return this.decompressedSizeLowerBound == this.decompressedSizeUpperBound ? OptionalLong.of(this.decompressedSizeLowerBound) : OptionalLong.empty();
    }
}
