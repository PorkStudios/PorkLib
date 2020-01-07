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

package net.daporkchop.lib.http.impl.netty.util;

import io.netty.channel.FileRegion;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.http.entity.transfer.TransferSession;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

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
