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

package net.daporkchop.lib.crypto.test.pork;

import net.daporkchop.lib.crypto.engine.symmetric.PorkCrypt2Engine;
import net.daporkchop.lib.encoding.Hexadecimal;
import net.daporkchop.lib.hash.helper.PorkHashHelper;
import org.bouncycastle.crypto.params.KeyParameter;
import org.junit.Test;

/**
 * @author DaPorkchop_
 */
public class PorkCrypt2Test {
    private static final String text = "Hello world! AAA";

    @Test
    public void test() {
        System.out.println("Testing PorkHash2: Hello world!");

        byte[] key = PorkHashHelper.porkhash(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15});

        System.out.println("Input text: " + text);
        PorkCrypt2Engine engine = new PorkCrypt2Engine();
        engine.init(true, new KeyParameter(key));
        byte[] out = new byte[16];
        engine.processBlock(text.getBytes(), 0, out, 0);
        System.out.println("Encrypted: " + new String(out).replaceAll("\n", " ") + ' ' + Hexadecimal.encode(out));
        engine.init(false, new KeyParameter(key));
        byte[] newOut = new byte[16];
        engine.processBlock(out, 0, newOut, 0);
        System.out.println("Decrypted: " + new String(newOut) + ' ' + Hexadecimal.encode(newOut));
    }

    /*
    @Test
    public void lShiftTest()    {
        System.out.println("Left shift:");
        byte[] b = new byte[16];
        ThreadLocalRandom.current().nextBytes(b);
        printBin(b);
        PorkCrypt2Engine.Util.shiftLeft(b, 2);
        printBin(b);
    }

    @Test
    public void rShiftTest()    {
        System.out.println("Right shift:");
        byte[] b = new byte[16];
        ThreadLocalRandom.current().nextBytes(b);
        printBin(b);
        PorkCrypt2Engine.Util.shiftRight(b, 2);
        printBin(b);
    }

    private void printBin(byte[] b) {
        for (int i = 0; i < b.length; i++)  {
            System.out.print(Integer.toBinaryString((b[i] & 0xFF) + 0x100).substring(1));
        }
        System.out.println();
    }
    */
}
