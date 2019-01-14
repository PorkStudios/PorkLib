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

package net.daporkchop.lib.encoding.qr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.daporkchop.lib.encoding.qr.util.GF;
import net.daporkchop.lib.encoding.qr.util.QRBitOutput;
import net.daporkchop.lib.encoding.qr.util.QRLevel;
import net.daporkchop.lib.encoding.qr.util.QRMask;
import net.daporkchop.lib.encoding.qr.util.QRVersion;
import net.daporkchop.lib.encoding.qr.util.RSEncoder;
import net.daporkchop.lib.encoding.qr.util.SimpleBitOutput;
import net.daporkchop.lib.encoding.util.XYIndexedBitSet;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

import static net.daporkchop.lib.encoding.qr.QRConstants.*;

/**
 * Makes QR codes!
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class QRCodeBuilder {
    /**
     * The level of redundancy in the output QR code.
     * <p>
     * Officially called "EDC Level", and it "Determines count/distribution of codewords in D-space & E-space".
     * <p>
     * The higher this is set, the higher the level of error correction the code can handle, at the expense of larger size.
     */
    @NonNull
    protected QRLevel level = QRLevel.Medium;

    /**
     * An additional bitmask that is applied (XOR-ed) onto the data.
     *
     * If {@code null}, the best one will be chosen automagically.
     */
    protected QRMask mask = null;

    protected boolean isUtf8 = false;

    public QRCode encode(@NonNull String text)  {
        return this.setUtf8(true).encode(text.getBytes(Charset.forName("UTF-8")));
    }

    public QRCode encode(@NonNull byte[] data)   {
        //int version = getBestVersionForData(data.length, this.level);
        //int size = getSizeForVersion(version);
        //BitSet bits = new BitSet(size * size);
        //XYIndexedBitSet xyBits = new XYIndexedBitSet(size, bits);
        //QRBitOutput out = new QRBitOutput(version, bits);

        BitSet headers = new BitSet();
        int headersLength;
        {
            SimpleBitOutput headersOut = new SimpleBitOutput(headers);
            if (this.isUtf8) {
                headersOut.write(0b0111, 4);
                headersOut.write(26, 8);
            }
            headersOut.write(0b0100, 4);
            headersLength = headersOut.getI();
        }

        BitSet dataBits = new BitSet();
        {
            SimpleBitOutput dataOut = new SimpleBitOutput(dataBits);
            for (byte b : data) {
                dataOut.write(b & 0xFF, 8);
            }
        }

        QRVersion version;
        {
            QRVersion tempVersion = this.choose(headersLength + getLengthPrefixBits(1) + (data.length << 3), this.level);
            version = this.choose(headersLength + getLengthPrefixBits(tempVersion.getVersionNumber()) + (data.length << 3), this.level);
        }

        QRVersion.ECBlocks blocks = version.getBlocks(this.level);
        int dataBytes = version.getTotalCodewords() - blocks.getTotalECCodewords();

        BitSet headersAndData = new BitSet();
        int headersAndDataLength;
        {
            SimpleBitOutput output = new SimpleBitOutput(headersAndData);
            output.write(headers, headersLength);
            output.write(data.length, getLengthPrefixBits(version.getVersionNumber()));
            output.write(dataBits, data.length << 3);

            for (int i = 0; i < 4 && output.getI() < (dataBytes << 3); i++)    {
                output.write(false);
            }

            if ((output.getI() & 0x7) > 0)  {
                for (int i = output.getI() & 0x7; i < 8; i++)   {
                    output.write(false);
                }
            }

            int paddingCount = dataBytes - (output.getI() >>> 3);
            for (int i = 0; i < paddingCount; i++)  {
                output.write((i & 1) == 0 ? 0xEC : 0x11, 8);
            }

            headersAndDataLength = output.getI();
        }

        BitSet weDoneBois = this.interleaveWithECBytes(headersAndData, version.getTotalCodewords(), dataBytes, blocks.getNumBlocks());



        /*out.put(this.isUtf8 ? 0b0111 : 0b0100, 4); //hard-coded to byte mode
        out.put(data.length, getLengthPrefixBits(version));
        for (int i = 0; i < data.length; i++)   {
            out.put(data[i] & 0xFF, 8);
        }
        out.put(0, 4); //pad to be multiple of 8
        {
            //pad again until the full data length has been reached
            int required = getRequiredBits(version, this.level);
            int last = 0b11101100;
            while (out.writerPos() > required) {
                out.put(last, 8);
                if (last == 0b11101100) { //these constants are required by the spec
                    last = 0b00010001;
                } else {
                    last = 0b11101100;
                }
            }
        }*/
        {

        }
        return null;
    }

    protected QRMask chooseMaskPattern(@NonNull BitSet bits, @NonNull QRLevel level, @NonNull QRVersion version, @NonNull XYIndexedBitSet matrix)   {
        int minPenalty = Integer.MAX_VALUE;
        QRMask best = null;
        for (QRMask mask : QRMask.values()) {

        }
        return best;
    }

    protected void buildMatrix(@NonNull BitSet dataBits, @NonNull QRLevel ecLevel, @NonNull QRVersion version, int maskPattern, @NonNull XYIndexedBitSet matrix)    {
        matrix.clear();
        int size = matrix.getSize();
        matrix.setArea(0, 0, 6, 6);
        matrix.clearArea(1, 1, 4, 4);
        matrix.setArea(2, 2, 2, 2);

        matrix.setArea(size - 7, 0, 6, 6);
        matrix.clearArea(size - 6, 1, 4, 4);
        matrix.setArea(size - 5, 2, 2, 2);

        matrix.setArea(0, size - 7, 6, 6);
        matrix.clearArea(1, size - 6, 4, 4);
        matrix.setArea(2, size - 5, 2, 2);
    }

    protected BitSet interleaveWithECBytes(BitSet bits, int numTotalBytes, int numDataBytes, int numRSBlocks) {
        if (bits.size() != numDataBytes) {
            throw new IllegalArgumentException("Number of bits and data bytes does not match");
        }

        int dataBytesOffset = 0;
        int maxNumDataBytes = 0;
        int maxNumEcBytes = 0;

        Collection<BlockPair> blocks = new ArrayList<>(numRSBlocks);
        SimpleBitOutput bitsOut = new SimpleBitOutput(bits);

        for (int i = 0; i < numRSBlocks; ++i) {
            int[] numDataBytesInBlock = new int[1];
            int[] numEcBytesInBlock = new int[1];
            getNumDataBytesAndNumECBytesForBlockID(numTotalBytes, numDataBytes, numRSBlocks, i, numDataBytesInBlock, numEcBytesInBlock);

            int size = numDataBytesInBlock[0];
            byte[] dataBytes = new byte[size];
            bitsOut.toBytes(8 * dataBytesOffset, dataBytes, 0, size);
            byte[] ecBytes = generateECBytes(dataBytes, numEcBytesInBlock[0]);
            blocks.add(new BlockPair(dataBytes, ecBytes));

            maxNumDataBytes = Math.max(maxNumDataBytes, size);
            maxNumEcBytes = Math.max(maxNumEcBytes, ecBytes.length);
            dataBytesOffset += numDataBytesInBlock[0];
        }
        if (numDataBytes != dataBytesOffset) {
            throw new IllegalStateException("Data bytes does not match offset");
        }

        BitSet resultBits = new BitSet();
        SimpleBitOutput result = new SimpleBitOutput(resultBits);

        for (int i = 0; i < maxNumDataBytes; ++i) {
            for (BlockPair block : blocks) {
                byte[] dataBytes = block.getDataBytes();
                if (i < dataBytes.length) {
                    result.write(dataBytes[i] & 0xFF, 8);
                }
            }
        }
        for (int i = 0; i < maxNumEcBytes; ++i) {
            for (BlockPair block : blocks) {
                byte[] ecBytes = block.getErrorCorrectionBytes();
                if (i < ecBytes.length) {
                    result.write(ecBytes[i] & 0xFF, 8);
                }
            }
        }

        return resultBits;
    }

    protected byte[] generateECBytes(@NonNull byte[] dataBytes, int numEcBytesInBlock) {
        int numDataBytes = dataBytes.length;
        int[] toEncode = new int[numDataBytes + numEcBytesInBlock];
        for (int i = 0; i < numDataBytes; i++) {
            toEncode[i] = dataBytes[i] & 0xFF;
        }
        new RSEncoder(GF.QR_CODE_FIELD_256).encode(toEncode, numEcBytesInBlock);

        byte[] ecBytes = new byte[numEcBytesInBlock];
        for (int i = 0; i < numEcBytesInBlock; i++) {
            ecBytes[i] = (byte) toEncode[numDataBytes + i];
        }
        return ecBytes;
    }

    protected void getNumDataBytesAndNumECBytesForBlockID(int numTotalBytes, int numDataBytes, int numRSBlocks, int blockID, @NonNull int[] numDataBytesInBlock, @NonNull int[] numECBytesInBlock) {
        if (blockID >= numRSBlocks) {
            throw new IllegalArgumentException("Block ID too large");
        }
        int numRsBlocksInGroup2 = numTotalBytes % numRSBlocks;
        int numRsBlocksInGroup1 = numRSBlocks - numRsBlocksInGroup2;
        int numTotalBytesInGroup1 = numTotalBytes / numRSBlocks;
        int numTotalBytesInGroup2 = numTotalBytesInGroup1 + 1;
        int numDataBytesInGroup1 = numDataBytes / numRSBlocks;
        int numDataBytesInGroup2 = numDataBytesInGroup1 + 1;
        int numEcBytesInGroup1 = numTotalBytesInGroup1 - numDataBytesInGroup1;
        int numEcBytesInGroup2 = numTotalBytesInGroup2 - numDataBytesInGroup2;
        if (numEcBytesInGroup1 != numEcBytesInGroup2) {
            throw new IllegalStateException("EC bytes mismatch");
        }
        if (numRSBlocks != numRsBlocksInGroup1 + numRsBlocksInGroup2) {
            throw new IllegalStateException("RS blocks mismatch");
        }
        if (numTotalBytes != ((numDataBytesInGroup1 + numEcBytesInGroup1) * numRsBlocksInGroup1) + ((numDataBytesInGroup2 + numEcBytesInGroup2) * numRsBlocksInGroup2)) {
            throw new IllegalStateException("Total bytes mismatch");
        }

        if (blockID < numRsBlocksInGroup1) {
            numDataBytesInBlock[0] = numDataBytesInGroup1;
            numECBytesInBlock[0] = numEcBytesInGroup1;
        } else {
            numDataBytesInBlock[0] = numDataBytesInGroup2;
            numECBytesInBlock[0] = numEcBytesInGroup2;
        }
    }

    protected QRVersion choose(int numInputBits, @NonNull QRLevel level)  {
        for (int v = 1; v <= MAX_VERSION; v++)  {
            QRVersion version = QRVersion.QR_VERSIONS[v];
            if (version.getTotalCodewords() - version.getBlocks(level).getTotalECCodewords() >= (numInputBits + 7) >>> 3)   {
                return version;
            }
        }
        throw new IllegalArgumentException("Data too large!");
    }

    @RequiredArgsConstructor
    @Getter
    protected static class BlockPair {
        @NonNull
        private final byte[] dataBytes;
        @NonNull
        private final byte[] errorCorrectionBytes;
    }
}
