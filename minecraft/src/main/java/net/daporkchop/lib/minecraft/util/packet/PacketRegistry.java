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

package net.daporkchop.lib.minecraft.util.packet;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.primitive.lambda.function.ObjectToIntegerFunction;
import soupply.util.Packet;

import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The main definition of a protocol, maps packet IDs to Soupply {@link Packet} constructors, translators
 * and decoders
 *
 * @author DaPorkchop_
 */
@SuppressWarnings("unchecked")
public abstract class PacketRegistry<SoupplyPacket extends Packet> {
    private final Map<Class<SoupplyPacket>, Supplier<SoupplyPacket>> packetConstructors = new Hashtable<>();
    private final Class<SoupplyPacket>[] packetClasses = (Class<SoupplyPacket>[]) new Class[256];
    private final TObjectIntMap<Class<SoupplyPacket>> packetIds = new TObjectIntHashMap<>();
    private final ObjectToIntegerFunction<SoupplyPacket> idGetter;

    //TODO: translators and decoders
    public PacketRegistry(@NonNull ObjectToIntegerFunction<SoupplyPacket> idGetter) {
        this.idGetter = idGetter;
        Registry registry = new Registry(this.packetConstructors);
        this.registerPackets(registry);
        this.packetConstructors.forEach((clazz, supplier) -> {
            SoupplyPacket instance = supplier.get();
            int id = idGetter.apply(instance);
            if (id > 255 || id < 0) {
                throw new IllegalStateException(String.format("Invalid packet id: %d", id));
            }
            PacketRegistry.this.packetClasses[id] = clazz;
            PacketRegistry.this.packetIds.put(clazz, id);
        });
    }

    protected abstract void registerPackets(@NonNull Registry registry);

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    protected final class Registry {
        @NonNull
        private final Map<Class<SoupplyPacket>, Supplier<SoupplyPacket>> packetConstructors;

        public void register(@NonNull Class<SoupplyPacket> clazz) {
            try {
                Constructor<SoupplyPacket> constructor = clazz.getDeclaredConstructor();
                this.register(clazz, () -> {
                    try {
                        return constructor.newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public void register(@NonNull Supplier<SoupplyPacket> supplier) {
            this.register((Class<SoupplyPacket>) supplier.get().getClass(), supplier);
        }

        public void register(@NonNull Class<SoupplyPacket> clazz, @NonNull Supplier<SoupplyPacket> supplier) {
            if (this.packetConstructors.containsKey(clazz)) {
                throw new IllegalStateException(String.format("Packet %s already registered", clazz.getCanonicalName()));
            }
            this.packetConstructors.put(clazz, supplier);
        }

        public void registerAll(@NonNull Class<SoupplyPacket>... clazzez) {
            for (Class<SoupplyPacket> clazz : clazzez) {
                this.register(clazz);
            }
        }

        public void registerAll(@NonNull Supplier<SoupplyPacket>... suppliers) {
            for (Supplier<SoupplyPacket> supplier : suppliers) {
                this.register(supplier);
            }
        }
    }
}
