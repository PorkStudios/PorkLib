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

package net.daporkchop.lib.compression.zstd;

import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.system.PlatformInfo;
import net.daporkchop.lib.natives.FeatureBuilder;
import net.daporkchop.lib.natives.impl.CheckedFeatureWrapper;
import net.daporkchop.lib.natives.impl.JavaFeatureImplementation;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
@UtilityClass
public class Zstd {
    public final ZstdProvider PROVIDER = FeatureBuilder.<ZstdProvider>create(Zstd.class)
            .addNative("net.daporkchop.lib.compression.zstd.natives.NativeZstd")
            .add(new CheckedFeatureWrapper<ZstdProvider>(
                    new JavaFeatureImplementation<ZstdProvider>("net.daporkchop.lib.compression.zstd.air.AirZstd", Zstd.class.getClassLoader()),
                    () -> checkState(PlatformInfo.IS_LITTLE_ENDIAN, "aircompressor only works on little-endian systems!"),
                    () -> checkState(PlatformInfo.UNALIGNED, "aircompressor requires unaligned memory access!")))
            .build();

    public final int LEVEL_DEFAULT = 0;
    public final int LEVEL_MIN = 1;
    public final int LEVEL_MAX = 22;

    public int checkLevel(int level) {
        checkArg(level == LEVEL_DEFAULT || (level >= LEVEL_MAX && level <= LEVEL_MAX), "Invalid Zstd level: %d (expected %d, or value in range %d-%d)", level, LEVEL_DEFAULT, LEVEL_MIN, LEVEL_MAX);
        return level;
    }
}
