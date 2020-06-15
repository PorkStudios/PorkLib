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

package net.daporkchop.lib.minecraft.format.vanilla;

import lombok.experimental.UtilityClass;
import net.daporkchop.lib.minecraft.save.SaveOptions;

import java.util.concurrent.TimeUnit;

/**
 * {@link SaveOptions} keys used by vanilla-style save formats.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class VanillaSaveOptions {
    /**
     * The maximum number chunks whose data may be cached in memory at once.
     * <p>
     * May not be negative.
     */
    public static final SaveOptions.Key<Integer> CHUNK_CACHE_SIZE = SaveOptions.key("vanilla_chunk_cache_size", 1024);

    /**
     * The maximum time (in milliseconds) that chunk data may remain cached in memory before eviction.
     * <p>
     * This does not guarantee that chunks cached longer than this duration will be immediately evicted. Generally, whenever the cache fills up, all chunks
     * older than this duration will be evicted, and if no chunks match, the oldest ones will be evicted regardless of this value.
     * <p>
     * May not be negative.
     */
    public static final SaveOptions.Key<Long> MAX_CHUNK_CACHE_TIME = SaveOptions.key("vanilla_chunk_cache_time", TimeUnit.MINUTES.toMillis(15L));
}
