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

package net.daporkchop.lib.encoding.qr.util;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.encoding.qr.QRConstants;

import java.util.BitSet;

import static net.daporkchop.lib.encoding.qr.QRConstants.*;

/**
 * @author DaPorkchop_
 */
@Getter
public class QRBitOutput {
    protected final int version;
    protected final int size;

    protected final BitSet bits;

    public QRBitOutput(int version, @NonNull BitSet bits)   {
        this.version = ensureValidVersion(version);
        this.bits = bits;

        this.size = getSizeForVersion(version);
    }

    public void put(int i, int bits)    {
        for (bits--; bits >= 0; bits--) {
            this.put(((i >>> bits) & 1) != 0);
        }
    }

    public void put(boolean val)    {
        //TODO
    }

    public int writerPos()  {
        //TODO
        return -1;
    }
}
