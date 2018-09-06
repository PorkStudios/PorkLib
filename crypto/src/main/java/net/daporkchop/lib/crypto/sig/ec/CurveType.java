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

package net.daporkchop.lib.crypto.sig.ec;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;

/**
 * A listing of all ECDSA curve types supported by Bouncycastle
 *
 * @author DaPorkchop_
 */
public enum CurveType {
    /*
     *   F p
     */

    //X9.62
    prime192v1("prime192v1", 192),
    prime192v2("prime192v2", 192),
    prime192v3("prime192v3", 192),
    prime239v1("prime239v1", 239),
    prime239v2("prime239v2", 239),
    prime239v3("prime239v3", 239),
    prime256v1("prime256v1", 256),
    //SEC
    secp192k1("secp192k1", 192),
    secp192r1("secp192r1", 192),
    secp224k1("secp224k1", 224),
    secp224r1("secp224r1", 224),
    secp256k1("secp256k1", 256),
    secp256r1("secp256r1", 256),
    secp384r1("secp384r1", 384),
    secp521r1("secp521r1", 521),
    //NIST
    P_224("P-224", 224),
    P_256("P-256", 256),
    P_384("P-384", 384),
    P_521("P-521", 521),

    /*
     *   F 2m
     */
    //X9.62
    c2pnb163v1("c2pnb163v1", 163),
    c2pnb163v2("c2pnb163v2", 163),
    c2pnb163v3("c2pnb163v3", 163),
    c2pnb176w1("c2pnb176w1", 176),
    c2tnb191v1("c2tnb191v1", 191),
    c2tnb191v2("c2tnb191v2", 191),
    c2tnb191v3("c2tnb191v3", 191),
    c2pnb208w1("c2pnb208w1", 208),
    c2tnb239v1("c2tnb239v1", 239),
    c2tnb239v2("c2tnb239v2", 239),
    c2tnb239v3("c2tnb239v3", 239),
    c2pnb272w1("c2pnb272w1", 272),
    c2pnb304w1("c2pnb304w1", 304),
    c2tnb359v1("c2tnb359v1", 359),
    c2pnb368w1("c2pnb368w1", 368),
    c2tnb431r1("c2tnb431r1", 431),
    //SEC
    sect163k1("sect163k1", 163),
    sect163r1("sect163r1", 163),
    sect163r2("sect163r2", 163),
    sect193r1("sect193r1", 193),
    sect193r2("sect193r2", 193),
    sect233k1("sect233k1", 233),
    sect233r1("sect233r1", 233),
    sect239k1("sect239k1", 239),
    sect283k1("sect283k1", 283),
    sect283r1("sect283r1", 283),
    sect409k1("sect409k1", 409),
    sect409r1("sect409r1", 409),
    sect571k1("sect571k1", 571),
    sect571r1("sect571r1", 571),
    //NIST
    B_163("B-163", 163),
    B_233("B-233", 233),
    B_283("B-283", 283),
    B_409("B-409", 409),
    B_571("B-571", 571),
    //teletrust
    brainpoolp160r1("brainpoolp160r1", 160),
    brainpoolp160t1("brainpoolp160t1", 160),
    brainpoolp192r1("brainpoolp192r1", 192),
    brainpoolp192t1("brainpoolp192t1", 192),
    brainpoolp224r1("brainpoolp224r1", 224),
    brainpoolp224t1("brainpoolp224t1", 224),
    brainpoolp256r1("brainpoolp256r1", 256),
    brainpoolp256t1("brainpoolp256t1", 256),
    brainpoolp320r1("brainpoolp320r1", 320),
    brainpoolp320t1("brainpoolp320t1", 320),
    brainpoolp384r1("brainpoolp384r1", 384),
    brainpoolp384t1("brainpoolp384t1", 384),
    brainpoolp512r1("brainpoolp512r1", 512),
    brainpoolp512t1("brainpoolp512t1", 512),

    //ECGOST
    CRYPTOPRO_A("GostR3410-2001-CryptoPro-A", 0),
    CRYPTOPRO_XCHB("GostR3410-2001-CryptoPro-XchB", 0),
    CRYPTOPRO_XCHA("GostR3410-2001-CryptoPro-XchA", 0),
    CRYPTOPRO_C("GostR3410-2001-CryptoPro-C", 0),
    CRYPTOPRO_B("GostR3410-2001-CryptoPro-B", 0);

    public final String name;
    public final int keySize;
    public final ECParameterSpec spec;

    CurveType(String name, int keySize) {
        this.name = name.intern();
        this.keySize = keySize;
        this.spec = ECNamedCurveTable.getParameterSpec(this.name);
    }
}
