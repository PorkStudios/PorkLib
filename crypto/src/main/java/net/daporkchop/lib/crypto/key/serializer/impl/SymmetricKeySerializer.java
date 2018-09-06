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

package net.daporkchop.lib.crypto.key.serializer.impl;

import net.daporkchop.lib.crypto.key.serializer.AbstractKeySerializer;
import net.daporkchop.lib.crypto.key.symmetric.AbstractSymmetricKey;
import org.bouncycastle.crypto.params.KeyParameter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SymmetricKeySerializer extends AbstractKeySerializer<AbstractSymmetricKey> {
    public static final SymmetricKeySerializer INSTANCE = new SymmetricKeySerializer();

    private SymmetricKeySerializer() {
    }

    @Override
    protected void doSerialize(AbstractSymmetricKey key, OutputStream baos) throws IOException {
        DataOutput output = new DataOutputStream(baos);
        output.writeUTF(key.getClass().getCanonicalName());
        byte[] iv = key.getIV();
        output.writeInt(iv.length);
        output.write(iv);
        KeyParameter parameter = (KeyParameter) key.getParameters();
        byte[] keyBytes = parameter.getKey();
        output.writeInt(keyBytes.length);
        output.write(keyBytes);

    }

    @Override
    protected AbstractSymmetricKey doDeserialize(InputStream bais) throws IOException {
        DataInput input = new DataInputStream(bais);
        Constructor<?> constructor;

        {
            try {
                Class<?> clazz = Class.forName(input.readUTF());
                constructor = clazz.getConstructor(SecretKey.class, byte[].class);
                constructor.setAccessible(true);
            } catch (ClassNotFoundException
                    | NoSuchMethodException e) {
                e.printStackTrace();
                throw new IllegalStateException(e);
            }
        }

        byte[] iv = new byte[input.readInt()];
        input.readFully(iv);

        byte[] keyBytes = new byte[input.readInt()];
        input.readFully(keyBytes);
        {
            try {
                return (AbstractSymmetricKey) constructor.newInstance(new SecretKeySpec(keyBytes, "jef"), iv);
            } catch (InstantiationException
                    | IllegalAccessException
                    | InvocationTargetException e) {
                e.printStackTrace();
                throw new IllegalStateException(e);
            }
        }
    }
}
