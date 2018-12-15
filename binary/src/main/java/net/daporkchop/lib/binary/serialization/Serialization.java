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
 */

package net.daporkchop.lib.binary.serialization;

import com.zaxxer.sparsebits.SparseBitSet;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.netty.NettyByteBufUtil;
import net.daporkchop.lib.binary.serialization.impl.BasicSerializer;
import net.daporkchop.lib.binary.serialization.impl.ByteArraySerializer;
import net.daporkchop.lib.binary.serialization.impl.StringSerializer;
import net.daporkchop.lib.binary.serialization.impl.UUIDSerializer;
import net.daporkchop.lib.binary.stream.DataIn;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Maps {@link Serializer}s to ids and then does cool things with them
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class Serialization {
    /**
     * A default global serialization registry.
     * <p>
     * Uses varInts for ids.
     */
    public static final Serialization DEFAULT_REGISTRY = new Serialization(true)
            .register(String.class, StringSerializer.INSTANCE)
            .register(byte[].class, ByteArraySerializer.INSTANCE)
            .register(UUID.class, UUIDSerializer.INSTANCE);

    private final Map<Class<?>, Integer> classToId = new IdentityHashMap<>();
    private final Map<Class<?>, Serializer<?>> classToSerializer = new IdentityHashMap<>();
    private final Map<Integer, Serializer<?>> idToSerializer = new HashMap<>();
    private final SparseBitSet ids = new SparseBitSet();
    private final ReadWriteLock mapAccessLock = new ReentrantReadWriteLock();

    @Getter
    private final boolean useVarInt;

    public <T extends Serializable> Serialization register(@NonNull Class<T> clazz) {
        this.mapAccessLock.writeLock().lock();
        try {
            return this.register(clazz, this.ids.nextClearBit(0));
        } finally {
            this.mapAccessLock.writeLock().unlock();
        }
    }

    public <T extends Serializable> Serialization register(@NonNull Class<T> clazz, int id) {
        this.mapAccessLock.writeLock().lock();
        try {
            return this.register(clazz, BasicSerializer.getInstance(), id);
        } finally {
            this.mapAccessLock.writeLock().unlock();
        }
    }

    public <T> Serialization register(@NonNull Class<T> clazz, @NonNull Serializer<T> serializer) {
        this.mapAccessLock.writeLock().lock();
        try {
            return this.register(clazz, serializer, this.ids.nextClearBit(0));
        } finally {
            this.mapAccessLock.writeLock().unlock();
        }
    }

    public <T> Serialization register(@NonNull Class<T> clazz, @NonNull Serializer<T> serializer, int id) {
        this.mapAccessLock.writeLock().lock();
        try {
            if (this.ids.get(id)) {
                throw new IllegalArgumentException(String.format("ID %d already taken!", id));
            } else {
                this.ids.set(id);
                Integer idObj = id;
                this.classToId.put(clazz, idObj);
                this.classToSerializer.put(clazz, serializer);
                this.idToSerializer.put(idObj, serializer);
                return this;
            }
        } finally {
            this.mapAccessLock.writeLock().unlock();
        }
    }

    public <T> T read(@NonNull InputStream in) throws IOException {
        return this.read(DataIn.wrap(in));
    }

    public <T> T read(@NonNull File in) throws IOException {
        return this.read(DataIn.wrap(in));
    }

    public <T> T read(@NonNull ByteBuffer in) {
        try {
            return this.read(DataIn.wrap(in));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T read(@NonNull ByteBuf in) {
        try {
            return this.read(NettyByteBufUtil.wrapIn(in));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T read(@NonNull DataIn in) throws IOException {
        return this.read(in, this.useVarInt ? in.readVarInt(true) : in.readInt());
    }

    @SuppressWarnings("unchecked")
    public <T> T read(@NonNull DataIn in, int id) throws IOException {
        Serializer<T> serializer;
        this.mapAccessLock.readLock().lock();
        try {
            serializer = (Serializer<T>) this.idToSerializer.get(id);
        } finally {
            this.mapAccessLock.readLock().unlock();
        }

        if (serializer == null) {
            throw new IllegalArgumentException(String.format("Unregistered ID: %d", id));
        } else {
            return serializer.read(in);
        }
    }
}
