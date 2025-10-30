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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.annotation.ValueBased;
import net.daporkchop.lib.common.annotation.param.NotNegative;
import net.daporkchop.lib.common.annotation.param.Positive;

import java.util.OptionalInt;
import java.util.OptionalLong;

/**
 * @author DaPorkchop_
 */
@Value
@ValueBased
@Accessors(fluent = true)
public final class ZstdFrameHeader {
    /**
     * The size of the frame header, in bytes.
     */
    private final @Positive int headerSize;

    /**
     * The frame window size, or {@code -1} if not present in the header.
     */
    private final long windowSize;

    /**
     * The maximum size of a block.
     */
    private final @NotNegative int blockSizeMax;

    /**
     * The frame dictionary ID, or {@code 0} if no dictionary will be used.
     */
    private final int dictionaryId;

    /**
     * The frame content size, or {@code -1} if not present in the header.
     */
    private final long contentSize;

    private final boolean singleSegmentFlag;
    private final boolean contentChecksumFlag;

    public @NotNegative OptionalLong windowSize() {
        return this.windowSize != -1 ? OptionalLong.of(this.windowSize) : OptionalLong.empty();
    }

    public OptionalInt dictionaryId() {
        return this.dictionaryId != 0 ? OptionalInt.of(this.dictionaryId) : OptionalInt.empty();
    }

    public @NotNegative OptionalLong contentSize() {
        return this.contentSize != -1 ? OptionalLong.of(this.contentSize) : OptionalLong.empty();
    }
}
