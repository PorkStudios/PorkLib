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

package net.daporkchop.lib.crypto.key.ec;

import net.daporkchop.lib.crypto.key.serializer.impl.ECKeySerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.PrivateKey;
import java.security.PublicKey;

public abstract class AbstractECKeyPair {
    protected final PrivateKey privateKey;
    protected final PublicKey publicKey;

    protected AbstractECKeyPair(PrivateKey privateKey, PublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public static <T extends AbstractECKeyPair> T fromFullEncoding(byte[] data) {
        return ECKeySerializer.INSTANCE.deserialize(data);
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractECKeyPair> T decodePublic(byte[] encoded) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(encoded);
            ObjectInputStream ois = new ObjectInputStream(bais);
            String className = (String) ois.readObject();
            Class<?> clazz = Class.forName(className);
            Constructor<?> constructor = clazz.getConstructor(PrivateKey.class, PublicKey.class);
            return (T) constructor.newInstance(null, (PublicKey) ois.readObject());
        } catch (IOException
                | ClassNotFoundException
                | NoSuchMethodException
                | InstantiationException
                | IllegalAccessException
                | InvocationTargetException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractECKeyPair> T decodePrivate(byte[] encoded) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(encoded);
            ObjectInputStream ois = new ObjectInputStream(bais);
            String className = (String) ois.readObject();
            Class<?> clazz = Class.forName(className);
            Constructor<?> constructor = clazz.getConstructor(PrivateKey.class, PublicKey.class);
            return (T) constructor.newInstance((PrivateKey) ois.readObject(), null);
        } catch (IOException
                | ClassNotFoundException
                | NoSuchMethodException
                | InstantiationException
                | IllegalAccessException
                | InvocationTargetException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public byte[] encodeFull() {
        return ECKeySerializer.INSTANCE.serialize(this);
    }

    public byte[] encodePublic() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this.getClass().getCanonicalName());
            oos.writeObject(this.publicKey);
            oos.close();
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public byte[] encodePrivate() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this.getClass().getCanonicalName());
            oos.writeObject(this.privateKey);
            oos.close();
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }
}
