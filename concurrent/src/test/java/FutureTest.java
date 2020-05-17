/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2018-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

import io.netty.util.concurrent.GlobalEventExecutor;
import net.daporkchop.lib.concurrent.PFuture;
import net.daporkchop.lib.concurrent.PFutures;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * @author DaPorkchop_
 */
public class FutureTest {
    @Test
    public void test() {
        System.out.println("Starting...");
        PFuture<String> a = PFutures.wrap(GlobalEventExecutor.INSTANCE.submit(() -> {
            sleep(2000L);
            System.out.println("A");
            return "Hello ";
        }));
        PFuture<String> b = PFutures.wrap(CompletableFuture.supplyAsync(() -> {
            sleep(1000L);
            System.out.println("B");
            return "World";
        }));
        PFuture<String> c = PFutures.computeAsync(() -> {
            sleep(2000L);
            System.out.println("C");
            return "!";
        });

        a.thenCombine(b, String::concat)
                .thenCombine(c, String::concat)
                .thenAccept(System.out::println)
                .awaitUninterruptibly();
    }

    @Test
    public void testList()  {
        List<String> initialValues = Arrays.asList("A", "B", "C");
        List<String> list = PFutures.mergeToList(initialValues.stream()
                .map(s -> (Supplier<String>) () -> {
                    sleep(1000L);
                    return s;
                })
                .map(PFutures::computeAsync)
                .collect(Collectors.toList()))
                .join();
        System.out.println(list);
    }
}
