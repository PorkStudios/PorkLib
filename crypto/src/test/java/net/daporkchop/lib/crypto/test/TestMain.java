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

package net.daporkchop.lib.crypto.test;

import net.daporkchop.lib.crypto.test.impl.asymmetric.DHTest;
import net.daporkchop.lib.crypto.test.impl.asymmetric.RSATest;
import net.daporkchop.lib.crypto.test.impl.ec.impl.ECDHTest;
import net.daporkchop.lib.crypto.test.impl.ec.impl.ECDSATest;
import net.daporkchop.lib.crypto.test.impl.symmetric.impl.AESTest;
import net.daporkchop.lib.crypto.test.impl.symmetric.impl.ARIATest;
import net.daporkchop.lib.crypto.test.impl.symmetric.impl.BlowFishTest;
import net.daporkchop.lib.crypto.test.impl.symmetric.impl.CAST5Test;
import net.daporkchop.lib.crypto.test.impl.symmetric.impl.CAST6Test;
import net.daporkchop.lib.crypto.test.impl.symmetric.impl.CamelliaTest;
import net.daporkchop.lib.crypto.test.impl.symmetric.impl.DESTest;
import net.daporkchop.lib.crypto.test.impl.symmetric.impl.DSTU_7624_128Test;
import net.daporkchop.lib.crypto.test.impl.symmetric.impl.DSTU_7624_256Test;
import net.daporkchop.lib.crypto.test.impl.symmetric.impl.DSTU_7624_512Test;
import net.daporkchop.lib.crypto.test.impl.symmetric.impl.GOST_28147Test;
import net.daporkchop.lib.crypto.test.impl.symmetric.impl.GOST_3412_2015Test;
import net.daporkchop.lib.crypto.test.impl.symmetric.impl.IDEATest;
import net.daporkchop.lib.crypto.test.impl.symmetric.impl.NoekeonTest;
import net.daporkchop.lib.crypto.test.impl.symmetric.impl.RC2Test;
import net.daporkchop.lib.crypto.test.impl.symmetric.impl.RC6Test;
import net.daporkchop.lib.crypto.test.impl.symmetric.impl.RijndaelTest;
import net.daporkchop.lib.crypto.test.impl.symmetric.impl.SEEDTest;
import net.daporkchop.lib.crypto.test.impl.symmetric.impl.SKIPJACKTest;
import net.daporkchop.lib.crypto.test.impl.symmetric.impl.SM4Test;
import net.daporkchop.lib.crypto.test.impl.symmetric.impl.SerpentTest;
import net.daporkchop.lib.crypto.test.impl.symmetric.impl.Shacal2Test;
import net.daporkchop.lib.crypto.test.impl.symmetric.impl.TEATest;
import net.daporkchop.lib.crypto.test.impl.symmetric.impl.Threefish_1024Test;
import net.daporkchop.lib.crypto.test.impl.symmetric.impl.Threefish_256Test;
import net.daporkchop.lib.crypto.test.impl.symmetric.impl.Threefish_512Test;
import net.daporkchop.lib.crypto.test.impl.symmetric.impl.TwoFishTest;
import net.daporkchop.lib.crypto.test.impl.symmetric.impl.XTEATest;

import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class TestMain {
    public static final byte[][] RANDOM_DATA = new byte[8][];

    static {
        System.out.println("Generating random data...");
        for (int i = 0; i < RANDOM_DATA.length; i++) {
            byte[] a = new byte[ThreadLocalRandom.current().nextInt(256) + 256];
            ThreadLocalRandom.current().nextBytes(a);
            RANDOM_DATA[i] = a;
        }
    }

    public static void main(String... args) {
        new Thread("ok") {
            @Override
            public void run() {
                if (false) {
                    Scanner s = new Scanner(System.in);
                    while (!s.nextLine().equals("stop")) System.gc();
                    s.close();
                }
            }
        }.start();

        //DEBUG: sleep until key pressed
        if (false) {
            Scanner s = new Scanner(System.in);
            s.nextLine();
            s.close();
        }

        SYMMETRIC:
        {
            //if (true) break SYMMETRIC;

            new AESTest().test();
            new ARIATest().test();
            new BlowFishTest().test();
            new CamelliaTest().test();
            new CAST5Test().test();
            new CAST6Test().test();
            new DESTest().test();
            new DSTU_7624_128Test().test();
            new DSTU_7624_256Test().test();
            new DSTU_7624_512Test().test();
            new GOST_3412_2015Test().test();
            new GOST_28147Test().test();
            new IDEATest().test();
            new NoekeonTest().test();
            new RC2Test().test();
            new RC6Test().test();
            new RijndaelTest().test();
            new SEEDTest().test();
            new SerpentTest().test();
            new Shacal2Test().test();
            new SKIPJACKTest().test();
            new SM4Test().test();
            new TEATest().test();
            new Threefish_256Test().test();
            new Threefish_512Test().test();
            new Threefish_1024Test().test();
            new TwoFishTest().test();
            new XTEATest().test();
        }

        EC:
        {
            if (true) break EC;

            new ECDHTest().test();
            new ECDSATest().test();
        }

        RSA:
        {
            if (true) break RSA;

            new RSATest().testCipher();
            new RSATest().testSig();
            new DHTest().test();
        }
    }
}
