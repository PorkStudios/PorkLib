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
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.lib.db.PorkDB;
import net.daporkchop.lib.network.endpoint.builder.ClientBuilder;
import net.daporkchop.lib.network.endpoint.client.Client;
import net.daporkchop.lib.network.protocol.netty.sctp.SctpProtocolManager;

import java.net.InetSocketAddress;

/**
 * An implementation of {@link PorkDB} which runs on a remote machine
 *
 * @author DaPorkchop_
 */
public class RemoteDB extends PorkDB {
    @Getter
    private final InetSocketAddress remoteAddress;
    @Getter
    private final Client netClient;

    protected RemoteDB(@NonNull Builder builder) {
        super(builder);

        if (builder.remoteAddress == null) {
            throw new IllegalArgumentException("Remote address must be set!");
        } else {
            this.remoteAddress = builder.remoteAddress;
        }
        this.netClient = new ClientBuilder()
                .setAddress(this.remoteAddress)
                .addProtocol(new RemoteDBProtocol(this))
                .setManager(SctpProtocolManager.INSTANCE)
                .build();
    }

    @Override
    public boolean isRemote() {
        return true;
    }

    @RequiredArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    @Getter
    @Setter
    public static class Builder extends PorkDB.Builder<Builder, RemoteDB>   {
        /**
         * The remote address of this database.
         * <p>
         * If this is a local database, this will be {@code null}
         */
        @NonNull
        private InetSocketAddress remoteAddress;

        @Override
        public RemoteDB build() {
            if (this.remoteAddress == null) {
                throw new IllegalStateException("remote address must be set!");
            }

            return new RemoteDB(this);
        }
    }
}
