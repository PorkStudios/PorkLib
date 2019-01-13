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
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.daporkchop.lib.encoding.qr.util.QRBitOutput;
import net.daporkchop.lib.encoding.qr.util.QRLevel;
import net.daporkchop.lib.encoding.qr.util.QRMask;
import net.daporkchop.lib.encoding.util.XYIndexedBitSet;

import java.util.BitSet;
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
     */
    @NonNull
    protected QRMask mask = QRMask.MASK_0;

    public QRCode encode(@NonNull byte[] data)   {
        int version = getBestVersionForData(data.length, this.level);
        int size = getSizeForVersion(version);
        BitSet bits = new BitSet(size * size);
        XYIndexedBitSet xyBits = new XYIndexedBitSet(size, bits);
        QRBitOutput out = new QRBitOutput(version, bits);

        out.put(0b0100, 4); //hard-coded to byte mode
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
        }
        return null;
    }
}
