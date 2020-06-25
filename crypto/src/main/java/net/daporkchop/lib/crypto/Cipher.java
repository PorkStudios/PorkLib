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

package net.daporkchop.lib.crypto;

import io.netty.buffer.ByteBuf;
import lombok.NonNull;
import net.daporkchop.lib.common.misc.refcount.RefCounted;
import net.daporkchop.lib.crypto.key.Key;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * Base representation of a cipher.
 * <p>
 * A cipher consumes plain-/ciphertext and en-/decrypts it.
 * <p>
 * Ciphers are stateful and not thread-safe. They are initialized in encryption or decryption mode with a key, and
 * may buffer unprocessed data internally.
 *
 * @author DaPorkchop_
 */
public interface Cipher extends RefCounted {
    /**
     * Resets this cipher to its initial state, erasing any stored keys/buffers.
     * <p>
     * A cipher may be reset at any time. Once reset, a cipher cannot be used again until initialized.
     */
    void reset();

    void init(boolean encrypt, @NonNull Key key);

    /**
     * Reads data from the source buffer, processes it, and writes it to the destination buffer.
     * <p>
     * Implementations may apply any restrictions they like to the size/content of the source data, however passing an
     * empty source buffer is generally expected to do nothing.
     * <p>
     * Using the same buffer for both parameters will result in undefined behavior.
     *
     * @param src the {@link ByteBuf} containing the source data
     * @param dst the {@link ByteBuf} that processed data will be written to
     */
    void process(@NonNull ByteBuf src, @NonNull ByteBuf dst);

    @Override
    int refCnt();

    @Override
    Cipher retain() throws AlreadyReleasedException;

    @Override
    boolean release() throws AlreadyReleasedException;
}
