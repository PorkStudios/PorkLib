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

package net.daporkchop.lib.network.protocol.netty.sctp;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.packet.Packet;

import java.io.IOException;

/**
 * Used so that reliability parameters and so forth can be processed in encoders/decoders
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class SctpPacketWrapper implements Packet, Logging {
    @NonNull
    private final Packet packet;

    private final int channel;
    private final boolean ordered;

    @Override
    public void read(DataIn in) throws IOException {
        throw new UnsupportedOperationException("read");
    }

    @Override
    public void write(DataOut out) throws IOException {
        throw new UnsupportedOperationException("write");
    }

    @Override
    public String toString() {
        return this.format("packet=${0}, channel=${1}, ordered=${2}", this.packet.getClass(), this.channel, this.ordered);
    }
}
