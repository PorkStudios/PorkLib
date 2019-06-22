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

package net.daporkchop.lib.network.endpoint;

import io.netty.util.concurrent.Future;
import net.daporkchop.lib.concurrent.future.Promise;
import net.daporkchop.lib.logging.Logger;
import net.daporkchop.lib.network.EndpointType;
import net.daporkchop.lib.network.session.AbstractUserSession;
import net.daporkchop.lib.network.session.BaseUserSession;
import net.daporkchop.lib.network.session.PSession;

/**
 * A client can connect to a single remote endpoint.
 *
 * @author DaPorkchop_
 */
public interface PClient<S extends AbstractUserSession<S>> extends PEndpoint<PClient<S>, S>, BaseUserSession<PClient<S>, S> {
    @Override
    default EndpointType type() {
        return EndpointType.CLIENT;
    }

    @Override
    default void closeNow() {
        BaseUserSession.super.closeNow();
    }

    @Override
    default Promise closeAsync() {
        return BaseUserSession.super.closeAsync();
    }

    @Override
    default Logger logger() {
        return BaseUserSession.super.logger();
    }

    /**
     * Gets this session's user session instance.
     * @return this session's user session instance
     */
    S userSession();
}
