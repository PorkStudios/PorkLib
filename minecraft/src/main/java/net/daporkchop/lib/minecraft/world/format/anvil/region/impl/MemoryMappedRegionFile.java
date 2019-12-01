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

package net.daporkchop.lib.minecraft.world.format.anvil.region.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.NonNull;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.minecraft.world.format.anvil.region.AbstractRegionFile;
import net.daporkchop.lib.minecraft.world.format.anvil.region.RegionConstants;
import net.daporkchop.lib.minecraft.world.format.anvil.region.RegionOpenOptions;
import net.daporkchop.lib.minecraft.world.format.anvil.region.ex.CorruptedRegionException;
import sun.nio.ch.DirectBuffer;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * A read-only implementation of {@link net.daporkchop.lib.minecraft.world.format.anvil.region.RegionFile} which maps the entire region file into memory
 * at once.
 *
 * @author DaPorkchop_
 */
public final class MemoryMappedRegionFile extends AbstractRegionFile {
    protected final MappedByteBuffer map;
    protected final ByteBuf nettyBuf;

    public MemoryMappedRegionFile(@NonNull File file, @NonNull RegionOpenOptions options) throws IOException {
        super(file, options);

        this.map = this.channel.map(FileChannel.MapMode.READ_ONLY, 0L, this.channel.size());
        this.nettyBuf = Unpooled.wrappedBuffer(((DirectBuffer) this.map).address(), RegionConstants.HEADER_BYTES, false).asReadOnly();
    }

    @Override
    protected boolean optionsSupported(@NonNull RegionOpenOptions options) {
        return options.access() == Access.READ_ONLY && options.mode() == Mode.MMAP_FULL;
    }

    @Override
    protected ByteBuf headersBuf() {
        return this.nettyBuf;
    }

    @Override
    protected ByteBuf doRead(int x, int z, int offsetIndex) throws IOException {
        int offset = this.nettyBuf.getInt(offsetIndex);
        int pos = (offset >>> 8) * RegionConstants.SECTOR_BYTES;
        int length = this.map.getInt(pos);
        int maxLength = ((offset & 0xFF) * RegionConstants.SECTOR_BYTES) - RegionConstants.LENGTH_HEADER_SIZE;
        if (length < 0 || length > maxLength) {
            throw new CorruptedRegionException(String.format("Length at sector %d (offset %d) is %d! (should be max. %d)", offset >>> 8, pos, length, maxLength));
        }
        return Unpooled.wrappedBuffer(this.nettyBuf.memoryAddress() + pos + RegionConstants.LENGTH_HEADER_SIZE, length, false).asReadOnly();
    }

    @Override
    protected void handleDelete(int x, int z, int startIndex, int length) {
        //shouldn't ever be called
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doFlush() throws IOException {
        //shouldn't ever be called
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doClose() throws IOException {
        PorkUtil.release(this.map);
        this.channel.close();
    }

    @Override
    public boolean readOnly() {
        return true;
    }
}
