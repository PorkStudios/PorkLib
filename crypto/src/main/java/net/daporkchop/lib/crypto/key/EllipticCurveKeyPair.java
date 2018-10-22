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
