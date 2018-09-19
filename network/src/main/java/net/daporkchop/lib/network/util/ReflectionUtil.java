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

package net.daporkchop.lib.network.util;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryonet.Connection;
import lombok.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author DaPorkchop_
 */
public class ReflectionUtil {
    private static final Field connection_tcp;
    private static Method tcpConnection_writeOperation;

    private static final Field input_capacity;

    static {
        try {
            connection_tcp = Connection.class.getDeclaredField("tcp");
            connection_tcp.setAccessible(true);

            input_capacity = Input.class.getDeclaredField("capacity");
            input_capacity.setAccessible(true);
        } catch (Exception e)   {
            throw new RuntimeException(e);
        }
    }

    public static void flush(@NonNull Connection connection)  {
        try {
            if (!connection.isConnected())  {
                return;
            }
            Object o = connection_tcp.get(connection);
            if (tcpConnection_writeOperation == null) {
                tcpConnection_writeOperation = o.getClass().getDeclaredMethod("writeOperation");
                tcpConnection_writeOperation.setAccessible(true);
            }
            tcpConnection_writeOperation.invoke(o);
        } catch (Exception e)   {
            throw new RuntimeException(e);
        }
    }

    public static void setCapacity(@NonNull Input input, int capacity)  {
        try {
            input_capacity.set(input, capacity);
        } catch (Exception e)   {
            throw new RuntimeException(e);
        }
    }
}
