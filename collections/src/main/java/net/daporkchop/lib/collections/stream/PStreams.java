/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2020 DaPorkchop_ and contributors
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

package net.daporkchop.lib.collections.stream;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.collections.PList;
import net.daporkchop.lib.collections.PSet;
import net.daporkchop.lib.collections.impl.list.JavaListWrapper;
import net.daporkchop.lib.collections.impl.set.JavaSetWrapper;
import net.daporkchop.lib.collections.stream.impl.array.ArrayStream;
import net.daporkchop.lib.collections.stream.impl.list.UncheckedListStream;
import net.daporkchop.lib.collections.stream.impl.set.UncheckedSetStream;

import java.util.List;
import java.util.Set;

/**
 * @author DaPorkchop_
 */
@UtilityClass
public class PStreams {
    public <V> PStream<V> of(@NonNull V... values)   {
        return ofArray(values);
    }

    public <V> PStream<V> ofArray(@NonNull V[] array) {
        return new ArrayStream<>(array);
    }

    public <V> PStream<V> list(@NonNull List<V> list)  {
        return list(new JavaListWrapper<>(list));
    }

    public <V> PStream<V> list(@NonNull PList<V> list) {
        return list.stream();
    }

    public <V> PStream<V> set(@NonNull Set<V> set)  {
        return set(new JavaSetWrapper<>(set));
    }

    public <V> PStream<V> set(@NonNull PSet<V> set) {
        return set.stream();
    }
}
