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

package net.daporkchop.lib.minecraft.common;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.daporkchop.lib.minecraft.api.PorkServer;
import net.daporkchop.lib.minecraft.api.PorkSession;
import net.daporkchop.lib.minecraft.data.Protocol;
import net.daporkchop.lib.primitive.map.LongObjectMap;
import net.daporkchop.lib.primitive.map.concurrent.LongObjectConcurrentTreeHashMap;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public abstract class BaseServer implements PorkServer {
    @NonNull
    private final Protocol protocol;
    /**
     * The maximum number of players that can be connected to this server at any given time
     */
    private final int maxPlayers;
    private final LongObjectMap<PorkSession> players = new LongObjectConcurrentTreeHashMap<>();
    /**
     * This server's current MOTD (message of the day)
     * <p>
     * Shown in the server list
     */
    @NonNull
    @Setter
    private String motd = "A Minecraft server";
}
