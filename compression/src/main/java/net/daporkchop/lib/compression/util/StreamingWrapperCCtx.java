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
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.compression.CCtx;
import net.daporkchop.lib.compression.CompressionProvider;
import net.daporkchop.lib.compression.PDeflater;
import net.daporkchop.lib.compression.util.exception.InvalidCompressionLevelException;
import net.daporkchop.lib.natives.util.exception.InvalidBufferTypeException;
import net.daporkchop.lib.unsafe.util.AbstractReleasable;

/**
 * An implementation of {@link CCtx} that wraps a {@link PDeflater}.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public class StreamingWrapperCCtx extends AbstractReleasable implements CCtx {
    @Getter
    protected final CompressionProvider provider;

    protected PDeflater deflater;
    protected ByteBuf   dict;
    @Getter
    protected int       level;

    public StreamingWrapperCCtx(@NonNull CompressionProvider provider, int level) {
        this(provider, provider.deflater(level), level);
    }

    protected StreamingWrapperCCtx(@NonNull CompressionProvider provider, @NonNull PDeflater deflater, int level) {
        this.provider = provider;
        this.deflater = deflater;
        this.level = level;
    }

    @Override
    public boolean compress(@NonNull ByteBuf src, @NonNull ByteBuf dst) throws InvalidBufferTypeException {
        this.deflater.reset();
        if (this.dict != null) {
            //set dictionary if needed
            this.deflater.dict(this.dict);
        }
        return this.deflater.fullDeflate(src, dst);
    }

    @Override
    public CCtx level(int level) throws InvalidCompressionLevelException {
        if (level != this.level) {
            InvalidCompressionLevelException.validate(level, this.provider);

            //release deflater with old level and create new one with the new level
            this.deflater.release();
            this.deflater = this.provider.deflater(this.level = level);
        }
        return this;
    }

    @Override
    public CCtx reset() {
        this.deflater.reset();
        if (this.dict != null) {
            this.dict.release();
            this.dict = null;
        }
        return this;
    }

    @Override
    public boolean hasDict() {
        return this.deflater.hasDict();
    }

    @Override
    public CCtx dict(@NonNull ByteBuf dict) throws InvalidBufferTypeException, UnsupportedOperationException {
        if (!this.deflater.hasDict()) {
            throw new UnsupportedOperationException();
        }

        //release old dictionary
        if (this.dict != null) {
            this.dict.release();
            this.dict = null;
        }

        this.dict = this.deflater.assertAcceptable(dict).retainedSlice();
        return this;
    }

    @Override
    public boolean directAccepted() {
        return this.deflater.directAccepted();
    }

    @Override
    public boolean heapAccepted() {
        return this.deflater.heapAccepted();
    }

    @Override
    public boolean isAcceptable(@NonNull ByteBuf buf) {
        return this.deflater.isAcceptable(buf);
    }

    @Override
    public ByteBuf assertAcceptable(@NonNull ByteBuf buf) throws InvalidBufferTypeException {
        return this.deflater.assertAcceptable(buf);
    }

    @Override
    protected void doRelease() {
        this.deflater.release();
        this.deflater = null;
    }
}
