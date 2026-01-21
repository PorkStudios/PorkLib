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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Magic numbers used in the ZSTD format.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ZstdConstants {
    public static final int MAGIC_SIZE = Integer.BYTES;
    public static final int MAGIC_NUMBER_V7 = 0xFD2FB527;
    public static final int MAGIC_NUMBER_V8 = 0xFD2FB528;

    public static final int ZSTD_SKIPPABLE_HEADER_SIZE = 8;
    public static final int ZSTD_MAGIC_SKIPPABLE_START = 0x184D2A50;
    public static final int ZSTD_MAGIC_SKIPPABLE_MASK = 0xFFFFFFF0;

    public static final int FRAME_HEADER_DESCRIPTOR_SIZE = Byte.BYTES;
    public static final int MIN_FRAME_HEADER_SIZE = 2;
    public static final int MAX_FRAME_HEADER_SIZE = 14;

    public static final int BLOCK_HEADER_SIZE = 3;

    public static final int BLOCK_TYPE_RAW = 0;
    public static final int BLOCK_TYPE_RLE = 1;
    public static final int BLOCK_TYPE_COMPRESSED = 2;

    public static final int MAX_BLOCK_SIZE = 128 * 1024;

    public static final int FRAME_CHECKSUM_SIZE = 4;
}
