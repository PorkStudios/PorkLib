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

import java.io.IOException;
import java.io.InputStream;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public class WrapperDataAsInput extends InputStream {
    @NonNull
    protected final DataIn delegate;
    protected boolean forwardClose = false;

    @Override
    public int read() throws IOException {
        return this.delegate.next();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.delegate.nextAvailableBytes(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return this.delegate.nextAvailableBytes(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        this.delegate.skip(n); //TODO: i don't think this has exactly the same behavior
        return n;
    }

    @Override
    public void close() throws IOException {
        if (this.forwardClose)  {
            this.delegate.close();
        }
    }
}
