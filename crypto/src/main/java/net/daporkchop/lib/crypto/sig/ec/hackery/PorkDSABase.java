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

package net.daporkchop.lib.crypto.sig.ec.hackery;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.DSA;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.jcajce.provider.asymmetric.util.DSAEncoder;

import java.math.BigInteger;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.spec.AlgorithmParameterSpec;

public abstract class PorkDSABase extends SignatureSpi implements PKCSObjectIdentifiers, X509ObjectIdentifiers {
    public Digest digest;
    public DSA signer;
    public DSAEncoder encoder;

    public PorkDSABase(Digest digest, DSA signer, DSAEncoder encoder) {
        this.digest = digest;
        this.signer = signer;
        this.encoder = encoder;
    }

    public void engineUpdate(byte b) throws SignatureException {
        digest.update(b);
    }

    public void engineUpdate(byte[] b, int off, int len) throws SignatureException {
        digest.update(b, off, len);
    }

    public byte[] engineSign() throws SignatureException {
        byte[] hash = new byte[digest.getDigestSize()];

        digest.doFinal(hash, 0);

        try {
            BigInteger[] sig = signer.generateSignature(hash);

            return encoder.encode(sig[0], sig[1]);
        } catch (Exception e) {
            throw new SignatureException(e.toString());
        }
    }

    public boolean engineVerify(byte[] sigBytes) throws SignatureException {
        byte[] hash = new byte[digest.getDigestSize()];

        digest.doFinal(hash, 0);

        BigInteger[] sig;

        try {
            sig = encoder.decode(sigBytes);
        } catch (Exception e) {
            throw new SignatureException("error decoding signature bytes.");
        }

        return signer.verifySignature(hash, sig[0], sig[1]);
    }

    public void engineSetParameter(AlgorithmParameterSpec params) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    /**
     * @deprecated replaced with "#engineSetParameter(java.security.spec.AlgorithmParameterSpec)"
     */
    public void engineSetParameter(String param, Object value) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    /**
     * @deprecated
     */
    public Object engineGetParameter(String param) {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }
}
