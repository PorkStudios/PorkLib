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

package net.daporkchop.lib.minecraft.save.codec;

import lombok.NonNull;
import net.daporkchop.lib.nbt.tag.CompoundTag;

/**
 * Encodes/decodes a Minecraft object to/from its {@link CompoundTag} form.
 * <p>
 * Implementations must be stateless and therefore thread-safe.
 *
 * @author DaPorkchop_
 */
public interface MinecraftCodec<V> {
    /**
     * Decodes the given value.
     *
     * @param tag         the encoded value data
     * @param dataVersion the data version that the value is encoded with, or {@code 0} if it is unknown
     * @return the decoded value
     */
    V decode(@NonNull CompoundTag tag, int dataVersion);

    /**
     * Encodes the given value.
     *
     * @param value the value to encode
     * @return the encoded value data, along with its data version (if applicable)
     */
    EncodedObject encode(@NonNull V value);
}
