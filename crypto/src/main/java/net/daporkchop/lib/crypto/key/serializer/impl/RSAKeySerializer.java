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

import net.daporkchop.lib.crypto.key.asymmetric.RSAKeyPair;
import net.daporkchop.lib.crypto.key.serializer.AbstractKeySerializer;
import net.daporkchop.lib.crypto.sig.asymmetric.PlainDSAEncoder;
import org.bouncycastle.crypto.params.RSAKeyParameters;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

public class RSAKeySerializer extends AbstractKeySerializer<RSAKeyPair> {
    public static final RSAKeySerializer INSTANCE = new RSAKeySerializer();

    private RSAKeySerializer() {
    }

    @Override
    protected void doSerialize(RSAKeyPair key, OutputStream baos) throws IOException {
        DataOutput output = new DataOutputStream(baos);
        {
            RSAKeyParameters parameters = (RSAKeyParameters) key.getPublic();
            byte[] data = PlainDSAEncoder.INSTANCE.encode(parameters.getExponent(), parameters.getModulus());
            output.writeInt(data.length);
            output.write(data);
        }
        {
            RSAKeyParameters parameters = (RSAKeyParameters) key.getPrivate();
            byte[] data = PlainDSAEncoder.INSTANCE.encode(parameters.getExponent(), parameters.getModulus());
            output.writeInt(data.length);
            output.write(data);
        }
    }

    @Override
    protected RSAKeyPair doDeserialize(InputStream bais) throws IOException {
        DataInput input = new DataInputStream(bais);
        RSAKeyParameters pub, priv;
        {
            byte[] data = new byte[input.readInt()];
            input.readFully(data);
            BigInteger[] integers = PlainDSAEncoder.INSTANCE.decode(data);
            pub = new RSAKeyParameters(false, integers[1], integers[0]);
        }
        {
            byte[] data = new byte[input.readInt()];
            input.readFully(data);
            BigInteger[] integers = PlainDSAEncoder.INSTANCE.decode(data);
            priv = new RSAKeyParameters(true, integers[1], integers[0]);
        }
        return new RSAKeyPair(pub, priv);
    }
}
