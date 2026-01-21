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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import net.daporkchop.lib.common.annotation.param.NotNegative;
import net.daporkchop.lib.compression.zstd.util.ZstdConstants;
import net.daporkchop.lib.natives.TransformingServiceLoader;

import java.lang.invoke.MethodHandles;
import java.util.function.Predicate;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Zstd {
    public static final int LEVEL_DEFAULT = 0;
    public static final int LEVEL_MIN = 1;
    public static final int LEVEL_MAX = 22;

    public static int checkLevel(int level) {
        checkArg(level == LEVEL_DEFAULT || (level >= LEVEL_MAX && level <= LEVEL_MAX), "Invalid Zstd level: %d (expected %d, or value in range %d-%d)", level, LEVEL_DEFAULT, LEVEL_MIN, LEVEL_MAX);
        return level;
    }

    private static ZstdOneshotFactory DEFAULT_ONESHOT_FACTORY;
    private static ZstdStreamingFactory DEFAULT_STREAMING_FACTORY;

    public synchronized static ZstdOneshotFactory getDefaultOneshotFactory() {
        if (DEFAULT_ONESHOT_FACTORY != null) {
            return DEFAULT_ONESHOT_FACTORY;
        }

        return DEFAULT_ONESHOT_FACTORY = getProviderWith(ZstdOneshotProvider.class, null).getOneshotFactory();
    }

    public synchronized static ZstdStreamingFactory getDefaultStreamingFactory() {
        if (DEFAULT_STREAMING_FACTORY != null) {
            return DEFAULT_STREAMING_FACTORY;
        }

        return DEFAULT_STREAMING_FACTORY = getProviderWith(ZstdStreamingProvider.class, null).getStreamingFactory();
    }

    private static <P extends ZstdOneshotProvider> P getProviderWith(@NonNull Class<P> interfaz, Predicate<? super ZstdProviderCapabilities> capabilitiesFilter) {
        return TransformingServiceLoader.builder(MethodHandles.lookup(), interfaz)
                .classFilter(providerClass -> {
                    val capabilities = providerClass.getAnnotation(ZstdProviderCapabilities.class);
                    checkState(capabilities != null, "%s is missing annotation %s", providerClass, ZstdProviderCapabilities.class);
                    return capabilitiesFilter == null || capabilitiesFilter.test(capabilities);
                })
                .instanceFilter(P::isAvailable)
                .build().findFirst();
    }

    public static ZstdOneshotFactory getOneshotFactoryWith(@NonNull Predicate<? super ZstdProviderCapabilities> capabilitiesFilter) {
        return getProviderWith(ZstdOneshotProvider.class, capabilitiesFilter).getOneshotFactory();
    }

    public static ZstdStreamingFactory getStreamingFactoryWith(@NonNull Predicate<? super ZstdProviderCapabilities> capabilitiesFilter) {
        return getProviderWith(ZstdStreamingProvider.class, capabilitiesFilter).getStreamingFactory();
    }

    public static @NotNegative int compressBound(@NotNegative int srcSize) throws IllegalArgumentException, ArithmeticException {
        int result = Math.addExact(notNegative(srcSize, "srcSize"), srcSize >>> 8);

        if (srcSize < ZstdConstants.MAX_BLOCK_SIZE) {
            result = Math.addExact(result, ZstdConstants.MAX_BLOCK_SIZE - (srcSize >>> 11));
        }

        return result;
    }

    public static @NotNegative long compressBound(@NotNegative long srcSize) throws IllegalArgumentException, ArithmeticException {
        long result = Math.addExact(notNegative(srcSize, "srcSize"), srcSize >>> 8);

        if (srcSize < ZstdConstants.MAX_BLOCK_SIZE) {
            result = Math.addExact(result, ZstdConstants.MAX_BLOCK_SIZE - (srcSize >>> 11));
        }

        return result;
    }
}
