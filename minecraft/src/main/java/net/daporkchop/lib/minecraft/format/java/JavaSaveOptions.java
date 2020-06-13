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

package net.daporkchop.lib.minecraft.format.java;

import lombok.experimental.UtilityClass;
import net.daporkchop.lib.minecraft.save.SaveOptions;

/**
 * {@link SaveOptions} keys used by Java edition save formats.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class JavaSaveOptions {
    /**
     * The maximum number of regions files that may be open at once.
     * <p>
     * May not be negative.
     */
    public static final SaveOptions.Key<Integer> REGION_CACHE_SIZE = SaveOptions.key("java_region_cache_size", 1024);

    /**
     * Whether or not regions should be opened using memory-mapping rather than traditional read/write methods.
     * <p>
     * This is likely to improve performance when loading chunks, but could have negative effects on systems with low memory or slow read speeds.
     * <p>
     * May only be set if the world is read-only.
     */
    public static final SaveOptions.Key<Boolean> MMAP_REGIONS = SaveOptions.key("java_region_mmap", false);

    /**
     * Whether or not memory-mapped regions should be prefetched from disk when opened.
     * <p>
     * Will have no effect unless the world is set to read-only.
     */
    public static final SaveOptions.Key<Boolean> PREFETCH_REGIONS = SaveOptions.key("java_region_mmap_prefetch", false);

    /**
     * The {@link JavaFixers} to use when decoding things.
     * <p>
     * Defaults to {@link JavaFixers#defaultFixers()}.
     */
    public static final SaveOptions.Key<JavaFixers> FIXERS = SaveOptions.keyLazy("java_fixers", JavaFixers::defaultFixers);
}
