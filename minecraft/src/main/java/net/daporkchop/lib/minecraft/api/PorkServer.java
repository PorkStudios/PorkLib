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

import net.daporkchop.lib.minecraft.data.Platform;
import net.daporkchop.lib.minecraft.data.Protocol;
import net.daporkchop.lib.primitive.map.LongObjectMap;

/**
 * @author DaPorkchop_
 */
public interface PorkServer {
    /**
     * Stops the server, disconnecting all players and closing any additional resources related to it
     */
    void stop();

    /**
     * Checks if the server is currently online and accepting connections
     *
     * @return if the server is running
     */
    boolean isRunning();

    /**
     * Binds to the given port and start listening for connections
     *
     * @param port
     */
    void bind(short port);

    /**
     * Gets all players currently connected to the server
     *
     * @return a collection of all currently connected players
     */
    LongObjectMap<PorkSession> getPlayers();

    /**
     * Gets this player's network protocol version
     *
     * @return this player's network protocol version
     */
    Protocol getProtocol();

    /**
     * Gets this player's minecraft platform
     *
     * @return this player's minecraft platform
     */
    default Platform getPlatform() {
        return this.getProtocol().getPlatform();
    }
}
