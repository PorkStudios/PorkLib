/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.natives.zlib;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * Implementation of {@link PInflater} in pure Java.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public final class JavaInflater implements PInflater {
    private long inputAddr;
    private long outputAddr;

    private final Inflater inflater;
    private byte[] inputBuffer = new byte[8192];
    private byte[] outputBuffer = new byte[8192];

    @Getter
    private int readBytes;
    @Getter
    private int writtenBytes;

    private int inputSize;
    private int outputSize;

    public JavaInflater(@NonNull ZlibMode mode)   {
        switch (mode)   {
            case ZLIB:
                this.inflater = new Inflater(false);
                break;
            case RAW:
                this.inflater = new Inflater(true);
                break;
            case AUTO:
            case GZIP:
                throw new IllegalArgumentException("Pure Java inflater doesn't support Gzip!");
            default:
                throw new IllegalArgumentException("Invalid mode: " + mode);
        }
    }

    @Override
    public void input(long addr, int size) {
        this.inputAddr = addr;
        this.inputSize = size;
    }

    @Override
    public void output(long addr, int size) {
        this.outputAddr = addr;
        this.outputSize = size;
    }

    @Override
    public void inflate() {
        long prevBytesRead = this.inflater.getBytesRead();
        int inputCount = Math.min(this.inputSize, this.inputBuffer.length);
        if (inputCount > 0) {
            PUnsafe.copyMemory(null, this.inputAddr, this.inputBuffer, PUnsafe.ARRAY_BYTE_BASE_OFFSET, inputCount);
        }
        this.inflater.setInput(this.inputBuffer, 0, inputCount);
        try {
            int written = this.inflater.inflate(this.outputBuffer, 0, Math.min(this.outputSize, this.outputBuffer.length));
            if (written > 0) {
                PUnsafe.copyMemory(this.outputBuffer, PUnsafe.ARRAY_BYTE_BASE_OFFSET, null, this.outputAddr, written);
            }
            this.readBytes = (int) (this.inflater.getBytesRead() - prevBytesRead);
            this.writtenBytes = written;

            this.inputAddr += this.readBytes;
            this.inputSize -= this.readBytes;
            this.outputAddr += written;
            this.outputAddr -= written;
        } catch (DataFormatException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean finished() {
        return this.inflater.finished();
    }

    @Override
    public void reset() {
        this.inflater.reset();
        this.inputAddr = this.outputAddr = 0L;
        this.inputSize = this.outputSize = 0;
        this.readBytes = this.writtenBytes = 0;
    }

    @Override
    public void release() throws AlreadyReleasedException {
        this.inflater.end();
        this.inputBuffer = this.outputBuffer = null;
    }
}
