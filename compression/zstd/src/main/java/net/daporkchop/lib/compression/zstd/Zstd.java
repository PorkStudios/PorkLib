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
import net.daporkchop.lib.natives.FeatureLoader;

import java.lang.invoke.MethodHandles;

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

    private static ZstdOneshotProvider DEFAULT_ONESHOT_PROVIDER;
    private static ZstdStreamingProvider DEFAULT_STREAMING_PROVIDER;

    public synchronized static ZstdOneshotProvider getDefaultOneshotProvider() {
        if (DEFAULT_ONESHOT_PROVIDER != null) {
            return DEFAULT_ONESHOT_PROVIDER;
        }

        return DEFAULT_ONESHOT_PROVIDER = FeatureLoader.loadService(MethodHandles.lookup(), ZstdOneshotProvider.class);
    }

    public synchronized static ZstdStreamingProvider getDefaultStreamingProvider() {
        if (DEFAULT_STREAMING_PROVIDER != null) {
            return DEFAULT_STREAMING_PROVIDER;
        }

        return DEFAULT_STREAMING_PROVIDER = FeatureLoader.loadService(MethodHandles.lookup(), ZstdStreamingProvider.class);
    }
}
