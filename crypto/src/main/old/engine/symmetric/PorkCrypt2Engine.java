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

package net.daporkchop.lib.crypto.engine.symmetric;

import net.daporkchop.lib.binary.util.ByteArrayShift;
import net.daporkchop.lib.hash.helper.PorkHashHelper;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.KeyParameter;

/**
 * An implementation of the PorkCrypt2 symmetric encryption algorithm.
 *
 * @author DaPorkchop_
 */
public class PorkCrypt2Engine implements BlockCipher {
    private static final int BLOCK_SIZE = 16;
    private static final int BLOCK_MASK = BLOCK_SIZE - 1;
    private static final String NAME = "PorkCrypt2";
    private final byte[] inBuf = new byte[BLOCK_SIZE], outBuf = new byte[BLOCK_SIZE], key = new byte[BLOCK_SIZE];
    private boolean encrypting;
    private byte[] workingKey;
    private byte[] keyHash;
    private int rounds;

    @Override
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        if (params instanceof KeyParameter) {
            this.encrypting = forEncryption;
            this.workingKey = ((KeyParameter) params).getKey();

            if ((this.workingKey.length & (BLOCK_SIZE - 1)) != 0) {
                byte[] workingKey = this.workingKey;
                this.workingKey = null;
                throw new IllegalArgumentException("Key length must be a multiple of " + BLOCK_SIZE + " (given: " + workingKey.length + ')');
            }
            this.rounds = this.workingKey.length >> 4;
            this.keyHash = PorkHashHelper.porkhash(this.workingKey);

            return;
        }

        throw new IllegalArgumentException("invalid parameter passed to PorkCrypt2 init - " + params.getClass().getName());
    }

    @Override
    public String getAlgorithmName() {
        return NAME;
    }

    @Override
    public int getBlockSize() {
        return BLOCK_SIZE;
    }

    @Override
    public synchronized int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (in.length - inOff < BLOCK_SIZE || out.length - outOff < BLOCK_SIZE) {
            throw new DataLengthException();
        }

        System.arraycopy(in, inOff, this.inBuf, 0, BLOCK_SIZE);
        for (int i = 0; i < this.rounds; i++) {
            System.arraycopy(this.workingKey, this.encrypting ? i << 4 : (this.rounds - i - 1) << 4, this.key, 0, BLOCK_SIZE);
            if (this.encrypting) {
                this.encryptBlock(i);
            } else {
                this.decryptBlock((this.rounds - 1) - i);
            }
            if (i + 1 != this.rounds) {
                System.arraycopy(this.outBuf, 0, this.inBuf, 0, BLOCK_SIZE);
            }
        }
        System.arraycopy(this.outBuf, 0, out, outOff, BLOCK_SIZE);

        return BLOCK_SIZE;
    }

    @Override
    public void reset() {
        this.workingKey = null;
    }

    private void encryptBlock(int r) {
        int k = this.key[r] & 0xFF;
        {
            //phase 1: shift the array
            //if the unsigned value of the byte is even, shift the array to the left. otherwise,
            //shift to the right. the number of bits to shift by is then shifted one to the right, to
            //discard the lsb which has already been used.
            if ((k & 1) == 0) {
                ByteArrayShift.circularShiftLeft(this.inBuf, this.outBuf, k >> 1);
            } else {
                ByteArrayShift.circularShiftRight(this.inBuf, this.outBuf, k >> 1);
            }
        }

        {
            //phase 2: scramble array
            //not very strong, but should be confusing enough (also has the extremely useful side effect
            //of copying bytes from inBuf to outBuf)
            int a = k + r;
            for (int i = 0; i < BLOCK_SIZE; i++) {
                this.outBuf[i] = this.inBuf[~(a + i) & BLOCK_MASK];
            }
        }

        {
            //phase 2: XOR everything with the hash of the key
            for (int i = 0; i < BLOCK_SIZE; i++) {
                byte b = this.outBuf[i];
                for (int j = 0; j < this.keyHash.length; j++) {
                    b ^= this.keyHash[(j + i * r) & BLOCK_MASK];
                }
                this.outBuf[i] = b;
            }
        }
    }

    private void decryptBlock(int r) {
        int k = this.key[r] & 0xFF;
        {
            //works just like above, except we iterate backwards through the hash
            for (int i = 0; i < BLOCK_SIZE; i++) {
                byte b = this.inBuf[i];
                for (int j = this.keyHash.length - 1; j >= 0; j--) {
                    b ^= this.keyHash[(j + i * r) & BLOCK_MASK];
                }
                this.inBuf[i] = b;
            }
        }

        {
            //also like above, also backwards
            int a = k + r;
            for (int i = 0; i < BLOCK_SIZE; i++) {
                this.outBuf[i] = this.inBuf[~(a + i) & BLOCK_MASK];
            }
        }

        {
            //also like above, also backwards
            if ((k & 1) == 0) {
                ByteArrayShift.circularShiftRight(this.outBuf, this.inBuf, k >> 1);
            } else {
                ByteArrayShift.circularShiftLeft(this.outBuf, this.inBuf, k >> 1);
            }
        }
    }
}
