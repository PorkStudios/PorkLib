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

package collections;

import net.daporkchop.lib.collections.impl.ordered.BigLinkedCollection;
import net.daporkchop.lib.collections.impl.ordered.concurrent.ConcurrentBigLinkedCollection;
import net.daporkchop.lib.collections.impl.list.JavaListWrapper;
import net.daporkchop.lib.collections.impl.set.JavaSetWrapper;
import net.daporkchop.lib.collections.stream.PStream;
import net.daporkchop.lib.collections.stream.impl.array.ArrayStream;
import net.daporkchop.lib.collections.stream.impl.array.ConcurrentArrayStream;
import net.daporkchop.lib.collections.stream.impl.array.UncheckedArrayStream;
import net.daporkchop.lib.collections.stream.impl.collection.ConcurrentOrderedCollectionStream;
import net.daporkchop.lib.collections.stream.impl.collection.UncheckedOrderedCollectionStream;
import net.daporkchop.lib.collections.stream.impl.list.ConcurrentListStream;
import net.daporkchop.lib.collections.stream.impl.list.UncheckedListStream;
import net.daporkchop.lib.collections.stream.impl.set.ConcurrentSetStream;
import net.daporkchop.lib.collections.stream.impl.set.UncheckedSetStream;
import net.daporkchop.lib.logging.Logging;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author DaPorkchop_
 */
public class StreamTest implements Logging {
    @Test
    @SuppressWarnings("unchecked")
    public void testBasicStreams() {
        Integer[] i = new Integer[8192];
        for (int j = i.length - 1; j >= 0; j--) {
            i[j] = ThreadLocalRandom.current().nextInt();
        }
        Collection<Integer> collection = new ArrayList<>();
        Collections.addAll(collection, i);

        for (PStream<Integer> stream : (PStream<Integer>[])new PStream[]{
                new ArrayStream<>(i),
                new UncheckedArrayStream<>(i),
                new ConcurrentArrayStream<>(i),
                new UncheckedListStream<>(new JavaListWrapper<>(new ArrayList<>(collection)), true),
                new ConcurrentListStream<>(new JavaListWrapper<>(new ArrayList<>(collection)), true),
                new UncheckedSetStream<>(new JavaSetWrapper<>(new HashSet<>(collection)), true),
                new ConcurrentSetStream(new JavaSetWrapper<>(new HashSet<>(collection)), true),
                new ConcurrentOrderedCollectionStream<>(new ConcurrentBigLinkedCollection<>(collection), true),
                new UncheckedOrderedCollectionStream<>(new BigLinkedCollection<>(collection), true)
        }) {
            logger.info("Testing ${0}...", stream.getClass());
            AtomicInteger counter = new AtomicInteger(0);
            stream
                    .map(String::valueOf)
                    .map(Integer::parseInt)
                    .forEach(value -> {
                        if (!collection.contains(value)) {
                            throw new IllegalStateException();
                        }
                        counter.incrementAndGet();
                    });
            if (counter.get() != collection.size()) {
                throw new IllegalStateException();
            }
        }
    }
}
