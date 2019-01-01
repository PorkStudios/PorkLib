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

package net.daporkchop.lib.network.protocol.netty.sctp;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.logging.Logging;

/**
 * Used so that reliability parameters and so forth can be processed in encoders/decoders
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor
@Getter
public class SctpPacketWrapper implements Logging {
    @NonNull
    private final ByteBuf data;

    private final int channel;
    private final int id;
    private final boolean ordered;

    @Override
    public String toString() {
        return this.format("packet=${0}, channel=${1}, ordered=${2}, id=${3}", this.data, this.channel, this.ordered, this.id);
    }
}

@AllArgsConstructor
@Getter
class UnencodedSctpPacket implements Logging {
    @NonNull
    private final Object message;

    private final int channel;
    private final int id;
    private final boolean ordered;

    @Override
    public String toString() {
        return this.format("message=${0}, channel=${1}, ordered=${2}, id=${3}", this.message.getClass(), this.channel, this.ordered, this.id);
    }
}
