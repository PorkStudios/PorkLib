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

package net.daporkchop.lib.minecraft.world.impl.section;

import io.netty.buffer.ByteBuf;
import net.daporkchop.lib.minecraft.world.Chunk;
import net.daporkchop.lib.minecraft.world.format.anvil.PooledByteArrayTag;
import net.daporkchop.lib.nbt.tag.notch.ByteArrayTag;
import net.daporkchop.lib.unsafe.capability.Releasable;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * @author DaPorkchop_
 */
public class PooledHeapSectionImpl extends HeapSectionImpl implements Releasable {
    private ByteBuf blocksBuf;
    private ByteBuf addBuf;
    private ByteBuf metaBuf;
    private ByteBuf blockLightBuf;
    private ByteBuf skyLightBuf;

    public PooledHeapSectionImpl(int y, Chunk chunk) {
        super(y, chunk);
    }

    @Override
    public void setBlocks(ByteArrayTag tag) {
        super.setBlocks(tag);
        this.blocksBuf = tag instanceof PooledByteArrayTag ? ((PooledByteArrayTag) tag).buf() : null;
    }

    @Override
    public void setAdd(ByteArrayTag tag) {
        super.setAdd(tag);
        this.addBuf = tag instanceof PooledByteArrayTag ? ((PooledByteArrayTag) tag).buf() : null;
    }

    @Override
    public void setMeta(ByteArrayTag tag) {
        super.setMeta(tag);
        this.metaBuf = tag instanceof PooledByteArrayTag ? ((PooledByteArrayTag) tag).buf() : null;
    }

    @Override
    public void setBlockLight(ByteArrayTag tag) {
        super.setBlockLight(tag);
        this.blockLightBuf = tag instanceof PooledByteArrayTag ? ((PooledByteArrayTag) tag).buf() : null;
    }

    @Override
    public void setSkyLight(ByteArrayTag tag) {
        super.setSkyLight(tag);
        this.skyLightBuf = tag instanceof PooledByteArrayTag ? ((PooledByteArrayTag) tag).buf() : null;
    }

    @Override
    public void release() throws AlreadyReleasedException {
        if (this.blocksBuf != null) this.blocksBuf.release();
        if (this.addBuf != null) this.addBuf.release();
        if (this.metaBuf != null) this.metaBuf.release();
        if (this.blockLightBuf != null) this.blockLightBuf.release();
        if (this.skyLightBuf != null) this.skyLightBuf.release();
    }
}
