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
     * @author DaPorkchop_
     */
    private static final class EmptyBuffers {
        static final ByteBuffer BYTE_DIRECT = ByteBuffer.allocateDirect(0).duplicate();
        static final ByteBuffer BYTE_HEAP = ByteBuffer.wrap(PorkUtil.emptyByteArray());
        
        static final ShortBuffer SHORT_DIRECT = BYTE_DIRECT.asShortBuffer();
        static final ShortBuffer SHORT_HEAP = ShortBuffer.wrap(PorkUtil.emptyShortArray());

        static final CharBuffer CHAR_DIRECT = BYTE_DIRECT.asCharBuffer();
        static final CharBuffer CHAR_HEAP = CharBuffer.wrap(PorkUtil.emptyCharArray());

        static final IntBuffer INT_DIRECT = BYTE_DIRECT.asIntBuffer();
        static final IntBuffer INT_HEAP = IntBuffer.wrap(PorkUtil.emptyIntArray());

        static final LongBuffer LONG_DIRECT = BYTE_DIRECT.asLongBuffer();
        static final LongBuffer LONG_HEAP = LongBuffer.wrap(PorkUtil.emptyLongArray());
        
        static final FloatBuffer FLOAT_DIRECT = BYTE_DIRECT.asFloatBuffer();
        static final FloatBuffer FLOAT_HEAP = FloatBuffer.wrap(PorkUtil.emptyFloatArray());
        
        static final DoubleBuffer DOUBLE_DIRECT = BYTE_DIRECT.asDoubleBuffer();
        static final DoubleBuffer DOUBLE_HEAP = DoubleBuffer.wrap(PorkUtil.emptyDoubleArray());
    }

    /**
     * @return an empty {@link ByteBuffer} backed by direct memory
     */
    public static ByteBuffer emptyByteBufferDirect() {
        return EmptyBuffers.BYTE_DIRECT;
    }

    /**
     * @return an empty {@link ByteBuffer} backed by a Java heap array
     */
    public static ByteBuffer emptyByteBufferHeap() {
        return EmptyBuffers.BYTE_HEAP;
    }

    /**
     * @return an empty {@link ShortBuffer} backed by direct memory
     */
    public static ShortBuffer emptyShortBufferDirect() {
        return EmptyBuffers.SHORT_DIRECT;
    }

    /**
     * @return an empty {@link ShortBuffer} backed by a Java heap array
     */
    public static ShortBuffer emptyShortBufferHeap() {
        return EmptyBuffers.SHORT_HEAP;
    }

    /**
     * @return an empty {@link CharBuffer} backed by direct memory
     */
    public static CharBuffer emptyCharBufferDirect() {
        return EmptyBuffers.CHAR_DIRECT;
    }

    /**
     * @return an empty {@link CharBuffer} backed by a Java heap array
     */
    public static CharBuffer emptyCharBufferHeap() {
        return EmptyBuffers.CHAR_HEAP;
    }

    /**
     * @return an empty {@link IntBuffer} backed by direct memory
     */
    public static IntBuffer emptyIntBufferDirect() {
        return EmptyBuffers.INT_DIRECT;
    }

    /**
     * @return an empty {@link IntBuffer} backed by a Java heap array
     */
    public static IntBuffer emptyIntBufferHeap() {
        return EmptyBuffers.INT_HEAP;
    }

    /**
     * @return an empty {@link LongBuffer} backed by direct memory
     */
    public static LongBuffer emptyLongBufferDirect() {
        return EmptyBuffers.LONG_DIRECT;
    }

    /**
     * @return an empty {@link LongBuffer} backed by a Java heap array
     */
    public static LongBuffer emptyLongBufferHeap() {
        return EmptyBuffers.LONG_HEAP;
    }

    /**
     * @return an empty {@link FloatBuffer} backed by direct memory
     */
    public static FloatBuffer emptyFloatBufferDirect() {
        return EmptyBuffers.FLOAT_DIRECT;
    }

    /**
     * @return an empty {@link FloatBuffer} backed by a Java heap array
     */
    public static FloatBuffer emptyFloatBufferHeap() {
        return EmptyBuffers.FLOAT_HEAP;
    }

    /**
     * @return an empty {@link DoubleBuffer} backed by direct memory
     */
    public static DoubleBuffer emptyDoubleBufferDirect() {
        return EmptyBuffers.DOUBLE_DIRECT;
    }

    /**
     * @return an empty {@link DoubleBuffer} backed by a Java heap array
     */
    public static DoubleBuffer emptyDoubleBufferHeap() {
        return EmptyBuffers.DOUBLE_HEAP;
    }

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

    /**
     * Copies the range {@code [srcOffset, srcOffset + length)} of the given buffer's contents to the range {@code [dstOffset, dstOffset + length)} of the given buffer. The buffer's {@link Buffer#position() positions} are not modified.
     *
     * @param src       the source
     * @param srcOffset the index of the first element in {@code src}
     * @param dst       the destination
     * @param dstOffset the index of the first element in {@code dst}
     * @param length    the number of elements to copy
     */
    public static void copy(ByteBuffer src, int srcOffset, ByteBuffer dst, int dstOffset, int length) {
        duplicateRange(dst, dstOffset, length).put(duplicateRange(src, srcOffset, length));
    }

    /**
     * Copies the range {@code [srcOffset, srcOffset + length)} of the given buffer's contents to the range {@code [dstOffset, dstOffset + length)} of the given buffer. The buffer's {@link Buffer#position() positions} are not modified.
     *
     * @param src       the source
     * @param srcOffset the index of the first element in {@code src}
     * @param dst       the destination
     * @param dstOffset the index of the first element in {@code dst}
     * @param length    the number of elements to copy
     */
    public static void copy(ShortBuffer src, int srcOffset, ShortBuffer dst, int dstOffset, int length) {
        duplicateRange(dst, dstOffset, length).put(duplicateRange(src, srcOffset, length));
    }

    /**
     * Copies the range {@code [srcOffset, srcOffset + length)} of the given buffer's contents to the range {@code [dstOffset, dstOffset + length)} of the given buffer. The buffer's {@link Buffer#position() positions} are not modified.
     *
     * @param src       the source
     * @param srcOffset the index of the first element in {@code src}
     * @param dst       the destination
     * @param dstOffset the index of the first element in {@code dst}
     * @param length    the number of elements to copy
     */
    public static void copy(CharBuffer src, int srcOffset, CharBuffer dst, int dstOffset, int length) {
        duplicateRange(dst, dstOffset, length).put(duplicateRange(src, srcOffset, length));
    }

    /**
     * Copies the range {@code [srcOffset, srcOffset + length)} of the given buffer's contents to the range {@code [dstOffset, dstOffset + length)} of the given buffer. The buffer's {@link Buffer#position() positions} are not modified.
     *
     * @param src       the source
     * @param srcOffset the index of the first element in {@code src}
     * @param dst       the destination
     * @param dstOffset the index of the first element in {@code dst}
     * @param length    the number of elements to copy
     */
    public static void copy(IntBuffer src, int srcOffset, IntBuffer dst, int dstOffset, int length) {
        duplicateRange(dst, dstOffset, length).put(duplicateRange(src, srcOffset, length));
    }

    /**
     * Copies the range {@code [srcOffset, srcOffset + length)} of the given buffer's contents to the range {@code [dstOffset, dstOffset + length)} of the given buffer. The buffer's {@link Buffer#position() positions} are not modified.
     *
     * @param src       the source
     * @param srcOffset the index of the first element in {@code src}
     * @param dst       the destination
     * @param dstOffset the index of the first element in {@code dst}
     * @param length    the number of elements to copy
     */
    public static void copy(LongBuffer src, int srcOffset, LongBuffer dst, int dstOffset, int length) {
        duplicateRange(dst, dstOffset, length).put(duplicateRange(src, srcOffset, length));
    }

    /**
     * Copies the range {@code [srcOffset, srcOffset + length)} of the given buffer's contents to the range {@code [dstOffset, dstOffset + length)} of the given buffer. The buffer's {@link Buffer#position() positions} are not modified.
     *
     * @param src       the source
     * @param srcOffset the index of the first element in {@code src}
     * @param dst       the destination
     * @param dstOffset the index of the first element in {@code dst}
     * @param length    the number of elements to copy
     */
    public static void copy(FloatBuffer src, int srcOffset, FloatBuffer dst, int dstOffset, int length) {
        duplicateRange(dst, dstOffset, length).put(duplicateRange(src, srcOffset, length));
    }

    /**
     * Copies the range {@code [srcOffset, srcOffset + length)} of the given buffer's contents to the range {@code [dstOffset, dstOffset + length)} of the given buffer. The buffer's {@link Buffer#position() positions} are not modified.
     *
     * @param src       the source
     * @param srcOffset the index of the first element in {@code src}
     * @param dst       the destination
     * @param dstOffset the index of the first element in {@code dst}
     * @param length    the number of elements to copy
     */
    public static void copy(DoubleBuffer src, int srcOffset, DoubleBuffer dst, int dstOffset, int length) {
        duplicateRange(dst, dstOffset, length).put(duplicateRange(src, srcOffset, length));
    }

    /**
     * Copies the range {@code [srcOffset, srcOffset + length)} of the given buffer's contents to the range {@code [dstOffset, dstOffset + length)} of the given array. The buffer's {@link Buffer#position() position} is not modified.
     *
     * @param src       the source
     * @param srcOffset the index of the first element in {@code src}
     * @param dst       the destination
     * @param dstOffset the index of the first element in {@code dst}
     * @param length    the number of elements to copy
     */
    public static void copy(ByteBuffer src, int srcOffset, byte[] dst, int dstOffset, int length) {
        duplicateRange(src, srcOffset, length).get(dst, dstOffset, length);
    }

    /**
     * Copies the range {@code [srcOffset, srcOffset + length)} of the given buffer's contents to the range {@code [dstOffset, dstOffset + length)} of the given array. The buffer's {@link Buffer#position() position} is not modified.
     *
     * @param src       the source
     * @param srcOffset the index of the first element in {@code src}
     * @param dst       the destination
     * @param dstOffset the index of the first element in {@code dst}
     * @param length    the number of elements to copy
     */
    public static void copy(ShortBuffer src, int srcOffset, short[] dst, int dstOffset, int length) {
        duplicateRange(src, srcOffset, length).get(dst, dstOffset, length);
    }

    /**
     * Copies the range {@code [srcOffset, srcOffset + length)} of the given buffer's contents to the range {@code [dstOffset, dstOffset + length)} of the given array. The buffer's {@link Buffer#position() position} is not modified.
     *
     * @param src       the source
     * @param srcOffset the index of the first element in {@code src}
     * @param dst       the destination
     * @param dstOffset the index of the first element in {@code dst}
     * @param length    the number of elements to copy
     */
    public static void copy(CharBuffer src, int srcOffset, char[] dst, int dstOffset, int length) {
        duplicateRange(src, srcOffset, length).get(dst, dstOffset, length);
    }

    /**
     * Copies the range {@code [srcOffset, srcOffset + length)} of the given buffer's contents to the range {@code [dstOffset, dstOffset + length)} of the given array. The buffer's {@link Buffer#position() position} is not modified.
     *
     * @param src       the source
     * @param srcOffset the index of the first element in {@code src}
     * @param dst       the destination
     * @param dstOffset the index of the first element in {@code dst}
     * @param length    the number of elements to copy
     */
    public static void copy(IntBuffer src, int srcOffset, int[] dst, int dstOffset, int length) {
        duplicateRange(src, srcOffset, length).get(dst, dstOffset, length);
    }

    /**
     * Copies the range {@code [srcOffset, srcOffset + length)} of the given buffer's contents to the range {@code [dstOffset, dstOffset + length)} of the given array. The buffer's {@link Buffer#position() position} is not modified.
     *
     * @param src       the source
     * @param srcOffset the index of the first element in {@code src}
     * @param dst       the destination
     * @param dstOffset the index of the first element in {@code dst}
     * @param length    the number of elements to copy
     */
    public static void copy(LongBuffer src, int srcOffset, long[] dst, int dstOffset, int length) {
        duplicateRange(src, srcOffset, length).get(dst, dstOffset, length);
    }

    /**
     * Copies the range {@code [srcOffset, srcOffset + length)} of the given buffer's contents to the range {@code [dstOffset, dstOffset + length)} of the given array. The buffer's {@link Buffer#position() position} is not modified.
     *
     * @param src       the source
     * @param srcOffset the index of the first element in {@code src}
     * @param dst       the destination
     * @param dstOffset the index of the first element in {@code dst}
     * @param length    the number of elements to copy
     */
    public static void copy(FloatBuffer src, int srcOffset, float[] dst, int dstOffset, int length) {
        duplicateRange(src, srcOffset, length).get(dst, dstOffset, length);
    }

    /**
     * Copies the range {@code [srcOffset, srcOffset + length)} of the given buffer's contents to the range {@code [dstOffset, dstOffset + length)} of the given array. The buffer's {@link Buffer#position() position} is not modified.
     *
     * @param src       the source
     * @param srcOffset the index of the first element in {@code src}
     * @param dst       the destination
     * @param dstOffset the index of the first element in {@code dst}
     * @param length    the number of elements to copy
     */
    public static void copy(DoubleBuffer src, int srcOffset, double[] dst, int dstOffset, int length) {
        duplicateRange(src, srcOffset, length).get(dst, dstOffset, length);
    }

    /**
     * Copies the range {@code [srcOffset, srcOffset + length)} of the given array's contents to the range {@code [dstOffset, dstOffset + length)} of the given buffer. The buffer's {@link Buffer#position() position} is not modified.
     *
     * @param src       the source
     * @param srcOffset the index of the first element in {@code src}
     * @param dst       the destination
     * @param dstOffset the index of the first element in {@code dst}
     * @param length    the number of elements to copy
     */
    public static void copy(byte[] src, int srcOffset, ByteBuffer dst, int dstOffset, int length) {
        duplicateRange(dst, dstOffset, length).put(src, srcOffset, length);
    }

    /**
     * Copies the range {@code [srcOffset, srcOffset + length)} of the given array's contents to the range {@code [dstOffset, dstOffset + length)} of the given buffer. The buffer's {@link Buffer#position() position} is not modified.
     *
     * @param src       the source
     * @param srcOffset the index of the first element in {@code src}
     * @param dst       the destination
     * @param dstOffset the index of the first element in {@code dst}
     * @param length    the number of elements to copy
     */
    public static void copy(short[] src, int srcOffset, ShortBuffer dst, int dstOffset, int length) {
        duplicateRange(dst, dstOffset, length).put(src, srcOffset, length);
    }

    /**
     * Copies the range {@code [srcOffset, srcOffset + length)} of the given array's contents to the range {@code [dstOffset, dstOffset + length)} of the given buffer. The buffer's {@link Buffer#position() position} is not modified.
     *
     * @param src       the source
     * @param srcOffset the index of the first element in {@code src}
     * @param dst       the destination
     * @param dstOffset the index of the first element in {@code dst}
     * @param length    the number of elements to copy
     */
    public static void copy(char[] src, int srcOffset, CharBuffer dst, int dstOffset, int length) {
        duplicateRange(dst, dstOffset, length).put(src, srcOffset, length);
    }

    /**
     * Copies the range {@code [srcOffset, srcOffset + length)} of the given array's contents to the range {@code [dstOffset, dstOffset + length)} of the given buffer. The buffer's {@link Buffer#position() position} is not modified.
     *
     * @param src       the source
     * @param srcOffset the index of the first element in {@code src}
     * @param dst       the destination
     * @param dstOffset the index of the first element in {@code dst}
     * @param length    the number of elements to copy
     */
    public static void copy(int[] src, int srcOffset, IntBuffer dst, int dstOffset, int length) {
        duplicateRange(dst, dstOffset, length).put(src, srcOffset, length);
    }

    /**
     * Copies the range {@code [srcOffset, srcOffset + length)} of the given array's contents to the range {@code [dstOffset, dstOffset + length)} of the given buffer. The buffer's {@link Buffer#position() position} is not modified.
     *
     * @param src       the source
     * @param srcOffset the index of the first element in {@code src}
     * @param dst       the destination
     * @param dstOffset the index of the first element in {@code dst}
     * @param length    the number of elements to copy
     */
    public static void copy(long[] src, int srcOffset, LongBuffer dst, int dstOffset, int length) {
        duplicateRange(dst, dstOffset, length).put(src, srcOffset, length);
    }

    /**
     * Copies the range {@code [srcOffset, srcOffset + length)} of the given array's contents to the range {@code [dstOffset, dstOffset + length)} of the given buffer. The buffer's {@link Buffer#position() position} is not modified.
     *
     * @param src       the source
     * @param srcOffset the index of the first element in {@code src}
     * @param dst       the destination
     * @param dstOffset the index of the first element in {@code dst}
     * @param length    the number of elements to copy
     */
    public static void copy(float[] src, int srcOffset, FloatBuffer dst, int dstOffset, int length) {
        duplicateRange(dst, dstOffset, length).put(src, srcOffset, length);
    }

    /**
     * Copies the range {@code [srcOffset, srcOffset + length)} of the given array's contents to the range {@code [dstOffset, dstOffset + length)} of the given buffer. The buffer's {@link Buffer#position() position} is not modified.
     *
     * @param src       the source
     * @param srcOffset the index of the first element in {@code src}
     * @param dst       the destination
     * @param dstOffset the index of the first element in {@code dst}
     * @param length    the number of elements to copy
     */
    public static void copy(double[] src, int srcOffset, DoubleBuffer dst, int dstOffset, int length) {
        duplicateRange(dst, dstOffset, length).put(src, srcOffset, length);
    }
}
