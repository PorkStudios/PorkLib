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

package net.daporkchop.lib.crypto.key.symmetric;

import lombok.NonNull;
import net.daporkchop.lib.crypto.key.serializer.impl.SymmetricKeySerializer;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.lang.reflect.Field;

public abstract class AbstractSymmetricKey extends ParametersWithIV implements Serializable {
    private static Field ivField;
    private static Field keyField;

    static {
        try {
            ivField = ParametersWithIV.class.getDeclaredField("iv");
            ivField.setAccessible(true);

            keyField = KeyParameter.class.getDeclaredField("key");
            keyField.setAccessible(true);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IllegalStateException(t);
        }
    }

    public AbstractSymmetricKey(SecretKey key, byte[] iv) {
        super(new KeyParameter(key == null ? new byte[0] : key.getEncoded()), iv);
    }

    public static AbstractSymmetricKey decodeFull(byte[] data) {
        return SymmetricKeySerializer.INSTANCE.deserialize(data);
    }

    public byte[] encodeFull() {
        return SymmetricKeySerializer.INSTANCE.serialize(this);
    }

    public void setIV(@NonNull byte[] iv) {
        if (iv.length != this.getIV().length) {
            throw new IllegalArgumentException("Input IV has invalid length (" + iv.length + ", current is " + this.getIV().length + ')');
        }

        try {
            ivField.set(this, iv);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IllegalStateException(t);
        }
    }

    public byte[] getKey() {
        return ((KeyParameter) this.getParameters()).getKey();
    }

    public void setKey(@NonNull byte[] key) {
        if (key.length != this.getKey().length) {
            throw new IllegalArgumentException("Input key has invalid length (" + key.length + ", current is " + this.getKey().length + ')');
        }

        try {
            keyField.set(this.getParameters(), key);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IllegalStateException(t);
        }
    }
}
