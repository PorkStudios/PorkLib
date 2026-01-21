/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2022 DaPorkchop_
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
 *
 */

package net.daporkchop.lib.http.impl.netty.util;

import io.netty.channel.FileRegion;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.http.entity.transfer.TransferSession;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.common.util.exception.AlreadyReleasedException;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public final class TransferSessionAsFileRegion implements FileRegion {
    protected static final long TRANSFERRED_OFFSET = PUnsafe.pork_getOffset(TransferSessionAsFileRegion.class, "transferred");
    protected static final long REFCNT_OFFSET      = PUnsafe.pork_getOffset(TransferSessionAsFileRegion.class, "refCnt");

    protected volatile long            transferred;
    @NonNull
    protected final    TransferSession session;
    protected volatile int refCnt = 0;

    @Override
    public long position() {
        try {
            return this.session.position();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long count() {
        try {
            return this.session.length();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long transfered() {
        return this.transferred;
    }

    @Override
    public long transferTo(WritableByteChannel target, long position) throws IOException {
        try {
            long transferred = this.session.transfer(position, target);
            PUnsafe.getAndAddLong(this, TRANSFERRED_OFFSET, transferred);
            return transferred;
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public FileRegion retain() {
        this.session.retain();
        return this;
    }

    @Override
    public FileRegion retain(int increment) {
        for (int i = 0; i < increment; i++) {
            this.session.retain();
        }
        return this;
    }

    @Override
    public FileRegion touch() {
        return this;
    }

    @Override
    public FileRegion touch(Object hint) {
        return this;
    }

    @Override
    public boolean release() {
        return this.session.release();
    }

    @Override
    public boolean release(int decrement) {
        boolean last = false;
        for (int i = 0; i < decrement; i++) {
            if (last)   {
                throw new AlreadyReleasedException();
            }
            last = this.session.release();
        }
        return last;
    }
}
