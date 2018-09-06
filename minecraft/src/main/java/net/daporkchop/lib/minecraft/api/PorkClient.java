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

package net.daporkchop.lib.minecraft.api;

import lombok.NonNull;
import net.daporkchop.lib.minecraft.data.Platform;
import net.daporkchop.lib.minecraft.data.Protocol;

import java.net.InetSocketAddress;

/**
 * @author DaPorkchop_
 */
public interface PorkClient {
    /**
     * Checks if this client is running (connected to a server)
     *
     * @return whether or not this client is running
     */
    boolean isRunning();

    /**
     * Disconnects from the server
     *
     * @throws net.daporkchop.lib.minecraft.api.exception.AlreadyClosedException if not connected
     */
    void disconnect();

    /**
     * Connects to a server at the given address
     *
     * @param address the address to connect to
     */
    void connect(@NonNull InetSocketAddress address);

    /**
     * Gets this client's session (player)
     *
     * @return this client's sesssion
     */
    PorkSession getSession();

    /**
     * Gets this player's network protocol version
     *
     * @return this player's network protocol version
     */
    Protocol getProtocol();

    /**
     * Pings a server
     *
     * @param address the server's address
     * @return a {@link PingData} if the server could be pinged, null otherwise
     */
    PingData ping(@NonNull InetSocketAddress address);

    /**
     * Gets this player's minecraft platform
     *
     * @return this player's minecraft platform
     */
    default Platform getPlatform() {
        return this.getProtocol().getPlatform();
    }
}
