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

package net.daporkchop.lib.db.remote;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.db.container.AbstractContainer;
import net.daporkchop.lib.db.container.Containers;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.network.packet.Codec;
import net.daporkchop.lib.network.packet.Packet;
import net.daporkchop.lib.network.packet.UserProtocol;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * @author DaPorkchop_
 */
@Getter
public class RemoteDBProtocol extends UserProtocol<RemoteDBConnection> implements Logging {
    @NonNull
    private final RemoteDB db;

    public RemoteDBProtocol(@NonNull RemoteDB db) {
        super("PorkLib DB - remote", 2);
        this.db = db;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void registerPackets() {
        //authentication packets

        //container packets
        try {
            for (Containers containerContainer : Containers.values()) {
                Class<? extends AbstractContainer> clazz = containerContainer.getClazz();
                Field field = clazz.getDeclaredField("PACKETS");
                if (field == null)  {
                    logger.debug("No additional packets found for ${0}", clazz);
                } else {
                    field.setAccessible(true);
                    Collection<Class<Codec<? extends Packet, RemoteDBConnection>>> packetClasses = (Collection<Class<Codec<? extends Packet, RemoteDBConnection>>>) field.get(null);
                    for (Class<Codec<? extends Packet, RemoteDBConnection>> packetClass : packetClasses)   {
                        Constructor<? extends Codec> constructor = packetClass.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        this.register((Codec<? extends Packet, RemoteDBConnection>) constructor.newInstance());
                    }
                    logger.debug("Registered ${0} extra packets for ${1}", packetClasses.size(), clazz);
                }
            }
        } catch (NoSuchFieldException
                | IllegalAccessException
                | NoSuchMethodException
                | InstantiationException
                | InvocationTargetException e)    {
            throw this.exception(e);
        }
    }

    @Override
    public RemoteDBConnection newConnection() {
        return new RemoteDBConnection(this.db);
    }
}
