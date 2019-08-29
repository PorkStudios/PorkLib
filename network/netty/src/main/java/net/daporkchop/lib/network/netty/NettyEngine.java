/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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

package net.daporkchop.lib.network.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.binary.netty.NettyUtil;
import net.daporkchop.lib.binary.stream.OldDataIn;
import net.daporkchop.lib.network.transport.TransportEngine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public abstract class NettyEngine implements TransportEngine {
    protected final Map<ChannelOption, Object> clientOptions;
    protected final Map<ChannelOption, Object> serverOptions;

    protected final NioEventLoopGroup group;
    protected final boolean autoShutdownGroup;

    protected final ByteBufAllocator alloc;

    public NettyEngine(@NonNull Builder<? extends Builder, ?> builder) {
        builder.validate();

        this.clientOptions = Collections.unmodifiableMap(builder.clientOptions);
        this.serverOptions = Collections.unmodifiableMap(builder.serverOptions);

        this.group = builder.group;
        this.autoShutdownGroup = builder.autoShutdownGroup;

        this.alloc = builder.alloc;
    }

    @Override
    public boolean isBinary(@NonNull Object msg) {
        return msg instanceof ByteBuf;
    }

    @Override
    public OldDataIn attemptRead(@NonNull Object msg) {
        return msg instanceof ByteBuf ? NettyUtil.wrapIn((ByteBuf) msg) : null;
    }

    @Getter
    @Accessors(fluent = true)
    public static abstract class Builder<Impl extends Builder<Impl, R>, R extends NettyEngine> {
        protected final Map<ChannelOption, Object> clientOptions = new HashMap<>();
        protected final Map<ChannelOption, Object> serverOptions = new HashMap<>();

        /**
         * The {@link NioEventLoopGroup} use.
         * <p>
         * If {@code null}, a default one will be used.
         */
        protected NioEventLoopGroup group;

        /**
         * Whether or not to automatically shut down the {@link #group} when endpoints are closed.
         * <p>
         * Groups **are** reference counted, but if you want to share one group across multiple engines you may want
         * to retain manual control over the state of the group.
         * <p>
         * If {@link #group} is {@code null}, this value is forcibly set to {@code true}.
         */
        protected boolean autoShutdownGroup = true;

        /**
         * The {@link ByteBufAllocator} to use.
         * <p>
         * If {@code null}, {@link PooledByteBufAllocator#DEFAULT} will be used.
         */
        protected ByteBufAllocator alloc;

        public <T> Impl option(@NonNull ChannelOption<T> option, T value) {
            return this.clientOption(option, value).serverOption(option, value);
        }

        @SuppressWarnings("unchecked")
        public <T> Impl clientOption(@NonNull ChannelOption<T> option, T value) {
            if (value == null) {
                this.clientOptions.remove(option);
            } else {
                this.clientOptions.put(option, value);
            }
            return (Impl) this;
        }

        @SuppressWarnings("unchecked")
        public <T> Impl serverOption(@NonNull ChannelOption<T> option, T value) {
            if (value == null) {
                this.serverOptions.remove(option);
            } else {
                this.serverOptions.put(option, value);
            }
            return (Impl) this;
        }

        @SuppressWarnings("unchecked")
        public Impl group(NioEventLoopGroup group) {
            this.group = group;
            return (Impl) this;
        }

        @SuppressWarnings("unchecked")
        public Impl autoShutdownGroup(boolean autoShutdownGroup) {
            this.autoShutdownGroup = autoShutdownGroup;
            return (Impl) this;
        }

        @SuppressWarnings("unchecked")
        public Impl alloc(ByteBufAllocator alloc) {
            this.alloc = alloc;
            return (Impl) this;
        }

        public final R build() {
            this.validate();
            return this.doBuild();
        }

        protected void validate() {
            if (this.group == null) {
                this.autoShutdownGroup = true;
            }
            if (this.alloc == null) {
                this.alloc = PooledByteBufAllocator.DEFAULT;
            }
        }

        protected abstract R doBuild();
    }
}
