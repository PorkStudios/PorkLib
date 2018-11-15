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

import net.daporkchop.lib.common.test.TestRandomData;
import net.daporkchop.lib.encoding.compression.Compression;
import net.daporkchop.lib.encoding.compression.CompressionHelper;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
public class CompressionTest {
    @Test
    public void test() {
        Compression.BZIP2_HIGH.toString();
        Map<String, Double> ratios = Collections.synchronizedMap(new Hashtable<>());
        Map<String, Long> times = Collections.synchronizedMap(new Hashtable<>());
        CompressionHelper.forAllRegisteredAlgs((name, helper) -> {
            System.out.printf("Testing compression: %s\n", helper);
            int inputSize = 0;
            int outputSize = 0;
            long time = System.currentTimeMillis();
            for (byte[] orig : TestRandomData.randomBytes) {
                inputSize += orig.length;
                byte[] compressed = helper.deflate(orig);
                outputSize += compressed.length;
                byte[] inflated = helper.inflate(compressed);
                if (!Arrays.equals(orig, inflated)) {
                    throw new IllegalStateException(String.format("Inflated data was not the same as original data on %s", helper));
                }
            }
            ratios.put(helper.toString(), ((double) inputSize / (double) outputSize) * 100.0d);
            times.put(helper.toString(), System.currentTimeMillis() - time);
        }, true);
        System.out.println();
        System.out.println("Compression ratios:");
        ratios.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue, Double::compare))
                .forEachOrdered(e -> System.out.printf("  %.3f%% for %s\n", e.getValue(), e.getKey()));

        System.out.println();
        System.out.println("Compression times:");
        times.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue, Long::compare))
                .forEachOrdered(e -> System.out.printf("  %05dms for %s\n", e.getValue(), e.getKey()));
    }
}
