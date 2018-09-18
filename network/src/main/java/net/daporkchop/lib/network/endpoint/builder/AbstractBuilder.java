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

package net.daporkchop.lib.network.endpoint.builder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.network.endpoint.EndpointListener;
import net.daporkchop.lib.network.packet.protocol.PacketProtocol;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

/**
 * @author DaPorkchop_
 */
@Accessors(chain = true)
@Getter
@Setter
public abstract class AbstractBuilder<T> {
    private final Set<EndpointListener> listeners = new HashSet<>();
    @NonNull
    private InetSocketAddress address;
    @NonNull
    private PacketProtocol protocol;

    public final T build()  {
        if (this.address == null)   {
            throw new NullPointerException("address");
        } else if (this.protocol == null)   {
            throw new NullPointerException("protocol");
        }
        return this.doBuild();
    }

    protected abstract T doBuild();

    public void addListener(@NonNull EndpointListener... listeners) {
        for (EndpointListener listener : listeners) {
            if (listener == null) {
                throw new NullPointerException("listener");
            }
            this.listeners.add(listener);
        }
    }
}
