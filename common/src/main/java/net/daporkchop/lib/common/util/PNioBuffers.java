/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2025 DaPorkchop_
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
 */

package net.daporkchop.lib.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import net.daporkchop.lib.common.annotation.param.NotNegative;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

/**
 * Generic helper methods for working with NIO buffers.
 *
 * @author DaPorkchop_
 */
//TODO: move the methods from the identically named class in :binary
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PNioBuffers {
    /**
     * The buffer's position is set to {@code offset}, the limit is set to {@code offset + length}, and the mark is discarded.
     *
     * @param buffer the buffer
     * @param offset the new position
     * @param length the new number of remaining elements
     * @return the buffer
     */
    public static <B extends Buffer> B range(B buffer, @NotNegative int offset, @NotNegative int length) {
        PValidation.checkRangeLen(buffer.capacity(), offset, length);
        buffer.clear().position(offset).limit(offset + length);
        return buffer;
    }

    /**
     * Gets a {@link ByteBuffer#duplicate() duplicate} of the given buffer. The new buffer's position is set to {@code offset}, the limit is set to
     * {@code offset + length}, the mark is discarded, and the byte order is preserved.
     *
     * @param buffer the original buffer
     * @param offset the new position
     * @param length the new number of remaining elements
     * @return the duplicated buffer
     */
    public static ByteBuffer duplicateRange(ByteBuffer buffer, @NotNegative int offset, @NotNegative int length) {
        return range(buffer.duplicate(), offset, length).order(buffer.order());
    }

    /**
     * Gets a {@link ShortBuffer#duplicate() duplicate} of the given buffer. The new buffer's position is set to {@code offset}, the limit is set to
     * {@code offset + length}, the mark is discarded, and the byte order is preserved.
     *
     * @param buffer the original buffer
     * @param offset the new position
     * @param length the new number of remaining elements
     * @return the duplicated buffer
     */
    public static ShortBuffer duplicateRange(ShortBuffer buffer, @NotNegative int offset, @NotNegative int length) {
        return range(buffer.duplicate(), offset, length);
    }

    /**
     * Gets a {@link CharBuffer#duplicate() duplicate} of the given buffer. The new buffer's position is set to {@code offset}, the limit is set to
     * {@code offset + length}, the mark is discarded, and the byte order is preserved.
     *
     * @param buffer the original buffer
     * @param offset the new position
     * @param length the new number of remaining elements
     * @return the duplicated buffer
     */
    public static CharBuffer duplicateRange(CharBuffer buffer, @NotNegative int offset, @NotNegative int length) {
        return range(buffer.duplicate(), offset, length);
    }

    /**
     * Gets a {@link IntBuffer#duplicate() duplicate} of the given buffer. The new buffer's position is set to {@code offset}, the limit is set to
     * {@code offset + length}, the mark is discarded, and the byte order is preserved.
     *
     * @param buffer the original buffer
     * @param offset the new position
     * @param length the new number of remaining elements
     * @return the duplicated buffer
     */
    public static IntBuffer duplicateRange(IntBuffer buffer, @NotNegative int offset, @NotNegative int length) {
        return range(buffer.duplicate(), offset, length);
    }

    /**
     * Gets a {@link LongBuffer#duplicate() duplicate} of the given buffer. The new buffer's position is set to {@code offset}, the limit is set to
     * {@code offset + length}, the mark is discarded, and the byte order is preserved.
     *
     * @param buffer the original buffer
     * @param offset the new position
     * @param length the new number of remaining elements
     * @return the duplicated buffer
     */
    public static LongBuffer duplicateRange(LongBuffer buffer, @NotNegative int offset, @NotNegative int length) {
        return range(buffer.duplicate(), offset, length);
    }

    /**
     * Gets a {@link FloatBuffer#duplicate() duplicate} of the given buffer. The new buffer's position is set to {@code offset}, the limit is set to
     * {@code offset + length}, the mark is discarded, and the byte order is preserved.
     *
     * @param buffer the original buffer
     * @param offset the new position
     * @param length the new number of remaining elements
     * @return the duplicated buffer
     */
    public static FloatBuffer duplicateRange(FloatBuffer buffer, @NotNegative int offset, @NotNegative int length) {
        return range(buffer.duplicate(), offset, length);
    }

    /**
     * Gets a {@link DoubleBuffer#duplicate() duplicate} of the given buffer. The new buffer's position is set to {@code offset}, the limit is set to
     * {@code offset + length}, the mark is discarded, and the byte order is preserved.
     *
     * @param buffer the original buffer
     * @param offset the new position
     * @param length the new number of remaining elements
     * @return the duplicated buffer
     */
    public static DoubleBuffer duplicateRange(DoubleBuffer buffer, @NotNegative int offset, @NotNegative int length) {
        return range(buffer.duplicate(), offset, length);
    }

    /**
     * Copies the contents of the given buffer into a new {@code byte[]}. The buffer's {@link Buffer#position() position} is modified.
     *
     * @param buffer the buffer
     * @return a new {@code byte[]} containing the given buffer's contents
     */
    public static byte[] readToArray(ByteBuffer buffer) {
        val result = PUnsafe.allocateUninitializedByteArray(buffer.remaining());
        buffer.get(result);
        return result;
    }

    /**
     * Copies the contents of the given buffer into a new {@code short[]}. The buffer's {@link Buffer#position() position} is modified.
     *
     * @param buffer the buffer
     * @return a new {@code short[]} containing the given buffer's contents
     */
    public static short[] readToArray(ShortBuffer buffer) {
        val result = PUnsafe.allocateUninitializedShortArray(buffer.remaining());
        buffer.get(result);
        return result;
    }

    /**
     * Copies the contents of the given buffer into a new {@code char[]}. The buffer's {@link Buffer#position() position} is modified.
     *
     * @param buffer the buffer
     * @return a new {@code char[]} containing the given buffer's contents
     */
    public static char[] readToArray(CharBuffer buffer) {
        val result = PUnsafe.allocateUninitializedCharArray(buffer.remaining());
        buffer.get(result);
        return result;
    }

    /**
     * Copies the contents of the given buffer into a new {@code int[]}. The buffer's {@link Buffer#position() position} is modified.
     *
     * @param buffer the buffer
     * @return a new {@code int[]} containing the given buffer's contents
     */
    public static int[] readToArray(IntBuffer buffer) {
        val result = PUnsafe.allocateUninitializedIntArray(buffer.remaining());
        buffer.get(result);
        return result;
    }

    /**
     * Copies the contents of the given buffer into a new {@code long[]}. The buffer's {@link Buffer#position() position} is modified.
     *
     * @param buffer the buffer
     * @return a new {@code long[]} containing the given buffer's contents
     */
    public static long[] readToArray(LongBuffer buffer) {
        val result = PUnsafe.allocateUninitializedLongArray(buffer.remaining());
        buffer.get(result);
        return result;
    }

    /**
     * Copies the contents of the given buffer into a new {@code float[]}. The buffer's {@link Buffer#position() position} is modified.
     *
     * @param buffer the buffer
     * @return a new {@code float[]} containing the given buffer's contents
     */
    public static float[] readToArray(FloatBuffer buffer) {
        val result = PUnsafe.allocateUninitializedFloatArray(buffer.remaining());
        buffer.get(result);
        return result;
    }

    /**
     * Copies the contents of the given buffer into a new {@code double[]}. The buffer's {@link Buffer#position() position} is modified.
     *
     * @param buffer the buffer
     * @return a new {@code double[]} containing the given buffer's contents
     */
    public static double[] readToArray(DoubleBuffer buffer) {
        val result = PUnsafe.allocateUninitializedDoubleArray(buffer.remaining());
        buffer.get(result);
        return result;
    }

    /**
     * Copies the first {@code n} elements of the given buffer's contents into a new {@code byte[]}. The buffer's {@link Buffer#position() position} is modified.
     *
     * @param buffer the buffer
     * @param n      the number of elements to copy
     * @return a new {@code byte[]} containing the first {@code n} elements of the given buffer's contents
     */
    public static byte[] readToArray(ByteBuffer buffer, @NotNegative int n) {
        val result = PUnsafe.allocateUninitializedByteArray(n);
        buffer.get(result);
        return result;
    }

    /**
     * Copies the first {@code n} elements of the given buffer's contents into a new {@code short[]}. The buffer's {@link Buffer#position() position} is modified.
     *
     * @param buffer the buffer
     * @param n      the number of elements to copy
     * @return a new {@code short[]} containing the first {@code n} elements of the given buffer's contents
     */
    public static short[] readToArray(ShortBuffer buffer, @NotNegative int n) {
        val result = PUnsafe.allocateUninitializedShortArray(n);
        buffer.get(result);
        return result;
    }

    /**
     * Copies the first {@code n} elements of the given buffer's contents into a new {@code char[]}. The buffer's {@link Buffer#position() position} is modified.
     *
     * @param buffer the buffer
     * @param n      the number of elements to copy
     * @return a new {@code char[]} containing the first {@code n} elements of the given buffer's contents
     */
    public static char[] readToArray(CharBuffer buffer, @NotNegative int n) {
        val result = PUnsafe.allocateUninitializedCharArray(n);
        buffer.get(result);
        return result;
    }

    /**
     * Copies the first {@code n} elements of the given buffer's contents into a new {@code int[]}. The buffer's {@link Buffer#position() position} is modified.
     *
     * @param buffer the buffer
     * @param n      the number of elements to copy
     * @return a new {@code int[]} containing the first {@code n} elements of the given buffer's contents
     */
    public static int[] readToArray(IntBuffer buffer, @NotNegative int n) {
        val result = PUnsafe.allocateUninitializedIntArray(n);
        buffer.get(result);
        return result;
    }

    /**
     * Copies the first {@code n} elements of the given buffer's contents into a new {@code long[]}. The buffer's {@link Buffer#position() position} is modified.
     *
     * @param buffer the buffer
     * @param n      the number of elements to copy
     * @return a new {@code long[]} containing the first {@code n} elements of the given buffer's contents
     */
    public static long[] readToArray(LongBuffer buffer, @NotNegative int n) {
        val result = PUnsafe.allocateUninitializedLongArray(n);
        buffer.get(result);
        return result;
    }

    /**
     * Copies the first {@code n} elements of the given buffer's contents into a new {@code float[]}. The buffer's {@link Buffer#position() position} is modified.
     *
     * @param buffer the buffer
     * @param n      the number of elements to copy
     * @return a new {@code float[]} containing the first {@code n} elements of the given buffer's contents
     */
    public static float[] readToArray(FloatBuffer buffer, @NotNegative int n) {
        val result = PUnsafe.allocateUninitializedFloatArray(n);
        buffer.get(result);
        return result;
    }

    /**
     * Copies the first {@code n} elements of the given buffer's contents into a new {@code double[]}. The buffer's {@link Buffer#position() position} is modified.
     *
     * @param buffer the buffer
     * @param n      the number of elements to copy
     * @return a new {@code double[]} containing the first {@code n} elements of the given buffer's contents
     */
    public static double[] readToArray(DoubleBuffer buffer, @NotNegative int n) {
        val result = PUnsafe.allocateUninitializedDoubleArray(n);
        buffer.get(result);
        return result;
    }

    /**
     * Copies the contents of the given buffer into a new {@code byte[]}. The buffer's {@link Buffer#position() position} is not modified.
     *
     * @param buffer the buffer
     * @return a new {@code byte[]} containing the given buffer's contents
     */
    public static byte[] toArray(ByteBuffer buffer) {
        //the object allocation in duplicate() will hopefully get optimized away
        return readToArray(buffer.duplicate());
    }

    /**
     * Copies the contents of the given buffer into a new {@code short[]}. The buffer's {@link Buffer#position() position} is not modified.
     *
     * @param buffer the buffer
     * @return a new {@code short[]} containing the given buffer's contents
     */
    public static short[] toArray(ShortBuffer buffer) {
        //the object allocation in duplicate() will hopefully get optimized away
        return readToArray(buffer.duplicate());
    }

    /**
     * Copies the contents of the given buffer into a new {@code char[]}. The buffer's {@link Buffer#position() position} is not modified.
     *
     * @param buffer the buffer
     * @return a new {@code char[]} containing the given buffer's contents
     */
    public static char[] toArray(CharBuffer buffer) {
        //the object allocation in duplicate() will hopefully get optimized away
        return readToArray(buffer.duplicate());
    }

    /**
     * Copies the contents of the given buffer into a new {@code int[]}. The buffer's {@link Buffer#position() position} is not modified.
     *
     * @param buffer the buffer
     * @return a new {@code int[]} containing the given buffer's contents
     */
    public static int[] toArray(IntBuffer buffer) {
        //the object allocation in duplicate() will hopefully get optimized away
        return readToArray(buffer.duplicate());
    }

    /**
     * Copies the contents of the given buffer into a new {@code long[]}. The buffer's {@link Buffer#position() position} is not modified.
     *
     * @param buffer the buffer
     * @return a new {@code long[]} containing the given buffer's contents
     */
    public static long[] toArray(LongBuffer buffer) {
        //the object allocation in duplicate() will hopefully get optimized away
        return readToArray(buffer.duplicate());
    }

    /**
     * Copies the contents of the given buffer into a new {@code float[]}. The buffer's {@link Buffer#position() position} is not modified.
     *
     * @param buffer the buffer
     * @return a new {@code float[]} containing the given buffer's contents
     */
    public static float[] toArray(FloatBuffer buffer) {
        //the object allocation in duplicate() will hopefully get optimized away
        return readToArray(buffer.duplicate());
    }

    /**
     * Copies the contents of the given buffer into a new {@code double[]}. The buffer's {@link Buffer#position() position} is not modified.
     *
     * @param buffer the buffer
     * @return a new {@code double[]} containing the given buffer's contents
     */
    public static double[] toArray(DoubleBuffer buffer) {
        //the object allocation in duplicate() will hopefully get optimized away
        return readToArray(buffer.duplicate());
    }

    /**
     * Copies the range {@code [offset, offset + length)} of the given buffer's contents into a new {@code byte[]}. The buffer's {@link Buffer#position() position} is not modified.
     *
     * @param buffer the buffer
     * @param offset the index of the first element in the buffer
     * @param length the number of elements to copy
     * @return a new {@code byte[ the given buffer's contents in the given range
     */
    public static byte[] toArray(ByteBuffer buffer, @NotNegative int offset, @NotNegative int length) {
        //the object allocation in duplicate() will hopefully get optimized away
        return readToArray(duplicateRange(buffer, offset, length));
    }

    /**
     * Copies the range {@code [offset, offset + length)} of the given buffer's contents} into a new {@code short[]}. The buffer's {@link Buffer#position() position} is not modified.
     *
     * @param buffer the buffer
     * @param offset the index of the first element in the buffer
     * @param length the number of elements to copy
     * @return a new {@code short[]} containing the given buffer's contents in the given range
     */
    public static short[] toArray(ShortBuffer buffer, @NotNegative int offset, @NotNegative int length) {
        //the object allocation in duplicate() will hopefully get optimized away
        return readToArray(duplicateRange(buffer, offset, length));
    }

    /**
     * Copies the range {@code [offset, offset + length)} of the given buffer's contents into a new {@code char[]}. The buffer's {@link Buffer#position() position} is not modified.
     *
     * @param buffer the buffer
     * @param offset the index of the first element in the buffer
     * @param length the number of elements to copy
     * @return a new {@code char[]} containing the given buffer's contents in the given range
     */
    public static char[] toArray(CharBuffer buffer, @NotNegative int offset, @NotNegative int length) {
        //the object allocation in duplicate() will hopefully get optimized away
        return readToArray(duplicateRange(buffer, offset, length));
    }

    /**
     * Copies  the range {@code [offset, offset + length)} of the given buffer's contents into a new {@code int[]}. The buffer's {@link Buffer#position() position} is not modified.
     *
     * @param buffer the buffer
     * @param offset the index of the first element in the buffer
     * @param length the number of elements to copy
     * @return a new {@code int[]} containing the given buffer's contents in the given range
     */
    public static int[] toArray(IntBuffer buffer, @NotNegative int offset, @NotNegative int length) {
        //the object allocation in duplicate() will hopefully get optimized away
        return readToArray(duplicateRange(buffer, offset, length));
    }

    /**
     * Copies the range {@code [offset, offset + length)} of the given buffer's contents into a new {@code long[]}. The buffer's {@link Buffer#position() position} is not modified.
     *
     * @param buffer the buffer
     * @param offset the index of the first element in the buffer
     * @param length the number of elements to copy
     * @return a new {@code long[]} containing the given buffer's contents in the given range
     */
    public static long[] toArray(LongBuffer buffer, @NotNegative int offset, @NotNegative int length) {
        //the object allocation in duplicate() will hopefully get optimized away
        return readToArray(duplicateRange(buffer, offset, length));
    }

    /**
     * Copies the range {@code [offset, offset + length)} of the given buffer's contents into a new {@code float[]}. The buffer's {@link Buffer#position() position} is not modified.
     *
     * @param buffer the buffer
     * @param offset the index of the first element in the buffer
     * @param length the number of elements to copy
     * @return a new {@code float[]} containing the given buffer's contents in the given range
     */
    public static float[] toArray(FloatBuffer buffer, @NotNegative int offset, @NotNegative int length) {
        //the object allocation in duplicate() will hopefully get optimized away
        return readToArray(duplicateRange(buffer, offset, length));
    }

    /**
     * Copies the range {@code [offset, offset + length)} of the given buffer's contents into a new {@code double[]}. The buffer's {@link Buffer#position() position} is not modified.
     *
     * @param buffer the buffer
     * @param offset the index of the first element in the buffer
     * @param length the number of elements to copy
     * @return a new {@code double[]} containing the given buffer's contents in the given range
     */
    public static double[] toArray(DoubleBuffer buffer, @NotNegative int offset, @NotNegative int length) {
        //the object allocation in duplicate() will hopefully get optimized away
        return readToArray(duplicateRange(buffer, offset, length));
    }

    /**
     * Allocates a new {@link ByteBuffer} with direct memory using the native {@link ByteOrder}.
     *
     * @param capacity the capacity
     * @return a new {@link ByteBuffer}
     */
    public static ByteBuffer allocateDirectNativeByte(@NotNegative int capacity) {
        return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
    }

    /**
     * Allocates a new {@link ShortBuffer} with direct memory using the native {@link ByteOrder}.
     *
     * @param capacity the capacity
     * @return a new {@link ShortBuffer}
     */
    public static ShortBuffer allocateDirectNativeShort(@NotNegative int capacity) {
        return allocateDirectNativeByte(Math.multiplyExact(capacity, Short.BYTES)).asShortBuffer();
    }

    /**
     * Allocates a new {@link CharBuffer} with direct memory using the native {@link ByteOrder}.
     *
     * @param capacity the capacity
     * @return a new {@link CharBuffer}
     */
    public static CharBuffer allocateDirectNativeChar(@NotNegative int capacity) {
        return allocateDirectNativeByte(Math.multiplyExact(capacity, Character.BYTES)).asCharBuffer();
    }

    /**
     * Allocates a new {@link IntBuffer} with direct memory using the native {@link ByteOrder}.
     *
     * @param capacity the capacity
     * @return a new {@link IntBuffer}
     */
    public static IntBuffer allocateDirectNativeInt(@NotNegative int capacity) {
        return allocateDirectNativeByte(Math.multiplyExact(capacity, Integer.BYTES)).asIntBuffer();
    }

    /**
     * Allocates a new {@link LongBuffer} with direct memory using the native {@link ByteOrder}.
     *
     * @param capacity the capacity
     * @return a new {@link LongBuffer}
     */
    public static LongBuffer allocateDirectNativeLong(@NotNegative int capacity) {
        return allocateDirectNativeByte(Math.multiplyExact(capacity, Long.BYTES)).asLongBuffer();
    }

    /**
     * Allocates a new {@link FloatBuffer} with direct memory using the native {@link ByteOrder}.
     *
     * @param capacity the capacity
     * @return a new {@link FloatBuffer}
     */
    public static FloatBuffer allocateDirectNativeFloat(@NotNegative int capacity) {
        return allocateDirectNativeByte(Math.multiplyExact(capacity, Float.BYTES)).asFloatBuffer();
    }

    /**
     * Allocates a new {@link DoubleBuffer} with direct memory using the native {@link ByteOrder}.
     *
     * @param capacity the capacity
     * @return a new {@link DoubleBuffer}
     */
    public static DoubleBuffer allocateDirectNativeDouble(@NotNegative int capacity) {
        return allocateDirectNativeByte(Math.multiplyExact(capacity, Double.BYTES)).asDoubleBuffer();
    }
}
