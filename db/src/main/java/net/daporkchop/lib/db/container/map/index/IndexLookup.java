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

package net.daporkchop.lib.db.container.map.index;

import lombok.NonNull;
import net.daporkchop.lib.binary.Persistent;
import net.daporkchop.lib.db.container.map.DBMap;
import net.daporkchop.lib.db.data.key.KeyHasher;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
public interface IndexLookup<K> extends Persistent {
    default DBMap<K, ?> getBacking() {
        throw new UnsupportedOperationException(String.format("%s doesn't use a hash!", this.getClass().getCanonicalName()));
    }

    default void init(@NonNull DBMap<K, ?> map, @NonNull RandomAccessFile file) throws IOException {
        this.load();
    }

    long get(@NonNull K key) throws IOException;

    void set(@NonNull K key, long val) throws IOException;

    boolean contains(@NonNull K key) throws IOException;

    long remove(@NonNull K key) throws IOException;

    void clear() throws IOException;
}
