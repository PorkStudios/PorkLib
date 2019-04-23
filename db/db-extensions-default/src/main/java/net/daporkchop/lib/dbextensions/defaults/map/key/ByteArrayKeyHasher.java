/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.dbextensions.defaults.map.key;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.dbextensions.defaults.map.KeyHasher;
import net.daporkchop.lib.hash.util.Digest;
import net.daporkchop.lib.logging.Logging;

/**
 * Allows hashing a byte array using a {@link Digest}
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class ByteArrayKeyHasher implements KeyHasher<byte[]> {
    @NonNull
    private final Digest digest;

    @Override
    public byte[] hash(@NonNull byte[] key) {
        return this.digest.hash(key).getHash();
    }

    @Override
    public int getHashLength() {
        return this.digest.getHashSize();
    }

    @RequiredArgsConstructor
    @Getter
    public static class ConstantLength implements KeyHasher<byte[]>, Logging {
        private final int hashLength;

        @Override
        public byte[] hash(@NonNull byte[] key) {
            if (key.length != this.hashLength) {
                throw this.exception("Invalid byte[] size: ${0} (expected: ${1}", key.length, this.hashLength);
            }
            return key.clone();
        }

        @Override
        public boolean canReconstructFromHash() {
            return true;
        }

        @Override
        public byte[] reconstructFromHash(@NonNull byte[] hash) {
            if (hash.length != this.hashLength) {
                throw this.exception("Invalid byte[] size: ${0} (expected: ${1}", hash.length, this.hashLength);
            }
            return hash.clone();
        }
    }
}
