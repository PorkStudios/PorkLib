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

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.KryoSerialization;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.ByteBufferInputStream;
import net.daporkchop.lib.binary.stream.ByteBufferOutputStream;
import net.daporkchop.lib.network.conn.PorkConnection;
import net.daporkchop.lib.network.endpoint.Endpoint;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;

/**
 * @author DaPorkchop_
 */
public class KryoSerializationWrapper extends KryoSerialization {
    @NonNull
    private final Endpoint endpoint;

    private final Kryo kryo;
    private final Output output = new Output(1024, -1);
    private final Input input = new Input(1024);

    public KryoSerializationWrapper(@NonNull Endpoint endpoint) {
        super();

        this.endpoint = endpoint;

        try {
            Field field = KryoSerialization.class.getDeclaredField("kryo");
            field.setAccessible(true);
            this.kryo = (Kryo) field.get(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized void write(Connection connection, ByteBuffer buffer, Object object) {
        try {
            //System.out.printf("Writing %s...\n", object.getClass().getCanonicalName());
            PorkConnection porkConnection = (PorkConnection) connection;
            OutputStream o = porkConnection.getPacketReprocessor().encrypt(new ByteBufferOutputStream(buffer), porkConnection.getState());
            this.output.setOutputStream(o);
            this.kryo.getContext().put("connection", connection);
            this.kryo.writeClassAndObject(this.output, object);
            this.output.flush();
            o.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized Object read(Connection connection, ByteBuffer buffer) {
        try {
            PorkConnection porkConnection = (PorkConnection) connection;
            InputStream i = porkConnection.getPacketReprocessor().decrypt(new ByteBufferInputStream(buffer), porkConnection.getState());
            this.input.setInputStream(i);
            this.kryo.getContext().put("connection", connection);
            Object object = this.kryo.readClassAndObject(this.input);
            //System.out.printf("Read %s!\n", object.getClass().getCanonicalName());
            i.close();
            return object;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
