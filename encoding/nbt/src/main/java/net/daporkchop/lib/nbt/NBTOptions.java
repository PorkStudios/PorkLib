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

package net.daporkchop.lib.nbt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.pool.array.ArrayAllocator;

/**
 * Additional options used when processing NBT data.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public final class NBTOptions {
    public static final NBTOptions DEFAULT = new NBTOptions(null, null, null, true, false);

    /**
     * The {@link ArrayAllocator} used for allocating {@code byte[]}s.
     * <p>
     * If {@code null}, {@code byte[]}s will be allocated using {@code new}.
     */
    protected final ArrayAllocator<byte[]> byteAlloc;

    /**
     * The {@link ArrayAllocator} used for allocating {@code int[]}s.
     * <p>
     * If {@code null}, {@code int[]}s will be allocated using {@code new}.
     */
    protected final ArrayAllocator<int[]> intAlloc;

    /**
     * The {@link ArrayAllocator} used for allocating {@code long[]}s.
     * <p>
     * If {@code null}, {@code long[]}s will be allocated using {@code new}.
     */
    protected final ArrayAllocator<long[]> longAlloc;

    /**
     * Controls whether or not array sizes must be exact.
     * <p>
     * If {@code false} and a non-null {@link ArrayAllocator} is configured for a given type, arrays for that type will be allocated using
     * {@link ArrayAllocator#atLeast(int)} instead of {@link ArrayAllocator#exactly(int)}.
     */
    protected final boolean exactArraySize;

    /**
     * Controls whether or not duplicate tag names are allowed in compound tags.
     * <p>
     * If {@code false} and a duplicate tag name is read, an {@link IllegalStateException} will be thrown.
     * <p>
     * If {@code true} and a duplicate tag name is read, the older value will be silently replaced.
     */
    protected final boolean allowDuplicates;

    public NBTOptions withByteAlloc(ArrayAllocator<byte[]> byteAlloc) {
        if (byteAlloc == this.byteAlloc) {
            return this;
        }
        return new NBTOptions(byteAlloc, this.intAlloc, this.longAlloc, this.exactArraySize, this.allowDuplicates);
    }

    public NBTOptions withIntAlloc(ArrayAllocator<int[]> intAlloc) {
        if (intAlloc == this.intAlloc) {
            return this;
        }
        return new NBTOptions(this.byteAlloc, intAlloc, this.longAlloc, this.exactArraySize, this.allowDuplicates);
    }

    public NBTOptions withLongAlloc(ArrayAllocator<long[]> longAlloc) {
        if (longAlloc == this.longAlloc) {
            return this;
        }
        return new NBTOptions(this.byteAlloc, this.intAlloc, longAlloc, this.exactArraySize, this.allowDuplicates);
    }

    public NBTOptions withExactArraySize(boolean exactArraySize) {
        if (exactArraySize == this.exactArraySize) {
            return this;
        }
        return new NBTOptions(this.byteAlloc, this.intAlloc, this.longAlloc, exactArraySize, this.allowDuplicates);
    }

    public NBTOptions withDuplicates(boolean allowDuplicates) {
        if (allowDuplicates == this.allowDuplicates) {
            return this;
        }
        return new NBTOptions(this.byteAlloc, this.intAlloc, this.longAlloc, this.exactArraySize, allowDuplicates);
    }
}
