/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2026 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.daporkchop.lib.binary.nio.channel;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.ClosedChannelException;

/**
 * @author DaPorkchop_
 */
public abstract class AbstractNonInterruptibleSynchronizedChannel implements Channel {
    private volatile boolean open = true;

    @Override
    public final boolean isOpen() {
        return this.open;
    }

    @Override
    public synchronized final void close() throws IOException {
        if (!this.open) {
            return;
        }

        this.open = false;
        this.implCloseChannel();
    }

    protected abstract void implCloseChannel() throws IOException;

    protected final void requireOpen() throws IOException {
        assert Thread.holdsLock(this) : "caller must be synchronized";

        if (!this.open) {
            throw new ClosedChannelException();
        }
    }
}
