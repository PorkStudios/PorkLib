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

package net.daporkchop.lib.network.protocol;

import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;

import java.io.IOException;

/**
 * A packet contains data that is sent over the interwebs :D
 *
 * @author DaPorkchop_
 */
public interface Packet {
    /**
     * Read this packet from the received data
     *
     * @param in an instance of {@link DataIn} containing the data written to this packet
     * @throws IOException if an IO exception happens i guess lol
     */
    void read(DataIn in) throws IOException;

    /**
     * Write this packet to data that will be sent over the network
     *
     * @param out an instance of {@link DataOut} that this packet's data should be written to
     * @throws IOException if an IO exception happens i guess lol
     */
    void write(DataOut out) throws IOException;

    /**
     * Get the direction that this packet may be sent in
     *
     * @return the direction that this packet may be sent in
     */
    PacketDirection getDirection();

    /**
     * This packet's ID
     *
     * @return this packet's ID
     */
    byte getId();
}
