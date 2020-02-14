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

package net.daporkchop.lib.compression.util;

import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.compression.CCtx;
import net.daporkchop.lib.compression.CompressionProvider;
import net.daporkchop.lib.compression.DCtx;
import net.daporkchop.lib.compression.PInflater;
import net.daporkchop.lib.compression.util.exception.InvalidCompressionLevelException;
import net.daporkchop.lib.natives.util.exception.InvalidBufferTypeException;
import net.daporkchop.lib.unsafe.util.AbstractReleasable;

/**
 * An implementation of {@link DCtx} that wraps a {@link PInflater}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Accessors(fluent = true)
public class StreamingWrapperDCtx extends AbstractReleasable implements DCtx {
    @Getter
    protected final CompressionProvider provider;

    @NonNull
    protected PInflater inflater;
    protected ByteBuf   dict;

    public StreamingWrapperDCtx(@NonNull CompressionProvider provider) {
        this(provider, provider.inflater());
    }

    @Override
    public boolean decompress(@NonNull ByteBuf src, @NonNull ByteBuf dst) throws InvalidBufferTypeException {
        this.inflater.reset();
        if (this.dict != null) {
            //set dictionary if needed
            this.inflater.dict(this.dict);
        }
        return this.inflater.fullInflate(src, dst);
    }

    @Override
    public DCtx reset() {
        this.inflater.reset();
        if (this.dict != null) {
            this.dict.release();
            this.dict = null;
        }
        return this;
    }

    @Override
    public boolean hasDict() {
        return this.inflater.hasDict();
    }

    @Override
    public DCtx dict(@NonNull ByteBuf dict) throws InvalidBufferTypeException, UnsupportedOperationException {
        if (!this.inflater.hasDict()) {
            throw new UnsupportedOperationException();
        }

        //release old dictionary
        if (this.dict != null) {
            this.dict.release();
            this.dict = null;
        }

        this.dict = this.inflater.assertAcceptable(dict).retainedSlice();
        return this;
    }

    @Override
    public boolean directAccepted() {
        return this.inflater.directAccepted();
    }

    @Override
    public boolean heapAccepted() {
        return this.inflater.heapAccepted();
    }

    @Override
    public boolean isAcceptable(@NonNull ByteBuf buf) {
        return this.inflater.isAcceptable(buf);
    }

    @Override
    public ByteBuf assertAcceptable(@NonNull ByteBuf buf) throws InvalidBufferTypeException {
        return this.inflater.assertAcceptable(buf);
    }

    @Override
    protected void doRelease() {
        this.inflater.release();
        this.inflater = null;
    }
}
