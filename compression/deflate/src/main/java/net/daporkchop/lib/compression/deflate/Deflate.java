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

package net.daporkchop.lib.compression.deflate;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;
import net.daporkchop.lib.natives.TransformingServiceLoader;

import java.lang.invoke.MethodHandles;
import java.util.function.Predicate;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Deflate {
    private static DeflateOneshotFactory DEFAULT_ONESHOT_FACTORY;
    private static DeflateStreamingFactory DEFAULT_STREAMING_FACTORY;

    public synchronized static DeflateOneshotFactory getDefaultOneshotFactory() {
        if (DEFAULT_ONESHOT_FACTORY != null) {
            return DEFAULT_ONESHOT_FACTORY;
        }

        return DEFAULT_ONESHOT_FACTORY = getProviderWith(DeflateOneshotProvider.class, null).getOneshotFactory();
    }

    public synchronized static DeflateStreamingFactory getDefaultStreamingFactory() {
        if (DEFAULT_STREAMING_FACTORY != null) {
            return DEFAULT_STREAMING_FACTORY;
        }

        return DEFAULT_STREAMING_FACTORY = getProviderWith(DeflateStreamingProvider.class, null).getStreamingFactory();
    }

    private static <P extends DeflateOneshotProvider> P getProviderWith(@NonNull Class<P> interfaz, Predicate<? super DeflateProviderCapabilities> capabilitiesFilter) {
        return TransformingServiceLoader.builder(MethodHandles.lookup(), interfaz)
                .classFilter(providerClass -> {
                    val capabilities = providerClass.getAnnotation(DeflateProviderCapabilities.class);
                    checkState(capabilities != null, "%s is missing annotation %s", providerClass, DeflateProviderCapabilities.class);
                    return capabilitiesFilter == null || capabilitiesFilter.test(capabilities);
                })
                .instanceFilter(P::isAvailable)
                .build().findFirst();
    }

    public static DeflateOneshotFactory getOneshotFactoryWith(@NonNull Predicate<? super DeflateProviderCapabilities> capabilitiesFilter) {
        return getProviderWith(DeflateOneshotProvider.class, capabilitiesFilter).getOneshotFactory();
    }

    public static DeflateStreamingFactory getStreamingFactoryWith(@NonNull Predicate<? super DeflateProviderCapabilities> capabilitiesFilter) {
        return getProviderWith(DeflateStreamingProvider.class, capabilitiesFilter).getStreamingFactory();
    }
}
