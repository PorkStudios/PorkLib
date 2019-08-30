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

package net.daporkchop.lib.binary.io.wrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.io.DataIn;
import net.daporkchop.lib.binary.util.exception.EndOfStreamException;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public class WrapperInputAsData implements DataIn {
    @NonNull
    protected final InputStream delegate;
    protected boolean forwardClose = false;

    @Override
    public InputStream java() {
        return this.delegate;
    }

    @Override
    public int next() throws IOException {
        int v = this.delegate.read();
        if (v != -1) {
            return v;
        } else {
            throw new EndOfStreamException();
        }
    }

    @Override
    public void nextBytes(@NonNull byte[] arr, int start, int count) throws IOException {
        while (count > 0)   {
            int read = this.delegate.read(arr, start, count);
            count -= read;
            start += read;
        }
    }

    @Override
    public int nextAvailableBytes(@NonNull byte[] arr) throws IOException {
        return this.delegate.read(arr);
    }

    @Override
    public int nextAvailableBytes(@NonNull byte[] arr, int start, int count) throws IOException {
        return this.delegate.read(arr, start, count);
    }

    @Override
    public void skip(long count) throws IOException {
        while (count > 0L)  {
            count -= this.delegate.skip(count);
        }
    }

    @Override
    public void close() throws IOException {
        if (this.forwardClose) {
            this.delegate.close();
        }
    }
}
