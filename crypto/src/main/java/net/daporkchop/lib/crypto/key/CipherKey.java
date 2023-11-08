/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.lib.crypto.key;

import lombok.NonNull;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.lang.reflect.Field;

public class CipherKey extends ParametersWithIV implements Serializable {
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

    public CipherKey(SecretKey key, byte[] iv) {
        super(new KeyParameter(key == null ? new byte[0] : key.getEncoded()), iv);
    }

    public CipherKey(byte[] key, byte[] iv) {
        super(new KeyParameter(key), iv);
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
