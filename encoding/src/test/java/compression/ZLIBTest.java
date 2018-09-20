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

package compression;

import net.daporkchop.lib.encoding.compression.ZLIBHelper;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author DaPorkchop_
 */
public class ZLIBTest {
    @Test
    public void test() {
        byte[] rand = new byte[8192];
        ThreadLocalRandom.current().nextBytes(rand);
        byte[] compressed = ZLIBHelper.compress(rand);
        System.out.println("Compressed: " + compressed.length + " bytes (original: 8192 bytes) (@" + Arrays.hashCode(rand) + ')');
        byte[] inflated = ZLIBHelper.inflate(compressed);
        System.out.println("Inflated: " + inflated.length + " bytes (@" + Arrays.hashCode(inflated) + ')');
        if (!Arrays.equals(rand, inflated)) throw new IllegalStateException("Data didn't match");
    }
}
