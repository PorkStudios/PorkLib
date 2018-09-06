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

package net.daporkchop.lib.network.protocol.encapsulated;

/**
 * Contains constant fields for use in the encapsulated protocol
 *
 * @author DaPorkchop_
 */
public interface EncapsulatedConstants {
    /**
     * The version of the protocol
     */
    int NETWORK_VERSION = 1;

    byte
            ID_HANDSHAKEINIT = 0,
            ID_HANDSHAKECLIENTINIT = 1,
            ID_HANDSHAKEENCODING = 2,
            ID_HANDSHAKESTARTENCRYPTION = 3,
            ID_HANDSHAKECOMPLETE = 4,
            ID_AUTHENTICATE = 5,
            ID_AUTHENTICATECOMPLETE = 6,
            ID_WRAPPED = 64,
            ID_BATCH = 65,
            ID_KEEPALIVE = 66,
            ID_DISCONNECT = 67,
            ID_PING = 68;
}
