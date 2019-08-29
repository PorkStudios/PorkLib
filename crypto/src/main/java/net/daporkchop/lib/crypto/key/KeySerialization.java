/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2019 DaPorkchop_ and contributors
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

package net.daporkchop.lib.crypto.key;

import lombok.NonNull;
import net.daporkchop.lib.binary.stream.OldDataIn;
import net.daporkchop.lib.binary.stream.OldDataOut;
import net.daporkchop.lib.crypto.sig.ec.CurveType;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class KeySerialization {
    public static void encodeEC(@NonNull OldDataOut out, @NonNull EllipticCurveKeyPair keyPair) throws IOException {
        encodeEC(out, keyPair, true, true);
    }

    public static void encodeEC(@NonNull OldDataOut out, @NonNull EllipticCurveKeyPair keyPair, boolean pubKey, boolean privKey) throws IOException {
        if (!(pubKey | privKey)) {
            throw new IllegalArgumentException("Must encode either public key, private key or both!");
        }
        out.writeUTF(keyPair.getCurveType().name());
        try (ObjectOutputStream oos = new ObjectOutputStream(OldDataOut.wrapNonClosing(out))) {
            if (privKey) {
                oos.writeObject(keyPair.getPrivateKey());
            }
            if (pubKey) {
                oos.writeObject(keyPair.getPublicKey());
            }
        }
    }

    public static EllipticCurveKeyPair decodeEC(@NonNull OldDataIn in) throws IOException {
        return decodeEC(in, true, true);
    }

    public static EllipticCurveKeyPair decodeEC(@NonNull OldDataIn in, boolean pubKey, boolean privKey) throws IOException {
        if (!(pubKey | privKey)) {
            throw new IllegalArgumentException("Must encode either public key, private key or both!");
        }
        CurveType type = CurveType.valueOf(in.readUTF());
        try (ObjectInputStream ois = new ObjectInputStream(OldDataIn.wrapNonClosing(in))) {
            if (pubKey && privKey) {
                return new EllipticCurveKeyPair(type, (BCECPrivateKey) ois.readObject(), (BCECPublicKey) ois.readObject());
            } else if (pubKey) {
                return new EllipticCurveKeyPair(type, (BCECPublicKey) ois.readObject());
            } else {
                return new EllipticCurveKeyPair(type, (BCECPrivateKey) ois.readObject());
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
