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

package net.daporkchop.lib.compression.zstd.air.v0;

import net.daporkchop.lib.common.util.PThrowables;
import net.daporkchop.lib.compression.zstd.ZstdImplementationCapabilities;
import net.daporkchop.lib.compression.zstd.ZstdImplementationCaps;
import net.daporkchop.lib.compression.zstd.ZstdOneshotImplementation;
import net.daporkchop.lib.compression.zstd.ZstdOneshotFactory;
import net.daporkchop.lib.natives.util.MemoryPreference;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.nio.ByteOrder;

/**
 * @author DaPorkchop_
 */
@ZstdImplementationCaps(
        memoryPreference = MemoryPreference.ANY,
        supportsStreaming = false,
        supportsDictionary = false,
        supportsCompressionLevel = false,
        supportsChecksumFlag = false,
        supportsContentSizeFlag = false,
        supportsDictIdFlag = false)
public final class AircompressorV0ZstdImplementation implements ZstdOneshotImplementation {
    private static final Throwable UNAVAILABILITY_CAUSE;

    static {
        if (ByteOrder.nativeOrder() != ByteOrder.LITTLE_ENDIAN) {
            UNAVAILABILITY_CAUSE = PThrowables.setUnknownStackTrace(new IllegalStateException("aircompressor only works on little-endian systems!"));
        } else if (!PUnsafe.isUnalignedAccessSupported()) {
            UNAVAILABILITY_CAUSE = PThrowables.setUnknownStackTrace(new IllegalStateException("aircompressor requires unaligned memory access!"));
        } else {
            UNAVAILABILITY_CAUSE = null;
        }
    }

    @Override
    public Throwable unavailabilityCause() {
        return UNAVAILABILITY_CAUSE;
    }

    @Override
    public ZstdImplementationCapabilities capabilities() {
        return null;
    }

    @Override
    public ZstdOneshotFactory getOneshotFactory() throws UnsatisfiedLinkError {
        this.ensureAvailability();
        return new AircompressorV0ZstdOneshotFactory();
    }
}
