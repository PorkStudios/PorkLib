/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.binary.serialization;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.serialization.impl.BasicSerializer;
import net.daporkchop.lib.binary.serialization.impl.ByteArraySerializer;
import net.daporkchop.lib.binary.serialization.impl.ConstantLengthSerializer;
import net.daporkchop.lib.binary.serialization.impl.StringSerializer;
import net.daporkchop.lib.binary.serialization.impl.UUIDSerializer;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Maps {@link Serializer}s to ids.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class SerializationRegistry {
    /**
     * A default global serialization registry.
     * <p>
     * Uses varInts for ids.
     */
    public static final SerializationRegistry DEFAULT = new SerializationRegistry(true)
            .register(String.class, StringSerializer.INSTANCE)
            .register(byte[].class, ByteArraySerializer.INSTANCE)
            .register(UUID.class, UUIDSerializer.INSTANCE)
            .register(Boolean.class, ConstantLengthSerializer.BOOLEAN)
            .register(Byte.class, ConstantLengthSerializer.BYTE)
            .register(Short.class, ConstantLengthSerializer.SHORT)
            .register(Integer.class, ConstantLengthSerializer.INT)
            .register(Long.class, ConstantLengthSerializer.LONG)
            .register(Float.class, ConstantLengthSerializer.FLOAT)
            .register(Double.class, ConstantLengthSerializer.DOUBLE);

    private final Map<Class<?>, Integer> classToId = new IdentityHashMap<>();
    private final Map<Class<?>, Serializer<?>> classToSerializer = new IdentityHashMap<>();
    private final Map<Integer, Serializer<?>> idToSerializer = new HashMap<>();
    private final BitSet ids = new BitSet();
    private final ReadWriteLock mapAccessLock = new ReentrantReadWriteLock();

    @Getter
    private final boolean useVarInt;

    /**
     * Registers a {@link Serializable} to the registry
     *
     * @param clazz the serializable class to register
     * @param <T>   the type
     * @return this {@link SerializationRegistry} instance
     */
    public <T extends Serializable> SerializationRegistry register(@NonNull Class<T> clazz) {
        this.mapAccessLock.writeLock().lock();
        try {
            return this.register(clazz, this.ids.nextClearBit(0));
        } finally {
            this.mapAccessLock.writeLock().unlock();
        }
    }

    /**
     * Registers a {@link Serializable} to the registry with a specific id
     *
     * @param clazz the serializable class to register
     * @param id    the id to register it with
     * @param <T>   the type
     * @return this {@link SerializationRegistry} instance
     */
    public <T extends Serializable> SerializationRegistry register(@NonNull Class<T> clazz, int id) {
        this.mapAccessLock.writeLock().lock();
        try {
            return this.register(clazz, BasicSerializer.getInstance(), id);
        } finally {
            this.mapAccessLock.writeLock().unlock();
        }
    }

    /**
     * Registers a type to the registry
     *
     * @param clazz      the class to register
     * @param serializer the serializer to use for reading and writing values
     * @param <T>        the type
     * @return this {@link SerializationRegistry} instance
     */
    public <T> SerializationRegistry register(@NonNull Class<T> clazz, @NonNull Serializer<T> serializer) {
        this.mapAccessLock.writeLock().lock();
        try {
            return this.register(clazz, serializer, this.ids.nextClearBit(0));
        } finally {
            this.mapAccessLock.writeLock().unlock();
        }
    }

    /**
     * Registers a type to the registry with a specific id
     *
     * @param clazz      the class to register
     * @param serializer the serializer to use for reading and writing values
     * @param id         the id to register it with
     * @param <T>        the type
     * @return this {@link SerializationRegistry} instance
     */
    public <T> SerializationRegistry register(@NonNull Class<T> clazz, @NonNull Serializer<T> serializer, int id) {
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

    /**
     * Reads an object
     *
     * @param in  an {@link InputStream} to read data from
     * @param <T> the type of the object to be read
     * @return the object that was read
     * @throws IOException if an IO exception occurs you dummy
     */
    public <T> T read(@NonNull InputStream in) throws IOException {
        return this.read(DataIn.wrap(in));
    }

    /**
     * Reads an object
     *
     * @param in  a {@link File} to read data from
     * @param <T> the type of the object to be read
     * @return the object that was read
     * @throws IOException if an IO exception occurs you dummy
     */
    public <T> T read(@NonNull File in) throws IOException {
        return this.read(DataIn.wrap(in));
    }

    /**
     * Reads an object
     *
     * @param in  a {@link ByteBuffer} to read data from
     * @param <T> the type of the object to be read
     * @return the object that was read
     */
    public <T> T read(@NonNull ByteBuffer in) {
        try {
            return this.read(DataIn.wrap(in));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads an object
     *
     * @param in  a {@link ByteBuf} to read data from
     * @param <T> the type of the object to be read
     * @return the object that was read
     */
    public <T> T readNetty(@NonNull ByteBuf in) {
        try {
            return this.read(DataIn.wrap(in));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads an object
     *
     * @param in  a {@link DataIn} to read data from
     * @param <T> the type of the object to be read
     * @return the object that was read
     */
    public <T> T read(@NonNull DataIn in) throws IOException {
        return this.read(in, this.useVarInt ? in.readVarInt() : in.readInt());
    }

    /**
     * Reads an object with a specific id
     *
     * @param in  a {@link DataIn} to read data from
     * @param id  the id of the object type to be read
     * @param <T> the type of the object to be read
     * @return the object that was read
     */
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

    /**
     * Writes an object
     *
     * @param value the value to write
     * @param out   an {@link OutputStream} to write data to
     * @throws IOException if an IO exception occurs you dummy
     */
    public void write(@NonNull Object value, @NonNull OutputStream out) throws IOException {
        this.write(value, DataOut.wrap(out));
    }

    /**
     * Writes an object
     *
     * @param value the value to write
     * @param out   a {@link File} to write data to
     * @throws IOException if an IO exception occurs you dummy
     */
    public void write(@NonNull Object value, @NonNull File out) throws IOException {
        this.write(value, DataOut.wrap(out));
    }

    /**
     * Writes an object
     *
     * @param value the value to write
     * @param out   a {@link ByteBuffer} to write data to
     */
    public void write(@NonNull Object value, @NonNull ByteBuffer out) {
        try {
            this.write(value, DataOut.wrap(out));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes an object
     *
     * @param value the value to write
     * @param out   a {@link ByteBuf} to write data to
     */
    public void writeNetty(@NonNull Object value, @NonNull ByteBuf out) {
        try {
            this.write(value, DataOut.wrap(out));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes an object
     *
     * @param value the value to write
     * @param out   a {@link DataOut} to write data to
     * @throws IOException if an IO exception occurs you dummy
     */
    @SuppressWarnings("unchecked")
    public void write(@NonNull Object value, @NonNull DataOut out) throws IOException {
        Serializer serializer;
        int id;
        this.mapAccessLock.readLock().lock();
        try {
            serializer = this.classToSerializer.get(value.getClass());
            id = this.classToId.get(value.getClass());
        } finally {
            this.mapAccessLock.readLock().unlock();
        }

        if (serializer == null) {
            throw new IllegalArgumentException(String.format("Unregistered class: %s", value.getClass().getCanonicalName()));
        } else {
            out.writeVarInt(id);
            serializer.write(value, out);
        }
    }
}
