package net.daporkchop.lib.crypto;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author DaPorkchop_
 */
public class TestConstants {
    public static final byte[][] randomData = new byte[32][];

    static {
        System.out.println("Generating random data...");
        for (int i = randomData.length - 1; i >= 0; i--)  {
            byte[] b = new byte[ThreadLocalRandom.current().nextInt(1024, 8192)];
            ThreadLocalRandom.current().nextBytes(b);
            randomData[i] = b;
        }
        System.out.println("Generated random data.");
    }
}
