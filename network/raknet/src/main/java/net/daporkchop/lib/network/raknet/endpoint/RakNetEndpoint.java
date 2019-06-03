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

package net.daporkchop.lib.network.raknet.endpoint;

import com.nukkitx.network.raknet.RakNet;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.network.endpoint.PEndpoint;
import net.daporkchop.lib.network.endpoint.builder.EndpointBuilder;
import net.daporkchop.lib.network.protocol.Protocol;
import net.daporkchop.lib.network.raknet.RakNetEngine;
import net.daporkchop.lib.network.session.AbstractUserSession;

/**
 * @author DaPorkchop_
 */
@Getter
@Accessors(fluent = true)
public abstract class RakNetEndpoint<Impl extends PEndpoint<Impl, S>, S extends AbstractUserSession<S>, R extends RakNet> implements PEndpoint<Impl, S> {
    protected final RakNetEngine transportEngine;
    protected final EventLoopGroup group;
    protected final Protocol<S> protocol;
    protected R rakNet;

    public RakNetEndpoint(@NonNull EndpointBuilder<?, ?, S> builder)    {
        this.transportEngine = (RakNetEngine) builder.engine();
        this.group = this.transportEngine.useGroup();
        this.protocol = builder.protocol();
    }

    @Override
    public boolean isClosed() {
        return !this.rakNet.isRunning();
    }

    @Override
    public void closeNow() {
        this.rakNet.close();
    }

    @Override
    public Future<Void> closeAsync() {
        return this.group.submit(() -> {
            this.rakNet.close();
            return null;
        });
    }
}
