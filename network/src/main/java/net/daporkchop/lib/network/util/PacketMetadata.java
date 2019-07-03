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

package net.daporkchop.lib.network.util;

import io.netty.util.Recycler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.network.util.reliability.Reliability;

/**
 * Additional metadata that is used as part of a packet's encoding.
 * <p>
 * Some options may or may not be supported by certain transport engines or configurations thereof, and will throw
 * exceptions at runtime if used.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(chain = true, fluent = true)
public final class PacketMetadata {
    protected static final int RELIABILITY_MASK = 1 << 0;
    public static final int CHANNELID_MASK = 1 << 1;
    public static final int PROTOCOLID_MASK = 1 << 2;
    public static final int ORIGINAL_MASK = 1 << 3;

    private static final Recycler<PacketMetadata> RECYCLER = new Recycler<PacketMetadata>() {
        @Override
        protected PacketMetadata newObject(@NonNull Handle<PacketMetadata> handle) {
            return new PacketMetadata(handle);
        }
    };

    /**
     * Obtains an instance of {@link PacketMetadata} with the given settings.
     *
     * @param reliability the reliability level
     * @param channelId   the channel id
     * @param protocolId  the protocol id
     * @return an instance of {@link PacketMetadata} with the given settings
     */
    public static PacketMetadata instance(Reliability reliability, int channelId, int protocolId, boolean original) {
        PacketMetadata metadata = RECYCLER.get();
        metadata.reliability = reliability;
        metadata.channelId = channelId;
        metadata.protocolId = protocolId;
        metadata.setFlags = original ? ORIGINAL_MASK : 0;
        return metadata;
    }

    //handle
    private final Recycler.Handle<PacketMetadata> handle;

    //values
    @Getter
    private Reliability reliability;
    @Getter
    private int channelId;
    @Getter
    private int protocolId;

    //set field flags, used to check if a field was actually changed or is as default
    private int setFlags;

    public PacketMetadata reliability(@NonNull Reliability reliability) {
        this.reliability = reliability;
        this.setFlags |= RELIABILITY_MASK;
        return this;
    }

    public PacketMetadata channelId(int channelId) {
        this.channelId = channelId;
        this.setFlags |= CHANNELID_MASK;
        return this;
    }

    public PacketMetadata protocolId(int protocolId) {
        this.protocolId = protocolId;
        this.setFlags |= PROTOCOLID_MASK;
        return this;
    }

    public boolean checkReliabilitySet() {
        return (this.setFlags & RELIABILITY_MASK) != 0;
    }

    public boolean checkChannelIdSet() {
        return (this.setFlags & CHANNELID_MASK) != 0;
    }

    public boolean checkProtocolIdSet() {
        return (this.setFlags & PROTOCOLID_MASK) != 0;
    }

    public boolean checkAnySet(int mask)    {
        return (this.setFlags & mask) != 0;
    }

    public boolean isOriginal() {
        return (this.setFlags & ORIGINAL_MASK) != 0;
    }

    /**
     * Clears this {@link PacketMetadata} instance and makes it available to the recycler for future reuse.
     * <p>
     * This method should not be invoked by user code unless you know EXACTLY what you're doing!
     */
    public void release() {
        this.reliability = null;
        this.channelId = this.protocolId = this.setFlags = 0;
        if (this.handle != null) {
            this.handle.recycle(this);
        }
    }

    /**
     * Creates a duplicate of this {@link PacketMetadata} instance.
     * <p>
     * This method should not be invoked by user code unless you know EXACTLY what you're doing!
     *
     * @return a duplicate of this {@link PacketMetadata} instance
     */
    public PacketMetadata clone() {
        PacketMetadata clone = RECYCLER.get();
        clone.reliability = this.reliability;
        clone.channelId = this.channelId;
        clone.protocolId = this.protocolId;
        clone.setFlags = this.setFlags & ~ORIGINAL_MASK;
        return clone;
    }
}
