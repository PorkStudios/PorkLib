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

package net.daporkchop.lib.hash.impl.skid;

public abstract class BaseHash {

    protected String name;

    protected int hashSize;

    protected int blockSize;

    protected long count;

    protected byte[] buffer;

    protected BaseHash(String name, int hashSize, int blockSize) {
        super();

        this.name = name;
        this.hashSize = hashSize;
        this.blockSize = blockSize;
        this.buffer = new byte[blockSize];

        this.resetContext();
    }

    public String name() {
        return this.name;
    }

    public int hashSize() {
        return this.hashSize;
    }

    public int blockSize() {
        return this.blockSize;
    }

    public void update(byte b) {
        int i = (int) (this.count % this.blockSize);
        this.count++;
        this.buffer[i] = b;
        if (i == (this.blockSize - 1)) {
            this.transform(this.buffer, 0);
        }
    }

    public void update(byte[] b, int offset, int len) {
        int n = (int) (this.count % this.blockSize);
        this.count += len;
        int partLen = this.blockSize - n;
        int i = 0;

        if (len >= partLen) {
            System.arraycopy(b, offset, this.buffer, n, partLen);
            this.transform(this.buffer, 0);
            for (i = partLen; i + this.blockSize - 1 < len; i += this.blockSize) {
                this.transform(b, offset + i);
            }
            n = 0;
        }

        if (i < len) {
            System.arraycopy(b, offset + i, this.buffer, n, len - i);
        }
    }

    public byte[] digest() {
        byte[] tail = this.padBuffer();
        this.update(tail, 0, tail.length);
        byte[] result = this.getResult();

        this.reset();

        return result;
    }

    public void reset() {
        this.count = 0L;
        for (int i = 0; i < this.blockSize; ) {
            this.buffer[i++] = 0;
        }

        this.resetContext();
    }

    public abstract Object clone();

    public abstract boolean selfTest();

    protected abstract byte[] padBuffer();

    protected abstract byte[] getResult();

    protected abstract void resetContext();

    protected abstract void transform(byte[] in, int offset);
}
