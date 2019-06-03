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

package net.daporkchop.lib.network.session;

import lombok.NonNull;
import net.daporkchop.lib.network.transport.TransportEngine;
import net.daporkchop.lib.network.util.TransportEngineHolder;

import java.util.Collection;

/**
 * @author DaPorkchop_
 */
public interface Reliable<Impl extends Reliable<Impl>> extends TransportEngineHolder {
    /**
     * Gets this channel's fallback reliability level. Packets that are sent without having a specific reliability
     * defined will be sent using this reliability.
     *
     * @return this channel's fallback reliability level
     */
    Reliability fallbackReliability();

    /**
     * Gets this channel's fallback reliability level. Packets that are sent without having a specific reliability
     * defined will be sent using this reliability.
     *
     * @param reliability the new fallback reliability level to use
     * @return this channel's fallback reliability level
     * @throws IllegalArgumentException if the given reliability level is not supported by this channel
     */
    Impl fallbackReliability(@NonNull Reliability reliability) throws IllegalArgumentException;

    /**
     * @see TransportEngine#supportedReliabilities()
     */
    default Collection<Reliability> supportedReliabilities()    {
        return this.transportEngine().supportedReliabilities();
    }

    /**
     * @see TransportEngine#isReliabilitySupported(Reliability)
     */
    default boolean isReliabilitySupported(@NonNull Reliability reliability) {
        return this.transportEngine().isReliabilitySupported(reliability);
    }
}
