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

package net.daporkchop.lib.db.container.map.key;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Wrapper class which stores a bunch of {@link KeyHasher}s for primitive types
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PrimitiveKeyHasher {
    /**
     * A key hasher that can hash a 1-byte (8-bit) {@link Byte} integral value
     */
    public static final KeyHasher<Byte> BYTE = new KeyHasher.ThreadLocalKeyHasher<Byte>(1) {
        @Override
        protected void doHash(@NonNull Byte key, @NonNull byte[] b) {
            b[0] = key;
        }

        @Override
        public boolean canReconstructFromHash() {
            return true;
        }

        @Override
        public Byte reconstructFromHash(@NonNull byte[] hash) {
            return hash[0];
        }
    };

    /**
     * A key hasher that can hash a 2-byte (16-bit) {@link Short} integral value
     */
    public static final KeyHasher<Short> SHORT = new KeyHasher.ThreadLocalKeyHasher<Short>(2) {
        @Override
        protected void doHash(@NonNull Short key, @NonNull byte[] b) {
            short s = key; //prevent tons of unboxing
            b[0] = (byte) (s & 0xFF);
            b[1] = (byte) ((s >>> 8) & 0xFF);
        }

        @Override
        public boolean canReconstructFromHash() {
            return true;
        }

        @Override
        public Short reconstructFromHash(byte[] hash) {
            return (short) ((hash[0] & 0xFF) |
                    ((hash[1] & 0xFF) << 8));
        }
    };

    /**
     * A key hasher that can hash a 4-byte (32-bit) {@link Integer} integral value
     */
    public static final KeyHasher<Integer> INTEGER = new KeyHasher.ThreadLocalKeyHasher<Integer>(4) {
        @Override
        protected void doHash(@NonNull Integer key, @NonNull byte[] b) {
            int i = key; //prevent tons of unboxing
            b[0] = (byte) (i & 0xFF);
            b[1] = (byte) ((i >>> 8) & 0xFF);
            b[2] = (byte) ((i >>> 16) & 0xFF);
            b[3] = (byte) ((i >>> 24) & 0xFF);
        }

        @Override
        public boolean canReconstructFromHash() {
            return true;
        }

        @Override
        public Integer reconstructFromHash(byte[] hash) {
            return (hash[0] & 0xFF) |
                    ((hash[1] & 0xFF) << 8) |
                    ((hash[2] & 0xFF) << 16) |
                    ((hash[3] & 0xFF) << 24);
        }
    };

    /**
     * A key hasher that can hash a 8-byte (64-bit) {@link Long} integral value
     */
    public static final KeyHasher<Long> LONG = new KeyHasher.ThreadLocalKeyHasher<Long>(8) {
        @Override
        protected void doHash(@NonNull Long key, @NonNull byte[] b) {
            long l = key; //prevent tons of unboxing
            b[0] = (byte) (l & 0xFFL);
            b[1] = (byte) ((l >>> 8L) & 0xFFL);
            b[2] = (byte) ((l >>> 16L) & 0xFFL);
            b[3] = (byte) ((l >>> 24L) & 0xFFL);
            b[4] = (byte) ((l >>> 32L) & 0xFFL);
            b[5] = (byte) ((l >>> 40L) & 0xFFL);
            b[6] = (byte) ((l >>> 48L) & 0xFFL);
            b[7] = (byte) ((l >>> 56L) & 0xFFL);
        }

        @Override
        public boolean canReconstructFromHash() {
            return true;
        }

        @Override
        public Long reconstructFromHash(byte[] hash) {
            return (hash[0] & 0xFFL) |
                    ((hash[1] & 0xFFL) << 8L) |
                    ((hash[2] & 0xFFL) << 16L) |
                    ((hash[3] & 0xFFL) << 24L) |
                    ((hash[4] & 0xFFL) << 32L) |
                    ((hash[5] & 0xFFL) << 40L) |
                    ((hash[6] & 0xFFL) << 48L) |
                    ((hash[7] & 0xFFL) << 56L);
        }
    };

    /**
     * A key hasher that can hash a 4-byte (32-bit) {@link Float} floating-point value
     */
    public static final KeyHasher<Float> FLOAT = new KeyHasher.ThreadLocalKeyHasher<Float>(4) {
        @Override
        protected void doHash(@NonNull Float key, @NonNull byte[] b) {
            int i = Float.floatToIntBits(key); //prevent tons of unboxing
            b[0] = (byte) (i & 0xFF);
            b[1] = (byte) ((i >>> 8) & 0xFF);
            b[2] = (byte) ((i >>> 16) & 0xFF);
            b[3] = (byte) ((i >>> 24) & 0xFF);
        }

        @Override
        public boolean canReconstructFromHash() {
            return true;
        }

        @Override
        public Float reconstructFromHash(byte[] hash) {
            return Float.intBitsToFloat(((hash[0] & 0xFF) |
                    ((hash[1] & 0xFF) << 8) |
                    ((hash[2] & 0xFF) << 16) |
                    ((hash[3] & 0xFF) << 24)));
        }
    };

    /**
     * A key hasher that can hash a 8-byte (64-bit) {@link Double} floating-point value
     */
    public static final KeyHasher<Double> DOUBLE = new KeyHasher.ThreadLocalKeyHasher<Double>(8) {
        @Override
        protected void doHash(@NonNull Double key, @NonNull byte[] b) {
            long l = Double.doubleToLongBits(key); //prevent tons of unboxing
            b[0] = (byte) (l & 0xFFL);
            b[1] = (byte) ((l >>> 8L) & 0xFFL);
            b[2] = (byte) ((l >>> 16L) & 0xFFL);
            b[3] = (byte) ((l >>> 24L) & 0xFFL);
            b[4] = (byte) ((l >>> 32L) & 0xFFL);
            b[5] = (byte) ((l >>> 40L) & 0xFFL);
            b[6] = (byte) ((l >>> 48L) & 0xFFL);
            b[7] = (byte) ((l >>> 56L) & 0xFFL);
        }

        @Override
        public boolean canReconstructFromHash() {
            return true;
        }

        @Override
        public Double reconstructFromHash(byte[] hash) {
            return Double.longBitsToDouble(((hash[0] & 0xFFL) |
                    ((hash[1] & 0xFFL) << 8L) |
                    ((hash[2] & 0xFFL) << 16L) |
                    ((hash[3] & 0xFFL) << 24L) |
                    ((hash[4] & 0xFFL) << 32L) |
                    ((hash[5] & 0xFFL) << 40L) |
                    ((hash[6] & 0xFFL) << 48L) |
                    ((hash[7] & 0xFFL) << 56L)));
        }
    };
}
