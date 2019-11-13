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

package net.daporkchop.lib.http.header;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * A simple, hashtable-based implementation of {@link HeaderMap}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class DefaultHeaderMap implements HeaderMap {
    @NonNull
    protected final List<Header>        list;
    @NonNull
    protected final Map<String, Header> map;

    public DefaultHeaderMap() {
        this.list = new ArrayList<>();
        this.map = new HashMap<>();
    }

    public DefaultHeaderMap(@NonNull HeaderMap source) {
        this();
        this.putAll(source);
    }

    public DefaultHeaderMap(@NonNull Stream<Header> source) {
        this();
        source.forEach(this::put);
    }

    @Override
    public synchronized int size() {
        return this.list.size();
    }

    @Override
    public synchronized Header get(int index) throws IndexOutOfBoundsException {
        return this.list.get(index);
    }

    @Override
    public synchronized Header get(@NonNull String key) {
        return this.map.get(key);
    }

    @Override
    public String put(@NonNull String key, @NonNull String value) {
        return this.put(new HeaderImpl(key, value));
    }

    protected synchronized String put(@NonNull Header header) {
        Header old = this.map.get(header.key());
        if (old == null) {
            //new header entry must be created
            this.list.add(header);
            this.map.put(header.key(), header);
            return null;
        } else {
            this.list.set(this.list.indexOf(old), header);
            this.map.replace(header.key(), old, header);
            return old.value();
        }
    }

    @Override
    public synchronized String remove(@NonNull String key) {
        Header old = this.map.remove(key);
        return old != null && this.list.remove(old) ? old.value() : null;
    }
}
