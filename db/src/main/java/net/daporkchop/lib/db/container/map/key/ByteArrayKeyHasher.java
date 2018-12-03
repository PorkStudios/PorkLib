/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

package net.daporkchop.lib.db.container.map.key;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.hash.HashAlg;

/**
 * Allows hashing a byte array using one of the hashing algorithms defined in {@link HashAlg}
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class ByteArrayKeyHasher implements KeyHasher<byte[]> {
    @RequiredArgsConstructor
    @Getter
    public static class ConstantLength implements KeyHasher<byte[]> {
        private final int hashLength;

        @Override
        public byte[] hash(@NonNull byte[] key) {
            return key.clone();
        }

        @Override
        public boolean canReconstructFromHash() {
            return true;
        }

        @Override
        public byte[] reconstructFromHash(@NonNull byte[] hash) {
            return hash.clone();
        }
    }

    @NonNull
    private final HashAlg alg;

    @Override
    public byte[] hash(@NonNull byte[] key) {
        return this.alg.hash(key);
    }

    @Override
    public int getHashLength() {
        return this.alg.getLength();
    }
}
