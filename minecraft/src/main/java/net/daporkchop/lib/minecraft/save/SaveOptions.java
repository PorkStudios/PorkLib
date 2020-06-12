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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.misc.Cloneable;
import net.daporkchop.lib.common.pool.array.ArrayAllocator;
import net.daporkchop.lib.minecraft.util.WriteAccess;
import net.daporkchop.lib.unsafe.PUnsafe;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 * Options used when opening a {@link Save}.
 *
 * @author DaPorkchop_
 */
@NoArgsConstructor
@Getter
@Accessors(fluent = true)
public class SaveOptions implements Cloneable<SaveOptions> {
    @Getter(AccessLevel.NONE)
    protected boolean locked = false;

    /**
     * The write access level that the save will be opened with.
     * <p>
     * Defaults to {@link WriteAccess#WRITE_REQUIRED}.
     */
    protected WriteAccess access = WriteAccess.WRITE_REQUIRED;

    /**
     * The {@link Executor} that will be used for executing async I/O operations.
     * <p>
     * Defaults to {@link ForkJoinPool#commonPool()}.
     */
    protected Executor ioExecutor = ForkJoinPool.commonPool();

    /**
     * The {@link ByteBufAllocator} used for allocating Netty {@link io.netty.buffer.ByteBuf}s.
     * <p>
     * Defaults to {@link PooledByteBufAllocator#DEFAULT}
     */
    protected ByteBufAllocator nettyAlloc = PooledByteBufAllocator.DEFAULT;

    /**
     * The {@link ArrayAllocator} used for allocating {@code byte[]}s.
     * <p>
     * If {@code null}, {@code byte[]}s will be allocated using {@code new}.
     */
    protected ArrayAllocator<byte[]> byteAlloc;

    /**
     * The {@link ArrayAllocator} used for allocating {@code int[]}s.
     * <p>
     * If {@code null}, {@code int[]}s will be allocated using {@code new}.
     */
    protected ArrayAllocator<int[]> intAlloc;

    /**
     * The {@link ArrayAllocator} used for allocating {@code long[]}s.
     * <p>
     * If {@code null}, {@code long[]}s will be allocated using {@code new}.
     */
    protected ArrayAllocator<long[]> longAlloc;

    public SaveOptions(@NonNull SaveOptions other) {
        this.access = other.access;
        this.ioExecutor = other.ioExecutor;
        this.nettyAlloc = other.nettyAlloc;
        this.byteAlloc = other.byteAlloc;
        this.intAlloc = other.intAlloc;
        this.longAlloc = other.longAlloc;
    }

    public synchronized SaveOptions access(@NonNull WriteAccess access) {
        this.ensureNotLocked();
        this.access = access;
        return this;
    }

    public synchronized SaveOptions ioExecutor(@NonNull Executor ioExecutor) {
        this.ensureNotLocked();
        this.ioExecutor = ioExecutor;
        return this;
    }

    public synchronized SaveOptions nettyAlloc(@NonNull ByteBufAllocator nettyAlloc) {
        this.ensureNotLocked();
        this.nettyAlloc = nettyAlloc;
        return this;
    }

    public synchronized SaveOptions byteAlloc(ArrayAllocator<byte[]> byteAlloc) {
        this.ensureNotLocked();
        this.byteAlloc = byteAlloc;
        return this;
    }

    public synchronized SaveOptions intAlloc(ArrayAllocator<int[]> intAlloc) {
        this.ensureNotLocked();
        this.intAlloc = intAlloc;
        return this;
    }

    public synchronized SaveOptions longAlloc(ArrayAllocator<long[]> longAlloc) {
        this.ensureNotLocked();
        this.longAlloc = longAlloc;
        return this;
    }

    @Override
    public SaveOptions clone() {
        return new SaveOptions(this);
    }

    /**
     * Locks this {@link SaveOptions} instance, ensuring that the values cannot be modified any further.
     *
     * @return this instance
     */
    public synchronized SaveOptions lock() {
        this.ensureNotLocked();
        this.validate();
        this.locked = false;
        return this;
    }

    /**
     * Ensures that the currently configured options are valid.
     */
    protected void validate() {
    }

    protected void ensureNotLocked() {
        if (this.locked) {
            throw new IllegalStateException("locked");
        }
    }
}
