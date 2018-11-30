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
 */

package net.daporkchop.lib.hash.alg;

/**
 * Implementation of the SHA3 hash algorithm
 *
 * @author Some BouncyCastle dev
 */
public class SHA3 extends Keccak {
    public SHA3() {
        this(256);
    }

    public SHA3(int bitLength) {
        super(checkBitLength(bitLength));
    }

    private static int checkBitLength(int bitLength) {
        switch (bitLength) {
            case 224:
            case 256:
            case 384:
            case 512:
                return bitLength;
            default:
                throw new IllegalArgumentException("'bitLength' " + bitLength + " not supported for SHA-3");
        }
    }

    @Override
    public String getAlgorithmName() {
        return String.format("SHA3-%d", this.fixedOutputLength);
    }

    @Override
    public int doFinal(byte[] out, int outOff) {
        this.absorbBits(0x02, 2);

        return super.doFinal(out, outOff);
    }

    @Override
    protected int doFinal(byte[] out, int outOff, byte partialByte, int partialBits) {
        if (partialBits < 0 || partialBits > 7) {
            throw new IllegalArgumentException("'partialBits' must be in the range [0,7]");
        }

        int finalInput = ((int) partialByte & ((1 << partialBits) - 1)) | (0x02 << partialBits);
        int finalBits = partialBits + 2;

        if (finalBits >= 8) {
            this.absorb(new byte[]{(byte) finalInput}, 0, 1);
            finalBits -= 8;
            finalInput >>>= 8;
        }

        return super.doFinal(out, outOff, (byte) finalInput, finalBits);
    }
}
