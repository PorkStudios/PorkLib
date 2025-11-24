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

package net.daporkchop.lib.compression.zstd;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.annotation.ValueBased;
import net.daporkchop.lib.natives.util.MemoryPreference;

/**
 * The features supported by a ZSTD implementation.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
@Builder(toBuilder = true)
@Value
@ValueBased
public final class ZstdImplementationCapabilities implements ZstdImplementationCaps {
    /**
     * @return an instance of {@link ZstdImplementationCapabilities} which doesn't explicitly require any capabilities
     */
    public static ZstdImplementationCapabilities none() {
        return builder().build();
    }

    public static ZstdImplementationCapabilities fromAnnotation(@NonNull ZstdImplementationCaps annotation) {
        if (annotation instanceof ZstdImplementationCapabilities) {
            return (ZstdImplementationCapabilities) annotation;
        }

        return new ZstdImplementationCapabilities(
                annotation.memoryPreference(),
                annotation.supportsStreaming(),
                annotation.supportsDictionary(),
                annotation.supportsCompressionLevel(),
                annotation.supportsChecksumFlag(),
                annotation.supportsContentSizeFlag(),
                annotation.supportsDictIdFlag());
    }

    /**
     * The type of memory which this implementation prefers to operate on.
     */
    @Builder.Default
    private final @NonNull MemoryPreference memoryPreference = MemoryPreference.ANY;

    /**
     * {@code true} iff. the implementation supports compression with a non-default level.
     */
    private final boolean supportsStreaming;

    /**
     * {@code true} iff. the implementation supports using a ZSTD dictionary for compression and/or decompression.
     */
    private final boolean supportsDictionary;
    /**
     * {@code true} iff. the implementation supports compression with a non-default level.
     */
    private final boolean supportsCompressionLevel;

    /**
     * {@code true} iff. the implementation supports compression with the checksum flag set to {@code false}.
     */
    private final boolean supportsChecksumFlag;
    /**
     * {@code true} iff. the implementation supports compression with the content size flag set to {@code false}.
     */
    private final boolean supportsContentSizeFlag;
    /**
     * {@code true} iff. the implementation supports compression with the dictionary ID flag set to {@code false}.
     */
    private final boolean supportsDictIdFlag;

    /**
     * Checks if this instance indicates support for all of the requested capabilities.
     *
     * @param requested the requested features
     * @return {@code true} if this instance indicates support for all of the requested capabilities
     */
    public boolean supportsAllRequested(@NonNull ZstdImplementationCapabilities requested) {
        return (!requested.supportsStreaming || this.supportsStreaming)
                && (!requested.supportsDictionary || this.supportsDictionary)
                && (!requested.supportsCompressionLevel || this.supportsCompressionLevel)
                && (!requested.supportsChecksumFlag || this.supportsChecksumFlag)
                && (!requested.supportsContentSizeFlag || this.supportsContentSizeFlag)
                && (!requested.supportsDictIdFlag || this.supportsDictIdFlag);
    }

    /**
     * Checks if the given capabilities supports all of the capabilities requested by this instance
     *
     * @param supported the supported capabilities
     * @return {@code true} if the given capabilities supports all of the capabilities requested by this instance
     */
    public boolean supportedBy(@NonNull ZstdImplementationCaps supported) {
        return (!this.supportsStreaming || supported.supportsStreaming())
                && (!this.supportsDictionary || supported.supportsDictionary())
                && (!this.supportsCompressionLevel || supported.supportsCompressionLevel())
                && (!this.supportsChecksumFlag || supported.supportsChecksumFlag())
                && (!this.supportsContentSizeFlag || supported.supportsContentSizeFlag())
                && (!this.supportsDictIdFlag || supported.supportsDictIdFlag());
    }

    @Override
    public Class<ZstdImplementationCaps> annotationType() {
        return ZstdImplementationCaps.class;
    }
}
