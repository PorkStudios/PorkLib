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

package net.daporkchop.lib.gdxnetwork.protocol.encapsulated;

import net.daporkchop.lib.gdxnetwork.protocol.PacketProtocol;

/**
 * @author DaPorkchop_
 */
public class EncapsulatedProtocol extends PacketProtocol {
    public static final EncapsulatedProtocol INSTANCE = new EncapsulatedProtocol();

    public static final int WRAPPED_ID = 16;
    public static final int MESSAGE_ID = 17;

    private EncapsulatedProtocol() {
        super("Encapsulated", 1);

        this.registerPacket(WrappedPacket::new, new WrappedPacket.WrappedPacketHandler());
        this.registerPacket(MessagePacket::new, new MessagePacket.MessagePacketHandler());
    }
}
