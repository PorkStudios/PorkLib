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

package net.daporkchop.lib.network.packet;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.KryoSerialization;
import lombok.NonNull;
import net.daporkchop.lib.network.conn.PorkConnection;
import net.daporkchop.lib.network.endpoint.Endpoint;

import java.nio.ByteBuffer;

/**
 * @author DaPorkchop_
 */
//TODO: see if i can remove this
public class KryoSerializationWrapper extends KryoSerialization {
    @NonNull
    private final Endpoint endpoint;

    private ByteBuffer writeBuffer;

    public KryoSerializationWrapper(@NonNull Endpoint endpoint) {
        super();

        this.endpoint = endpoint;
    }

    @Override
    public synchronized void write(Connection connection, ByteBuffer buffer, Object object) {
        PorkConnection porkConnection = (PorkConnection) connection;
        if (this.writeBuffer == null)   {
            this.writeBuffer = ByteBuffer.allocate(buffer.capacity());
        }
        this.writeBuffer.clear();
        int pos = buffer.position();
        this.writeBuffer.position(pos);
        super.write(connection, this.writeBuffer, object);
        byte[] b = new byte[this.writeBuffer.position() - pos];
        buffer.put(porkConnection.getCryptHelper().encrypt(b));
    }

    @Override
    public synchronized Object read(Connection connection, ByteBuffer buffer) {
        PorkConnection porkConnection = (PorkConnection) connection;
        byte[] b = new byte[buffer.remaining()];
        buffer.get(b);
        return super.read(connection, ByteBuffer.wrap(porkConnection.getCryptHelper().decrypt(b)));
    }
}
