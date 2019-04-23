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

import net.daporkchop.lib.common.function.throwing.ERunnable;
import net.daporkchop.lib.concurrent.future.Future;
import net.daporkchop.lib.concurrent.tasks.TaskExecutor;
import net.daporkchop.lib.concurrent.tasks.impl.SimpleThreadPoolTaskExecutor;

import java.util.concurrent.TimeUnit;

/**
 * @author DaPorkchop_
 */
public class ExecutorExample {
    public static void main(String... args) {
        TaskExecutor executor = SimpleThreadPoolTaskExecutor.builder()
                .setCorePoolSize(2)
                .setMaxPoolSize(4)
                .setThreadKeepAliveTime(TimeUnit.SECONDS.toMillis(5L))
                .build();

        long time = System.currentTimeMillis();
        Future[] futures = new Future[8];
        for (int i = futures.length - 1; i >= 0; i--)   {
            futures[i] = executor.submit((ERunnable) () -> Thread.sleep(5000L));
        }
        for (int i = futures.length - 1; i >= 0; i--)   {
            futures[i].sync();
        }
        System.out.printf("Took %dms to wait %d*5000ms\n", System.currentTimeMillis() - time, futures.length);

        executor.close();
    }
}
