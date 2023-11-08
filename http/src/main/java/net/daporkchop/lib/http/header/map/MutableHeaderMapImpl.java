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

package net.daporkchop.lib.http.header.map;

import lombok.NonNull;
import net.daporkchop.lib.http.header.Header;
import net.daporkchop.lib.http.header.MultiHeaderImpl;
import net.daporkchop.lib.http.header.SingletonHeaderImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A simple implementation of {@link MutableHeaderMap}.
 *
 * @author DaPorkchop_
 */
public class MutableHeaderMapImpl implements MutableHeaderMap {
    protected final List<Header>        list;
    protected final Map<String, Header> map;

    public MutableHeaderMapImpl() {
        this.list = new ArrayList<>();
        this.map = new HashMap<>();
    }

    public MutableHeaderMapImpl(@NonNull HeaderMap source) {
        this();
        this.putAll(source);
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public Header get(int index) throws IndexOutOfBoundsException {
        return this.list.get(index);
    }

    @Override
    public Header get(@NonNull String key) {
        return this.map.get(key.toLowerCase());
    }

    @Override
    public Header put(@NonNull Header header) {
        header = Header.immutable(header);
        String key = header.key().toLowerCase();
        Header old = this.map.putIfAbsent(key, header);
        if (old == null) {
            //the header is new
            this.list.add(header);
            return null;
        } else {
            //the header already exists
            this.map.replace(key, old, header);
            this.list.set(this.list.indexOf(old), header);
            return old;
        }
    }

    @Override
    public void add(@NonNull Header header) {
        header = Header.immutable(header);
        String key = header.key().toLowerCase();
        Header old = this.map.putIfAbsent(key, header);
        if (old == null) {
            //the header is new
            this.list.add(header);
        } else {
            //the header already exists
            List<String> list = new ArrayList<>();
            if (old.singleton())    {
                list.add(old.value());
            } else {
                list.addAll(old.values());
            }
            if (header.singleton()) {
                list.add(header.value());
            } else {
                list.addAll(header.values());
            }
            header = new MultiHeaderImpl(key, list, true);
            this.map.replace(key, old, header);
            this.list.set(this.list.indexOf(old), header);
        }
    }

    @Override
    public Header remove(@NonNull String key) {
        Header old = this.map.remove(key.toLowerCase());
        return old != null && this.list.remove(old) ? old : null;
    }

    @Override
    public Header remove(int index) throws IndexOutOfBoundsException {
        Header header = this.list.remove(index);
        if (!this.map.remove(header.key().toLowerCase(), header))   {
            throw new IllegalStateException(String.format("Couldn't remove header at index %d (key \"%s\" (internal: \"%s\") is not present in map!)", index, header.key(), header.key().toLowerCase()));
        }
        return header;
    }

    @Override
    public void forEach(@NonNull Consumer<Header> callback) {
        this.list.forEach(callback);
    }
}
