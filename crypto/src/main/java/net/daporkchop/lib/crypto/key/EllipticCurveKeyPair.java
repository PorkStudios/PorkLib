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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.crypto.sig.ec.CurveType;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;

@Getter
@AllArgsConstructor
public class EllipticCurveKeyPair {
    @NonNull
    private final CurveType curveType;

    @NonNull
    private final BCECPrivateKey privateKey;

    @NonNull
    private final BCECPublicKey publicKey;

    public EllipticCurveKeyPair(@NonNull CurveType curveType, @NonNull BCECPublicKey publicKey) {
        this.curveType = curveType;
        this.publicKey = publicKey;
        this.privateKey = null;
    }

    public EllipticCurveKeyPair(@NonNull CurveType curveType, @NonNull BCECPrivateKey privateKey) {
        this.curveType = curveType;
        this.publicKey = null;
        this.privateKey = privateKey;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EllipticCurveKeyPair)) {
            return false;
        }
        EllipticCurveKeyPair keyPair = (EllipticCurveKeyPair) obj;
        return this == keyPair ||
                (this.curveType == keyPair.curveType
                        && (this.privateKey == keyPair.privateKey || (this.privateKey != null && this.privateKey.equals(keyPair.privateKey)))
                        && (this.publicKey == keyPair.publicKey || (this.publicKey != null && this.publicKey.equals(keyPair.publicKey)))
                );
    }
}
