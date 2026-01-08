/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2026 DaPorkchop_
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

package net.daporkchop.lib.compression.zstd.util;

import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.daporkchop.lib.common.annotation.ValueBased;
import net.daporkchop.lib.common.annotation.param.NotNegative;
import net.daporkchop.lib.common.annotation.param.Positive;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.OptionalLong;
import java.util.zip.DataFormatException;

/**
 * Methods for inspecting ZSTD frames implemented in pure Java.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JavaZstdFrameInspector {
    //
    // BUFFER ACCESS HELPERS
    //

    private static short getShortLE(ByteBuffer buf, int index) {
        return buf.order() == ByteOrder.LITTLE_ENDIAN ? buf.getShort(index) : Short.reverseBytes(buf.getShort(index));
    }

    private static int getUnsignedMediumLE(ByteBuffer buf, int index) {
        return buf.get(index) & 0xFF
                | (buf.get(index + 1) & 0xFF) << 8
                | (buf.get(index + 2) & 0xFF) << 16;
    }

    private static int getIntLE(ByteBuffer buf, int index) {
        return buf.order() == ByteOrder.LITTLE_ENDIAN ? buf.getInt(index) : Integer.reverseBytes(buf.getInt(index));
    }

    private static long getLongLE(ByteBuffer buf, int index) {
        return buf.order() == ByteOrder.LITTLE_ENDIAN ? buf.getLong(index) : Long.reverseBytes(buf.getLong(index));
    }

    @SuppressWarnings("deprecation")
    private static short getShortLE(ByteBuf buf, int index) {
        return buf.order() == ByteOrder.BIG_ENDIAN ? buf.getShortLE(index) : buf.getShort(index);
    }

    @SuppressWarnings("deprecation")
    private static int getUnsignedMediumLE(ByteBuf buf, int index) {
        return buf.order() == ByteOrder.BIG_ENDIAN ? buf.getMediumLE(index) : buf.getMedium(index);
    }

    @SuppressWarnings("deprecation")
    private static int getIntLE(ByteBuf buf, int index) {
        return buf.order() == ByteOrder.BIG_ENDIAN ? buf.getIntLE(index) : buf.getInt(index);
    }

    @SuppressWarnings("deprecation")
    private static long getLongLE(ByteBuf buf, int index) {
        return buf.order() == ByteOrder.BIG_ENDIAN ? buf.getLongLE(index) : buf.getLong(index);
    }

    //
    // ACTUAL FRAME DECODING CODE
    //

    private static DataFormatException throwNotEnoughInput() throws DataFormatException {
        throw new DataFormatException("not enough input bytes");
    }

    private static void verifyReadable(int index, int limit, int count) throws DataFormatException {
        if (limit - index < count) {
            throw throwNotEnoughInput();
        }
    }

    private static int verifyMagic(int magic) throws DataFormatException {
        switch (magic) {
            case ZstdConstants.MAGIC_NUMBER_V8:
                return Integer.BYTES;
            case ZstdConstants.MAGIC_NUMBER_V7:
                throw new DataFormatException("Data encoded in unsupported ZSTD v0.7 format");
            default:
                throw new DataFormatException("Invalid magic prefix: " + Integer.toHexString(magic));
        }
    }

    private static int verifyMagic(ByteBuffer buf, final int inputIndex, final int inputLimit) throws DataFormatException {
        verifyReadable(inputIndex, inputLimit, Integer.BYTES);
        return verifyMagic(getIntLE(buf, inputIndex));
    }

    private static int verifyMagic(ByteBuf buf, final int inputIndex, final int inputLimit) throws DataFormatException {
        verifyReadable(inputIndex, inputLimit, Integer.BYTES);
        return verifyMagic(getIntLE(buf, inputIndex));
    }

    private static @Positive int determineFrameHeaderSize(byte frameHeaderDescriptor) throws DataFormatException {
        int contentSizeFlag = Byte.toUnsignedInt(frameHeaderDescriptor) >>> 6;
        boolean singleSegmentFlag = (frameHeaderDescriptor & (1 << 5)) != 0;
        int dictionaryIdFlag = frameHeaderDescriptor & 0b11;
        if ((frameHeaderDescriptor & (1 << 3)) != 0) {
            throw new DataFormatException("Reserved_bit must be zero");
        }

        return Byte.BYTES +
                (singleSegmentFlag ? 0 : Byte.BYTES) +
                (dictionaryIdFlag == 0 ? 0 : (1 << (dictionaryIdFlag - 1))) +
                (contentSizeFlag == 0 ? (singleSegmentFlag ? Byte.BYTES : 0) : (1 << contentSizeFlag));
    }

    private static ZstdFrameHeader decodeFrameHeader(ByteBuffer buf, final int inputIndex, final int inputLimit) throws DataFormatException {
        int index = inputIndex;

        verifyReadable(index, inputLimit, ZstdConstants.MIN_FRAME_HEADER_SIZE);

        byte frameHeaderDescriptor = buf.get(index++);
        int headerSize = determineFrameHeaderSize(frameHeaderDescriptor);

        int contentSizeFlag = Byte.toUnsignedInt(frameHeaderDescriptor) >>> 6;
        boolean singleSegmentFlag = (frameHeaderDescriptor & (1 << 5)) != 0;
        boolean contentChecksumFlag = (frameHeaderDescriptor & (1 << 2)) != 0;
        int dictionaryIdFlag = frameHeaderDescriptor & 0b11;

        verifyReadable(index, inputLimit, headerSize);

        long windowSize = 0;
        if (!singleSegmentFlag) {
            int windowDescriptor = Byte.toUnsignedInt(buf.get(index++));

            int exponent = windowDescriptor >>> 3;
            int mantissa = windowDescriptor & 0b111;

            int windowLog = 10 + exponent;
            long windowBase = 1L << windowLog;
            long windowAdd = (windowBase / 8L) * mantissa;
            windowSize = windowBase + windowAdd;
        }

        int dictionaryId = 0;
        switch (dictionaryIdFlag) {
            case 1:
                dictionaryId = Byte.toUnsignedInt(buf.get(index));
                index += Byte.BYTES;
                break;
            case 2:
                dictionaryId = Short.toUnsignedInt(getShortLE(buf, index));
                index += Character.BYTES;
                break;
            case 3:
                dictionaryId = getIntLE(buf, index);
                index += Integer.BYTES;
                break;
        }

        long contentSize = -1;
        switch (contentSizeFlag) {
            case 0:
                if (singleSegmentFlag) {
                    contentSize = Byte.toUnsignedInt(buf.get(index));
                    index += Byte.BYTES;
                }
                break;
            case 1:
                contentSize = Short.toUnsignedInt(getShortLE(buf, index)) + 256;
                index += Short.BYTES;
                break;
            case 2:
                contentSize = Integer.toUnsignedLong(getIntLE(buf, index));
                index += Integer.BYTES;
                break;
            case 3:
                contentSize = getLongLE(buf, index);
                index += Long.BYTES;
                assert contentSize != -1 : "frame content size is 2^64-1";
                break;
        }

        if (singleSegmentFlag) {
            windowSize = contentSize;
        }

        assert index - inputIndex == headerSize : "expected headerSize=" + headerSize + ", but only consumed " + (index - inputIndex);

        return new ZstdFrameHeader(
                headerSize,
                windowSize, (int) Math.min(windowSize, ZstdConstants.MAX_BLOCK_SIZE),
                dictionaryId, contentSize, singleSegmentFlag, contentChecksumFlag);
    }

    private static ZstdFrameHeader decodeFrameHeader(ByteBuf buf, final int inputIndex, final int inputLimit) throws DataFormatException {
        int headerLength = Math.min(inputLimit - inputIndex, ZstdConstants.MAX_FRAME_HEADER_SIZE);
        return decodeFrameHeader(buf.nioBuffer(inputIndex, headerLength), 0, headerLength);
    }

    private static ZstdBlockHeader decodeBlockHeaderToObject(int blockHeader) {
        assert (blockHeader & ~0x00FF_FFFF) == 0;

        int blockSize = blockHeader >>> 3;
        boolean lastBlock = (blockHeader & 1) != 0;
        int blockType = (blockHeader >> 1) & 0b11;

        return new ZstdBlockHeader(lastBlock, blockType, blockSize);
    }

    private static ZstdBlockHeader getBlockHeader(ByteBuffer buf, final int inputIndex, final int inputLimit) throws DataFormatException {
        verifyReadable(inputIndex, inputLimit, 3);
        return decodeBlockHeaderToObject(getUnsignedMediumLE(buf, inputIndex));
    }

    private static ZstdBlockHeader getBlockHeader(ByteBuf buf, final int inputIndex, final int inputLimit) throws DataFormatException {
        verifyReadable(inputIndex, inputLimit, 3);
        return decodeBlockHeaderToObject(getUnsignedMediumLE(buf, inputIndex));
    }

    private static ZstdFrameSizeInfo getFrameSizeInfo(ByteBuffer buf, final int inputIndex, final int inputLimit) throws DataFormatException {
        int index = inputIndex;

        index += verifyMagic(buf, index, inputLimit);

        ZstdFrameHeader frameHeader = decodeFrameHeader(buf, index, inputLimit);
        index += frameHeader.headerSize();

        long numBlocks = 0;
        long decompressedSizeLowerBound = 0;
        long decompressedSizeUpperBound = 0;

        while (true) {
            val blockHeader = getBlockHeader(buf, index, inputLimit);

            int compressedSize;
            switch (blockHeader.blockType) {
                case ZstdConstants.BLOCK_TYPE_RAW:
                    compressedSize = ZstdConstants.BLOCK_HEADER_SIZE + blockHeader.blockSize;
                    decompressedSizeLowerBound += blockHeader.blockSize;
                    decompressedSizeUpperBound += blockHeader.blockSize;
                    break;
                case ZstdConstants.BLOCK_TYPE_RLE:
                    compressedSize = ZstdConstants.BLOCK_HEADER_SIZE + 1;
                    decompressedSizeLowerBound += blockHeader.blockSize;
                    decompressedSizeUpperBound += blockHeader.blockSize;
                    break;
                case ZstdConstants.BLOCK_TYPE_COMPRESSED:
                    compressedSize = ZstdConstants.BLOCK_HEADER_SIZE + blockHeader.blockSize;
                    decompressedSizeUpperBound += frameHeader.blockSizeMax();
                    break;
                default:
                    throw new DataFormatException("invalid block type");
            }

            verifyReadable(index, inputLimit, compressedSize);
            numBlocks++;
            index += compressedSize;

            if (blockHeader.lastBlock) {
                break;
            }
        }

        if (frameHeader.contentChecksumFlag()) {
            verifyReadable(index, inputLimit, ZstdConstants.FRAME_CHECKSUM_SIZE);
            index += ZstdConstants.FRAME_CHECKSUM_SIZE;
        }

        val contentSize = frameHeader.contentSize();
        if (contentSize.isPresent()) {
            decompressedSizeLowerBound = decompressedSizeUpperBound = contentSize.getAsLong();
        }

        return new ZstdFrameSizeInfo(numBlocks, index - inputIndex, decompressedSizeLowerBound, decompressedSizeUpperBound);
    }

    private static ZstdFrameSizeInfo getFrameSizeInfo(ByteBuf buf, final int inputIndex, final int inputLimit) throws DataFormatException {
        int index = inputIndex;

        index += verifyMagic(buf, index, inputLimit);

        ZstdFrameHeader frameHeader = decodeFrameHeader(buf, index, inputLimit);
        index += frameHeader.headerSize();

        long numBlocks = 0;
        long decompressedSizeLowerBound = 0;
        long decompressedSizeUpperBound = 0;

        while (true) {
            val blockHeader = getBlockHeader(buf, index, inputLimit);

            int compressedSize;
            switch (blockHeader.blockType) {
                case ZstdConstants.BLOCK_TYPE_RAW:
                    compressedSize = ZstdConstants.BLOCK_HEADER_SIZE + blockHeader.blockSize;
                    decompressedSizeLowerBound += blockHeader.blockSize;
                    decompressedSizeUpperBound += blockHeader.blockSize;
                    break;
                case ZstdConstants.BLOCK_TYPE_RLE:
                    compressedSize = ZstdConstants.BLOCK_HEADER_SIZE + 1;
                    decompressedSizeLowerBound += blockHeader.blockSize;
                    decompressedSizeUpperBound += blockHeader.blockSize;
                    break;
                case ZstdConstants.BLOCK_TYPE_COMPRESSED:
                    compressedSize = ZstdConstants.BLOCK_HEADER_SIZE + blockHeader.blockSize;
                    decompressedSizeUpperBound += frameHeader.blockSizeMax();
                    break;
                default:
                    throw new DataFormatException("invalid block type");
            }

            verifyReadable(index, inputLimit, compressedSize);
            numBlocks++;
            index += compressedSize;

            if (blockHeader.lastBlock) {
                break;
            }
        }

        if (frameHeader.contentChecksumFlag()) {
            verifyReadable(index, inputLimit, ZstdConstants.FRAME_CHECKSUM_SIZE);
            index += ZstdConstants.FRAME_CHECKSUM_SIZE;
        }

        val contentSize = frameHeader.contentSize();
        if (contentSize.isPresent()) {
            decompressedSizeLowerBound = decompressedSizeUpperBound = contentSize.getAsLong();
        }

        return new ZstdFrameSizeInfo(numBlocks, index - inputIndex, decompressedSizeLowerBound, decompressedSizeUpperBound);
    }

    //
    // PUBLIC API
    //

    /**
     * Verifies that the given buffer contains a valid ZSTD frame magic header at the given index.
     * <p>
     * The buffer's indices will not be modified.
     *
     * @param buf        the buffer containing the input data
     * @param inputIndex the index in the buffer to read from
     * @return the size of the frame header magic, always {@link ZstdConstants#MAGIC_SIZE}
     * @throws DataFormatException if the input data does not start with a valid ZSTD frame magic, or if there are fewer than {@link ZstdConstants#MAGIC_SIZE} bytes available
     */
    public static @Positive int verifyMagic(@NonNull ByteBuffer buf, final int inputIndex) throws DataFormatException {
        return verifyMagic(buf, inputIndex, buf.limit());
    }

    /**
     * Verifies that the given buffer contains a valid ZSTD frame header magic at the given index.
     * <p>
     * The buffer's indices will not be modified.
     *
     * @param buf        the buffer containing the input data
     * @param inputIndex the index in the buffer to read from
     * @return the size of the frame header magic, always {@link ZstdConstants#MAGIC_SIZE}
     * @throws DataFormatException if the input data does not start with a valid ZSTD frame magic, or if there are fewer than {@link ZstdConstants#MAGIC_SIZE} bytes available
     */
    public static @Positive int verifyMagic(@NonNull ByteBuf buf, final int inputIndex) throws DataFormatException {
        return verifyMagic(buf, inputIndex, buf.writerIndex());
    }

    /**
     * Calculate the total size of a ZSTD frame header based on the frame header descriptor. Requires at least {@link ZstdConstants#FRAME_HEADER_DESCRIPTOR_SIZE} available input bytes.
     * <p>
     * This is intended to be used in a streaming scenario to determine if there is sufficient input data for a subsequent call to {@link #getRawFrameHeader}. Specifically, as long as
     * the number of available input bytes is greater than or equal to the value returned by this method, a call to {@link #getRawFrameHeader} is guaranteed to complete without failing
     * because of insufficient input data.
     * <p>
     * The buffer's indices will not be modified.
     *
     * @param buf        the buffer containing the input data
     * @param inputIndex the index in the buffer to read from
     * @return the total size of the frame header (including the frame header descriptor)
     * @throws DataFormatException if the input data does not start with a valid ZSTD frame header descriptor, or if there are fewer than {@link ZstdConstants#FRAME_HEADER_DESCRIPTOR_SIZE} bytes available
     */
    public static @Positive int determineFrameHeaderSize(@NonNull ByteBuffer buf, final int inputIndex) throws DataFormatException {
        verifyReadable(inputIndex, buf.limit(), Byte.BYTES);
        return determineFrameHeaderSize(buf.get(inputIndex));
    }

    /**
     * Calculate the total size of a ZSTD frame header based on the frame header descriptor. Requires at least {@link ZstdConstants#FRAME_HEADER_DESCRIPTOR_SIZE} available input bytes.
     * <p>
     * This is intended to be used in a streaming scenario to determine if there is sufficient input data for a subsequent call to {@link #getRawFrameHeader}. Specifically, as long as
     * the number of available input bytes is greater than or equal to the value returned by this method, a call to {@link #getRawFrameHeader} is guaranteed to complete without failing
     * because of insufficient input data.
     * <p>
     * The buffer's indices will not be modified.
     *
     * @param buf        the buffer containing the input data
     * @param inputIndex the index in the buffer to read from
     * @return the total size of the frame header (including the frame header descriptor)
     * @throws DataFormatException if the input data does not start with a valid ZSTD frame header descriptor, or if there are fewer than {@link ZstdConstants#FRAME_HEADER_DESCRIPTOR_SIZE} bytes available
     */
    public static @Positive int determineFrameHeaderSize(@NonNull ByteBuf buf, final int inputIndex) throws DataFormatException {
        verifyReadable(inputIndex, buf.writerIndex(), Byte.BYTES);
        return determineFrameHeaderSize(buf.getByte(inputIndex));
    }

    /**
     * Reads a ZSTD frame header (without the magic prefix) from the given buffer starting at the given index.
     * <p>
     * The the buffer's indices will not be modified.
     *
     * @param buf        the buffer containing the input data
     * @param inputIndex the index in the buffer to read from
     * @return the ZSTD frame header
     * @throws DataFormatException if the input data does not start with a valid ZSTD frame header, or if there isn't enough data available
     * @see #determineFrameHeaderSize
     */
    public static ZstdFrameHeader getRawFrameHeader(@NonNull ByteBuffer buf, final int inputIndex) throws DataFormatException {
        return decodeFrameHeader(buf, inputIndex, buf.limit());
    }

    /**
     * Reads a ZSTD frame header (without the magic prefix) from the given buffer starting at the given index.
     * <p>
     * The buffer's indices will not be modified.
     *
     * @param buf        the buffer containing the input data
     * @param inputIndex the index in the buffer to read from
     * @return the ZSTD frame header
     * @throws DataFormatException if the input data does not start with a valid ZSTD frame header, or if there isn't enough data available
     * @see #determineFrameHeaderSize
     */
    public static ZstdFrameHeader getRawFrameHeader(@NonNull ByteBuf buf, final int inputIndex) throws DataFormatException {
        return decodeFrameHeader(buf, inputIndex, buf.writerIndex());
    }

    /**
     * Reads a ZSTD frame header (including the magic prefix) from the given buffer starting at the buffer's position.
     * <p>
     * The buffer's indices will not be modified.
     * <p>
     * Note that the {@link ZstdFrameHeader#headerSize() header size} in the returned {@link ZstdFrameHeader} does not include the size of the magic prefix,
     * which is an additional {@link ZstdConstants#MAGIC_SIZE}.
     *
     * @param buf the buffer containing the input data
     * @return the ZSTD frame header
     * @throws DataFormatException if the input data does not start with a valid ZSTD magic prefix or frame header, or if there isn't enough data available
     */
    public static ZstdFrameHeader getFrameHeader(@NonNull ByteBuffer buf) throws DataFormatException {
        int index = buf.position();
        final int inputLimit = buf.limit();

        index += verifyMagic(buf, index, inputLimit);
        return decodeFrameHeader(buf, index, inputLimit);
    }

    /**
     * Reads a ZSTD frame header (including the magic prefix) from the given buffer starting at the buffer's reader index.
     * <p>
     * The buffer's indices will not be modified.
     * <p>
     * Note that the {@link ZstdFrameHeader#headerSize() header size} in the returned {@link ZstdFrameHeader} does not include the size of the magic prefix,
     * which is an additional {@link ZstdConstants#MAGIC_SIZE}.
     *
     * @param buf the buffer containing the input data
     * @return the ZSTD frame header
     * @throws DataFormatException if the input data does not start with a valid ZSTD magic prefix or frame header, or if there isn't enough data available
     */
    public static ZstdFrameHeader getFrameHeader(@NonNull ByteBuf buf) throws DataFormatException {
        int index = buf.readerIndex();
        final int inputLimit = buf.writerIndex();

        index += verifyMagic(buf, index, inputLimit);
        return decodeFrameHeader(buf, index, inputLimit);
    }

    /**
     * Inspects a single ZSTD frame starting at the given buffer's current position and computes an upper and lower bound on its total decompressed size.
     * <p>
     * This method <strong>may</strong> access and validate compressed data until the end of the frame is reached. Any unknown/garbage data past the end
     * of the frame will be ignored.
     * <p>
     * The buffer's indices will not be modified.
     *
     * @param buf the buffer containing the input data
     * @return an upper and lower bound on the ZSTD frame's decompressed size
     * @throws DataFormatException if the input data does not contain a valid ZSTD frame
     */
    public static ZstdFrameSizeBounds getFrameSizeBounds(@NonNull ByteBuffer buf) throws DataFormatException {
        int index = buf.position();
        final int inputLimit = buf.limit();

        OptionalLong contentSize = getFrameHeader(buf).contentSize();
        if (contentSize.isPresent()) {
            //the decompressed size is stored in the frame header, use that and exit immediately
            return new ZstdFrameSizeBounds(contentSize.getAsLong(), contentSize.getAsLong());
        }

        //slow path: inspect each block individually and add up their bounds
        val frameSizeInfo = getFrameSizeInfo(buf, index, inputLimit);
        return new ZstdFrameSizeBounds(frameSizeInfo.decompressedSizeLowerBound(), frameSizeInfo.decompressedSizeUpperBound());
    }

    /**
     * Inspects a single ZSTD frame starting at the given buffer's current position and computes an upper and lower bound on its total decompressed size.
     * <p>
     * This method <strong>may</strong> access and validate compressed data until the end of the frame is reached. Any unknown/garbage data past the end
     * of the frame will be ignored.
     * <p>
     * The buffer's indices will not be modified.
     *
     * @param buf the buffer containing the input data
     * @return an upper and lower bound on the ZSTD frame's decompressed size
     * @throws DataFormatException if the input data does not contain a valid ZSTD frame
     */
    public static ZstdFrameSizeBounds getFrameSizeBounds(@NonNull ByteBuf buf) throws DataFormatException {
        int index = buf.readerIndex();
        final int inputLimit = buf.writerIndex();

        OptionalLong contentSize = getFrameHeader(buf).contentSize();
        if (contentSize.isPresent()) {
            //the decompressed size is stored in the frame header, use that and exit immediately
            return new ZstdFrameSizeBounds(contentSize.getAsLong(), contentSize.getAsLong());
        }

        //slow path: inspect each block individually and add up their bounds
        val frameSizeInfo = getFrameSizeInfo(buf, index, inputLimit);
        return new ZstdFrameSizeBounds(frameSizeInfo.decompressedSizeLowerBound(), frameSizeInfo.decompressedSizeUpperBound());
    }

    /**
     * Inspects a single ZSTD frame starting at the given buffer's current position and computes information about its total compressed and uncompressed size.
     * <p>
     * This method <strong>will</strong> access and validate compressed data until the end of the frame is reached. Any unknown/garbage data past the end
     * of the frame will be ignored.
     * <p>
     * The buffer's indices will not be modified.
     *
     * @param buf the buffer containing the input data
     * @return information about the size of a single ZSTD frame
     * @throws DataFormatException if the input data does not contain a valid ZSTD frame
     */
    public static ZstdFrameSizeInfo getFrameSizeInfo(@NonNull ByteBuffer buf) throws DataFormatException {
        int index = buf.position();
        final int inputLimit = buf.limit();

        return getFrameSizeInfo(buf, index, inputLimit);
    }

    /**
     * Inspects a single ZSTD frame starting at the given buffer's current position and computes information about its total compressed and uncompressed size.
     * <p>
     * This method <strong>will</strong> access and validate compressed data until the end of the frame is reached. Any unknown/garbage data past the end
     * of the frame will be ignored.
     * <p>
     * The buffer's indices will not be modified.
     *
     * @param buf the buffer containing the input data
     * @return information about the size of a single ZSTD frame
     * @throws DataFormatException if the input data does not contain a valid ZSTD frame
     */
    public static ZstdFrameSizeInfo getFrameSizeInfo(@NonNull ByteBuf buf) throws DataFormatException {
        int index = buf.readerIndex();
        final int inputLimit = buf.writerIndex();

        return getFrameSizeInfo(buf, index, inputLimit);
    }

    /**
     * Inspects a sequence of concatenated ZSTD frames and computes an upper and lower bound on their total decompressed size.
     * <p>
     * This method will start at the buffer's current position and keep going until the end of the input data is reached. If the input data is truncated
     * or if a frame is followed by garbage data (i.e. any data which isn't a valid ZSTD frame), this method will fail with {@link DataFormatException}.
     * <p>
     * The buffer's indices will not be modified.
     *
     * @param buf the buffer containing the input data
     * @return information about the total decompressed size of the provided sequence of concatenated ZSTD frames
     * @throws DataFormatException if the input data does not contain a valid sequence of concatenated ZSTD frames
     */
    public static ZstdSequenceSizeInfo getSequenceSizeInfo(@NonNull ByteBuffer buf) throws DataFormatException {
        int index = buf.position();
        final int inputLimit = buf.limit();

        long numFrames = 0;
        long decompressedSizeLowerBound = 0;
        long decompressedSizeUpperBound = 0;

        while (index != inputLimit) {
            val frameSizeInfo = getFrameSizeInfo(buf, index, inputLimit);
            numFrames++;
            index += frameSizeInfo.compressedSize();
            decompressedSizeLowerBound += frameSizeInfo.decompressedSizeLowerBound();
            decompressedSizeUpperBound += frameSizeInfo.decompressedSizeUpperBound();
        }

        return new ZstdSequenceSizeInfo(numFrames, decompressedSizeLowerBound, decompressedSizeUpperBound);
    }

    /**
     * Inspects a sequence of concatenated ZSTD frames and computes an upper and lower bound on their total decompressed size.
     * <p>
     * This method will start at the buffer's current position and keep going until the end of the input data is reached. If the input data is truncated
     * or if a frame is followed by garbage data (i.e. any data which isn't a valid ZSTD frame), this method will fail with {@link DataFormatException}.
     * <p>
     * The buffer's indices will not be modified.
     *
     * @param buf the buffer containing the input data
     * @return information about the total decompressed size of the provided sequence of concatenated ZSTD frames
     * @throws DataFormatException if the input data does not contain a valid sequence of concatenated ZSTD frames
     */
    public static ZstdSequenceSizeInfo getSequenceSizeInfo(@NonNull ByteBuf buf) throws DataFormatException {
        int index = buf.readerIndex();
        final int inputLimit = buf.writerIndex();

        long numFrames = 0;
        long decompressedSizeLowerBound = 0;
        long decompressedSizeUpperBound = 0;

        while (index != inputLimit) {
            val frameSizeInfo = getFrameSizeInfo(buf, index, inputLimit);
            numFrames++;
            index += frameSizeInfo.compressedSize();
            decompressedSizeLowerBound += frameSizeInfo.decompressedSizeLowerBound();
            decompressedSizeUpperBound += frameSizeInfo.decompressedSizeUpperBound();
        }

        return new ZstdSequenceSizeInfo(numFrames, decompressedSizeLowerBound, decompressedSizeUpperBound);
    }

    /**
     * @author DaPorkchop_
     */
    @RequiredArgsConstructor
    @ValueBased
    private static final class ZstdBlockHeader {
        /**
         * {@code true} if this is the last block in the frame.
         */
        public final @NotNegative boolean lastBlock;

        /**
         * The type of block.
         * <p>
         * Always one of:
         * <ul>
         *     <li>{@link ZstdConstants#BLOCK_TYPE_RAW}</li>
         *     <li>{@link ZstdConstants#BLOCK_TYPE_RLE}</li>
         *     <li>{@link ZstdConstants#BLOCK_TYPE_COMPRESSED}</li>
         * </ul>
         */
        public final int blockType;

        /**
         * The size of the block.
         *
         * <ul>
         *     <li>For {@link ZstdConstants#BLOCK_TYPE_RAW}, this is both the compressed and decompressed size of the block.</li>
         *     <li>For {@link ZstdConstants#BLOCK_TYPE_RLE}, this is the decompressed size of the block (the compressed size is always {@code 1}).</li>
         *     <li>For {@link ZstdConstants#BLOCK_TYPE_COMPRESSED}, this is the decompressed size of the block.</li>
         * </ul>
         */
        public final @NotNegative int blockSize;
    }
}
