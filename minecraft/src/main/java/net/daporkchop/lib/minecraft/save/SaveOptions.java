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

package net.daporkchop.lib.minecraft.save;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.misc.Cloneable;
import net.daporkchop.lib.common.pool.array.ArrayAllocator;
import net.daporkchop.lib.minecraft.util.WriteAccess;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * Options used when opening a {@link Save}.
 *
 * @author DaPorkchop_
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true)
public final class SaveOptions implements Cloneable<SaveOptions.Builder> {
    private static final Map<String, Key<?>> KEY_LOOKUP = new ConcurrentHashMap<>();

    public static final SaveOptions DEFAULT = new SaveOptions(Collections.emptyMap());

    /**
     * @return a new {@link Builder}
     */
    public static Builder builder() {
        return new Builder(new HashMap<>());
    }

    /**
     * Gets a new {@link Key} with the given name and default value.
     *
     * @param name         the name of the {@link Key}
     * @param defaultValue the default value for the key
     * @param <T>          the key's value type
     * @return a new {@link Key} with the given name
     * @throws IllegalStateException if another key with the same name already exists
     */
    public static <T> Key<T> key(@NonNull String name, T defaultValue) {
        Key<T> key = new Key<>(name = name.intern(), null, defaultValue);
        checkState(KEY_LOOKUP.putIfAbsent(name, key) == null, "duplicate key name: %s", name);
        return key;
    }

    /**
     * Gets a new {@link Key} with the given name and default value factory.
     *
     * @param name         the name of the {@link Key}
     * @param defaultValue a {@link Supplier} for providing instances of the default value for the key
     * @param <T>          the key's value type
     * @return a new {@link Key} with the given name
     * @throws IllegalStateException if another key with the same name already exists
     */
    public static <T> Key<T> keyLazy(@NonNull String name, @NonNull Supplier<T> defaultValue) {
        Key<T> key = new Key<>(name = name.intern(), defaultValue, null);
        checkState(KEY_LOOKUP.putIfAbsent(name, key) == null, "duplicate key name: %s", name);
        return key;
    }

    /**
     * The write access level that the save will be opened with.
     * <p>
     * Defaults to {@link WriteAccess#WRITE_REQUIRED}.
     */
    public static final Key<WriteAccess> ACCESS = key("access", WriteAccess.WRITE_REQUIRED);

    /**
     * The {@link Executor} that will be used for executing async I/O operations.
     * <p>
     * Defaults to {@link ForkJoinPool#commonPool()}.
     */
    public static final Key<Executor> IO_EXECUTOR = keyLazy("executor_io", ForkJoinPool::commonPool);

    /**
     * The {@link ByteBufAllocator} used for allocating Netty {@link io.netty.buffer.ByteBuf}s.
     * <p>
     * Defaults to {@link PooledByteBufAllocator#DEFAULT}
     */
    public static final Key<ByteBufAllocator> NETTY_ALLOC = keyLazy("alloc_netty", () -> PooledByteBufAllocator.DEFAULT);

    /**
     * The {@link ArrayAllocator} used for allocating {@code byte[]}s.
     * <p>
     * If {@code null}, {@code byte[]}s will be allocated using {@code new}.
     */
    public static final Key<ArrayAllocator<byte[]>> BYTE_ALLOC = key("alloc_byte", null);

    /**
     * The {@link ArrayAllocator} used for allocating {@code int[]}s.
     * <p>
     * If {@code null}, {@code int[]}s will be allocated using {@code new}.
     */
    public static final Key<ArrayAllocator<int[]>> INT_ALLOC = key("alloc_int", null);

    /**
     * The {@link ArrayAllocator} used for allocating {@code long[]}s.
     * <p>
     * If {@code null}, {@code long[]}s will be allocated using {@code new}.
     */
    public static final Key<ArrayAllocator<long[]>> LONG_ALLOC = key("alloc_long", null);

    @NonNull
    private final Map<Key<?>, ?> map;

    private SaveOptions(@NonNull Builder builder) {
        this.map = new HashMap<>(builder.map);
    }

    /**
     * Gets the value mapped to the given {@link Key}.
     * <p>
     * If the given key does not exist, returns the key's default value.
     *
     * @param key the key
     * @param <T> the key's value type
     * @return the value for the given {@link Key}
     */
    public <T> T get(@NonNull Key<T> key) {
        T value = uncheckedCast(this.map.get(key));
        return value != null ? value : key.defaultValue();
    }

    @Override
    public Builder clone() {
        return new Builder(new HashMap<>(this.map));
    }

    /**
     * A key, used by {@link SaveOptions}.
     *
     * @param <T> the type of value stored by this option key
     * @author DaPorkchop_
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public static final class Key<T> implements Comparable<Key<?>> {
        @NonNull
        private final String name;
        private Supplier<T> defaultValueFactory;
        private T defaultValue;
        private final int hashCode = ThreadLocalRandom.current().nextInt();

        @Override
        public int compareTo(Key<?> o) {
            return this.name.compareTo(o.name);
        }

        private T defaultValue() {
            Supplier<T> defaultValueFactory = this.defaultValueFactory;
            if (defaultValueFactory != null) { //not quite atomic, but it's unlikely to be super critical
                this.defaultValue = defaultValueFactory.get();
                this.defaultValueFactory = null;
            }
            return this.defaultValue;
        }
    }

    /**
     * A builder for {@link SaveOptions}.
     *
     * @author DaPorkchop_
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Builder implements Cloneable<Builder> {
        @NonNull
        private final Map<Key<?>, ?> map;

        /**
         * Sets the given key to the given value.
         *
         * @param key   the key
         * @param value the value
         * @param <T>   the key's value type
         * @return this builder
         */
        public <T> Builder set(@NonNull Key<T> key, T value) {
            this.map.put(key, uncheckedCast(value));
            return this;
        }

        @Override
        public Builder clone() {
            return new Builder(new HashMap<>(this.map));
        }
    }
}
