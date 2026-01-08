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

package net.daporkchop.lib.compression.deflate.jdk;

import lombok.NonNull;
import lombok.val;
import net.daporkchop.lib.common.util.PNioBuffers;
import net.daporkchop.lib.compression.deflate.DeflateStreamingDecompressor;
import net.daporkchop.lib.compression.generic.AbstractStreamingDecompressor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.GZIPInputStream;

/**
 * @author DaPorkchop_
 */
final class JdkGzipStreamingDecompressor extends AbstractStreamingDecompressor implements DeflateStreamingDecompressor, JdkDeflateContext {
    private static final byte STATE_RESET = 0;
    private static final byte STATE_AWAIT_MEMBER = 1;
    private static final byte STATE_READ_HEADER = 2;
    private static final byte STATE_DECOMPRESS = 3;
    private static final byte STATE_READ_TRAILER = 4;
    private static final byte STATE_DONE = 5;

    private final JdkDeflateStreamingDecompressor inflater;
    private final CRC32 crc = new CRC32();

    private byte state = STATE_RESET;

    JdkGzipStreamingDecompressor() {
        this.inflater = new JdkDeflateStreamingDecompressor(true);
        this.inflater.setSingleStream(true);
    }

    @Override
    public void close() {
        this.inflater.close();
    }

    @Override
    public void resetStream() {
        super.resetStream();

        this.inflater.resetStream();
        this.crc.reset();
        this.state = STATE_RESET;
    }

    @Override
    protected boolean isStreamOngoing() {
        return this.state == STATE_RESET;
    }

    @Override
    public boolean decompress(@NonNull ByteBuffer src, @NonNull ByteBuffer dst, boolean eof) throws DataFormatException, ReadOnlyBufferException {
        this.setLastReadWrittenBytes(0, 0);

        if (dst.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }

        if (this.state == STATE_RESET) {
            this.state = STATE_AWAIT_MEMBER;
        }

        this.validateEofParameter(eof);

        if (this.state == STATE_AWAIT_MEMBER) {
            if (!src.hasRemaining()) {
                if (eof) {
                    if (this.singleStream) {
                        //we've reached the end of the input stream and there's no input data remaining, but not a single GZIP member has been decompressed so we're going to abort
                        throw new DataFormatException("empty input stream is not allowed in single stream mode");
                    } else {
                        //we've reached the end of the input stream and there's no input data remaining, so we're finished here :)
                        this.state = STATE_DONE;
                    }
                } else {
                    //wait for more input
                    return false;
                }
            } else {
                //there is some input available, begin decompressing the next member
                this.state = STATE_READ_HEADER;
                this.readHeaderBegin();
            }
        }

        if (this.state == STATE_READ_HEADER) {
            //this automatically increments lastReadBytes
            if (this.readHeaderStep(src)) {
                //we've read the entire header, begin decompressing the contents
                this.state = STATE_DECOMPRESS;
                this.crc.reset();
            } else {
                //we need more input data!
                assert !src.hasRemaining() : src; //if we get to this point, readHeaderStep() should have consumed all of the available input
                if (eof) {
                    throw new DataFormatException("Unexpected end of GZIP input stream");
                } else {
                    //wait for more input
                    return false;
                }
            }
        }

        if (this.state == STATE_DECOMPRESS) {
            final boolean done;
            try {
                //TODO: figure out what to do with buffer positions in case of an exception being thrown later on
                done = this.inflater.decompress(src, dst, eof);
            } finally {
                //increment last read/written count
                this.addLastReadWrittenBytes(this.inflater.getLastReadBytes(), this.inflater.getLastWrittenBytes());
            }

            //update the checksum on the uncompressed output
            val inflaterLastWrittenBytes = Math.toIntExact(this.inflater.getLastWrittenBytes());
            this.crc.update(PNioBuffers.duplicateRange(dst, dst.position() - inflaterLastWrittenBytes, inflaterLastWrittenBytes));

            if (done) {
                //reached the end of the compressed stream, now we just need to read and verify the checksum
                this.state = STATE_READ_TRAILER;
                this.readTrailerBegin();
            } else {
                //wait for more input or output space
                return false;
            }
        }

        if (this.state == STATE_READ_TRAILER) {
            //this automatically increments lastReadBytes
            if (this.readTrailerStep(src)) {
                //we've read the entire trailer, and thus the end of this GZIP member
                if (this.singleStream) {
                    //stop after completing a single member
                    this.state = STATE_DONE;
                } else {
                    //wait for the next GZIP member or EOF
                    this.state = STATE_AWAIT_MEMBER;
                }
            } else {
                //we need more input data!
                assert !src.hasRemaining() : src; //if we get to this point, readTrailerStep() should have consumed all of the available input
                if (eof) {
                    throw new DataFormatException("Unexpected end of GZIP input stream");
                } else {
                    //wait for more input
                    return false;
                }
            }
        }

        if (this.state == STATE_DONE) {
            //decompression is done, we can reset the stream now :)
            this.resetStream();
            return true;
        }

        return false;
    }

    //
    // HEADER READING
    //

    // GZIP file header flags, copied from GZIPInputStream
    //private static final byte FTEXT = 1; // Extra text
    private static final byte FHCRC = 2; // Header CRC
    private static final byte FEXTRA = 4; // Extra field
    private static final byte FNAME = 8; // File name
    private static final byte FCOMMENT = 16; // File comment

    private static final byte HEADER_STATE_READ_MAGIC = 0;
    private static final byte HEADER_STATE_READ_COMPRESSION = 1;
    private static final byte HEADER_STATE_READ_FLAGS = 2;
    private static final byte HEADER_STATE_READ_SKIPPED = 3;
    private static final byte HEADER_STATE_READ_EXTRA_SIZE = 4;
    private static final byte HEADER_STATE_READ_EXTRA_BODY = 5;
    private static final byte HEADER_STATE_READ_FILE_NAME = 6;
    private static final byte HEADER_STATE_READ_FILE_COMMENT = 7;
    private static final byte HEADER_STATE_READ_CRC = 8;
    private static final byte HEADER_STATE_DONE = 9;

    private byte headerState;
    private byte headerFlags;
    private char headerSkipCountRemaining;
    private char headerComputedCrc16;
    private short headerReadShortFirstByte;

    private void readHeaderBegin() {
        this.headerState = HEADER_STATE_READ_MAGIC;
        this.headerReadShortFirstByte = -1;
        this.crc.reset();
    }

    //returns true once the entire header has been read, false if more input is needed.
    //lastReadBytes is incremented automatically.
    private boolean readHeaderStep(ByteBuffer src) throws DataFormatException {
        if (this.headerState == HEADER_STATE_READ_MAGIC) {
            val magic = this.readHeader_tryReadUShort(src);
            if (magic < 0) {
                return false;
            } else if (magic != GZIPInputStream.GZIP_MAGIC) {
                throw new DataFormatException("Not in GZIP format");
            } else {
                this.headerState = HEADER_STATE_READ_COMPRESSION;
            }
        }

        if (this.headerState == HEADER_STATE_READ_COMPRESSION) {
            val compression = this.readHeader_tryReadUByte(src);
            if (compression < 0) {
                return false;
            } else if (compression != 8) {
                throw new DataFormatException("Unsupported compression method");
            } else {
                this.headerState = HEADER_STATE_READ_FLAGS;
            }
        }

        if (this.headerState == HEADER_STATE_READ_FLAGS) {
            val flags = this.readHeader_tryReadUByte(src);
            if (flags < 0) {
                return false;
            } else {
                this.headerFlags = (byte) flags;
                this.headerState = HEADER_STATE_READ_SKIPPED;
                this.headerSkipCountRemaining = 6;
            }
        }

        if (this.headerState == HEADER_STATE_READ_SKIPPED) {
            if (!this.readHeader_skipBytes(src)) {
                return false;
            }
            this.headerState = HEADER_STATE_READ_EXTRA_SIZE;
        }

        if (this.headerState == HEADER_STATE_READ_EXTRA_SIZE) {
            final int size;
            if ((this.headerFlags & FEXTRA) != 0) {
                size = this.readHeader_tryReadUShort(src);
                if (size < 0) {
                    return false;
                } else {
                    //fallthrough
                }
            } else {
                size = 0;
            }
            this.headerState = HEADER_STATE_READ_EXTRA_BODY;
            this.headerSkipCountRemaining = (char) size;
        }

        if (this.headerState == HEADER_STATE_READ_EXTRA_BODY) {
            if (!this.readHeader_skipBytes(src)) {
                return false;
            }
            this.headerState = HEADER_STATE_READ_FILE_NAME;
        }

        if (this.headerState == HEADER_STATE_READ_FILE_NAME) {
            if ((this.headerFlags & FNAME) != 0 && !this.readHeader_skipName(src)) {
                return false;
            }
            this.headerState = HEADER_STATE_READ_FILE_COMMENT;
        }

        if (this.headerState == HEADER_STATE_READ_FILE_COMMENT) {
            if ((this.headerFlags & FCOMMENT) != 0 && !this.readHeader_skipName(src)) {
                return false;
            }
            this.headerState = HEADER_STATE_READ_CRC;

            //save the current checksum to the state, since it'll get updated by 'readHeader_tryReadUShort()' while reading the expected checksum field
            this.headerComputedCrc16 = (char) this.crc.getValue();
        }

        if (this.headerState == HEADER_STATE_READ_CRC) {
            if ((this.headerFlags & FHCRC) != 0) {
                val hcrc = this.readHeader_tryReadUShort(src);
                if (hcrc < 0) {
                    return false;
                } else if (hcrc != this.headerComputedCrc16) {
                    throw new DataFormatException("Corrupt GZIP header");
                }
            }
            this.headerState = HEADER_STATE_DONE;
            return true;
        }

        throw new AssertionError(this.headerState);
    }

    private boolean readHeader_skipBytes(ByteBuffer src) {
        while (this.headerSkipCountRemaining > 0) {
            val b = this.readHeader_tryReadUByte(src);
            if (b < 0) {
                //wait for more input
                return false;
            } else {
                this.headerSkipCountRemaining--;
            }
        }
        return true;
    }

    private boolean readHeader_skipName(ByteBuffer src) {
        while (true) {
            val b = this.readHeader_tryReadUByte(src);
            if (b < 0) {
                //wait for more input
                return false;
            } else if (b == 0) {
                //reached the end of the string
                return true;
            }
        }
    }

    private int readHeader_tryReadUShort(ByteBuffer src) {
        if (this.headerReadShortFirstByte < 0 && (this.headerReadShortFirstByte = this.readHeader_tryReadUByte(src)) < 0) {
            return -1;
        }

        val secondByte = this.readHeader_tryReadUByte(src);
        if (secondByte < 0) {
            return -1;
        }

        val firstByte = this.headerReadShortFirstByte;
        this.headerReadShortFirstByte = -1;
        return (secondByte << Byte.SIZE) | firstByte;
    }

    private short readHeader_tryReadUByte(ByteBuffer src) {
        if (src.hasRemaining()) {
            val b = src.get();
            this.crc.update(b);
            this.addLastReadWrittenBytes(1, 0);
            return (short) Byte.toUnsignedInt(b);
        } else {
            return -1;
        }
    }

    //
    // TRAILER READING
    //

    private static final byte TRAILER_BUF_SIZE = Integer.BYTES * 2;
    private final byte[] trailerBuf = new byte[TRAILER_BUF_SIZE];
    private byte trailerBufOffset;

    private void readTrailerBegin() {
        this.trailerBufOffset = 0;
    }

    //returns true once the entire trailer has been read, false if more input is needed.
    //lastReadBytes is incremented automatically.
    private boolean readTrailerStep(ByteBuffer src) throws DataFormatException {
        assert this.trailerBufOffset < TRAILER_BUF_SIZE : this.trailerBufOffset;

        val count = Math.min(TRAILER_BUF_SIZE - this.trailerBufOffset, src.remaining());
        src.get(this.trailerBuf, this.trailerBufOffset, count);
        this.trailerBufOffset += count;
        this.addLastReadWrittenBytes(count, 0);

        if (this.trailerBufOffset < TRAILER_BUF_SIZE) {
            //wait for more input
            return false;
        }

        val trailer = ByteBuffer.wrap(this.trailerBuf).order(ByteOrder.LITTLE_ENDIAN);
        if (trailer.getInt() != (int) this.crc.getValue() || trailer.getInt() != (int) this.inflater.inflater.getBytesWritten()) {
            throw new DataFormatException("Corrupt GZIP trailer");
        }

        return true;
    }
}
