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

package net.daporkchop.lib.http.header.map;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.daporkchop.lib.http.header.Header;
import net.daporkchop.lib.http.header.MultiHeaderImpl;
import net.daporkchop.lib.http.header.SingletonHeaderImpl;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * An implementation of {@link HeaderMap} that allows appending headers quickly, but doesn't use a hash table for lookups. Should cause minimal
 * memory overhead, but may cause slower lookups in some cases.
 *
 * @author DaPorkchop_
 */
@Accessors(fluent = true)
public final class ArrayHeaderMap implements HeaderMap {
    protected Object[] data;

    @Getter
    protected int size = 0;

    public ArrayHeaderMap() {
        this(16);
    }

    public ArrayHeaderMap(int initialCapacity) {
        this.data = new Object[initialCapacity << 1];
    }

    @Override
    public Header get(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        return this.headerAt(index);
    }

    @Override
    public Header get(@NonNull String key) {
        int index = this.findIndex(key);
        return index < 0 ? null : this.headerAt(index);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void forEach(@NonNull BiConsumer<String, String> callback) {
        for (int i = 0, size = this.size; i < size; i++) {
            String key = (String) this.data[i << 1];
            Object value = this.data[(i << 1) + 1];
            if (value instanceof String) {
                callback.accept(key, (String) value);
            } else {
                List<String> list = (List<String>) value;
                for (int j = 0, listSize = list.size(); j < listSize; j++) {
                    callback.accept(key, list.get(j));
                }
            }
        }
    }

    public void append(@NonNull String key, @NonNull String value) {
        int index = this.size++;
        if (index == (this.data.length >> 1)) {
            //expand array
            Object[] newArray = new Object[this.data.length << 1];
            System.arraycopy(this.data, 0, newArray, 0, this.data.length);
            this.data = newArray;
        }
        this.data[index <<= 1] = key;
        this.data[index + 1] = value;
    }

    protected int findIndex(@NonNull String key) {
        key = key.toLowerCase();
        for (int i = 0, size = this.size; i < size; i++) {
            if (key.equals(this.data[i << 1])) {
                return i;
            }
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    protected Header headerAt(int index) {
        String key = (String) this.data[index <<= 1];
        Object value = this.data[index + 1];
        return value instanceof String
                ? new SingletonHeaderImpl(key, (String) value)
                : new MultiHeaderImpl(key, (List<String>) value, true);
    }
}
