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

package net.daporkchop.lib.db.container.atomiclong;

import net.daporkchop.lib.db.Container;
import net.daporkchop.lib.db.container.atomiclong.remote.PacketDBAtomicLong;
import net.daporkchop.lib.db.remote.AbstractRemoteContainer;
import net.daporkchop.lib.db.remote.RemoteDB;
import net.daporkchop.lib.network.packet.Codec;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author DaPorkchop_
 */
public class RemoteDBAtomicLong extends AbstractRemoteContainer<AtomicLong, RemoteDBAtomicLong.Builder> implements DBAtomicLong<RemoteDBAtomicLong, RemoteDBAtomicLong.Builder, RemoteDB> {
    private static final Collection<Class<? extends Codec>> PACKETS = Arrays.asList(
            PacketDBAtomicLong.DBAtomicLongCodec.class
    );

    public RemoteDBAtomicLong(Builder builder) throws IOException {
        super(builder);
    }

    @Override
    public AtomicLong getValue() {
        return new AtomicLong(this.get());
    }

    public static class Builder extends AbstractRemoteContainer.Builder<AtomicLong, RemoteDBAtomicLong> {
        protected Builder(RemoteDB db, String name) {
            super(db, name);
        }

        @Override
        protected RemoteDBAtomicLong buildImpl() throws IOException {
            return new RemoteDBAtomicLong(this);
        }
    }
}
