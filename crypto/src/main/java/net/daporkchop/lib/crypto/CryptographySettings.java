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

package net.daporkchop.lib.crypto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.daporkchop.lib.binary.Data;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherMode;
import net.daporkchop.lib.crypto.cipher.symmetric.BlockCipherType;
import net.daporkchop.lib.crypto.cipher.symmetric.padding.BlockCipherPadding;
import net.daporkchop.lib.crypto.key.ec.impl.ECDHKeyPair;
import net.daporkchop.lib.crypto.sig.ec.CurveType;

import java.io.IOException;

/**
 * @author DaPorkchop_
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CryptographySettings implements Data {
    private CurveType curveType;
    private ECDHKeyPair key;
    private BlockCipherType cipherType;
    private BlockCipherMode cipherMode;
    private BlockCipherPadding cipherPadding;

    @Override
    public void read(DataIn in) throws IOException {
        if (in.readBoolean()) {
            this.curveType = CurveType.valueOf(in.readUTF());
            this.key = ECDHKeyPair.decodePublic(in.readBytesSimple());
            this.cipherType = BlockCipherType.valueOf(in.readUTF());
            this.cipherMode = BlockCipherMode.valueOf(in.readUTF());
            this.cipherPadding = BlockCipherPadding.valueOf(in.readUTF());
        } else {
            this.curveType = null;
            this.key = null;
            this.cipherType = null;
            this.cipherMode = null;
            this.cipherPadding = null;
        }
    }

    @Override
    public void write(DataOut out) throws IOException {
        if (this.curveType == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeUTF(this.curveType.name());
            out.writeBytesSimple(this.key.encodePublic());
            out.writeUTF(this.cipherType.name());
            out.writeUTF(this.cipherMode.name());
            out.writeUTF(this.cipherPadding.name());
        }
    }

    public boolean doesEncrypt()    {
        return this.curveType != null;
    }
}
