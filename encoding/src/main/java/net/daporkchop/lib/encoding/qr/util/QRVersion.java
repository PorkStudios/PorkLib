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

package net.daporkchop.lib.encoding.qr.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import net.daporkchop.lib.encoding.util.XYIndexedBitSet;

import static net.daporkchop.lib.encoding.qr.QRConstants.getSizeForVersion;

/**
 * @author DaPorkchop_
 */
@Getter
public class QRVersion {
    private static final int[] VERSION_DECODE_INFO = {
            0x07C94,
            0x085BC,
            0x09A99,
            0x0A4D3,
            0x0BBF6,
            0x0C762,
            0x0D847,
            0x0E60D,
            0x0F928,
            0x10B78,
            0x1145D,
            0x12A17,
            0x13532,
            0x149A6,
            0x15683,
            0x168C9,
            0x177EC,
            0x18EC4,
            0x191E1,
            0x1AFAB,
            0x1B08E,
            0x1CC1A,
            0x1D33F,
            0x1ED75,
            0x1F250,
            0x209D5,
            0x216F0,
            0x228BA,
            0x2379F,
            0x24B0B,
            0x2542E,
            0x26A64,
            0x27541,
            0x28C69
    };

    public static final QRVersion[] QR_VERSIONS = {
            new QRVersion(1, new int[]{},
                    new ECBlocks(7, new ECB(1, 19)),
                    new ECBlocks(10, new ECB(1, 16)),
                    new ECBlocks(13, new ECB(1, 13)),
                    new ECBlocks(17, new ECB(1, 9))),
            new QRVersion(2, new int[]{6, 18},
                    new ECBlocks(10, new ECB(1, 34)),
                    new ECBlocks(16, new ECB(1, 28)),
                    new ECBlocks(22, new ECB(1, 22)),
                    new ECBlocks(28, new ECB(1, 16))),
            new QRVersion(3, new int[]{6, 22},
                    new ECBlocks(15, new ECB(1, 55)),
                    new ECBlocks(26, new ECB(1, 44)),
                    new ECBlocks(18, new ECB(2, 17)),
                    new ECBlocks(22, new ECB(2, 13))),
            new QRVersion(4, new int[]{6, 26},
                    new ECBlocks(20, new ECB(1, 80)),
                    new ECBlocks(18, new ECB(2, 32)),
                    new ECBlocks(26, new ECB(2, 24)),
                    new ECBlocks(16, new ECB(4, 9))),
            new QRVersion(5, new int[]{6, 30},
                    new ECBlocks(26, new ECB(1, 108)),
                    new ECBlocks(24, new ECB(2, 43)),
                    new ECBlocks(18, new ECB(2, 15),
                            new ECB(2, 16)),
                    new ECBlocks(22, new ECB(2, 11),
                            new ECB(2, 12))),
            new QRVersion(6, new int[]{6, 34},
                    new ECBlocks(18, new ECB(2, 68)),
                    new ECBlocks(16, new ECB(4, 27)),
                    new ECBlocks(24, new ECB(4, 19)),
                    new ECBlocks(28, new ECB(4, 15))),
            new QRVersion(7, new int[]{6, 22, 38},
                    new ECBlocks(20, new ECB(2, 78)),
                    new ECBlocks(18, new ECB(4, 31)),
                    new ECBlocks(18, new ECB(2, 14),
                            new ECB(4, 15)),
                    new ECBlocks(26, new ECB(4, 13),
                            new ECB(1, 14))),
            new QRVersion(8, new int[]{6, 24, 42},
                    new ECBlocks(24, new ECB(2, 97)),
                    new ECBlocks(22, new ECB(2, 38),
                            new ECB(2, 39)),
                    new ECBlocks(22, new ECB(4, 18),
                            new ECB(2, 19)),
                    new ECBlocks(26, new ECB(4, 14),
                            new ECB(2, 15))),
            new QRVersion(9, new int[]{6, 26, 46},
                    new ECBlocks(30, new ECB(2, 116)),
                    new ECBlocks(22, new ECB(3, 36),
                            new ECB(2, 37)),
                    new ECBlocks(20, new ECB(4, 16),
                            new ECB(4, 17)),
                    new ECBlocks(24, new ECB(4, 12),
                            new ECB(4, 13))),
            new QRVersion(10, new int[]{6, 28, 50},
                    new ECBlocks(18, new ECB(2, 68),
                            new ECB(2, 69)),
                    new ECBlocks(26, new ECB(4, 43),
                            new ECB(1, 44)),
                    new ECBlocks(24, new ECB(6, 19),
                            new ECB(2, 20)),
                    new ECBlocks(28, new ECB(6, 15),
                            new ECB(2, 16))),
            new QRVersion(11, new int[]{6, 30, 54},
                    new ECBlocks(20, new ECB(4, 81)),
                    new ECBlocks(30, new ECB(1, 50),
                            new ECB(4, 51)),
                    new ECBlocks(28, new ECB(4, 22),
                            new ECB(4, 23)),
                    new ECBlocks(24, new ECB(3, 12),
                            new ECB(8, 13))),
            new QRVersion(12, new int[]{6, 32, 58},
                    new ECBlocks(24, new ECB(2, 92),
                            new ECB(2, 93)),
                    new ECBlocks(22, new ECB(6, 36),
                            new ECB(2, 37)),
                    new ECBlocks(26, new ECB(4, 20),
                            new ECB(6, 21)),
                    new ECBlocks(28, new ECB(7, 14),
                            new ECB(4, 15))),
            new QRVersion(13, new int[]{6, 34, 62},
                    new ECBlocks(26, new ECB(4, 107)),
                    new ECBlocks(22, new ECB(8, 37),
                            new ECB(1, 38)),
                    new ECBlocks(24, new ECB(8, 20),
                            new ECB(4, 21)),
                    new ECBlocks(22, new ECB(12, 11),
                            new ECB(4, 12))),
            new QRVersion(14, new int[]{6, 26, 46, 66},
                    new ECBlocks(30, new ECB(3, 115),
                            new ECB(1, 116)),
                    new ECBlocks(24, new ECB(4, 40),
                            new ECB(5, 41)),
                    new ECBlocks(20, new ECB(11, 16),
                            new ECB(5, 17)),
                    new ECBlocks(24, new ECB(11, 12),
                            new ECB(5, 13))),
            new QRVersion(15, new int[]{6, 26, 48, 70},
                    new ECBlocks(22, new ECB(5, 87),
                            new ECB(1, 88)),
                    new ECBlocks(24, new ECB(5, 41),
                            new ECB(5, 42)),
                    new ECBlocks(30, new ECB(5, 24),
                            new ECB(7, 25)),
                    new ECBlocks(24, new ECB(11, 12),
                            new ECB(7, 13))),
            new QRVersion(16, new int[]{6, 26, 50, 74},
                    new ECBlocks(24, new ECB(5, 98),
                            new ECB(1, 99)),
                    new ECBlocks(28, new ECB(7, 45),
                            new ECB(3, 46)),
                    new ECBlocks(24, new ECB(15, 19),
                            new ECB(2, 20)),
                    new ECBlocks(30, new ECB(3, 15),
                            new ECB(13, 16))),
            new QRVersion(17, new int[]{6, 30, 54, 78},
                    new ECBlocks(28, new ECB(1, 107),
                            new ECB(5, 108)),
                    new ECBlocks(28, new ECB(10, 46),
                            new ECB(1, 47)),
                    new ECBlocks(28, new ECB(1, 22),
                            new ECB(15, 23)),
                    new ECBlocks(28, new ECB(2, 14),
                            new ECB(17, 15))),
            new QRVersion(18, new int[]{6, 30, 56, 82},
                    new ECBlocks(30, new ECB(5, 120),
                            new ECB(1, 121)),
                    new ECBlocks(26, new ECB(9, 43),
                            new ECB(4, 44)),
                    new ECBlocks(28, new ECB(17, 22),
                            new ECB(1, 23)),
                    new ECBlocks(28, new ECB(2, 14),
                            new ECB(19, 15))),
            new QRVersion(19, new int[]{6, 30, 58, 86},
                    new ECBlocks(28, new ECB(3, 113),
                            new ECB(4, 114)),
                    new ECBlocks(26, new ECB(3, 44),
                            new ECB(11, 45)),
                    new ECBlocks(26, new ECB(17, 21),
                            new ECB(4, 22)),
                    new ECBlocks(26, new ECB(9, 13),
                            new ECB(16, 14))),
            new QRVersion(20, new int[]{6, 34, 62, 90},
                    new ECBlocks(28, new ECB(3, 107),
                            new ECB(5, 108)),
                    new ECBlocks(26, new ECB(3, 41),
                            new ECB(13, 42)),
                    new ECBlocks(30, new ECB(15, 24),
                            new ECB(5, 25)),
                    new ECBlocks(28, new ECB(15, 15),
                            new ECB(10, 16))),
            new QRVersion(21, new int[]{6, 28, 50, 72, 94},
                    new ECBlocks(28, new ECB(4, 116),
                            new ECB(4, 117)),
                    new ECBlocks(26, new ECB(17, 42)),
                    new ECBlocks(28, new ECB(17, 22),
                            new ECB(6, 23)),
                    new ECBlocks(30, new ECB(19, 16),
                            new ECB(6, 17))),
            new QRVersion(22, new int[]{6, 26, 50, 74, 98},
                    new ECBlocks(28, new ECB(2, 111),
                            new ECB(7, 112)),
                    new ECBlocks(28, new ECB(17, 46)),
                    new ECBlocks(30, new ECB(7, 24),
                            new ECB(16, 25)),
                    new ECBlocks(24, new ECB(34, 13))),
            new QRVersion(23, new int[]{6, 30, 54, 78, 102},
                    new ECBlocks(30, new ECB(4, 121),
                            new ECB(5, 122)),
                    new ECBlocks(28, new ECB(4, 47),
                            new ECB(14, 48)),
                    new ECBlocks(30, new ECB(11, 24),
                            new ECB(14, 25)),
                    new ECBlocks(30, new ECB(16, 15),
                            new ECB(14, 16))),
            new QRVersion(24, new int[]{6, 28, 54, 80, 106},
                    new ECBlocks(30, new ECB(6, 117),
                            new ECB(4, 118)),
                    new ECBlocks(28, new ECB(6, 45),
                            new ECB(14, 46)),
                    new ECBlocks(30, new ECB(11, 24),
                            new ECB(16, 25)),
                    new ECBlocks(30, new ECB(30, 16),
                            new ECB(2, 17))),
            new QRVersion(25, new int[]{6, 32, 58, 84, 110},
                    new ECBlocks(26, new ECB(8, 106),
                            new ECB(4, 107)),
                    new ECBlocks(28, new ECB(8, 47),
                            new ECB(13, 48)),
                    new ECBlocks(30, new ECB(7, 24),
                            new ECB(22, 25)),
                    new ECBlocks(30, new ECB(22, 15),
                            new ECB(13, 16))),
            new QRVersion(26, new int[]{6, 30, 58, 86, 114},
                    new ECBlocks(28, new ECB(10, 114),
                            new ECB(2, 115)),
                    new ECBlocks(28, new ECB(19, 46),
                            new ECB(4, 47)),
                    new ECBlocks(28, new ECB(28, 22),
                            new ECB(6, 23)),
                    new ECBlocks(30, new ECB(33, 16),
                            new ECB(4, 17))),
            new QRVersion(27, new int[]{6, 34, 62, 90, 118},
                    new ECBlocks(30, new ECB(8, 122),
                            new ECB(4, 123)),
                    new ECBlocks(28, new ECB(22, 45),
                            new ECB(3, 46)),
                    new ECBlocks(30, new ECB(8, 23),
                            new ECB(26, 24)),
                    new ECBlocks(30, new ECB(12, 15),
                            new ECB(28, 16))),
            new QRVersion(28, new int[]{6, 26, 50, 74, 98, 122},
                    new ECBlocks(30, new ECB(3, 117),
                            new ECB(10, 118)),
                    new ECBlocks(28, new ECB(3, 45),
                            new ECB(23, 46)),
                    new ECBlocks(30, new ECB(4, 24),
                            new ECB(31, 25)),
                    new ECBlocks(30, new ECB(11, 15),
                            new ECB(31, 16))),
            new QRVersion(29, new int[]{6, 30, 54, 78, 102, 126},
                    new ECBlocks(30, new ECB(7, 116),
                            new ECB(7, 117)),
                    new ECBlocks(28, new ECB(21, 45),
                            new ECB(7, 46)),
                    new ECBlocks(30, new ECB(1, 23),
                            new ECB(37, 24)),
                    new ECBlocks(30, new ECB(19, 15),
                            new ECB(26, 16))),
            new QRVersion(30, new int[]{6, 26, 52, 78, 104, 130},
                    new ECBlocks(30, new ECB(5, 115),
                            new ECB(10, 116)),
                    new ECBlocks(28, new ECB(19, 47),
                            new ECB(10, 48)),
                    new ECBlocks(30, new ECB(15, 24),
                            new ECB(25, 25)),
                    new ECBlocks(30, new ECB(23, 15),
                            new ECB(25, 16))),
            new QRVersion(31, new int[]{6, 30, 56, 82, 108, 134},
                    new ECBlocks(30, new ECB(13, 115),
                            new ECB(3, 116)),
                    new ECBlocks(28, new ECB(2, 46),
                            new ECB(29, 47)),
                    new ECBlocks(30, new ECB(42, 24),
                            new ECB(1, 25)),
                    new ECBlocks(30, new ECB(23, 15),
                            new ECB(28, 16))),
            new QRVersion(32, new int[]{6, 34, 60, 86, 112, 138},
                    new ECBlocks(30, new ECB(17, 115)),
                    new ECBlocks(28, new ECB(10, 46),
                            new ECB(23, 47)),
                    new ECBlocks(30, new ECB(10, 24),
                            new ECB(35, 25)),
                    new ECBlocks(30, new ECB(19, 15),
                            new ECB(35, 16))),
            new QRVersion(33, new int[]{6, 30, 58, 86, 114, 142},
                    new ECBlocks(30, new ECB(17, 115),
                            new ECB(1, 116)),
                    new ECBlocks(28, new ECB(14, 46),
                            new ECB(21, 47)),
                    new ECBlocks(30, new ECB(29, 24),
                            new ECB(19, 25)),
                    new ECBlocks(30, new ECB(11, 15),
                            new ECB(46, 16))),
            new QRVersion(34, new int[]{6, 34, 62, 90, 118, 146},
                    new ECBlocks(30, new ECB(13, 115),
                            new ECB(6, 116)),
                    new ECBlocks(28, new ECB(14, 46),
                            new ECB(23, 47)),
                    new ECBlocks(30, new ECB(44, 24),
                            new ECB(7, 25)),
                    new ECBlocks(30, new ECB(59, 16),
                            new ECB(1, 17))),
            new QRVersion(35, new int[]{6, 30, 54, 78, 102, 126, 150},
                    new ECBlocks(30, new ECB(12, 121),
                            new ECB(7, 122)),
                    new ECBlocks(28, new ECB(12, 47),
                            new ECB(26, 48)),
                    new ECBlocks(30, new ECB(39, 24),
                            new ECB(14, 25)),
                    new ECBlocks(30, new ECB(22, 15),
                            new ECB(41, 16))),
            new QRVersion(36, new int[]{6, 24, 50, 76, 102, 128, 154},
                    new ECBlocks(30, new ECB(6, 121),
                            new ECB(14, 122)),
                    new ECBlocks(28, new ECB(6, 47),
                            new ECB(34, 48)),
                    new ECBlocks(30, new ECB(46, 24),
                            new ECB(10, 25)),
                    new ECBlocks(30, new ECB(2, 15),
                            new ECB(64, 16))),
            new QRVersion(37, new int[]{6, 28, 54, 80, 106, 132, 158},
                    new ECBlocks(30, new ECB(17, 122),
                            new ECB(4, 123)),
                    new ECBlocks(28, new ECB(29, 46),
                            new ECB(14, 47)),
                    new ECBlocks(30, new ECB(49, 24),
                            new ECB(10, 25)),
                    new ECBlocks(30, new ECB(24, 15),
                            new ECB(46, 16))),
            new QRVersion(38, new int[]{6, 32, 58, 84, 110, 136, 162},
                    new ECBlocks(30, new ECB(4, 122),
                            new ECB(18, 123)),
                    new ECBlocks(28, new ECB(13, 46),
                            new ECB(32, 47)),
                    new ECBlocks(30, new ECB(48, 24),
                            new ECB(14, 25)),
                    new ECBlocks(30, new ECB(42, 15),
                            new ECB(32, 16))),
            new QRVersion(39, new int[]{6, 26, 54, 82, 110, 138, 166},
                    new ECBlocks(30, new ECB(20, 117),
                            new ECB(4, 118)),
                    new ECBlocks(28, new ECB(40, 47),
                            new ECB(7, 48)),
                    new ECBlocks(30, new ECB(43, 24),
                            new ECB(22, 25)),
                    new ECBlocks(30, new ECB(10, 15),
                            new ECB(67, 16))),
            new QRVersion(40, new int[]{6, 30, 58, 86, 114, 142, 170},
                    new ECBlocks(30, new ECB(19, 118),
                            new ECB(6, 119)),
                    new ECBlocks(28, new ECB(18, 47),
                            new ECB(31, 48)),
                    new ECBlocks(30, new ECB(34, 24),
                            new ECB(34, 25)),
                    new ECBlocks(30, new ECB(20, 15),
                            new ECB(61, 16)))
    };

    public static QRVersion getProvisionalVersionForDimension(int dimension) {
        if (dimension % 4 != 1) {
            throw new IllegalArgumentException();
        }
        try {
            return getVersionForNumber((dimension - 17) / 4);
        } catch (IllegalArgumentException ignored) {
            throw new IllegalArgumentException();
        }
    }

    public static QRVersion getVersionForNumber(int versionNumber) {
        if (versionNumber < 1 || versionNumber > 40) {
            throw new IllegalArgumentException();
        }
        return QR_VERSIONS[versionNumber - 1];
    }

    public static QRVersion decodeVersionInformation(int versionBits) {
        int bestDifference = Integer.MAX_VALUE;
        int bestVersion = 0;
        for (int i = 0; i < VERSION_DECODE_INFO.length; i++) {
            int targetVersion = VERSION_DECODE_INFO[i];
            if (targetVersion == versionBits) {
                return getVersionForNumber(i + 7);
            }
            int bitsDifference = Integer.bitCount(versionBits ^ targetVersion);
            if (bitsDifference < bestDifference) {
                bestVersion = i + 7;
                bestDifference = bitsDifference;
            }
        }
        if (bestDifference <= 3) {
            return getVersionForNumber(bestVersion);
        }
        return null;
    }

    protected final int versionNumber;
    protected final int[] alignmentPatternCenters;
    protected final ECBlocks[] ecBlocks;
    protected final int totalCodewords;

    protected QRVersion(int versionNumber, @NonNull int[] alignmentPatternCenters, ECBlocks... ecBlocks) {
        this.versionNumber = versionNumber;
        this.alignmentPatternCenters = alignmentPatternCenters;
        this.ecBlocks = ecBlocks;
        int total = 0;
        int ecCodewords = ecBlocks[0].getEcCodewordsPerBlock();
        ECB[] ecbArray = ecBlocks[0].getEcBlocks();
        for (ECB ecBlock : ecbArray) {
            total += ecBlock.getCount() * (ecBlock.getDataCodewords() + ecCodewords);
        }
        this.totalCodewords = total;
    }

    public ECBlocks getBlocks(@NonNull QRLevel ecLevel) {
        return this.ecBlocks[ecLevel.ordinal()];
    }

    public void writeFunctionPattern(@NonNull XYIndexedBitSet bits) {
        int size = getSizeForVersion(this.versionNumber);
        if (bits.getSize() != size) {
            throw new IllegalArgumentException(String.format("Invalid grid size! Found %d but expected %d!", bits.getSize(), size));
        }

        bits.setArea(0, 0, 9, 9);
        bits.setArea(size - 8, 0, 8, 9);
        bits.setArea(0, size - 8, 9, 8);

        int max = this.alignmentPatternCenters.length;
        for (int x = 0; x < max; x++) {
            int i = this.alignmentPatternCenters[x] - 2;
            for (int y = 0; y < max; y++) {
                if ((x == 0 && (y == 0 || y == max - 1)) || (x == max - 1 && y == 0)) {
                    continue;
                }
                bits.setArea(this.alignmentPatternCenters[y] - 2, i, 5, 5);
            }
        }

        bits.setArea(6, 9, 1, size - 17);
        bits.setArea(9, 6, size - 17, 1);

        if (this.versionNumber > 6) {
            bits.setArea(size - 11, 0, 3, 6);
            bits.setArea(0, size - 11, 6, 3);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(this.versionNumber);
    }

    @Getter
    @ToString
    public static class ECBlocks {
        protected final int ecCodewordsPerBlock;
        protected final ECB[] ecBlocks;

        ECBlocks(int ecCodewordsPerBlock, @NonNull ECB... ecBlocks) {
            this.ecCodewordsPerBlock = ecCodewordsPerBlock;
            this.ecBlocks = ecBlocks;
        }

        public int getNumBlocks() {
            int total = 0;
            for (ECB ecBlock : this.ecBlocks) {
                total += ecBlock.getCount();
            }
            return total;
        }

        public int getTotalECCodewords() {
            return this.ecCodewordsPerBlock * this.getNumBlocks();
        }
    }

    @AllArgsConstructor
    @Getter
    @ToString
    public static class ECB {
        protected final int count;
        protected final int dataCodewords;
    }
}
