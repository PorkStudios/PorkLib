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

package net.daporkchop.lib.crypto.keygen;

import lombok.NonNull;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

import static net.daporkchop.lib.unsafe.PUnsafe.*;

/**
 * @author DaPorkchop_
 */
public class EntropyPool implements AutoCloseable {
    protected final long offset;
    protected final long size;
    protected volatile boolean closed = false;
    protected final AtomicLong count = new AtomicLong(0L);

    public EntropyPool(long bufferSize) {
        this.offset = allocateMemory(bufferSize);
        this.size = bufferSize;

        this.clear();
    }

    public void clear() {
        this.count.set(0L);
        setMemory(this.offset, this.size, (byte) 0);
    }

    public void update(@NonNull byte[] entropy) {
        if (entropy.length == 0) {
            return;
        }
        this.count.addAndGet(entropy.length);
        long limit = this.offset + this.size - 8L;
        for (long pos = this.offset; pos < limit; pos++) {
            long val = getLong(pos);
            for (int i = entropy.length - 1; i >= 0; i--) {
                val ^= entropy[i] * 8200158685984349719L + 1156627166349328451L;
                val += entropy[i] * 6223392334446656207L + 4106409398621068397L;
            }
            putLong(pos, val);
        }
    }

    public byte[] get(int size) {
        return this.get(size, ThreadLocalRandom.current());
    }

    public byte[] get(int size, @NonNull Random random) {
        byte[] b = new byte[size];
        this.get(b, random);
        return b;
    }

    public void get(@NonNull byte[] dst) {
        this.get(dst, ThreadLocalRandom.current());
    }

    public void get(@NonNull byte[] dst, @NonNull Random random) {
        long limit = this.size - 8L;
        for (int i = dst.length - 1; i >= 0; i--) {
            long val = 435939424300075867L;
            for (int j = random.nextInt(4096) + 4096; j >= 0; j--) {
                val += getLong(this.offset + (random.nextLong() & Long.MAX_VALUE) % limit) * 8148005417735576993L + 1586753936759271967L;
                val ^= getLong(this.offset + (random.nextLong() & Long.MAX_VALUE) % limit) * 5319060908530498721L + 6876297655203449329L;
            }
            dst[i] = (byte) (val ^
                    (val >>> 8L) ^
                    (val >>> 16L) ^
                    (val >>> 24L) ^
                    (val >>> 32L) ^
                    (val >>> 40L) ^
                    (val >>> 48L) ^
                    (val >>> 56L));
        }
    }

    @Override
    public synchronized void close() {
        if (!this.closed) {
            this.closed = true;
            freeMemory(this.offset);
        }
    }

    public long getCount()    {
        return this.count.get();
    }
}
