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
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.crypto.sig.ec.CurveType;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class KeySerialization {
    public static void encodeEC(@NonNull DataOut out, @NonNull EllipticCurveKeyPair keyPair) throws IOException {
        encodeEC(out, keyPair, true, true);
    }

    public static void encodeEC(@NonNull DataOut out, @NonNull EllipticCurveKeyPair keyPair, boolean pubKey, boolean privKey) throws IOException {
        if (!(pubKey | privKey)) {
            throw new IllegalArgumentException("Must encode either public key, private key or both!");
        }
        out.writeUTF(keyPair.getCurveType().name());
        try (ObjectOutputStream oos = new ObjectOutputStream(DataOut.wrapNonClosing(out))) {
            if (privKey) {
                oos.writeObject(keyPair.getPrivateKey());
            }
            if (pubKey) {
                oos.writeObject(keyPair.getPublicKey());
            }
        }
    }

    public static EllipticCurveKeyPair decodeEC(@NonNull DataIn in) throws IOException {
        return decodeEC(in, true, true);
    }

    public static EllipticCurveKeyPair decodeEC(@NonNull DataIn in, boolean pubKey, boolean privKey) throws IOException {
        if (!(pubKey | privKey)) {
            throw new IllegalArgumentException("Must encode either public key, private key or both!");
        }
        CurveType type = CurveType.valueOf(in.readUTF());
        ObjectInputStream ois = new ObjectInputStream(in.asStream());
        try {
            if (pubKey && privKey) {
                return new EllipticCurveKeyPair(type, (BCECPrivateKey) ois.readObject(), (BCECPublicKey) ois.readObject());
            } else if (pubKey) {
                return new EllipticCurveKeyPair(type, (BCECPublicKey) ois.readObject());
            } else {
                return new EllipticCurveKeyPair(type, (BCECPrivateKey) ois.readObject());
            }
        } catch (ClassNotFoundException e)  {
            throw new IOException(e);
        }
    }
}
