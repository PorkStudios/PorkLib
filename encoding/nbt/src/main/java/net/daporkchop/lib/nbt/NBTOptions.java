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
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.pool.array.ArrayAllocator;
import net.daporkchop.lib.nbt.tag.Tag;
import net.daporkchop.lib.nbt.util.NBTObjectParser;

/**
 * Additional options used when processing NBT data.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public final class NBTOptions {
    public static final NBTOptions DEFAULT = new NBTOptions(Tag.DEFAULT_NBT_PARSER, false, false, false);

    /**
     * The {@link NBTObjectParser} used for actually creating the NBT tag objects when reading an object tree.
     * <p>
     * May not be {@code null}.
     */
    protected final NBTObjectParser parser;

    /**
     * Controls whether or not duplicate tag names are allowed in compound tags.
     * <p>
     * If {@code false} and a duplicate tag name is read, an {@link IllegalStateException} will be thrown.
     * <p>
     * If {@code true} and a duplicate tag name is read, the older value will be silently replaced.
     */
    protected final boolean allowDuplicates;

    /**
     * Controls whether or not compound tag keys should be interned.
     */
    protected final boolean internKeys;

    /**
     * Controls whether {@link String} tag values should be interned.
     */
    protected final boolean internValues;

    public NBTOptions withObjectParser(@NonNull NBTObjectParser parser) {
        if (parser == this.parser) {
            return this;
        }
        return new NBTOptions(parser, this.allowDuplicates, this.internKeys, this.internValues);
    }

    public NBTOptions withDuplicates(boolean allowDuplicates) {
        if (allowDuplicates == this.allowDuplicates) {
            return this;
        }
        return new NBTOptions(this.parser, allowDuplicates, this.internKeys, this.internValues);
    }

    public NBTOptions withInternedKeys(boolean internKeys) {
        if (internKeys == this.internKeys) {
            return this;
        }
        return new NBTOptions(this.parser, this.allowDuplicates, internKeys, this.internValues);
    }

    public NBTOptions withInternedValues(boolean internValues) {
        if (internValues == this.internValues) {
            return this;
        }
        return new NBTOptions(this.parser, this.allowDuplicates, this.internKeys, internValues);
    }
}
