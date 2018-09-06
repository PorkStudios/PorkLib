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
 *
 */

package net.daporkchop.lib.hash.impl;

import net.daporkchop.lib.encoding.Hexadecimal;
import net.daporkchop.lib.hash.impl.skid.BaseHash;

import java.util.Arrays;

import static java.lang.Math.sqrt;

/**
 * @author DaPorkchop_
 */
public class PorkHash extends BaseHash {
    public static final int BLOCK_SIZE = 64;
    public static final int HASH_ROUNDS = 16;

    private static final double PI = 3.14159665357897d;
    private static final double SQRT_2 = sqrt(2.0d);
    private static final double SQRT_3 = sqrt(3.0d);
    private static final double SQRT_5 = sqrt(5.0d);

    private static final byte[] EMPTY_HASH = Hexadecimal.decode("d9d36aee9a21f33f74ed44a6e60f2a4f386a201d08578e42629bb8bbc898c0eb35b33c6df1e2417e88dde92b361f0102ac1cd3b2d07d3f1f875dd9c5cf8e161c");
    private static final int MASK = BLOCK_SIZE - 1;
    private static Boolean test = null;
    private byte[] result = new byte[BLOCK_SIZE], buf = new byte[BLOCK_SIZE];

    public PorkHash() {
        super("PorkHash", 0, BLOCK_SIZE);
    }

    @Override
    protected void transform(byte[] inA, int offset) {
        byte[] in = this.buf;
        System.arraycopy(inA, offset, in, 0, BLOCK_SIZE);

        for (int round = 0; round < HASH_ROUNDS; round++) {
            {
                for (int i = 0; i < BLOCK_SIZE; i++) {
                    double x = Double.longBitsToDouble(
                            (this.result[(i - 4) & MASK] & 0xFFL) |
                                    ((this.result[(i - 3) & MASK] & 0xFFL) << 8) |
                                    ((this.result[(i - 2) & MASK] & 0xFFL) << 16) |
                                    ((this.result[(i - 1) & MASK] & 0xFFL) << 24) |
                                    ((this.result[(i + 1) & MASK] & 0xFFL) << 32) |
                                    ((this.result[(i + 2) & MASK] & 0xFFL) << 40) |
                                    ((this.result[(i + 3) & MASK] & 0xFFL) << 48) |
                                    ((this.result[(i + 4) & MASK] & 0xFFL) << 56)
                    );
                    double y = Double.longBitsToDouble(
                            (in[(i - 4) & MASK] & 0xFFL) |
                                    ((in[(i - 3) & MASK] & 0xFFL) << 8) |
                                    ((in[(i - 2) & MASK] & 0xFFL) << 16) |
                                    ((in[(i - 1) & MASK] & 0xFFL) << 24) |
                                    ((in[(i + 1) & MASK] & 0xFFL) << 32) |
                                    ((in[(i + 2) & MASK] & 0xFFL) << 40) |
                                    ((in[(i + 3) & MASK] & 0xFFL) << 48) |
                                    ((in[(i + 4) & MASK] & 0xFFL) << 56)
                    );
                    double z = Double.longBitsToDouble(
                            (this.result[(i << 4) & MASK] & 0xFFL) |
                                    ((this.result[(i << 3) & MASK] & 0xFFL) << 8) |
                                    ((this.result[(i << 2) & MASK] & 0xFFL) << 16) |
                                    ((this.result[(i << 1) & MASK] & 0xFFL) << 24) |
                                    ((this.result[(i >> 1) & MASK] & 0xFFL) << 32) |
                                    ((this.result[(i >> 2) & MASK] & 0xFFL) << 40) |
                                    ((this.result[(i >> 3) & MASK] & 0xFFL) << 48) |
                                    ((this.result[(i >> 4) & MASK] & 0xFFL) << 56)
                    );
                    double w = Double.longBitsToDouble(
                            (in[(i << 4) & MASK] & 0xFFL) |
                                    ((in[(i << 3) & MASK] & 0xFFL) << 8) |
                                    ((in[(i << 2) & MASK] & 0xFFL) << 16) |
                                    ((in[(i << 1) & MASK] & 0xFFL) << 24) |
                                    ((in[(i >> 1) & MASK] & 0xFFL) << 32) |
                                    ((in[(i >> 2) & MASK] & 0xFFL) << 40) |
                                    ((in[(i >> 3) & MASK] & 0xFFL) << 48) |
                                    ((in[(i >> 4) & MASK] & 0xFFL) << 56)
                    );
                    x += PI;
                    y += SQRT_2;
                    z += SQRT_3;
                    w += SQRT_5;
                    x *= SQRT_3;
                    y *= PI;
                    w *= SQRT_2;
                    z *= SQRT_5;
                    x /= z;
                    y /= w;

                    long l = Double.doubleToLongBits(sqrt(x * x + y * y + z * z + w * w));
                    in[i] ^= (byte) (l & 0xFF);
                    in[(i + 1) & MASK] ^= (byte) ((l >> 8) & 0xFF);
                    in[(i + 2) & MASK] ^= (byte) ((l >> 16) & 0xFF);
                    in[(i + 3) & MASK] ^= (byte) ((l >> 24) & 0xFF);
                    in[(i + 4) & MASK] ^= (byte) ((l >> 32) & 0xFF);
                    in[(i + 5) & MASK] ^= (byte) ((l >> 40) & 0xFF);
                    in[(i + 6) & MASK] ^= (byte) ((l >> 48) & 0xFF);
                    in[(i + 7) & MASK] ^= (byte) ((l >> 56) & 0xFF);
                }
            }

            {
                byte b1a = 0, b2a = 0, b1b = 0, b2b = 0;
                for (int i = 0; i < BLOCK_SIZE; i++) {
                    b1a ^= this.result[(i << 4) & MASK];
                    b1a ^= this.result[(i << 3) & MASK];
                    b1a ^= this.result[(i << 2) & MASK];
                    b1a ^= this.result[(i << 1) & MASK];
                    b1a ^= this.result[(i >> 1) & MASK];
                    b1a ^= this.result[(i >> 2) & MASK];
                    b1a ^= this.result[(i >> 3) & MASK];
                    b1a ^= this.result[(i >> 4) & MASK];
                    b1a ^= this.result[i];

                    b2a ^= in[(i << 4) & MASK];
                    b2a ^= in[(i << 3) & MASK];
                    b2a ^= in[(i << 2) & MASK];
                    b2a ^= in[(i << 1) & MASK];
                    b2a ^= in[(i >> 1) & MASK];
                    b2a ^= in[(i >> 2) & MASK];
                    b2a ^= in[(i >> 3) & MASK];
                    b2a ^= in[(i >> 4) & MASK];
                    b2a ^= in[i];

                    b1b ^= this.result[(i + 4) & MASK];
                    b1b ^= this.result[(i + 3) & MASK];
                    b1b ^= this.result[(i + 2) & MASK];
                    b1b ^= this.result[(i + 1) & MASK];
                    b1b ^= this.result[(i - 1) & MASK];
                    b1b ^= this.result[(i - 2) & MASK];
                    b1b ^= this.result[(i - 3) & MASK];
                    b1b ^= this.result[(i - 4) & MASK];
                    b1b ^= this.result[i];

                    b2b ^= in[(i + 4) & MASK];
                    b2b ^= in[(i + 3) & MASK];
                    b2b ^= in[(i + 2) & MASK];
                    b2b ^= in[(i + 1) & MASK];
                    b2b ^= in[(i - 1) & MASK];
                    b2b ^= in[(i - 2) & MASK];
                    b2b ^= in[(i - 3) & MASK];
                    b2b ^= in[(i - 4) & MASK];
                    b2b ^= in[i];
                }

                for (int i = 0; i < BLOCK_SIZE; i++) {
                    this.result[(i + 4) & MASK] ^= this.result[(i + 3) & MASK] ^= this.result[(i + 2) & MASK] ^= this.result[(i + 1) & MASK] ^= b1a ^ b2b;
                    this.result[(i - 1) & MASK] ^= this.result[(i - 2) & MASK] ^= this.result[(i - 3) & MASK] ^= this.result[(i - 4) & MASK] ^= b2a ^ b1b;
                }
            }
        }
    }

    @Override
    protected byte[] getResult() {
        return result;
    }

    @Override
    protected void resetContext() {
        result = new byte[BLOCK_SIZE];
    }

    @Override
    public Object clone() {
        return new PorkHash();
    }

    @Override
    public synchronized boolean selfTest() {
        //if (true) return true;
        if (test == null) {
            byte[] hash = new PorkHash().digest();
            test = Arrays.equals(hash, EMPTY_HASH);
        }
        return test;
    }

    @Override
    protected byte[] padBuffer() {
        byte[] result = this.result.clone();
        for (int i = 0; i < BLOCK_SIZE; i++) {
            result[i] ^= result[(i + 1) & 0x3F] ^ count * 31;
        }
        return result;
    }
}
