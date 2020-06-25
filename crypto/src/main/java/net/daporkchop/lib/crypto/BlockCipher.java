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
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * Representation of a block cipher.
 * <p>
 * Block ciphers are symmetric ciphers which process data in fixed-size blocks. Plaintext and ciphertext are always
 * exactly the same length, and data is only accepted in a size multiple of the block size.
 *
 * @author DaPorkchop_
 */
public interface BlockCipher extends SymmetricCipher {
    @Override
    void process(@NonNull ByteBuf src, @NonNull ByteBuf dst);

    /**
     * @return the size of the blocks used by this cipher, in bytes
     */
    int blockSize();

    @Override
    BlockCipher retain() throws AlreadyReleasedException;
}
