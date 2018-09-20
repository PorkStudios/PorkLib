/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
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

package net.daporkchop.lib.network.util;

import lombok.NonNull;
import net.daporkchop.lib.network.packet.encapsulated.LargeDataPacket;

import java.util.BitSet;

/**
 * @author DaPorkchop_
 */
public class LargeReceiveBuf {
    public final byte[] data;
    public final BitSet occupied;
    public final int totalSize;
    public final int totalBlocks;

    public LargeReceiveBuf(int size)    {
        if (size <= 0 || size > NetworkConstants.LARGE_PACKET_MAX_SIZE)   {
            throw new IllegalStateException(String.format("Illegal size: %d", size));
        }

        this.totalBlocks = (size >> 10) + ((size & 0x3FF) == 0 ? 0 : 1);
        this.occupied = new BitSet(this.totalBlocks);
        this.totalSize = size;
        this.data = new byte[size];
    }

    public boolean isComplete() {
        return this.occupied.cardinality() == this.totalBlocks;
    }

    public void receive(@NonNull LargeDataPacket packet)   {
        int block = packet.offset >> 10;
        if (block >= this.totalBlocks)   {
            throw new IllegalStateException(String.format("Invalid block ID! Total: %d, given: %d", this.totalBlocks, block));
        }
        if (this.occupied.get(block))   {
            throw new IllegalStateException("Already received data at block! Packet info: " + packet);
        }
        this.occupied.set(block);
        System.arraycopy(packet.data, 0, this.data, packet.offset, packet.data.length);
    }
}
