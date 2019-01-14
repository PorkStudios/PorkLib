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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.daporkchop.lib.encoding.qr.util.QRLevel;
import net.daporkchop.lib.encoding.qr.util.QRVersionCapacity;

/**
 * @author DaPorkchop_
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class QRConstants {
    public static final int SIZE_VERSION_1 = 21;
    public static final int INCREASE_PER_VERSION = 4;
    public static final int MAX_VERSION = 40;

    public static final int ORIENTATION_COUNT = 3;
    public static final int ORIENTATION_SIZE = 7;
    public static final int ORIENTATION_PIXEL_COUNT = ORIENTATION_SIZE * ORIENTATION_SIZE * ORIENTATION_COUNT;

    public static final int ALIGNMENT_SIZE = 5;
    public static final int ALIGNMENT_PIXEL_COUNT = ALIGNMENT_SIZE * ALIGNMENT_SIZE;

    public static final int FORMAT_INFO_PIXEL_COUNT = 31;

    public static final int VERSION_INFO_PIXEL_COUNT = 3 * 6;
    public static final int VERSION_INFO_THRESHOLD = 7;

    //from https://www.thonky.com/qr-code-tutorial/character-capacities
    //gosh darn it i didn't need to make this
    public static final QRVersionCapacity[] VERSION_CAPACITIES = {
            new QRVersionCapacity(1, 17, 14, 11, 7),
            new QRVersionCapacity(2, 32, 26, 20, 14),
            new QRVersionCapacity(3, 53, 42, 32, 24),
            new QRVersionCapacity(4, 78, 62, 46, 34),
            new QRVersionCapacity(5, 106, 84, 60, 44),
            new QRVersionCapacity(6, 134, 106, 74, 58),
            new QRVersionCapacity(7, 154, 122, 86, 64),
            new QRVersionCapacity(8, 192, 152, 108, 84),
            new QRVersionCapacity(9, 230, 180, 130, 98),
            new QRVersionCapacity(10, 271, 213, 151, 119),
            new QRVersionCapacity(11, 321, 251, 117, 137),
            new QRVersionCapacity(12, 367, 287, 203, 155),
            new QRVersionCapacity(13, 425, 331, 241, 177),
            new QRVersionCapacity(14, 458, 362, 258, 194),
            new QRVersionCapacity(15, 520, 412, 292, 220),
            new QRVersionCapacity(16, 586, 450, 322, 250),
            new QRVersionCapacity(17, 644, 504, 364, 280),
            new QRVersionCapacity(18, 718, 568, 394, 310),
            new QRVersionCapacity(19, 792, 624, 442, 338),
            new QRVersionCapacity(20, 858, 666, 482, 382),
            new QRVersionCapacity(21, 929, 711, 509, 403),
            new QRVersionCapacity(22, 1003, 779, 565, 439),
            new QRVersionCapacity(23, 1091, 857, 611, 461),
            new QRVersionCapacity(24, 1171, 911, 661, 511),
            new QRVersionCapacity(25, 1273, 997, 713, 535),
            new QRVersionCapacity(26, 1367, 1059, 751, 593),
            new QRVersionCapacity(27, 1465, 1125, 805, 625),
            new QRVersionCapacity(28, 1528, 1190, 868, 658),
            new QRVersionCapacity(29, 1628, 1264, 908, 698),
            new QRVersionCapacity(30, 1732, 1370, 982, 742),
            new QRVersionCapacity(31, 1840, 1452, 1030, 790),
            new QRVersionCapacity(32, 1952, 1538, 1112, 843),
            new QRVersionCapacity(33, 2068, 1628, 1168, 898),
            new QRVersionCapacity(34, 2188, 1722, 1228, 958),
            new QRVersionCapacity(35, 2303, 1809, 1283, 983),
            new QRVersionCapacity(36, 2431, 1911, 1351, 1051),
            new QRVersionCapacity(37, 2563, 1989, 1423, 1093),
            new QRVersionCapacity(38, 2699, 2099, 1499, 1139),
            new QRVersionCapacity(39, 2809, 2213, 1579, 1219),
            new QRVersionCapacity(40, 2953, 2331, 1663, 1273)
    };

    public static int getSizeForVersion(int version) {
        return SIZE_VERSION_1 + INCREASE_PER_VERSION * (ensureValidVersion(version) - 1);
    }

    public static int getVersionForSize(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Size must be greater than 0!");
        }
        return ensureValidVersion((size - 17) >>> 2);
    }

    public static int ensureValidVersion(int version) {
        if (version <= 0 || version > MAX_VERSION) {
            throw new IllegalArgumentException(String.format("Invalid version: %d (must be in range 1-%d)", version, MAX_VERSION));
        } else {
            return version;
        }
    }

    public static int getAlignmentCountForVersion(int version) {
        if (ensureValidVersion(version) == 1) {
            return 0;
        } else if (version >= 35) {
            return 46;
        } else if (version >= 28) {
            return 33;
        } else if (version >= 21) {
            return 22;
        } else if (version >= 14) {
            return 13;
        } else if (version >= 7) {
            return 6;
        } else if (version >= 2) {
            return 1;
        } else {
            throw new IllegalStateException();
        }
    }

    public static int getTimingPixelsForVersion(int version) {
        return (getSizeForVersion(version) - 16) << 1;
    }

    public static boolean hasVersionInfo(int version) {
        return ensureValidVersion(version) >= VERSION_INFO_THRESHOLD;
    }

    public static int getVersionInfoPixelCount(int version) {
        return hasVersionInfo(version) ? VERSION_INFO_PIXEL_COUNT : 0;
    }

    public static int getPixelCountForVersion(int version) {
        int i = getSizeForVersion(version);
        return i * i;
    }

    public static int getContentBitCountForVersion(int version) {
        return getPixelCountForVersion(version)
                - ORIENTATION_PIXEL_COUNT
                - 45 //orientation separator things
                - ALIGNMENT_PIXEL_COUNT * getAlignmentCountForVersion(version)
                - getTimingPixelsForVersion(version)
                - FORMAT_INFO_PIXEL_COUNT
                - getVersionInfoPixelCount(version);
    }

    public static int getUsableByteCountForVersion(int version, @NonNull QRLevel level) {
        return VERSION_CAPACITIES[ensureValidVersion(version)].getCapacity(level);
    }

    public static int getBestVersionForData(int dataLength, @NonNull QRLevel level) {
        if (dataLength <= 0) {
            throw new IllegalStateException("Must be at least 1 byte!");
        }
        for (int i = 0; i < VERSION_CAPACITIES.length; i++) {
            if (VERSION_CAPACITIES[i].getCapacity(level) >= dataLength) {
                return i + 1;
            }
        }
        throw new IllegalArgumentException(String.format("Data length too big: %d bytes", dataLength));
    }

    public static int getLengthPrefixBits(int version) {
        if (ensureValidVersion(version) >= 10)  {
            return 16;
        } else if (version >= 1)    {
            return 8;
        } else {
            throw new IllegalStateException();
        }
    }

    public static int getRequiredBits(int version, @NonNull QRLevel level)  {
        int diff = (getLengthPrefixBits(version) >>> 3) + 1;
        return (getUsableByteCountForVersion(version, level) + diff) << 3;
    }
}
